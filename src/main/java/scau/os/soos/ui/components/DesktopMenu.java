package scau.os.soos.ui.components;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import scau.os.soos.MainApplication;
import scau.os.soos.ui.components.base.Popover;

import java.io.IOException;

public class DesktopMenu extends Popover {
    public DesktopMenu() {
        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("components/desktop_menu.fxml"));
        try {
            this.container = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
