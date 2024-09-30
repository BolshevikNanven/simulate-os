package scau.os.soos.module.cpu;

import scau.os.soos.common.OS;
import scau.os.soos.common.enums.CPU_STATES;
import scau.os.soos.common.enums.DEVICE_TYPE;
import scau.os.soos.common.enums.INTERRUPT;
import scau.os.soos.module.cpu.model.Register;
import scau.os.soos.module.device.DeviceController;
import scau.os.soos.module.memory.MemoryController;
import scau.os.soos.module.process.ProcessController;
import scau.os.soos.module.process.model.Process;

public class CpuService {
    private final Register reg;                                             // 寄存器

    private CPU_STATES cpuState;                                            // CPU状态

    private final Process[] interruptSource;                                // 中断源

    private Process runningProcess;                                         // 运行进程



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
        if((reg.getPSW() & (1 << interruptType.ordinal())) > 0 || process == null){
            System.out.println("CPU: 拒绝中断请求");
            return false;
        }

        setInterrupt(interruptType);
        interruptSource[interruptType.ordinal()] = process;

        System.out.println("CPU: 中断请求成功");
        return true;
    }

    /**
     * 执行指令
     * 空转则调度进程
     */
    public void executeInstruction() {
        if (runningProcess == null) {
            // 空转 -> 调度进程
            ProcessController.getInstance().schedule();
            if(runningProcess == null){
                System.out.println("CPU: 空闲");
                return;
            }
        }

        int pc = reg.getPC();
        reg.setIR(MemoryController.getInstance().read(pc)); // 读取指令
        decodeInstruction();                                // 指令译码
        reg.incPC();                                        // 更新PC

        //TODO: 设计空闲进程
    }


    /**
     * 处理进程
     * @param process
     */
    public boolean handleProcess(Process process) {
        if(runningProcess != null || process == null){
            return false;
        }
        runningProcess = process;
        cpuState = CPU_STATES.BUSY;
        System.out.println("CPU: 处理进程 " + process.getPCB().getPid());

        // 恢复CPU现场
        reg.setPC(process.getPCB().getPC()); // 创建进程pc要指向首地址
        reg.setAX(process.getPCB().getAX());
        return true;
    }

    /**
     * 检测中断
     */
    public void detectInterrupt() {
        if ((reg.getPSW() & 0b001) > 0) {
            handleProgramEndInterrupt();
        }
        else if ((reg.getPSW() & 0b010) > 0) {
            handleTimeSliceEndInterrupt();
        }
        else if ((reg.getPSW() & 0b100) > 0) {
            handleIOInterrupt();
        }
    }

    /**
     * 程序结束中断处理
     */
    private void handleProgramEndInterrupt() {
        System.out.println("CPU: 中断-程序结束");
        unload();
        Process process = interruptSource[0];
        if(process != null){
            ProcessController.getInstance().destroy(interruptSource[0]);
        }
        ProcessController.getInstance().schedule();

        clearInterrupt(INTERRUPT.ProgramEnd);
        clearInterrupt(INTERRUPT.TimeSliceEnd);
    }

    /**
     * 时间片结束中断处理
     */
    private void handleTimeSliceEndInterrupt() {
        System.out.println("CPU: 中断-时间片结束");
        // 保护CPU现场
        runningProcess.getPCB().setPC(reg.getPC());
        runningProcess.getPCB().setAX(reg.getAX());

        unload();

        if(runningProcess != null){
            ProcessController.getInstance().handoff(runningProcess);
        }
        ProcessController.getInstance().schedule();

        clearInterrupt(INTERRUPT.TimeSliceEnd);
    }

    /**
     * IO中断处理
     */
    private void handleIOInterrupt() {
        System.out.println("CPU: 中断-IO中断");
        Process process = interruptSource[2];
        if(process != null){
            ProcessController.getInstance().wake(interruptSource[2]);
        }

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
        System.out.println("CPU: 执行指令" + Integer.toBinaryString(instruction));
        int op = instruction >> 4;
        int tmp = instruction & 0b00001111;
        switch (op) {
            case 0b0001 -> {
                System.out.println("x = " + tmp);
                reg.setAX(tmp);
            }
            case 0b0010 -> {
                System.out.println("x ++");
                reg.incAX();
            }
            case 0b0011 -> {
                System.out.println("x --");
                reg.decAX();
            }
            case 0b0100 -> {
                int time = tmp >> 2;
                int device = tmp & 0b0011;
                DEVICE_TYPE deviceType = DEVICE_TYPE.ordinalToDeviceType(device);
                System.out.println("!"+time+","+deviceType);

                unload();

                ProcessController.getInstance().block(runningProcess);
                DeviceController.getInstance().assign(deviceType,time, runningProcess);
                ProcessController.getInstance().schedule();
            }
            case 0b0101 -> {
                System.out.println("end");
                requestInterrupt(INTERRUPT.ProgramEnd, runningProcess);
            }
            default -> {
                System.out.println("指令错误");
            }
        }
        System.out.println("x = " + reg.getAX());
    }



    /**
     * 进程下处理机
     */
    public void unload(){
        runningProcess = null;
        cpuState = CPU_STATES.IDLE;
    }

    public Process getCurrentProcess() {
        return this.runningProcess;
    }
}
