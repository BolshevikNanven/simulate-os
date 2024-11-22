package scau.os.soos.module.file;

import scau.os.soos.common.enums.FILE_TYPE;
import scau.os.soos.common.exception.*;
import scau.os.soos.module.file.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import static scau.os.soos.common.enums.FILE_TYPE.*;

public class FileService {

    private static Disk disk;

    public FileService() {
        disk = new Disk();
        disk.init();
    }

    public static Disk getDisk() {
        return disk;
    }

    // 路径必须为/盘名/目录名/文件名.e
    private FILE_TYPE check(String path) throws IllegalOperationException {
        if (!path.contains(":")) {
            throw new IllegalOperationException("请检查路径格式！");
        } else if (path.endsWith(".e")) {
            return EXE;
        } else if (path.endsWith(".t")) {
            return TXT;
        } else if (path.contains(".")) {
            throw new IllegalOperationException("请检查路径格式！");
        } else {
            return DIRECTORY;
        }
    }

    private Item find(String path, FILE_TYPE type) {
        switch (type) {
            case EXE -> {
                return find(path, false, (byte) 'e');
            }
            case TXT -> {
                return find(path, false, (byte) 't');
            }
            case DIRECTORY -> {
                return find(path, true, (byte) 0);
            }
        }
        return null;
    }

    private Item find(String path, boolean isDirectory, byte type) {
        Directory root = disk.getPartitionDirectory();
        if (path == null) {
            return null;
        }
        StringTokenizer tokenizer = new StringTokenizer(path, "/");
        List<String> pathParts = new ArrayList<>();
        while (tokenizer.hasMoreTokens()) {
            String pathPart = tokenizer.nextToken();
            pathParts.add(pathPart);
        }

        // 从根目录开始查找
        return Directory.find(root, pathParts, 0, isDirectory, type);
    }

    public Item findItem(String path) throws IllegalOperationException, ItemNotFoundException {
        return findItem(path,check(path));
    }

    public Item findItem(String path, FILE_TYPE type) throws ItemNotFoundException {
        Item item = find(path, type);
        if (item == null) {
            throw new ItemNotFoundException(path+" 不存在！");
        }
        return item;
    }

    /**
     * 创建
     *
     */
    private Item create(String path, FILE_TYPE type) throws DiskSpaceInsufficientException, ItemNotFoundException, ItemAlreadyExistsException {
        //1. 判断文件/文件夹是否存在
        isItemAlreadyExists(path,type);

        //2. 判断父目录是否存在
        String parentPath = path.substring(0, path.lastIndexOf("/"));
        Directory parent = (Directory) findItem(parentPath,DIRECTORY);

        //3. 判断磁盘空间是否足够
        int rootStartDisk = parent.getRootDirectory().getStartBlockNum();
        int startDisk = disk.findFreeDiskBlock(rootStartDisk);
        if (startDisk == -1) {
            throw new DiskSpaceInsufficientException(path.charAt(1) + " 盘空间不足！");
        }

        //4. 创建文件/文件夹
        String name = path.substring(path.lastIndexOf("/") + 1);
        Item file;
        if (type == DIRECTORY) {
            file = getItemFromCreate(parent, name, (byte) 0, true, false, false, true, startDisk, 0);
        } else if (type == EXE) {
            name = name.substring(0, name.lastIndexOf('.'));
            file = getItemFromCreate(parent, name, (byte) 'e', true, false, true, false, startDisk, 0);
        } else if (type == TXT){
            name = name.substring(0, name.lastIndexOf('.'));
            file = getItemFromCreate(parent, name, (byte) 't', true, false, true, false, startDisk, 0);
        }else {
            return null;
        }

        //5. 修改fat表，父目录添加孩子
        disk.getFat().setNextBlockIndex(startDisk, Fat.TERMINATED);
        disk.getFat().writeFatToDisk();
        parent.addChildren(file);
        parent.updateSize();
        writeItemAndParentsToDisk(file);
        return file;
    }

    public Item createFile(String path) throws
            ItemAlreadyExistsException, DiskSpaceInsufficientException, ItemNotFoundException, IllegalOperationException {

        FILE_TYPE type = check(path);
        if (type == DIRECTORY) {
            throw new IllegalOperationException("不能用于创建目录！");
        }

        return create(path, type);
    }

    public Item createDirectory(String path) throws
            ItemAlreadyExistsException, DiskSpaceInsufficientException, ItemNotFoundException, IllegalOperationException {

        FILE_TYPE type = check(path);
        if (type != DIRECTORY) {
            throw new IllegalOperationException("不能用于创建文件！");
        }

        return create(path, DIRECTORY);
    }


    /**
     * 删除
     *
     */
    public void delete(String path, boolean isDeleteDirectory, boolean isDeleteNotEmpty) throws
            ItemNotFoundException, DirectoryNoEmptyException, IllegalOperationException {

        //1. 检查路径合法性
        FILE_TYPE type = check(path);
        if (isDeleteDirectory && type != DIRECTORY) {
            throw new IllegalOperationException("不能用于删除文件！");
        }

        if (!isDeleteDirectory && type == DIRECTORY) {
            throw new IllegalOperationException("不能用于删除目录！");
        }

        //2. 查找文件/文件夹 是否存在
        Item item = findItem(path,type);

        if (!isDeleteNotEmpty && item.getSize() != 0) {
            throw new IllegalOperationException("目录 "+item.getName()+" 非空！");
        }

        //3. 删除文件/文件夹
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

        parent.removeChild(item);
        deleteItem(item);

        // 更新父目录的大小
        updateItemSize(parent);
        // 更新父目录及其上级目录到磁盘
        writeItemAndParentsToDisk(parent);
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

    private void deleteItem(Item file) {
        int rootStartBlockNum = file.getRootDirectory().getStartBlockNum();
        disk.formatFatTable(file.getStartBlockNum(), rootStartBlockNum);
        file.setParent(null);
    }

    /**
     * 复制
     *
     */
    public void copy(String sourcePath, String targetPath) throws
            DiskSpaceInsufficientException, ItemAlreadyExistsException, ItemNotFoundException, IllegalOperationException {

        //查重
        FILE_TYPE type = check(sourcePath);
        Item srcItem = find(sourcePath, type);
        if (srcItem == null) {
            throw new ItemNotFoundException(sourcePath + " 不存在！");
        }

        FILE_TYPE targetType = check(targetPath);

        if (targetType!=DIRECTORY) {
            throw new IllegalOperationException("目标需为目录！");
        }

        Directory parent = (Directory) find(targetPath, DIRECTORY);
        if (parent == null) {
            throw new ItemNotFoundException(targetPath + " 不存在！");
        }

        // 获取需要复制的磁盘块数
        Fat fat = disk.getFat();
        int needDiskNum = 0;
        if(srcItem.isDirectory()){
            needDiskNum = getDirectoryTotalDiskBlocks((Directory) srcItem);
        }else{
            needDiskNum = srcItem.calculateTotalBlockNum(fat);
        }


        int rootStartBlockNum = parent.getRootDirectory().getStartBlockNum();
        List<Integer> needDiskBlocks = disk.findFreeDiskBlock(needDiskNum, rootStartBlockNum);
        if (needDiskBlocks.size() < needDiskNum) {
            throw new DiskSpaceInsufficientException(targetPath.charAt(1) + " 盘空间不足！");
        }


        if (targetPath.endsWith("/")) {
            targetPath = targetPath.substring(0, targetPath.length() - 1);
        }
        String targetItem = targetPath + "/" + srcItem.getName();
        Item existingItem = find(targetItem, type);
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

        newItem.setParent(parent);
        newItem.setStartBlockNum(needDiskBlocks.get(0));
        newItem.setPath();

        parent.addChildren(newItem);
        updateItemSize(newItem);
        writeItemAndParentsToDisk(newItem);

        if (srcItem.isDirectory()) {
            Directory srcDir = (Directory) srcItem;
            for (Item child : srcDir.getChildren()) {
                String childSourcePath = child.getPath();
                String childTargetPath = targetItem;
                copy(childSourcePath, childTargetPath);
            }
        }
    }

    public void move(String sourcePath, String targetPath) throws ItemAlreadyExistsException, DiskSpaceInsufficientException, IllegalOperationException, ItemNotFoundException {
        copy(sourcePath,targetPath);
        delete(find(sourcePath,check(sourcePath)));
    }

    public int getDirectoryTotalDiskBlocks(Directory directory){
        int totalDiskBlocks = 0;
        for (Item item : directory.getChildren()) {
            if (item.isDirectory()) {
                totalDiskBlocks += getDirectoryTotalDiskBlocks((Directory) item);
            } else {
                totalDiskBlocks += item.calculateTotalBlockNum(disk.getFat());
            }
        }
        return totalDiskBlocks+directory.calculateTotalBlockNum(disk.getFat());
    }

    /**
     * 分区
     *
     */
    public void diskPartition(String sourcePath, String targetPath, int needDiskNum) throws IllegalOperationException, DiskSpaceInsufficientException, MaxCapacityExceededException, ItemNotFoundException {
        // 1.验证路径格式：必须以斜杠开头，单个大小写字母，冒号结尾
        String regex = "/[a-zA-Z]:";
        if (!sourcePath.matches(regex) || !targetPath.matches(regex)) {
            throw new IllegalOperationException("磁盘命名格式: '/[a-zA-Z]:'!");
        }

        // 2.查找源根目录
        Directory sourceRoot = (Directory) find(sourcePath, DIRECTORY);
        if (sourceRoot == null) {
            throw new ItemNotFoundException(sourcePath + " 盘不存在!");
        }
        int sourceStartBlockNum = sourceRoot.getStartBlockNum();

        // 3.判断源盘空间是否足够
        Fat fat = disk.getFat();
        List<Integer> needDiskBlocks = disk.findFreeDiskBlockFromTail(needDiskNum, sourceStartBlockNum);
        if (needDiskBlocks.size() < needDiskNum && sourceRoot.getSize()!=needDiskNum) {
            throw new DiskSpaceInsufficientException(sourceRoot + " 盘空间不足!");
        }
        if(sourceRoot.getSize()==needDiskNum){
            if(!sourceRoot.getChildren().isEmpty()){
                throw new DiskSpaceInsufficientException(sourceRoot + " 盘空间不足!");
            }
            // TODO: 2024/11/22  记得改回
            //needDiskBlocks.addFirst(sourceRoot.getStartBlockNum());
        }

        // 4.查找或创建目标根目录
        Directory targetRoot = (Directory) find(targetPath, DIRECTORY);
        int targetStartBlockNum;
        // 分区存在，则转移根目录并刷新FAT表
        if (targetRoot != null) {
            // 获取原盘块号
            targetStartBlockNum = targetRoot.getStartBlockNum();
            int newStartBlockNum = needDiskBlocks.get(0);
            // 设置新盘块号
            if(newStartBlockNum<targetStartBlockNum){
                targetRoot.setStartBlockNum(newStartBlockNum);
                // 剪切磁盘块
                fat.setNextBlockIndex(targetStartBlockNum, newStartBlockNum);
                disk.copyDiskBlock(targetStartBlockNum, newStartBlockNum);
                disk.formatDiskBlock(targetStartBlockNum);
                // 刷新FAT表
                fat.refresh(targetStartBlockNum, newStartBlockNum);
                targetStartBlockNum = newStartBlockNum;
            }

            // 更新分区大小
            targetRoot.setSize(targetRoot.getSize() + needDiskNum);
        } else {
            targetStartBlockNum = needDiskBlocks.get(0);
            targetRoot = (Directory) FileService.getItemFromCreate(
                    disk.getPartitionDirectory(),
                    targetPath.substring(1),
                    (byte) 0,
                    false,
                    false,
                    false,
                    true,
                    targetStartBlockNum,
                    needDiskNum);
            if (!disk.getPartitionDirectory().addChildren(targetRoot)) {
                targetRoot.setParent(null);
                throw new MaxCapacityExceededException("已达到最大分区，最多创建 8 个分区！");
            }
            targetRoot.setRoot(true);
        }
        if(sourceRoot.getSize()==needDiskNum){
            disk.getPartitionDirectory().removeChild(sourceRoot);
        }
        sourceRoot.setSize(sourceRoot.getSize() - needDiskNum);
        updateItemSize(sourceRoot);
        updateItemSize(targetRoot);

        // 5.修改FAT表
        for (int i : needDiskBlocks) {
            fat.setNextBlockIndex(i, targetStartBlockNum);
        }
        fat.setNextBlockIndex(targetStartBlockNum, Fat.TERMINATED);
        fat.writeFatToDisk();

        writeItemAndParentsToDisk(targetRoot);
    }

    public void formatDisk(String targetPath) throws IllegalOperationException, ItemNotFoundException {
        // 1.验证路径格式：必须以斜杠开头，单个大小写字母，冒号结尾
        String regex = "/[a-zA-Z]:";
        if (!targetPath.matches(regex)) {
            throw new IllegalOperationException("磁盘命名格式: '/[a-zA-Z]:'!");
        }
        // 2.查找目标根目录
        Directory targetRoot = (Directory) find(targetPath, DIRECTORY);
        if (targetRoot == null) {
            throw new ItemNotFoundException(targetPath + " 盘不存在!");
        }
//        int sourceStartBlockNum = targetRoot.getStartBlockNum();
    }

    public int getSize(Item item) {
        return item.getSize();
    }

    /**
     * 从指定的文件项中读取指令到内存中。
     *
     * @param file 要读取的文件项。如果文件项为null，则抛出ItemNotFoundException异常。
     * @return 返回一个字节数组，包含从文件中读取的指令。如果文件项不是Exe类型，则返回null。
     * @throws ItemNotFoundException 如果指定的文件项为null，则抛出此异常。
     */
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

    public void writeFile(Item item) throws
            DiskSpaceInsufficientException {

        //获取需要写入的字符串长度，计算需要多少个磁盘块
        Fat fat = disk.getFat();
        //需要的块数=文件总大小需要的磁盘块数-已占有的块数
        int needDiskNum = (int) Math.ceil((double) item.getSize() / Disk.BYTES_PER_BLOCK) - item.calculateTotalBlockNum(fat);
        List<Integer> list = disk.findFreeDiskBlock(needDiskNum, item.getRootDirectory().getStartBlockNum());
        int num = list.size();


        //如果磁盘块不足，则无法写入文件
        if (num < needDiskNum) {
            throw new DiskSpaceInsufficientException("磁盘空间不足！");
        }

        updateItemSize(item);
        writeItemAndParentsToDisk(item);
    }

    public void reName(String path, String newName) throws ItemAlreadyExistsException, IllegalOperationException, ItemNotFoundException {
        Item item = findItem(path);
        if (newName.isEmpty() || newName.length() > 3) {
            throw new IllegalOperationException(newName);
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
        item.setPath();
        writeItemAndParentsToDisk(item);
    }

    public void reAttribute(String path, boolean readOnly, boolean systemFile, boolean regularFile, boolean isDirectory) throws IllegalOperationException, ItemNotFoundException {
        Item item = findItem(path);
        item.setAttribute(readOnly, systemFile, regularFile, isDirectory);
        writeItemAndParentsToDisk(item);
    }

    public String typeFile(String path) {

        return "";
    }

    public static Item getItemFromDisk(byte[] data) {
        // 获取数据中的类型字节
        byte type = data[3];
        // 获取数据中的属性字节
        byte attribute = data[4];

        // 判断属性字节的第三位是否为0
        if ((attribute & 0x08) != 0) {
            // 是目录类型，返回目录实例
            return new Directory(data);
        } else {
            // 如果不是目录，继续判断类型字节
            if (type != 0) {
                // 类型字节为'e'，返回exe实例
                if (type == 'e') {
                    return new Exe(data);
                }
                if (type == 't') {
                    // 类型字节为't'，返回txt实例
                    return new Txt(data);
                }
            }
        }
        return null;
    }

    public static Item getItemFromCreate(Item parent, String name, byte type, boolean readOnly, boolean systemFile, boolean regularFile, boolean isDirectory, int startBlockNum, int size) {
        if (isDirectory) {
            return new Directory(parent, name, type, readOnly, systemFile, regularFile, true, startBlockNum, size);
        } else {
            if (type == (byte) 'e') {
                return new Exe(parent, name, (byte) 'e', readOnly, systemFile, regularFile, false, startBlockNum, size);
            } else {
                return new Txt(parent, name, type, readOnly, systemFile, regularFile, false, startBlockNum, size);
            }
        }
    }

    private void writeItemAndParentsToDisk(Item item) {
        if (item == null) {
            return;
        }
        item.writeContentToDisk();
        Item parent = item.getParent();
        while (parent != null) {
            if (!parent.writeContentToDisk()) {
                return;
            }
            parent = parent.getParent();
        }

        // 通知文件系统
        FileController.getInstance().notify(item);
    }

    private void updateItemSize(Item item) {
        if (item == null) {
            return;
        }

        item.updateSize();

        Item parent = item.getParent();
        while (parent != null) {
            parent.updateSize();
            parent = parent.getParent();
        }
    }

    /**
     * 检查指定路径和类型的项是否已经存在。
     *
     * @param path 要检查的路径
     * @param type 要检查的项类型（例如文件或目录）
     * @throws ItemAlreadyExistsException 如果指定路径和类型的项已经存在，则抛出此异常
     */
    public void isItemAlreadyExists(String path, FILE_TYPE type) throws ItemAlreadyExistsException {
        Item item = find(path, type);
        if (item != null) {
            throw new ItemAlreadyExistsException(item.getFullName() +" 已存在！");
        }
    }
}
