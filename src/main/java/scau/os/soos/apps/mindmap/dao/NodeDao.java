package scau.os.soos.apps.mindmap.dao;

import scau.os.soos.apps.mindmap.common.handler.MapEventHandler;
import scau.os.soos.apps.mindmap.entity.Command;
import scau.os.soos.apps.mindmap.entity.NodeEntity;
import scau.os.soos.apps.mindmap.service.*;
import scau.os.soos.apps.mindmap.store.SystemStore;
import scau.os.soos.apps.mindmap.util.AlgorithmUtil;
import scau.os.soos.apps.mindmap.util.StyleUtil;
import javafx.geometry.Pos;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class NodeDao {

    public static void moveNode(NodeEntity node, NodeEntity parent, int index) {
        WeakReference<NodeEntity> weakNode = new WeakReference<>(node);
        WeakReference<NodeEntity> weakNewParent = new WeakReference<>(parent);
        WeakReference<NodeEntity> weakOldParent = new WeakReference<>(node.getParent());

        int oldIndex;
        double oldX = node.getX();
        double oldY = node.getY();

        oldIndex = (node.getParent() != null) ? node.getParent().getChildren().indexOf(node) : SystemStore.getRootNodeList().indexOf(node);

        UndoAndRedoService.getInstance().execute(new Command() {
            @Override
            public void execute() {
                NodeEntity node = weakNode.get();
                NodeEntity parent = weakNewParent.get();
                if (node != null) {
                    node.setX(oldX);
                    node.setY(oldY);
                    doMoveNode(node, parent, index);
                    NodeService.getInstance().renderNodeTree();
                }

            }

            @Override
            public void undo() {
                NodeEntity node = weakNode.get();
                NodeEntity parent = weakOldParent.get();
                if (node != null) {
                    node.setX(oldX);
                    node.setY(oldY);
                    doMoveNode(node, parent, oldIndex);
                    NodeService.getInstance().renderNodeTree();
                }
            }
        });

    }

    private static void doMoveNode(NodeEntity node, NodeEntity parent, int index) {
        if (parent == null) {
            if (node.getParent() != null) {
                SystemStore.getRootNodeList().add(node);
            }
            node.getParent().getChildren().remove(node);
            node.setParent(null);
            node.setLine(null);

            return;
        }

        //从根节点列表移除
        if (node.getParent() == null) {
            SystemStore.getRootNodeList().remove(node);
        }
        if (node.getParent() == parent) {
            int prevIndex = parent.getChildren().indexOf(node);
            parent.getChildren().remove(prevIndex);
            if (index > prevIndex) {
                parent.getChildren().add(index - 1, node);
            } else parent.getChildren().add(index, node);
        } else {
            if (node.getParent() != null) {
                node.getParent().getChildren().remove(node);
            } else {
                SystemStore.getRootNodeList().remove(node);
            }
            parent.getChildren().add(index, node);
            node.setParent(parent);
        }
    }

    //TODO：加入撤回操作
    public static void treeApplyStyle(NodeEntity node) {
        AlgorithmUtil.headMapNode(node, ((parent, node1) -> {
            if (parent != null) {
                node1.setAlignment(parent.getAlignment());
                node1.setBorder(parent.getBorder());
                node1.setBackground(parent.getBackground());
                node1.setColor(parent.getColor());
                node1.setFont(parent.getFont());
                node1.setFontUnderline(parent.isFontUnderline());
                node1.setHeight(parent.getHeight());
                node1.setWidth(parent.getWidth());
            }
        }));
    }

    public static void deleteNode(NodeEntity node) {
        WeakReference<NodeEntity> weakNode = new WeakReference<>(node);
        WeakReference<NodeEntity> weakParent = new WeakReference<>(node.getParent());

        int index = node.getParent() != null ? node.getParent().getChildren().indexOf(node) : 0;

        UndoAndRedoService.getInstance().execute(new Command() {
            @Override
            public void execute() {
                NodeEntity node = weakNode.get();
                NodeEntity parent = weakParent.get();
                if (node != null) {
                    if (parent == null) {
                        SystemStore.getRootNodeList().remove(node);
                    } else {
                        parent.getChildren().remove(node);
                    }
                    NodeService.getInstance().renderNodeTree();
                }
            }

            @Override
            public void undo() {
                NodeEntity node = weakNode.get();
                NodeEntity parent = weakParent.get();
                if (node != null) {
                    if (parent == null) {
                        SystemStore.getRootNodeList().add(node);
                    } else {
                        parent.getChildren().add(index,node);
                    }
                    NodeService.getInstance().renderNodeTree();
                }
            }
        });


    }

    private static void doDeleteNode(NodeEntity node) {
        if (!node.getChildren().isEmpty()) {
            for (NodeEntity child : node.getChildren()) {
                doDeleteNode(child);
            }
        }
        node.setParent(null);
        node.setChildren(null);
        node.setDeleteSymbol(true);
    }

    public static NodeEntity newNode(NodeEntity parent, NodeEntity prevNode) {
        NodeEntity node = newBean(prevNode);

        WeakReference<NodeEntity> weakParent = new WeakReference<>(parent);
        WeakReference<NodeEntity> weakNode = new WeakReference<>(node);
        UndoAndRedoService.getInstance().execute(new Command() {
            @Override
            public void execute() {
                NodeEntity parent = weakParent.get();
                NodeEntity node = weakNode.get();
                if (parent != null && node != null) {
                    node.setParent(parent);
                    node.setChildren(new ArrayList<>());
                    parent.getChildren().add(node);

                    NodeService.getInstance().renderNodeTree();
                    NodeService.getInstance().selectNode(node);
                }
            }

            @Override
            public void undo() {
                NodeEntity parent = weakParent.get();
                NodeEntity node = weakNode.get();
                if (parent != null && node != null) {
                    parent.getChildren().remove(node);

                    NodeService.getInstance().renderNodeTree();
                    NodeService.getInstance().selectNode(node);
                }
            }
        });
        return node;
    }

    public static NodeEntity newNode(NodeEntity parent) {
        NodeEntity node = newBean(parent);

        WeakReference<NodeEntity> weakParent = new WeakReference<>(parent);
        WeakReference<NodeEntity> weakNode = new WeakReference<>(node);
        UndoAndRedoService.getInstance().execute(new Command() {
            @Override
            public void execute() {
                NodeEntity parent = weakParent.get();
                NodeEntity node = weakNode.get();
                if (parent != null && node != null) {
                    node.setParent(parent);
                    node.setChildren(new ArrayList<>());
                    parent.getChildren().add(node);

                    NodeService.getInstance().renderNodeTree();
                    NodeService.getInstance().selectNode(node);
                }

            }

            @Override
            public void undo() {
                NodeEntity parent = weakParent.get();
                NodeEntity node = weakNode.get();
                if (parent != null && node != null) {
                    parent.getChildren().remove(node);

                    NodeService.getInstance().renderNodeTree();
                    NodeService.getInstance().selectNode(node);
                }
            }
        });


        return node;
    }

    public static NodeEntity newNode() {
        NodeEntity node = newBean(null);

        WeakReference<NodeEntity> weakNode = new WeakReference<>(node);
        UndoAndRedoService.getInstance().execute(new Command() {
            @Override
            public void execute() {
                NodeEntity node = weakNode.get();
                if (node != null) {
                    SystemStore.getRootNodeList().add(node);
                    node.setParent(null);
                    node.setChildren(new ArrayList<>());

                    NodeService.getInstance().renderNodeTree();
                    NodeService.getInstance().selectNode(node);
                }

            }

            @Override
            public void undo() {
                NodeEntity node = weakNode.get();
                if (node != null) {
                    SystemStore.getRootNodeList().remove(node);
                    NodeService.getInstance().renderNodeTree();
                    NodeService.getInstance().selectNode(node);
                }
            }
        });

        return node;
    }


    private static NodeEntity newBean(NodeEntity template) {
        NodeEntity node = new NodeEntity();
        if (template == null) {
            node.setAlignment(Pos.CENTER_LEFT);
            node.setContent("新节点");
            node.setX(CanvasService.getInstance().getCenterX());
            node.setY(CanvasService.getInstance().getCenterY());
            node.setHeight(42);
            node.setWidth(86);
            node.setDisabled(false);
            node.setBackground(StyleUtil.newBackground("rgb(211,227,253)"));
            node.setBorder(StyleUtil.newBorder("transparent"));
            node.setColor(Paint.valueOf("#212121"));
            node.setFont(Font.font("system", FontWeight.NORMAL, FontPosture.REGULAR, 14));
            node.setFontUnderline(false);
        } else {
            node.setAlignment(template.getAlignment());
            node.setContent("新节点");
            node.setX(CanvasService.getInstance().getCenterX());
            node.setY(CanvasService.getInstance().getCenterY());
            node.setHeight(42);
            node.setDisabled(false);
            node.setWidth(template.getWidth());
            node.setBackground(template.getBackground());
            node.setBorder(template.getBorder());
            node.setColor(template.getColor());
            node.setFont(template.getFont());
            node.setFontUnderline(template.isFontUnderline());
        }

        return node;
    }
}
