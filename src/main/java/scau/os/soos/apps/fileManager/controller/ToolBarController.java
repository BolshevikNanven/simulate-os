package scau.os.soos.apps.fileManager.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import scau.os.soos.apps.fileManager.FileManagerApp;

import java.net.URL;
import java.util.ResourceBundle;

public class ToolBarController implements Initializable {
    @FXML public Button leftBtn;
    @FXML public Button rightBtn;
    @FXML public Button upBtn;
    @FXML public Button refreshBtn;
    @FXML public Button shearBtn;
    @FXML public Button copyBtn;
    @FXML public Button pasteBtn;
    @FXML public Button reNameBtn;
    @FXML public Button deleteBtn;

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

        addListener();
    }

    private void addListener() {
        leftBtn.setOnAction((e)->
                DirectoryTreeController.getInstance().stepBackward());

        rightBtn.setOnAction((e)->
                DirectoryTreeController.getInstance().stepForward());

        upBtn.setOnAction((e)->
                DirectoryTreeController.getInstance().upDirectory());

        refreshBtn.setOnAction((e)->
                FileManagerApp.getInstance().refreshCurrentDirectory());

        shearBtn.setOnAction((e)->{

        });

        copyBtn.setOnAction((e)->{

        });

        pasteBtn.setOnAction((e)->{

        });

        reNameBtn.setOnAction((e)->{

        });

        deleteBtn.setOnAction((e)->{

        });
    }
}
