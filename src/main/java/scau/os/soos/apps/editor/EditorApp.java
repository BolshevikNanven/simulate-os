package scau.os.soos.apps.editor;

import scau.os.soos.module.file.model.Item;
import scau.os.soos.ui.components.base.Window;

public class EditorApp extends Window {

    private Item item;

    public EditorApp(Item item) {
        super("编辑器", "main.fxml", 900, 520);
        this.item = item;
    }

    @Override
    protected void initialize() {

    }

    @Override
    protected void close() {

    }
}
