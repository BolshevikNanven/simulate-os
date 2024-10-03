package scau.os.soos.apps.mindmap.service.layout;

import scau.os.soos.apps.mindmap.common.enums.Direction;
import scau.os.soos.apps.mindmap.entity.NodeEntity;
import scau.os.soos.apps.mindmap.service.LayoutService;
import javafx.scene.layout.Pane;

public abstract class LayoutParent implements LayoutService {
    protected final Double MARGIN_V = 16.0;
    protected final Pane canvas;
    protected final Pane indicator;

    public LayoutParent(Pane canvas) {
        this.canvas = canvas;
        this.indicator = new Pane();
        this.indicator.setPrefHeight(4);
    }

    protected void showIndicator(NodeEntity node, Direction direction) {
        switch (direction) {
            case TOP -> {
                indicator.setLayoutX(node.getX());
                indicator.setLayoutY(node.getY() - (MARGIN_V + indicator.getHeight()) / 2);
            }
            case RIGHT -> {
                indicator.setLayoutX(node.getX() + node.getActualWidth());
                indicator.setLayoutY(node.getY() + node.getActualHeight() / 2 - 2);
            }
            case BOTTOM -> {
                indicator.setLayoutX(node.getX());
                indicator.setLayoutY(node.getY() + node.getActualHeight() + (MARGIN_V - indicator.getHeight()) / 2);
            }
            case LEFT -> {
                indicator.setLayoutX(node.getX() - indicator.getWidth());
                indicator.setLayoutY(node.getY() + node.getActualHeight() / 2 - 2);
            }
        }

        if (!canvas.getChildren().contains(indicator)){
            indicator.setBackground(node.getBackground());
            indicator.setPrefWidth(node.getActualWidth());

            canvas.getChildren().add(indicator);
        }

    }

    protected void hideIndicator() {
        canvas.getChildren().remove(indicator);
    }
}
