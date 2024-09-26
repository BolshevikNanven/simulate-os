package scau.os.soos.module.process;

import scau.os.soos.common.enums.INTERRUPT;
import scau.os.soos.module.cpu.CpuController;

import java.util.ArrayList;

public class ProcessService {

    // 就绪队列
    private ArrayList<Process> readyQueue = new ArrayList<>();

    public ProcessService() {

    }

    public void clockSchedule() {
        // 判断就绪队列是否为空

        // 判断cpu空闲

        // 调度进程上处理机

    }

    public void processSchedule() {
        System.out.println("调度新进程");
    }


}
