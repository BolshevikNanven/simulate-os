package scau.os.soos.apps.mindmap.util;

import scau.os.soos.apps.mindmap.entity.NodeEntity;
import scau.os.soos.apps.mindmap.common.handler.MapEventHandler;

public class AlgorithmUtil {
    private static void headMapNode(NodeEntity parent, NodeEntity node, MapEventHandler handler) {
        handler.handle(parent, node);
        for (NodeEntity child : node.getChildren()) {
            headMapNode(node, child, handler);
        }
    }

    public static void headMapNode(NodeEntity node, MapEventHandler handler) {
        handler.handle(null, node);
        for (NodeEntity child : node.getChildren()) {
            headMapNode(node, child, handler);
        }
    }

    public static void tailMapNode(NodeEntity node, MapEventHandler handler) {
        for (NodeEntity child : node.getChildren()) {
            tailMapNode(child, handler);
        }
        handler.handle(null, node);
    }

    public static NodeEntity findRoot(NodeEntity target) {
        NodeEntity node = target;
        while (true) {
            if (node.getParent() == null) {
                return node;
            }
            node = node.getParent();
        }
    }

    //向根节点判断节点是否存在
    public static boolean checkExistParent(NodeEntity start, NodeEntity target) {
        NodeEntity node = start;
        while (true) {
            if (node.getParent() == null) {
                return false;
            }

            if (target == node.getParent()) {
                return true;
            }
            node = node.getParent();
        }
    }
}
