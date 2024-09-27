package scau.os.soos.module.process.model;

import java.util.LinkedList;
import java.util.Queue;

public class BlockingQueue {
    private final Queue<PCB> blockingQueue;

    public BlockingQueue(){
        this.blockingQueue = new LinkedList<>();
    }

    public boolean offerProcess(PCB process){
        return this.blockingQueue.offer(process);
    }

    public boolean isEmpty() {
        return this.blockingQueue.isEmpty();
    }

    public PCB pollProcess() {
        return this.blockingQueue.poll();
    }

    public Queue<PCB> getBlockingQueue() {
        return this.blockingQueue;
    }
}
