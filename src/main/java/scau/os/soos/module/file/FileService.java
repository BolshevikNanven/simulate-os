package scau.os.soos.module.file;

import javafx.scene.Parent;
import scau.os.soos.module.file.model.Disk;
import scau.os.soos.module.file.model.Fat;
import scau.os.soos.module.file.model.Folder;
import scau.os.soos.module.file.model.MyFile;

import java.util.Scanner;

public class FileService {

    private Disk DISK;
    private Fat fatTable;
    public FileService() {
        // TODO: 2024/9/28 读某个模拟文件作为磁盘disk 
        this.DISK = new Disk();
        this.fatTable = (Fat) DISK.getDisk()[0][0];
    }
    // 查找空闲磁盘块的编号
    public int findFreeDiskBlock(){
        for(int i=3;i<Disk.BLOCKS_PER_DISK;i++){
            if(fatTable.isEmptyDisk(i)){
                return i;
            }
        }
        return -1;
    }

    public MyFile createFile(String path){
        MyFile myFile = null;
        String[] name = path.split("/");
        int diskNum = findFreeDiskBlock();
        if(path.contains(".e")){
            myFile = new MyFile(name[name.length-1],path,8,"e","exe",diskNum,1);
        }else{
            myFile = new MyFile(name[name.length-1],path,8,"t","txt",diskNum,1);
        }
        return myFile;
    }

    public boolean isEmpty(int diskNum){
       return fatTable.isEmptyDisk(diskNum);
    }

    public Folder createFolder(String name,Folder parent,String path) {
        int diskNum = findFreeDiskBlock();
        if(diskNum == -1){
            return null;
        }
        if(parent == null){
            Folder root = (Folder) DISK.getDisk()[2][0];
            if(root.getChildren().size()==8){
                System.out.println("根目录已满，无法创建新目录！");
                return null;
            }
            Folder folder =new Folder(name,diskNum,root,path);
            root.getChildren().add(folder);
            return folder;
        }else{
            return new Folder(name, diskNum,parent,path);
        }

    }
    public void deleteFolder(String path) {
        Folder folder = findFolder(path);
        if (folder != null) {
            for (Object e : folder.getChildren()) {
                if (e instanceof MyFile) {
                    deleteFile(((MyFile) e).getPath());
                } else {
                    deleteFolder(((Folder) e).getPath());
                }
            }
            formatFatTable(folder.getDiskNum());
        }
    }



    public void formatFatTable(int diskNum){
        int index = diskNum;
        while(fatTable.getFat()[index]!=-1){
            int temp = index;
            index = fatTable.getFat()[index];
            fatTable.getFat()[temp] = 0;
        }
    }
    public int getFolderSize(String path) {
      Folder folder = findFolder(path);
      if(folder!=null){
          return folder.getSize();
      }
      else return -1;
    }
    public int getFileSize(MyFile file) {
        return file.getSize();
    }
    public MyFile findFile(String path) {
       for(int i=2;i<Disk.BLOCKS_PER_DISK;i++){
           if(DISK.getDisk()[i][0] instanceof MyFile && ((MyFile) DISK.getDisk()[i][0]).getPath().equals(path)){
               return (MyFile) DISK.getDisk()[i][0];
           }
       }
       return null;
    }
    public Folder findFolder(String path) {
        for(int i=2;i<Disk.BLOCKS_PER_DISK;i++){
            if(DISK.getDisk()[i][0] instanceof Folder && ((Folder) DISK.getDisk()[i][0]).getPath().equals(path)){
                return (Folder) DISK.getDisk()[i][0];
            }
        }
        return null;
    }

    public void deleteFile(String path) {
        MyFile file = findFile(path);
        if(file != null) {
            formatFatTable(file.getDiskNum());
        }
    }


    public void writeFile(MyFile file){
        System.out.println("请输入文件内容！");
        Scanner sc = new Scanner(System.in);
        String str = sc.nextLine();
        file.setContent(str);
        System.out.println("写入成功!");
    }

    public boolean copyFile(MyFile file,String path){
        try {
            MyFile newFile = createFile(path);
            Folder parent = newFile.getParent();
            newFile = (MyFile)file.clone();
            newFile.setParent(parent);
            return true;
        } catch (CloneNotSupportedException e) {
            System.out.println("复制失败!");
            throw new RuntimeException(e);
        }
    }

    public boolean copyFolder(Folder folder, String newPath) {
        try {
            String[] paths = newPath.split("/");
            String parentPath = "";
            for (int i=0;i<paths.length-1;i++){
                parentPath = newPath + "/"+paths[i];
            }
            Folder parent = FileController.getInstance().createDirectory(parentPath);
            Folder newFolder = (Folder)folder.clone();
            newFolder.setParent(parent);
            return true;
        } catch (CloneNotSupportedException e) {
            System.out.println("复制失败!");
            throw new RuntimeException(e);
        }
    }


}
