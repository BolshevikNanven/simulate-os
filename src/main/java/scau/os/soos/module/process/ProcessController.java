package scau.os.soos.module.process;

import scau.os.soos.common.OS;
import scau.os.soos.common.enums.DEVICE_TYPE;
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
    public void schedule(){
        // 判断就绪队列是否为空

        // 判断CPU是否空闲

        processService.processSchedule();
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
    public void destroy(Process process){
        processService.destroy(process);
    }

    /**进程唤醒
     * 遍历对应等待设备的进程队列，唤醒其中一个进程
     * @param deviceType
     */
    public void wake(DEVICE_TYPE deviceType){
        // 要判断下设备数量 防止多个模块重复唤醒
        processService.wake(deviceType);
    }

    /**进程唤醒
     * 唤醒指定进程
     * @param process
     */
    public void wake(Process process){
        processService.wake(process);
    }

    /**进程阻塞
     * @param process
     */
    public void block(Process process){
        processService.block(process);
    }

    /**进程切换
     * @param process
     */
    public void handoff(Process process){
        processService.handoff(process);
    }

    @Override
    public void run() {
        OS.clock.bind(processService::clockSchedule);
    }

}

