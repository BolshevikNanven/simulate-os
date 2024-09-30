package scau.os.soos.ui;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import scau.os.soos.common.GlobalUI;
import scau.os.soos.ui.components.DesktopMenu;

public class DesktopManager {
    private static DesktopManager instance;
    private Pane desktop;
    private DesktopMenu desktopMenu;

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

        addListener();
    }

    private void addListener() {
        desktop.addEventFilter(MouseEvent.MOUSE_PRESSED, (e) -> {
            // 右键点击
            if (e.isSecondaryButtonDown()){
                desktopMenu.render(e);
            }
        });
    }
}
