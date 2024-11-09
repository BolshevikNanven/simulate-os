package scau.os.soos.module.file;

import scau.os.soos.common.enums.FILE_TYPE;
import scau.os.soos.module.Module;
import scau.os.soos.module.file.model.Directory;
import scau.os.soos.module.file.model.Fat;
import scau.os.soos.module.file.model.Item;
import scau.os.soos.module.file.util.FileServiceUtil;

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
    public Item createFile(String path){
         return fileService.createFile(path);
    }

    /**
     * 删除文件
     */
    public void deleteFile(String path) {
        fileService.delete(path,false,true);
    }

    /**
     * 写文件
     */
    public void writeFile(Item file, String content) {
        fileService.writeFile(file, content);
    }

    /**
     * 读文件
     */
    public byte[] readFile(Item file) {
        return fileService.readFile(file);
    }

    /**
     * 拷贝文件
     */
    public void copyFile(String sourcePath, String targetPath) {
        fileService.copy(sourcePath, targetPath);
    }

    /**
     * 建立目录
     */
    public Directory createDirectory(String path) {
        return fileService.createDirectory(path);
    }

    /**
     * 删除空目录
     */
    public void deleteEmptyDirectory(String path) {
        fileService.delete(path,true,false);
    }

    /**
     * 删除目录
     */
    public void deleteDirectory(String path) {
        fileService.delete(path,true,true);
    }

    public boolean reName(Item item,String newName){
        return fileService.reName(item,newName);
    }

    public boolean reAttribute(Item item,boolean readOnly, boolean systemFile, boolean regularFile, boolean isDirectory){
        return fileService.reAttribute(item,readOnly,systemFile,regularFile,isDirectory);
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


        System.out.println(FileServiceUtil.find(getInstance().fileService.getDisk(),"/a/m.e",FILE_TYPE.EXE).getPath());


        System.out.println(FileServiceUtil.find(getInstance().fileService.getDisk(), "/a", FILE_TYPE.DIRECTORY));



//        getInstance().fileService.getDisk().disk2file();
//        for (byte[]bytes: getInstance().fileService.getDisk().getDisk()){
//            System.out.println(Arrays.toString(bytes));
//        }
    }
}
