package scau.os.soos.module.process.model;

import java.util.LinkedList;
import java.util.Queue;

public class EmptyPCBQueue {
    // 最大PCB数量
    private final int maxPCBCount;
    // 空白PCB队列
    private final Queue<PCB> emptyPCBQueue;

    public EmptyPCBQueue(int maxPCBCount) {
        this.maxPCBCount = maxPCBCount;
        this.emptyPCBQueue = new LinkedList<>();
        // 初始化空闲PCB队列
        for (int i = 0; i < maxPCBCount; i++) {
            emptyPCBQueue.add(new PCB());
        }
    }

    // 申请一个空白PCB
    public PCB applyEmptyPCB(){
        return emptyPCBQueue.poll();
    }

    // 归还一个空白PCB
    public boolean recycleEmptyPCB(){
        if(emptyPCBQueue.size() < maxPCBCount){
            return emptyPCBQueue.offer(new PCB());
        }
        return false;
    }
}
