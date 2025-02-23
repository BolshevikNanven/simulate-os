package scau.os.soos.module.process.model;

import java.util.LinkedList;
import java.util.Queue;
import java.util.stream.Stream;

public class BlockingQueue {
    private final Queue<PCB> blockingQueue;

    public BlockingQueue() {
        this.blockingQueue = new LinkedList<>();
    }

    public boolean offerPCB(Process process) {
        return this.blockingQueue.offer(process.getPCB());
    }

    public boolean isEmpty() {
        return this.blockingQueue.isEmpty();
    }

    public boolean removePCB(PCB pcb) {
        return this.blockingQueue.remove(pcb);
    }
    public int size(){
        return blockingQueue.size();
    }
    public Stream<PCB> stream() {
        return blockingQueue.stream();
    }
}
