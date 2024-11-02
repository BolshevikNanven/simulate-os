package scau.os.soos.apps.fileManager.controller;

import javafx.fxml.Initializable;
import scau.os.soos.module.file.FileController;
import scau.os.soos.module.file.FileService;

import java.net.URL;
import java.util.ResourceBundle;

public class ToolBarController implements Initializable {
    private static ToolBarController instance;

    public static ToolBarController getInstance() {
        if (instance == null) {
            throw new RuntimeException("未初始化ToolBarController");
        }
        return instance;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        instance = this;
    }
}
