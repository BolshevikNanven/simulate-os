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
import scau.os.soos.apps.fileManager.controller.DirectoryTreeController;
import scau.os.soos.apps.fileManager.model.ThumbnailBox;
import scau.os.soos.module.file.model.Directory;
import scau.os.soos.module.file.model.Item;
import scau.os.soos.ui.components.base.Window;

import java.util.ArrayList;
import java.util.List;


public class FileManagerApp extends Window {
    @FXML
    public Pane selectedArea;
    @FXML
    public FlowPane itemContainer;
    @FXML
    public Label itemNumber;
    @FXML
    public Label itemSelectedNumber;

    // 文件列表
    private List<Item> itemList;
    // 选中文件列表
    private List<ThumbnailBox> selectedList;
    // 选中文件数量
    private IntegerProperty selectedCount;
    // 选中文件大小
    private DoubleProperty selectedSize;
    // 文件总数
    private IntegerProperty totalCount;

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

    public List<ThumbnailBox> getSelectedList() {
        return selectedList;
    }

    public FlowPane getItemContainer() {
        return itemContainer;
    }

    @Override
    public void initialize() {
        instance = this;

        selectedCount = new SimpleIntegerProperty(0);
        totalCount = new SimpleIntegerProperty(0);
        selectedSize = new SimpleDoubleProperty(0);
        itemList = new ArrayList<>();
        selectedList = new ArrayList<>();

        addListener();
        initBottomBar();

        new FileAreaSelect(selectedArea);
    }

    @Override
    protected void close() {

    }

    public void addListener() {
        itemContainer.addEventFilter(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.PRIMARY
                    && mouseEvent.getClickCount() == 1
                    && !mouseEvent.isControlDown()){
                Node intersectedNode = mouseEvent.getPickResult().getIntersectedNode();
                if (itemContainer.equals(intersectedNode)) {
                    clearSelectedList();
                }
            }
        });
    }

    public void refreshCurrentDirectory(){
        loadDirectory(DirectoryTreeController.getInstance().getCurrentDirectory());
    }

    public void loadDirectory(Item directory) {
        if (directory == null)
            return ;

        if(!selectedList.isEmpty())
            clearSelectedList();

        itemList = ((Directory)directory).getChildren();
        totalCount.set(itemList.size());

        displayThumbnail();
    }

    public void displayThumbnail() {
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
            return ;
        } else {
            thumbnailBox.getStyleClass().add("thumbnail-box-selected");
            selectedList.add(thumbnailBox);
        }
        selectedCount.set(selectedList.size());
    }

    public void clearSelectedList() {
        for (ThumbnailBox thumbnailBoxModel : selectedList) {
            thumbnailBoxModel.getStyleClass().remove("thumbnail-box-selected");
        }
        selectedList.clear();
        selectedCount.set(0);
    }

    public void showContent(Item item) {
        System.out.println(item);
    }

    private void initBottomBar () {
        itemNumber.textProperty().bind(Bindings.createStringBinding(() -> String.format("%d 个项目",
                                totalCount.get()),totalCount));
        itemSelectedNumber.textProperty().bind(Bindings.createStringBinding(() -> String.format("选中 %d 个项目 ",
                selectedCount.get()),selectedCount));
    }
}
