package scau.os.soos.ui.animation;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class Transition {
    public static void playSlideInY(Node node, Duration duration, double from, double to) {
        TranslateTransition slideIn = new TranslateTransition(duration, node);
        slideIn.setFromY(from);
        slideIn.setToY(to);

        slideIn.play();
    }

    public static void playSlideInX(Node node, Duration duration, double from, double to) {
        TranslateTransition slideIn = new TranslateTransition(duration, node);
        slideIn.setFromX(from);
        slideIn.setToX(to);

        slideIn.play();
    }

    public static void playFadeIn(Node node, Duration duration) {
        FadeTransition fadeIn = new FadeTransition(duration, node);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        fadeIn.play();
    }

    public static void playZoomIn(Node node, Duration duration) {
        ScaleTransition zoomIn = new ScaleTransition(duration, node);

        zoomIn.setFromX(0.8);
        zoomIn.setFromY(0.8);
        zoomIn.setToX(1);
        zoomIn.setToY(1);

        zoomIn.play();
    }
    public static void playShake(Pane node){
        TranslateTransition translateTransition = new TranslateTransition();
        translateTransition.setNode(node);
        translateTransition.setFromX(0);
        translateTransition.setToX(12);
        translateTransition.setCycleCount(5);
        translateTransition.setAutoReverse(true);
        translateTransition.setInterpolator(javafx.animation.Interpolator.EASE_BOTH);
        translateTransition.setRate(6);

        translateTransition.play();
    }
}
