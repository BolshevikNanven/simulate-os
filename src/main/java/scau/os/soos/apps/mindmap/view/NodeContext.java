package scau.os.soos.apps.mindmap.view;

import scau.os.soos.apps.mindmap.service.NodeService;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;

public class NodeContext extends ContextMenu {
    private NodeView node;
    private final MenuItem editMenu;
    private final MenuItem deleteMenu;
    private final MenuItem addSubNodeMenu;
    private final MenuItem addBroNodeMenu;
    private final MenuItem applyStyleMenu;

    public NodeContext() {
        editMenu = new MenuItem("", generateMenu("编辑", "双击"));
        deleteMenu = new MenuItem("", generateMenu("删除节点", "Delete"));
        addSubNodeMenu = new MenuItem("", generateMenu("添加子节点", "Tab"));
        addBroNodeMenu = new MenuItem("", generateMenu("添加兄弟节点", "Enter"));
        applyStyleMenu = new MenuItem("", generateMenu("应用样式", ""));

        this.getItems().addAll(editMenu, deleteMenu, addSubNodeMenu, addBroNodeMenu, applyStyleMenu);

        addListener();
    }

    public NodeContext render(NodeView node, double x, double y) {
        this.node = node;
        this.show(node, x, y);
        return this;
    }

    private void addListener() {
        editMenu.setOnAction(actionEvent -> {
            node.focusText();
        });
        deleteMenu.setOnAction(actionEvent -> {
            NodeService.getInstance().deleteNode(node.getNodeEntity());
        });
        addBroNodeMenu.setOnAction(actionEvent -> {
            NodeService.getInstance().addBroNode();
        });
        addSubNodeMenu.setOnAction(actionEvent -> {
            NodeService.getInstance().addSubNode();
        });
        applyStyleMenu.setOnAction(actionEvent -> {
            NodeService.getInstance().applyStyle(node.getNodeEntity());
        });
    }

    private HBox generateMenu(String title, String key) {
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("title");
        titleLabel.setPrefWidth(86);

        Label keyLabel = new Label(key);
        keyLabel.getStyleClass().add("description");

        return new HBox(titleLabel, keyLabel);

    }
}
