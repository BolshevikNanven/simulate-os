package scau.os.soos.apps.fileManager;

import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import scau.os.soos.ui.components.DesktopAreaSelect;
import scau.os.soos.ui.components.base.Window;

import java.net.URL;
import java.util.ResourceBundle;

public class FileManagerApp extends Window {
    public FileManagerApp() {
        super("文件管理器", "main.fxml", 900, 600);
    }

    @Override
    public void initialize() {
        new FileAreaSelect((ScrollPane) body);
    }

    @Override
    protected void close() {

    }
}
