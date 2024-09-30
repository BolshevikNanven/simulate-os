package scau.os.soos.module.memory;

import scau.os.soos.module.memory.model.Memory;
import scau.os.soos.module.memory.model.MemoryBlock;
import scau.os.soos.module.process.model.PCB;

public class MemoryService {
    private final Memory memory;

    public MemoryService() {
        memory = new Memory();
    }

    public boolean allocateMemory(PCB pcb, int size) {
        MemoryBlock block = memory.getHeadBlock();
        while (block != null) {
            // 寻找空闲块
            if (!block.isFree() || block.getSize() < size) {
                block = block.getNext();
                continue;
            }
            // 尝试分配 PCB到系统区
            if (!allocatePCB(pcb)) {
                return false;
            }

            MemoryBlock memoryBlock;
            if (block.getSize() == size) {
                // 直接使用空闲进程块
                memoryBlock = block;
                memoryBlock.setFree(false);
            } else {
                memoryBlock = splitMemoryBlock(size, block);
            }
            memoryBlock.setPid(pcb.getPid());

            memory.setUsed(memory.getUsed() + size);

            //设置基址
            pcb.setPC(memoryBlock.getAddress());

            return true;
        }
        return false;
    }

    public boolean freeMemory(PCB pcb) {
        MemoryBlock memoryBlock = getMemoryBlock(pcb);

        if (memoryBlock == null) {
            return false;
        }
        if (!freePCB(pcb)) {
            return false;
        }
        freeMemoryBlock(memoryBlock);

        return true;
    }

    public boolean write(int address, int content) {
        memory.setUserArea(address, content);
        return true;
    }

    public int read(int address) {
        return memory.getUserArea(address);
    }

    private MemoryBlock splitMemoryBlock(int size, MemoryBlock target) {
        MemoryBlock memoryBlock = new MemoryBlock(target.getAddress(), size, false);
        memoryBlock.setAddress(target.getAddress());

        target.setSize(target.getSize() - size);
        target.setAddress(target.getAddress() + size);

        //连接上一块
        if (target.getPre() != null) {
            memoryBlock.setPre(target.getPre());
            target.getPre().setNext(memoryBlock);
        } else {
            memoryBlock.setPre(null);
            memory.setHeadBlock(memoryBlock);
        }

        memoryBlock.setNext(target);
        target.setPre(memoryBlock);

        return memoryBlock;
    }

    private boolean allocatePCB(PCB pcb) {
        for (int i = 0; i < 10; i++) {
            if (memory.getSystemArea(i) == null) {
                memory.setSystemArea(i, pcb);
                return true;
            }
        }
        return false;
    }

    private boolean freePCB(PCB pcb) {
        for (int i = 0; i < 10; i++) {
            if (memory.getSystemArea(i).getPid() == pcb.getPid()) {
                memory.setSystemArea(i, null);
                return true;
            }
        }
        return false;
    }

    private void freeMemoryBlock(MemoryBlock target) {
        target.setFree(true);
        memory.setUsed(memory.getUsed() - target.getSize());

        MemoryBlock preBlock = target.getPre();
        MemoryBlock nextBlock = target.getNext();
        // 合并上一块
        if (preBlock != null && preBlock.isFree()) {
            target.setSize(target.getSize() + preBlock.getSize());
            target.setAddress(preBlock.getAddress());
            target.setPre(preBlock.getPre());

            if (preBlock.getPre() != null) {
                preBlock.getPre().setNext(target);
            }

            preBlock.setNext(null);
            preBlock.setPre(null);
        }
        // 合并下一块
        if (nextBlock != null && nextBlock.isFree()) {
            target.setSize(target.getSize() + nextBlock.getSize());
            target.setNext(nextBlock.getNext());

            if (nextBlock.getNext() != null) {
                nextBlock.getNext().setPre(target);
            }

            nextBlock.setNext(null);
            nextBlock.setPre(null);
        }

    }

    private MemoryBlock getMemoryBlock(PCB pcb) {
        MemoryBlock memoryBlock = memory.getHeadBlock();
        while (memoryBlock != null) {
            if (memoryBlock.getPid() == pcb.getPid()) {
                return memoryBlock;
            }
            memoryBlock = memoryBlock.getNext();
        }
        return null;
    }

    public void printMemory() {
        MemoryBlock block = memory.getHeadBlock();
        while (block != null) {
            if (block.isFree()) {
                System.out.printf("内存块 空 size:%d %.2f/100 \n", block.getSize(), ((block.getSize() * 1.0) / (memory.getTotal())) * 100);
            } else {
                System.out.printf("内存块 pid:%d size:%d %.2f/100 \n", block.getPid(), block.getSize(), ((block.getSize() * 1.0) / (memory.getTotal())) * 100);
            }
            block = block.getNext();
        }
        System.out.println("使用了：" + memory.getUsed());
    }
}
