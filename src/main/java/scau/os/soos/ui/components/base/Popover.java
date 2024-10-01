package scau.os.soos.ui.components.base;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import scau.os.soos.common.GlobalUI;
import scau.os.soos.ui.animation.Animation;
import scau.os.soos.ui.animation.Transition;

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

    protected void hide() {
        container.setVisible(false);
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

        Transition.playSlideInY(container, Duration.millis(120), isTop ? 16 : -16, 0);
        Transition.playFadeIn(container, Duration.millis(100));

        addOutsideClickListener();
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
