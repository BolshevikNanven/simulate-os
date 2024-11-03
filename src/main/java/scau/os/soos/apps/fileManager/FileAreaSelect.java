package scau.os.soos.apps.fileManager;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import scau.os.soos.apps.fileManager.model.ThumbnailBox;
import scau.os.soos.ui.components.base.AreaSelect;

public class FileAreaSelect extends AreaSelect {
    public Pane selectedArea;
    public FlowPane itemContainer;

    public FileAreaSelect(Pane pane){
        this.render(pane);
        init();
    }

    public void init(){
        this.itemContainer = FileManagerApp.getInstance().itemContainer;
        this.selectedArea = FileManagerApp.getInstance().selectedArea;

        selectedArea.setVisible(false);
        selectedArea.toFront();

        itemContainer.addEventFilter(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            selectedArea.setVisible(true);
            selectedArea.fireEvent(mouseEvent);
        });

        itemContainer.addEventFilter(MouseEvent.MOUSE_DRAGGED,mouseEvent ->{
            double[] area = getSelectionArea();
            for (Node node : itemContainer.getChildren()) {
                if (node instanceof ThumbnailBox &&
                        node.getBoundsInParent().intersects(area[0], area[1], area[2], area[3])) {
                    FileManagerApp.getInstance().selectItem((ThumbnailBox) node);
                }
            }
            selectedArea.fireEvent(mouseEvent);
        });

        itemContainer.addEventFilter(MouseEvent.MOUSE_RELEASED,mouseEvent -> {
            selectedArea.fireEvent(mouseEvent);
            selectedArea.setVisible(false);
        });
    }

    @Override
    protected void onSelect(double startX, double startY, double endX, double endY) {

    }
}
