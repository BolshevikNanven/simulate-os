package scau.os.soos.module.file;

import scau.os.soos.common.enums.FILE_TYPE;
import scau.os.soos.module.Module;
import scau.os.soos.module.file.model.Directory;
import scau.os.soos.module.file.model.Exe;
import scau.os.soos.module.file.model.Item;
import scau.os.soos.module.file.model.Txt;

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
        fileService.deleteFile(path);
    }

    /**
     * 写文件
     */
    public void writeFile(Txt file, String content,FILE_TYPE type) {
        fileService.writeFile(file, content,type);
    }

    /**
     * 读文件
     */
    public Object readFile(Item file) {
        if (file == null) {
            System.out.println("没有该文件");
        }
        if (file != null) {
            return ((Exe) file).getInstructions();
        }
        return null;
    }

    /**
     * 拷贝文件
     */
    public void copyFile(String sourcePath, String targetPath) {
        fileService.copyFile(sourcePath, targetPath);
    }

    /**
     * 建立目录
     */
    public Directory createDirectory(String path) {
//        if(fileService.findFolder(path) != null){
//            System.out.println("目录已存在");
//            Directory f =  fileService.findFolder(path);
//            return f;
//        }
//        String[] paths = path.split("/");
//        String pathName= paths[0];
//        Directory parent = null;
//
//        for(int i=1;i<paths.length;i++){
//            pathName = pathName + "/" + paths[i];
//            if(fileService.findFolder(pathName)==null){
//                parent=fileService.createFolder(parent,pathName);
//            }else parent =  fileService.findFolder(pathName);
//        }
//        return parent;

        return fileService.createDirectory(path);
    }

    /**
     * 删除空目录
     */
    public void deleteEmptyDirectory(String path) {
        fileService.deleteDirectory(path, false);
    }

    /**
     * 删除目录
     */
    public void deleteDirectory(String path) {
        fileService.deleteDirectory(path, true);
    }

    public int getFileSize(Item file) {
        return fileService.getSize(file);
    }


    @Override
    public void run() {

    }

    public static void main(String[] args) {//显示根目录
//        Directory root = getInstance().fileService.findFolder("/");
//        System.out.println("root:" + root);
//        System.out.println("root children: " + root.getChildren());
//
////        //建立文件
////        MyFile c= getInstance().fileService.createFile("/a/c.e");
////        System.out.println("c:"+c);
////        System.out.println("c parent:"+c.getParent());
//
//
//        //getInstance().writeFile(c);
//
//        //System.out.println(getInstance().readFile(c));
//
////        //创建目录
////        Folder k = getInstance().createDirectory("/b/f/g");
////        System.out.println("k:" + k);
////        System.out.println("k parent:"+k.getParent());
//
//        //查找目录
//        Directory f = getInstance().fileService.findFolder("/b/f");
//        System.out.println("f:" + f);
//        System.out.println("f children: " + f.getChildren());
//
//        //查找目录
//        Directory a = getInstance().fileService.findFolder("/a");
//        System.out.println("a:" + a);
//        System.out.println("a children: " + a.getChildren());
//
////        //删除目录
////        getInstance().deleteDirectory("/b");
////        System.out.println("root children: "+root.getChildren());
////
////        //删除文件
////        getInstance().fileService.deleteFile("/a/c.e");
////        System.out.println("a children: "+a.getChildren());
////
////        MyFile cc= getInstance().fileService.createFile("/a/c.e");
////        System.out.println("cc:"+cc);
////        System.out.println("cc parent:"+cc.getParent());
//
////        //拷贝文件
////        getInstance().copyFile("/a/c.e","/b/d.e");
////        System.out.println("b children:"+getInstance().fileService.findFolder("/b").getChildren());
//
//
//        getInstance().fileService.disk2file();

        getInstance().createFile("/a.e");
        //getInstance().createDirectory("/a/");

        System.out.println(FileServiceUtil.find(getInstance().fileService.getDisk(), "/a.e",FILE_TYPE.EXE));


    }
}
