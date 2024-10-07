package scau.os.soos.apps.mindmap.util;

import scau.os.soos.apps.mindmap.entity.Command;
import scau.os.soos.apps.mindmap.common.handler.CommandEventHandler;

public class CommandUtil {
    public static Command generate(CommandEventHandler handler, Object param1, Object param2) {
        return new Command() {
            @Override
            public void execute() {
                handler.execute(param1);
            }

            @Override
            public void undo() {
                handler.execute(param2);
            }
        };
    }
}
