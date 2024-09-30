module scau.os.soos {
    requires javafx.controls;
    requires javafx.fxml;


    opens scau.os.soos to javafx.fxml;
    opens scau.os.soos.ui.components to javafx.fxml;
    exports scau.os.soos;
}