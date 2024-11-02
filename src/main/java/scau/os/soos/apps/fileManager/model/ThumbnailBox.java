package scau.os.soos.apps.fileManager.model;

//import com.od.imageview.manager.ControllerManager;
//import com.od.imageview.manager.WindowsManager;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import scau.os.soos.MainApplication;
import scau.os.soos.apps.fileManager.FileManagerApp;
import scau.os.soos.common.enums.FILE_TYPE;
import scau.os.soos.module.file.model.Directory;
import scau.os.soos.module.file.model.Exe;
import scau.os.soos.module.file.model.Item;
import scau.os.soos.module.file.model.Txt;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ThumbnailBox extends VBox {
    private final ImageView imageView;
    private final Item item;
    public ThumbnailBox(Item item) {
        this.item = item;

        StackPane stackPane = new StackPane();
        stackPane.setPrefWidth(100);
        stackPane.setPrefHeight(100);

        imageView = new ImageView();
        imageView.fitWidthProperty().bind(stackPane.prefWidthProperty());
        imageView.fitHeightProperty().bind(stackPane.prefHeightProperty());
        imageView.setPreserveRatio(true);
        imageView.setPickOnBounds(true);

        if(item instanceof Exe){
            imageView.setImage(new Image(String.valueOf(FileManagerApp.class.getResource("image/thumbnailBox/exe.png"))));
        }else if(item instanceof Txt){
            imageView.setImage(new Image(String.valueOf(FileManagerApp.class.getResource("image/thumbnailBox/txt.png"))));
        } else if (item instanceof Directory){
            imageView.setImage(new Image(String.valueOf(FileManagerApp.class.getResource("image/thumbnailBox/directory.png"))));
        }

        stackPane.getChildren().add(imageView);
        Label label = new Label();
        label.setText(item.getName());
//        this.getStylesheets().add(String.valueOf(FileManagerApp.class.getResource("css/ThumbnailBox.css")));
//        this.getStyleClass().add("thumbnail-box");
        this.setAlignment(Pos.CENTER);
        this.getChildren().addAll(stackPane, label);
        setEvent(); // 事件
    }

    private void setEvent(){
        setSelectEvent();
        setTipEvent();
    }

    private void setSelectEvent() {
//        this.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
//            // 多选(ctrl shift) 单选刷新
//            if(event.isControlDown() && event.getClickCount() == 1 && !event.isShiftDown() && !event.isAltDown()) {
//                ControllerManager.thumbnailPaneController.selectImage(this);
//                ControllerManager.thumbnailPaneController.showSelectedList();
//            } else if (!event.isAltDown()&&!event.isControlDown() && event.getClickCount() == 1 && event.isShiftDown() && ControllerManager.thumbnailPaneController.getSelectedList().size() == 1) {
//                 int startItem = -1,endItem = -1;
//                 if(ControllerManager.thumbnailPaneController.getSelectedList().size()>1)return;
//                 ThumbnailBoxModel selectedItem = ControllerManager.thumbnailPaneController.getSelectedList().get(0);
//                for (int i = 0; i < ControllerManager.thumbnailPaneController.getThumbnailPane().getChildren().size(); i++) {
//                    ThumbnailBoxModel item = (ThumbnailBoxModel)ControllerManager.thumbnailPaneController.getThumbnailPane().getChildren().get(i);
//                    if((item == selectedItem || item == this) && startItem == -1) {
//                        startItem = i;
//                    } else if (item == selectedItem || item == this) {
//                        endItem = i;
//                    }
//                    if(startItem != -1 && endItem != -1) {
//                        break;
//                    }
//                }
//                if(startItem != -1 && endItem != -1) {
//                    for (int i = startItem; i <= endItem; i++) {
//                        ControllerManager.thumbnailPaneController.selectImage((ThumbnailBoxModel) ControllerManager.thumbnailPaneController.getThumbnailPane().getChildren().get(i));
//                    }
//                }
//            } else if (!event.isAltDown()&&event.getClickCount() == 1 && (event.getButton() == MouseButton.PRIMARY ||
//                    (event.getButton() == MouseButton.SECONDARY &&
//                            ControllerManager.thumbnailPaneController.getSelectedList().size() <= 1))) {
//                ControllerManager.thumbnailPaneController.clearSelectedList();
//                System.out.println("单选刷新");
//                ControllerManager.thumbnailPaneController.selectImage(this);
//            }  else if (event.getClickCount() == 2) {
//                WindowsManager.showSlideWindow(new File(imageModel.getImageUrl()));
//            }
//        });
    }

    private void setTipEvent(){
        this.setOnMouseEntered(event -> {
            Tooltip tooltip = new Tooltip();
            tooltip.setStyle("-fx-text-fill: #f3f7f7;");
            tooltip.setText("图片名：" + item.getName() + '\n' + "图片类型：" + item.getType() + '\n' + "图片大小：" + item.getSize());
            Tooltip.install(this, tooltip);
        });
    }
}
