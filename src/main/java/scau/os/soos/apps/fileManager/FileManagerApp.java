package scau.os.soos.apps.fileManager;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;

import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
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
    // 文件总大小
    private DoubleProperty totalSize;

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

    @Override
    public void initialize() {
        new FileAreaSelect(selectedArea);

        selectedArea.setVisible(false);

        itemContainer.addEventFilter(MouseEvent.MOUSE_PRESSED,mouseEvent -> {
            selectedArea.setVisible(true);
            selectedArea.fireEvent(mouseEvent);
        });
        itemContainer.addEventFilter(MouseEvent.MOUSE_DRAGGED,mouseEvent -> {
            selectedArea.fireEvent(mouseEvent);
        });
        itemContainer.addEventFilter(MouseEvent.MOUSE_RELEASED,mouseEvent -> {
            selectedArea.fireEvent(mouseEvent);
            selectedArea.setVisible(false);
        });

        selectedCount = new SimpleIntegerProperty(0);
        totalCount = new SimpleIntegerProperty(0);
        selectedSize = new SimpleDoubleProperty(0);
        totalSize = new SimpleDoubleProperty(0);

        // 初始化文件模型列表
        itemList = new ArrayList<>();
        // 初始化选中文件列表
        selectedList = new ArrayList<>();

        instance = this;
    }

    @Override
    protected void close() {

    }

    public void loadDirectory(Item directory) {
        if (directory == null)
            return ;
        if(!selectedList.isEmpty())clearSelectedList();
        fileToItemList(directory);
        displayThumbnail();
    }
    public void fileToItemList(Item directory) {
        // 接受从目录树控制器传来当前目录
        // 如果传入的目录为空，则直接返回
        if (directory == null)
            return;
        if (directory.isDirectory()) {
            // 获取当前目录下所有文件和目录，转换成
            itemList = ((Directory)directory).getChildren();
        }
        // 非法路径，返回false
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

    public void clearSelectedList() {
        if(selectedList.isEmpty())
            return ;

        for (ThumbnailBox thumbnailBoxModel : selectedList) {
            thumbnailBoxModel.getStyleClass().remove("thumbnail-box-selected");
        }
        selectedList.clear();
        selectedCount.set(0);
        //System.out.println("clear");
    }
}
