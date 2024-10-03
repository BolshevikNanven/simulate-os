package scau.os.soos.apps.mindmap.common.handler;

import scau.os.soos.apps.mindmap.entity.NodeEntity;

@FunctionalInterface
public interface MapEventHandler {
    void handle(NodeEntity parent,NodeEntity node);
}
