package scau.os.soos.module.file;

import javafx.scene.Parent;
import scau.os.soos.common.enums.FILE_TYPE;
import scau.os.soos.module.file.Util.FileServiceUtil;
import scau.os.soos.module.file.model.*;

import java.util.ArrayList;
import java.util.List;

public class FileService {

    private Disk disk;

    public FileService() {
        disk = new Disk();
    }

    public Disk getDisk() {
        return disk;
    }

    public Item createFile(String path) {
        if (FileServiceUtil.find(disk, path, FILE_TYPE.EXE) != null) {
            System.out.println("文件已存在！");
            return null;
        }

        Item file = null;
        String parentPath = path.substring(0, path.lastIndexOf("/"));
        Directory parent = (Directory) FileServiceUtil.find(disk, parentPath, FILE_TYPE.DIRECTORY);
        if (parent == null) {
            System.out.println("父目录不存在！");
            return null;
        } else {
            int startDisk = disk.findFreeDiskBlock();

            if (startDisk == -1) {
                System.out.println("磁盘空间不足！");
                return null;
            }

            String name = path.substring(path.lastIndexOf("/") + 1, path.lastIndexOf('.'));

            if (path.contains(".e")) {
                file = FileServiceUtil.getItemFromCreate(disk, parent, name, (byte) 'e', true, false, true, false, startDisk, 0);
                FileServiceUtil.writeItemAndParentsToDisk(file);
            } else {
                file = FileServiceUtil.getItemFromCreate(disk, parent, name, (byte) 0, true, false, true, false, startDisk, 0);
                FileServiceUtil.writeItemAndParentsToDisk(file);
            }
            disk.getFat().setNextBlockIndex(startDisk, Fat.TERMINATED);
            parent.addChildren(file);
            return file;
        }

    }

    public Directory createDirectory(String path) {
        if (FileServiceUtil.find(disk, path, FILE_TYPE.DIRECTORY) != null) {
            System.out.println("文件夹已存在！");
            return null;
        }

        Directory folder = null;
        String parentPath = path.substring(0, path.lastIndexOf("/"));
        Directory parent = (Directory) FileServiceUtil.find(disk, parentPath, FILE_TYPE.DIRECTORY);

        if (parent == null) {
            System.out.println("父目录不存在！");
            return null;
        } else {
            int startDisk = disk.findFreeDiskBlock();

            if (startDisk == -1) {
                System.out.println("磁盘空间不足！");
                return null;
            }

            String name = path.substring(path.lastIndexOf("/") + 1);// \u0000为空字符
            folder = (Directory) FileServiceUtil.getItemFromCreate(disk, parent, name, (byte) 0, true, false, false, true, startDisk, 0);
            disk.getFat().setNextBlockIndex(startDisk, Fat.TERMINATED);
            parent.addChildren(folder);
            return folder;
        }
    }

    public void delete(String path, FILE_TYPE type, boolean isDeleteNotEmpty){
        Item item = FileServiceUtil.find(disk, path, type);

        if (item == null) {
            System.out.println("不存在！");
            return;
        }

        if (!isDeleteNotEmpty && item.getSize() != 0) {
            System.out.println("不是空目录或文件不为空，删除操作被取消。");
            return;
        }

        FileServiceUtil.deleteItemRecursively(item);
    }

    public int getSize(Item item) {
        return item.getSize();
    }

    public void writeFile(Item item, String content, FILE_TYPE type) {
        //获取需要写入的字符串长度，计算需要多少个磁盘块
        List<Integer> list = new ArrayList<>();
        Disk disk = item.getDisk();
        Fat fat = item.getDisk().getFat();
        //需要的块数=文件总大小需要的磁盘块数-已占有的块数
        int needDiskNum = (int) Math.ceil((double) content.length() / disk.BYTES_PER_BLOCK) - item.calculateTotalBlockNum(fat);
        int num = 0;
        for (int i = 3; i < disk.BLOCKS_PER_DISK && num < needDiskNum; i++) {
            if (fat.isFreeBlock(i)) {
                list.add(i);
                num++;
            }
        }

        //如果磁盘块不足，则无法写入文件
        if (num < needDiskNum) {
            System.out.println("磁盘空间不足!");
            return;
        }

        //更新fat表
        int endDisk = disk.findLastDisk(item.getStartBlockNum());

        for (Integer index : list) {
            fat.setNextBlockIndex(endDisk, index);
            endDisk = index;
        }
        fat.setNextBlockIndex(endDisk, Fat.TERMINATED);

        item.initFromString(content);
        FileServiceUtil.updateItemSize(item);
        FileServiceUtil.writeItemAndParentsToDisk(item);

        System.out.println("写入成功!");
    }

    public boolean copy(String sourcePath, String targetPath, FILE_TYPE fileType) {
        Item srcItem = FileServiceUtil.find(disk, sourcePath, fileType);
        if (srcItem == null) {
            System.out.println("文件不存在!");
            return false;
        }

        Disk disk = srcItem.getDisk();
        Fat fat = disk.getFat();

        int needDiskNum = srcItem.calculateTotalBlockNum(fat);
        List<Integer> needDiskBlocks = disk.findFreeDiskBlock(needDiskNum);
        if (needDiskBlocks.size() < needDiskNum) {
            System.out.println("磁盘空间不足!");
            return false;
        }

        String parentPath = targetPath.substring(0, targetPath.lastIndexOf("/"));
        Directory parent = (Directory) FileServiceUtil.find(disk, parentPath, FILE_TYPE.DIRECTORY);
        if (parent == null) {
            System.out.println("操作不允许!");
            return false;
        }

        int cur = needDiskBlocks.get(0);
        int pre = cur;
        for (int i = 1; i < needDiskBlocks.size(); i++) {
            cur = needDiskBlocks.get(i);
            fat.setNextBlockIndex(pre, cur);
            pre = cur;
        }
        fat.setNextBlockIndex(cur, Fat.TERMINATED);

        Item newItem = srcItem.copy();
        newItem.setDisk(disk);
        newItem.setParent(parent);
        newItem.setStartBlockNum(needDiskBlocks.get(0));

        parent.addChildren(newItem);
        return true;
    }
}
