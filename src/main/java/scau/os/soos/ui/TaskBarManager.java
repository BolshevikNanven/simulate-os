package scau.os.soos.ui;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import scau.os.soos.common.GlobalUI;
import scau.os.soos.common.enums.WINDOW_STATES;
import scau.os.soos.ui.components.TaskButton;
import scau.os.soos.ui.components.WindowsMenu;
import scau.os.soos.ui.components.base.Popover;
import scau.os.soos.ui.components.base.Window;

import java.util.ArrayList;
import java.util.List;

public class TaskBarManager {
    private static TaskBarManager instance;
    private List<Window> windowsList;
    private Button windowsButton;
    private Popover windowsMenu;
    private HBox taskArea;
    private BorderPane taskBar;

    public static TaskBarManager getInstance() {
        if (instance == null) {
            instance = new TaskBarManager();
        }
        return instance;
    }

    private TaskBarManager() {
    }

    public void init() {
        windowsButton = (Button) GlobalUI.rootNode.lookup("#windows-btn");
        taskArea = (HBox) GlobalUI.rootNode.lookup("#task-area");
        taskBar = (BorderPane) GlobalUI.rootNode.lookup("#task-bar");

        windowsMenu = new WindowsMenu();
        windowsList = new ArrayList<>();

        addListener();
    }

    public void closeTask(Window window) {
        windowsList.remove(window);

        // 移除任务栏按钮
        for (Node taskButton : taskArea.getChildren()) {
            if (((TaskButton) taskButton).getWindow() == window) {
                taskArea.getChildren().remove(taskButton);
                break;
            }
        }

        DesktopManager.getInstance().removeWindow(window);
    }

    public void addTask(Window window) {
        TaskButton taskButton = new TaskButton(window);
        windowsList.add(window);

        taskArea.getChildren().add(taskButton);
        DesktopManager.getInstance().addWindow(window);

        selectTask(window);
    }

    public void selectTask(Window window) {
        window.setState(WINDOW_STATES.ACTIVE);
        window.getWindow().toFront();
        window.getWindow().getStyleClass().add("active");
        // 设置其他窗口状态,仅运行一个活动窗口
        for (Window win : windowsList) {
            if (win != window && win.getState() == WINDOW_STATES.ACTIVE) {
                win.getWindow().getStyleClass().remove("active");
                win.setState(WINDOW_STATES.HANGUP);
            }
        }

        // 始终保持任务栏在最前
        taskBar.toFront();
    }

    private void addListener() {
        windowsButton.setOnAction(actionEvent -> {
            windowsMenu.render(windowsButton);
        });
    }

}
