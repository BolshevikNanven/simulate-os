package scau.os.soos.module.memory;

import scau.os.soos.module.Module;
import scau.os.soos.module.process.model.PCB;

public class MemoryController implements Module {
    private static MemoryController instance;
    private final MemoryService memoryService;

    public static MemoryController getInstance() {
        if (instance == null) {
            instance = new MemoryController();
        }
        return instance;
    }

    private MemoryController() {
        memoryService = new MemoryService();
    }

    public boolean allocate(PCB pcb, int size) {
        return memoryService.allocateMemory(pcb, size);
    }

    public boolean free(PCB pcb) {
        return memoryService.freeMemory(pcb);
    }

    public int read(int address) {
        return memoryService.read(address);
    }

    public boolean write(int address, Object content) {
        return memoryService.write(address, (int) content);
    }

    @Override
    public void run() {

    }

    public static void main(String[] args) {
        PCB pcb1 = new PCB();
        PCB pcb2 = new PCB();
        PCB pcb3 = new PCB();

        getInstance().allocate(pcb1, 128);
        getInstance().allocate(pcb2, 44);
        getInstance().allocate(pcb3, 15);

//        getInstance().free(pcb2);
//        getInstance().free(pcb1);

        getInstance().memoryService.printMemory();
    }
}
