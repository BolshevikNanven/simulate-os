package scau.os.soos.apps.fileManager.util;

import javafx.scene.Node;
import javafx.scene.control.Tooltip;

public class tipUtil {
    public static void setTooltip(Node button, String message, String style){
        // 设置提示框样式
        Tooltip tooltip = new Tooltip(message);
        tooltip.setStyle("-fx-background-color:#fafafa;" +
                "-fx-font-size:12;" +
                "-fx-text-fill:#585959;" +
                style);
        Tooltip.install(button, tooltip);
    }
    public static void setTooltip(Node button,String message){
        setTooltip(button,message,"");
    }
}
