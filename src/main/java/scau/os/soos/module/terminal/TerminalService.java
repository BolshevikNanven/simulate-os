package scau.os.soos.module.terminal;

import scau.os.soos.common.exception.*;
import scau.os.soos.module.file.FileController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TerminalService {
    private String  currentDirectory; // 当前操作目录
    private final Map<String, Operation<String,String>> commandMap; // 指令集

    private final ArrayList<String> historyCommand; // 历史命令

    private int commandIndex; // 命令索引

    public TerminalService() {
        currentDirectory = FileController.getInstance().listRoot().get(0).getPath(); // 初始化操作目录为根目录
        commandMap = new HashMap<>();
        initCommandMap(); // 初始化指令集
        historyCommand = new ArrayList<>();
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
        commandMap.put("help", this::help);
    }

    public String executeCommand(String command) {
        String[] parts = command.split(" ", 2);
        String cmd = parts[0];
        String arg = parts.length > 1 ? parts[1] : "";
        Operation<String, String> operation = commandMap.get(cmd.toLowerCase());
        if (operation != null) {
            return operation.execute(arg);
        } else {
            return("未知命令: " + cmd);
        }
    }

    // 获取上一条指令
    public String getLastCommand() {
        if(!isLegalIndex(commandIndex -1)){return "";}
        commandIndex--;
        return historyCommand.get(commandIndex);

    }

    // 获取下一条指令
    public String getNextCommand() {
        if(!isLegalIndex(commandIndex +1)){return "";}
        commandIndex++;
        return historyCommand.get(commandIndex);
    }

    private boolean isLegalIndex(int index) {
        return (index >= 0 && index < historyCommand.size());
    }

    public void addCommand(String command) {
        historyCommand.add(command);
        commandIndex = historyCommand.size();
    }

    public boolean isHistoryEmpty() {
        return historyCommand.isEmpty();
    }


    private String changeDirectory(String arg) {
        if(!arg.matches("^\\s*[^\\s]+$"))return "指令格式错误";
        try {
            if(FileController.getInstance().isExistedDirectory(arg)){
                currentDirectory = arg;
            }
        } catch (Exception e){
            return e.getMessage();
        }
        return "";
    }

    private String createFile(String arg) {
        if(!arg.matches("^\\s*[^\\s]+$"))return "指令格式错误";
        String path = currentDirectory + "/" + arg;
        try {
            FileController.getInstance().createFile(path);
            return "创建成功";
        }catch (Exception e){
            return e.getMessage();
        }
    }

    private String deleteFile(String arg) {
        if(!arg.matches("^\\s*[^\\s]+$"))return "指令格式错误";
        try {
            String path = currentDirectory + "/" + arg;
            FileController.getInstance().deleteFile(path);
            return "删除成功";
        } catch (Exception e){
            return e.getMessage();
        }
    }

    private String typeFile(String arg) {
        if(!arg.matches("^\\s*[^\\s]+$"))return "指令格式错误";
        try {
            String path = currentDirectory + "/" + arg;
            return FileController.getInstance().typeFile(path);
        } catch (Exception e){
            return e.getMessage();
        }
    }

    private String copyFile(String arg) {
        if(!arg.matches("^\\s*[^\\s]+\\s+[^\\s]+$"))return "指令格式错误";
        // 分离源文件和目标文件
        String[] parts = arg.split(" ", 2);
        String src = currentDirectory + "/" + parts[0];
        String dest = parts[1];
        try {
            FileController.getInstance().copyFile(src, dest);
            return "复制成功";
        }catch (Exception e){
            return e.getMessage();
        }
    }

    private String makeDirectory(String arg) {
        if(!arg.matches("^\\s*[^\\s]+$"))return "指令格式错误";
        try {
            String path = currentDirectory + "/" + arg;
            FileController.getInstance().createDirectory(path);
            return "创建成功";
        } catch (Exception e){
            return e.getMessage();
        }
    }

    private String removeDirectory(String arg) {
        if(!arg.matches("^\\s*[^\\s]+$"))return "指令格式错误";
        try {
            String path = currentDirectory + "/" + arg;
            FileController.getInstance().deleteEmptyDirectory(path);
            return "删除成功";
        } catch (Exception e){
            return e.getMessage();
        }
    }


    private String deleteDirectory(String arg) {
        if(!arg.matches("^\\s*[^\\s]+$"))return "指令格式错误";
        try {
            String path = currentDirectory + "/" + arg;
            FileController.getInstance().deleteDirectory(path);
            return "删除成功";
        } catch (Exception e){
            return e.getMessage();
        }
    }

    private String moveFile(String arg) {
        if(!arg.matches("^\\s*[^\\s]+\\s+[^\\s]+$"))return "指令格式错误";
        String[] parts = arg.split(" ", 2);
        String src = currentDirectory + "/" + parts[0];
        String dest = parts[1];
        try {
            FileController.getInstance().moveFile(src, dest);
            return "移动成功";
        }catch (Exception e){
            return e.getMessage();
        }
    }

    private String changeFileAttribute(String arg) {
        if(!arg.matches("^\\s*[^\\s]+\\s+[^\\s]+\\s+[^\\s]+\\s+[^\\s]+\\s+[^\\s]+$"))return "指令格式错误";
        // 改变文件属性
        String[] parts = arg.split(" ", 5);
        String path = currentDirectory + "/" + parts[0];
        boolean readOnly, systemFile, regularFile, isDirectory;
        for(int i = 1; i <=4; ++i)
        {
            if(!parts[i].matches("^(true|false)$"))
                return "指令格式错误";
        }
        // 判断输入是否合法
        readOnly = Boolean.parseBoolean(parts[1]);
        systemFile = Boolean.parseBoolean(parts[2]);
        regularFile = Boolean.parseBoolean(parts[3]);
        isDirectory = Boolean.parseBoolean(parts[4]);
        try {
            FileController.getInstance().reAttribute(path, readOnly, systemFile, regularFile, isDirectory);
            return "修改成功";
        } catch (Exception e){
            return e.getMessage();
        }

    }

    private String formatDisk(String arg) {
        if(!arg.matches("^\\s*[^\\s]+$"))return "指令格式错误";
        try {
            FileController.getInstance().formatDisk(arg);
            return "格式化成功";
        } catch (Exception e){
            return e.getMessage();
        }
    }

    private String partitionDisk(String arg) {
        if(!arg.matches("^\\s*[^\\s]+\\s+[^\\s]+\\s+\\d+$"))return "指令格式错误";
        // 磁盘分区
        String[] parts = arg.split(" ", 3);
        String src = parts[0];
        String dec = parts[1];
        int size = Integer.parseInt(parts[2]);
        try{
            FileController.getInstance().partitionDisk(src, dec, size);
            return "分区成功";
        }catch (Exception e){
            return e.getMessage();
        }

    }

    private String help(String arg){
        if(arg.isEmpty()) {
            return("""
                    创建文件：create <path>
                    删除文件：delete <path>
                    显示文件内容：type <path>
                    拷贝文件：copy <src> <dec>
                    创建目录：mkdir <path>
                    删除空目录：rmdir <path>
                    切换目录：chdir <path>
                    删除目录：deldir <path>
                    移动文件：move <src> <dec>
                    修改文件属性：change <path> <readOnly:boolean> <systemFile:boolean> <regularFile:boolean> <isDirectory:boolean>
                    格式化磁盘：format <path>
                    磁盘分区：fdisk <src> <dec> <size>
                    单指令查询：help <operation>""");
        }
        return switch (arg) {
            case "create" -> "创建文件：create <path>";
            case "delete" -> "删除文件：delete <path>";
            case "type" -> "显示文件内容：type <path>";
            case "copy" -> "拷贝文件：copy <src> <dec>";
            case "mkdir" -> "创建目录：mkdir <path>";
            case "rmdir" -> "删除空目录：rmdir <path>";
            case "chdir" -> "切换目录：chdir <path>";
            case "deldir" -> "删除目录：deldir <path>";
            case "move" -> "移动文件：move <src> <dec>";
            case "change" -> "修改文件属性：change <path> <readOnly:boolean> <systemFile:boolean> <regularFile:boolean> <isDirectory:boolean>";
            case "format" -> "格式化磁盘：format <path>";
            case "fdisk" -> "磁盘分区：fdisk <src> <dec> <size>";
            case "help" -> "单指令查询：help <operation>";
            default -> "未知命令：" + arg;
        };
    }



    public String getCurrentDirectory() {
        return currentDirectory;
    }

}
