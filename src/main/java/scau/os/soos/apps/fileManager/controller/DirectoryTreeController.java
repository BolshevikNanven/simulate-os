package scau.os.soos.apps.fileManager.controller;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import scau.os.soos.apps.fileManager.FileManagerApp;
import scau.os.soos.module.file.FileController;
import scau.os.soos.module.file.Notifier;
import scau.os.soos.module.file.model.Directory;
import scau.os.soos.module.file.model.Item;

import java.net.URL;
import java.util.*;

public class DirectoryTreeController implements Initializable, Notifier {
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

        FileController.getInstance().bind(this);
    }

    private void init() {
        // 加载根目录(加载根目录及其下一层子目录)
        loadRoots();

        setCell();

        addListener();
    }

    public void initAfterInitialize(){
        directoryTree.getSelectionModel().select(directoryTree.getRoot());
        pathPointer.set(pathPointer.get() + 1);
        pathList.add(directoryTree.getSelectionModel().getSelectedItem());
    }

    private void loadRoots() {
        Directory partitionDirectory = FileController.getInstance().getPartitionDirectory();
        // 获取系统中的所有逻辑驱动器
        List<Item> allDrives = FileController.getInstance().listRoot();

        // 创建根节点，第一个节点为系统中的第一个逻辑驱动器
        TreeItem<Item> root = new TreeItem<>(partitionDirectory);
        directoryTree.setRoot(root);
        itemMap.put(partitionDirectory, root);

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

    private void refreshDirectory(Item item) {
        if (item == null || !item.isDirectory()|| itemMap.get(item)==null) {
            return;
        }
        loadDirectory(itemMap.get(item));
        directoryTree.refresh();
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
        // 获取当前目录下的所有子项（从文件系统中）
        List<Item> childItemsFromFileSystem = dir.getChildren();

        // 获取当前目录项的所有子节点（从TreeView中）
        ObservableList<TreeItem<Item>> currentChildren = directoryItem.getChildren();
        Set<Item> itemsToRemove = new HashSet<>();

        // 找出需要移除的项
        Item root = directoryTree.getRoot().getValue();
        for (Item item : itemMap.keySet()) {
            if (item.getParent() != null || item == root) {
                continue;
            }
            // 由于我们稍后将从itemMap中移除这个项，我们需要先获取对应的TreeItem
            TreeItem<Item> treeItem = itemMap.get(item);
            // 检查这个TreeItem是否确实在当前目录项的子节点列表中
            if (currentChildren.contains(treeItem)) {
                itemsToRemove.add(item);
            }
        }

        // 移除需要移除的项
        for (Item item : itemsToRemove) {
            currentChildren.remove(itemMap.get(item));
            itemMap.remove(item);
        }

        // 遍历所有子项
        for (Item childItem : childItemsFromFileSystem) {
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
                        if (item == directoryTree.getRoot().getValue()) {
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

    @Override
    public void update(Item item) {
        refreshDirectory(item.getParent());
    }
}
