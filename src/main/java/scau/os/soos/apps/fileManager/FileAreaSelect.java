package scau.os.soos.apps.fileManager;

import javafx.scene.layout.Pane;
import scau.os.soos.ui.components.base.AreaSelect;

public class FileAreaSelect extends AreaSelect {
    public FileAreaSelect(Pane pane){
        this.render(pane);
    }
    @Override
    protected void onSelect(double startX, double startY, double endX, double endY) {

    }
}
