package scau.os.soos;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import scau.os.soos.common.GlobalUI;
import scau.os.soos.ui.DesktopManager;
import scau.os.soos.ui.TaskBarManager;

import java.net.URL;
import java.util.ResourceBundle;

public class MainUIController implements Initializable {
    @FXML
    private BorderPane root;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        GlobalUI.rootNode = root;
        setupUI();
    }

    private void setupUI() {
        TaskBarManager.getInstance().init();
        DesktopManager.getInstance().init();
    }
}
