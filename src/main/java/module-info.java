module scau.os.soos {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires com.fasterxml.jackson.databind;
    requires java.desktop;

    opens scau.os.soos to javafx.fxml;
    opens scau.os.soos.ui.components to javafx.fxml;
    opens scau.os.soos.apps.terminal to javafx.fxml;
    opens scau.os.soos.apps.mindmap to javafx.fxml;
    opens scau.os.soos.apps.taskManager to javafx.fxml;
    opens scau.os.soos.apps.diskManager to javafx.fxml;
    exports scau.os.soos;
    opens scau.os.soos.ui.components.base to javafx.fxml;
}