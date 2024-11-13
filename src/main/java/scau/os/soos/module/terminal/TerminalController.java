package scau.os.soos.module.terminal;

import scau.os.soos.module.Module;
import scau.os.soos.module.process.ProcessController;
import scau.os.soos.module.process.ProcessService;

public class TerminalController implements Module {
    private static TerminalController instance;
    private final TerminalService terminalService;

    public static TerminalController getInstance() {
        if (instance == null) {
            instance = new TerminalController();
        }
        return instance;
    }

    private TerminalController() {
        terminalService = new TerminalService();
    }

    public String executeCommand(String command) {
        return terminalService.executeCommand(command);
    }

    public String getCurrentDirectory(){
        return terminalService.getCurrentDirectory();
    }

    public String getLastCommand() {
        return terminalService.getLastCommand();
    }

    public String getNextCommand() {
        return terminalService.getNextCommand();
    }

    public void addCommand(String command) {
        terminalService.addCommand(command);
    }

    public boolean isHistoryEmpty() {
        return terminalService.isHistoryEmpty();
    }

    @Override
    public void run() {

    }
}
