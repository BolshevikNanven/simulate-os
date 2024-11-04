package scau.os.soos.apps.fileManager.model;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import scau.os.soos.apps.fileManager.FileManagerApp;
import scau.os.soos.module.file.model.Directory;
import scau.os.soos.module.file.model.Exe;
import scau.os.soos.module.file.model.Item;
import scau.os.soos.module.file.model.Txt;

import java.util.List;

public class ThumbnailBox extends VBox {
    private static final String EXE_IMAGE_PATH = String.valueOf(FileManagerApp.class.getResource("image/thumbnailBox/exe.png"));
    private static final String TXT_IMAGE_PATH = String.valueOf(FileManagerApp.class.getResource("image/thumbnailBox/txt.png"));
    private static final String DIRECTORY_IMAGE_PATH = String.valueOf(FileManagerApp.class.getResource("image/thumbnailBox/directory.png"));
    private static final String EMPTY_DIRECTORY_IMAGE_PATH = String.valueOf(FileManagerApp.class.getResource("image/thumbnailBox/empty_directory.png"));

    private final Item item;

    public ThumbnailBox(Item item) {
        this.item = item;

        StackPane stackPane = new StackPane();

        ImageView imageView = new ImageView();
        imageView.fitWidthProperty().bind(stackPane.prefWidthProperty());
        imageView.fitHeightProperty().bind(stackPane.prefHeightProperty());
        imageView.setPreserveRatio(true);
        imageView.setPickOnBounds(true);

        String imagePath = switch (item) {
            case Exe exe -> EXE_IMAGE_PATH;
            case Txt txt -> TXT_IMAGE_PATH;
            case Directory directoryItem ->
                    directoryItem.getSize() > 0 ? DIRECTORY_IMAGE_PATH : EMPTY_DIRECTORY_IMAGE_PATH;
            case null, default -> null;
        };

        if (imagePath != null) {
            imageView.setImage(new Image(imagePath));
        } else {
            imageView.setImage(null);
        }

        stackPane.getChildren().add(imageView);

        Label label = new Label();
        if (item != null) {
            label.setText(item.getName());
        }

        this.getStylesheets().add(String.valueOf(FileManagerApp.class.getResource("main.css")));
        this.getStyleClass().add("thumbnail-box");
        this.setAlignment(Pos.CENTER);
        this.getChildren().addAll(stackPane, label);

        setSelectEvent();
        setTipEvent();
    }

    public Item getItem() {
        return item;
    }

    private void setSelectEvent() {
        this.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            // 按住ctrl键多选
            if (event.isControlDown()
                    && event.getClickCount() == 1
                    && !event.isShiftDown()
                    && !event.isAltDown()) {
                FileManagerApp.getInstance().selectItem(this);

                return;
            }

            List<ThumbnailBox> selectedList = FileManagerApp.getInstance().getSelectedList();
            FlowPane itemContainer = FileManagerApp.getInstance().getItemContainer();

            // 按住shift键多选
            if (!event.isAltDown()
                    && !event.isControlDown()
                    && event.getClickCount() == 1
                    && event.isShiftDown()
                    && selectedList.size() == 1) {

                int startItem = -1, endItem = -1;

                ThumbnailBox selectedItem = selectedList.get(0);
                for (int i = 0; i < itemContainer.getChildren().size(); i++) {
                    ThumbnailBox item = (ThumbnailBox) itemContainer.getChildren().get(i);
                    if ((item == selectedItem || item == this) && startItem == -1) {
                        startItem = i;
                    } else if (item == selectedItem || item == this) {
                        endItem = i;
                    }
                    if (startItem != -1 && endItem != -1) {
                        break;
                    }
                }

                if (startItem != -1 && endItem != -1) {
                    for (int i = startItem; i <= endItem; i++) {
                        FileManagerApp.getInstance().selectItem((ThumbnailBox) itemContainer.getChildren().get(i));
                    }
                }

                return;
            }

            // 右键菜单
            if (!event.isAltDown()
                    && event.getClickCount() == 1
                    && (event.getButton() == MouseButton.PRIMARY
                    || (event.getButton() == MouseButton.SECONDARY
                    && selectedList.size() <= 1))) {
                FileManagerApp.getInstance().clearSelectedList();
                FileManagerApp.getInstance().selectItem(this);
                return;
            }

            // 双击打开
            if (event.getClickCount() == 2) {
                FileManagerApp.getInstance().open(item);

                /***
                 *
                 * 本应在 FileAreaSelect 类中执行的鼠标释放事件，被不知名原因被吞掉，
                 * 不加这一行，selectedArea就无法隐藏
                 *
                 */
                FileManagerApp.getInstance().getSelectedArea().setVisible(false);
            }
        });
    }

    private void setTipEvent() {
        this.setOnMouseEntered(event -> {
            Tooltip tooltip = new Tooltip();
            tooltip.setStyle("-fx-font-size:12;" +
                            "-fx-background-color:#fafafa;" +
                            "-fx-text-fill:#585959;");
            tooltip.setText("类型：" + (char) item.getType() + '\n' + "大小：" + item.getSize());
            Tooltip.install(this, tooltip);
        });
    }
}
