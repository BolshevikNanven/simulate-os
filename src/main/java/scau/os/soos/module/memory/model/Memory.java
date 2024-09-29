package scau.os.soos.module.memory.model;

import scau.os.soos.module.process.model.PCB;

import java.util.List;

public class Memory {
    private final int MEMORY_SIZE = 512;
    private final int total;
    private int used;
    private final PCB[] systemArea;
    private final int[] userArea;
    private MemoryBlock headBlock;

    public Memory() {
        total = MEMORY_SIZE;
        used = 0;
        systemArea = new PCB[10];
        userArea = new int[MEMORY_SIZE];
        headBlock = new MemoryBlock(0, MEMORY_SIZE, true);
    }

    public int getTotal() {
        return total;
    }

    public int getUsed() {
        return used;
    }

    public void setUsed(int used) {
        this.used = used;
    }

    public void setHeadBlock(MemoryBlock headBlock) {
        this.headBlock = headBlock;
    }

    public PCB getSystemArea(int index) {
        return systemArea[index];
    }

    public void setSystemArea(int index, PCB pcb) {
        systemArea[index] = pcb;
    }

    public int getUserArea(int index) {
        return userArea[index];
    }

    public void setUserArea(int index, int instruction) {
        userArea[index] = instruction;
    }

    public MemoryBlock getHeadBlock() {
        return headBlock;
    }
}
