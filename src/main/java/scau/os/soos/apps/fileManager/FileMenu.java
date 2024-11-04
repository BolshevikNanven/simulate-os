package scau.os.soos.apps.fileManager;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import scau.os.soos.apps.fileManager.controller.DirectoryTreeController;
import scau.os.soos.apps.fileManager.model.ThumbnailBox;
import scau.os.soos.module.file.FileController;
import scau.os.soos.module.file.model.Item;
import scau.os.soos.ui.components.base.Popover;

import java.io.IOException;
import java.util.List;

public class FileMenu extends Popover {
    private final Button openBtn;
    private final Button copyBtn;
    private final Button pasteBtn;
    private final Button deleteBtn;
    private final Button createTxtBtn;
    private final Button createExeBtn;
    private final Button createDirectoryBtn;
    private final Button reNameBtn;
    private final Button reAttributeBtn;

    public FileMenu() {
        this.gap = 4;

        openBtn = (Button) this.container.lookup("#open-btn");
        copyBtn = (Button) this.container.lookup("#copy-btn");
        pasteBtn = (Button) this.container.lookup("#paste-btn");
        deleteBtn = (Button) this.container.lookup("#delete-btn");
        createTxtBtn = (Button) this.container.lookup("#create-txt-btn");
        createExeBtn = (Button) this.container.lookup("#create-exe-btn");
        createDirectoryBtn = (Button) this.container.lookup("#create-directory-btn");
        reNameBtn = (Button) this.container.lookup("#re-name-btn");
        reAttributeBtn = (Button) this.container.lookup("#re-attribute-btn");

        addListener();
    }

    private void addListener() {
        openBtn.setOnAction((e) -> {
            Item item = FileManagerApp.getInstance().getSelectedList().get(0).getItem();
            FileManagerApp.getInstance().open(item);
            hide();
        });
        copyBtn.setOnAction(actionEvent -> {
            hide();
        });
        pasteBtn.setOnAction(actionEvent -> {
            hide();
        });
        deleteBtn.setOnAction(actionEvent -> {
            List<ThumbnailBox> items = FileManagerApp.getInstance().getSelectedList();
            for (ThumbnailBox thumbnailBox : items) {
                Item item = thumbnailBox.getItem();
                if (item.isDirectory()) {
                    FileController.getInstance().deleteDirectory(item.getPath());
                } else {
                    FileController.getInstance().deleteFile(item.getPath());
                }
            }
            FileManagerApp.getInstance().refreshCurrentDirectory();
            hide();
        });
        createTxtBtn.setOnAction(actionEvent -> {
            Item cur = DirectoryTreeController.getInstance().getCurrentDirectory();
            if (cur == null) {
                return;
            }
            FileController.getInstance().createFile(cur.getPath() + "new.t");
            FileManagerApp.getInstance().refreshCurrentDirectory();
            hide();
        });
        createExeBtn.setOnAction(actionEvent -> {
            Item cur = DirectoryTreeController.getInstance().getCurrentDirectory();
            FileController.getInstance().createFile(cur.getPath() + "new.e");
            FileManagerApp.getInstance().refreshCurrentDirectory();
            hide();
        });
        createDirectoryBtn.setOnAction(actionEvent -> {
            Item cur = DirectoryTreeController.getInstance().getCurrentDirectory();
            FileController.getInstance().createDirectory(cur.getPath() + "new");
            FileManagerApp.getInstance().refreshCurrentDirectory();
            hide();
        });
        reNameBtn.setOnAction(actionEvent -> {
            hide();
        });
        reAttributeBtn.setOnAction(actionEvent -> {
            hide();
        });
    }

    @Override
    protected Pane setup() {
        FXMLLoader loader = new FXMLLoader(FileManagerApp.class.getResource("components/file_menu.fxml"));
        try {
            return loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
