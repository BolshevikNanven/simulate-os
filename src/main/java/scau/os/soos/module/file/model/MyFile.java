package scau.os.soos.module.file.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MyFile implements Serializable, Cloneable {
    private  String fileName;//文件名
    private  int size;//文件大小
    private String extension;//扩展名
    private  String type;//文件类型
    private  String path;//文件路径
    private  int startDisk;//文件起始盘
    private int diskNum;//文件盘数
    private  int flag;//读写标志
    private Folder parent;//父目录
    private String content;//文件内容
    private boolean isOpen;//是否打开

    public MyFile(String fileName, String path,int size, String extension, String type, int startDisk, int diskNum) {
        this.fileName = fileName;
        this.path = path;
        this.size = size;
        this.extension = extension;
        this.type = type;
        this.startDisk = startDisk;
        this.diskNum = diskNum;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getStartDisk() {
        return startDisk;
    }

    public void setStartDisk(int startDisk) {
        this.startDisk = startDisk;
    }

    public int getDiskNum() {
        return diskNum;
    }

    public void setDiskNum(int diskNum) {
        this.diskNum = diskNum;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public Folder getParent() {
        return parent;
    }

    public void setParent(Folder parent) {
        this.parent = parent;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        try {
            MyFile clonedFile = (MyFile) super.clone();
            clonedFile.content = new String(this.content);

            // 深拷贝parent Folder对象
            if (this.parent != null) {
                clonedFile.parent = (Folder) this.parent.clone();
            }

            return clonedFile;
        } catch (CloneNotSupportedException e) {
            // 这不应该发生，因为我们已经实现了Cloneable接口
            throw new AssertionError();
        }
    }
}
