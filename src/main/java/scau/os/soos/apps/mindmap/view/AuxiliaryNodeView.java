package scau.os.soos.apps.mindmap.view;

import scau.os.soos.apps.mindmap.entity.NodeEntity;
import scau.os.soos.apps.mindmap.util.StyleUtil;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

public class AuxiliaryNodeView extends AnchorPane {
    private static final String BASE_CLASS = "node-box";
    private static final String TEXT_CLASS = "node-text";
    private final TextField textField;
    private final Pane canvas;

    public AuxiliaryNodeView(Pane canvas) {
        this.canvas = canvas;
        this.getStyleClass().add(BASE_CLASS);
        this.setVisible(false);

        textField = new TextField();
        textField.getStyleClass().add(TEXT_CLASS);
        AnchorPane.setTopAnchor(textField, 0.0);
        AnchorPane.setRightAnchor(textField, 0.0);
        AnchorPane.setBottomAnchor(textField, 0.0);
        AnchorPane.setLeftAnchor(textField, 0.0);
        textField.setPrefWidth(-1);
        textField.setDisable(true);

        this.getChildren().add(textField);
    }

    public void render(NodeEntity node) {
        this.setPrefWidth(node.getActualWidth());
        this.setPrefHeight(node.getActualHeight());

        this.setBackground(node.getBackground());

        textField.setText(node.getContent());
        textField.setAlignment(node.getAlignment());
        textField.setStyle("-fx-text-fill:#" + node.getColor().toString().substring(2));
        textField.setFont(node.getFont());
        if (node.isFontUnderline()) {
            textField.getStyleClass().add("underline");
        } else textField.getStyleClass().remove("underline");

        canvas.getChildren().remove(this);
        canvas.getChildren().add(this);
    }

    public void move(double x, double y) {
        this.setLayoutX(x);
        this.setLayoutY(y);
        if (!this.isVisible()) {
            this.setVisible(true);
        }
    }

    public void hide() {
        this.setVisible(false);
    }
}
