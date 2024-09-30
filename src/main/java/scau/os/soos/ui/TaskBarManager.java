package scau.os.soos.ui;

import javafx.scene.control.Button;
import scau.os.soos.common.GlobalUI;
import scau.os.soos.ui.components.WindowsMenu;

public class TaskBarManager {
    private static TaskBarManager instance;

    private Button windowsButton;
    private WindowsMenu windowsMenu;

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
        windowsMenu = new WindowsMenu();

        addListener();
    }

    private void addListener() {
        windowsButton.setOnAction(actionEvent -> {
            windowsMenu.render(windowsButton);
        });
    }
}
