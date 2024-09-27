package scau.os.soos.module.process.model;

import scau.os.soos.common.enums.DEVICE_TYPE;
import scau.os.soos.common.enums.PROCESS_STATES;

public class Process {
    // 进程控制块
    private PCB pcb;
    // 进程最大时间片
    private final int MAX_CLOCK = 6;
    // 时间片
    private int timeSlice;
    // 正在使用设备类型
    private DEVICE_TYPE deviceType;

    public Process(PCB pcb,int pid) {
        this.pcb = pcb;
        this.pcb.initPid(pid);
        this.timeSlice = MAX_CLOCK;
        this.deviceType = null;
    }

    public PCB getPCB() {
        return pcb;
    }

    public void decTimeSlice(int timeSlice) {
//        this.timeSlice --;
//        if (timeSlice == 0) {
//            CpuController.getInstance().interrupt(INTERRUPT.TimeSliceEnd,this);
//        }
    }

    private void resetTimeSlice(){
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
