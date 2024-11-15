package scau.os.soos.ui.dialog;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import scau.os.soos.ui.TaskBarManager;
import scau.os.soos.ui.animation.Transition;
import scau.os.soos.ui.components.base.Window;

import java.util.function.Consumer;


public class Dialog extends Window {
    private Window source;
    private Button confirmBtn;
    private Button cancelBtn;
    private boolean isConfirmBtnDown = false;
    private Consumer<Boolean> confirmAction;
    private Consumer<Boolean> cancelAndCloseAction;

    // 拦截窗口点击事件
    private final EventHandler<MouseEvent> sourceMouseEventHandler = event -> {
        event.consume();
        if (event.getEventType().equals(MouseEvent.MOUSE_PRESSED) || event.getEventType().equals(MouseEvent.MOUSE_DRAGGED)) {
            TaskBarManager.getInstance().selectTask(this);
            Transition.playShake(this.getWindow());
        }
    };
    // 拦截键盘点击事件
    private final EventHandler<KeyEvent> sourceKeyEventHandler = event -> {
        event.consume();
        if (event.getEventType().equals(KeyEvent.KEY_PRESSED)) {
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
        source.getWindow().addEventFilter(MouseEvent.ANY, sourceMouseEventHandler);
        source.getWindow().addEventFilter(KeyEvent.ANY, sourceKeyEventHandler);
        TaskBarManager.getInstance().addTask(this);
    }

    @Override
    protected void initialize() {
        confirmBtn = (Button) body.lookup("#confirm-btn");
        cancelBtn = (Button) body.lookup("#cancel-btn");
        confirmBtn.setOnAction(e -> onConfirm());
        cancelBtn.setOnAction(e -> onCancel());
    }

    @Override
    protected void close() {
        if (!isConfirmBtnDown && cancelAndCloseAction != null) {
            cancelAndCloseAction.accept(true); // 传入true表示确认操作
        }
        source.getWindow().removeEventFilter(MouseEvent.ANY, sourceMouseEventHandler);
        source.getWindow().removeEventFilter(KeyEvent.ANY, sourceKeyEventHandler);
    }

    public static Dialog getDialog(Window source, String title, boolean showConfirm, boolean showCancel, Consumer<Boolean> confirmAction, Consumer<Boolean> cancelAndCloseAction, Node content) {
        Dialog dialog = new Dialog(source, title);
        dialog.setContent(content);
        dialog.confirmBtn.setVisible(showConfirm);
        dialog.cancelBtn.setVisible(showCancel);
        dialog.confirmAction = confirmAction;
        dialog.cancelAndCloseAction = cancelAndCloseAction;
        return dialog;
    }

    // 调试窗口
    public static Dialog getEmptyDialog(Window source,String title) {
        return getDialog(source, title, true, false, null, null, null);
    }

    private void onConfirm() {
        if (confirmAction != null) {
            confirmAction.accept(true); // 传入true表示确认操作
            isConfirmBtnDown = true;
        }
        simulateCloseButtonClick();
    }

    private void onCancel() {
        if (cancelAndCloseAction != null) {
            cancelAndCloseAction.accept(true); // 传入true表示确认操作
        }
        simulateCloseButtonClick();
    }

    private void setContent(Node pane) {
        ((BorderPane) body).setCenter(pane);
    }
}
