package scau.os.soos.apps.fileManager;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import scau.os.soos.apps.fileManager.controller.ToolBarController;
import scau.os.soos.apps.fileManager.model.ThumbnailBox;
import scau.os.soos.ui.components.Dialog;
import scau.os.soos.ui.components.base.Popover;

import java.io.IOException;
import java.util.List;

public class FileMenu extends Popover {
    private final VBox menu;
    private final Button openBtn;
    private final Button operateBtn;
    private final Region separator1;
    private final Button copyBtn;
    private final Button pasteBtn;
    private final Button deleteBtn;
    private final Region separator2;
    private final Button createTxtBtn;
    private final Button createExeBtn;
    private final Button createDirectoryBtn;
    private final Region separator3;
    private final Button reNameBtn;
    private final Button reAttributeBtn;

    public FileMenu() {
        this.gap = 4;

        menu = (VBox) this.container.lookup("#menu");
        openBtn = (Button) this.container.lookup("#open-btn");
        operateBtn = (Button) this.container.lookup("#operate-btn");
        separator1 = (Region) this.container.lookup("#separator-1");
        copyBtn = (Button) this.container.lookup("#copy-btn");
        pasteBtn = (Button) this.container.lookup("#paste-btn");
        deleteBtn = (Button) this.container.lookup("#delete-btn");
        separator2 = (Region) this.container.lookup("#separator-2");
        createTxtBtn = (Button) this.container.lookup("#create-txt-btn");
        createExeBtn = (Button) this.container.lookup("#create-exe-btn");
        createDirectoryBtn = (Button) this.container.lookup("#create-directory-btn");
        separator3 = (Region) this.container.lookup("#separator-3");
        reNameBtn = (Button) this.container.lookup("#re-name-btn");
        reAttributeBtn = (Button) this.container.lookup("#re-attribute-btn");

        addListener();
    }

    private void updateMenuVisibility(boolean openBtn, boolean operateBtn,boolean separator1,
                          boolean copyBtn,boolean pasteBtn, boolean deleteBtn,
                          boolean separator2,boolean createTxtBtn, boolean createExeBtn,
                          boolean createDirectoryBtn, boolean separator3, boolean reNameBtn,boolean reAttributeBtn
                         ){
        menu.getChildren().clear();
        setMenuItemVisibility(this.openBtn,openBtn);
        setMenuItemVisibility(this.operateBtn,operateBtn);
        setMenuItemVisibility(this.separator1,separator1);
        setMenuItemVisibility(this.copyBtn,copyBtn);
        setMenuItemVisibility(this.pasteBtn,pasteBtn);
        setMenuItemVisibility(this.deleteBtn,deleteBtn);
        setMenuItemVisibility(this.separator2,separator2);
        setMenuItemVisibility(this.createTxtBtn,createTxtBtn);
        setMenuItemVisibility(this.createExeBtn,createExeBtn);
        setMenuItemVisibility(this.createDirectoryBtn,createDirectoryBtn);
        setMenuItemVisibility(this.separator3,separator3);
        setMenuItemVisibility(this.reNameBtn,reNameBtn);
        setMenuItemVisibility(this.reAttributeBtn,reAttributeBtn);
    }

    private void setMenuItemVisibility(Node target,boolean visible){
        if(!visible){
            menu.getChildren().remove(target);
        }else{
            menu.getChildren().add(target);
        }
    }

    public void renderOverPane(MouseEvent e,boolean isRenderPasteBtn){
        updateMenuVisibility(false,false,false,
                false,isRenderPasteBtn,false,
                isRenderPasteBtn,true,true,
                true,false,false,false);
        super.render(e);
    }

    public void renderOverItem(MouseEvent e){
        updateMenuVisibility(true,true,true,
                true,false,true,
                true,false,false,
                false,false,true,true);
        super.render(e);
    }

    private void addListener() {
        openBtn.setOnAction((e) -> {
            hide();
            List<ThumbnailBox> selectedList = FileManagerApp.getInstance().getSelectedList();
            for(ThumbnailBox selected : selectedList){
                FileManagerApp.getInstance().open(selected.getItem());
            }
        });
        operateBtn.setOnAction((e) -> {
            hide();
            List<ThumbnailBox> selectedList = FileManagerApp.getInstance().getSelectedList();
            for(ThumbnailBox selected : selectedList){
                FileManagerApp.getInstance().run(selected.getItem());
            }
        });
        copyBtn.setOnAction(actionEvent -> {
            hide();
            ToolBarController.getInstance().copyBtn.fire();
        });
        pasteBtn.setOnAction(actionEvent -> {
            hide();
            ToolBarController.getInstance().pasteBtn.fire();
        });
        deleteBtn.setOnAction(actionEvent -> {
            hide();
            ToolBarController.getInstance().deleteBtn.fire();
        });
        createTxtBtn.setOnAction(actionEvent -> {
            hide();
            ToolBarController.getInstance().createTxtBtn.fire();
        });
        createExeBtn.setOnAction(actionEvent -> {
            hide();
            ToolBarController.getInstance().createExeBtn.fire();
        });
        createDirectoryBtn.setOnAction(actionEvent -> {
            hide();
            ToolBarController.getInstance().createDirectoryBtn.fire();
        });
        reNameBtn.setOnAction(actionEvent -> {
            hide();
            ToolBarController.getInstance().reNameBtn.fire();
        });
        reAttributeBtn.setOnAction(actionEvent -> {
            hide();
//            Dialog dialog = Dialog.getDialog(FileManagerApp.getInstance(),"属性",
//                    true, true,
//                    null,null,
//                    new Button("确定"));
//            dialog.show();
        });
    }

    @Override
    protected Pane setup() {
        FXMLLoader loader = new FXMLLoader(FileManagerApp.class.getResource("components/file_menu.fxml"));
        try {
            return loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
