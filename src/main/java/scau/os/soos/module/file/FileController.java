package scau.os.soos.module.file;

import scau.os.soos.module.Module;
import scau.os.soos.module.device.DeviceController;
import scau.os.soos.module.device.DeviceService;
import scau.os.soos.module.file.model.MyFile;


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

    /**
     * 创建文件
     */
    public void createFile() {}

    /**
     * 删除文件
     */
    public void deleteFile() {}

    /**
     * 写文件
     */
    public void writeFile() {}

    /**
     * 读文件
     */
    public Object readFile(MyFile myFile) {
        return fileService.readFile(myFile);
    }

    /**
     * 拷贝文件
     */
    public void copyFile() {}

    /**
     * 建立目录
     */
    public void createDirectory() {}

    /**
     * 删除空目录
     */
    public void deleteDirectory() {}

    /**
     * 获取文件大小
     */
    public int getFileSize(MyFile myFile){
        return fileService.getFileSize(myFile);
    }

    @Override
    public void run() {

    }
}
