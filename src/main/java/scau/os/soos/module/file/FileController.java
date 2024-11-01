package scau.os.soos.module.file;

import scau.os.soos.common.enums.FILE_TYPE;
import scau.os.soos.module.Module;
import scau.os.soos.module.file.Util.FileServiceUtil;
import scau.os.soos.module.file.model.Directory;
import scau.os.soos.module.file.model.Item;

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
        fileService.delete(path, path.contains("e")?FILE_TYPE.EXE:FILE_TYPE.TXT,true);
    }

    /**
     * 写文件
     */
    public void writeFile(Item file, String content,FILE_TYPE type) {
        fileService.writeFile(file, content,type);
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
        fileService.delete(path, FILE_TYPE.DIRECTORY,false);
    }

    /**
     * 删除目录
     */
    public void deleteDirectory(String path) {
        fileService.delete(path, FILE_TYPE.DIRECTORY,true);
    }

    public int getFileSize(Item file) {
        return fileService.getSize(file);
    }


    @Override
    public void run() {

    }

    public static void main(String[] args) {//显示根目录

        System.out.println(FileServiceUtil.find(getInstance().fileService.getDisk(), "/",FILE_TYPE.DIRECTORY));

        getInstance().createFile("/a.t");
        System.out.println(FileServiceUtil.find(getInstance().fileService.getDisk(), "/a.t",FILE_TYPE.TXT));


        getInstance().createFile("/a.e");
        System.out.println(FileServiceUtil.find(getInstance().fileService.getDisk(), "/a.e",FILE_TYPE.EXE));

        getInstance().deleteFile("/a.e");
        System.out.println(FileServiceUtil.find(getInstance().fileService.getDisk(), "/a.e",FILE_TYPE.EXE));
        System.out.println(FileServiceUtil.find(getInstance().fileService.getDisk(), "/a.t",FILE_TYPE.TXT));





    }
}
