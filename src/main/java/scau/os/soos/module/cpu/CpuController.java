package scau.os.soos.module.cpu;

import scau.os.soos.common.OS;
import scau.os.soos.common.enums.INTERRUPT;
import scau.os.soos.module.Module;
import scau.os.soos.module.process.model.Process;

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

    public void interrupt(INTERRUPT interruptType, Process process){
        cpuService.setInterrupt(interruptType);
    }

    /**
     * 处理进程
     */
    public void handleProcess(Process process){

    }

    /**
     * 设置CPU上下文
     * @param ax
     * @param pc
     * @param psw
     */
    public void setContext(int ax,int pc,int psw) {

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
