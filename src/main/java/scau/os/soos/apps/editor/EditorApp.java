package scau.os.soos.apps.editor;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import scau.os.soos.common.exception.DiskSpaceInsufficientException;
import scau.os.soos.module.file.FileController;
import scau.os.soos.module.file.model.Exe;
import scau.os.soos.module.file.model.Item;
import scau.os.soos.module.file.model.Txt;

import scau.os.soos.ui.components.Dialog;
import scau.os.soos.ui.components.base.Window;

import java.util.ArrayList;
import java.util.List;

public class EditorApp extends Window {

    @FXML
    TextArea display;

    @FXML
    TextField input;

    @FXML
    Button editor_save;

    private final Item item;
    private boolean permitSave;

    public EditorApp(Item item) {
        super(item.getPath(), "main.fxml", 450, 520);
        this.item = item;
        String initialText = getItemContext(item).trim();
        display.setText(initialText);
        input.setText(String.valueOf(initialText.length()));
        display.textProperty().addListener((observable, oldValue, newValue) -> {
            int length = newValue.length();
            input.setText(String.valueOf(length));
        });
        editor_save.setOnAction(event -> save(item));
    }

    @Override
    protected void initialize() {

    }

    private void save(Item item){
        if (item instanceof Txt) {
            Txt txtItem = (Txt) item;
            txtItem.setContext(display.getText());
            try {
                FileController.getInstance().writeFile(txtItem);
            } catch (DiskSpaceInsufficientException e) {
                Dialog.getDialog(this,"保存失败！\n磁盘空间不足",
                        true,false,
                        null,null,
                        null).show();
            }
        }
        else if (item instanceof Exe) {
            Exe exeItem = (Exe) item;
            List<Byte> byteList = parseInstructions(display.getText());
            if(!permitSave){
                return;
            }
            exeItem.setInstructions(byteList);
            try {
                FileController.getInstance().writeFile(exeItem);
            } catch (DiskSpaceInsufficientException e) {
                Dialog.getDialog(this,"保存失败！\n磁盘空间不足",
                        true,false,
                        null,null,
                        null).show();
            }
        }
    }

    private List<Byte> parseInstructions(String input) {
        permitSave = true;
        List<Byte> byteList = new ArrayList<>();
        String[] lines = input.trim().split("\n"); // 使用换行符分隔指令

        for (String line : lines) {
            line = line.replaceAll("\\s+", ""); // 移除行内所有空格
            if (line.isEmpty()) {
                continue; // 跳过空行
            }

            String opcode = line.substring(0, 1); // 指令的第一个字符是操作码
            String operands = line.substring(1); // 剩余部分是操作数

            switch (opcode) {
                case "x":
                    if (operands.equals("++")) {
                        byteList.add((byte) 0b00100000);
                    } else if (operands.equals("--")) {
                        byteList.add((byte) 0b00110000);
                    } else if (operands.matches("=\\d{1,2}")) {  // x= 后跟一位或两位数字（0-15）
                        int value = Integer.parseInt(operands.substring(1)); // 去除等号后解析十进制数
                        if(value<=15&&value>=0){
                            String binary = String.format("%4s", Integer.toBinaryString(value)).replace(' ', '0'); // 补齐4位
                            byteList.add((byte) (0b0001 << 4 | Integer.parseInt(binary, 2))); // 构造 0001 aaaa 格式
                        }
                    } else {
                        // 未知或格式错误的 x 指令
                        permitSave = false;
                        break;
                    }
                    break;

                case "!":
                    if (operands.matches("\\d[A-Za-z]")) { // 数字后跟设备类型 A/B/C
                        int time = Integer.parseInt(operands.substring(0, 1)); // 提取使用时间（这里假设是单个数字，即0-9）
                        char deviceType = operands.charAt(1); // 提取设备类型

                        // 设备类型映射为二进制（这里简单映射为 00, 01, 10）
                        int deviceTypeBinary = (deviceType == 'A') ? 0b00 : (deviceType == 'B') ? 0b01 : (deviceType == 'C') ? 0b10 : -1;

                        if (deviceTypeBinary != -1 && time >= 0 && time <= 3) { // 检查设备类型和时间的有效性
                            // 构造指令（这里假设时间占用低3位，设备类型占用接下来的2位，操作码占用高1位已经确定）
                            // 注意：这里的编码方案是假设的，可能需要根据实际需求调整
                            byte instruction = (byte) (0b0100 << 4 | (deviceTypeBinary & 0b11 ) | (time & 0b11)<<2); // 合并操作码、设备类型和时间
                            byteList.add(instruction);
                        } else {
                            permitSave = false;
                            break;
                        }
                    } else {
                        permitSave = false;
                        break;
                    }
                    break;

                case "e":
                    if (operands.equals("nd"))
                        byteList.add((byte) 0b01010000);
                    else {
                        permitSave = false;
                    }
                    break;

                default:
                    permitSave = false;
                    break;
            }
        }

        if(!permitSave){
            Dialog.getDialog(this,"编译失败！\n输入指令含有非法字符",
                    true,false,
                    null,null,
            null).show();
            return null;
        }

        return byteList;
    }

    private static String typeInstructions(List<Byte> instructions){
        StringBuilder content = new StringBuilder();
        for(byte instruction : instructions){
            int op = instruction >> 4;
            int tmp = instruction & 0b00001111;
            switch (op) {
                case 0b0001 -> content.append("x=").append(tmp).append("\n");
                case 0b0010 -> content.append("x++").append("\n");
                case 0b0011 -> content.append("x--").append("\n");
                case 0b0100 -> {
                    int time = tmp >> 2;
                    int device = tmp & 0b0011;
                    char deviceType = switch (device) {
                        case 0 -> 'A';
                        case 1 -> 'B';
                        case 2 -> 'C';
                        default -> ' ';
                    };
                    content.append("!").append(time).append(deviceType).append("\n");
                }
                case 0b0101 -> content.append("end").append("\n");
            }
        }
        return  content.toString();
    }


    public static String getItemContext(Item item) {
        if (item instanceof Txt) {
            Txt txtItem = (Txt) item;
            return txtItem.getContext();
        }
        else{
            Exe exeItem = (Exe) item;
            List<Byte> byteList = exeItem.getInstructions();
            return typeInstructions(byteList);
        }
    }

    @Override
    protected void close() {

    }
}
