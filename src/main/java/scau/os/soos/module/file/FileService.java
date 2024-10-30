package scau.os.soos.module.file;

import scau.os.soos.module.file.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileService {

    private Disk disk;

    public FileService() {

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
        Directory parent = (Directory) findFile(parentPath);
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
                file = new Exe(name,'e',true,false,true,false,disk,parent,"");
                file.setStartBlockNum((byte)startDisk);
                ((Exe)file).writeContentToDisk(disk);
            }else{
                file = new Txt(name,'t',true,false,true,false,disk,parent,"");
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
            folder = new Directory(name,'\u0000',true,false,false,true,disk,parent,new ArrayList<>());
            folder.setStartBlockNum((byte)startDisk);
            disk.getFat().setNextBlockIndex(startDisk,Fat.TERMINATED);
            parent.addChildren(folder);
            return folder;
        }
    }



    public void deleteFolder(String path) {
        Item folder = disk.find(path);
        if (folder == null || folder instanceof Txt || folder instanceof Exe) {
            System.out.println("不是文件夹！");
            return;
        }
//        if (folder != null) {
//            for (Object e : folder.getChildren()) {
//                if (e instanceof MyFile) {
//                    deleteFile(((MyFile) e).getPath());
//                } else {
//                    deleteFolder(((Folder) e).getPath());
//                }
//            }
//            formatFatTable(folder.getStartDisk());
//        }
        //将空文件夹从父目录和磁盘中删除，并更新父目录的大小Folder
        Directory parent = folder.getParent();
        parent.getChildren().remove(folder);
        formatFatTable(folder.getStartDisk());
        updateFolderSize(parent);
    }

    public int getFolderSize(String path) {
        Directory folder = findFolder(path);
        if (folder != null) {
            return folder.getSize();
        } else return -1;
    }

    public int getFileSize(Item file) {
        return file.getSize();
    }

    public void deleteFile(String path) {
        Txt file = findFile(path);
//        if(file != null) {
//            file.getParent().getChildren().remove(file);
//            updateFileSize(file);
//            formatFatTable(file.getStartDisk());
//        }
        Directory parent = file.getParent();
        parent.getChildren().remove(file);
        int startDisk = file.getStartDisk();
        //DISK.getDisk()[startDisk][0] = null;
        formatFatTable(startDisk);

        updateFileSize(file);

    }

    public void writeFile(Item file, String content) {
        //获取需要写入的字符串长度，计算需要多少个磁盘块
        List<Integer> list = new ArrayList<>();
        Disk disk = file.getDisk();
        Fat fat = file.getDisk().getFat();
        //需要的块数=文件总大小需要的磁盘块数-已占有的块数
        int needDiskNum = (int) Math.ceil((double) content.length() / Disk.BYTES_PER_BLOCK) - file.calculateTotalBlockNum(fat);
        int num = 0;
        for (int i = 3; i < Disk.BLOCKS_PER_DISK && num < needDiskNum; i++) {
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
            int endDisk = disk.findLastDisk(file.getStartBlockNum());

            for (Integer index : list) {
                fat.allocateNewBlock(endDisk, index);
                endDisk = index;
            }

            file.initContentFromString(content);
            file.setContent(str);
            file.setSize(str.length());
            updateFileSize(file);
            file.setNumOfDiskBlock(file.getNumOfDiskBlock() + list.size());
            System.out.println("写入成功!");
        }
    }

    public boolean copyFile(Txt file, String path) {
        try {
            int needDiskNum = file.getNumOfDiskBlock();
            List<Integer> needDisk = findFreeDiskBlock(needDiskNum);
            if (needDisk.size() < needDiskNum) {
                System.out.println("拷贝失败!");
            }

            String parentPath = path.substring(0, path.lastIndexOf("/"));
            Directory parent = FileController.getInstance().createDirectory(parentPath);


            //MyFile newFile = createFile(path);
            //Folder parent = newFile.getParent();
            Txt newFile = (Txt) file.clone();
            newFile.setParent(parent);
            newFile.setStartDisk(needDisk.get(0));
            parent.getChildren().add(newFile);
            for (int i = 0; i < needDisk.size() - 1; i++) {
                fatTable.getFat()[needDisk.get(i)] = needDisk.get(i + 1);
                DISK.getDisk()[needDisk.get(i)][0] = newFile;
            }
            fatTable.getFat()[needDisk.get(needDiskNum - 1)] = -1;
            return true;
        } catch (CloneNotSupportedException e) {
            System.out.println("复制失败!");
            throw new RuntimeException(e);
        }
    }

    public boolean copyFolder(Directory directory, String newPath) {
        try {
            int needDiskNum = directory.calculateTotalBlockNum(directory.getDisk().getFat());
            List<Integer> needDiskBlock = disk.findFreeDiskBlock(needDiskNum);
            if (needDiskBlock.size() < needDiskNum) {
                System.out.println("磁盘空间不足！");
                return false;
            }

            String parentPath = newPath.substring(0, newPath.lastIndexOf("/"));
            Directory parent = FileController.getInstance().createDirectory(parentPath);
            Directory newFolder = (Directory) folder.clone();
            newFolder.setParent(parent);
            newFolder.setStartDisk(needDisk.get(0));
            for (int i = 0; i < needDisk.size() - 1; i++) {
                fatTable.getFat()[needDisk.get(i)] = needDisk.get(i + 1);
            }
            fatTable.getFat()[needDisk.get(needDiskNum - 1)] = -1;
            return true;
        } catch (CloneNotSupportedException e) {
            System.out.println("复制失败!");
            throw new RuntimeException(e);
        }
    }

    public void updateFileSize(Txt file, int size) {
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
}
