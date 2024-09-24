package scau.os.soos.module.device;

import scau.os.soos.common.enums.INTERRUPT;
import scau.os.soos.module.cpu.CpuController;

import java.util.Random;

public class DeviceService {
    private int test;

    public DeviceService() {
        Random random = new Random();
        test = random.nextInt(1, 10);
    }

    public void checkDevice() {
        test -= 1;
        if (test == 0) {
            CpuController.getInstance().setInterrupt(INTERRUPT.IO);
            Random random = new Random();
            test = random.nextInt(1, 10);
        }
    }
}
