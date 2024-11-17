package scau.os.soos.apps.editor;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import scau.os.soos.module.file.model.Exe;
import scau.os.soos.module.file.model.Item;
import scau.os.soos.module.file.model.Txt;
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

    private Item item;

    public EditorApp(Item item) {
        super(item.getPath(), "main.fxml", 450, 520);
        this.item = item;
        String initialText = getItemContext(item);
        display.setText(initialText);
        input.setText(String.valueOf(excludeWhitespace(initialText)));
        display.textProperty().addListener((observable, oldValue, newValue) -> {
            int length = newValue.length();
            input.setText(String.valueOf(length));
        });
        editor_save.setOnAction(event -> {
            save(item);
        });
    }

    @Override
    protected void initialize() {

    }
    private void save(Item item){
        if (item instanceof Txt) {
            Txt txtItem = (Txt) item;
            txtItem.setContext(display.getText());
        }
        else{
            Exe exeItem = (Exe) item;
            List<Byte> byteList = parseInstructions(display.getText());
            exeItem.setInstructions(byteList);
            exeItem.setSize(byteList.size());
        }
    }

    private List<Byte> parseInstructions(String input) {
        List<Byte> byteList = new ArrayList<>();
        String[] lines = input.trim().split("\n"); // 使用换行符分隔指令

        for (String line : lines) {
            line = line.trim(); // 去除每行首尾的空格
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
                            // 设备类型或时间无效（时间应在0-3之间）
                            // 可以选择抛出异常、记录错误或进行其他处理
                        }
                    } else {
                        // 格式错误的 ! 指令（应为一个数字后跟一个字母）
                        // 可以选择抛出异常、记录错误或进行其他处理
                    }
                    break;

                case "e":
                    if (operands.equals("nd"))
                        byteList.add((byte) 0b01010000);
                    break;

                default:
                    // 未知指令
                    // 可以选择抛出异常、记录错误或进行其他处理
                    break;
            }
        }

        return byteList;
    }


    public String getItemContext(Item item) {
        if (item instanceof Txt) {
            Txt txtItem = (Txt) item;
            return txtItem.getContext();
        }
        else{
            Exe exeItem = (Exe) item;
            // 将List<Byte>转换为byte[]
            List<Byte> byteList = exeItem.getInstructions();
            byte[] bytes = new byte[byteList.size()];
            for (int i = 0; i < byteList.size(); i++) {
                bytes[i] = byteList.get(i);
            }
            // 将字节数组转换为十六进制字符串
            StringBuilder hexString = new StringBuilder();
            for (byte b : bytes) {
                // 每个字节转换为两位十六进制数，前面补0
                hexString.append(String.format("%02x", b));
                hexString.append("\n"); // 可选：在十六进制数之间添加空格以增加可读性
            }
            // 去除末尾可能存在的多余空格
            if (!hexString.isEmpty() && hexString.charAt(hexString.length() - 1) == ' ') {
                hexString.deleteCharAt(hexString.length() - 1);
            }
            return hexString.toString();
        }
    }

    private int excludeWhitespace(String str) {
        return str == null ? 0 : str.replaceAll("\\s", "").length();
    }

    @Override
    protected void close() {

    }
}
