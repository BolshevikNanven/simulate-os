package scau.os.soos.apps.mindmap.view;

import scau.os.soos.apps.mindmap.dao.NodeDao;
import scau.os.soos.apps.mindmap.entity.NodeEntity;
import scau.os.soos.apps.mindmap.service.NodeService;
import scau.os.soos.apps.mindmap.store.SystemStore;
import javafx.scene.Cursor;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;

import java.util.concurrent.atomic.AtomicReference;

public class NodeView extends AnchorPane {
    private static final String BASE_CLASS = "node-box";
    private static final String TEXT_CLASS = "node-text";

    private NodeEntity nodeEntity;
    private TextField textField;
    private AnchorPane body;

    private NodeView() {
    }

    public NodeView(NodeEntity nodeEntity) {
        this.nodeEntity = nodeEntity;

        textField = new TextField();
        body = new AnchorPane();

        body.getStyleClass().add(BASE_CLASS);
        body.prefWidthProperty().bind(nodeEntity.widthProperty());
        body.prefHeightProperty().bind(nodeEntity.heightProperty());
        body.backgroundProperty().bind(nodeEntity.backgroundProperty());
        body.borderProperty().bind(nodeEntity.borderProperty());

        nodeEntity.actualWidthProperty().bind(body.widthProperty());
        nodeEntity.actualHeightProperty().bind(body.heightProperty());

        textField.getStyleClass().add(TEXT_CLASS);
        AnchorPane.setTopAnchor(textField, 0.0);
        AnchorPane.setRightAnchor(textField, 0.0);
        AnchorPane.setBottomAnchor(textField, 0.0);
        AnchorPane.setLeftAnchor(textField, 0.0);
        textField.setPrefWidth(-1);
        textField.textProperty().bindBidirectional(nodeEntity.contentProperty());
        textField.alignmentProperty().bind(nodeEntity.alignmentProperty());
        textField.setStyle("-fx-text-fill:#" + nodeEntity.getColor().toString().substring(2));
        textField.fontProperty().bind(nodeEntity.fontProperty());
        textField.disableProperty().bind(nodeEntity.disabledProperty());

        this.getStyleClass().add("node-container");
        AnchorPane.setTopAnchor(body, 0.0);
        AnchorPane.setRightAnchor(body, 0.0);
        AnchorPane.setBottomAnchor(body, 0.0);
        AnchorPane.setLeftAnchor(body, 0.0);
        this.layoutXProperty().bind(nodeEntity.xProperty().subtract(4));
        this.layoutYProperty().bind(nodeEntity.yProperty().subtract(4));

        nodeEntity.fontUnderlineProperty().addListener((e, prev, value) -> {
            if (value) {
                textField.getStyleClass().add("underline");
            } else textField.getStyleClass().remove("underline");
        });

        body.getChildren().add(textField);
        this.getChildren().add(body);
        addListener();
    }

    private void addListener() {
        //定义偏移量
        final double[] mouseAnchor = new double[2];
        AtomicReference<Boolean> isDrag = new AtomicReference<>(false);

        nodeEntity.deleteSymbolProperty().addListener((observableValue, aBoolean, t1) -> {
            if (t1) {
                nodeEntity = null;
                NodeService.getInstance().removeNode(this);
            }
        });
        nodeEntity.selectedSymbolProperty().addListener((observableValue, aBoolean, t1) -> {
            if (t1) {
                this.getStyleClass().add("node-selected");
                body.requestFocus();
            } else {
                this.getStyleClass().remove("node-selected");
                nodeEntity.setDisabled(true);
            }
        });
        nodeEntity.colorProperty().addListener(e -> {
            textField.setStyle("-fx-text-fill:#" + nodeEntity.getColor().toString().substring(2));
        });

        //鼠标点击事件
        body.setOnMouseClicked(e -> {
            NodeService.getInstance().selectNode(nodeEntity);
            if (e.getClickCount() == 2) {
                nodeEntity.setDisabled(false);
                focusText();
            }
            if (e.getButton() == MouseButton.SECONDARY) {
                NodeService.getInstance().showContext(this, e.getScreenX(), e.getScreenY());
            }

            e.consume();
        });

        //键盘事件
        body.setOnKeyPressed(e -> {
            if (e.isControlDown()) {
                return;
            }
            switch (e.getCode()) {
                case ENTER -> NodeService.getInstance().addBroNode();
                case TAB -> NodeService.getInstance().addSubNode();
                case DELETE -> NodeService.getInstance().deleteNode(nodeEntity);
                case ESCAPE -> {
                    if (nodeEntity.getDisabled()) {
                        NodeService.getInstance().selectNode(null);
                    } else {
                        body.requestFocus();
                        nodeEntity.setDisabled(true);
                    }
                    e.consume();
                }
                case UP -> {
                    NodeService.getInstance().selectNodeByKey(KeyCode.UP);
                    e.consume();
                }
                case DOWN -> {
                    NodeService.getInstance().selectNodeByKey(KeyCode.DOWN);
                    e.consume();
                }
                case LEFT -> {
                    NodeService.getInstance().selectNodeByKey(KeyCode.LEFT);
                    e.consume();
                }
                case RIGHT -> {
                    NodeService.getInstance().selectNodeByKey(KeyCode.RIGHT);
                    e.consume();
                }
                default -> focusText();
            }
        });

        //输入框输入事件
        textField.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case TAB -> {
                    NodeService.getInstance().addSubNode();
                    e.consume();
                }
            }
        });

        this.setOnMouseMoved(e -> {
            if (e.getX() > body.getWidth() - 4 && e.getX() < body.getWidth() + 4 && e.getY() > body.getHeight() - 4 && e.getY() < body.getHeight() + 4) {
                this.setCursor(Cursor.SE_RESIZE);
            } else if (e.getX() > body.getWidth() - 4 && e.getX() < body.getWidth() + 4) {
                this.setCursor(Cursor.E_RESIZE);
            } else if (e.getY() > body.getHeight() - 4 && e.getY() < body.getHeight() + 4) {
                this.setCursor(Cursor.S_RESIZE);
            } else {
                this.setCursor(Cursor.DEFAULT);
            }
            e.consume();
        });
        this.setOnMousePressed(e -> {
            SystemStore.getAuxiliaryNode().render(nodeEntity);
            mouseAnchor[0] = e.getSceneX();
            mouseAnchor[1] = e.getSceneY();

            e.consume();
        });
        this.setOnMouseDragged(e -> {
            isDrag.set(true);
            NodeService.getInstance().dragNode(this, mouseAnchor[0], mouseAnchor[1], e);
            e.consume();
        });
        this.setOnMouseReleased(e -> {
            if (isDrag.get()) {
                NodeService.getInstance().dragDoneNode(this, mouseAnchor[0], mouseAnchor[1], e);
            }
            isDrag.set(false);
            e.consume();
        });
    }

    public NodeEntity getNodeEntity() {
        return nodeEntity;
    }

    public void focusText() {
        this.nodeEntity.setDisabled(false);
        this.textField.requestFocus();
    }
}
