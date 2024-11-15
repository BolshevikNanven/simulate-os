package scau.os.soos.module.device.view;

import java.util.List;

public record DeviceReadView(Integer usage, Integer available, Integer total, List<Integer> using,
                             List<Integer> waiting) {
}
