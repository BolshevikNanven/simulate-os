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

    private final List<Notifier> listeners;

    public static FileController getInstance() {
        if (instance == null) {
            instance = new FileController();
        }
        return instance;
    }

    private FileController() {
        fileService = new FileService();
        listeners = new ArrayList<>();
    }

    public void bind(Notifier notifier) {
        listeners.add(notifier);
    }

    public void unBind(Notifier notifier) {
        listeners.remove(notifier);
    }

    public void notify(Item item){
        for(Notifier notifier : listeners) {
            notifier.update(item);
        }
    }

    /**
     * 创建文件
     */
    public Item createFile(String path) throws ItemAlreadyExistsException, ItemNotFoundException, DiskSpaceInsufficientException, IllegalOperationException, ReadOnlyFileModifiedException {
        return fileService.createFile(path);
    }

    /**
     * 删除文件
     */
    public void deleteFile(String path) throws ItemNotFoundException, IllegalOperationException, SystemFileDeleteException, ConcurrentAccessException {
        try {
            fileService.delete(path, false, true);
        } catch (DirectoryNoEmptyException ignored) {

        }
    }

    /**
     * 写文件
     */
    public void writeFile(Item file) throws DiskSpaceInsufficientException, ReadOnlyFileModifiedException {
        fileService.writeFile(file);
    }

    /**
     * 读文件
     */
    public byte[] readFile(Item file) {
        return fileService.readFile(file);
    }

    /**
     * 拷贝文件
     */
    public void copyFile(String sourcePath, String targetPath) throws ItemAlreadyExistsException, DiskSpaceInsufficientException, ItemNotFoundException, IllegalOperationException, ReadOnlyFileModifiedException, ConcurrentAccessException {
        fileService.copy(sourcePath, targetPath);
    }

    /**
     * 移动文件
     */
    public void moveFile(String sourcePath, String targetPath) throws ItemAlreadyExistsException, DiskSpaceInsufficientException, ItemNotFoundException, IllegalOperationException, ReadOnlyFileModifiedException, ConcurrentAccessException {
        fileService.move(sourcePath, targetPath);
    }

    /**
     * 建立目录
     */
    public Item createDirectory(String path) throws ItemAlreadyExistsException, ItemNotFoundException, DiskSpaceInsufficientException, IllegalOperationException, ReadOnlyFileModifiedException {
        return fileService.createDirectory(path);
    }

    /**
     * 删除空目录
     */
    public void deleteEmptyDirectory(String path) throws ItemNotFoundException, DirectoryNoEmptyException, IllegalOperationException, SystemFileDeleteException, ConcurrentAccessException {
        fileService.delete(path, true, false);
    }

    /**
     * 删除目录
     */
    public void deleteDirectory(String path) throws ItemNotFoundException, IllegalOperationException, SystemFileDeleteException {
        try {
            fileService.delete(path, true, true);
        } catch (DirectoryNoEmptyException | ConcurrentAccessException ignored) {

        }
    }

    /**
     * 分区
     */
    public void partitionDisk(String src, String dec, int size) throws IllegalOperationException, DiskSpaceInsufficientException, MaxCapacityExceededException, ItemNotFoundException {
        fileService.diskPartition(src, dec, size);
    }

    public void reName(String path, String newName) throws ItemAlreadyExistsException, IllegalOperationException, ItemNotFoundException, ConcurrentAccessException {
        fileService.reName(path, newName);
    }

    public void reAttribute(String path, boolean readOnly, boolean systemFile, boolean regularFile, boolean isDirectory) throws IllegalOperationException, ItemNotFoundException, ConcurrentAccessException {
        fileService.reAttribute(path, readOnly, systemFile, regularFile, isDirectory);
    }

    public Item findItem(String path, FILE_TYPE type) throws ItemNotFoundException {
        return fileService.findItem(path,type);
    }

    public Item findItem(String path) throws IllegalOperationException, ItemNotFoundException {
        return fileService.findItem(path);
    }

    public boolean isExistedDirectory(String path) throws ItemNotFoundException {
        return findItem(path, FILE_TYPE.DIRECTORY) != null;
    }

    // 格式化硬盘
    public void formatDisk(String path) throws IllegalOperationException, ItemNotFoundException {
        fileService.formatDisk(path);
    }

    // 返回文件内容
    public String typeFile(String path) throws IllegalOperationException, ItemNotFoundException {
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
//        FileService.getDisk().test();
    }
}
