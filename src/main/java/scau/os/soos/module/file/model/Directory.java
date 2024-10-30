package scau.os.soos.module.file.model;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Directory extends Item {
    public static final int BYTES_PER_ITEM = 8;
    private final Disk disk;
    private final List<Item> children;

    public Directory(Disk disk, byte[] data) {
        super(data);
        this.disk = disk;

        children = new ArrayList<>();

        initChildren();
    }

    public void initChildren() {
        byte[][] content = super.getContent(disk);

        for (byte[] block : content) {
            for (int i = 0; i < block.length; i += BYTES_PER_ITEM) {
                byte[] itemData = new byte[BYTES_PER_ITEM];
                System.arraycopy(block, i, itemData, 0, BYTES_PER_ITEM);
                Item item = new Item(itemData);
                if(disk.isItemExist(item)){
                    children.add(item);
                }
            }
        }
    }

    public Item find(String path) {
        //读文件"dir1/dir2/file.txt"
        //读目录"dir1/dir2"
        StringTokenizer tokenizer = new StringTokenizer(path, "/");
        List<String> pathParts = new ArrayList<>();
        while (tokenizer.hasMoreTokens()) {
            pathParts.add(tokenizer.nextToken());
        }

        // 从根目录（即this）开始查找
        return findInDirectory(this, pathParts, 0);
    }

    private Item findInDirectory(Directory currentDir, List<String> pathParts, int index) {
        // 校验pathParts是否为空或index是否越界
        if (pathParts == null || index < 0 || index >= pathParts.size()) {
            throw new IllegalArgumentException("Invalid path parts or index");
        }

        // 获取当前目录的子项
        List<Item> children = currentDir.getChildren();

        // 检查是否已到达路径的末尾
        if (index == pathParts.size() - 1) {
            // 遍历子项，寻找与路径最后一部分同名的项
            for (Item child : children) {
                if (child.getName().equals(pathParts.get(index))) {
                    return child;
                }
            }
        } else {
            // 若未到达路径末尾，则继续递归查找
            String nameToFind = pathParts.get(index);
            for (Item child : children) {
                if (child.getName().equals(nameToFind)) {
                    // 如果找到了匹配的项，并且它是一个目录，则递归地在其内部查找
                    if (child instanceof Directory) {
                        return findInDirectory((Directory) child, pathParts, index + 1);
                    } else {
                        // 如果找到了匹配的项，并且它是一个文件（不是目录），则返回它
                        // 注意：这里我们假设路径总是指向文件，除非进行特殊修改以允许目录返回
                        return child;
                    }
                }
            }
        }
        // 若未找到匹配的目录，则返回null
        return null;
    }

    public List<Item> getChildren() {
        for (int i = 0; i < children.size(); i++) {
            if (children.get(i).getType() == 0) {
                children.set(i, new Directory(disk, children.get(i).getData()));
            } else if (children.get(i).getType() == 1) {
                children.set(i, new Txt(disk, children.get(i).getData()));
            } else if (children.get(i).getType() == 2) {
                children.set(i, new Exe(disk, children.get(i).getData()));
            }
        }
        return children;
    }
}
