package scau.os.soos.module.cpu;

import scau.os.soos.common.OS;
import scau.os.soos.common.enums.INTERRUPT;
import scau.os.soos.module.cpu.model.Register;
import scau.os.soos.module.process.ProcessController;

public class CpuService {
    private final Register reg;

    public CpuService() {
        reg = new Register();
    }

    public void executeInstruction() {
        //...
        System.out.println("执行指令");
    }

    public void executeInterrupt() {
        //中断检测
        if ((OS.PSW & 0b001) > 0) {
            handleProgramEndInterrupt();
        }
        if ((OS.PSW & 0b010) > 0) {
            handleTimeSliceEndInterrupt();
        }
        if ((OS.PSW & 0b100) > 0) {
            handleIOInterrupt();
        }

    }

    private void handleProgramEndInterrupt() {

    }

    private void handleTimeSliceEndInterrupt() {
        System.out.println("时间片结束");
        ProcessController.getInstance().schedule();
        OS.clearInterrupt(INTERRUPT.TimeSliceEnd);
    }

    private void handleIOInterrupt() {

    }
}
