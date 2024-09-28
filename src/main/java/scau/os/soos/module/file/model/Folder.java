package scau.os.soos.module.file.model;

import java.util.ArrayList;
import java.util.List;

public class Folder {
    private String name;
    private int diskNum;
    private int size;
    private Folder parent;
    private List<Object> children;

    public Folder(String name, int diskNum, Folder parent) {
        this.name = name;
        this.diskNum = diskNum;
        this.size = 0;
        this.setChildren(new ArrayList<Object>());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDiskNum() {
        return diskNum;
    }

    public void setDiskNum(int diskNum) {
        this.diskNum = diskNum;
    }

    public double getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Folder getParent() {
        return parent;
    }

    public void setParent(Folder parent) {
        this.parent = parent;
    }

    public List<Object> getChildren(){
        return children;
    }

    public void setChildren(List<Object> children) {
        this.children = children;
    }
}
