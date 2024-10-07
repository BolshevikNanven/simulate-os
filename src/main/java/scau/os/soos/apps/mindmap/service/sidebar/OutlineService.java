package scau.os.soos.apps.mindmap.service.sidebar;

import scau.os.soos.apps.mindmap.entity.NodeEntity;
import scau.os.soos.apps.mindmap.service.SidebarService;
import scau.os.soos.apps.mindmap.store.SystemStore;
import scau.os.soos.apps.mindmap.util.AlgorithmUtil;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.util.List;

public class OutlineService implements SidebarService {
    private final TreeView<String> outlineTreeView;
    private final TreeItem<String> virtualRootItem;

    public OutlineService() {
        outlineTreeView = new TreeView<>();
        virtualRootItem = new TreeItem<>(""); // 初始化虚拟根节点，可以使用空字符串作为占位符
        outlineTreeView.setShowRoot(false); // 不显示虚拟根节点
        outlineTreeView.setRoot(virtualRootItem); // 将虚拟根节点设置为TreeView的根节点
    }

    // 构建大纲
    private void buildOutlineFromNode(NodeEntity node) {
        NodeEntity rootNode = AlgorithmUtil.findRoot(node);
        if (rootNode != null) {
            TreeItem<String> rootItem = new TreeItem<>(rootNode.getContent());
            outlineTreeView.setRoot(rootItem);
            addTreeItems(rootItem, rootNode.getChildren());
            outlineTreeView.setShowRoot(true);
        }
    }

    // 递归地添加子节点到TreeView
    private void addTreeItems(TreeItem<String> parentItem, List<NodeEntity> children) {
        if (children == null) return;

        for (NodeEntity child : children) {
            TreeItem<String> item = new TreeItem<>(child.getContent());
            parentItem.getChildren().add(item);
            item.setExpanded(true); // 确保新添加的节点是展开的
            if (child.getChildren() != null && !child.getChildren().isEmpty()) {
                addTreeItems(item, child.getChildren());
            }
        }
    }


    private void updateOutline() {
        virtualRootItem.getChildren().clear(); // 清除以前所有的根节点
        for (NodeEntity node : SystemStore.getRootNodeList()) {
            if (node.getParent()==null) { // 只处理没有父节点的节点
                TreeItem<String> rootItem = new TreeItem<>(node.getContent());
                virtualRootItem.getChildren().add(rootItem); // 添加到虚拟根节点作为它的子节点
                addTreeItems(rootItem, node.getChildren()); // 根据新的根节点重建大纲
                rootItem.setExpanded(true); // 确保根节点被展开
            }
        }
    }
    @Override
    public Node render() {
        updateOutline();
        return outlineTreeView;
    }

    @Override
    public void sync() {
        updateOutline();
    }
}
