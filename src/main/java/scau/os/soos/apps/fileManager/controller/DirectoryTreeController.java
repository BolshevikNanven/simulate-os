package scau.os.soos.apps.fileManager.controller;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import scau.os.soos.apps.fileManager.FileManagerApp;
import scau.os.soos.module.file.FileController;
import scau.os.soos.module.file.FileService;
import scau.os.soos.module.file.model.Directory;
import scau.os.soos.module.file.model.Item;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;

public class DirectoryTreeController implements Initializable {
    @FXML
    public TreeView<Item> directoryTree;
    // 路径指针，用于前进和后退
    private IntegerProperty pathPointer;
    // 路径列表，用于前进和后退
    private ListProperty<TreeItem<Item>> pathList;
    // 当前目录
    private ObjectProperty<Item> currentDirectory;
    // 文件与节点映射
    private HashMap<Item, TreeItem<Item>> itemMap;

    private static DirectoryTreeController instance;

    public static DirectoryTreeController getInstance() {
        if (instance == null) {
            throw new RuntimeException("DirectoryTreeController未初始化");
        }
        return instance;
    }

    public Item getCurrentDirectory() {
        return currentDirectory.get();
    }

    public IntegerProperty getPathPointerProperty() {
        return pathPointer;
    }

    public ListProperty<TreeItem<Item>> getPathListProperty() {
        return pathList;
    }

    public ObjectProperty<Item> getCurrentDirectoryProperty() {
        return currentDirectory;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        instance = this;

        pathPointer = new SimpleIntegerProperty(-1);
        pathList = new SimpleListProperty<>(FXCollections.observableArrayList());
        currentDirectory = new SimpleObjectProperty<>(null);
        itemMap = new HashMap<>();

        init();
    }

    private void init() {
        // 加载根目录(加载根目录及其下一层子目录)
        loadRoots();

        setCell();

        addListener();
    }

    private void loadRoots() {
        // 获取系统中的所有逻辑驱动器
        List<Item> allDrives = FileController.getInstance().listRoot();

        // 创建根节点，第一个节点为系统中的第一个逻辑驱动器
        TreeItem<Item> root = new TreeItem<>(FileController.getInstance().getPartitionDirectory());
        directoryTree.setRoot(root);

        // 遍历所有驱动器
        for (Item drive : allDrives) {
            // 检查驱动器是否为空且不是只读的
            if (drive != null && !drive.isReadOnly()) {
                // 为每个磁盘创建一个新的TreeItem
                TreeItem<Item> driveItem = new TreeItem<>(drive);
                itemMap.put(drive, driveItem);

                // 加载当前驱动器的子目录
                loadDirectory(driveItem);

                // 将磁盘节点添加到顶级TreeItem中
                root.getChildren().add(driveItem);
            }
        }
    }

    public void refreshCurrentDirectory() {
        if (currentDirectory.get() != null) {
            loadDirectory(itemMap.get(currentDirectory.get()));
        }
        directoryTree.refresh();
    }

    public void refreshCurrentDirectory(Item item) {
        deleteDirectory(item);
    }

    private void loadDirectory(TreeItem<Item> directoryItem) {
        // 加载当前目录项的直接子项
        loadImmediateChildren(directoryItem);

        // 递归地展开当前目录项的所有子项
        expandChildrenRecursively(directoryItem);
    }

    private void loadImmediateChildren(TreeItem<Item> directoryItem) {
        // 加载指定目录下的所有子目录作为子节点
        Item curItem = directoryItem.getValue();
        // 跳过空项或非目录项
        if (curItem == null || !curItem.isDirectory()) {
            return;
        }

        // 将当前项转换为Directory类型
        Directory dir = (Directory) curItem;
        // 获取当前目录下的所有子项
        List<Item> childItems = dir.getChildren();

        // 遍历所有子项
        for (Item childItem : childItems) {
            // 跳过已经存在的项或非目录项
            if (itemMap.containsKey(childItem) || !childItem.isDirectory()) {
                continue;
            }

            // 为每个子项创建一个新的TreeItem
            TreeItem<Item> childTreeItem = new TreeItem<>(childItem);
            // 将子项添加到当前项的子节点列表中
            directoryItem.getChildren().add(childTreeItem);
            // 将项与树形视图节点关联起来，存入映射中
            itemMap.put(childItem, childTreeItem);
        }
    }

    private void expandChildrenRecursively(TreeItem<Item> parentItem) {
        // 遍历父项的所有子项
        for (TreeItem<Item> item : parentItem.getChildren()) {
            // 为每个子项加载其直接子项
            loadImmediateChildren(item);
        }
    }

    private void deleteDirectory(Item directory) {
        // 从文件与节点映射中获取要删除的目录对应的TreeItem
        TreeItem<Item> treeItem = itemMap.get(directory);
        // 如果未找到对应的TreeItem，则直接返回
        if (treeItem == null) {
            return;
        }
        // 获取要删除的TreeItem的父节点
        TreeItem<Item> parentItem = treeItem.getParent();
        // 如果要删除的目录是根目录（即没有父节点），则打印提示信息并返回
        if (parentItem == null) {
            System.out.println("Cannot delete root directory.");
            return;
        }
        // 从父节点的子节点列表中移除要删除的TreeItem
        parentItem.getChildren().remove(treeItem);
        // 从文件与节点映射中移除对应的条目
        itemMap.remove(directory);
    }

    private void addListener() {
        //点击事件
        directoryTree.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() > 0 &&
                    (pathPointer.get() == -1 || pathList.get(pathPointer.get())
                            != directoryTree.getSelectionModel().getSelectedItem())) {
                // path进list
                pathPointer.set(pathPointer.get() + 1);
                pathList.add(directoryTree.getSelectionModel().getSelectedItem());
            }
        });
        // 选中事件
        directoryTree.getSelectionModel().selectedItemProperty().addListener(
                (observableValue, oldValue, newValue) -> {
                    if (newValue == null || newValue.getValue() == null) {
                        return;
                    }
                    currentDirectory.set(newValue.getValue());
                    //更新当前目录显示
                    ToolBarController.getInstance().showCurrentDirectory(newValue.getValue().getPath());
                    ToolBarController.getInstance().resetSearchButton();
                    ToolBarController.getInstance().resetSortMenu();
                    ToolBarController.getInstance().resetSelectMenu();

                    //重新设置imageModelList 并展示image
                    FileManagerApp.getInstance().loadDirectory(newValue.getValue());
                });
        // 展开事件
        directoryTree.getRoot().addEventHandler(TreeItem.<Item>branchExpandedEvent(),
                event -> loadDirectory(event.getSource()));
    }

    private void setCell() {
//        directoryTree.setShowRoot(false);
        directoryTree.setFocusTraversable(false);
        directoryTree.getRoot().setExpanded(true);

        // 自定义工厂
        directoryTree.setCellFactory(new Callback<>() {
            @Override
            public TreeCell<Item> call(TreeView<Item> fileTreeView) {
                // 自定义TreeCell
                return new TreeCell<>() {
                    private final HBox hbox = new HBox();
                    private final Label label = new Label();
                    private final ImageView imageView = new ImageView();
                    private static final String FOLDER_OPEN_FOR_COPY_PATH = String.valueOf(FileManagerApp.class.getResource("image/directoryTree/folder-open-for-copy.png"));
                    private static final String OPEN_FOLDER_PATH = String.valueOf(FileManagerApp.class.getResource("image/directoryTree/open-folder.png"));
                    private static final String CLOSE_FOLDER_PATH = String.valueOf(FileManagerApp.class.getResource("image/directoryTree/close-folder.png"));

                    {
                        imageView.setPreserveRatio(true);
                        imageView.setFitHeight(16);
                        hbox.getChildren().add(imageView);
                        hbox.getChildren().add(label);
                    }

                    @Override
                    protected void updateItem(Item item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            this.setGraphic(null);
                            return;
                        }
                        // 设置图片和文本
                        label.setText(item.getName());
                        if(item == directoryTree.getRoot().getValue()){
                            label.setText("此电脑");
                        }
                        URL resource = FileManagerApp.class.getResource("image/directoryTree/" +
                                (getTreeItem().isExpanded() ? "open-folder.png" : "close-folder.png"));
                        if (resource != null) {
                            imageView.setImage(new Image(resource.toExternalForm()));
                        }
                        setGraphic(hbox);
                    }
                };
            }
        });
    }

    public void goToDirectory(Item directory) {
        if (directory == null || !directory.isDirectory() || directory == currentDirectory.get()) {
            return;
        }
        if (itemMap.containsKey(directory)) {
            TreeItem<Item> directoryItem = itemMap.get(directory);
            directoryTree.getSelectionModel().select(directoryItem);
            pathPointer.set(pathPointer.get() + 1);
            pathList.add(directoryItem);
            return;
        }
        Item file = directory.getParent();
        Stack<Item> stack = new Stack<>();
        while (file != null) {
            stack.push(file);
            file = file.getParent();
        }
        while (!stack.isEmpty()) {
            Item parent = stack.pop();
            TreeItem<Item> parentItem = itemMap.get(parent);
            try {
                parentItem.setExpanded(true);
            } catch (Exception e) {
                System.out.println(parent.getParent() == null ? parent.toString() : parent.getName() + " is not expanded");
            }
        }
        if (itemMap.containsKey(directory)) {
            TreeItem<Item> directoryItem = itemMap.get(directory);
            directoryTree.getSelectionModel().select(directoryItem);
            pathPointer.set(pathPointer.get() + 1);
            pathList.add(directoryItem);
        }
    }


    // 返回父级目录
    public void upDirectory() {
        TreeItem<Item> item = directoryTree.getSelectionModel().getSelectedItem();
        if (item == null || item.getParent() == null || item.getParent().getParent() == null) {
            return;
        }

        directoryTree.getSelectionModel().select(item.getParent());
        pathPointer.set(pathPointer.get() + 1);
        pathList.add(item.getParent());
    }

    public boolean stepBackward() {
        if (pathPointer.get() > 0) {
            // 路径指针--
            pathPointer.set(pathPointer.get() - 1);
            // 重新设置imageModelList
            TreeItem<Item> item = pathList.get(pathPointer.get());
            // 重新设置TreeView
            directoryTree.getSelectionModel().select(item);
            return true;
        }
        return false;
    }

    public boolean stepForward() {
        if (pathPointer.get() < pathList.size() - 1) {
            // 路径指针++
            pathPointer.set(pathPointer.get() + 1);
            // 重新设置imageModelList
            TreeItem<Item> item = pathList.get(pathPointer.get());
            // 重新设置TreeView
            directoryTree.getSelectionModel().select(item);
            return true;
        }
        return false;
    }
}
