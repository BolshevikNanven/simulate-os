package scau.os.soos.module.file;

import scau.os.soos.common.enums.FILE_TYPE;
import scau.os.soos.module.Module;
import scau.os.soos.common.exception.*;
import scau.os.soos.module.file.model.Directory;
import scau.os.soos.module.file.model.Fat;
import scau.os.soos.module.file.model.Item;

import java.util.ArrayList;
import java.util.List;

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
    public Item createFile(String path) throws ItemAlreadyExistsException, ItemNotFoundException, DiskSpaceInsufficientException, IllegalPathException {
        return fileService.createFile(path);
    }

    /**
     * 删除文件
     */
    public void deleteFile(String path) throws ItemNotFoundException, IllegalPathException {
        try {
            fileService.delete(path, false, true);
        } catch (DirectoryNoEmptyException ignored) {

        }
    }

    /**
     * 写文件
     */
    public void writeFile(Item file, String content) throws DiskSpaceInsufficientException {
        fileService.writeFile(file, content);
    }

    /**
     * 读文件
     */
    public byte[] readFile(Item file) throws ItemNotFoundException {
        return fileService.readFile(file);
    }

    /**
     * 拷贝文件
     */
    public void copyFile(String sourcePath, String targetPath) throws ItemAlreadyExistsException, DiskSpaceInsufficientException, ItemNotFoundException, IllegalPathException {
        fileService.copy(sourcePath, targetPath, false);
    }

    /**
     * 移动文件
     */
    public void moveFile(String sourcePath, String targetPath) throws ItemAlreadyExistsException, DiskSpaceInsufficientException, ItemNotFoundException, IllegalPathException {
        fileService.copy(sourcePath, targetPath, true);
    }

    /**
     * 建立目录
     */
    public Item createDirectory(String path) throws ItemAlreadyExistsException, ItemNotFoundException, DiskSpaceInsufficientException, IllegalPathException {
        return fileService.createDirectory(path);
    }

    /**
     * 删除空目录
     */
    public void deleteEmptyDirectory(String path) throws ItemNotFoundException, DirectoryNoEmptyException, IllegalPathException {
        fileService.delete(path, true, false);
    }

    /**
     * 删除目录
     */
    public void deleteDirectory(String path) throws ItemNotFoundException, IllegalPathException {
        try {
            fileService.delete(path, true, true);
        } catch (DirectoryNoEmptyException ignored) {

        }
    }

    /**
     * 分区
     */
    public void partitionDisk(String src, String dec, int size) throws IllegalPathException, DiskSpaceInsufficientException, MaxCapacityExceededException, ItemNotFoundException {
        fileService.diskPartition(src, dec, size);
    }

    public void reName(String path, String newName) throws ItemAlreadyExistsException, IllegalNameException, IllegalPathException, ItemNotFoundException {
        fileService.reName(path, newName);
    }

    public void reAttribute(String path, boolean readOnly, boolean systemFile, boolean regularFile, boolean isDirectory) throws IllegalPathException, ItemNotFoundException {
        fileService.reAttribute(path, readOnly, systemFile, regularFile, isDirectory);
    }

    public Item findItem(String path, FILE_TYPE type) throws ItemNotFoundException {
        return fileService.findItem(path,type);
    }

    public Item findItem(String path) throws IllegalPathException, ItemNotFoundException {
        return fileService.findItem(path);
    }

    public boolean isExistedDirectory(String path) throws ItemNotFoundException {
        return findItem(path, FILE_TYPE.DIRECTORY) != null;
    }

    // 待写 格式化硬盘
    public void formatDisk(String path) throws IllegalPathException, ItemNotFoundException {
        fileService.formatDisk(path);
    }

    // 待写 返回文件内容
    public String typeFile(String path) {
        return "";
    }

    public int getFileSize(Item file) {
        return fileService.getSize(file);
    }

    public List<Item> listRoot() {
        return new ArrayList<>(getPartitionDirectory().getChildren());
    }

    public Directory getPartitionDirectory() {
        return FileService.getDisk().getPartitionDirectory();
    }

    public Fat getFat() {
        return FileService.getDisk().getFat();
    }

    @Override
    public void run() {

    }

    public static void main(String[] args) {
        FileController.getInstance();

        Directory root = (Directory) getInstance().fileService.find("/", FILE_TYPE.DIRECTORY);
//        FileService.getDisk().test();
        Directory C = (Directory)  getInstance().fileService.find("/C:", FILE_TYPE.DIRECTORY);
        System.out.println("---");
//        System.out.println(getInstance().fileService.getDisk().);
        try {
            getInstance().createDirectory("/C:/a");
            getInstance().createDirectory("/C:/b");
            Directory a = (Directory)  getInstance().fileService.find("/C:/a", FILE_TYPE.DIRECTORY);
            Directory b = (Directory)  getInstance().fileService.find("/C:/b", FILE_TYPE.DIRECTORY);
            System.out.println(a.getPath());
            System.out.println(b.getPath());
        } catch (ItemAlreadyExistsException | ItemNotFoundException | DiskSpaceInsufficientException |
                 IllegalPathException e) {
            throw new RuntimeException(e);
        }
        System.out.println(root.getPath());
        System.out.println(root.getChildren());
        System.out.println(C.getChildren());
        try {
            try {
                getInstance().fileService.diskPartition("/C:", "/D:",10);
                getInstance().fileService.diskPartition("/C:", "/D:",10);
                getInstance().fileService.diskPartition("/C:", "/E:",10);
                getInstance().fileService.diskPartition("/C:", "/D:",10);

                getInstance().fileService.diskPartition("/E:", "/D:",10);
            } catch (IllegalPathException | ItemNotFoundException | DiskSpaceInsufficientException |
                     MaxCapacityExceededException e) {
                throw new RuntimeException(e);
            }


//            getInstance().fileService.diskPartition("/C:", "/D:",50);
        } finally {

        }
        FileService.getDisk().test();


    }
}
