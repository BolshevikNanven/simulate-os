package scau.os.soos.apps.mindmap.service.layout;

import scau.os.soos.apps.mindmap.common.enums.Direction;
import scau.os.soos.apps.mindmap.dao.NodeDao;
import scau.os.soos.apps.mindmap.entity.NodeEntity;
import scau.os.soos.apps.mindmap.service.LayoutService;
import scau.os.soos.apps.mindmap.service.LineService;
import scau.os.soos.apps.mindmap.store.SettingStore;
import scau.os.soos.apps.mindmap.store.SystemStore;
import scau.os.soos.apps.mindmap.util.AlgorithmUtil;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.layout.Pane;

import java.util.List;

public class MindMapLayout extends LayoutParent {
    private NodeEntity parent;
    private NodeEntity brother;
    private Direction nodeDirection;

    public MindMapLayout(Pane canvas) {
        super(canvas);

    }

    //TODO:BUG: node children可能为null,原因不明
    private void doBounds(NodeEntity node) {
        double bounds = 0.0;
        if (node.getChildren() == null || node.getChildren().isEmpty()) {
            bounds = node.getActualHeight();
        } else {
            for (int i = 0; i < node.getChildren().size(); i++) {
                NodeEntity child = node.getChildren().get(i);
                doBounds(child);
                if (bounds == 0.0) {
                    bounds += child.getBounds();
                } else bounds += child.getBounds() + MARGIN_V;

            }
        }

        //子节点高度和小于此节点高度
        if (bounds < node.getActualHeight()) {
            bounds = node.getActualHeight();
        }
        node.setBounds(bounds);

        //根节点需设置左右bounds,直接放params里
        if (node.getParent() == null) {
            double rightBounds = 0.0;
            double leftBounds = 0.0;

            for (int i = 0; i < node.getChildren().size(); i++) {
                NodeEntity child = node.getChildren().get(i);
                if (i % 2 == 0) {
                    child.setParam(Direction.RIGHT);
                    rightBounds += child.getBounds() + MARGIN_V;
                } else {
                    child.setParam(Direction.LEFT);
                    leftBounds += child.getBounds() + MARGIN_V;
                }
            }
            rightBounds -= MARGIN_V;
            leftBounds -= MARGIN_V;

            node.setParam(new Double[]{leftBounds, rightBounds});
        }
    }

    private void doLayout(NodeEntity node) {
        if (node.getParent() != null) {
            List<NodeEntity> broNodeList = node.getParent().getChildren();
            NodeEntity parent = node.getParent();

            double marginH = SettingStore.getMarginH() * 1.0;
            int index = broNodeList.indexOf(node);

            double top = parent.getY() + parent.getActualHeight() / 2 - parent.getBounds() / 2;

            //二级节点需特殊处理左右布局
            if (parent.getParent() == null) {
                Double[] bounds = (Double[]) parent.getParam();
                if (node.getParam() == Direction.RIGHT) {
                    top = parent.getY() + parent.getActualHeight() / 2 - bounds[1] / 2;
                } else {
                    top = parent.getY() + parent.getActualHeight() / 2 - bounds[0] / 2;
                }
            }

            for (int i = 0; i < index; i++) {
                NodeEntity n = broNodeList.get(i);
                if (n.getParam() == node.getParam()) {
                    top += n.getBounds() + MARGIN_V;
                }
            }

            top += node.getBounds() / 2 - node.getActualHeight() / 2;

            node.setY(top);
            if (node.getParam() == Direction.RIGHT) {
                node.setX(parent.getX() + parent.getActualWidth() + marginH);
            } else {
                node.setX(parent.getX() - node.getActualWidth() - marginH);
            }
            LineService.getInstance().addLine(parent, node);
        }
        if (node.getChildren() != null) {
            for (int i = 0; i < node.getChildren().size(); i++) {
                NodeEntity child = node.getChildren().get(i);
                NodeEntity parent = node.getParent();
                if (parent != null) {
                    child.setParam(node.getParam());
                }

                doLayout(child);
            }
        }

    }

    @Override
    public void layout() {
        for (NodeEntity root : SystemStore.getRootNodeList()) {
            doBounds(root);
            doLayout(root);
        }
    }

    @Override
    public void indicate(NodeEntity node, double x, double y) {
        double bottom = y - node.getY();
        double right = x - node.getX();


        if (Math.abs(bottom) > node.getActualHeight() + 64 || Math.abs(right) > node.getActualWidth() + 72) {
            parent = null;
            brother = null;
            hideIndicator();
            return;
        }


        if (right > node.getActualWidth() / 2 && node.getParam() == Direction.RIGHT) {
            //右吸附
            showIndicator(node, Direction.RIGHT);

            parent = node;
            brother = null;
        } else if (right < 0 && node.getParam() == Direction.LEFT) {
            //左吸附
            showIndicator(node, Direction.LEFT);

            parent = node;
            brother = null;
        } else if (bottom > 0) {
            //下吸附
            showIndicator(node, Direction.BOTTOM);

            parent = node.getParent();
            brother = node;
            nodeDirection = Direction.BOTTOM;
        } else if (bottom < 0) {
            //上吸附
            showIndicator(node, Direction.TOP);

            parent = node.getParent();
            brother = node;
            nodeDirection = Direction.TOP;
        }

    }

    //TODO:优化逻辑
    @Override
    public void snap(NodeEntity node, double x, double y, double prevX, double prevY) {
        //节点不吸附直接移动
        if (parent == null || parent == node || brother == node || node.getParent() == null && parent == null) {
            node.setX(x - prevX + node.getX());
            node.setY(y - prevY + node.getY());
        }

        ///节点吸附自身，结束
        if (parent == node || brother == node) {
            hideIndicator();
            return;
        }

        if (parent == null && node.getParent() != null) {   //节点取消吸附
            NodeDao.moveNode(node, null, 0);

        } else if (parent != null && brother == null) {  //节点左右吸附
            if (AlgorithmUtil.checkExistParent(parent, node)) {
                hideIndicator();
                return;
            }
            NodeDao.moveNode(node, parent, 0);

            SystemStore.getRootNodeList().remove(node);
        } else if (parent != null && brother != null) {   //节点上下吸附
            int broIndex = brother.getParent().getChildren().indexOf(brother);

            NodeDao.moveNode(node, parent, nodeDirection == Direction.TOP ? broIndex : broIndex + 1);
        }
        hideIndicator();

    }

    @Override
    public SimpleDoubleProperty[] getLineHead(NodeEntity head, NodeEntity tail) {
        if (head == null) return null;


        SimpleDoubleProperty[] res = new SimpleDoubleProperty[]{new SimpleDoubleProperty(), new SimpleDoubleProperty()};
        if (head.getParam() == Direction.RIGHT || head.getParent() == null && tail.getParam() == Direction.RIGHT) {
            res[0].set(head.getX() + head.getActualWidth());
            head.xProperty().addListener((observableValue, number, t1) -> {
                res[0].set(head.getX() + head.getActualWidth());
            });
            head.widthProperty().addListener((observableValue, number, t1) -> {
                res[0].set(head.getX() + head.getActualWidth());
            });
        } else {
            res[0].set(head.getX());
            head.xProperty().addListener((observableValue, number, t1) -> {
                res[0].set(head.getX());
            });
        }
        res[1].set(head.getY() + head.getActualHeight() / 2);
        head.yProperty().addListener((observableValue, number, t1) -> {
            res[1].set(head.getY() + head.getActualHeight() / 2);
        });
        head.heightProperty().addListener((observableValue, number, t1) -> {
            res[1].set(head.getY() + head.getActualHeight() / 2);
        });
        return res;
    }

    @Override
    public SimpleDoubleProperty[] getLineTail(NodeEntity head, NodeEntity tail) {
        SimpleDoubleProperty[] res = new SimpleDoubleProperty[]{new SimpleDoubleProperty(), new SimpleDoubleProperty()};
        if (tail.getParam() == Direction.RIGHT) {
            res[0].set(tail.getX());
            tail.xProperty().addListener((observableValue, number, t1) -> {
                res[0].set(tail.getX());
            });
        } else {
            res[0].set(tail.getX() + tail.getActualWidth());
            tail.xProperty().addListener((observableValue, number, t1) -> {
                res[0].set(tail.getX() + tail.getActualWidth());
            });
            tail.widthProperty().addListener((observableValue, number, t1) -> {
                res[0].set(tail.getX() + tail.getActualWidth());
            });
        }
        res[1].set(tail.getY() + tail.getActualHeight() / 2);
        tail.yProperty().addListener((observableValue, number, t1) -> {
            res[1].set(tail.getY() + tail.getActualHeight() / 2);
        });
        tail.heightProperty().addListener((observableValue, number, t1) -> {
            res[1].set(tail.getY() + tail.getActualHeight() / 2);
        });
        return res;
    }
}
