package scau.os.soos.apps.fileManager;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import scau.os.soos.apps.fileManager.controller.ToolBarController;
import scau.os.soos.apps.fileManager.model.ThumbnailBox;
import scau.os.soos.common.exception.ConcurrentAccessException;
import scau.os.soos.common.exception.IllegalOperationException;
import scau.os.soos.common.exception.ItemNotFoundException;
import scau.os.soos.common.exception.ReadOnlyFileModifiedException;
import scau.os.soos.module.file.FileController;
import scau.os.soos.module.file.model.Item;
import scau.os.soos.ui.components.Dialog;
import scau.os.soos.ui.components.base.Popover;

import java.io.IOException;
import java.util.List;

public class FileMenu extends Popover {
    private final VBox menu;
    private final Button openBtn;
    private final Button operateBtn;
    private final Region separator1;
    private final Button copyBtn;
    private final Button pasteBtn;
    private final Button deleteBtn;
    private final Region separator2;
    private final Button createTxtBtn;
    private final Button createExeBtn;
    private final Button createDirectoryBtn;
    private final Region separator3;
    private final Button reNameBtn;
    private final Button reAttributeBtn;

    public FileMenu() {
        this.gap = 4;

        menu = (VBox) this.container.lookup("#menu");
        openBtn = (Button) this.container.lookup("#open-btn");
        operateBtn = (Button) this.container.lookup("#operate-btn");
        separator1 = (Region) this.container.lookup("#separator-1");
        copyBtn = (Button) this.container.lookup("#copy-btn");
        pasteBtn = (Button) this.container.lookup("#paste-btn");
        deleteBtn = (Button) this.container.lookup("#delete-btn");
        separator2 = (Region) this.container.lookup("#separator-2");
        createTxtBtn = (Button) this.container.lookup("#create-txt-btn");
        createExeBtn = (Button) this.container.lookup("#create-exe-btn");
        createDirectoryBtn = (Button) this.container.lookup("#create-directory-btn");
        separator3 = (Region) this.container.lookup("#separator-3");
        reNameBtn = (Button) this.container.lookup("#re-name-btn");
        reAttributeBtn = (Button) this.container.lookup("#re-attribute-btn");

        addListener();
    }

    private void updateMenuVisibility(boolean openBtn, boolean operateBtn, boolean separator1,
                                      boolean copyBtn, boolean pasteBtn, boolean deleteBtn,
                                      boolean separator2, boolean createTxtBtn, boolean createExeBtn,
                                      boolean createDirectoryBtn, boolean separator3, boolean reNameBtn, boolean reAttributeBtn
    ) {
        menu.getChildren().clear();
        setMenuItemVisibility(this.openBtn, openBtn);
        setMenuItemVisibility(this.operateBtn, operateBtn);
        setMenuItemVisibility(this.separator1, separator1);
        setMenuItemVisibility(this.copyBtn, copyBtn);
        setMenuItemVisibility(this.pasteBtn, pasteBtn);
        setMenuItemVisibility(this.deleteBtn, deleteBtn);
        setMenuItemVisibility(this.separator2, separator2);
        setMenuItemVisibility(this.createTxtBtn, createTxtBtn);
        setMenuItemVisibility(this.createExeBtn, createExeBtn);
        setMenuItemVisibility(this.createDirectoryBtn, createDirectoryBtn);
        setMenuItemVisibility(this.separator3, separator3);
        setMenuItemVisibility(this.reNameBtn, reNameBtn);
        setMenuItemVisibility(this.reAttributeBtn, reAttributeBtn);
    }

    private void setMenuItemVisibility(Node target, boolean visible) {
        if (!visible) {
            menu.getChildren().remove(target);
        } else {
            menu.getChildren().add(target);
        }
    }

    public void renderOverPane(MouseEvent e, boolean isRenderPasteBtn) {
        updateMenuVisibility(false, false, false,
                false, isRenderPasteBtn, false,
                isRenderPasteBtn, true, true,
                true, false, false, false);
        super.render(e);
    }

    public void renderOverItem(MouseEvent e) {
        updateMenuVisibility(true, true, true,
                true, false, true,
                true, false, false,
                false, false, true, true);
        super.render(e);
    }

    private void addListener() {
        openBtn.setOnAction((e) -> {
            hide();
            List<ThumbnailBox> selectedList = FileManagerApp.getInstance().getSelectedList();
            for (ThumbnailBox selected : selectedList) {
                FileManagerApp.getInstance().open(selected.getItem());
            }
        });
        operateBtn.setOnAction((e) -> {
            hide();
            List<ThumbnailBox> selectedList = FileManagerApp.getInstance().getSelectedList();
            for (ThumbnailBox selected : selectedList) {
                FileManagerApp.getInstance().run(selected.getItem());
            }
        });
        copyBtn.setOnAction(actionEvent -> {
            hide();
            ToolBarController.getInstance().copyBtn.fire();
        });
        pasteBtn.setOnAction(actionEvent -> {
            hide();
            ToolBarController.getInstance().pasteBtn.fire();
        });
        deleteBtn.setOnAction(actionEvent -> {
            hide();
            ToolBarController.getInstance().deleteBtn.fire();
        });
        createTxtBtn.setOnAction(actionEvent -> {
            hide();
            ToolBarController.getInstance().createTxtBtn.fire();
        });
        createExeBtn.setOnAction(actionEvent -> {
            hide();
            ToolBarController.getInstance().createExeBtn.fire();
        });
        createDirectoryBtn.setOnAction(actionEvent -> {
            hide();
            ToolBarController.getInstance().createDirectoryBtn.fire();
        });
        reNameBtn.setOnAction(actionEvent -> {
            hide();
            ToolBarController.getInstance().reNameBtn.fire();
        });
        reAttributeBtn.setOnAction(actionEvent -> {
            hide();
            List<ThumbnailBox> selectedList = FileManagerApp.getInstance().getSelectedList();
            if (selectedList.isEmpty()) {
                return;
            }
            Item targetItem = selectedList.get(0).getItem();
            String path = targetItem.getPath();

            // 创建并初始化CheckBox
            CheckBox readOnlyCheckBox = createCheckBox("只读", targetItem.isReadOnly());
            CheckBox systemFileCheckBox = createCheckBox("系统", targetItem.isSystemFile());
            CheckBox regularFileCheckBox = createCheckBox("文件", targetItem.isRegularFile());
            CheckBox directoryCheckBox = createCheckBox("目录", targetItem.isDirectory());

            // 创建一个HBox容器，用于放置CheckBox
            HBox hBox = new HBox(30);
            hBox.setAlignment(Pos.TOP_CENTER);

            hBox.setPadding(new Insets(10));
            hBox.getChildren().addAll(readOnlyCheckBox, systemFileCheckBox, regularFileCheckBox, directoryCheckBox);
            Dialog dialog = Dialog.getDialog(FileManagerApp.getInstance(), "属性\t"+targetItem.getFullName(),
                    true, true,
                    confirm -> {
                        try {
                            FileController.getInstance().reAttribute(path,
                                    readOnlyCheckBox.isSelected(),
                                    systemFileCheckBox.isSelected(),
                                    regularFileCheckBox.isSelected(),
                                    directoryCheckBox.isSelected());
                        } catch (IllegalOperationException | ItemNotFoundException e) {
                            Dialog.getEmptyDialog(FileManagerApp.getInstance(), e.getMessage()).show();
                        } catch (ConcurrentAccessException e) {
                            Dialog.getDialog(FileManagerApp.getInstance(), "不允许为正在打开的文件设置属性",
                                    true, false,
                                    null, null,
                                    null).show();
                        }
                    }, null,
                    hBox);
            dialog.show();
        });
    }

    // 创建一个辅助方法来创建并初始化CheckBox
    private CheckBox createCheckBox(String text, boolean selected) {
        CheckBox checkBox = new CheckBox(text);
        checkBox.setSelected(selected);
        return checkBox;
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
