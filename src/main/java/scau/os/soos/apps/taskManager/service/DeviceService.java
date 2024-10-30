package scau.os.soos.apps.taskManager.service;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import scau.os.soos.apps.taskManager.TaskManagerApp;
import scau.os.soos.apps.taskManager.TaskManagerService;

import java.io.IOException;

public class DeviceService implements TaskManagerService {
    private final ScrollPane detailContainer;
    private final VBox deviceDetail;

    public DeviceService(ScrollPane detailContainer) {
        this.detailContainer = detailContainer;
        try {
            deviceDetail = FXMLLoader.load(TaskManagerApp.class.getResource("device/device.fxml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void show() {
        detailContainer.setContent(deviceDetail);
    }

    @Override
    public void render() {

    }
}
