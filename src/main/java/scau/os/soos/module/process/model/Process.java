package scau.os.soos.module.process.model;

import scau.os.soos.common.enums.INTERRUPT;
import scau.os.soos.module.cpu.CpuController;

public class Process {
    private PCB pcb;
    private final int MAX_CLOCK = 6;
    private int timeSlice; // 时间片

    public Process() {
        //this.pcb = pcb;
        this.timeSlice = MAX_CLOCK;
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
}
