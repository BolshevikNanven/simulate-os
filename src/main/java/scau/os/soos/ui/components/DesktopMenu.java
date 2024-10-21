package scau.os.soos.ui.components;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import scau.os.soos.MainApplication;
import scau.os.soos.ui.components.base.Popover;

import java.io.IOException;

public class DesktopMenu extends Popover {
    public DesktopMenu() {

    }

    @Override
    protected Pane setup() {
        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("components/desktop_menu.fxml"));
        try {
            return loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
