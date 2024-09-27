package scau.os.soos.module.process.model;

import scau.os.soos.common.enums.DEVICE_TYPE;

public class Process {
    private PCB pcb;
    private final int MAX_CLOCK = 6;
    private int timeSlice; // 时间片
    private DEVICE_TYPE deviceType; // 正在使用设备类型
    public Process() {
        //this.pcb = pcb;
        this.timeSlice = MAX_CLOCK;
        this.deviceType = null;
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
}
