package scau.os.soos.module.file;

import scau.os.soos.module.file.model.Disk;
import scau.os.soos.module.file.model.Fat;
import scau.os.soos.module.file.model.Folder;
import scau.os.soos.module.file.model.MyFile;

public class FileService {

    private Disk DISK;
    private Fat fatTable;
    public FileService() {
        // TODO: 2024/9/28 读某个模拟文件作为磁盘disk 
        this.DISK = new Disk();
        this.fatTable = (Fat) DISK.getDisk()[1][1];
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

    public MyFile createFile(String fileName){
        MyFile myFile = null;
        if(fileName.contains(".e")){
            int diskNum = findFreeDiskBlock();
            myFile = new MyFile(fileName,0,"e","exe",diskNum,1);
        }else{
            int diskNum = findFreeDiskBlock();
            myFile = new MyFile(fileName,0,"d","txt",diskNum,1);
        }
        return myFile;
    }

    public boolean isEmpty(int diskNum,Fat FatTable){
       return FatTable.isEmptyDisk(diskNum);
    }

    public Folder createFolder(String name,Folder parent) {
        int diskNum = findFreeDiskBlock();
        if (diskNum != -1) {
            return new Folder(name, diskNum,parent);
        }
        else return null;
    }
    public void deleteFolder(String name) {
    }
    public double getFolderSize(String name) {
        return 0;
    }
    public int getFileSize(MyFile file) {
        return 0;
    }




}
