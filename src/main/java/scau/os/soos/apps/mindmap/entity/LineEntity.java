package scau.os.soos.apps.mindmap.entity;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.shape.CubicCurve;

public class LineEntity {
    private final SimpleObjectProperty<NodeEntity> head = new SimpleObjectProperty<>();
    private NodeEntity tail;
    private CubicCurve curve;

    public NodeEntity getHead() {
        return head.get();
    }

    public SimpleObjectProperty<NodeEntity> headProperty() {
        return head;
    }

    public void setHead(NodeEntity head) {
        this.head.set(head);
    }

    public NodeEntity getTail() {
        return tail;
    }

    public void setTail(NodeEntity tail) {
        this.tail = tail;
    }

    public CubicCurve getCurve() {
        return curve;
    }


    public void setCurve(CubicCurve curve) {
        this.curve = curve;
    }
}
