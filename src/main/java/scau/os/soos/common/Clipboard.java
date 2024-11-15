package scau.os.soos.common;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import scau.os.soos.apps.fileManager.controller.DirectoryTreeController;
import scau.os.soos.apps.fileManager.model.ThumbnailBox;

import java.util.ArrayList;
import java.util.List;

public class Clipboard {
    private static Clipboard instance;
    private final ListProperty<ThumbnailBox> copiedItems;
    private String sourcePath = "";

    private Clipboard() {
        copiedItems = new SimpleListProperty<>(FXCollections.observableArrayList());
    }

    public static synchronized Clipboard getInstance() {
        if (instance == null) {
            instance = new Clipboard();
        }
        return instance;
    }

    public ListProperty<ThumbnailBox> getCopiedItemsProperty() {
        return copiedItems;
    }

    public void copy(List<ThumbnailBox> items) {
        sourcePath = DirectoryTreeController.getInstance().getCurrentDirectory().getPath();
        copiedItems.clear();
        copiedItems.addAll(items);
    }

    public List<ThumbnailBox> getCopiedItems() {
        return new ArrayList<>(this.copiedItems); // 返回复制项目的副本
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public void clear() {
        copiedItems.clear();
    }
}
