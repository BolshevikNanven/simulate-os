package scau.os.soos.module.terminal;

import java.util.HashMap;
import java.util.Map;

public class TerminalService {
    private String  currentDirectory; // 当前操作目录
    private final Map<String, Operation<String,String>> commandMap; // 指令集

    public TerminalService() {
        currentDirectory = "/"; // 初始化操作目录为根目录
        commandMap = new HashMap<>();
        initCommandMap(); // 初始化指令集
    }

    private void initCommandMap() {
        commandMap.put("create", this::createFile);
        commandMap.put("delete", this::deleteFile);
        commandMap.put("type", this::typeFile);
        commandMap.put("copy", this::copyFile);
        commandMap.put("mkdir", this::makeDirectory);
        commandMap.put("rmdir", this::removeDirectory);
        commandMap.put("chdir", this::changeDirectory);
        commandMap.put("deldir", this::deleteDirectory);
        commandMap.put("move", this::moveFile);
        commandMap.put("change", this::changeFileAttribute);
        commandMap.put("format", this::formatDisk);
        commandMap.put("fdisk", this::partitionDisk);
    }

    public String executeCommand(String command) {
        String[] parts = command.split(" ", 2);
        String cmd = parts[0];
        String arg = parts.length > 1 ? parts[1] : "";
        Operation<String, String> operation = commandMap.get(cmd.toLowerCase());
        if (operation != null) {
            return operation.execute(arg);
        } else {
            return("未知命令: " + cmd + "\n");
        }
    }


    private String changeDirectory(String arg) {return "";}

    private String createFile(String arg) {
        // 实现创建文件的逻辑
        return "";
    }

    private String deleteFile(String arg) {
        // 实现删除文件的逻辑
        return "";
    }

    private String typeFile(String arg) {
        // 实现显示文件内容的逻辑
        return "";
    }

    private String copyFile(String arg) {
        // 实现拷贝文件的逻辑
        return "";
    }

    private String makeDirectory(String arg) {
        // 实现创建目录的逻辑
        return "";
    }

    private String removeDirectory(String arg) {
        // 实现删除空目录的逻辑
        return "";
    }


    private String deleteDirectory(String arg) {
        // 实现删除目录的逻辑
        return "";
    }

    private String moveFile(String arg) {
        // 实现移动文件的逻辑
        return "";
    }

    private String changeFileAttribute(String arg) {
        // 实现改变文件属性的逻辑
        return "";
    }

    private String formatDisk(String arg) {
        // 实现磁盘格式化的逻辑
        return "";
    }

    private String partitionDisk(String arg) {
        // 实现磁盘分区的逻辑
        return "";
    }



    public String getCurrentDirectory() {
        return currentDirectory;
    }

    public void setCurrentDirectory(String currentDirectory) {
        this.currentDirectory = currentDirectory;
    }
}
