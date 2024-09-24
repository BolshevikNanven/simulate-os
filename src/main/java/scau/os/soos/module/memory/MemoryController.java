package scau.os.soos.module.memory;

import scau.os.soos.module.Module;

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

    @Override
    public void run() {

    }
}
