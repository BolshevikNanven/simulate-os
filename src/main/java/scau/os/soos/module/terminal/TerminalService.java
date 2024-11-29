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
        try {
            if(FileController.getInstance().isExistedDirectory(arg)){
                currentDirectory = arg;
            }
        } catch (ItemNotFoundException e) {
            return "该目录不存在";
        }
        return "";
    }

    private String createFile(String arg) {
        String path = currentDirectory + "/" + arg;
        try {
            FileController.getInstance().createFile(path);
            return "创建成功";
        }catch (ItemAlreadyExistsException e){
            return "该文件已存在";
        }catch (DiskSpaceInsufficientException e){
            return "磁盘空间不足";
        }catch (Exception e){
            return "未知错误";
        }
    }

    private String deleteFile(String arg) {
        try {
            String path = currentDirectory + "/" + arg;
            FileController.getInstance().deleteFile(path);
            return "删除成功";
        } catch (ItemNotFoundException e) {
            return "该文件不存在";
        } catch (IllegalOperationException e) {
            return "非法路径";           // ?????
        } catch (SystemFileDeleteException e) {
            return "系统文件不能删除";
        } catch (ConcurrentAccessException e) {
            return "文件正在被使用";
        }
    }

    private String typeFile(String arg) {
        try {
            String path = currentDirectory + "/" + arg;
            return FileController.getInstance().typeFile(path);
        } catch (IllegalOperationException e) {
            return "非法路径";
        } catch (ItemNotFoundException e) {
            return "该文件不存在";
        }
    }

    private String copyFile(String arg) {
        // 分离源文件和目标文件
        String[] parts = arg.split(" ", 2);
        String src = currentDirectory + "/" + parts[0];
        String dest = parts[1];
        try {
            FileController.getInstance().copyFile(src, dest);
            return "复制成功";
        }catch (ItemNotFoundException e) {
            return "源文件不存在";
        }catch (ItemAlreadyExistsException e){
            return "目标文件已存在";
        }catch (DiskSpaceInsufficientException e){
            return "磁盘空间不足";
        }catch (IllegalOperationException e){
            return "非法目标路径";
        } catch (ReadOnlyFileModifiedException e) {
            return "只读文件不能修改";
        } catch (ConcurrentAccessException e) {
            return "文件正在被使用";
        }
    }

    private String makeDirectory(String arg) {
        try {
            String path = currentDirectory + "/" + arg;
            FileController.getInstance().createDirectory(path);
            return "创建成功";
        } catch (ItemAlreadyExistsException e) {
            return "该目录已存在";
        }catch (DiskSpaceInsufficientException e){
            return "磁盘空间不足";
        }catch (Exception e){
            return "未知错误";
        }
    }

    private String removeDirectory(String arg) {
        try {
            String path = currentDirectory + "/" + arg;
            FileController.getInstance().deleteEmptyDirectory(path);
            return "删除成功";
        } catch (ItemNotFoundException e) {
            return "该目录不存在";
        } catch (DirectoryNoEmptyException e) {
            return "该目录不为空";
        } catch (IllegalOperationException e) {
            return "非法路径";           // ?????
        } catch (SystemFileDeleteException e) {
            return "系统文件不能删除";
        } catch (ConcurrentAccessException e) {
            return "文件正在被使用";
        }
    }


    private String deleteDirectory(String arg) {
        try {
            String path = currentDirectory + "/" + arg;
            FileController.getInstance().deleteFile(path);
            return "删除成功";
        } catch (ItemNotFoundException e) {
            return "该目录不存在";
        } catch (IllegalOperationException e) {
            return "非法路径";           // ?????
        } catch (SystemFileDeleteException e) {
            return "系统文件不能删除";
        } catch (ConcurrentAccessException e) {
            return "文件正在被使用";
        }
    }

    private String moveFile(String arg) {
        String[] parts = arg.split(" ", 2);
        String src = currentDirectory + "/" + parts[0];
        String dest = parts[1];
        try {
            FileController.getInstance().moveFile(src, dest);
            return "移动成功";
        }catch (ItemNotFoundException e) {
            return "源文件不存在";
        }catch (ItemAlreadyExistsException e){
            return "目标文件已存在";
        }catch (DiskSpaceInsufficientException e){
            return "磁盘空间不足";
        }catch (IllegalOperationException e){
            return "非法目标路径";
        } catch (ReadOnlyFileModifiedException e) {
            return "只读文件不能修改";
        } catch (ConcurrentAccessException e) {
            return "文件正在被使用";
        }
    }

    private String changeFileAttribute(String arg) {
        // 改变文件属性
        String[] parts = arg.split(" ", 5);
        String path = currentDirectory + "/" + parts[0];
        boolean readOnly, systemFile, regularFile, isDirectory;
        for(int i = 1; i <=4; ++i)
        {
            if(!parts[i].matches("^(true|false)$"))
                return "输入格式错误";
        }
        // 判断输入是否合法
        readOnly = Boolean.parseBoolean(parts[1]);
        systemFile = Boolean.parseBoolean(parts[2]);
        regularFile = Boolean.parseBoolean(parts[3]);
        isDirectory = Boolean.parseBoolean(parts[4]);
        try {
            FileController.getInstance().reAttribute(path, readOnly, systemFile, regularFile, isDirectory);
            return "修改成功";
        } catch (IllegalOperationException e) {
            return "非法操作";
        } catch (ItemNotFoundException e) {
            return "该文件不存在";
        } catch (ConcurrentAccessException e) {
            return "文件正在被使用";
        }

    }

    private String formatDisk(String arg) {
        // 磁盘格式化
        String[] parts = arg.split(" ", 2);
        String path = currentDirectory + "/" + parts[0];
        try {
            FileController.getInstance().formatDisk(path);
            return "格式化成功";
        } catch (IllegalOperationException e) {
            return "非法操作";
        } catch (ItemNotFoundException e) {
            return "该文件不存在";
        }
    }

    private String partitionDisk(String arg) {
        // 磁盘分区
        String[] parts = arg.split(" ", 3);
        String src = parts[0];
        String dec = parts[1];
        int size;
        try {
            size = Integer.parseInt(parts[2]);
        } catch (NumberFormatException e) {
            return "输入格式错误";
        }

        try{
            FileController.getInstance().partitionDisk(src, dec, size);
            return "分区成功";
        }catch (IllegalOperationException e){
            return "非法操作";
        }catch (DiskSpaceInsufficientException e){
            return "磁盘空间不足";
        }catch (MaxCapacityExceededException e){
            return "磁盘容量已满";
        }catch (ItemNotFoundException e){
            return "源磁盘不存在";
        }

    }

    private String help(String arg){
        if(arg.isEmpty()) {
            return("""
                    命令列表:
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
                    磁盘分区：partition <src> <dec> <size>""");
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
            case "change" ->
                    "修改文件属性：change <path> <readOnly:boolean> <systemFile:boolean> <regularFile:boolean> <isDirectory:boolean>";
            case "format" -> "格式化磁盘：format <path>";
            case "partition" -> "磁盘分区：partition <src> <dec> <size>";
            default -> "未知命令：" + arg;
        };
    }



    public String getCurrentDirectory() {
        return currentDirectory;
    }

}
