package scau.os.soos.apps.mindmap.service.layout;

import scau.os.soos.apps.mindmap.common.enums.Direction;
import scau.os.soos.apps.mindmap.service.LayoutService;
import javafx.scene.layout.Pane;

public class LayoutFactory {
    private static LayoutFactory instance;
    private Pane canvas;

    private LayoutFactory() {

    }

    private LayoutFactory(Pane canvas) {
        this.canvas = canvas;
    }

    public static void init(Pane canvas) {
        if (null == instance) {
            instance = new LayoutFactory(canvas);
        }
    }

    public static LayoutFactory getInstance() {
        return instance;
    }

    public LayoutService getService(String type) {
        switch (type) {
            case "LeftTreeLayout" -> {
                return new HorizonTreeLayout(canvas, Direction.LEFT);
            }
            case "MindMapLayout" -> {
                return new MindMapLayout(canvas);
            }
            default -> {
                return new HorizonTreeLayout(canvas, Direction.RIGHT);
            }
        }
    }

}
