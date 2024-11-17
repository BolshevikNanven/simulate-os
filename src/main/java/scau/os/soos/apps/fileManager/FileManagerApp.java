package scau.os.soos.apps.fileManager;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;

import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import scau.os.soos.apps.editor.EditorApp;
import scau.os.soos.apps.fileManager.controller.DirectoryTreeController;
import scau.os.soos.apps.fileManager.controller.ToolBarController;
import scau.os.soos.apps.fileManager.model.ThumbnailBox;
import scau.os.soos.common.Clipboard;
import scau.os.soos.module.file.FileController;
import scau.os.soos.module.file.model.Directory;
import scau.os.soos.module.file.model.Exe;
import scau.os.soos.module.file.model.Item;
import scau.os.soos.module.process.ProcessController;
import scau.os.soos.ui.TaskBarManager;
import scau.os.soos.ui.components.base.Window;

import java.util.ArrayList;
import java.util.List;


public class FileManagerApp extends Window {
    @FXML
    public ScrollPane scrollPane;
    @FXML
    public StackPane scrollPaneContent;
    @FXML
    public Pane selectedArea;
    @FXML
    public FlowPane itemContainer;
    @FXML
    public Label itemNumber;
    @FXML
    public Label itemSelectedNumber;

    private FileMenu fileMenu;

    private List<Item> itemList;
    private IntegerProperty itemCount;
    private List<ThumbnailBox> selectedList;
    private IntegerProperty selectedCount;
    private DoubleProperty selectedSize;

    public FileManagerApp() {
        super("文件管理器", "main.fxml", 900, 600);
    }

    private static FileManagerApp instance;

    public static FileManagerApp getInstance() {
        if (instance == null) {
            throw new RuntimeException("FileManagerApp未初始化");
        }
        return instance;
    }

    public IntegerProperty getSelectedCountProperty() {
        return selectedCount;
    }

    public Pane getSelectedArea() {
        return selectedArea;
    }

    public List<ThumbnailBox> getSelectedList() {
        return selectedList;
    }

    public FlowPane getItemContainer() {
        return itemContainer;
    }

    public List<Item> getItemList() {
        return itemList;
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
        itemCount.set(itemList.size());
    }

    public FileMenu getFileMenu() {
        return fileMenu;
    }

    @Override
    public void initialize() {
        instance = this;

        selectedCount = new SimpleIntegerProperty(0);
        itemCount = new SimpleIntegerProperty(0);
        selectedSize = new SimpleDoubleProperty(0);
        itemList = new ArrayList<>();
        selectedList = new ArrayList<>();

        new FileAreaSelect(selectedArea);
        fileMenu = new FileMenu();

        initBinding();
        addListener();
        initBottomBar();

        ToolBarController.getInstance().init();
    }

    private void initBinding() {
        scrollPaneContent.prefHeightProperty().bind(scrollPane.heightProperty().subtract(3.5));
        scrollPaneContent.prefWidthProperty().bind(scrollPane.widthProperty());
    }

    @Override
    protected void close() {
        FileController.getInstance().save();
    }

    public void addListener() {
        scrollPane.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> {
            if (e.isSecondaryButtonDown()) {
                if (!selectedList.isEmpty()) {
                    fileMenu.renderOverItem(e);
                } else {
                    fileMenu.renderOverPane(e, !Clipboard.getInstance().getCopiedItems().isEmpty());
                }
            }
        });
        itemContainer.addEventFilter(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.PRIMARY
                    && mouseEvent.getClickCount() == 1
                    && !mouseEvent.isControlDown()) {
                Node intersectedNode = mouseEvent.getPickResult().getIntersectedNode();
                if (itemContainer.equals(intersectedNode)) {
                    clearSelectedList();
                }
            }
        });
    }

    public void refreshCurrentDirectory() {
        loadDirectory(DirectoryTreeController.getInstance().getCurrentDirectory());
    }

    public void loadDirectory(Item directory) {
        if (directory == null)
            return;

        if (!selectedList.isEmpty())
            clearSelectedList();

        itemList = ((Directory) directory).getChildren();
        itemCount.set(itemList.size());

        displayItem();
    }

    public void displayItem() {
        // 清空选择列表
        clearSelectedList();
        // 清空ThumbnailPane中的所有子节点
        itemContainer.getChildren().clear();
        // 如果imageModelList为空，则直接返回
        if (itemList.isEmpty())
            return;
        // 遍历imageModelList列表，为每个ImageModel创建一个ThumbnailBox，并将其添加到ThumbnailPane中
        // 自适应调整大小，显示图片和标签
        for (Item item : itemList) {
            // 将ThumbnailBox添加到ThumbnailPane中
            itemContainer.getChildren().add(new ThumbnailBox(item));
        }
    }

    public void selectItem(ThumbnailBox thumbnailBox) {
        // 将选中的文件加入到selectedList中
        if (selectedList.contains(thumbnailBox)) {
            return;
        } else {
            thumbnailBox.getStyleClass().add("thumbnail-box-selected");
            selectedList.add(thumbnailBox);
            selectedSize.setValue((selectedSize.getValue() + thumbnailBox.getItem().getSize()));
        }
        selectedCount.set(selectedList.size());
    }

    public void clearSelectedList() {
        for (ThumbnailBox thumbnailBoxModel : selectedList) {
            thumbnailBoxModel.getStyleClass().remove("thumbnail-box-selected");
        }
        selectedList.clear();
        selectedCount.set(0);
        selectedSize.set(0);
    }

    public void showContent(Item item) {
        System.out.println(item);
    }

    private void initBottomBar() {
        itemNumber.textProperty().bind(Bindings.createStringBinding(() -> String.format("%d 个项目",
                itemCount.get()), itemCount));
        itemSelectedNumber.textProperty().bind(Bindings.createStringBinding(() -> String.format("选中 %d 个项目 %s",
                        selectedCount.get(), getFormatSize(selectedSize.get())),
                selectedCount, selectedSize));
    }

    private String getFormatSize(double size) {
        if (size == 0)
            return "";
        if (size < 1024)
            return String.format("\t\t%.0f Byte", size);
        else
            return String.format("\t\t%.0f KB", size / 1024);
    }

    public void run(Item item) {
        if (item instanceof Exe) {
            ProcessController.getInstance().create(item);
        }
    }

    public void open(Item item) {
        if (item instanceof Directory) {
            DirectoryTreeController.getInstance().goToDirectory(item);
        } else {
            TaskBarManager.getInstance().addTask(new EditorApp(item));
        }
    }
}
