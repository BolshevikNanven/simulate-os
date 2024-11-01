package scau.os.soos.module.file;

import scau.os.soos.common.enums.FILE_TYPE;
import scau.os.soos.module.Module;
import scau.os.soos.module.file.Util.FileServiceUtil;
import scau.os.soos.module.file.model.Directory;
import scau.os.soos.module.file.model.Disk;
import scau.os.soos.module.file.model.Item;

import java.util.Arrays;

public class FileController implements Module {
    private static FileController instance;
    private final FileService fileService;

    public static FileController getInstance() {
        if (instance == null) {
            instance = new FileController();
        }
        return instance;
    }

    public FileService getFileService() {
        return fileService;
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
    public Object readFile(Item file) {
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

    public int getFileSize(Item file) {
        return fileService.getSize(file);
    }


    @Override
    public void run() {

    }

    public static void main(String[] args) {//显示根目录


        getInstance().createDirectory("/a");
        getInstance().createDirectory("/b");
        getInstance().createFile("/c.t");
        getInstance().createFile("/c.e");
        getInstance().createFile("/a/d.t");
        getInstance().createFile("/a/e.t");
        getInstance().createDirectory("/b/f");
        getInstance().createFile("/b/f/g.t");


        Directory root = (Directory) FileServiceUtil.find(getInstance().fileService.getDisk(), "/", FILE_TYPE.DIRECTORY);
        System.out.println(root);
        System.out.println(FileServiceUtil.find(getInstance().fileService.getDisk(), "/b", FILE_TYPE.DIRECTORY));
        getInstance().deleteDirectory("/b");
        System.out.println(root);
        System.out.println(FileServiceUtil.find(getInstance().fileService.getDisk(), "/b", FILE_TYPE.DIRECTORY));
        getInstance().deleteDirectory("/b");
        System.out.println(root);
        System.out.println(FileServiceUtil.find(getInstance().fileService.getDisk(), "/b", FILE_TYPE.DIRECTORY));
//        getInstance().fileService.getDisk().disk2file();
    }
}
