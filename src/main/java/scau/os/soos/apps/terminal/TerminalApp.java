package scau.os.soos.apps.terminal;

import scau.os.soos.apps.AppUtil;
import scau.os.soos.ui.components.Window;

public class TerminalApp extends Window {

    public TerminalApp() {
        super("终端", "apps/terminal/icon.png", AppUtil.loadFXML("apps/terminal/main.fxml"));
    }

}
