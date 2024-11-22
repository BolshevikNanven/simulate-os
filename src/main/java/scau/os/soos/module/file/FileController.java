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
    public Item createFile(String path) throws ItemAlreadyExistsException, ItemNotFoundException, DiskSpaceInsufficientException, IllegalOperationException {
        return fileService.createFile(path);
    }

    /**
     * 删除文件
     */
    public void deleteFile(String path) throws ItemNotFoundException, IllegalOperationException {
        try {
            fileService.delete(path, false, true);
        } catch (DirectoryNoEmptyException ignored) {

        }
    }

    /**
     * 写文件
     */
    public void writeFile(Item file) throws DiskSpaceInsufficientException {
        fileService.writeFile(file);
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
    public void copyFile(String sourcePath, String targetPath) throws ItemAlreadyExistsException, DiskSpaceInsufficientException, ItemNotFoundException, IllegalOperationException {
        fileService.copy(sourcePath, targetPath, false);
    }

    /**
     * 移动文件
     */
    public void moveFile(String sourcePath, String targetPath) throws ItemAlreadyExistsException, DiskSpaceInsufficientException, ItemNotFoundException, IllegalOperationException {
        fileService.copy(sourcePath, targetPath, true);
    }

    /**
     * 建立目录
     */
    public Item createDirectory(String path) throws ItemAlreadyExistsException, ItemNotFoundException, DiskSpaceInsufficientException, IllegalOperationException {
        return fileService.createDirectory(path);
    }

    /**
     * 删除空目录
     */
    public void deleteEmptyDirectory(String path) throws ItemNotFoundException, DirectoryNoEmptyException, IllegalOperationException {
        fileService.delete(path, true, false);
    }

    /**
     * 删除目录
     */
    public void deleteDirectory(String path) throws ItemNotFoundException, IllegalOperationException {
        try {
            fileService.delete(path, true, true);
        } catch (DirectoryNoEmptyException ignored) {

        }
    }

    /**
     * 分区
     */
    public void partitionDisk(String src, String dec, int size) throws IllegalOperationException, DiskSpaceInsufficientException, MaxCapacityExceededException, ItemNotFoundException {
        fileService.diskPartition(src, dec, size);
    }

    public void reName(String path, String newName) throws ItemAlreadyExistsException, IllegalOperationException, ItemNotFoundException {
        fileService.reName(path, newName);
    }

    public void reAttribute(String path, boolean readOnly, boolean systemFile, boolean regularFile, boolean isDirectory) throws IllegalOperationException, ItemNotFoundException {
        fileService.reAttribute(path, readOnly, systemFile, regularFile, isDirectory);
    }

    public Item findItem(String path, FILE_TYPE type) throws ItemNotFoundException {
        return fileService.isItemNotFound(path,type);
    }

    public Item findItem(String path) throws IllegalOperationException, ItemNotFoundException {
        return fileService.findItem(path);
    }

    public boolean isExistedDirectory(String path) throws ItemNotFoundException {
        return findItem(path, FILE_TYPE.DIRECTORY) != null;
    }

    // 待写 格式化硬盘
    public void formatDisk(String path) throws IllegalOperationException, ItemNotFoundException {
        fileService.formatDisk(path);
    }

    // 待写 返回文件内容
    public String typeFile(String path) {
        return fileService.typeFile(path);
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

    public void save() {
        FileService.getDisk().disk2file();
    }

    @Override
    public void run() {

    }

    public static void main(String[] args) {
        FileController.getInstance();
//        Directory root;
//        Directory C;
//        try {
//        root = (Directory) getInstance().findItem("/", FILE_TYPE.DIRECTORY);
////        FileService.getDisk().test();
//        C = (Directory)  getInstance().findItem("/C:", FILE_TYPE.DIRECTORY);
//        System.out.println("---");
////        System.out.println(getInstance().fileService.getDisk().);
//
//            getInstance().createDirectory("/C:/a");
//            getInstance().createDirectory("/C:/b");
//            Directory a = (Directory)  getInstance().findItem("/C:/a", FILE_TYPE.DIRECTORY);
//            Directory b = (Directory)  getInstance().findItem("/C:/b", FILE_TYPE.DIRECTORY);
//            System.out.println(a.getPath());
//            System.out.println(b.getPath());
//        } catch (ItemAlreadyExistsException | ItemNotFoundException | DiskSpaceInsufficientException |
//                 IllegalOperationException e) {
//            throw new RuntimeException(e);
//        }
//        System.out.println(root.getPath());
//        System.out.println(root.getChildren());
//        System.out.println(C.getChildren());
//        try {
//            getInstance().fileService.diskPartition("/C:", "/D:",10);
//            getInstance().fileService.diskPartition("/C:", "/D:",10);
//            getInstance().fileService.diskPartition("/C:", "/E:",10);
//            getInstance().fileService.diskPartition("/C:", "/D:",10);
//
//            getInstance().fileService.diskPartition("/E:", "/D:",10);
//        } catch (IllegalOperationException | ItemNotFoundException | DiskSpaceInsufficientException |
//                 MaxCapacityExceededException e) {
//            throw new RuntimeException(e);
//        }
//        try {
//            Item itm = getInstance().fileService.findItem("/C:/t0.t");
//            System.out.println(itm.getStartBlockNum());
//        } catch (IllegalOperationException e) {
//            throw new RuntimeException(e);
//        } catch (ItemNotFoundException e) {
//            throw new RuntimeException(e);
//        }
        FileService.getDisk().test();
//        getInstance().save();
    }
}
