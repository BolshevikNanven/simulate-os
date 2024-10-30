package scau.os.soos.module.file.model;

import java.util.Arrays;

public class Item {
    private Disk disk;
    private Item parent;
    private byte[] name = new byte[3];
    private byte type;
    private byte attribute;
    private byte startBlockNum;
    private final byte[] size = new byte[2];

    public Item(Disk disk,byte[] data) {
        setDisk(disk);
        System.arraycopy(data, 0, name, 0, 3);
        type = data[3];
        attribute = data[4];
        startBlockNum = data[5];
        System.arraycopy(data, 6, size, 0, 2);
    }

    public Item(String name, byte type, boolean readOnly, boolean systemFile,
                boolean regularFile, boolean isDirectory,Disk disk,Item parent) {
        setDisk(disk);
        setName(name);
        setType(type);
        setParent(parent);
        setSize(0);
        setAttribute(readOnly, systemFile, regularFile, isDirectory);
    }

    public Disk getDisk() {
        return disk;
    }

    public void setDisk(Disk disk) {
        this.disk = disk;
    }

    public void setName(String name) {
        this.name = name.getBytes();
    }

    public void setType(byte type) {
        this.type = (byte) type;
    }

    public void setStartBlockNum(byte startBlockNum) {
        this.startBlockNum = startBlockNum;
    }

    public byte[] getData(){
        return new byte[]{
                name[0], name[1], name[2],
                type, attribute, startBlockNum,
                size[0], size[1]
        };
    }

    public void setParent(Item parent) {
        this.parent = parent;
    }

    public Item getParent() {
        return parent;
    }

    public String getName() {
        StringBuilder sb = new StringBuilder();
        for(byte b : name){
            sb.append((char) b);
        }
        return sb.toString();
    }

    public char getType() {
        return (char) type;
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

    public int getStartBlockNum() {
        return (int) startBlockNum;
    }

    public int getSize() {
        return size[0] << 8 + size[1];
    }

    public void setSize(int size) {
        this.size[0] = (byte) ((size >> 8) & 0xFF);
        this.size[1] = (byte) (size & 0xFF);
    }

    /**
     * 从磁盘中读取指定起始块之后的所有数据块的内容，并存储到二维数组中
     *
     * @param disk 磁盘对象，用于从中读取数据
     * @return 二维字节数组，存储了从指定起始块开始的所有数据块的内容
     */
    protected byte[][] readContentFromDisk(Disk disk) {
        Fat fat = disk.getFat();
        int bytesPerBlock = Disk.BYTES_PER_BLOCK;
        int startBlockNum = this.startBlockNum;

        int blockCount = calculateTotalBlockNum(fat);

        byte[][] content = new byte[blockCount][bytesPerBlock];

        // 重新遍历FAT表，以实际读取数据块并填充二维数组
        int currentBlock = startBlockNum;
        for(int i = 0; i < blockCount; i++){
            byte[] blockData = disk.getDiskBlock(currentBlock);
            // 复制数据到二维数组的当前行
            System.arraycopy(blockData, 0, content[i], 0, bytesPerBlock);
            currentBlock = fat.getNextBlockIndex(currentBlock);
        }

        return content; // 返回填充后的数组
    }

    /**
     * 将内容写入磁盘
     *
     * @param disk     磁盘对象
     * @param content   需要写入磁盘的内容
     *                  要求严格按照 64 字节/块的规定的格式进行填充
     */
    protected void writeContentToDisk(Disk disk, byte[][] content) {
        Fat fat = disk.getFat();
        int cur = startBlockNum;
        int pre = cur;
        for (byte[] bytes : content) {
            if(cur == -1){
                System.out.println("分配新磁盘块");
                cur = disk.findFreeDiskBlock();
                if(cur == -1){
                    System.out.println("磁盘已满");
                    break;
                }
                fat.allocateNewBlock(pre,cur);
            }
            disk.setDiskBlock(cur, bytes);
            pre = cur;
            cur = fat.getNextBlockIndex(cur);
        }
    }

    /**
     * 计算从指定起始块开始占用的磁盘块总数
     *
     * @param fat Fat对象，用于访问FAT表以获取磁盘块索引
     * @return 占用的磁盘块总数
     */
    public int calculateTotalBlockNum(Fat fat) {
        int blockCount = 0; // 用于记录占用的磁盘块数量
        int currentBlock = startBlockNum;
        while (currentBlock != Fat.TERMINATED ) {
            blockCount++;
            System.out.println(blockCount);
            currentBlock = fat.getNextBlockIndex(currentBlock);
        }
        return blockCount;
    }

    public void initFromString(String content){

    }
}
