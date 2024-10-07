package scau.os.soos.apps.mindmap.service;

import scau.os.soos.apps.mindmap.entity.Command;
import scau.os.soos.apps.mindmap.store.SystemStore;

public class UndoAndRedoService {

    private static UndoAndRedoService instance;
    private boolean flagOfRedo = false;

    public static UndoAndRedoService getInstance() {
        return instance;
    }

    public static void init() {
        if (instance == null)
            instance = new UndoAndRedoService();
    }

    private void pushUndoStack(Command command) {
        flagOfRedo = false;
        if (SystemStore.getUndoStack().size() < 10) {
            SystemStore.getUndoStack().push(command);
        }else {
            Command[] temp = new Command[10];
            for (int i = 0; i < SystemStore.getUndoStack().size(); i++)
                temp[i] = SystemStore.getUndoStack().pop();
            for (int i = SystemStore.getUndoStack().size() - 1; i > 0; i--)
                SystemStore.getUndoStack().push(temp[i]);
            SystemStore.getUndoStack().push(command);
        }
    }

    private void pushRedoStack(Command command) {

        if (flagOfRedo)//可以入栈
        {
            SystemStore.getRedoStack().push(command);
        } else {//清空Redo栈
            for (int i = 0; i < SystemStore.getRedoStack().size(); i++) {
                SystemStore.getRedoStack().pop();
            }
        }
    }

    public void execute(Command command) {
        pushUndoStack(command);
        SystemStore.getRedoStack().clear();
        command.execute();
    }
    public void clear(){
        SystemStore.getRedoStack().clear();
        SystemStore.getUndoStack().clear();
    }
    public void undo() {
        if (!SystemStore.getUndoStack().isEmpty()) {
            flagOfRedo = true;
            Command pop = SystemStore.getUndoStack().pop();
            pushRedoStack(pop);
            pop.undo();
        }
    }

    public void redo() {
        if (!SystemStore.getRedoStack().isEmpty()) {
            Command pop = SystemStore.getRedoStack().pop();
            pushUndoStack(pop);
            pop.execute();
        }
    }
}
