package scau.os.soos.apps.mindmap.view.Line;

import scau.os.soos.apps.mindmap.entity.NodeEntity;
import scau.os.soos.apps.mindmap.service.LineService;
import scau.os.soos.apps.mindmap.util.StyleUtil;
import scau.os.soos.apps.mindmap.view.LineView;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;

import java.util.Objects;

public class TwoPolyLine extends LineView {
    public TwoPolyLine(NodeEntity head, NodeEntity tail, SimpleDoubleProperty[] headBindings, SimpleDoubleProperty[] tailBindings) {
        Polyline line = new Polyline();

        this.head = head;
        this.tail = tail;

        double mid = (headBindings[0].get() + tailBindings[0].get()) / 2;

        line.getPoints().addAll(headBindings[0].get(), headBindings[1].get(),
                mid, headBindings[1].get(),
                mid, tailBindings[1].get(),
                tailBindings[0].get(), tailBindings[1].get()
        );
        //更新不是同步的所以需设置两个监听
        headBindings[0].addListener((observableValue, number, t1) -> {
            line.getPoints().set(0, headBindings[0].get());

            double mid1 = (headBindings[0].get() + tailBindings[0].get()) / 2;
            line.getPoints().set(2, mid1);
            line.getPoints().set(4, mid1);
        });
        headBindings[1].addListener((observableValue, number, t1) -> {
            line.getPoints().set(1, headBindings[1].get());
            line.getPoints().set(3, headBindings[1].get());
        });
        //更新不是同步的所以需设置两个监听
        tailBindings[0].addListener((observableValue, number, t1) -> {
            line.getPoints().set(6, tailBindings[0].get());

            double mid1 = (headBindings[0].get() + tailBindings[0].get()) / 2;
            line.getPoints().set(2, mid1);
            line.getPoints().set(4, mid1);
        });
        tailBindings[1].addListener((observableValue, number, t1) -> {
            line.getPoints().set(5, tailBindings[1].get());
            line.getPoints().set(7, tailBindings[1].get());
        });

        String borderColor = StyleUtil.getBorderColor(tail.getBorder());
        String borderOpacity = borderColor.substring(borderColor.length() - 2);

        if (Objects.equals(borderOpacity, "00")) {
            line.setStroke(Color.valueOf(StyleUtil.getBackgroundColor(tail.getBackground())));
        } else {
            line.setStroke(Color.valueOf(borderColor));
        }
        line.setStrokeWidth(2.0);

        this.line = line;
        addListener();
    }

    @Override
    public Node render() {
        return this.line;
    }
}
