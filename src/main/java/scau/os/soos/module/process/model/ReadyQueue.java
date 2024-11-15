package scau.os.soos.module.process.model;

import java.util.LinkedList;
import java.util.Queue;
import java.util.stream.Stream;

public class ReadyQueue {
    private final Queue<PCB> readyQueue;

    public ReadyQueue() {
        this.readyQueue = new LinkedList<>();
    }

    public boolean offerPCB(Process process) {
        return this.readyQueue.offer(process.getPCB());
    }

    public PCB pollPCB() {
        return this.readyQueue.poll();
    }

    public int size() {
        return readyQueue.size();
    }
    public Stream<PCB> stream() {
        return readyQueue.stream();
    }
}


