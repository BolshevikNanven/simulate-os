package scau.os.soos.module.process;

import scau.os.soos.common.OS;
import scau.os.soos.common.enums.INTERRUPT;

public class ProcessService {
    private final int MAX_CLOCK = 6;
    private int processClock;

    public ProcessService() {
        resetProcessClock();
    }

    public void clockSchedule() {
        processClock--;
        if (processClock == 0) {
            OS.setInterrupt(INTERRUPT.TimeSliceEnd);
        }
    }
    public void processSchedule(){
        System.out.println("调度新进程");
        resetProcessClock();
    }

    private void resetProcessClock() {
        processClock = MAX_CLOCK;
    }
}
