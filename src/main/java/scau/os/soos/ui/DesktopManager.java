package scau.os.soos.ui;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import scau.os.soos.common.GlobalUI;
import scau.os.soos.ui.components.DesktopAreaSelect;
import scau.os.soos.ui.components.DesktopMenu;
import scau.os.soos.ui.components.base.AreaSelect;
import scau.os.soos.ui.components.base.Popover;
import scau.os.soos.ui.components.Window;

public class DesktopManager {
    private static DesktopManager instance;
    private Pane desktop;
    private Popover desktopMenu;
    private AreaSelect areaSelect;

    public static DesktopManager getInstance() {
        if (instance == null) {
            instance = new DesktopManager();
        }
        return instance;
    }

    private DesktopManager() {

    }

    public void init() {
        desktop = (Pane) GlobalUI.rootNode.lookup("#desktop");
        desktopMenu = new DesktopMenu();
        areaSelect = new DesktopAreaSelect(desktop);

        addListener();
    }

    public void addWindow(Window window) {
        desktop.getChildren().add(window.getWindow());
    }

    public void removeWindow(Window window) {
        desktop.getChildren().remove(window.getWindow());
    }

    private void addListener() {
        desktop.addEventFilter(MouseEvent.MOUSE_PRESSED, (e) -> {
            if (e.getTarget() == desktop && e.isSecondaryButtonDown()) {
                desktopMenu.render(e);
            }
        });
    }
}
