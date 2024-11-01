package scau.os.soos.module.file.Util;

import scau.os.soos.common.enums.FILE_TYPE;
import scau.os.soos.module.file.model.*;

public class FileServiceUtil {

    public static Item getItemFromDisk(Disk disk, byte[] data) {
        // 获取数据中的类型字节
        byte type = data[3];
        // 获取数据中的属性字节
        byte attribute = data[4];

        // 判断属性字节的第三位是否为0
        if ((attribute & 0x08) != 0) {
            // 是目录类型，返回目录实例
            Directory directory = new Directory(disk, data);
            directory.initFromDisk();
            return directory;
        } else {
            // 如果不是目录，继续判断类型字节
            if (type != 0) {
                // 类型字节为'e'，返回exe实例
                if (type == 'e') {
                    Exe exe = new Exe(disk, data);
                    exe.initFromDisk();
                    return exe;
                }
                if (type == 't') {
                    // 类型字节为't'，返回txt实例
                    Txt txt = new Txt(disk, data);
                    txt.initFromDisk();
                    return txt;
                }
            }
        }
        return null;
    }


    public static Item getItemFromCreate(Disk disk, Item parent, String name, byte type, boolean readOnly, boolean systemFile, boolean regularFile, boolean isDirectory, int startBlockNum, int size) {
        if (isDirectory) {
            return new Directory(disk, parent, name, type, readOnly, systemFile, regularFile, true, startBlockNum, size);
        } else {
            if (type == (byte) 'e') {
                return new Exe(disk, parent, name, (byte) 'e', readOnly, systemFile, regularFile, false, startBlockNum, size);
            } else {
                return new Txt(disk, parent, name, type, readOnly, systemFile, regularFile, false, startBlockNum, size);
            }
        }
    }

    public static Item find(Disk disk, String path, FILE_TYPE type) {
        Directory root = disk.getRootDirectory();
        return root.find(path, type);
    }

    public static boolean writeItemAndParentsToDisk(Item item) {
        if (item == null || item.getDisk() == null) {
            return false;
        }
        Directory root = item.getDisk().getRootDirectory();

        return writeItemToDiskAndPropagateToRoot(item, root);
    }

    private static boolean writeItemToDiskAndPropagateToRoot(Item item, Directory root) {
        item.writeContentToDisk();
        Item parent = item.getParent();
        while (parent != null) {
            if (!parent.writeContentToDisk()) {
                return false;
            }
            parent = parent.getParent();
        }
        return true;
    }

    public static boolean updateItemSize(Item item) {
        if (item == null || item.getDisk() == null) {
            return false;
        }
        Directory root = item.getDisk().getRootDirectory();

        return updateItemAndPropagateToRootSize(item, root);
    }

    private static boolean updateItemAndPropagateToRootSize(Item item, Directory root) {
        item.updateSize();

        Item parent = item.getParent();
        while (parent != null) {
            parent.updateSize();
            parent = parent.getParent();
        }
        return true;
    }

    public static void delete(Item item) {
        if (item instanceof Directory) {
            deleteDirectoryRecursively((Directory) item);
        } else {
            deleteItem(item);
        }

        Item parent = item.getParent();
        if (parent == null) {
            return;
        }

        // 更新父目录的大小
        FileServiceUtil.updateItemSize(parent);
        // 更新父目录及其上级目录到磁盘
        FileServiceUtil.writeItemAndParentsToDisk(parent);
    }

    private static void deleteItem(Item file) {
        Disk disk = file.getDisk();
        disk.formatFatTable(file.getStartBlockNum());
        Directory parent = (Directory) file.getParent();
        file.setParent(null);
        file.setDisk(null);
        parent.removeChild(file);
    }

    private static void deleteDirectoryRecursively(Directory directory) {
        for (Item child : directory.getChildren()) {
            if (child instanceof Directory) {
                deleteDirectoryRecursively((Directory) child);
            } else {
                deleteItem(child);
            }
        }
        deleteItem(directory);
    }
}
