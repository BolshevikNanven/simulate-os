package scau.os.soos.module.device;

import scau.os.soos.common.OS;
import scau.os.soos.common.enums.DEVICE_TYPE;
import scau.os.soos.module.Module;
import scau.os.soos.module.process.model.Process;

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
     * @param deviceType 设备类型
     * @param time 分配时间
     * @param process 分配对象
     */
    public void assign(DEVICE_TYPE deviceType, int time, Process process) {}

    public int getCurrentFreeDeviceCount(DEVICE_TYPE deviceType) {
        return deviceService.getCurrentFreeDeviceCount(deviceType);
    }

    @Override
    public void run() {
        OS.clock.bind(deviceService::checkDevice);
    }
}
