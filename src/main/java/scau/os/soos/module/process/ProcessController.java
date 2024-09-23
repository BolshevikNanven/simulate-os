package scau.os.soos.module.process;

import scau.os.soos.common.OS;
import scau.os.soos.module.Module;

public class ProcessController implements Module {
    private static ProcessController instance;
    private final ProcessService processService;

    public static ProcessController getInstance() {
        if (instance == null) {
            instance = new ProcessController();
        }
        return instance;
    }

    private ProcessController() {
        processService = new ProcessService();
    }

    public void schedule(){
        processService.processSchedule();
    }
    @Override
    public void run() {
        OS.clock.bind(processService::clockSchedule);
    }
}
