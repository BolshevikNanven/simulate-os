package scau.os.soos.apps.terminal;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import scau.os.soos.common.OS;
import scau.os.soos.common.model.Handler;
import scau.os.soos.module.terminal.TerminalController;
import scau.os.soos.ui.components.base.Window;

import java.util.ArrayList;
import java.util.Stack;

public class TerminalApp extends Window {
    @FXML
    private TextArea textArea;
    // 提示符
    private final String prompt = "> ";


    public TerminalApp() {
        super("终端", "main.fxml", 900, 520);
    }

    @Override
    protected void initialize() {
        //获取根目录
        String directory = "C:/";
        textArea.appendText(directory + prompt);
        addListener();
    }

    private void addListener() {
        textArea.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleEnter();
            } else if (event.getCode() == KeyCode.UP) {
                handleUpArrow();
            }
        });
        textArea.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.BACK_SPACE || event.getCode() == KeyCode.LEFT) {
                String currentText = textArea.getText();
                int lastPromptIndex = currentText.lastIndexOf(prompt);
                if (textArea.getCaretPosition() <= lastPromptIndex + prompt.length()){
                    event.consume();
                }
            }
            if (event.getCode() == KeyCode.ENTER){
                textArea.positionCaret(textArea.getText().length());
            }
        });
        textArea.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            if (event.getCharacter().equals(prompt.trim())) {
                event.consume();
            }
        });

    }

    private void handleEnter() {
        String currentText = textArea.getText();

        int lastPromptIndex = currentText.lastIndexOf(prompt);
        String currentCommand = currentText.substring(lastPromptIndex + prompt.length()).trim();
        if (!currentCommand.isEmpty()) {
            TerminalController.getInstance().addCommand(currentCommand);
            String feedback = TerminalController.getInstance().executeCommand(currentCommand); // 执行命令并获取反馈
            if(!feedback.isEmpty())
                textArea.appendText(feedback + "\n");
            else
                textArea.appendText("exe:" + currentCommand + '\n'); //  ??
        }
        textArea.appendText(TerminalController.getInstance().getCurrentDirectory() + prompt);
    }

    private void handleUpArrow() {
        if(!TerminalController.getInstance().isHistoryEmpty()){
            String lastCommand = TerminalController.getInstance().getLastCommand();
            int lastPromptIndex = textArea.getText().lastIndexOf(prompt);
            int commandLength = textArea.getText().length() - lastPromptIndex - prompt.length();
            textArea.deleteText(lastPromptIndex + prompt.length(), lastPromptIndex + prompt.length() + commandLength);
            textArea.appendText(lastCommand);




        }
    }



    // 光标不能随鼠标
    // 左键不能过>
    // 在中间换行bug
    // nextCommand
    @Override
    protected void close() {
//        OS.clock.unBind(handler);
    }
}
