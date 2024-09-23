package scau.os.soos.module.cpu;

import scau.os.soos.common.OS;
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

    @Override
    public void run() {
        while (true) {
            //中断检测
            cpuService.executeInterrupt();
            //执行指令
            cpuService.executeInstruction();
            //时钟加一
            OS.clock.inc();

            //测试用
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
