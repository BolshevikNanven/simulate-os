package scau.os.soos.ui.components;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import scau.os.soos.MainApplication;
import scau.os.soos.common.GlobalUI;
import scau.os.soos.ui.components.base.Popover;

import java.io.IOException;

public class WindowsMenu extends Popover {
    private final Button shutdownBtn;

    public WindowsMenu() {
        this.gap = 8;
        this.isTop = true;
        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("components/windows_menu.fxml"));
        try {
            this.container = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        shutdownBtn = (Button) this.container.lookup("#shutdown-btn");

        addListener();
    }

    private void addListener() {
        shutdownBtn.setOnAction((e) -> {
            GlobalUI.stage.close();
        });
    }
}
