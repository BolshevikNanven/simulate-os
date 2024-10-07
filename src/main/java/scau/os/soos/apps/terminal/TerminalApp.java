package scau.os.soos.apps.terminal;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import scau.os.soos.apps.AppUtil;
import scau.os.soos.ui.components.Window;

import java.net.URL;
import java.util.ResourceBundle;

public class TerminalApp extends Window implements Initializable {
    @FXML
    private BorderPane TerminalApp;

    public TerminalApp() {
        super("终端", "apps/terminal/icon.png", 900, 520);
        AppUtil.loadFXML(this, "main.fxml");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        load(TerminalApp);
    }
}
