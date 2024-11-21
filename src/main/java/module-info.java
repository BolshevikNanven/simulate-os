module scau.os.soos {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires java.desktop;

    opens scau.os.soos to javafx.fxml;
    opens scau.os.soos.ui.components to javafx.fxml;
    opens scau.os.soos.apps.terminal to javafx.fxml;
    opens scau.os.soos.apps.taskManager to javafx.fxml;
    opens scau.os.soos.apps.fileManager to javafx.fxml;
    opens scau.os.soos.apps.diskManager to javafx.fxml, javafx.base;
    exports scau.os.soos;
    opens scau.os.soos.ui.components.base to javafx.fxml;
    opens scau.os.soos.apps.fileManager.controller to javafx.fxml;
    exports scau.os.soos.apps.editor;
    opens scau.os.soos.apps.editor to javafx.fxml;
}