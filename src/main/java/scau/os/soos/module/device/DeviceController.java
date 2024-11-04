package scau.os.soos.module.device;

import scau.os.soos.common.OS;
import scau.os.soos.common.enums.DEVICE_TYPE;
import scau.os.soos.module.Module;
import scau.os.soos.module.device.view.DeviceOverviewReadView;
import scau.os.soos.module.device.view.DeviceReadView;
import scau.os.soos.module.process.model.PCB;
import scau.os.soos.module.process.model.Process;

import java.util.List;
import java.util.Map;

public class DeviceController implements Module {
    private static DeviceController instance;
    private final DeviceService deviceService;

    public static DeviceController getInstance() {
        if (instance == null) {
            instance = new DeviceController();
        }
        return instance;
    }

    private DeviceController() {
        deviceService = new DeviceService();
    }

    /**
     * 设备分配
     *
     * @param deviceType 设备类型
     * @param time       分配时间
     * @param process    分配对象
     */
    public void assign(DEVICE_TYPE deviceType, int time, Process process) {
        deviceService.assignDevice(deviceType, time, process);
    }

    public DeviceOverviewReadView getOverview() {
        return deviceService.overview();
    }

    public Map<DEVICE_TYPE, DeviceReadView> getData() {
        return deviceService.analyse();
    }

    @Override
    public void run() {
        OS.clock.bind(deviceService::checkDevice);
    }

    public static void main(String[] args) {
        getInstance().assign(DEVICE_TYPE.B, 6, new Process(new PCB(), 1));
        getInstance().assign(DEVICE_TYPE.B, 10, new Process(new PCB(), 2));
        getInstance().assign(DEVICE_TYPE.B, 6, new Process(new PCB(), 3));

        while (true) {
            getInstance().deviceService.checkDevice();
            getInstance().deviceService.printDevices();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
