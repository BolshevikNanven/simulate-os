package scau.os.soos.ui.animation;

import javafx.animation.*;
import javafx.scene.Node;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class Animation {
    public static void playSlideInX(Pane node, Duration duration, double to) {
        Timeline timeline = new Timeline();

        KeyValue widthValue = new KeyValue(node.layoutXProperty(), to);

        KeyFrame keyFrame = new KeyFrame(duration, widthValue);

        timeline.getKeyFrames().add(keyFrame);

        timeline.play();
    }

    public static void playSlideInY(Pane node, Duration duration, double to) {
        Timeline timeline = new Timeline();

        KeyValue widthValue = new KeyValue(node.layoutYProperty(), to);

        KeyFrame keyFrame = new KeyFrame(duration, widthValue);

        timeline.getKeyFrames().add(keyFrame);

        timeline.play();
    }

    public static void playWidthIn(Pane node, Duration duration, double to) {
        Timeline timeline = new Timeline();

        KeyValue widthValue = new KeyValue(node.prefWidthProperty(), to);

        KeyFrame keyFrame = new KeyFrame(duration, widthValue);

        timeline.getKeyFrames().add(keyFrame);

        timeline.play();
    }

    public static void playHeightIn(Pane node, Duration duration, double to) {
        Timeline timeline = new Timeline();

        KeyValue widthValue = new KeyValue(node.prefHeightProperty(), to);

        KeyFrame keyFrame = new KeyFrame(duration, widthValue);

        timeline.getKeyFrames().add(keyFrame);

        timeline.play();
    }
}
