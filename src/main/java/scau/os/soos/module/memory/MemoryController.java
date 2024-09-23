package scau.os.soos.module.memory;

import scau.os.soos.common.OS;
import scau.os.soos.module.Module;
import scau.os.soos.module.file.FileController;
import scau.os.soos.module.file.FileService;

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
