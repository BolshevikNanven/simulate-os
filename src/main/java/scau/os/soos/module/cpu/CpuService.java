package scau.os.soos.module.cpu;

import scau.os.soos.common.OS;
import scau.os.soos.common.enums.CPU_STATES;
import scau.os.soos.common.enums.INTERRUPT;
import scau.os.soos.module.cpu.model.Register;
import scau.os.soos.module.process.ProcessController;

public class CpuService {
    private final Register reg;

    private CPU_STATES cpuState = CPU_STATES.IDLE;

    public CpuService() {
        reg = new Register();
    }

    public void executeInstruction() {
        //...
        System.out.println("执行指令 clock:"+ OS.clock.get());
    }

    public void executeInterrupt() {
        //中断检测
        if ((reg.getPSW() & 0b001) > 0) {
            handleProgramEndInterrupt();
        }
        if ((reg.getPSW() & 0b010) > 0) {
            handleTimeSliceEndInterrupt();
        }
        if ((reg.getPSW() & 0b100) > 0) {
            handleIOInterrupt();
        }

    }

    private void handleProgramEndInterrupt() {

    }

    private void handleTimeSliceEndInterrupt() {
        System.out.println("时间片结束");
        ProcessController.getInstance().schedule();
        clearInterrupt(INTERRUPT.TimeSliceEnd);
    }

    private void handleIOInterrupt() {
        System.out.println("IO中断");
        ProcessController.getInstance().schedule();
        clearInterrupt(INTERRUPT.IO);
    }

    // 设置PSW中的中断标志
    public void setInterrupt(INTERRUPT interruptType) {
        reg.setPSW(reg.getPSW() | (1 << interruptType.ordinal()));
    }

    // 清除PSW中的中断标志
    public void clearInterrupt(INTERRUPT interruptType) {
        reg.setPSW(reg.getPSW() & ~(1 << interruptType.ordinal()));
    }

    public void setCpuState(CPU_STATES cpuState) {
        this.cpuState = cpuState;
    }

    public CPU_STATES getCpuState() {
        return cpuState;
    }
}
