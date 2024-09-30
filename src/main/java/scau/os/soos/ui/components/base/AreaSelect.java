package scau.os.soos.ui.components.base;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import scau.os.soos.common.model.Handler;

public class AreaSelect {
    protected double edgeTop = -1;
    protected double edgeRight = -1;
    protected double edgeBottom = -1;
    protected double edgeLeft = -1;
    private double startX, startY;
    private Rectangle selectionRectangle; // 选框
    private Handler selectedHandler;

    protected void render(Pane pane) {
        // 初始化选框
        selectionRectangle = new Rectangle(0, 0, 0, 0);
        selectionRectangle.setStroke(Paint.valueOf("rgb(0,120,212)")); // 边框颜色
        selectionRectangle.setFill(Paint.valueOf("rgba(0,120,212,0.46)"));
        pane.getChildren().add(selectionRectangle);

        // 监听鼠标按下事件，开始绘制选框
        pane.setOnMousePressed(this::startSelection);

        // 监听鼠标拖动事件，更新选框的大小
        pane.setOnMouseDragged(this::updateSelection);

        // 监听鼠标释放事件，结束绘制选框
        pane.setOnMouseReleased(event -> endSelection());

    }

    private void startSelection(MouseEvent event) {
        if (!event.isPrimaryButtonDown()) {
            return;
        }
        // 记录鼠标按下的起始坐标
        startX = event.getX();
        startY = event.getY();

        // 设置选框初始位置和大小
        selectionRectangle.setX(startX);
        selectionRectangle.setY(startY);
        selectionRectangle.setWidth(0);
        selectionRectangle.setHeight(0);
        selectionRectangle.setVisible(true);
    }

    private void updateSelection(MouseEvent event) {
        if (!event.isPrimaryButtonDown()) {
            return;
        }
        // 计算当前鼠标位置和起始坐标之间的距离来动态调整选框的大小和位置
        double currentX = event.getX();
        double currentY = event.getY();

        // 根据边界条件调整 currentX 和 currentY
        if (edgeLeft >= 0) {
            currentX = Math.max(currentX, edgeLeft); // 限制左边界
        }
        if (edgeRight >= 0) {
            currentX = Math.min(currentX, edgeRight); // 限制右边界
        }
        if (edgeTop >= 0) {
            currentY = Math.max(currentY, edgeTop); // 限制上边界
        }
        if (edgeBottom >= 0) {
            currentY = Math.min(currentY, edgeBottom); // 限制下边界
        }

        // 设置选框的宽度和高度
        selectionRectangle.setWidth(Math.abs(currentX - startX));
        selectionRectangle.setHeight(Math.abs(currentY - startY));

        // 调整选框的X和Y坐标以确保选框始终从左上角开始绘制
        selectionRectangle.setX(Math.min(startX, currentX));
        selectionRectangle.setY(Math.min(startY, currentY));
    }

    private void endSelection() {
        if (selectedHandler != null) {
            selectedHandler.handle();
        }
        // 隐藏选框
        selectionRectangle.setVisible(false);
    }
}
