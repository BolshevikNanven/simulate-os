package scau.os.soos.module.file.model;

import scau.os.soos.module.file.Disk;
import scau.os.soos.module.file.FileService;

import java.util.Arrays;

public abstract class Item {
    private Item parent;
    private byte[] name = new byte[3];
    private byte type;
    private byte attribute;
    private byte startBlockNum;
    private final byte[] size = new byte[2];
    private String path;
    private boolean isOpened;

    public Item(byte[] data) {
        System.arraycopy(data, 0, name, 0, 3);
        type = data[3];
        attribute = data[4];
        startBlockNum = data[5];
        System.arraycopy(data, 6, size, 0, 2);
    }

    public Item(Item parent, String name, byte type, boolean readOnly, boolean systemFile, boolean regularFile, boolean isDirectory, int startBlockNum, int size) {
        setName(name);
        setType(type);
        setParent(parent);
        setStartBlockNum(startBlockNum);
        setSize(size);
        setAttribute(readOnly, systemFile, regularFile, isDirectory);
        setPath();
    }

    public void setParent(Item parent) {
        this.parent = parent;
    }

    public Item getParent() {
        return parent;
    }

    public void setName(String name) {
        if (name == null) {
            System.out.println("Name is null");
            return;
        }
        if (name.getBytes().length > 3) {
            System.out.println("Name is too long");
            return;
        }
        this.name = Arrays.copyOf(name.getBytes(), 3);
    }

    public String getName() {
        StringBuilder sb = new StringBuilder();
        for (byte b : name) {
            sb.append((char) b);
        }
        return sb.toString().trim();
    }

    public void setType(byte type) {
        this.type = type;
    }

    public byte getType() {
        return type;
    }

    // 设置属性（使用位操作）
    public void setAttribute(boolean readOnly, boolean systemFile, boolean regularFile, boolean isDirectory) {
        attribute = 0;

        // 利用位运算设置各个属性
        if (readOnly) {
            attribute |= 1; // 设置第0位
        }
        if (systemFile) {
            attribute |= 1 << 1; // 设置第1位
        }
        if (regularFile) {
            attribute |= 1 << 2; // 设置第2位
        }
        if (isDirectory) {
            attribute |= 1 << 3; // 设置第3位
        }
    }

    // 获取属性
    public boolean isReadOnly() {
        return (attribute & (1)) != 0;
    }

    public boolean isSystemFile() {
        return (attribute & (1 << 1)) != 0;
    }

    public boolean isRegularFile() {
        return (attribute & (1 << 2)) != 0;
    }

    public boolean isDirectory() {
        return (attribute & (1 << 3)) != 0;
    }

    public void setStartBlockNum(int startBlockNum) {
        this.startBlockNum = (byte) startBlockNum;
    }

    public int getStartBlockNum() {
        return startBlockNum & 0xFF;
    }

    public void setSize(int size) {
        this.size[1] = (byte) ((size >> 8) & 0xFF);
        this.size[0] = (byte) (size & 0xFF);
    }

    public int getSize() {
        int s = 0;
        s += size[0] & 0xFF;
        s += (size[1] << 8) & 0xFFFF;
        return s;
    }

    public void setPath() {
        if (parent == null) {
            path = "/";
            return;
        }

        if (this instanceof Directory) {
            path = parent.getPath() + getName() + "/";
            return;
        }

        path = parent.getPath() + getName() + '.' + (char) getType();
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setOpened(boolean opened) {
        isOpened = opened;
    }

    public boolean isOpened() {
        return isOpened;
    }

    public String getFullName() {
        return getName() + (getType() == 0 ? "" : "." + (char) getType());
    }

    public byte[] getData() {
        return new byte[]{name[0], name[1], name[2], type, attribute, startBlockNum, size[0], size[1]};
    }


    /**
     * 从磁盘中读取指定起始块之后的所有数据块的内容，并存储到二维数组中
     *
     * @return 二维字节数组，存储了从指定起始块开始的所有数据块的内容
     * 返回 64 字节/块
     */
    protected byte[][] readContentFromDisk() {
        Disk disk = FileService.getDisk();
        Fat fat = disk.getFat();

        int blockCount = calculateTotalBlockNum(fat);

        byte[][] content = new byte[blockCount][Disk.BYTES_PER_BLOCK];

        // 重新遍历FAT表，以实际读取数据块并填充二维数组
        int currentBlock = getStartBlockNum();
        for (int i = 0; i < blockCount; i++) {
            byte[] blockData = disk.getDiskBlock(currentBlock);
            // 复制数据到二维数组的当前行
            System.arraycopy(blockData, 0, content[i], 0, Disk.BYTES_PER_BLOCK);
            currentBlock = fat.getNextBlockIndex(currentBlock);
        }

        return content; // 返回填充后的数组
    }

    public Item getRootDirectory() {
        if(parent == null){
            return this;
        }
        if (this instanceof Directory) {
            if (((Directory) this).isRoot()) {
                return this;
            }
            return parent.getRootDirectory();
        }
        return parent.getRootDirectory();
    }

    /**
     * 将内容写入磁盘
     *
     * @param content 需要写入磁盘的内容
     *                要求严格按照 64 字节/块的规定的格式进行填充
     */
    protected boolean writeContentToDisk(byte[][] content) {
        Disk disk = FileService.getDisk();
        Fat fat = disk.getFat();
        int cur = getStartBlockNum();
        int pre = cur;

        //找根节点起始盘块
        int rootStartBlockNum = getRootDirectory().getStartBlockNum();

        for (byte[] bytes : content) {
            if (cur == Fat.TERMINATED) {
                cur = disk.findFreeDiskBlock(rootStartBlockNum);
                System.out.println("分配新磁盘块");
                if (cur == Fat.TERMINATED) {
                    System.out.println("磁盘已满");
                    disk.formatFatTable(getStartBlockNum(), rootStartBlockNum);
                    return false;
                }
                fat.setNextBlockIndex(pre, cur);
                fat.setNextBlockIndex(cur, Fat.TERMINATED);
            }
            disk.setDiskBlock(cur, bytes);
            pre = cur;
            cur = fat.getNextBlockIndex(cur);
        }
        int i = 0;
        while(cur!=Fat.TERMINATED){
            fat.setNextBlockIndex(pre,Fat.TERMINATED);
            i++;
            pre = cur;
            cur = fat.getNextBlockIndex(cur);
        }
        if(i>0){
            fat.setNextBlockIndex(pre,Fat.TERMINATED);
        }
        fat.writeFatToDisk();
        return true;
    }

    /**
     * 计算从指定起始块开始占用的磁盘块总数
     *
     * @param fat Fat对象，用于访问FAT表以获取磁盘块索引
     * @return 占用的磁盘块总数
     */
    public int calculateTotalBlockNum(Fat fat) {
        int blockCount = 0; // 用于记录占用的磁盘块数量
        int currentBlock = getStartBlockNum();
        while (currentBlock != Fat.TERMINATED) {
            blockCount++;
            currentBlock = fat.getNextBlockIndex(currentBlock);
        }
        return blockCount;
    }

    public abstract void updateSize();

    public abstract void initFromDisk();

    public abstract boolean writeContentToDisk();

    /***
     * 需自行设置 disk, parent, startBlockNum 属性
     * @return
     */
    public abstract Item copy();

    public String toString() {
        return "name: " + getName() +
                " type: " + (char) getType() +
                " attribute: " + attribute +
                " startBlockNum: " + getStartBlockNum() +
                " size: " + getSize();
    }
}
