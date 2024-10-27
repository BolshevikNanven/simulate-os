package scau.os.soos.module.file;

import scau.os.soos.module.file.model.Disk;
import scau.os.soos.module.file.model.Fat;
import scau.os.soos.module.file.model.Folder;
import scau.os.soos.module.file.model.MyFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileService {

    private Disk DISK;
    private Fat fatTable;
    public FileService() {
        // TODO: 2024/9/28 读某个模拟文件作为磁盘disk 
//        this.DISK = new Disk();
//        this.fatTable = (Fat) DISK.getDisk()[0][0];

        Disk disk = new Disk();
        Fat fat = new Fat();
        disk.getDisk()[0][0]=fat;

        Folder root = new Folder(2,null,"/");
        disk.getDisk()[2][0]=root;

        Folder a = new Folder(3,root,"/a");
        Folder b = new Folder(4,root,"/b");
        disk.getDisk()[3][0]=a;
        disk.getDisk()[4][0]=b;
        fat.getFat()[3] = -1;
        fat.getFat()[4] = -1;
        root.getChildren().add(a);
        root.getChildren().add(b);

        this.DISK = disk;
        this.fatTable = (Fat) disk.getDisk()[0][0];

    }
    // 查找空闲磁盘块的编号
    public int findFreeDiskBlock(){

        //从第3块磁盘块开始查询，如果找到空闲磁盘块则返回该编号，否则返回-1
        for(int i=3;i<Disk.BLOCKS_PER_DISK;i++){
            if(fatTable.isEmptyDisk(i)){
                return i;
            }
        }
        return -1;
    }

    public List<Integer> findFreeDiskBlock(int num){
        List<Integer> list = new ArrayList<>();
        for(int i=3;i<Disk.BLOCKS_PER_DISK && list.size() < num;i++){
            if(fatTable.isEmptyDisk(i)){
                list.add(i);
            }
        }
        return list;
    }

    public MyFile createFile(String path){

        if(findFile(path) != null){
            System.out.println("文件已存在！");
            return null;
        }

        MyFile myFile = null;

        //根据路径获取文件名和父目录的路径
        String name = path.substring(path.lastIndexOf("/")+1);
        String parentPath = path.substring(0,path.lastIndexOf("/"));//父目录不存在则创建
        Folder parent = FileController.getInstance().createDirectory(parentPath);

        int diskNum = findFreeDiskBlock();//磁盘块占用
        if(diskNum == -1){
            return null;
        }else{
            fatTable.getFat()[diskNum] = -1;//占用磁盘块
        }


        //创建文件对象
        if(path.contains(".e")){
            myFile = new MyFile(name,path,0,"e","exe",diskNum,1,parent);
        }else{
            myFile = new MyFile(name,path,0,"t","txt",diskNum,1,parent);
        }
        parent.getChildren().add(myFile);
        DISK.getDisk()[diskNum][0] = myFile;
        return myFile;
    }

    public boolean isEmpty(int diskNum){//磁盘块是否为空
       return fatTable.isEmptyDisk(diskNum);
    }

    public Folder createFolder(Folder parent,String path) {

        if(findFolder(path) != null){
            System.out.println("文件夹已存在！");
            return null;
        }

        Folder folder = null;

        int diskNum = findFreeDiskBlock();
        if(diskNum == -1){
            return null;
        }else{
            fatTable.getFat()[diskNum] = -1;//占用磁盘块
        }

        //创建在根目录下的
        if(parent == null){
            Folder root = (Folder) DISK.getDisk()[2][0];
            if(root.getChildren().size()==8){
                System.out.println("根目录已满，无法创建新目录！");
                fatTable.getFat()[diskNum] = 0;//无法创建目录，则释放之前的磁盘块占用
                return null;
            }
            String name = path.substring(path.lastIndexOf("/")+1);
            folder =new Folder(diskNum,root,path);
            //fatTable.getFat()[diskNum] = -1;
            DISK.getDisk()[diskNum][0] = folder;
            root.getChildren().add(folder);
            return folder;
        }else{
            //fatTable.getFat()[diskNum] = -1;
            if(parent.getChildren().size()!=0&&parent.getChildren().size()%8==0){//如果父目录所占磁盘已满，则新建一个磁盘块作为父目录的下一个磁盘块
               int newParentDisk = findFreeDiskBlock();
               if(newParentDisk == -1){
                   System.out.println("磁盘空间不足！");
                   fatTable.getFat()[diskNum] = 0;//无法创建目录，则释放之前的磁盘块占用
                   return null;
               }

               int endDisk=findLastDisk(parent.getStartDisk());//更新fat表
               fatTable.getFat()[endDisk] = newParentDisk;
               fatTable.getFat()[newParentDisk] = -1;
            }

            folder = new Folder(diskNum,parent,path);
            DISK.getDisk()[diskNum][0] = folder;
            parent.getChildren().add(folder);
            return folder;
        }

    }
    public void deleteFolder(String path) {
        Folder folder = findFolder(path);
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
        Folder parent =folder.getParent();
        parent.getChildren().remove(folder);
        formatFatTable(folder.getStartDisk());
        updateFolderSize(parent);
    }



    public void formatFatTable(int diskNum){

        int[] fat = fatTable.getFat();
        int index = diskNum;
        int nextIndex = fat[index];

        // 将起始块标记为未使用
        DISK.getDisk()[index][0] = null;
        fat[index] = 0;
        index = nextIndex;

        // 循环FAT表，将该磁盘块之后的磁盘块全部置为0，直到下一个磁盘块为空
        while (index !=-1&&fat[index] != -1) {
            nextIndex = fat[index];
            fat[index] = 0; // 标记当前块为未使用
            index = nextIndex;
            if (index == -1) {
                break; // 退出循环，因为下一个块已经是未使用的
            }
            DISK.getDisk()[index][0] = null; // 标记下一个块为未使用
        }
        if(index != -1) fat[index] = 0; // 标记最后一个块为未使用

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


           if(DISK.getDisk()[i][0]==null) continue;



           if(DISK.getDisk()[i][0] instanceof MyFile && ((MyFile) DISK.getDisk()[i][0]).getPath().equals(path)){
               String name =((MyFile) DISK.getDisk()[i][0]).getPath();
               return (MyFile) DISK.getDisk()[i][0];
           }
       }
       return null;
    }
    public Folder findFolder(String path) {
        for(int i=2;i<Disk.BLOCKS_PER_DISK;i++){

            if(DISK.getDisk()[i][0]==null) continue;

            if(DISK.getDisk()[i][0] instanceof Folder && ((Folder) DISK.getDisk()[i][0]).getPath().equals(path)){

                String name =((Folder) DISK.getDisk()[i][0]).getPath();

                return (Folder) DISK.getDisk()[i][0];
            }
        }
        return null;
    }

    public void deleteFile(String path) {
        MyFile file = findFile(path);
//        if(file != null) {
//            file.getParent().getChildren().remove(file);
//            updateFileSize(file);
//            formatFatTable(file.getStartDisk());
//        }
        Folder parent =file.getParent();
        parent.getChildren().remove(file);
        int startDisk = file.getStartDisk();
        //DISK.getDisk()[startDisk][0] = null;
        formatFatTable(startDisk);

        updateFileSize(file);

    }


    public void writeFile(MyFile file){

        System.out.println("请输入文件内容！");
        Scanner sc = new Scanner(System.in);
        String str = sc.nextLine();

        //获取需要写入的字符串长度，计算需要多少个磁盘块
        List<Integer> list = new ArrayList<>();
        //需要的块数=文件总大小需要的磁盘块数-已占有的块数
        int needDiskNum = (int)Math.ceil(str.length()/64.0)-file.getNumOfDiskBlock();
        int num=0;
        for(int i=3;i<Disk.BLOCKS_PER_DISK&&num<needDiskNum;i++) {
            if (fatTable.isEmptyDisk(i)) {
                list.add(i);
                num++;
            }
        }

        //如果磁盘块不足，则无法写入文件
        if(num<needDiskNum) {
            System.out.println("磁盘空间不足!");
        }else{
            //更新fat表

            int endDisk=findLastDisk(file.getStartDisk());


            for(int i=0;i<list.size();i++){
                fatTable.getFat()[endDisk] = list.get(i);
                endDisk = list.get(i);
            }

            fatTable.getFat()[endDisk] = -1;
            file.setContent(str);
            updateFileSize(file);
            file.setNumOfDiskBlock(file.getNumOfDiskBlock()+list.size());
            System.out.println("写入成功!");
        }
    }

    public boolean copyFile(MyFile file,String path){
        try {
            int needDiskNum = file.getNumOfDiskBlock();
            List<Integer> needDisk = findFreeDiskBlock(needDiskNum);
            if(needDisk.size()<needDiskNum){
                System.out.println("拷贝失败!");
            }

            String parentPath = path.substring(0,path.lastIndexOf("/"));
            Folder parent = FileController.getInstance().createDirectory(parentPath);


            //MyFile newFile = createFile(path);
            //Folder parent = newFile.getParent();
            MyFile newFile = (MyFile)file.clone();
            newFile.setParent(parent);
            newFile.setStartDisk(needDisk.get(0));
            parent.getChildren().add(newFile);
            for(int i=0;i<needDisk.size()-1;i++){
                fatTable.getFat()[needDisk.get(i)] = needDisk.get(i+1);
                DISK.getDisk()[needDisk.get(i)][0] = newFile;
            }
            fatTable.getFat()[needDisk.get(needDiskNum-1)] = -1;
            return true;
        } catch (CloneNotSupportedException e) {
            System.out.println("复制失败!");
            throw new RuntimeException(e);
        }
    }

    public boolean copyFolder(Folder folder, String newPath) {
        try {

            int needDiskNum = folder.getNumOfDiskBlock();
            List<Integer> needDisk = findFreeDiskBlock(needDiskNum);
            if(needDisk.size()<needDiskNum){
                System.out.println("拷贝失败!");
            }

            String parentPath = newPath.substring(0,newPath.lastIndexOf("/"));
            Folder parent = FileController.getInstance().createDirectory(parentPath);
            Folder newFolder = (Folder)folder.clone();
            newFolder.setParent(parent);
            newFolder.setStartDisk(needDisk.get(0));
            for(int i=0;i<needDisk.size()-1;i++){
                fatTable.getFat()[needDisk.get(i)] = needDisk.get(i+1);
            }
            fatTable.getFat()[needDisk.get(needDiskNum-1)] = -1;
            return true;
        } catch (CloneNotSupportedException e) {
            System.out.println("复制失败!");
            throw new RuntimeException(e);
        }
    }

    public void updateFileSize(MyFile file){
        Folder parent = file.getParent();
        while(parent != null){
            updateFolderSize(parent);
            parent = parent.getParent();
        }
    }

    public void updateFolderSize(Folder folder){
        folder.setSize(folder.getSize());
    }

    public int findLastDisk(int startDisk){
        int endDisk=startDisk;
        while(fatTable.getFat()[endDisk]!=-1){
            endDisk = fatTable.getFat()[endDisk];
        }
        return endDisk;
    }

}
