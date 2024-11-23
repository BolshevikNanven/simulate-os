package scau.os.soos.ui.components;

import javafx.scene.Node;

public class Tooltip {
    public static void setTooltip(Node button, String message, String style){
        // 设置提示框样式
        javafx.scene.control.Tooltip tooltip = new javafx.scene.control.Tooltip(message);
        tooltip.setStyle("-fx-background-color:#fafafa;" +
                "-fx-font-size:12;" +
                "-fx-text-fill:#585959;" +
                style);
        javafx.scene.control.Tooltip.install(button, tooltip);
    }
    public static void setTooltip(Node button,String message){
        setTooltip(button,message,"");
    }
}
