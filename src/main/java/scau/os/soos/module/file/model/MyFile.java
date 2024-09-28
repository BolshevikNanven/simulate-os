package scau.os.soos.module.file.model;

import java.util.ArrayList;
import java.util.List;

public class MyFile {
    private  String fileName;//文件名
    private  int size;//文件大小
    private String extension;//扩展名
    private  String type;//文件类型
    private  boolean isDir;
    private  String path;//文件路径
    private  int startDisk;//文件起始盘
    private int diskNum;//文件盘数
    private  int flag;//读写标志
    private List<MyFile> sonFiles=new ArrayList<>();//子目录文件
    private MyFile parent;//父目录文件
    private String content;//文件内容
    private boolean isOpen;//是否打开

    public MyFile(String fileName, int size, String extension, String type, int startDisk, int diskNum) {
        this.fileName = fileName;
        this.size = size;
        this.extension = extension;
        this.type = type;
        this.startDisk = startDisk;
        this.diskNum = diskNum;
    }






}
