package scau.os.soos.module.process.model;

import scau.os.soos.common.enums.DEVICE_TYPE;

public class Process {
    // 进程控制块
    private PCB pcb;

    // 正在使用设备类型
    private DEVICE_TYPE deviceType;

    public Process(PCB pcb, int pid) {
        this.pcb = pcb;
        this.pcb.initPCB(pid,this);
        this.deviceType = null;
    }

    public PCB getPCB() {
        return pcb;
    }

    public void setDeviceType(DEVICE_TYPE deviceType) {
        this.deviceType = deviceType;
    }

    public DEVICE_TYPE getDeviceType() {
        return deviceType;
    }

}
