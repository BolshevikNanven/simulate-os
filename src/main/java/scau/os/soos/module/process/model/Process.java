package scau.os.soos.module.process.model;

import scau.os.soos.common.enums.DEVICE_TYPE;
import scau.os.soos.common.enums.INTERRUPT;
import scau.os.soos.common.enums.PROCESS_STATES;
import scau.os.soos.module.cpu.CpuController;

public class Process {
    // 进程控制块
    private PCB pcb;
    // 进程最大时间片
    private final int MAX_CLOCK = 6;
    // 时间片
    private int timeSlice;
    // 正在使用设备类型
    private DEVICE_TYPE deviceType;

    public Process(PCB pcb, int pid) {
        this.pcb = pcb;
        this.pcb.initPCB(pid,this);
        this.timeSlice = MAX_CLOCK;
        this.deviceType = null;
    }

    public PCB getPCB() {
        return pcb;
    }

    public void decTimeSlice() {
        // 如果进程为运行状态，则时间片减一
        if(this.getPCB().getStatus()== PROCESS_STATES.RUNNING){
            timeSlice --;
            if (timeSlice == 0) {
                // 时间片用完，请求中断处理
                CpuController.getInstance().requestInterrupt(INTERRUPT.TimeSliceEnd,this);
                resetTimeSlice();
            }
        }
    }

    private void resetTimeSlice() {
        this.timeSlice = MAX_CLOCK;
    }

    public DEVICE_TYPE getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(DEVICE_TYPE deviceType) {
        this.deviceType = deviceType;
    }

    public PCB getPcb() {
        return pcb;
    }

}
