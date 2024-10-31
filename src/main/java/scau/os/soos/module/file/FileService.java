package scau.os.soos.module.file;

import scau.os.soos.common.enums.FILE_TYPE;
import scau.os.soos.module.file.model.*;

import java.util.ArrayList;
import java.util.List;

public class FileService {

    private Disk disk;

    public FileService() {
        disk = new Disk();
    }

    public Item findFile(String path) {
        return disk.findFile(path);
    }

    public Item findDirectory(String path) {
        return disk.findDirectory(path);
    }

    public Item createFile(String path) {;

        if(findFile(path) != null){
            System.out.println("文件已存在！");
            return null;
        }

        Item file = null;
        String parentPath = path.substring(0,path.lastIndexOf("/"));
        Directory parent = (Directory) findDirectory(parentPath);
        if(parent == null){
            System.out.println("父目录不存在！");
            return null;
        }else{
            int startDisk = disk.findFreeDiskBlock();

            if(startDisk == -1){
                System.out.println("磁盘空间不足！");
                return null;
            }

            String name = path.substring(path.lastIndexOf("/") + 1);

            if(path.contains(".e")){
                file = new Exe(name,(byte)1,true,false,true,false,disk,parent,"");
                file.setStartBlockNum((byte)startDisk);
                ((Exe)file).writeContentToDisk(disk);
            }else{
                file = new Txt(name,(byte)2,true,false,true,false,disk,parent,"");
                file.setStartBlockNum((byte)startDisk);
                ((Txt)file).writeContentToDisk(disk);
            }
            disk.getFat().setNextBlockIndex(startDisk,Fat.TERMINATED);
            parent.addChildren(file);
            return file;
        }

    }

    public Directory createDirectory(String path) {

//        if (findFolder(path) != null) {
//            System.out.println("文件夹已存在！");
//            return null;
//        }
//
//        Directory folder = null;
//
//        int diskNum = findFreeDiskBlock();
//        if (diskNum == -1) {
//            return null;
//        } else {
//            fatTable.getFat()[diskNum] = -1;//占用磁盘块
//        }
//
//        //创建在根目录下的
//        if (parent == null) {
//            Directory root = (Directory) DISK.getDisk()[2][0];
//            if (root.getChildren().size() == 8) {
//                System.out.println("根目录已满，无法创建新目录！");
//                fatTable.getFat()[diskNum] = 0;//无法创建目录，则释放之前的磁盘块占用
//                return null;
//            }
//            String name = path.substring(path.lastIndexOf("/") + 1);
//            folder = new Directory(diskNum, root, path);
//            //fatTable.getFat()[diskNum] = -1;
//            DISK.getDisk()[diskNum][0] = folder;
//            root.getChildren().add(folder);
//            return folder;
//        } else {
//            //fatTable.getFat()[diskNum] = -1;
//            if (parent.getChildren().size() != 0 && parent.getChildren().size() % 8 == 0) {//如果父目录所占磁盘已满，则新建一个磁盘块作为父目录的下一个磁盘块
//                int newParentDisk = findFreeDiskBlock();
//                if (newParentDisk == -1) {
//                    System.out.println("磁盘空间不足！");
//                    fatTable.getFat()[diskNum] = 0;//无法创建目录，则释放之前的磁盘块占用
//                    return null;
//                }
//
//                int endDisk = findLastDisk(parent.getStartDisk());//更新fat表
//                fatTable.getFat()[endDisk] = newParentDisk;
//                fatTable.getFat()[newParentDisk] = -1;
//            }
//
//            folder = new Directory(diskNum, parent, path);
//            DISK.getDisk()[diskNum][0] = folder;
//            parent.getChildren().add(folder);
//            return folder;
//        }
        if(disk.findDirectory(path) != null){
            System.out.println("文件夹已存在！");
            return null;
        }

        Directory folder = null;
        String parentPath = path.substring(0,path.lastIndexOf("/"));
        Directory parent = (Directory) findDirectory(parentPath);

        if(parent == null){
            System.out.println("父目录不存在！");
            return null;
        }else{
            int startDisk = disk.findFreeDiskBlock();

            if(startDisk == -1){
                System.out.println("磁盘空间不足！");
                return null;
            }

            String name = path.substring(path.lastIndexOf("/") + 1);// \u0000为空字符
            folder = new Directory(name,(byte)0,true,false,false,true,disk,parent,new ArrayList<>());
            folder.setStartBlockNum((byte)startDisk);
            disk.getFat().setNextBlockIndex(startDisk,Fat.TERMINATED);
            parent.addChildren(folder);
            return folder;
        }
    }

    public void deleteDirectory(String path, boolean isDeleteNotEmpty) {
        // 在磁盘上查找指定路径的目录
        Item item = this.findDirectory(path);
        // 如果目录为空，或者找到的项是一个Txt文件或Exe文件，则不是文件夹
        if (item == null) {
            System.out.println("不是文件夹！");
            return;
        }

        if (!isDeleteNotEmpty) {
            if (item.getSize() != 0) {
                System.out.println("不是空目录");
            }
            return;
        }

        this.updateParent(item);
    }

    public void deleteFile(String path) {
        Item item = this.findFile(path);
        // 如果目录为空，或者找到的项是一个目录，则不是文件
        if (item == null) {
            System.out.println("不是文件或不存在！");
            return;
        }
        this.updateParent(item);
    }

    private void updateParent(Item item) {
        Directory directory = (Directory) item;

        // 获取目录的父目录，并确保类型转换安全
        Item parentItem = directory.getParent();
        if (!(parentItem instanceof Directory parent)) {
            System.out.println("父目录类型错误！");
            return;
        }
        // 从父目录中移除当前目录
        parent.removeChild(directory);
        // 格式化FAT表，从指定目录的起始块开始（确保directory非空）
        disk.formatFatTable(directory.getStartBlockNum());
        // 调用父目录的getChildren方法（更新parent的子节点）
        parent.getChildren();

        // 将修改后的父目录内容写回磁盘
        parent.writeContentToDisk();
    }

    public int getSize(Item item) {
        if (item == null) {
            return 0;
        }
        if (item instanceof Directory) {
            ((Directory) item).getChildren();
        }
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
        } else {
            //更新fat表
            int endDisk = disk.findLastDisk(item.getStartBlockNum());

            for (Integer index : list) {
                fat.setNextBlockIndex(endDisk,index);
                fat.setNextBlockIndex(index,Fat.TERMINATED);
                endDisk = index;
            }

            switch (type) {
                case EXE -> {
                    Exe exe = (Exe) item;
                    exe.initFromString(content);
                    updateFileSize(exe, exe.getSize());
                    exe.writeContentToDisk(exe.getDisk());
                }
                case FILE -> {
                    Txt txt = (Txt) item;
                    txt.initFromString(content);
                    updateFileSize(txt, txt.getSize());
                    txt.writeContentToDisk(txt.getDisk());
                }
            }

            System.out.println("写入成功!");
        }
    }

    public boolean copyFile(String sourcePath, String targetPath) {
        Item srcItem = this.findFile(sourcePath);
        if (srcItem == null) {
            System.out.println("文件不存在!");
            return false;
        }

        Disk disk = srcItem.getDisk();
        Fat fat = disk.getFat();

        int needDiskNum  = srcItem.calculateTotalBlockNum(fat);
        List<Integer> needDiskBlocks = disk.findFreeDiskBlock(needDiskNum);
        if (needDiskBlocks.size() < needDiskNum) {
            System.out.println("磁盘空间不足!");
            return false;
        }

        String parentPath = targetPath.substring(0, targetPath.lastIndexOf("/"));
        Directory parent = (Directory) this.findDirectory(parentPath);
        if (parent == null) {
            System.out.println("操作不允许!");
            return false;
        }

        int sourceBlock = srcItem.getStartBlockNum();
        for(Integer block : needDiskBlocks) {
            disk.copyDiskBlock(sourceBlock, block);
            sourceBlock = fat.getNextBlockIndex(sourceBlock);
        }
        int cur = needDiskBlocks.get(0);
        int pre = cur;
        for (int i = 1; i < needDiskBlocks.size(); i++) {
            cur = needDiskBlocks.get(i);
            fat.setNextBlockIndex(pre, cur);
            pre = cur;
        }
        fat.setNextBlockIndex(cur, Fat.TERMINATED);
        Item newFile = FileService.getItemFromCreate(srcItem.getName(),
                (byte)srcItem.getType(),
                srcItem.isReadOnly(),
                srcItem.isSystemFile(),
                srcItem.isRegularFile(),
                srcItem.isDirectory(),
                disk,
                parent);

        parent.addChildren(newFile);
        return true;
    }

//    public boolean copyFolder(String  sourcePath, String targetPath) {
//
//        try {
//            int needDiskNum = directory.calculateTotalBlockNum(directory.getDisk().getFat());
//            List<Integer> needDiskBlock = disk.findFreeDiskBlock(needDiskNum);
//            if (needDiskBlock.size() < needDiskNum) {
//                System.out.println("磁盘空间不足！");
//                return false;
//            }
//
//            String parentPath = newPath.substring(0, newPath.lastIndexOf("/"));
//            Directory parent = FileController.getInstance().createDirectory(parentPath);
//            for (Integer block : needDiskBlock) {
//
//            }
//            newFolder.setParent(parent);
//            newFolder.setStartDisk(needDisk.get(0));
//            for (int i = 0; i < needDisk.size() - 1; i++) {
//                fatTable.getFat()[needDisk.get(i)] = needDisk.get(i + 1);
//            }
//            fatTable.getFat()[needDisk.get(needDiskNum - 1)] = -1;
//            return true;
//        } catch (CloneNotSupportedException e) {
//            System.out.println("复制失败!");
//            throw new RuntimeException(e);
//        }
//    }

    public void updateFileSize(Item file, int size) {
        file.setSize(size);
        Directory parent = (Directory) file.getParent();
        while (parent != null) {
            updateDirectorySize(parent);
            parent = (Directory) parent.getParent();
        }
    }

    public void updateDirectorySize(Directory directory) {
        directory.getChildren();
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
                // 类型字节不是'e'，返回null
                return null;
            } else {
                // 类型字节为0，返回txt实例
                return new Txt(disk, data);
            }
        }
    }


    public static Item getItemFromCreate(String name, byte type, boolean readOnly, boolean systemFile,
                                  boolean regularFile, boolean isDirectory,Disk disk,Item parent){
        if(isDirectory) {
            return new Directory(name, (byte) 0, readOnly, systemFile, regularFile, true, disk, parent,null);
        }else {
            if(type == 'e') {
                return new Exe(name, (byte) 'e', readOnly, systemFile, regularFile, false, disk, parent,null);
            }else {
                return new Txt(name, (byte) 0, readOnly, systemFile, regularFile, false, disk, parent,null);
            }
        }
    }
}
