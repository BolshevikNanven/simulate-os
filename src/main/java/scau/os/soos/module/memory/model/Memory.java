package scau.os.soos.module.memory.model;

import scau.os.soos.module.process.model.PCB;

import java.util.List;

public class Memory {
    private List<PCB> systemArea;
    private int[] userArea;
    private MemoryBlock memoryBlock;
}
