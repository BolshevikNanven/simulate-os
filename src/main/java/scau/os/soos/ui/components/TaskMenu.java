package scau.os.soos.ui.components;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import scau.os.soos.MainApplication;
import scau.os.soos.common.enums.WINDOW_STATES;
import scau.os.soos.ui.TaskBarManager;
import scau.os.soos.ui.components.base.Popover;
import scau.os.soos.ui.components.base.Window;

import java.io.IOException;

public class TaskMenu extends Popover {
    private final Button closeButton;
    private final Window window;

    public TaskMenu(Window window) {
        this.gap = 8;
        this.isTop = true;
        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("components/task_menu.fxml"));
        try {
            this.container = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.window = window;
        closeButton = (Button) this.container.lookup("#task-menu-close-btn");

        addListener();
    }

    private void addListener() {
        closeButton.setOnAction(actionEvent -> {
            window.setState(WINDOW_STATES.CLOSE);
            hide();
        });
    }
}
