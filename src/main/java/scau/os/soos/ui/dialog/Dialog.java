package scau.os.soos.ui.dialog;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import scau.os.soos.ui.DesktopManager;
import scau.os.soos.ui.TaskBarManager;
import scau.os.soos.ui.animation.Animation;
import scau.os.soos.ui.animation.Transition;
import scau.os.soos.ui.components.base.Window;

import java.util.function.Consumer;


public class Dialog extends Window {
    private BorderPane content;
    private Window source;
    private Button confirmBtn;
    private Button cancelBtn;
    private Consumer<Boolean> confirmAction;
    private Consumer<Boolean> cancelAction;
    // 拦截窗口点击事件
    private final EventHandler<MouseEvent> sourceEventHandler = event -> {
        event.consume();
        if (event.getEventType().equals(MouseEvent.MOUSE_PRESSED) || event.getEventType().equals(MouseEvent.MOUSE_DRAGGED)) {
            TaskBarManager.getInstance().selectTask(this);
            Transition.playShake(this.getWindow());
        }
    };

    private Dialog(Window source, String title) {
        this(title, "dialog.fxml", 400, 200);
        this.source = source;
    }

    private Dialog(String title, String fxmlName, double width, double height) {
        super(title, fxmlName, width, height, true);
    }

    public void show() {
        source.getWindow().addEventFilter(MouseEvent.ANY, sourceEventHandler);
        TaskBarManager.getInstance().addTask(this);
    }

    @Override
    protected void initialize() {
        content = (BorderPane) body.lookup("#content");
        confirmBtn = (Button) body.lookup("#confirm-btn");
        cancelBtn = (Button) body.lookup("#cancel-btn");
        confirmBtn.setOnAction(e -> onConfirm());
        cancelBtn.setOnAction(e -> onCancel());
    }

    @Override
    protected void close() {
        source.getWindow().removeEventFilter(MouseEvent.ANY, sourceEventHandler);
    }

    public static Dialog getDialog(Window source, String title, Consumer<Boolean> confirmAction, Consumer<Boolean> cancelAction, Node content) {
        Dialog dialog = new Dialog(source, title);
        dialog.setContent(content);
        dialog.confirmAction = confirmAction;
        dialog.cancelAction = cancelAction;
        return dialog;
    }

    private void onConfirm() {
        simulateCloseButtonClick();
        close();
    }

    private void onCancel() {
        simulateCloseButtonClick();
        close();
    }

    private void setContent(Node pane) {
        ((BorderPane) body).setCenter(pane);
    }
}
