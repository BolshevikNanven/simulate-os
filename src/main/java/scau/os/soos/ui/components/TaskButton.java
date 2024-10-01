package scau.os.soos.ui.components;

import javafx.scene.control.Button;
import javafx.scene.layout.Region;
import scau.os.soos.common.enums.WINDOW_STATES;
import scau.os.soos.ui.TaskBarManager;

public class TaskButton extends Button {
    private Window window;

    private TaskButton() {
    }

    public TaskButton(Window window) {
        super("", newRegion());
        this.getStyleClass().add("task-btn");

        this.window = window;

        addListener();
    }

    public Window getWindow() {
        return window;
    }

    private void addListener() {
        // 响应窗口状态来改变样式
        this.window.getStateProperty().addListener((observableValue, windowStates, t1) -> {
            if (t1 == WINDOW_STATES.ACTIVE) {
                this.getStyleClass().add("active");
            } else {
                this.getStyleClass().remove("active");
            }
        });
        this.setOnAction(actionEvent -> {
            if (window.getState() == WINDOW_STATES.ACTIVE) {
                window.setStates(WINDOW_STATES.HIDE);
            } else {
                TaskBarManager.getInstance().selectTask(window);
            }
        });
    }

    private static Region newRegion() {
        Region region = new Region();
        region.getStyleClass().add("cmd-icon");

        return region;
    }
}
