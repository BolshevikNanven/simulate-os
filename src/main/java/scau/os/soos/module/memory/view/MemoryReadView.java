package scau.os.soos.module.memory.view;

import scau.os.soos.module.memory.model.MemoryBlock;

import java.util.List;

public record MemoryReadView(Integer total, Integer usage, Integer available, Integer pcb,
                             List<MemoryBlock> memoryBlockList) {

}
