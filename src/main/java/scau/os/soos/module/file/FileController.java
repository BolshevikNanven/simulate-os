package scau.os.soos.module.file;

import scau.os.soos.common.enums.FILE_TYPE;
import scau.os.soos.module.Module;
import scau.os.soos.common.exception.*;
import scau.os.soos.module.file.model.Directory;
import scau.os.soos.module.file.model.Fat;
import scau.os.soos.module.file.model.Item;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileController implements Module {
    private static FileController instance;
    private final FileService fileService;

    public static FileController getInstance() {
        if (instance == null) {
            instance = new FileController();
        }
        return instance;
    }

    private FileController() {
        fileService = new FileService();
    }

    /**
     * 创建文件
     */
    public Item createFile(String path) throws ItemAlreadyExistsException, ItemNotFoundException, DiskSpaceInsufficientException, IllegalPathException {
         return fileService.createFile(path);
    }

    /**
     * 删除文件
     */
    public void deleteFile(String path) throws ItemNotFoundException, IllegalPathException {
        try {
            fileService.delete(path,false,true);
        } catch (DirectoryNoEmptyException ignored) {

        }
    }

    /**
     * 写文件
     */
    public void writeFile(Item file, String content) throws DiskSpaceInsufficientException {
        fileService.writeFile(file, content);
    }

    /**
     * 读文件
     */
    public byte[] readFile(Item file) throws ItemNotFoundException {
        return fileService.readFile(file);
    }

    /**
     * 拷贝文件
     */
    public void copyFile(String sourcePath, String targetPath) throws ItemAlreadyExistsException, ItemNotFoundException, DiskSpaceInsufficientException, ItemNotFoundException, IllegalPathException {
        fileService.copy(sourcePath, targetPath,false);
    }

    /**
     * 移动文件
     */
    public void moveFile(String sourcePath, String targetPath) throws ItemAlreadyExistsException, ItemNotFoundException, DiskSpaceInsufficientException, ItemNotFoundException, IllegalPathException {
        fileService.copy(sourcePath, targetPath,true);
    }

    /**
     * 建立目录
     */
    public Directory createDirectory(String path) throws ItemAlreadyExistsException, ItemNotFoundException, DiskSpaceInsufficientException {
        return fileService.createDirectory(path);
    }

    /**
     * 删除空目录
     */
    public void deleteEmptyDirectory(String path) throws ItemNotFoundException, DirectoryNoEmptyException, IllegalPathException {
        fileService.delete(path,true,false);
    }

    /**
     * 删除目录
     */
    public void deleteDirectory(String path) throws ItemNotFoundException, IllegalPathException {
        try {
            fileService.delete(path,true,true);
        } catch (DirectoryNoEmptyException ignored) {

        }
    }

    public void reName(Item item, String newName) throws ItemAlreadyExistsException, IllegalNameException {
        fileService.reName(item, newName);
    }

    public void reAttribute(Item item,boolean readOnly, boolean systemFile, boolean regularFile, boolean isDirectory){
        fileService.reAttribute(item,readOnly,systemFile,regularFile,isDirectory);
    }

    public Item findItem(String path, FILE_TYPE type) throws ItemNotFoundException {
        // 获取后缀

        return fileService.findItem(path,type);
    }

    public boolean isExistedDirectory(String path) throws ItemNotFoundException {
        return findItem(path, FILE_TYPE.DIRECTORY) != null;
    }

    // 待写
    public void reAttribute(String path, boolean readOnly, boolean systemFile, boolean regularFile, boolean isDirectory){
        return ;
    }

    // 待写 格式化硬盘
    public void formatDisk(Path path) {
        return ;
    }

    // 待写 返回文件内容
    public String typeFile(String path) {
        return "";
    }

    // 待写
    public Item findItem(String path){
        return null;
    }

    // 待写 分区
    private void partitionDisk(String src,String dec,int size){
        // 磁盘分区 从src 抽取size 分配到dec
        // 判断src 是否有足够空闲空间
        // 判断dec 是否存在，不存在则创建
        // 从src转移size到dec
        // 判断src是否变为0，是则删除src
    }



    public int getFileSize(Item file) {
        return fileService.getSize(file);
    }

    public List<Item> getRoot(){
        List<Item> roots = new ArrayList<>();
        roots.add(fileService.getRoots());
        return roots;
    }

    public Fat getFat() {
        return fileService.getDisk().getFat();
    }

    @Override
    public void run() {

    }

    public static void main(String[] args) {//显示根目录
//        getInstance().createDirectory("/a");
//        getInstance().createDirectory("/a/b");
//        getInstance().createFile("/e.t");
//        getInstance().copyFile("/e.t","a/b/");

//        Directory root = (Directory) FileServiceUtil.find(getInstance().fileService.getDisk(), "/", FILE_TYPE.DIRECTORY);
//        System.out.println(root.getPath());
//        System.out.println(FileServiceUtil.find(getInstance().fileService.getDisk(), "/e.t", FILE_TYPE.TXT).getPath());
//        getInstance().writeFile(FileServiceUtil.find(getInstance().fileService.getDisk(), "/e.t", FILE_TYPE.TXT),"12345678901234567890");
//        System.out.println(FileServiceUtil.find(getInstance().fileService.getDisk(), "/e.t", FILE_TYPE.TXT));

//        getInstance().copyFile("/e.t", "/a/");
//        System.out.println(FileServiceUtil.find(getInstance().fileService.getDisk(), "/a/e.t", FILE_TYPE.TXT).getPath());

//        getInstance().fileService.getDisk().disk2file();

////
//        getInstance().createDirectory("/a");
//        getInstance().createDirectory("/a/b");
//        getInstance().createDirectory("/a/c");
//        getInstance().createDirectory("/a/d");
//        getInstance().createDirectory("/a/e");
//        getInstance().createFile("/a/f.t");
//        getInstance().createFile("/a/g.t");
//        getInstance().createFile("/a/h.e");
//        getInstance().createFile("/a/i.e");
//        getInstance().createFile("/a/j.e");
//        getInstance().createFile("/a/k.e");
//        getInstance().createFile("/a/l.e");
//        getInstance().createFile("/a/m.e");
//        getInstance().createFile("/a/n.e");
//        getInstance().createFile("/a/o.e");
//        getInstance().createFile("/a/p.e");
//        getInstance().createFile("/a/q.e");
//        getInstance().createFile("/a/r.e");


//        System.out.println(FileServiceUtil.find(getInstance().fileService.getDisk(),"/a/m.e",FILE_TYPE.EXE).getPath());
//
//
//        System.out.println(FileServiceUtil.find(getInstance().fileService.getDisk(), "/a", FILE_TYPE.DIRECTORY));



//        getInstance().fileService.getDisk().disk2file();


//        try {
//            getInstance().createDirectory("/C:");
//        } catch (ItemAlreadyExistsException e) {
//            throw new RuntimeException(e);
//        } catch (ItemNotFoundException e) {
//            throw new RuntimeException(e);
//        } catch (DiskSpaceInsufficientException e) {
//            throw new RuntimeException(e);
//        }

        Directory root = (Directory) FileService.find(getInstance().fileService.getDisk(), "/", FILE_TYPE.DIRECTORY);
        Directory C = (Directory) FileService.find(getInstance().fileService.getDisk(), "/C:", FILE_TYPE.DIRECTORY);
        System.out.println("---");
//        System.out.println(getInstance().fileService.getDisk().);
        try {
            getInstance().createDirectory( "/C:/a");
            getInstance().createDirectory( "/C:/b");
        } catch (ItemAlreadyExistsException e) {
            throw new RuntimeException(e);
        } catch (ItemNotFoundException e) {
            throw new RuntimeException(e);
        } catch (DiskSpaceInsufficientException e) {
            throw new RuntimeException(e);
        }
        System.out.println("-------------------");
        System.out.println(root.getPath());
        System.out.println(root.getChildren());
        System.out.println(C.getChildren());
        FileController.getInstance().getFat().diskPartition("/C:",50);
        FileController.getInstance().getFat().diskPartition("/C:",50);
        FileController.getInstance().getFat().diskPartition("/C:",50);
        FileController.getInstance().getFat().writeFatToDisk();


        for (byte[]bytes: getInstance().fileService.getDisk().getDisk()){
            for(byte b:bytes) System.out.print((b & 0xFF) + " ");
        System.out.println();
        }

    }
}
