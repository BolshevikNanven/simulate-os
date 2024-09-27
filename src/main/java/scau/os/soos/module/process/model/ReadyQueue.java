package scau.os.soos.module.process.model;

import java.util.LinkedList;
import java.util.Queue;

public class ReadyQueue {
    private final Queue<PCB> readyQueue;

    public ReadyQueue(){
        this.readyQueue = new LinkedList<>();
    }

    public boolean offerProcess(PCB process){
        return this.readyQueue.offer(process);
    }

    public boolean isEmpty() {
        return this.readyQueue.isEmpty();
    }

    public PCB pollProcess() {
        return this.readyQueue.poll();
    }

    public Queue<PCB> getReadyQueue() {
        return this.readyQueue;
    }
}
