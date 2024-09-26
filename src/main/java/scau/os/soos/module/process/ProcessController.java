package scau.os.soos.module.process;

import scau.os.soos.common.OS;
import scau.os.soos.module.Module;
import scau.os.soos.module.file.model.MyFile;

import java.io.File;

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

    /**进程创建
     * @param file
     * @return Process
     */
    public Process create(MyFile file){return null;}

    /**进程销毁
     * @param process
     */
    public void destroy(Process process){}

    /**进程唤醒
     * @param process
     */
    public void wake(Process process){}

    /**进程阻塞
     * @param process
     */
    public void block(Process process){}
    @Override
    public void run() {
        OS.clock.bind(processService::clockSchedule);
    }
}
