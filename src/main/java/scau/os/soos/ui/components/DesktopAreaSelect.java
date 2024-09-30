package scau.os.soos.ui.components;

import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import scau.os.soos.common.GlobalUI;
import scau.os.soos.ui.components.base.AreaSelect;

public class DesktopAreaSelect extends AreaSelect {
    public DesktopAreaSelect(Pane desktop) {
        this.render(desktop);
        this.edgeBottom = Screen.getPrimary().getBounds().getMaxY() - 52;
    }
}
