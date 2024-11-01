package scau.os.soos.module.file;

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
            return new Directory(disk, data);
        } else {
            // 如果不是目录，继续判断类型字节
            if (type != 0) {
                // 类型字节为'e'，返回exe实例
                if (type == 'e') {
                    return new Exe(disk, data);
                }
                // 类型字节不是'e'，返回null
                return null;
            } else {
                // 类型字节为0，返回txt实例
                return new Txt(disk, data);
            }
        }
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

    public static Item find(Disk disk, String path, FILE_TYPE type){
        Directory root = disk.getRootDirectory();
        return root.find(path,type);
    }

    public static boolean writeItemAndParentsToDisk(Item item) {
        if(item == null|| item.getDisk() == null){
            return false;
        }
        Directory root = item.getDisk().getRootDirectory();

        return writeItemToDiskAndPropagateToRoot(item, root);
    }

    private static boolean writeItemToDiskAndPropagateToRoot(Item item, Directory root) {
        item.writeContentToDisk();

        Item parent = item.getParent();
        while (parent != root) {
            if (!item.writeContentToDisk()) {
                return false;
            }
            parent = parent.getParent();
        }
        return true;
    }

    public static boolean updateItemSize(Item item){
        if(item == null|| item.getDisk() == null){
            return false;
        }
        Directory root = item.getDisk().getRootDirectory();

        return updateItemAndPropagateToRootSize(item, root);
    }

    private static boolean updateItemAndPropagateToRootSize(Item item, Directory root) {
        item.updateSize();

        Item parent = item.getParent();
        while (parent != root) {
            parent.updateSize();
            parent = parent.getParent();
        }
        return true;
    }
}
