package scau.os.soos.ui.components.base;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

public abstract class AreaSelect {
    protected double edgeTop = -1;
    protected double edgeRight = -1;
    protected double edgeBottom = -1;
    protected double edgeLeft = -1;
    private double startX, startY;
    private Rectangle selectionRectangle; // 选框

    protected void render(Pane pane) {
        selectionRectangle = new Rectangle(0, 0, 0, 0);
        selectionRectangle.setStroke(Paint.valueOf("rgb(0,120,212)")); // 边框颜色
        selectionRectangle.setFill(Paint.valueOf("rgba(0,120,212,0.46)"));
        pane.getChildren().add(selectionRectangle);

        pane.setOnMousePressed((e) -> {
            if (e.getTarget() == pane) {
                startSelection(e);
            }
        });
        pane.setOnMouseDragged((e) -> {
            if (e.getTarget() == pane) {
                updateSelection(e);
            }
        });
        pane.setOnMouseReleased(this::endSelection);
    }

    private void startSelection(MouseEvent event) {
        if (!event.isPrimaryButtonDown()) {
            return;
        }
        startX = event.getX();
        startY = event.getY();

        selectionRectangle.setX(startX);
        selectionRectangle.setY(startY);
        selectionRectangle.setWidth(0);
        selectionRectangle.setHeight(0);
        selectionRectangle.setVisible(true);
    }
    // 更新选框 (适用于 Pane)
    private void updateSelection(MouseEvent event) {
        if (!event.isPrimaryButtonDown()) {
            return;
        }

        double currentX = event.getX();
        double currentY = event.getY();

        // 更新选框大小和位置
        updateSelectionRectangle(currentX, currentY);
    }

    // 更新选框的大小和位置
    private void updateSelectionRectangle(double currentX, double currentY) {
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

        selectionRectangle.setWidth(Math.abs(currentX - startX));
        selectionRectangle.setHeight(Math.abs(currentY - startY));
        selectionRectangle.setX(Math.min(startX, currentX));
        selectionRectangle.setY(Math.min(startY, currentY));
    }

    // 鼠标释放后结束选择
    private void endSelection(MouseEvent event) {
        onSelect(startX, startY, event.getX(), event.getY());
        selectionRectangle.setVisible(false);
    }

    public double[] getSelectionArea() {
        return new double[]{selectionRectangle.getX(), selectionRectangle.getY(), selectionRectangle.getWidth(), selectionRectangle.getHeight()};
    }

    protected abstract void onSelect(double startX, double startY, double endX, double endY);
}

