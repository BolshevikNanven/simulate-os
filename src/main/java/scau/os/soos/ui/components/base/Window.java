package scau.os.soos.ui.components.base;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.util.Duration;
import scau.os.soos.MainApplication;
import scau.os.soos.common.enums.WINDOW_STATES;
import scau.os.soos.ui.DesktopManager;
import scau.os.soos.ui.TaskBarManager;
import scau.os.soos.ui.animation.Animation;
import scau.os.soos.ui.animation.Transition;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public abstract class Window implements Initializable {
    private static final int EDGE_SIZE = 5; // 可拖动边缘的宽度
    protected SimpleStringProperty title = new SimpleStringProperty();
    @FXML
    protected Node body;
    private BorderPane window;
    private Button hideButton;
    private Button scaleButton;
    private Button closeButton;
    private BorderPane topBar;
    private AnchorPane bodyContainer;

    // 响应式窗口状态，为taskButton响应
    private final SimpleObjectProperty<WINDOW_STATES> state = new SimpleObjectProperty<>();

    // 记录鼠标相对于窗口的偏移量
    private final double[] mouseOffset = new double[]{0, 0};
    private final double[] windowPos = new double[]{0, 0};

    // 记录窗口放大缩小相关值
    private boolean isFull = false;
    private double preWidth;
    private double preHeight;
    private double preX;
    private double preY;

    private Window() {
    }

    protected Window(String title, String fxmlName, double width, double height) {
        try {
            window = FXMLLoader.load(MainApplication.class.getResource("components/window.fxml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        hideButton = (Button) window.lookup("#hide-btn");
        scaleButton = (Button) window.lookup("#scale-btn");
        closeButton = (Button) window.lookup("#close-btn");
        topBar = (BorderPane) window.lookup("#top-bar");
        bodyContainer = (AnchorPane) window.lookup("#window-body");

        // 标题绑定
        Label titleLabel = (Label) window.lookup("#window-title");
        this.title.set(title);
        titleLabel.textProperty().bindBidirectional(this.title);

        // 标题栏图标加载
        Pane iconPane = (Pane) window.lookup("#window-icon");
        ImageView icon = getIcon();
        icon.setFitWidth(18);
        icon.setFitHeight(18);
        iconPane.getChildren().add(icon);

        window.setPrefWidth(width);
        window.setPrefHeight(height);

        // 初始化为隐藏
        setState(WINDOW_STATES.HIDE);

        addListener();
        addDragListener();
        addResizeListener();

        loadAndLinkFXML(this, fxmlName);
    }

    private void addListener() {
        //最小化
        hideButton.setOnAction(actionEvent -> {
            setState(WINDOW_STATES.HIDE);
        });

        //双击标题栏窗口化
        topBar.addEventFilter(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            if (mouseEvent.getClickCount() == 2) {
                if (isFull) {
                    zoomOutWindow(-1, -1);
                } else {
                    Rectangle2D screenBounds = Screen.getPrimary().getBounds();
                    zoomInWindow(0, 0, screenBounds.getWidth(), screenBounds.getHeight() - 52);
                }
            }
        });

        //按钮控制窗口化
        scaleButton.setOnAction(actionEvent -> {
            if (isFull) {
                zoomOutWindow(-1, -1);
            } else {
                Rectangle2D screenBounds = Screen.getPrimary().getBounds();
                zoomInWindow(0, 0, screenBounds.getWidth(), screenBounds.getHeight() - 52);
            }
        });

        // 关闭窗口
        closeButton.setOnAction(actionEvent -> {
            setState(WINDOW_STATES.CLOSE);
        });

        // 点击窗口时激活
        window.addEventFilter(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            if (getState() == WINDOW_STATES.HANGUP) {
                TaskBarManager.getInstance().selectTask(this);
            }
        });

        // 监听窗口是否为关闭
        state.addListener((observableValue, windowStates, t1) -> {
            if (t1 == WINDOW_STATES.CLOSE) {
                close();
                TaskBarManager.getInstance().closeTask(this);
            }
        });
    }

    private void addResizeListener() {
        window.setOnMouseMoved(event -> {
            if (isFull) {
                return;
            }
            double mouseX = event.getX();
            double mouseY = event.getY();
            double width = window.getWidth();
            double height = window.getHeight();

            // 判断鼠标是否在窗口的边缘
            if (mouseX < EDGE_SIZE && mouseY < EDGE_SIZE) {
                // 左上角
                window.setCursor(Cursor.NW_RESIZE);
            } else if (mouseX > width - EDGE_SIZE && mouseY < EDGE_SIZE) {
                // 右上角
                window.setCursor(Cursor.NE_RESIZE);
            } else if (mouseX > width - EDGE_SIZE && mouseY > height - EDGE_SIZE) {
                // 右下角
                window.setCursor(Cursor.SE_RESIZE);
            } else if (mouseX < EDGE_SIZE && mouseY > height - EDGE_SIZE) {
                // 左下角
                window.setCursor(Cursor.SW_RESIZE);
            } else if (mouseX < EDGE_SIZE) {
                // 左边缘
                window.setCursor(Cursor.W_RESIZE);
            } else if (mouseX > width - EDGE_SIZE) {
                // 右边缘
                window.setCursor(Cursor.E_RESIZE);
            } else if (mouseY < EDGE_SIZE) {
                // 上边缘
                window.setCursor(Cursor.N_RESIZE);
            } else if (mouseY > height - EDGE_SIZE) {
                // 下边缘
                window.setCursor(Cursor.S_RESIZE);
            } else {
                window.setCursor(Cursor.DEFAULT);
            }
        });

        window.setOnMouseDragged(event -> {
            if (isFull) {
                return;
            }

            double mouseX = event.getX();
            double mouseY = event.getY();
            double width = window.getPrefWidth();
            double height = window.getPrefHeight();
            double minWidth = 200;  // 设置最小宽度
            double minHeight = 150; // 设置最小高度

            if (window.getCursor() == Cursor.W_RESIZE) {
                // 左边缘拖动，调整窗口宽度
                double newWidth = width - (event.getScreenX() - window.getLayoutX());
                System.out.println((event.getScreenX() - window.getLayoutX()));
                if (newWidth >= minWidth) {
                    window.setPrefWidth(newWidth);
                    window.setLayoutX(event.getScreenX());
                }
            } else if (window.getCursor() == Cursor.E_RESIZE) {
                // 右边缘拖动，调整窗口宽度
                double newWidth = mouseX;
                if (newWidth >= minWidth) {
                    window.setPrefWidth(newWidth);
                }
            } else if (window.getCursor() == Cursor.N_RESIZE) {
                // 上边缘拖动，调整窗口高度
                double newHeight = height - (event.getScreenY() - window.getLayoutY());
                if (newHeight >= minHeight) {
                    window.setPrefHeight(newHeight);
                    window.setLayoutY(event.getScreenY());
                }
            } else if (window.getCursor() == Cursor.S_RESIZE) {
                // 下边缘拖动，调整窗口高度
                double newHeight = mouseY;
                if (newHeight >= minHeight) {
                    window.setPrefHeight(newHeight);
                }
            } else if (window.getCursor() == Cursor.NW_RESIZE) {
                // 左上角拖动，调整宽度和高度
                double newWidth = width - (event.getScreenX() - window.getLayoutX());
                double newHeight = height - (event.getScreenY() - window.getLayoutY());
                if (newWidth >= minWidth) {
                    window.setPrefWidth(newWidth);
                    window.setLayoutX(event.getScreenX());
                }
                if (newHeight >= minHeight) {
                    window.setPrefHeight(newHeight);
                    window.setLayoutY(event.getScreenY());
                }
            } else if (window.getCursor() == Cursor.NE_RESIZE) {
                // 右上角拖动，调整宽度和高度
                double newWidth = mouseX;
                double newHeight = height - (event.getScreenY() - window.getLayoutY());
                if (newWidth >= minWidth) {
                    window.setPrefWidth(newWidth);
                }
                if (newHeight >= minHeight) {
                    window.setPrefHeight(newHeight);
                    window.setLayoutY(event.getScreenY());
                }
            } else if (window.getCursor() == Cursor.SW_RESIZE) {
                // 左下角拖动，调整宽度和高度
                double newWidth = width - (event.getScreenX() - window.getLayoutX());
                double newHeight = mouseY;
                if (newWidth >= minWidth) {
                    window.setPrefWidth(newWidth);
                    window.setLayoutX(event.getScreenX());
                }
                if (newHeight >= minHeight) {
                    window.setPrefHeight(newHeight);
                }
            } else if (window.getCursor() == Cursor.SE_RESIZE) {
                // 右下角拖动，调整宽度和高度
                double newWidth = mouseX;
                double newHeight = mouseY;
                if (newWidth >= minWidth) {
                    window.setPrefWidth(newWidth);
                }
                if (newHeight >= minHeight) {
                    window.setPrefHeight(newHeight);
                }
            }
        });
    }

    private void addDragListener() {
        // 当鼠标按下时记录偏移量
        topBar.setOnMousePressed((event) -> {
            // 记录鼠标按下时的偏移量
            mouseOffset[0] = event.getSceneX();
            mouseOffset[1] = event.getSceneY();

            windowPos[0] = window.getLayoutX();
            windowPos[1] = window.getLayoutY();
        });

        // 当拖动时更新窗口的位置
        topBar.setOnMouseDragged((event) -> {
            double x = windowPos[0] + event.getScreenX() - mouseOffset[0];
            double y = windowPos[1] + event.getScreenY() - mouseOffset[1];
            if (isFull) {
                zoomOutWindow(x, y);
                return;
            }
            // 根据鼠标拖动的位置更新窗口位置
            window.setLayoutX(x);
            window.setLayoutY(y);

            AnchorPane indicator = DesktopManager.getInstance().getIndicator();
            Rectangle2D screenBounds = Screen.getPrimary().getBounds();

            // 拖拽至左边缘
            if (event.getScreenX() <= 2) {
                if (indicator.isVisible()) {
                    return;
                }
                indicator.setLayoutX(0);
                indicator.setLayoutY(0);
                indicator.setPrefHeight(screenBounds.getHeight() - 52);
                indicator.setPrefWidth(screenBounds.getWidth() / 2);
                indicator.setVisible(true);

                return;
            }

            // 拖拽至右边缘
            if (event.getScreenX() >= screenBounds.getWidth() - 2) {
                if (indicator.isVisible()) {
                    return;
                }
                indicator.setLayoutX(screenBounds.getWidth() / 2);
                indicator.setLayoutY(0);
                indicator.setPrefHeight(screenBounds.getHeight() - 52);
                indicator.setPrefWidth(screenBounds.getWidth() / 2);
                indicator.setVisible(true);

                return;
            }

            // 拖拽至上边缘
            if (event.getScreenY() <= 2) {
                if (indicator.isVisible()) {
                    return;
                }
                indicator.setLayoutX(0);
                indicator.setLayoutY(0);
                indicator.setPrefHeight(screenBounds.getHeight() - 52);
                indicator.setPrefWidth(screenBounds.getWidth());
                indicator.setVisible(true);

                return;
            }

            indicator.setVisible(false);

        });
        topBar.setOnMouseReleased(event -> {
            AnchorPane indicator = DesktopManager.getInstance().getIndicator();
            if (!indicator.isVisible()) {
                return;
            }

            Rectangle2D screenBounds = Screen.getPrimary().getBounds();
            // 拖拽至左边缘
            if (event.getScreenX() <= 2) {
                zoomInWindow(0, 0, screenBounds.getWidth() / 2, screenBounds.getHeight() - 52);
            }

            // 拖拽至右边缘
            if (event.getScreenX() >= screenBounds.getWidth() - 2) {
                zoomInWindow(screenBounds.getWidth() / 2, 0, screenBounds.getWidth() / 2, screenBounds.getHeight() - 52);
            }

            // 拖拽至上边缘
            if (event.getScreenY() <= 2) {
                zoomInWindow(0, 0, screenBounds.getWidth(), screenBounds.getHeight() - 52);
            }

            indicator.setVisible(false);
        });
    }

    private void zoomInWindow(double x, double y, double width, double height) {
        preWidth = window.getPrefWidth();
        preHeight = window.getPrefHeight();
        preX = window.getLayoutX();
        preY = window.getLayoutY();

        Animation.playWidthIn(window, Duration.millis(80), width);
        Animation.playHeightIn(window, Duration.millis(80), height);

        Animation.playSlideInX(window, Duration.millis(80), x);
        Animation.playSlideInY(window, Duration.millis(80), y);

        window.getStyleClass().add("full-screen");

        if (body != null) {
            body.getStyleClass().remove("window-body");
        }

        isFull = true;
    }

    /**
     * 缩小窗口
     *
     * @param x 如果为负则缩小到放大前位置
     * @param y 如果为负则缩小到放大前位置
     */
    private void zoomOutWindow(double x, double y) {
        Animation.playWidthIn(window, Duration.millis(80), preWidth);
        Animation.playHeightIn(window, Duration.millis(80), preHeight);

        Animation.playSlideInX(window, Duration.millis(80), x < 0 ? preX : x);
        Animation.playSlideInY(window, Duration.millis(80), y < 0 ? preY : y);

        window.getStyleClass().remove("full-screen");

        if (body != null) {
            body.getStyleClass().add("window-body");
        }

        isFull = false;
    }


    private void loadAndLinkFXML(Window controller, String url) {
        if (url.isEmpty()) {
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(controller.getClass().getResource(url));
            loader.setController(controller);
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public WINDOW_STATES getState() {
        return state.get();
    }

    public SimpleObjectProperty<WINDOW_STATES> getStateProperty() {
        return state;
    }

    public void setState(WINDOW_STATES state) {
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

    public ImageView getIcon() {
        ImageView imageView = new ImageView();

        URL iconUrl = this.getClass().getResource("icon.png");

        if (iconUrl == null) {
            iconUrl = this.getClass().getResource("icon.jpg");
        }

        if (iconUrl != null) {
            imageView.setImage(new Image(iconUrl.toExternalForm()));
        }

        return imageView;
    }


    public double[] getWindowArea(){
        Bounds pos = window.localToScreen(window.getBoundsInLocal());
        return new double[]{pos.getMinX(), pos.getMinY(), pos.getWidth(), pos.getHeight()};
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        body.getStyleClass().add("window-body");

        // 渲染窗口内容
        bodyContainer.getChildren().add(body);
        AnchorPane.setLeftAnchor(body, 0.0);
        AnchorPane.setBottomAnchor(body, 0.0);
        AnchorPane.setRightAnchor(body, 0.0);
        AnchorPane.setTopAnchor(body, 0.0);

        initialize();
    }

    protected abstract void initialize();

    protected abstract void close();

}
