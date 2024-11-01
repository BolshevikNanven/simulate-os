package scau.os.soos.module.file.model;

import scau.os.soos.common.enums.FILE_TYPE;
import scau.os.soos.module.file.Util.FileServiceUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Directory extends Item {
    public static final int BYTES_PER_ITEM = 8;
    private final List<Item> children;

    public Directory(Disk disk, byte[] data) {
        super(disk, data);
        this.children = new ArrayList<>();
    }

    public Directory(Disk disk, Item parent, String name, byte type, boolean readOnly, boolean systemFile, boolean regularFile, boolean isDirectory, int startBlockNum, int size) {
        super(disk, parent, name, type, readOnly, systemFile, regularFile, isDirectory, startBlockNum, size);
        this.children = new ArrayList<>();
    }

    public List<Item> getChildren() {
        return children;
    }

    public void addChildren(Item item) {
        children.add(item);
    }

    public void removeChild(Item item) {
        children.remove(item);
    }

    public Item find(String path, FILE_TYPE fileType) {
        switch (fileType) {
            case EXE -> {
                return find(path, false, (byte) 'e');
            }
            case TXT -> {
                return find(path, false, (byte) 't');
            }
            case DIRECTORY -> {
                return find(path, true, (byte) 0);
            }
        }
        return null;
    }

    private Item find(String path, boolean isDirectory, byte type) {
        StringTokenizer tokenizer = new StringTokenizer(path, "/");
        List<String> pathParts = new ArrayList<>();
        while (tokenizer.hasMoreTokens()) {
            String pathPart = tokenizer.nextToken();
            pathParts.add(pathPart);
        }

        // 从根目录（即this）开始查找
        return findInDirectory(this, pathParts, 0, isDirectory, type);
    }

    private Item findInDirectory(Directory currentDir, List<String> pathParts, int index, boolean isDirectory, byte type) {
        // 校验pathParts是否为空或index是否越界
        if (pathParts.isEmpty()) {
            return currentDir;
        }

        if (index < 0 || index >= pathParts.size()) {
            return null;
        }

        // 获取当前目录的子项
        List<Item> children = currentDir.getChildren();
        String nameToFind = pathParts.get(index);

        // 检查是否已到达路径的末尾
        if (index == pathParts.size() - 1) {
            // 遍历子项，寻找与路径最后一部分同名的项
            if (nameToFind.contains(".")) {
                nameToFind = nameToFind.substring(0, nameToFind.lastIndexOf('.'));
            }
            if (isDirectory) {
                for (Item child : children) {
                    if (child.getName().equals(nameToFind) && child.isDirectory()) {
                        System.out.println("find : " + nameToFind);
                        return child;
                    }
                }
            } else {
                for (Item child : children) {
                    if (child.getName().equals(nameToFind) && child.getType() == type) {
                        System.out.println("find : " + nameToFind);
                        return child;
                    }
                }
            }
        } else {
            // 若未到达路径末尾，则继续递归查找
            for (Item child : children) {
                if (child.getName().equals(nameToFind)) {
                    // 如果找到了匹配的项，并且它是一个目录，则递归地在其内部查找
                    if (child instanceof Directory) {
                        return findInDirectory((Directory) child, pathParts, index + 1, isDirectory, type);
                    }
                }
            }
        }
        // 若未找到匹配的目录，则返回null
        return null;
    }

    public void updateSize() {
        this.setSize(children.size() * BYTES_PER_ITEM);
    }

    public void initFromString(String content) {

    }

    public void initFromDisk() {
        Disk disk = super.getDisk();
        byte[][] content = super.readContentFromDisk(disk);

        for (byte[] block : content) {
            for (int i = 0; i < block.length; i += BYTES_PER_ITEM) {
                byte[] itemData = new byte[BYTES_PER_ITEM];
                System.arraycopy(block, i, itemData, 0, BYTES_PER_ITEM);
                Item item = FileServiceUtil.getItemFromDisk(disk, itemData);
                if (item != null && disk.isItemExist(item)) {
                    children.add(item);
                    item.setParent(this);
                    item.setDisk(disk);
                }
            }
        }

        this.setSize(children.size() * BYTES_PER_ITEM);
    }

    public boolean writeContentToDisk() {
        // 计算需要多少个数据块来存储所有子项
        int blockNum = (int) Math.ceil((double) getChildren().size() / BYTES_PER_ITEM);
        byte[][] allItemsData = new byte[blockNum][getDisk().BYTES_PER_BLOCK];

        int itemIndex = 0; // 用于追踪当前处理的Item
        int byteIndex = 0; // 用于追踪当前数据块中的字节位置

        for (int block = 0; block < blockNum; block++) {
            byte[] currentBlock = allItemsData[block];

            while (itemIndex < children.size() && byteIndex < getDisk().BYTES_PER_BLOCK) {
                Item item = children.get(itemIndex);
                byte[] itemData = item.getData();

                // 将Item数据复制到当前数据块
                System.arraycopy(itemData, 0, currentBlock, byteIndex, BYTES_PER_ITEM);

                // 更新索引
                byteIndex += BYTES_PER_ITEM;
                itemIndex++;
            }
            // 重置字节索引，为下一个数据块做准备
            byteIndex = 0;
        }

        // 调用父类方法，将整合后的数据写入磁盘
        return super.writeContentToDisk(allItemsData);
    }

    public Item copy() {
        Directory newItem = new Directory(
                null,
                null,
                this.getName(),
                this.getType(),
                this.isReadOnly(),
                this.isSystemFile(),
                this.isRegularFile(),
                this.isDirectory(),
                0,
                0);
        for (Item child : this.getChildren()) {
            Item newChild = child.copy();
            newItem.getChildren().add(newChild);
        }
        return newItem;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (Item child : getChildren()) {
            sb.append(' ');
            sb.append(child.getName()).append('.').append((char) child.getType());
            sb.append(' ');
        }
        sb.append("]");

        return "Directory: " +
                super.toString() +
                " Children: " +
                sb;
    }
}
