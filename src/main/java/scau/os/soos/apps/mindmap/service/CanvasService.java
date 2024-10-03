package scau.os.soos.apps.mindmap.service;

import scau.os.soos.apps.mindmap.entity.NodeEntity;
import scau.os.soos.apps.mindmap.store.SystemStore;
import scau.os.soos.apps.mindmap.util.AlgorithmUtil;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;

public class CanvasService {
    private static CanvasService instance;
    private ScrollPane canvasContainer;
    private Pane canvas;
    private double prevHeight;
    private double prevWidth;
    private boolean prevNew = false;

    private CanvasService() {
    }

    private CanvasService(ScrollPane canvasContainer, Pane canvas) {
        this.canvasContainer = canvasContainer;
        this.canvas = canvas;

        this.canvas.setPrefWidth(this.canvasContainer.getViewportBounds().getWidth());
        this.canvas.setPrefHeight(this.canvasContainer.getViewportBounds().getHeight());

        addListener();
    }

    public static void init(ScrollPane canvasContainer, Pane canvas) {
        if (instance == null) {
            instance = new CanvasService(canvasContainer, canvas);
        }
    }

    public static CanvasService getInstance() {
        return instance;
    }

    private void addListener() {
        final double[] mouseAnchor = new double[2];
        //监听缩放尺度
        SystemStore.canvasScaleProperty().addListener(((observableValue, number, t1) -> {
            canvas.setScaleX(t1.doubleValue() / 100);
            canvas.setScaleY(t1.doubleValue() / 100);
            if (!prevNew) {
                prevWidth = canvas.getWidth();
                prevHeight = canvas.getHeight();
                prevNew = true;
            }
            canvas.setPrefWidth(prevWidth * (t1.doubleValue() / 100));
            canvas.setPrefHeight(prevHeight * (t1.doubleValue() / 100));
        }));
        //控制画布大小为容器大小
        canvasContainer.widthProperty().addListener((observableValue, bounds, t1) -> {
            canvas.setPrefWidth((Double) t1);
        });
        canvasContainer.heightProperty().addListener((observableValue, bounds, t1) -> {
            canvas.setPrefHeight((Double) t1);
        });
        canvas.setOnMousePressed(e -> {
            canvas.setCursor(Cursor.CLOSED_HAND);
            mouseAnchor[0] = e.getSceneX();
            mouseAnchor[1] = e.getSceneY();
        });
        canvas.setOnMouseDragged(e -> {
            Point2D prevCoords = canvas.sceneToLocal(mouseAnchor[0], mouseAnchor[1]);
            Point2D localCoords = canvas.sceneToLocal(e.getSceneX(), e.getSceneY());
            double dx = localCoords.getX() - prevCoords.getX();
            double dy = localCoords.getY() - prevCoords.getY();
            double hValue = canvasContainer.getHvalue();
            double vValue = canvasContainer.getVvalue();

            //28为滚动条宽/高
            double xOverflow = canvas.getWidth() - canvas.getPrefWidth() + 28;
            double yOverflow = canvas.getHeight() - canvas.getPrefHeight() + 28;

            boolean xScroll = dx < 0 && hValue < 1 || dx > 0 && hValue > 0;
            boolean yScroll = dy < 0 && vValue < 1 || dy > 0 && vValue > 0;

            //滚动条滚动
            if (xScroll) {
                canvasContainer.setHvalue(hValue - dx / xOverflow);
            }
            if (yScroll) {
                canvasContainer.setVvalue(vValue - dy / yOverflow);
            }

            for (NodeEntity root : SystemStore.getRootNodeList()) {
                AlgorithmUtil.headMapNode(root, (parent, node) -> {
                    if (!xScroll) node.setX(node.getX() + dx);
                    if (!yScroll) node.setY(node.getY() + dy);
                });
            }
            mouseAnchor[0] = e.getSceneX();
            mouseAnchor[1] = e.getSceneY();
            e.consume();
        });
        canvas.setOnMouseReleased(e -> {
            canvas.setCursor(Cursor.DEFAULT);
            resize();
        });
    }

    public void scale(int value) {
        SystemStore.setCanvasScale(value);
    }

    public void resize() {
        double[] bounds = new double[]{0.0, 0.0, 0.0, 0.0};//上右下左

        //获取节点占位边界
        for (NodeEntity root : SystemStore.getRootNodeList()) {
            AlgorithmUtil.headMapNode(root, (parent, node) -> {
                if (node.getX() < bounds[3]) {
                    bounds[3] = node.getX();
                }
                if (node.getY() < bounds[0]) {
                    bounds[0] = node.getY();
                }
                if (node.getX() + node.getActualWidth() > bounds[1]) {
                    bounds[1] = node.getX() + node.getActualWidth();
                }
                if (node.getY() + node.getActualHeight() > bounds[2]) {
                    bounds[2] = node.getY() + node.getActualHeight();
                }
            });
        }

        if (bounds[0] < 0) {
            //超出上边界
            for (NodeEntity root : SystemStore.getRootNodeList()) {
                AlgorithmUtil.headMapNode(root, (parent, node) -> {
                    node.setY(node.getY() - bounds[0]);
                });
            }
            double height = canvas.getHeight() - bounds[0];
            canvas.setMinHeight(height);

            //强制更新
            canvasContainer.layout();
            canvasContainer.setVvalue((-bounds[0]) / (height - canvas.getPrefHeight()));
        }
        if (bounds[3] < 0) {
            //超出左边界
            for (NodeEntity root : SystemStore.getRootNodeList()) {
                AlgorithmUtil.headMapNode(root, (parent, node) -> {
                    node.setX(node.getX() - bounds[3]);
                });
            }
            double width = canvas.getWidth() - bounds[3];
            canvas.setMinWidth(width);

            //强制更新
            canvasContainer.layout();
            canvasContainer.setHvalue((-bounds[3]) / (width - canvas.getPrefWidth()));
        }
        if (bounds[1] > canvas.getWidth() || bounds[2] > canvas.getHeight()) {
            canvas.setMinWidth(bounds[1] + 36);
            canvas.setMinHeight(bounds[2] + 36);
        }

    }

    public double getCenterX() {
        double viewportWidth = canvasContainer.getViewportBounds().getWidth();

        double hValue = canvasContainer.getHvalue();

        double contentWidth = canvas.getBoundsInLocal().getWidth();

        double viewportTopLeftX = (contentWidth - viewportWidth) * hValue;

        return viewportTopLeftX + (viewportWidth / 2);

    }
    public double getCenterY(){
        double viewportHeight = canvasContainer.getViewportBounds().getHeight();

        double vValue = canvasContainer.getVvalue();

        double contentHeight = canvas.getBoundsInLocal().getHeight();

        double viewportTopLeftY = (contentHeight - viewportHeight) * vValue;

        return viewportTopLeftY + (viewportHeight / 2);
    }
}
