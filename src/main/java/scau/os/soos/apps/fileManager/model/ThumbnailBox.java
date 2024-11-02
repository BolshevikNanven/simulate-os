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
import scau.os.soos.module.file.model.Item;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ThumbnailBox extends VBox {
    private final ImageView imageView;
    private final Item item;
    public ThumbnailBox(Item item, DoubleProperty thumbnailBoxSpinnerValueProperty) {
        this.item = item;
        this.prefWidthProperty().bind(thumbnailBoxSpinnerValueProperty);
        this.prefHeightProperty().bind(thumbnailBoxSpinnerValueProperty);
        StackPane stackPane = new StackPane();
        stackPane.getStyleClass().add("thumbnail-box-stackpane");
        stackPane.maxWidthProperty().bind(stackPane.prefWidthProperty());
        stackPane.maxHeightProperty().bind(stackPane.prefHeightProperty());
        stackPane.minWidthProperty().bind(stackPane.prefWidthProperty());
        stackPane.minHeightProperty().bind(stackPane.prefHeightProperty());
        stackPane.prefWidthProperty().bind(prefWidthProperty());
        stackPane.prefHeightProperty().bind(prefHeightProperty().subtract(18));
        imageView = new ImageView();
        imageView.fitWidthProperty().bind(stackPane.prefWidthProperty());
        imageView.fitHeightProperty().bind(stackPane.prefHeightProperty());
        imageView.setPreserveRatio(true);
        imageView.setPickOnBounds(true);
        stackPane.getChildren().add(imageView);
        Label label = new Label();
        label.setText(item.getName());
        this.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/od/imageview/css/ThumbnailBoxModel.css")).toExternalForm());
        this.getStyleClass().add("thumbnail-box");
        this.setAlignment(Pos.CENTER);
        this.getChildren().addAll(stackPane, label);
//        setEvent(); // 事件
    }
//    public String getUrl() {
//        return item.getUrl();
//    }
//    private void setEvent(){
//        setSelectEvent();
//        setDragEvent();
//        setTipEvent();
//    }
//    private void setSelectEvent() {
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
//    }
//    private void setDragEvent() {
//        this.setOnDragDetected(event -> {
//            if (event.getButton() == MouseButton.PRIMARY && event.isAltDown()) {
//                Dragboard db = this.startDragAndDrop(TransferMode.MOVE);
//                ArrayList<ThumbnailBox> selectedList = ControllerManager.thumbnailPaneController.getSelectedList();
//                if (selectedList.isEmpty()) {
//                    ControllerManager.thumbnailPaneController.selectImage(this);
//                }
//                List<File> files = new ArrayList<>();
//                for(ThumbnailBox model : selectedList) {
//                    files.add(new File(model.getImageURL()));
//                }
//                Image im = new Image(String.valueOf(getClass().getResource("/com/od/imageview/image/icon/图片 (2).png") ));
//                db.setDragView(im,10,10);
//                ClipboardContent content = new ClipboardContent();
//                content.putFiles(files);
//                db.setContent(content);
//            }
//        });
//    }
//    private void setTipEvent(){
//        this.setOnMouseEntered(event -> {
//            Tooltip tooltip = new Tooltip();
//            tooltip.setStyle("-fx-text-fill: #f3f7f7;");
//            tooltip.setText("图片名：" + item.getName() + '\n' + "图片类型：" + item.getType() + '\n' + "图片大小：" + item.getFormatSize());
//            Tooltip.install(this, tooltip);
//        });
//    }
//    // 后台加载图片
//    public void loadImage(){
//        imageView.setImage(new Image(new File(item.getImageUrl()).toPath().toUri().toString(), 100,100,true,true,true));
//        //imageView.setImage(new Image(new File(imageModel.getImageUrl()).toPath().toUri().toString(), 100,100,true,true,false));
//    }
}
