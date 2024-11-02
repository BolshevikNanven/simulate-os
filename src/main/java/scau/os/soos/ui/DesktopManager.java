package scau.os.soos.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import scau.os.soos.MainApplication;
import scau.os.soos.common.GlobalUI;
import scau.os.soos.ui.components.DesktopAreaSelect;
import scau.os.soos.ui.components.DesktopMenu;
import scau.os.soos.ui.components.base.AreaSelect;
import scau.os.soos.ui.components.base.Popover;
import scau.os.soos.ui.components.base.Window;

import java.io.IOException;

public class DesktopManager {
    private static DesktopManager instance;
    private Pane desktop;
    private Popover desktopMenu;
    private AreaSelect areaSelect;
    private AnchorPane windowIndicator;

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
        try {
            windowIndicator = FXMLLoader.load(MainApplication.class.getResource("components/window_indicator.fxml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        desktop.getChildren().add(windowIndicator);

        addListener();
    }

    public void addWindow(Window window) {
        desktop.getChildren().add(window.getWindow());
    }

    public void removeWindow(Window window) {
        desktop.getChildren().remove(window.getWindow());
    }

    public AnchorPane getIndicator() {
        return windowIndicator;
    }

    private void addListener() {
        desktop.addEventFilter(MouseEvent.MOUSE_PRESSED, (e) -> {
            if (e.getTarget() == desktop && e.isSecondaryButtonDown()) {
                desktopMenu.render(e);
            }
        });
    }


}
