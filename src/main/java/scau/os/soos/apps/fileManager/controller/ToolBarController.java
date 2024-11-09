package scau.os.soos.apps.fileManager.controller;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import scau.os.soos.apps.fileManager.FileManagerApp;
import scau.os.soos.common.enums.FILE_TYPE;
import scau.os.soos.module.file.FileController;
import scau.os.soos.module.file.model.Item;
import scau.os.soos.ui.dialog.Dialog;

import java.net.URL;
import java.util.ResourceBundle;

public class ToolBarController implements Initializable {
    @FXML
    public Button leftBtn;
    @FXML
    public Button rightBtn;
    @FXML
    public Button upBtn;
    @FXML
    public Button refreshBtn;

    @FXML
    public TextField currentDirectory;
    @FXML
    public Button goToBtn;
    @FXML
    public TextField searchTextField;
    @FXML
    public Button searchBtn;

    @FXML
    public MenuButton createBtn;
    @FXML
    public MenuItem createTxtBtn;
    @FXML
    public MenuItem createExeBtn;
    @FXML
    public MenuItem createDirectoryBtn;

    @FXML
    public Button shearBtn;
    @FXML
    public Button copyBtn;
    @FXML
    public Button pasteBtn;
    @FXML
    public Button reNameBtn;
    @FXML
    public Button deleteBtn;

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
        // 为后退按钮添加监听器
        addListenerForBackwardButton();
        // 为前进按钮添加监听器
        addListenerForForwardButton();
        // 为上级目录按钮添加监听器
        addListenerForUpButton();
        // 为刷新按钮添加监听器
        addListenerForRefreshButton();

        // 为当前目录文本框添加监听器
        addListenerForCurrentDirectoryTextField();
        // 为跳转到指定目录按钮添加监听器
        addListenerForGoToButton();


        // 为剪切按钮添加监听器
        addListenerForShearButton();
        // 为复制按钮添加监听器
        addListenerForCopyButton();
        // 为粘贴按钮添加监听器
        addListenerForPasteButton();
        // 为重命名按钮添加监听器
        addListenerForRenameButton();
        // 为删除按钮添加监听器
        addListenerForDeleteButton();
    }

    /**
     * 为后退按钮添加事件监听器。
     */
    private void addListenerForBackwardButton() {
        leftBtn.setOnAction(e -> DirectoryTreeController.getInstance().stepBackward());
        // 绑定到路径指针属性上，当路径指针小于等于0时，按钮被禁用
        leftBtn.disableProperty().bind(
                DirectoryTreeController.getInstance().getPathPointerProperty()
                        .lessThanOrEqualTo(0));
    }

    /**
     * 为前进按钮添加事件监听器。
     */
    private void addListenerForForwardButton() {
        rightBtn.setOnAction(e -> DirectoryTreeController.getInstance().stepForward());

        DirectoryTreeController controller = DirectoryTreeController.getInstance();
        // 如果路径指针大于或等于路径列表大小减1（表示最后一个元素的索引），或者路径指针小于0
        // 则按钮应该被禁用
        rightBtn.disableProperty().bind(Bindings.createBooleanBinding(() ->
                        controller.getPathPointerProperty().get() >= controller.getPathListProperty().size() - 1
                                ||
                                controller.getPathPointerProperty().get() < 0,
                controller.getPathPointerProperty(),
                controller.getPathListProperty()));
    }

    /**
     * 为上移按钮添加事件监听器。
     */
    private void addListenerForUpButton() {
        upBtn.setOnAction(e -> DirectoryTreeController.getInstance().upDirectory());

        // 获取目录树的选中项模型
        MultipleSelectionModel<TreeItem<Item>> selectionModel = DirectoryTreeController.getInstance().directoryTree.selectionModelProperty().getValue();
        // 确保只有当选中项不是根项时，上移按钮才可用
        upBtn.disableProperty().bind(Bindings.createBooleanBinding(() -> {
            TreeItem<Item> value = selectionModel.getSelectedItem();
            return value == null ||
                    value.getParent() == null ||
                    value.getParent().getParent() == null;
        }, selectionModel.selectedItemProperty()));
    }

    /**
     * 为刷新按钮添加事件监听器。
     */
    private void addListenerForRefreshButton() {
        refreshBtn.setOnAction(e -> {
            // 刷新目录树
            DirectoryTreeController.getInstance().refreshCurrentDirectory();
            // 刷新文件列表
            FileManagerApp.getInstance().refreshCurrentDirectory();
        });
    }

    /**
     * 为当前目录文本框添加事件监听器。
     */
    private void addListenerForCurrentDirectoryTextField() {
        currentDirectory.focusedProperty().addListener((observable, oldValue, newValue) -> {
            // 如果currentDirectory失去了焦点，并且goToBtn也没有获得焦点
            if (!newValue && !goToBtn.isFocused()){
                // 根据当前目录项更新currentDirectory的文本内容
                Item directory = DirectoryTreeController.getInstance().getCurrentDirectory();
                currentDirectory.setText(directory == null ? null : directory.getPath());
            }
        });
        // 回车跳转到指定目录
        currentDirectory.setOnKeyPressed(event->{
            if(event.getCode().equals(KeyCode.ENTER)){
                handleGoToButtonClick();
            }
        });
    }

    /**
     * 为跳转到指定目录按钮添加事件监听器。
     */
    private void addListenerForGoToButton() {
        goToBtn.setOnAction(e -> {
            handleGoToButtonClick();
        });
    }

    private void handleGoToButtonClick() {
        String path = currentDirectory.getText();
        Item target = FileController.getInstance().findItem(path, FILE_TYPE.DIRECTORY);
        System.out.println(target);

        if (target == null) {
            String errorMessage = "找不到:\"" + (path == null ? "" : path) + "\", 请检查拼写并重试";
            Label label = new Label(errorMessage);
            Item directory = DirectoryTreeController.getInstance().getCurrentDirectory();
            // 显示对话框, 将文本框内容设置为当前目录的路径
            Dialog.getDialog(FileManagerApp.getInstance(),
                    "文件管理器",
                    aBoolean -> currentDirectory.setText(
                            directory == null ? null : directory.getPath()),
                    label).show();
            return;
        }
        // 如果找到了目标目录，则跳转到该目录
        DirectoryTreeController.getInstance().goToDirectory(target);
    }

    public void showCurrentDirectory(String directory) {
        // 目录树调用
        // 显示当前目录
        currentDirectory.setText(directory); // 绝对路径
    }


    /**
     * 为剪切按钮添加事件监听器（当前为空实现）。
     */
    private void addListenerForShearButton() {
        shearBtn.setOnAction(e -> {
            // TODO: 实现剪切功能
        });
    }

    /**
     * 为复制按钮添加事件监听器（当前为空实现）。
     */
    private void addListenerForCopyButton() {
        copyBtn.setOnAction(e -> {
            // TODO: 实现复制功能
        });
    }

    /**
     * 为粘贴按钮添加事件监听器（当前为空实现）。
     */
    private void addListenerForPasteButton() {
        pasteBtn.setOnAction(e -> {
            // TODO: 实现粘贴功能
        });
    }

    /**
     * 为重命名按钮添加事件监听器（当前为空实现）。
     */
    private void addListenerForRenameButton() {
        reNameBtn.setOnAction(e -> {
            // TODO: 实现重命名功能
        });
    }

    /**
     * 为删除按钮添加事件监听器（当前为空实现）。
     */
    private void addListenerForDeleteButton() {
        deleteBtn.setOnAction(e -> {
            // TODO: 实现删除功能
        });
    }
}