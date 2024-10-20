package scau.os.soos.apps.terminal;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import scau.os.soos.common.OS;
import scau.os.soos.common.model.Handler;
import scau.os.soos.ui.components.base.Window;

public class TerminalApp extends Window {
    @FXML
    private TextField input;
    private Handler handler;

    public TerminalApp() {
        super("终端", "main.fxml", 900, 520);
    }

    @Override
    protected void initialize() {
        handler = () -> input.setText("时钟：" + OS.clock.get());
        OS.clock.bind(handler);
    }

    @Override
    protected void close() {
        OS.clock.unBind(handler);
    }
}
