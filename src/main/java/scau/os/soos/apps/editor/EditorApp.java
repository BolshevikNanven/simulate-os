package scau.os.soos.apps.editor;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import scau.os.soos.module.file.model.Exe;
import scau.os.soos.module.file.model.Item;
import scau.os.soos.module.file.model.Txt;
import scau.os.soos.ui.components.base.Window;

import java.awt.*;
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
            List<Byte> byteList = new ArrayList<>();
            for (char c : display.getText().toCharArray()) {
                byteList.add((byte) c);
            }
            exeItem.setInstructions(byteList);
        }
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
            String result = new String(bytes);
            return result;
        }
    }

    private int excludeWhitespace(String str) {
        return str == null ? 0 : str.replaceAll("\\s", "").length();
    }

    @Override
    protected void close() {

    }
}
