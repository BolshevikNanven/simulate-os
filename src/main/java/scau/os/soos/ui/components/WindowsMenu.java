package scau.os.soos.ui.components;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.WindowEvent;
import scau.os.soos.MainApplication;
import scau.os.soos.apps.fileManager.FileManagerApp;
import scau.os.soos.apps.mindmap.MindMapApp;
import scau.os.soos.apps.taskManager.TaskManagerApp;
import scau.os.soos.apps.terminal.TerminalApp;
import scau.os.soos.common.GlobalUI;
import scau.os.soos.common.OS;
import scau.os.soos.ui.TaskBarManager;
import scau.os.soos.ui.components.base.Popover;

import java.io.IOException;

public class WindowsMenu extends Popover {
    private final Button shutdownBtn;
    private final Button cmdBtn;
    private final Button fileManagerBtn;
    private final Button taskManagerBtn;
    private final Button mindMapBtn;

    public WindowsMenu() {
        this.gap = 8;
        this.isTop = true;

        shutdownBtn = (Button) this.container.lookup("#shutdown-btn");
        cmdBtn = (Button) this.container.lookup("#windows-cmd-btn");
        fileManagerBtn = (Button) this.container.lookup("#windows-file-manager-btn");
        mindMapBtn = (Button) this.container.lookup("#windows-mind-map-btn");
        taskManagerBtn = (Button) this.container.lookup("#windows-task-manager-btn");

        addListener();
    }

    private void addListener() {
        shutdownBtn.setOnAction((e) -> {
            // 通过模拟事件来关闭，可以触发stage onClose事件
            GlobalUI.stage.fireEvent(new WindowEvent(GlobalUI.stage, WindowEvent.WINDOW_CLOSE_REQUEST));
        });
        cmdBtn.setOnAction(actionEvent -> {
            TaskBarManager.getInstance().addTask(new TerminalApp());
            hide();
        });
        fileManagerBtn.setOnAction(actionEvent -> {
            TaskBarManager.getInstance().addTask(new FileManagerApp());
            hide();
        });
        taskManagerBtn.setOnAction(actionEvent -> {
            TaskBarManager.getInstance().addTask(new TaskManagerApp());
            hide();
        });
        mindMapBtn.setOnAction(actionEvent -> {
            TaskBarManager.getInstance().addTask(new MindMapApp());
            hide();
        });
    }

    @Override
    protected Pane setup() {
        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("components/windows_menu.fxml"));
        try {
            return loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
