package scau.os.soos.common.exception;

import javafx.scene.control.Label;

public class DiskSpaceInsufficientException extends Exception {
    public DiskSpaceInsufficientException(String message) {
        super(message);
    }

    public static Label getPane() {
        return new Label("磁盘空间不足，请清理部分空间后重试");
    }
}
