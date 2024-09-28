package scau.os.soos.module.cpu;

import scau.os.soos.common.OS;
import scau.os.soos.common.enums.CPU_STATES;
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

    /**
     * 请求中断服务
     * @param interruptType 中断类型
     * @param process 中断源
     * @return true:成功 false:失败
     */
    public boolean requestInterrupt(INTERRUPT interruptType, Process process){
        return cpuService.requestInterrupt(interruptType,process);
    }

    /**
     * 处理进程
     */
    public boolean handleProcess(Process process){
        return cpuService.handleProcess(process);
    }



    public CPU_STATES getCpuState(){
        return cpuService.getCpuState();
    }

    public Process getCurrentProcess(){
        return cpuService.getCurrentProcess();
    }
    @Override
    public void run() {
        OS.clock.bind(() -> {
            //中断检测
            cpuService.detectInterrupt();
            //空闲则发起调度进程
            cpuService.executeInstruction();
        });
    }
}
