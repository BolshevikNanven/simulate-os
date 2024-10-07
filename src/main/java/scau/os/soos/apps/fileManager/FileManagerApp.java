package scau.os.soos.apps.fileManager;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import scau.os.soos.ui.components.Window;

import java.net.URL;
import java.util.ResourceBundle;

public class FileManagerApp extends Window implements Initializable {
    public FileManagerApp() {
        super("文件管理器", "apps/fileManager/icon.png", 720, 600);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
