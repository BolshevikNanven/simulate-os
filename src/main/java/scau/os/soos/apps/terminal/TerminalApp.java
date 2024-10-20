package scau.os.soos.apps.terminal;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import scau.os.soos.ui.components.base.Window;

import java.net.URL;
import java.util.ResourceBundle;

public class TerminalApp extends Window implements Initializable {
    @FXML
    private BorderPane TerminalApp;

    public TerminalApp() {
        super("终端", "main.fxml", 900, 520);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.setup(TerminalApp);
    }
}
