package scau.os.soos.apps.mindmap.entity;

import java.util.List;

public class FileDTO {
    private SettingEntity setting;
    private List<NodeEntity> nodes;

    public List<NodeEntity> getNodes() {
        return nodes;
    }

    public void setNodes(List<NodeEntity> nodes) {
        this.nodes = nodes;
    }

    public SettingEntity getSetting() {
        return setting;
    }

    public void setSetting(SettingEntity setting) {
        this.setting = setting;
    }
}
