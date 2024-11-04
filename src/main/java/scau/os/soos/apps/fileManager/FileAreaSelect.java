package scau.os.soos.apps.fileManager;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import scau.os.soos.apps.fileManager.model.ThumbnailBox;
import scau.os.soos.ui.components.base.AreaSelect;

public class FileAreaSelect extends AreaSelect {
    public ScrollPane scrollPane;
    public StackPane scrollPaneContent;
    public Pane selectedArea;
    public FlowPane itemContainer;

    public FileAreaSelect(Pane pane) {
        this.render(pane);
        init();
    }

    public void init() {
        // 从 FileManagerApp 单例中获取 itemContainer 和 selectedArea 实例
        this.scrollPane = FileManagerApp.getInstance().scrollPane;
        this.scrollPaneContent = FileManagerApp.getInstance().scrollPaneContent;
        this.itemContainer = FileManagerApp.getInstance().itemContainer;
        this.selectedArea = FileManagerApp.getInstance().selectedArea;


        // 隐藏 selectedArea，并将其置于顶层
        selectedArea.setVisible(false);
        selectedArea.toFront();

        // 当鼠标按下时，显示 selectedArea 并传递 mouseEvent 事件
        itemContainer.addEventFilter(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            selectedArea.setVisible(true);
            selectedArea.fireEvent(mouseEvent);
        });

        // 当鼠标拖动时
        itemContainer.addEventFilter(MouseEvent.MOUSE_DRAGGED, this::setOnMouseDragged);

        // 当鼠标释放时，隐藏 selectedArea 并传递 mouseEvent 事件
        itemContainer.addEventFilter(MouseEvent.MOUSE_RELEASED, mouseEvent -> {
            selectedArea.fireEvent(mouseEvent);
            selectedArea.setVisible(false);
        });
    }

    @Override
    protected void onSelect(double startX, double startY, double endX, double endY) {

    }

    private void setOnMouseDragged(MouseEvent mouseEvent){
        double mouseY = mouseEvent.getSceneY();
        double viewportMaxY = scrollPane.getBoundsInParent().getMaxY();
        double viewportMinY = scrollPane.getBoundsInParent().getMinY();
        double speed = 1 / (itemContainer.getHeight() / 10);
        System.out.println(scrollPane.getBoundsInParent());
        if (mouseY <= viewportMinY) { // 接近顶部时向上滚动
            scrollPane.setVvalue(Math.max(scrollPane.getVvalue() - speed, 0));
        } else if (mouseY >= viewportMaxY) { // 接近底部时向下滚动
            scrollPane.setVvalue(Math.min(scrollPane.getVvalue() + speed, 1));
        }

        // 清除当前选中的列表
        FileManagerApp.getInstance().clearSelectedList();
        // 获取选择区域
        double[] area = getSelectionArea();
        // 遍历 itemContainer 中的所有子节点
        for (Node node : itemContainer.getChildren()) {
            // 如果节点是 ThumbnailBox 类型，并且其边界与选择区域相交
            if (node instanceof ThumbnailBox &&
                    node.getBoundsInParent().intersects(area[0], area[1], area[2], area[3])) {
                // 选中该 ThumbnailBox 节点
                FileManagerApp.getInstance().selectItem((ThumbnailBox) node);
            }
        }
        selectedArea.fireEvent(mouseEvent);
    }
}
