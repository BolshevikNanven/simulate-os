package scau.os.soos.module.file.model;

import java.util.ArrayList;
import java.util.List;

public class Fat {
    private final int[] fat;
    private static  int BLOCKS_PER_DISK = 256;
    public Fat() {
        this.fat = new int[BLOCKS_PER_DISK];
    }
}
