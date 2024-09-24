package scau.os.soos.module.cpu;

import scau.os.soos.common.OS;
import scau.os.soos.common.enums.INTERRUPT;
import scau.os.soos.module.Module;

public class CpuController implements Module {
    private static CpuController instance;
    private final CpuService cpuService;

    public static CpuController getInstance() {
        if (instance == null) {
            instance = new CpuController();
        }
        return instance;
    }

    private CpuController() {
        cpuService = new CpuService();
    }

    public void setInterrupt(INTERRUPT interruptType) {
        cpuService.setInterrupt(interruptType);
    }

    @Override
    public void run() {
        OS.clock.bind(() -> {
            //中断检测
            cpuService.executeInterrupt();
            //执行指令
            cpuService.executeInstruction();
        });
    }
}
