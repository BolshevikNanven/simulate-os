package scau.os.soos.module.file.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Exe extends Item {
    private List<Byte> instructions;

    public Exe(Disk disk, byte[] data) {
        super(disk, data);
        this.instructions = new ArrayList<>();
    }

    public Exe(Disk disk, Item parent, String name, byte type, boolean readOnly, boolean systemFile, boolean regularFile, boolean isDirectory, int startBlockNum, int size) {
        super(disk, parent, name, type, readOnly, systemFile, regularFile, isDirectory, startBlockNum, size);
        this.instructions = new ArrayList<>();
    }


    public List<Byte> getInstructions() {
        return instructions;
    }

    public void updateSize(){
        setSize(instructions.size());
    }

    public void initFromString(String instructions) {
        this.instructions = Arrays.stream(instructions.split("\\s+")) // 分割字符串
                .map(String::trim) // 去除可能的前后空格
                .filter(s -> !s.isEmpty()) // 过滤掉空字符串
                .map(Byte::parseByte) // 将每个字符串转换为Byte
                .collect(Collectors.toList()); // 收集到List中
        setSize(this.instructions.size());
    }

    public void initFromDisk() {
        Disk disk = super.getDisk();
        this.instructions = new ArrayList<>();
        byte[][] content = super.readContentFromDisk(disk);

        for (byte[] block : content) {
            for (byte itemData : block) {
                if (itemData != 0) {
                    instructions.add(itemData);
                }
            }
        }

        setSize(instructions.size());
    }

    public boolean writeContentToDisk() {
        // 将instructions列表转换为字节数组
        byte[] instructionsBytes = new byte[instructions.size()];
        for (int i = 0; i < instructions.size(); i++) {
            instructionsBytes[i] = instructions.get(i);
        }

        // 计算需要多少个数据块来存储所有子项
        int blockNum = (int) Math.ceil((double) instructionsBytes.length / getDisk().BYTES_PER_BLOCK);
        byte[][] allItemsData = new byte[blockNum][getDisk().BYTES_PER_BLOCK];

        // 将内容复制到数据块中
        int byteIndex = 0;
        for (int block = 0; block < blockNum; block++) {
            byte[] currentBlock = allItemsData[block];
            int bytesToCopy = Math.min(instructionsBytes.length - byteIndex, getDisk().BYTES_PER_BLOCK);
            System.arraycopy(instructionsBytes, byteIndex, currentBlock, 0, bytesToCopy);
            byteIndex += bytesToCopy;
        }

        // 调用父类方法，将整合后的数据写入磁盘
        return super.writeContentToDisk(allItemsData);
    }

    public Item copy(){
        Exe newItem = new Exe(
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
        newItem.getInstructions().addAll(this.getInstructions());
        return newItem;
    }

    public String toString(){
        return "Exe: "+
                super.toString()+
                " Instructions: " + getInstructions().toString();
    }
}
