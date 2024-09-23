package scau.os.soos.module.file;

import scau.os.soos.module.Module;
import scau.os.soos.module.device.DeviceController;
import scau.os.soos.module.device.DeviceService;

public class FileController implements Module {
    private static FileController instance;
    private final FileService fileService;

    public static FileController getInstance() {
        if (instance == null) {
            instance = new FileController();
        }
        return instance;
    }

    private FileController() {
        fileService = new FileService();
    }
    @Override
    public void run() {

    }
}
