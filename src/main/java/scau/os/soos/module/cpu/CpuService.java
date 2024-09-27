package scau.os.soos.module.cpu;

import scau.os.soos.common.OS;
import scau.os.soos.common.enums.CPU_STATES;
import scau.os.soos.common.enums.DEVICE_TYPE;
import scau.os.soos.common.enums.INTERRUPT;
import scau.os.soos.module.cpu.model.Register;
import scau.os.soos.module.device.DeviceController;
import scau.os.soos.module.process.ProcessController;
import scau.os.soos.module.process.model.Process;

public class CpuService {
    private final Register reg;                                             // 寄存器

    private CPU_STATES cpuState;                                            // CPU状态

    private final Process[] interruptSource;                                // 中断源

    private Process runningProcess;                                   // 运行进程



    public CpuService() {
        reg = new Register();
        cpuState = CPU_STATES.IDLE;
        interruptSource = new Process[3];
        runningProcess = null;
    }

    /**
     * 中断请求
     * @param interruptType 中断类型
     * @param process 中断源
     * @return 是否成功请求中断
     */
    public boolean requestInterrupt(INTERRUPT interruptType, Process process){
        if((reg.getPSW() & (1 << interruptType.ordinal())) > 0){
            return false;
        }
        setInterrupt(interruptType);
        interruptSource[interruptType.ordinal()] = process;
        return true;
    }

    public void executeInstruction() {
        //...
        System.out.println("执行指令 clock:"+ OS.clock.get());
        reg.incPC(); // PC ++
    }

    /**
     * 检测中断
     */
    public void detectInterrupt() {
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

    /**
     * 程序结束中断处理
     */
    private void handleProgramEndInterrupt() {
        System.out.println("中断-程序结束");
        unload();
        ProcessController.getInstance().destroy(interruptSource[0]);
        clearInterrupt(INTERRUPT.ProgramEnd);
        clearInterrupt(INTERRUPT.TimeSliceEnd);
    }

    /**
     * 时间片结束中断处理
     */
    private void handleTimeSliceEndInterrupt() {
        System.out.println("中断-时间片结束");
        unload();
        ProcessController.getInstance().schedule();
        clearInterrupt(INTERRUPT.TimeSliceEnd);
    }

    /**
     * IO中断处理
     */
    private void handleIOInterrupt() {
        System.out.println("中断-IO中断");

        // TODO 要不要发出进程调度
        clearInterrupt(INTERRUPT.IO);
    }


    /**
     * 设置PSW中的中断标志
     * @param interruptType 中断类型
     */
    public void setInterrupt(INTERRUPT interruptType) {
        reg.setPSW(reg.getPSW() | (1 << interruptType.ordinal()));
    }

    /**
     * 清除PSW中的中断标志
     * @param interruptType 中断类型
     */
    public void clearInterrupt(INTERRUPT interruptType) {
        reg.setPSW(reg.getPSW() & ~(1 << interruptType.ordinal()));
    }

    public void setCpuState(CPU_STATES cpuState) {
        this.cpuState = cpuState;
    }

    public CPU_STATES getCpuState() {
        return cpuState;
    }

    /**
     * 八位二进制指令译码
     * 0001 aaaa -> x = aaaa
     * 0010 0000 -> x ++
     * 0011 0000 -> x --
     * 0100 bbcc -> !bbcc (bb设备使用时间 cc设备类型 00 01 10 -> A B C)
     * 0101 0000 -> end
     * ...
     */
    public void decodeInstruction() {
        int instruction = reg.getIR();
        int op = instruction >> 4;
        int tmp = instruction & 0b00001111;
        switch (op) {
            case 0b0001 -> {reg.setAX(reg.getAX() + tmp);}
            case 0b0010 -> {reg.incAX();}
            case 0b0011 -> {reg.decAX();}
            case 0b0100 -> {
                int time = tmp >> 2;
                int device = tmp & 0b0011;
                DEVICE_TYPE deviceType = DEVICE_TYPE.ordinalToDeviceType(device);
                DeviceController.getInstance().assign(deviceType,time, runningProcess);
                ProcessController.getInstance().block(runningProcess);
                unload();
                // TODO 进程调度 ：cpu 还是 进程管理 ？
            }
            case 0b0101 -> {
                System.out.println("程序结束");
                requestInterrupt(INTERRUPT.ProgramEnd, runningProcess);
            }
        }
    }

    /**
     * 进程下处理机
     */
    public void unload(){
        runningProcess = null;
        cpuState = CPU_STATES.IDLE;
    }
}
