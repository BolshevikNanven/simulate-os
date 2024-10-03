package scau.os.soos.apps.mindmap.service.layout;

import scau.os.soos.apps.mindmap.common.enums.Direction;
import scau.os.soos.apps.mindmap.dao.NodeDao;
import scau.os.soos.apps.mindmap.entity.NodeEntity;
import scau.os.soos.apps.mindmap.service.LayoutService;
import scau.os.soos.apps.mindmap.service.LineService;
import scau.os.soos.apps.mindmap.store.SettingStore;
import scau.os.soos.apps.mindmap.store.SystemStore;
import scau.os.soos.apps.mindmap.util.AlgorithmUtil;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.layout.Pane;

import java.util.List;

public class HorizonTreeLayout extends LayoutParent {
    private NodeEntity parent;
    private NodeEntity brother;
    private Direction nodeDirection;
    private final Direction layoutDirection;

    public HorizonTreeLayout(Pane canvas, Direction direction) {
        super(canvas);
        this.layoutDirection = direction;

    }

    private void doBounds(NodeEntity node) {
        // 直接检查子节点列表是否为空
        if (node.getChildren().isEmpty()) {
            node.setBounds(node.getActualHeight());
            return;
        }

        double totalHeight = 0.0;
        for (NodeEntity child : node.getChildren()) {
            doBounds(child);
            // 更新高度总和，考虑空白
            totalHeight += (totalHeight > 0 ? child.getBounds() + MARGIN_V : child.getBounds());
        }

        // 子节点高度和可能小于此节点高度
        node.setBounds(Math.max(totalHeight, node.getActualHeight()));
    }

    private void doLayout(NodeEntity node) {
        // 仅当节点有父级时，才处理布局
        if (node.getParent() != null) {
            NodeEntity parent = node.getParent();
            List<NodeEntity> siblings = parent.getChildren();

            // 提前计算父节点和所有子节点的相关位置，避免重复计算
            double parentCenterY = parent.getY() + parent.getActualHeight() / 2;
            double childrenTotalHeight = siblings.stream().mapToDouble(NodeEntity::getBounds).sum();
            double childrenSpacing = (siblings.size() - 1) * MARGIN_V;
            double top = parentCenterY - (childrenTotalHeight + childrenSpacing) / 2;

            for (NodeEntity child : siblings) {
                double childY = top + child.getBounds() / 2 - child.getActualHeight() / 2;
                double childX = layoutDirection == Direction.RIGHT ?
                        parent.getX() + parent.getActualWidth() + SettingStore.getMarginH() :
                        parent.getX() - child.getActualWidth() - SettingStore.getMarginH();

                // 直接设置计算得到的位置
                child.setY(childY);
                child.setX(childX);

                // 更新下一个子节点起始位置
                top += child.getBounds() + MARGIN_V;
            }
        }

        // 递归处理子节点
        node.getChildren().forEach(this::doLayout);
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


        if (right > node.getActualWidth() / 2 && layoutDirection == Direction.RIGHT) {
            //右吸附
            showIndicator(node, Direction.RIGHT);

            parent = node;
            brother = null;
        } else if (right < 0 && layoutDirection == Direction.LEFT) {
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
        return createLineEndProperties(head, true);
    }

    @Override
    public SimpleDoubleProperty[] getLineTail(NodeEntity head, NodeEntity tail) {
        return createLineEndProperties(tail, false);
    }

    private SimpleDoubleProperty[] createLineEndProperties(NodeEntity node, boolean isHead) {
        if (node == null) return null;

        SimpleDoubleProperty xProperty = new SimpleDoubleProperty();
        SimpleDoubleProperty yProperty = new SimpleDoubleProperty();

        updateXProperty(xProperty, node, isHead);
        updateYProperty(yProperty, node);

        node.xProperty().addListener((observable, oldValue, newValue) -> updateXProperty(xProperty, node, isHead));
        node.yProperty().addListener((observable, oldValue, newValue) -> updateYProperty(yProperty, node));
        node.widthProperty().addListener((observable, oldValue, newValue) -> updateXProperty(xProperty, node, isHead));
        node.heightProperty().addListener((observable, oldValue, newValue) -> updateYProperty(yProperty, node));

        return new SimpleDoubleProperty[]{xProperty, yProperty};
    }

    private void updateXProperty(SimpleDoubleProperty xProperty, NodeEntity node, boolean isHead) {
        if ((layoutDirection == Direction.RIGHT && isHead) || (layoutDirection != Direction.RIGHT && !isHead)) {
            xProperty.set(node.getX() + node.getActualWidth());
        } else {
            xProperty.set(node.getX());
        }
    }

    private void updateYProperty(SimpleDoubleProperty yProperty, NodeEntity node) {
        yProperty.set(node.getY() + node.getActualHeight() / 2);
    }


}
