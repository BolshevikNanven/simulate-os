package scau.os.soos.module.process;

import scau.os.soos.common.OS;
import scau.os.soos.module.Module;
import scau.os.soos.module.file.model.MyFile;
import scau.os.soos.module.process.model.Process;

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

    /**进程调度(新进程)
     *
     */
    public boolean schedule(){
        return processService.schedule();
    }

    /**进程创建
     * @param file
     * @return Process
     */
    public Process create(MyFile file){
        return processService.create(file);
    }

    /**进程销毁
     * @param process
     */
    public boolean destroy(Process process){
        return processService.destroy(process);
    }

    /**进程唤醒
     * 唤醒指定进程
     * @param process
     */
    public boolean wake(Process process){
        return processService.wake(process);
    }

    /**进程阻塞
     * @param process
     */
    public boolean block(Process process){
        return processService.block(process);
    }

    /**进程切换
     * @param process
     */
    public boolean handoff(Process process){
        return processService.handoff(process);
    }

    @Override
    public void run() {
        OS.clock.bind(processService::clockSchedule);
    }
}

