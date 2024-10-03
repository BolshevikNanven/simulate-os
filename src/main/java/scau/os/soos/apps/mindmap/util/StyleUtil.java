package scau.os.soos.apps.mindmap.util;

import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

public class StyleUtil {
    public static Font mergeFont(Font font, FontWeight fontWeight) {
        double finalSize = font.getSize();
        String style = font.getStyle().toLowerCase();
        FontPosture finalFontPosture = style.contains("italic") ? FontPosture.ITALIC : FontPosture.REGULAR;

        return Font.font(font.getFamily(), fontWeight, finalFontPosture, finalSize);
    }

    public static Font mergeFont(Font font, Double size) {
        String style = font.getStyle().toLowerCase();
        FontWeight finalFontWeight = style.contains("bold") ? FontWeight.BOLD : FontWeight.NORMAL;
        FontPosture finalFontPosture = style.contains("italic") ? FontPosture.ITALIC : FontPosture.REGULAR;

        return Font.font(font.getFamily(), finalFontWeight, finalFontPosture, size);
    }

    public static Font mergeFont(Font font, FontPosture posture) {
        double finalSize = font.getSize();
        String style = font.getStyle().toLowerCase();
        FontWeight finalFontWeight = style.contains("bold") ? FontWeight.BOLD : FontWeight.NORMAL;

        return Font.font(font.getFamily(), finalFontWeight, posture, finalSize);
    }

    public static Background newBackground(String color) {
        return new Background(new BackgroundFill(Paint.valueOf(color), new CornerRadii(10), null));
    }

    public static String getBackgroundColor(Background background) {
        return background.getFills().get(0).getFill().toString();
    }

    public static Border newBorder(String color) {
        return new Border(new BorderStroke(Paint.valueOf(color), BorderStrokeStyle.SOLID, new CornerRadii(8), new BorderWidths(2)));
    }
    public static String getBorderColor(Border border){
        return border.getStrokes().get(0).getLeftStroke().toString();
    }

}
