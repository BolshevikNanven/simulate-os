package scau.os.soos.module.file.model;

import java.util.ArrayList;
import java.util.List;

public class Exe extends Item{
    public static final int BYTES_PER_ITEM = 1;
    private final List<Byte> instructions;

    public Exe(Disk disk, byte[]data) {
        super(data);
        this.instructions = new ArrayList<>();
        initInstructions(disk);
    }

    public void initInstructions(Disk disk){
        byte[][] content = super.getContent(disk);

        for (byte[] block : content) {
            for (byte itemData : block) {
                instructions.add(itemData);
            }
        }

    }

    public List<Byte> getInstructions(){
        return instructions;
    }
}
