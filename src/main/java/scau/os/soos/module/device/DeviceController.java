package scau.os.soos.module.device;

import scau.os.soos.common.OS;
import scau.os.soos.module.Module;

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

    @Override
    public void run() {
        OS.clock.bind(deviceService::checkDevice);
    }
}
