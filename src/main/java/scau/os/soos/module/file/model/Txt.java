package scau.os.soos.module.file.model;

import java.util.Arrays;

public class Txt extends Item{
    private final StringBuilder context;

    public Txt(Disk disk, byte[]data){
        super(data);
        this.context = new StringBuilder();
        initContext(disk);
    }

    public void initContext(Disk disk){
        byte[][] content = super.getContent(disk);

        if (content != null) { // 检查content是否为null
            for (byte[] block : content) {
                if (block != null) { // 检查block是否为null
                    // 使用StringBuilder的append方法高效连接字符串
                    this.context.append(Arrays.toString(block)); // 假设我们想要将byte数组转换为字符串形式并追加
                }
            }
        }
    }

    public String getContext(){
        return context.toString();
    }
}
