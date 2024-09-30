package scau.os.soos.module.file.model;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Folder implements Serializable,Cloneable{
    private String name;
    String path;
    private int diskNum;
    private int size=8;
    private Folder parent;
    private List<Object> children;

    public Folder(String name, int diskNum, Folder parent,String path){
        this.name = name;
        this.diskNum = diskNum;
        this.size = 0;
        this.path=path;
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

    public int getSize() {
       for(Object e:children){
           if(e instanceof Folder) size+=((Folder) e).getSize();
           else size+=((MyFile) e).getSize();
       }
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        try {
            Folder clonedFolder = (Folder) super.clone();

            clonedFolder.setChildren(new ArrayList<Object>());
            for (Object child : this.getChildren()) {
                if (child instanceof Folder) {
                    clonedFolder.getChildren().add(((Folder) child).clone());
                } else if (child instanceof MyFile) {
                    clonedFolder.getChildren().add(((MyFile) child).clone());
                }
            }
            return clonedFolder;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
