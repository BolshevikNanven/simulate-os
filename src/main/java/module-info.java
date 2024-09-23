module scau.os.soos {
    requires javafx.controls;
    requires javafx.fxml;


    opens scau.os.soos to javafx.fxml;
    exports scau.os.soos;
}