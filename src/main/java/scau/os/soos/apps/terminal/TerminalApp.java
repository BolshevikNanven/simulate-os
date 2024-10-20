package scau.os.soos.apps.terminal;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import scau.os.soos.ui.components.base.Window;

public class TerminalApp extends Window{
    @FXML
    private TextField input;
    public TerminalApp() {
        super("终端", "main.fxml", 900, 520);
    }
    @Override
    protected void initialize() {
        input.setText("6");
    }
}
