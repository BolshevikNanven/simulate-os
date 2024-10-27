package scau.os.soos.module.file;

import scau.os.soos.module.Module;
import scau.os.soos.module.device.DeviceController;
import scau.os.soos.module.device.DeviceService;
import scau.os.soos.module.file.model.Fat;
import scau.os.soos.module.file.model.Folder;
import scau.os.soos.module.file.model.MyFile;

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
    public MyFile createFile(String path){
        if(fileService.findFile(path) != null){
            System.out.println("文件已存在");
            return null;
        }else{
            return fileService.createFile(path);
        }
    }

    /**
     * 删除文件
     */
    public void deleteFile(String path){
        if(fileService.findFile(path) == null){
            System.out.println("没有该文件");
        }else{
            fileService.deleteFile(path);
        }
    }

    /**
     * 写文件
     */
    public void writeFile(MyFile file){
            fileService.writeFile(file);
    }

    /**
     * 读文件
     */
    public Object readFile(MyFile file) {
        if(file == null){
            System.out.println("没有该文件");
        }
        return file.getContent();
    }

    /**
     * 拷贝文件
     */
    public void copyFile(String sourcePath,String targetPath){
        MyFile file1= fileService.findFile(sourcePath);
        if(file1 != null){
            fileService.copyFile(file1,targetPath);
        }else{
            System.out.println("没有该文件");
        }
    }

    /**
     * 建立目录
     */
    public Folder createDirectory(String path) {
        if(fileService.findFolder(path) != null){
            System.out.println("目录已存在");
            Folder f =  fileService.findFolder(path);
            return f;
        }
        String[] paths = path.split("/");
        String pathName= paths[0];
        Folder parent = null;

        for(int i=1;i<paths.length;i++){
            pathName = pathName + "/" + paths[i];
            if(fileService.findFolder(pathName)==null){
                parent=fileService.createFolder(parent,pathName);
            }else parent =  fileService.findFolder(pathName);
        }
        return parent;
    }

    /**
     * 删除空目录
     */
    public void deleteDirectory(String path) {
        Folder folder = fileService.findFolder(path);
        if(folder != null && folder.getChildren().size() == 0){
            fileService.deleteFolder(path);
        }
        else System.out.println("该目录不为空或目录不存在");
    }

    public int getFileSize(MyFile file) {
        if (file == null) {
            return 0;
        }
        return fileService.getFileSize(file);
    }



    @Override
    public void run() {

    }

    public static void main(String[] args) {

        //显示根目录
        Folder root = getInstance().fileService.findFolder("/");
        System.out.println("root:" + root);
        System.out.println("root children: "+root.getChildren());

        //建立文件
        MyFile c= getInstance().fileService.createFile("/a/c.e");
        System.out.println("c:"+c);
        System.out.println("c parent:"+c.getParent());


        getInstance().writeFile(c);

        System.out.println(getInstance().readFile(c));

        //创建目录
        Folder k = getInstance().createDirectory("/b/f/g");
        System.out.println("k:" + k);
        System.out.println("k parent:"+k.getParent());

        //查找目录
        Folder f = getInstance().fileService.findFolder("/b/f");
        System.out.println("f:" + f);
        System.out.println("f children: "+f.getChildren());

        //查找目录
        Folder a = getInstance().fileService.findFolder("/a");
        System.out.println("a:" + a);
        System.out.println("a children: "+a.getChildren());

//        //删除目录
//        getInstance().deleteDirectory("/b");
//        System.out.println("root children: "+root.getChildren());
//
//        //删除文件
//        getInstance().fileService.deleteFile("/a/c.e");
//        System.out.println("a children: "+a.getChildren());
//
//        MyFile cc= getInstance().fileService.createFile("/a/c.e");
//        System.out.println("cc:"+cc);
//        System.out.println("cc parent:"+cc.getParent());

        //拷贝文件
        getInstance().copyFile("/a/c.e","/b/d.e");
        System.out.println("b children:"+getInstance().fileService.findFolder("/b").getChildren());



















    }
}
