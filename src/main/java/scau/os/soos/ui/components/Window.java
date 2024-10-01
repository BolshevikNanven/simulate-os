package scau.os.soos.ui.components;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.util.Duration;
import scau.os.soos.MainApplication;
import scau.os.soos.common.enums.WINDOW_STATES;
import scau.os.soos.ui.TaskBarManager;
import scau.os.soos.ui.animation.Animation;
import scau.os.soos.ui.animation.Transition;

import java.io.IOException;

public class Window {
    protected SimpleStringProperty title = new SimpleStringProperty();
    protected Node body;
    private String iconUrl;
    private BorderPane window;
    private Button hideButton;
    private Button scaleButton;
    private Button closeButton;
    private BorderPane topBar;
    private AnchorPane bodyContainer;
    // 响应式状态
    private final SimpleObjectProperty<WINDOW_STATES> state = new SimpleObjectProperty<>();

    // 记录鼠标相对于窗口的偏移量
    private final double[] mouseOffset = new double[]{0, 0};
    private final double[] windowPos = new double[]{0, 0};
    private boolean isFull = false;
    private double preWidth;
    private double preHeight;
    private double preX;
    private double preY;

    private Window() {

    }

    public Window(String title, String iconUrl, Node content) {
        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("components/window.fxml"));
        try {
            window = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        hideButton = (Button) window.lookup("#hide-btn");
        scaleButton = (Button) window.lookup("#scale-btn");
        closeButton = (Button) window.lookup("#close-btn");
        topBar = (BorderPane) window.lookup("#top-bar");
        bodyContainer = (AnchorPane) window.lookup("#window-body");

        this.body = content;
        this.iconUrl = iconUrl;

        // 标题绑定
        Label titleLabel = (Label) window.lookup("#window-title");
        this.title.set(title);
        titleLabel.textProperty().bindBidirectional(this.title);

        // 渲染窗口内容
        bodyContainer.getChildren().add(content);
        AnchorPane.setLeftAnchor(content, 0.0);
        AnchorPane.setBottomAnchor(content, 0.0);
        AnchorPane.setRightAnchor(content, 0.0);
        AnchorPane.setTopAnchor(content, 0.0);

        // 初始化为隐藏
        setStates(WINDOW_STATES.HIDE);

        addListener();
    }

    private void addListener() {
        // 当鼠标按下时记录偏移量
        topBar.setOnMousePressed((MouseEvent event) -> {
            if (isFull) {
                return;
            }
            // 记录鼠标按下时的偏移量
            mouseOffset[0] = event.getSceneX();
            mouseOffset[1] = event.getSceneY();

            windowPos[0] = window.getLayoutX();
            windowPos[1] = window.getLayoutY();
        });

        // 当拖动时更新窗口的位置
        topBar.setOnMouseDragged((MouseEvent event) -> {
            if (isFull) {
                return;
            }
            // 根据鼠标拖动的位置更新窗口位置
            window.setLayoutX(windowPos[0] + event.getScreenX() - mouseOffset[0]);
            window.setLayoutY(windowPos[1] + event.getScreenY() - mouseOffset[1]);
        });

        //最小化
        hideButton.setOnAction(actionEvent -> {
            setStates(WINDOW_STATES.HIDE);
        });

        //窗口化
        scaleButton.setOnAction(actionEvent -> {
            if (isFull) {
                Animation.playWidthIn(window, Duration.millis(80), preWidth);
                Animation.playHeightIn(window, Duration.millis(80), preHeight);

                Animation.playSlideInX(window, Duration.millis(80), preX);
                Animation.playSlideInY(window, Duration.millis(80), preY);

                window.getStyleClass().remove("full-screen");
                isFull = false;
            } else {
                Rectangle2D screenBounds = Screen.getPrimary().getBounds();
                preWidth = window.getPrefWidth();
                preHeight = window.getPrefHeight();
                preX = window.getLayoutX();
                preY = window.getLayoutY();

                Animation.playWidthIn(window, Duration.millis(80), screenBounds.getWidth());
                Animation.playHeightIn(window, Duration.millis(80), screenBounds.getHeight() - 52);

                Animation.playSlideInX(window, Duration.millis(80), 0);
                Animation.playSlideInY(window, Duration.millis(80), 0);

                window.getStyleClass().add("full-screen");
                isFull = true;
            }
        });

        // 关闭窗口
        closeButton.setOnAction(actionEvent -> {
            TaskBarManager.getInstance().closeTask(this);
        });

        // 点击窗口时激活
        window.addEventFilter(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            if (getState() == WINDOW_STATES.HANGUP) {
                TaskBarManager.getInstance().selectTask(this);
            }
        });
    }

    public WINDOW_STATES getState() {
        return state.get();
    }

    public SimpleObjectProperty<WINDOW_STATES> getStateProperty() {
        return state;
    }

    public void setStates(WINDOW_STATES state) {
        if (state == WINDOW_STATES.HIDE) {
            window.setVisible(false);
        } else {
            window.setVisible(true);
            if (getState() == WINDOW_STATES.HIDE) {
                Transition.playFadeIn(window, Duration.millis(60));
                Transition.playZoomIn(window, Duration.millis(60));
            }
        }
        this.state.set(state);
    }

    public BorderPane getWindow() {
        return window;
    }

    public String getIconUrl() {
        return iconUrl;
    }
}
