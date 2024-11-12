package scau.os.soos.module.file;

import scau.os.soos.common.enums.FILE_TYPE;
import scau.os.soos.common.exception.*;
import scau.os.soos.module.file.util.FileServiceUtil;
import scau.os.soos.module.file.model.*;

import java.util.List;

public class FileService {

    private Disk disk;

    public FileService() {
        disk = new Disk();
        //disk.file2disk();
    }

    public Disk getDisk() {
        return disk;
    }

    public Item createFile(String path) throws
            ItemAlreadyExistsException, DiskSpaceInsufficientException, ItemNotFoundException {

        //查重
        FILE_TYPE type = FileServiceUtil.check(path);
        Item existingItem = FileServiceUtil.find(disk, path, type);
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
        Directory parent = (Directory) FileServiceUtil.find(disk, parentPath, FILE_TYPE.DIRECTORY);
        if (parent == null) {
            throw new ItemNotFoundException("父目录不存在！");
        }

        //创建文件
        String name = path.substring(path.lastIndexOf("/") + 1,path.lastIndexOf('.'));
        if (type == FILE_TYPE.EXE) {
            file = FileServiceUtil.getItemFromCreate(disk, parent,name, (byte) 'e',true,false,true,false,startDisk,0);
        } else {
            file = FileServiceUtil.getItemFromCreate(disk, parent,name, (byte) 't',true,false,true,false,startDisk,0);
        }

        //修改fat表，父目录添加孩子
        disk.getFat().setNextBlockIndex(startDisk, Fat.TERMINATED);
        disk.getFat().writeFatToDisk();
        parent.addChildren(file);
        parent.updateSize();
        FileServiceUtil.writeItemAndParentsToDisk(file);
        return file;
    }

    public Directory createDirectory(String path) throws
            ItemAlreadyExistsException, DiskSpaceInsufficientException, ItemNotFoundException {

        //查重
        Item existingItem = FileServiceUtil.find(disk,path,FILE_TYPE.DIRECTORY);
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
        Directory parent = (Directory) FileServiceUtil.find(disk,parentPath,FILE_TYPE.DIRECTORY);
        if (parent == null) {
            throw new ItemNotFoundException("父目录不存在！");
        }

        //创建文件夹
        String name = path.substring(path.lastIndexOf("/") + 1);// \u0000为空字符
        folder = (Directory) FileServiceUtil.getItemFromCreate(disk, parent,name, (byte) 0,true,false,false,true,startDisk,0);
        folder.setPath();

        //修改fat表，父目录添加孩子
        disk.getFat().setNextBlockIndex(startDisk, Fat.TERMINATED);
        disk.getFat().writeFatToDisk();
        parent.addChildren(folder);
        parent.updateSize();
        FileServiceUtil.writeItemAndParentsToDisk(folder);
        return folder;
    }

    public void delete(String path,boolean isDeleteDirectory, boolean isDeleteNotEmpty) throws
            ItemNotFoundException, DirectoryNoEmptyException {

        //查重
        FILE_TYPE type = FileServiceUtil.check(path);

        if(isDeleteDirectory){
            type = FILE_TYPE.DIRECTORY;
        }

        Item item = FileServiceUtil.find(disk, path, type);

        if (item == null) {
            throw new ItemNotFoundException("不存在！");
        }

        if (!isDeleteNotEmpty && item.getSize() != 0) {
            throw new DirectoryNoEmptyException("不是空目录或文件不为空，删除操作被取消。");
        }

        FileServiceUtil.delete(item);
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
        FileServiceUtil.updateItemSize(item);
        FileServiceUtil.writeItemAndParentsToDisk(item);

        System.out.println("写入成功!");
    }

    public void copy(String sourcePath, String targetPath) throws
            DiskSpaceInsufficientException, ItemNotFoundException, ItemAlreadyExistsException, ItemNotFoundException, IllegalPathException {

        //查重
        FILE_TYPE type = FileServiceUtil.check(sourcePath);

        Item srcItem = FileServiceUtil.find(disk, sourcePath, type);
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

        Directory parent = (Directory) FileServiceUtil.find(disk, targetPath, FILE_TYPE.DIRECTORY);
        if (parent == null) {
            throw new ItemNotFoundException("父目录不存在！");
        }

        if(targetPath.endsWith("/")){
            targetPath = targetPath.substring(0,targetPath.length() - 1);
        }
        String targetItem = targetPath + "/" + srcItem.getName();
        Item existingItem = FileServiceUtil.find(disk,targetItem,type);
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

        parent.addChildren(newItem);
        FileServiceUtil.updateItemSize(newItem);
        FileServiceUtil.writeItemAndParentsToDisk(newItem);
    }

    public Directory getRoots() {
        return disk.getRootDirectory();
    }

    public void reName(Item item, String newName) throws ItemAlreadyExistsException {
        Directory parent = (Directory) item.getParent();
        byte curType = item.getType();
        for(Item child : parent.getChildren()){
            if(child == item){
                continue;
            }
            if(child.getName().equals(newName)&&child.getType()==curType){
                throw new ItemAlreadyExistsException(child.getFullName());
            }
        }
        item.setName(newName);
        FileServiceUtil.writeItemAndParentsToDisk(item);
    }

    public void reAttribute(Item item, boolean readOnly, boolean systemFile, boolean regularFile, boolean isDirectory) {
        item.setAttribute(readOnly,systemFile,regularFile,isDirectory);
        FileServiceUtil.writeItemAndParentsToDisk(item);
    }

    public Item findItem(String path, FILE_TYPE type) throws ItemNotFoundException {
        Item item = FileServiceUtil.find(disk,path,type);
        if(item == null)
            throw new ItemNotFoundException("文件不存在！");
        return item;
    }
}
