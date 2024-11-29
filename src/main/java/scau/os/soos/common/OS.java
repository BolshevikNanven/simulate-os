package scau.os.soos.common;

import scau.os.soos.apps.fileManager.FileManagerApp;
import scau.os.soos.common.enums.OS_STATES;
import scau.os.soos.common.exception.IllegalOperationException;
import scau.os.soos.common.exception.ItemNotFoundException;
import scau.os.soos.common.model.Clock;
import scau.os.soos.module.file.FileController;
import scau.os.soos.module.file.model.Directory;
import scau.os.soos.module.file.model.Item;

import java.util.List;
import java.util.Random;

/**
 * 模拟硬件全局变量
 */
public class OS {
    public static Clock clock = new Clock();
    public static OS_STATES state = OS_STATES.STOPPED;

    public static void simulateSchedule(){
        try {
            Item directory = FileController.getInstance().findItem("/C:/sys");
            if(directory==null||!directory.isDirectory()){
                return;
            }
            List<Item> children = ((Directory) directory).getChildren();
            if (!children.isEmpty()) {

                int randomIndex = new Random().nextInt(children.size());
                Item randomItem = children.get(randomIndex);
                FileManagerApp.getInstance().run(randomItem);
            }
        } catch (IllegalOperationException | ItemNotFoundException ignored) {
        }
    }
}
