package scau.os.soos.module.file;

import scau.os.soos.common.enums.FILE_TYPE;
import scau.os.soos.common.exception.*;
import scau.os.soos.module.file.model.*;

import java.util.List;

public class FileService {

    private static Disk disk;

    public FileService() {
        disk = new Disk();
        //disk.file2disk();
    }

    public static Disk getDisk() {
        return disk;
    }

    public Item createFile(String path) throws
            ItemAlreadyExistsException, DiskSpaceInsufficientException, ItemNotFoundException, IllegalPathException {

        //查重
        FILE_TYPE type = check(path);
        if (type == FILE_TYPE.DIRECTORY) {
            throw new IllegalPathException("非法路径！");
        }

        Item existingItem = find(path, type);
        if (existingItem != null) {
            throw new ItemAlreadyExistsException(existingItem.getFullName());
        }

        //找空闲磁盘块
        int startDisk = disk.findFreeDiskBlock();
        if (startDisk == -1) {
            throw new DiskSpaceInsufficientException("磁盘空间不足！");
        }

        //找父目录
        Item file = null;
        String parentPath = path.substring(0, path.lastIndexOf("/"));
        Directory parent = (Directory) find( parentPath, FILE_TYPE.DIRECTORY);
        if (parent == null) {
            throw new ItemNotFoundException("父目录不存在！");
        }

        //创建文件
        String name = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf('.'));
        if (type == FILE_TYPE.EXE) {
            file = getItemFromCreate(disk, parent, name, (byte) 'e', true, false, true, false, startDisk, 0);
        } else {
            file = getItemFromCreate(disk, parent, name, (byte) 't', true, false, true, false, startDisk, 0);
        }

        //修改fat表，父目录添加孩子
        disk.getFat().setNextBlockIndex(startDisk, Fat.TERMINATED);
        disk.getFat().writeFatToDisk();
        parent.addChildren(file);
        parent.updateSize();
        writeItemAndParentsToDisk(file);
        return file;
    }

    public Directory createDirectory(String path) throws
            ItemAlreadyExistsException, DiskSpaceInsufficientException, ItemNotFoundException {

        //查重
        Item existingItem = find(path, FILE_TYPE.DIRECTORY);
        if (existingItem != null) {
            throw new ItemAlreadyExistsException("目录已存在！");
        }

        //找空闲磁盘块
        int startDisk = disk.findFreeDiskBlock();
        if (startDisk == -1) {
            throw new DiskSpaceInsufficientException("磁盘空间不足！");
        }

        //找父目录
        Directory folder = null;
        String parentPath = path.substring(0, path.lastIndexOf("/"));
        Directory parent = (Directory) find( parentPath, FILE_TYPE.DIRECTORY);
        if (parent == null) {
            throw new ItemNotFoundException("父目录不存在！");
        }

        //创建文件夹
        String name = path.substring(path.lastIndexOf("/") + 1);// \u0000为空字符
        folder = (Directory) getItemFromCreate(disk, parent, name, (byte) 0, true, false, false, true, startDisk, 0);
        folder.setPath();

        //修改fat表，父目录添加孩子
        disk.getFat().setNextBlockIndex(startDisk, Fat.TERMINATED);
        disk.getFat().writeFatToDisk();
        parent.addChildren(folder);
        parent.updateSize();
        writeItemAndParentsToDisk(folder);
        return folder;
    }

    public void delete(String path, boolean isDeleteDirectory, boolean isDeleteNotEmpty) throws
            ItemNotFoundException, DirectoryNoEmptyException, IllegalPathException {

        //查重
        FILE_TYPE type = check(path);

        if (isDeleteDirectory && type != FILE_TYPE.DIRECTORY) {
            throw new IllegalPathException("非法路径！");
        }

        if (!isDeleteDirectory && type == FILE_TYPE.DIRECTORY) {
            throw new IllegalPathException("非法路径！");
        }

        Item item =find( path, type);

        if (item == null) {
            throw new ItemNotFoundException("不存在！");
        }

        if (!isDeleteNotEmpty && item.getSize() != 0) {
            throw new DirectoryNoEmptyException("不是空目录或文件不为空，删除操作被取消。");
        }

        delete(item);
    }

    private void delete(Item item) {
        if (item instanceof Directory) {
            deleteDirectoryRecursively((Directory) item);
        }

        Directory parent = (Directory) item.getParent();
        if (parent == null) {
            return;
        }

        parent.getChildren().remove(item);
        deleteItem(item);

        // 更新父目录的大小
        updateItemSize(parent);
        // 更新父目录及其上级目录到磁盘
        writeItemAndParentsToDisk(parent);
    }

    private void deleteItem(Item file) {
        Disk disk = file.getDisk();
        disk.formatFatTable(file.getStartBlockNum());
        file.setParent(null);
        file.setDisk(null);
    }

    private void deleteDirectoryRecursively(Directory directory) {
        for (Item child : directory.getChildren()) {
            if (child instanceof Directory childDir) {
                deleteDirectoryRecursively(childDir);
                childDir.getChildren().clear();
            }
            deleteItem(child);
        }
    }


    public int getSize(Item item) {
        return item.getSize();
    }

    public byte[] readFile(Item file) throws ItemNotFoundException {
        if (file == null) {
            throw new ItemNotFoundException("没有该文件");
        }
        if (file instanceof Exe exe) {
            List<Byte> instructions = exe.getInstructions();
            byte[] instructionsArray = new byte[instructions.size()];
            for (int i = 0; i < instructions.size(); i++) {
                instructionsArray[i] = instructions.get(i);
            }
            return instructionsArray;
        }
        return null;
    }

    public void writeFile(Item item, String content) throws
            DiskSpaceInsufficientException {

        //获取需要写入的字符串长度，计算需要多少个磁盘块
        Disk disk = item.getDisk();
        Fat fat = item.getDisk().getFat();
        //需要的块数=文件总大小需要的磁盘块数-已占有的块数
        int needDiskNum = (int) Math.ceil((double) content.length() / disk.BYTES_PER_BLOCK) - item.calculateTotalBlockNum(fat);
        List<Integer> list = disk.findFreeDiskBlock(needDiskNum);
        int num = list.size();


        //如果磁盘块不足，则无法写入文件
        if (num < needDiskNum) {
            throw new DiskSpaceInsufficientException("磁盘空间不足！");
        }

        //更新fat表
        int endDisk = disk.findLastDisk(item.getStartBlockNum());

        for (Integer index : list) {
            fat.setNextBlockIndex(endDisk, index);
            endDisk = index;
        }
        fat.setNextBlockIndex(endDisk, Fat.TERMINATED);
        fat.writeFatToDisk();

        item.initFromString(content);
        updateItemSize(item);
        writeItemAndParentsToDisk(item);

        System.out.println("写入成功!");
    }

    public void copy(String sourcePath, String targetPath, boolean isMove) throws
            DiskSpaceInsufficientException, ItemAlreadyExistsException, ItemNotFoundException, IllegalPathException {

        //查重
        FILE_TYPE type = check(sourcePath);

        Item srcItem = find( sourcePath, type);
        if (srcItem == null) {
            throw new ItemNotFoundException("文件不存在！");
        }

        if (targetPath.contains(".")) {
            throw new IllegalPathException("目标非路径！");
        }

        Disk disk = srcItem.getDisk();
        Fat fat = disk.getFat();

        int needDiskNum = srcItem.calculateTotalBlockNum(fat);

        List<Integer> needDiskBlocks = disk.findFreeDiskBlock(needDiskNum);
        if (needDiskBlocks.size() < needDiskNum) {
            throw new DiskSpaceInsufficientException("磁盘空间不足！");
        }

        Directory parent = (Directory) find( targetPath, FILE_TYPE.DIRECTORY);
        if (parent == null) {
            throw new ItemNotFoundException("父目录不存在！");
        }

        if (targetPath.endsWith("/")) {
            targetPath = targetPath.substring(0, targetPath.length() - 1);
        }
        String targetItem = targetPath + "/" + srcItem.getName();
        Item existingItem = find( targetItem, type);
        if (existingItem != null) {
            throw new ItemAlreadyExistsException(existingItem.getFullName());
        }

        int cur = needDiskBlocks.get(0);
        int pre = cur;

        for (int i = 1; i < needDiskBlocks.size(); i++) {
            cur = needDiskBlocks.get(i);
            fat.setNextBlockIndex(pre, cur);
            pre = cur;
        }
        fat.setNextBlockIndex(cur, Fat.TERMINATED);
        fat.writeFatToDisk();

        Item newItem = srcItem.copy();
        newItem.setDisk(disk);
        newItem.setParent(parent);
        newItem.setStartBlockNum(needDiskBlocks.get(0));
        newItem.setPath();

        parent.addChildren(newItem);

        if (isMove) {
            delete(srcItem);
        }

        updateItemSize(newItem);
        writeItemAndParentsToDisk(newItem);
    }

    public Directory getRoots() {
        return disk.getPartitionDirectory();
    }

    public void reName(Item item, String newName) throws ItemAlreadyExistsException, IllegalNameException {
        if (newName.isEmpty() || newName.length() > 3) {
            throw new IllegalNameException(newName);
        }

        Directory parent = (Directory) item.getParent();
        byte curType = item.getType();
        for (Item child : parent.getChildren()) {
            if (child == item) {
                continue;
            }
            if (child.getName().equals(newName) && child.getType() == curType) {
                throw new ItemAlreadyExistsException(child.getFullName());
            }
        }

        item.setName(newName);
        writeItemAndParentsToDisk(item);
    }

    public void reAttribute(Item item, boolean readOnly, boolean systemFile, boolean regularFile, boolean isDirectory) {
        item.setAttribute(readOnly, systemFile, regularFile, isDirectory);
        writeItemAndParentsToDisk(item);
    }

    /**
     * 查找文件
     * @param path 传入的 path 严格遵守 盘名/目录名/文件名 的格式，如：C:/dir1/file.t
     *             区域之间使用 / 分割
     * @param type 文件类型
     * @return 找到的文件项
     * @throws ItemNotFoundException 如果文件不存在
     * @throws IllegalArgumentException 如果路径格式不正确
     */
    public Item find(String path, FILE_TYPE type) throws ItemNotFoundException {
        // 检查路径是否包含至少一个 /
        if (path.indexOf('/') == -1) {
            throw new IllegalArgumentException("路径格式不正确，必须包含至少一个 '/' 分隔符。");
        }

        // 安全地获取分区目录
        Directory partitionDirectory = disk.getPartitionDirectory();
        if (partitionDirectory == null) {
            throw new IllegalStateException("分区目录为空，无法执行查找操作。");
        }

        // 提取盘名
        String diskName = path.substring(0, path.indexOf('/'));
        // 提取剩余路径
        String remainingPath = path.substring(path.indexOf('/') + 1);

        // 在分区目录中查找盘名对应的目录
        for (Item item : partitionDirectory.getChildren()) {
            if (item instanceof Directory && item.getName().equals(diskName)) {
                return ((Directory) item).find(remainingPath, type); // 递归查找剩余路径
            }
        }

        // 如果未找到，抛出异常
        throw new ItemNotFoundException("文件不存在！");
    }


    public static Item getItemFromDisk(Disk disk, byte[] data) {
        // 获取数据中的类型字节
        byte type = data[3];
        // 获取数据中的属性字节
        byte attribute = data[4];

        // 判断属性字节的第三位是否为0
        if ((attribute & 0x08) != 0) {
            // 是目录类型，返回目录实例
            return new Directory(disk, data);
        } else {
            // 如果不是目录，继续判断类型字节
            if (type != 0) {
                // 类型字节为'e'，返回exe实例
                if (type == 'e') {
                    return new Exe(disk, data);
                }
                if (type == 't') {
                    // 类型字节为't'，返回txt实例
                    return new Txt(disk, data);
                }
            }
        }
        return null;
    }


    public static Item getItemFromCreate(Disk disk, Item parent, String name, byte type, boolean readOnly, boolean systemFile, boolean regularFile, boolean isDirectory, int startBlockNum, int size) {
        if (isDirectory) {
            return new Directory(disk, parent, name, type, readOnly, systemFile, regularFile, true, startBlockNum, size);
        } else {
            if (type == (byte) 'e') {
                return new Exe(disk, parent, name, (byte) 'e', readOnly, systemFile, regularFile, false, startBlockNum, size);
            } else {
                return new Txt(disk, parent, name, type, readOnly, systemFile, regularFile, false, startBlockNum, size);
            }
        }
    }


    public boolean writeItemAndParentsToDisk(Item item) {
        if (item == null || item.getDisk() == null) {
            return false;
        }

        return writeItemToDiskAndPropagateToRoot(item);
    }

    private boolean writeItemToDiskAndPropagateToRoot(Item item) {
        item.writeContentToDisk();
        Item parent = item.getParent();
        while (parent != null) {
            if (!parent.writeContentToDisk()) {
                return false;
            }
            parent = parent.getParent();
        }
        return true;
    }

    public boolean updateItemSize(Item item) {
        if (item == null || item.getDisk() == null) {
            return false;
        }

        return updateItemAndPropagateToRootSize(item);
    }

    private boolean updateItemAndPropagateToRootSize(Item item) {
        item.updateSize();

        Item parent = item.getParent();
        while (parent != null) {
            parent.updateSize();
            parent = parent.getParent();
        }
        return true;
    }

    public FILE_TYPE check(String path){
        if(path.endsWith(".e")){
            return FILE_TYPE.EXE;
        } else if (path.endsWith(".t")){
            return FILE_TYPE.TXT;
        } else if (path.contains(".")){
            return null;
        } else {
            return FILE_TYPE.DIRECTORY;
        }
    }
}
