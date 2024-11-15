package scau.os.soos.apps.taskManager.service;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import scau.os.soos.apps.taskManager.TaskManagerApp;
import scau.os.soos.apps.taskManager.TaskManagerService;
import scau.os.soos.common.OS;
import scau.os.soos.common.enums.DEVICE_TYPE;
import scau.os.soos.module.device.DeviceController;
import scau.os.soos.module.device.view.DeviceOverviewReadView;
import scau.os.soos.module.device.view.DeviceReadView;

import java.io.IOException;
import java.util.Map;

public class DeviceService implements TaskManagerService {
    private final ScrollPane detailContainer;
    private final VBox deviceDetail;
    private final AreaChart<String, Integer> overviewChart;
    private final XYChart.Series<String, Integer> overviewSeries;
    private final AreaChart<String, Integer> AChart;
    private final XYChart.Series<String, Integer> ASeries;
    private final Label AUsage;
    private final Label AAvailable;
    private final Label AUsing;
    private final Label AWaiting;
    private final AreaChart<String, Integer> BChart;
    private final XYChart.Series<String, Integer> BSeries;
    private final Label BUsage;
    private final Label BAvailable;
    private final Label BUsing;
    private final Label BWaiting;
    private final AreaChart<String, Integer> CChart;
    private final XYChart.Series<String, Integer> CSeries;
    private final Label CUsage;
    private final Label CAvailable;
    private final Label CUsing;
    private final Label CWaiting;
    private final Label overview;

    public DeviceService(AreaChart<String, Integer> overviewChart, Label overview, ScrollPane detailContainer) {
        this.detailContainer = detailContainer;
        this.overviewChart = overviewChart;
        this.overview = overview;

        this.overviewSeries = new XYChart.Series<>();
        this.ASeries = new XYChart.Series<>();
        this.BSeries = new XYChart.Series<>();
        this.CSeries = new XYChart.Series<>();

        try {
            deviceDetail = FXMLLoader.load(TaskManagerApp.class.getResource("device/device.fxml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        AChart = (AreaChart<String, Integer>) deviceDetail.lookup("#device-A-chart");
        AUsage = (Label) deviceDetail.lookup("#device-A-usage");
        AAvailable = (Label) deviceDetail.lookup("#device-A-available");
        AUsing = (Label) deviceDetail.lookup("#device-A-using");
        AWaiting = (Label) deviceDetail.lookup("#device-A-waiting");

        BChart = (AreaChart<String, Integer>) deviceDetail.lookup("#device-B-chart");
        BUsage = (Label) deviceDetail.lookup("#device-B-usage");
        BAvailable = (Label) deviceDetail.lookup("#device-B-available");
        BUsing = (Label) deviceDetail.lookup("#device-B-using");
        BWaiting = (Label) deviceDetail.lookup("#device-B-waiting");

        CChart = (AreaChart<String, Integer>) deviceDetail.lookup("#device-C-chart");
        CUsage = (Label) deviceDetail.lookup("#device-C-usage");
        CAvailable = (Label) deviceDetail.lookup("#device-C-available");
        CUsing = (Label) deviceDetail.lookup("#device-C-using");
        CWaiting = (Label) deviceDetail.lookup("#device-C-waiting");

        overviewChart.getData().add(overviewSeries);
        AChart.getData().add(ASeries);
        BChart.getData().add(BSeries);
        CChart.getData().add(CSeries);
    }


    @Override
    public void show() {
        detailContainer.setContent(deviceDetail);
    }

    @Override
    public void render() {
        Map<DEVICE_TYPE, DeviceReadView> deviceDataMap = DeviceController.getInstance().getData();

        Platform.runLater(() -> {
            setDeviceData(deviceDataMap.get(DEVICE_TYPE.A), AUsage, AAvailable, AWaiting, AUsing, ASeries);
            setDeviceData(deviceDataMap.get(DEVICE_TYPE.B), BUsage, BAvailable, BWaiting, BUsing, BSeries);
            setDeviceData(deviceDataMap.get(DEVICE_TYPE.C), CUsage, CAvailable, CWaiting, CUsing, CSeries);
        });
    }

    @Override
    public void overview() {
        DeviceOverviewReadView overviewReadView = DeviceController.getInstance().getOverview();
        overviewSeries.getData().add(new XYChart.Data<>(String.valueOf(OS.clock.get()), overviewReadView.usage()));
        if (overviewSeries.getData().size() > 60) {
            overviewSeries.getData().removeFirst();
        }
        overview.setText(String.format("%d/8台", overviewReadView.usage()));
    }

    private void setDeviceData(DeviceReadView data, Label usage, Label available,
                               Label waiting, Label using, XYChart.Series<String, Integer> series) {
        usage.setText(data.usage().toString());
        available.setText(data.available().toString());
        waiting.setText(String.join(";", data.waiting().stream().map(String::valueOf).toList()));
        using.setText(String.join(";", data.waiting().stream().map(String::valueOf).toList()));
        series.getData().add(new XYChart.Data<>(String.valueOf(OS.clock.get()), data.usage()));
        if (overviewSeries.getData().size() > 60) {
            overviewSeries.getData().removeFirst();
        }
    }
}
