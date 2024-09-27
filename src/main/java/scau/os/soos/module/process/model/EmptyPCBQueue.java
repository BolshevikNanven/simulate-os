package scau.os.soos.module.process.model;

import scau.os.soos.module.cpu.CpuController;

import java.util.LinkedList;
import java.util.Queue;

public class EmptyPCBQueue {
    private static final int PCB_COUNT = 10;
    private final Queue<PCB> emptyPCBQueue;

    public EmptyPCBQueue() {
        this.emptyPCBQueue = new LinkedList<>();
        for (int i = 0; i < PCB_COUNT; i++) {

        }
    }
}
