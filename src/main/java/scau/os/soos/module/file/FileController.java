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

    private FileController() {
        fileService = new FileService();
    }

    /**
     * 创建文件
     */
    public MyFile createFile(String fileName){
        if(fileService.findFile(fileName) != null){
            System.out.println("文件已存在");
            return null;
        }else{
            return fileService.createFile(fileName);
        }
    }

    /**
     * 删除文件
     */
    public void deleteFile(String fileName){
        if(fileService.findFile(fileName) == null){
            System.out.println("没有该文件");
        }else{
            fileService.deleteFile(fileName);
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
    public void copyFile(String path1,String path2){
        MyFile file1= fileService.findFile(path1);
        if(file1 != null){
            fileService.copyFile(file1,path2);
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
            return null;
        }
        String[] paths = path.split("/");
        String pathName= paths[0];
        Folder parent = null;

        for(int i=1;i<paths.length;i++){
            pathName = pathName + "/" + paths[i];
            if(fileService.findFolder(pathName)==null){
                parent=fileService.createFolder(paths[i],parent,pathName);
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
}
