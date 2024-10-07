package scau.os.soos.apps.mindmap.service;

import scau.os.soos.apps.mindmap.controller.SidebarController;
import scau.os.soos.apps.mindmap.dao.NodeDao;
import scau.os.soos.apps.mindmap.entity.NodeEntity;
import scau.os.soos.apps.mindmap.service.layout.LayoutFactory;
import scau.os.soos.apps.mindmap.store.SettingStore;
import scau.os.soos.apps.mindmap.store.SystemStore;
import scau.os.soos.apps.mindmap.util.AlgorithmUtil;
import scau.os.soos.apps.mindmap.view.AuxiliaryNodeView;
import scau.os.soos.apps.mindmap.view.NodeContext;
import scau.os.soos.apps.mindmap.view.NodeView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class NodeService {
    private static NodeService instance;
    private Pane canvas;
    private ScrollPane container;

    private NodeService() {

    }

    private NodeService(ScrollPane container, Pane canvas) {
        this.canvas = canvas;
        this.container = container;
        canvas.setFocusTraversable(true);
        canvas.setOnMouseClicked(e -> {
            canvas.requestFocus();
            if (e.getButton() == MouseButton.PRIMARY) {
                selectNode(null);
            }
        });
        SystemStore.setAuxiliaryNode(new AuxiliaryNodeView(canvas));
    }

    public static NodeService getInstance() {
        return instance;
    }

    public static void init(ScrollPane container, Pane canvas) {
        if (null == instance) {
            instance = new NodeService(container, canvas);
        }
    }


    private void addNode(NodeEntity parent, NodeEntity brother) {
        NodeEntity newNode;
        if (parent == null) {
            newNode = NodeDao.newNode();
        } else if (brother == null) {
            newNode = NodeDao.newNode(parent);
        } else {
            newNode = NodeDao.newNode(parent, brother);
        }

    }

    public void addSubNode() {
        NodeEntity selectedNode = SystemStore.getSelectedNode();
        if (selectedNode == null) {
            addNode(null, null);
        } else {
            addNode(selectedNode, null);
        }
    }

    public void addBroNode() {
        NodeEntity selectedNode = SystemStore.getSelectedNode();
        if (selectedNode == null) {
            addNode(null, null);
        } else {
            addNode(selectedNode.getParent(), selectedNode);
        }
    }

    public void showContext(NodeView nodeView, double x, double y) {
        SystemStore.getNodeContext().render(nodeView, x, y);
    }

    public void renderNodeTree() {
        this.canvas.getChildren().clear();
        final NodeEntity[] last = {null};

        for (NodeEntity root : SystemStore.getRootNodeList()) {
            AlgorithmUtil.headMapNode(root, (parent, node) -> {
                NodeView nodeView = new NodeView(node);
                nodeView.setFocusTraversable(true);

                this.canvas.getChildren().add(nodeView);
                LineService.getInstance().addLine(node.getParent(), node);
                last[0] = node;

            });
        }
        SidebarController.getInstance().sync();
        if (last[0] == null) return;

        //等待节点渲染完成再进行布局
        last[0].actualHeightProperty().addListener(new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                CanvasService.getInstance().resize();
                SettingStore.getLayoutService().layout();

                last[0].actualHeightProperty().removeListener(this);
            }
        });

    }

    public void selectNodeByKey(KeyCode keyCode) {
        NodeEntity selectedNode = SystemStore.getSelectedNode();
        if (selectedNode == null) {
            return;
        }
        NodeEntity parent = selectedNode.getParent();
        int index = parent != null ? parent.getChildren().indexOf(selectedNode) : 0;
        switch (keyCode) {
            case UP -> {
                if (index != 0) selectNode(parent.getChildren().get(index - 1));
            }
            case DOWN -> {
                if (parent != null && index < parent.getChildren().size() - 1) {
                    selectNode(parent.getChildren().get(index + 1));
                }
            }
            case LEFT -> {
                if (parent != null) {
                    selectNode(parent);
                }
            }
            case RIGHT -> {
                if (selectedNode.getChildren() != null && !selectedNode.getChildren().isEmpty()) {
                    selectNode(selectedNode.getChildren().get(0));
                }
            }
        }
    }

    public void selectNode(NodeEntity newNode) {
        NodeEntity prevNode = SystemStore.getSelectedNode();
        if (prevNode != null) {
            prevNode.setSelectedSymbol(false);
        }
        if (newNode != null) {
            newNode.setSelectedSymbol(true);
        }
        SystemStore.setSelectedNode(newNode);
        ToolbarService.getInstance().syncState();
        SidebarController.getInstance().sync();
    }

    public void dragNode(NodeView node, double prevX, double prevY, MouseEvent e) {
        Point2D prevLocalCoords = canvas.sceneToLocal(prevX, prevY);
        Point2D localCoords = canvas.sceneToLocal(e.getSceneX(), e.getSceneY());

        if (node.getCursor() == Cursor.SE_RESIZE) {
            //右下角拉伸
            node.getNodeEntity().widthProperty().set(localCoords.getX() - node.getLayoutX());
            node.getNodeEntity().heightProperty().set(localCoords.getY() - node.getLayoutY());
        } else if (node.getCursor() == Cursor.E_RESIZE) {
            //右边拉伸
            node.getNodeEntity().widthProperty().set(localCoords.getX() - node.getLayoutX());
        } else if (node.getCursor() == Cursor.S_RESIZE) {
            //下边拉伸
            node.getNodeEntity().heightProperty().set(localCoords.getY() - node.getLayoutY());
        } else {
            //拖拽节点
            Bounds viewportBounds = container.getViewportBounds();
            double offsetX = localCoords.getX() - prevLocalCoords.getX() + node.getLayoutX();
            double offsetY = localCoords.getY() - prevLocalCoords.getY() + node.getLayoutY();

            //lambda变量
            final NodeEntity[] nearby = {null};
            final double[] distance = {0.0};

            //container跟随滚动
            if (e.getSceneX() > container.getLayoutX() + viewportBounds.getWidth()) {
                container.setHvalue(container.getHvalue() + (e.getSceneX() - container.getLayoutX() - viewportBounds.getWidth()) / viewportBounds.getWidth());
            } else if (e.getSceneX() < container.getLayoutX()) {
                container.setHvalue(container.getHvalue() - (container.getLayoutX() - e.getSceneX()) / viewportBounds.getWidth());
            }

            if (e.getSceneY() - 100 > container.getLayoutY() + viewportBounds.getHeight()) {
                container.setVvalue(container.getVvalue() + (e.getSceneY() - 100 - container.getLayoutY() - viewportBounds.getHeight()) / viewportBounds.getHeight());
            } else if (e.getSceneY() - 100 < container.getLayoutY()) {
                container.setVvalue(container.getVvalue() + (container.getLayoutY() + e.getSceneY() - 100) / viewportBounds.getHeight());
            }

            //拖拽辅助节点
            SystemStore.getAuxiliaryNode().move(offsetX, offsetY);
            node.setDisable(true);

            //遍历寻找吸附节点
            for (NodeEntity root : SystemStore.getRootNodeList()) {
                AlgorithmUtil.headMapNode(root, (parent, n) -> {
                    double centerX = n.getX() + n.getActualWidth() / 2;
                    double centerY = n.getY() + n.getActualHeight() / 2;

                    double currentDistance = Math.hypot(localCoords.getX() - centerX, localCoords.getY() - centerY);
                    if (currentDistance <= distance[0] || distance[0] == 0.0) {
                        distance[0] = currentDistance;
                        nearby[0] = n;
                    }

                });
            }
            SettingStore.getLayoutService().indicate(nearby[0], localCoords.getX(), localCoords.getY());

        }
    }

    public void dragDoneNode(NodeView node, double prevX, double prevY, MouseEvent e) {
        //如果为拖拽节点
        if (node.getCursor() == Cursor.DEFAULT) {
            Point2D orgLocalCoords = canvas.sceneToLocal(prevX, prevY);
            Point2D localCoords = canvas.sceneToLocal(e.getSceneX(), e.getSceneY());

            SettingStore.getLayoutService().snap(node.getNodeEntity(), localCoords.getX(), localCoords.getY(), orgLocalCoords.getX(), orgLocalCoords.getY());
        }

        //冗余设计，防止错误
        SystemStore.getAuxiliaryNode().hide();


        SettingStore.getLayoutService().layout();
        CanvasService.getInstance().resize();
        SidebarController.getInstance().sync();

        node.setDisable(false);
    }

    public void applyStyle(NodeEntity node) {
        NodeDao.treeApplyStyle(node);

        renderNodeTree();
    }

    public void deleteNode(NodeEntity node) {
        selectNode(null);
        NodeDao.deleteNode(node);
        ToolbarService.getInstance().syncState();
    }

    public void removeNode(NodeView node) {
        //移除node视图
        canvas.getChildren().remove(node);
        SettingStore.getLayoutService().layout();
        SidebarController.getInstance().sync();
    }

}
