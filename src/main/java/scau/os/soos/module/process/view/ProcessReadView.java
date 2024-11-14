package scau.os.soos.module.process.view;

import scau.os.soos.common.enums.PROCESS_STATES;

public record ProcessReadView(int pid, PROCESS_STATES state, int memory) {
}
