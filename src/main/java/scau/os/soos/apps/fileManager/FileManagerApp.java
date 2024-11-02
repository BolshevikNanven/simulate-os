package scau.os.soos.apps.fileManager;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import scau.os.soos.MainApplication;
import scau.os.soos.module.file.FileController;
import scau.os.soos.module.file.model.Directory;
import scau.os.soos.module.file.model.Item;
import scau.os.soos.ui.components.base.Window;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;

public class FileManagerApp extends Window {
    @FXML
    public ScrollPane selectedArea;
    @FXML
    public FlowPane itemContainer;
    @FXML
    public Label itemNumber;
    @FXML
    public Label itemSelectedNumber;
    @FXML
    public TreeView<Item> directoryTree;
    // 路径指针，用于前进和后退
    private int pathPointer;
    // 路径列表，用于前进和后退
    private ArrayList<TreeItem<Item>> pathList;
    // 当前目录
    private Item currentDirectory;
    // 文件与节点映射
    private HashMap<Item, TreeItem<Item>> itemMap;
    private ArrayList<Item> itemList;

    public FileManagerApp() {
        super("文件管理器", "main.fxml", 900, 600);
    }

    @Override
    public void initialize() {
        pathPointer = -1;
        pathList = new ArrayList<>();
        itemMap = new HashMap<>();

        init();

        new FileAreaSelect(selectedArea);
    }

    @Override
    protected void close() {

    }

    private void init() {
        // 加载根目录(加载根目录及其下一层子目录)
        loadRoots();

        setCell();

        addListener();
    }

    private void loadRoots() {
        // 获取系统中的所有逻辑驱动器
        List<Item> allDrives = FileController.getInstance().getRoot();

        // 创建根节点，第一个节点为系统中的第一个逻辑驱动器
        TreeItem<Item> root = new TreeItem<>(allDrives.get(0));
        directoryTree.setRoot(root);

        // 遍历所有驱动器
        for (Item drive : allDrives) {
            // 检查驱动器是否为空且不是只读的
            if (drive != null && !drive.isReadOnly()) {
                // 为每个磁盘创建一个新的TreeItem
                TreeItem<Item> driveItem = new TreeItem<>(drive);
                itemMap.put(drive, driveItem);

                // 加载磁盘下一级内容
                List<Item> items = ((Directory) drive).getChildren();
                for (Item item : items) {
                    if (!item.isDirectory()) {
                        continue;
                    }
                    // 为每个磁盘下一级内容创建一个新的TreeItem
                    TreeItem<Item> childrenItems = new TreeItem<>(item);

                    // 将新的TreeItem添加到磁盘节点中
                    driveItem.getChildren().add(childrenItems);

                    // 将文件与新的TreeItem关联，添加到文件与节点映射中
                    itemMap.put(item, childrenItems);
                }

                // 将磁盘节点添加到顶级TreeItem中
                root.getChildren().add(driveItem);
            }
        }
    }

    private void loadChildren(TreeItem<Item> directoryItem) {
        for (TreeItem<Item> item : directoryItem.getChildren()) {
            // 加载指定目录下的所有子目录作为子节点
            Item currentItem = item.getValue();
            // 跳过空项或非目录项
            if (currentItem == null || !currentItem.isDirectory()) {
                continue;
            }

            Directory directory = (Directory) currentItem;
            List<Item> childrenItems = directory.getChildren();
            for (Item childItem : childrenItems) {
                // 跳过已经存在的项
                if (itemMap.containsKey(childItem) || !childItem.isDirectory()) {
                    continue;
                }
                TreeItem<Item> childTreeItem = new TreeItem<>(childItem);
                // 将子项添加到当前项的子节点列表中
                item.getChildren().add(childTreeItem);
                // 将项与树形视图节点关联起来，存入映射中
                itemMap.put(childItem, childTreeItem);
            }
        }
    }


    private void addListener() {
        //点击事件
        directoryTree.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() > 0 &&
                    (pathPointer == -1 || pathList.get(pathPointer)
                            != directoryTree.getSelectionModel().getSelectedItem())) {
                // path进list
                pathPointer++;
                pathList.add(directoryTree.getSelectionModel().getSelectedItem());
            }
        });
        // 选中事件
        directoryTree.getSelectionModel().selectedItemProperty().addListener(
                (observableValue, oldValue, newValue) -> {
            if (newValue == null || newValue.getValue() == null) {
                return;
            }
            currentDirectory = newValue.getValue();
//            // 更新当前目录显示
//            ControllerManager.toolBarController.showCurrentDirectory(newValue.getValue().getAbsolutePath());
//            // 重新设置imageModelList 并展示image
//            ControllerManager.thumbnailPaneController.loadDirectory(newValue.getValue());
        });
        // 展开事件
        directoryTree.getRoot().addEventHandler(TreeItem.<Item>branchExpandedEvent(),
                event -> loadChildren(event.getSource()));
    }

    private void setCell() {
        directoryTree.setShowRoot(false);
        directoryTree.setFocusTraversable(false);

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
                        URL resource = getClass().getResource("image/directoryTree/" +
                                (getTreeItem().isExpanded() ? "openFolder.png" : "closeFolder.png"));
                        if (resource != null) {
                            imageView.setImage(new Image(resource.toExternalForm()));
                        }
                        setGraphic(hbox);
//                        // 拖拽copy
//                        hbox.setOnDragEntered(event -> {
//                            imageView.setImage(new Image(String.valueOf(getClass().getResource("/com/od/imageview/image/directoryTree/folderOpenforCopy.png"))));
//                        });
//                        hbox.setOnDragExited(event -> {
//                            if (this.getTreeItem().isExpanded()) {
//                                imageView.setImage(new Image(String.valueOf(getClass().getResource("/com/od/imageview/image/directoryTree/openFolder.png"))));
//                            } else if (!this.getTreeItem().isExpanded()) {
//                                imageView.setImage(new Image(String.valueOf(getClass().getResource("/com/od/imageview/image/directoryTree/closeFolder.png"))));
//                            }
//                        });
//                        hbox.setOnDragOver(event -> {
//                            Dragboard db = event.getDragboard();
//                            if (db.hasFiles()) {
//                                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
//                            } else {
//                                event.consume();
//                            }
//                        });
//                        hbox.setOnDragDropped(event -> {
//                            // 将拖拽的文件复制进当前目录
//                            Dragboard db = event.getDragboard();
//                            db.getFiles().forEach(file -> {
//                                String name = file.getName();
//                                // 复制文件到当前目录
//                                File newFile = new File(item, name);
//                                if (newFile.exists()) newFile = FileUtil.unConflictFile(newFile);
//                                try {
//                                    Files.copy(file.toPath(), newFile.toPath());
//                                } catch (IOException e) {
//                                    throw new RuntimeException(e);
//                                }
//                            });
//                            if (currentDirectory == item)
//                                ControllerManager.thumbnailPaneController.loadDirectory(item);
//                        });
//                        this.setGraphic(hbox);
                    }
                };
            }
        });
    }

//    public void displayItem() {
////        // 清空选择列表
////        clearSelectedList();
//        // 清空ThumbnailPane中的所有子节点
//        itemContainer.getChildren().clear();
//        // 如果imageModelList为空，则直接返回
//        if (itemList.isEmpty())
//            return;
//        // 遍历imageModelList列表，为每个ImageModel创建一个ThumbnailBox，并将其添加到ThumbnailPane中
//        // 自适应调整大小，显示图片和标签
//        for (int i = 0; i < itemList.size(); i++) {
//            ImageModel imageModel = itemList.get(i);
//            // 将ThumbnailBox添加到ThumbnailPane中
//            thumbnailPane.getChildren().add(new ThumbnailBoxModel(imageModel,ControllerManager.toolBarController.getThumbnailBoxSlider().valueProperty()));
//        }
//        if (task != null && task.isRunning()) {
//            task.cancel();
//        }
//        newLoadImageTask(thumbnailPane);
//        executor.execute(task);
//    }

    public Item getCurrentDirectory() {
        return currentDirectory;
    }


    public void goToDirectory(Item directory) {
        if (directory == null || !directory.isDirectory() || directory == currentDirectory) {
            return;
        }
        if (itemMap.containsKey(directory)) {
            TreeItem<Item> directoryItem = itemMap.get(directory);
            directoryTree.getSelectionModel().select(directoryItem);
            pathPointer++;
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
            pathPointer++;
            pathList.add(directoryItem);
        }
    }


    // 返回父级目录
    public void upDirectory() {
        TreeItem<Item> item = directoryTree.getSelectionModel().getSelectedItem();
        if (item == null || item.getParent() == null || item.getParent().getParent() == null) {
            return;
        } else {
            Item file = item.getParent().getValue();
            directoryTree.getSelectionModel().select(item.getParent());
            pathPointer++;
            pathList.add(item.getParent());
        }
    }

    public boolean stepBackward() {
        if (pathPointer > 0) {
            // 路径指针--
            pathPointer--;
            // 重新设置imageModelList
            TreeItem<Item> item = pathList.get(pathPointer);
            // 重新设置TreeView
            directoryTree.getSelectionModel().select(item);
            return true;
        }
        return false;
    }

    public boolean stepForward() {
        if (pathPointer < pathList.size() - 1) {
            // 路径指针++
            pathPointer++;
            // 重新设置imageModelList
            TreeItem<Item> item = pathList.get(pathPointer);
            // 重新设置TreeView
            directoryTree.getSelectionModel().select(item);
            System.out.println(pathPointer);
            return true;
        }
        return false;
    }
}
