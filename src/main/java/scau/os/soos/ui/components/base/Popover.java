package scau.os.soos.ui.components.base;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import scau.os.soos.common.GlobalUI;

public class Popover {
    protected Pane container;
    protected boolean isTop = false;
    protected int gap = 0;
    private boolean rendered = false;

    public void render(Node target) {
        double left = target.localToScreen(target.getBoundsInLocal()).getMinX();
        double top = target.localToScreen(target.getBoundsInLocal()).getMinY();

        if (!isTop) {
            top = target.localToScreen(target.getBoundsInLocal()).getMaxY();
        }

        render(top, left);
    }

    public void render(MouseEvent mouseEvent) {
        double top = mouseEvent.getScreenY();
        double left = mouseEvent.getScreenX();

        render(top, left);
    }

    private void render(double top, double left) {
        // 首次渲染先插入
        if (!rendered) {
            container.setVisible(false);
            container.setFocusTraversable(true);
            GlobalUI.rootNode.getChildren().add(container);
            rendered = true;
        }

        container.setVisible(true);

        // 设置menu位置
        if (isTop) {
            container.setLayoutY(top - container.getPrefHeight() - gap);
        } else {
            container.setLayoutY(top + gap);
        }
        container.setLayoutX(left);

        playSlideInAnimation();
        addOutsideClickListener();
    }

    private void playSlideInAnimation() {
        // 滑入动画
        TranslateTransition slideIn = new TranslateTransition(Duration.millis(120), container);
        slideIn.setFromY(isTop ? 16 : -16);
        slideIn.setToY(0);

        // 淡入动画
        FadeTransition fadeIn = new FadeTransition(Duration.millis(100), container);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        fadeIn.play();
        slideIn.play();
    }

    private void addOutsideClickListener() {
        GlobalUI.scene.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                // 如果点击的不是Popover内部，则隐藏Popover
                if (!container.isHover()) {
                    container.setVisible(false);
                    GlobalUI.scene.removeEventFilter(MouseEvent.MOUSE_PRESSED, this);
                }
            }
        });
    }
}
