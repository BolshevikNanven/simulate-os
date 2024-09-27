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

    public boolean allocate(PCB pcb,int size) {
        return false;
    }

    public boolean free(PCB pcb) {
        return false;
    }

    public int read(int address) {
        return 0;
    }

    public boolean write(int address, Object content) {
        return false;
    }

    @Override
    public void run() {

    }
}
