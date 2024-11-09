package scau.os.soos.apps.fileManager.model;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import scau.os.soos.apps.fileManager.FileManagerApp;
import scau.os.soos.apps.fileManager.controller.DirectoryTreeController;
import scau.os.soos.module.file.FileController;
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
    private TextField textField;

    private EventHandler<KeyEvent> f2RenameHandler;

    /**
     * 构造函数，用于创建 ThumbnailBox 实例。
     *
     * @param item 要展示的项目
     */
    public ThumbnailBox(Item item) {
        this.item = item;
        initializeImageView();
        initializeTextContainer();
        setupStyleAndLayout();
        initializeEventListeners();
    }

    public Item getItem() {
        return item;
    }

    /**
     * 初始化 ImageView 及其容器。
     */
    private void initializeImageView() {
        StackPane imageViewContainer = new StackPane();

        ImageView imageView = new ImageView();
        imageView.fitWidthProperty().bind(imageViewContainer.prefWidthProperty());
        imageView.fitHeightProperty().bind(imageViewContainer.prefHeightProperty());
        imageView.setPreserveRatio(true);
        imageView.setPickOnBounds(true);

        String imagePath = determineImagePath();
        if (imagePath != null) {
            imageView.setImage(new Image(imagePath));
        } else {
            imageView.setImage(null);
        }

        imageViewContainer.getChildren().add(imageView);
        this.getChildren().add(imageViewContainer);
    }

    /**
     * 根据项目类型确定图片路径。
     *
     * @return 图片路径，如果不需要图片则返回 null
     */
    private String determineImagePath() {
        return switch (item) {
            case Exe exe -> EXE_IMAGE_PATH;
            case Txt txt -> TXT_IMAGE_PATH;
            case Directory directoryItem ->
                    directoryItem.getSize() > 0 ? DIRECTORY_IMAGE_PATH : EMPTY_DIRECTORY_IMAGE_PATH;
            case null, default -> null;
        };
    }

    /**
     * 初始化文本容器，包括 Label 和 TextField。
     */
    private void initializeTextContainer() {
        StackPane textContainer = new StackPane();
        textField = new TextField(item != null ? item.getName() : "");
        textField.setAlignment(Pos.CENTER);
        textField.setEditable(false);
        textField.setVisible(false);

        Label label = new Label();
        if (item != null) {
            label.setText(item.getName());
        }

        textContainer.getChildren().addAll(label, textField);
        this.getChildren().add(textContainer);
    }

    /**
     * 设置样式和布局。
     */
    private void setupStyleAndLayout() {
        this.getStylesheets().add(String.valueOf(FileManagerApp.class.getResource("main.css")));
        this.getStyleClass().add("thumbnail-box");
        this.setAlignment(Pos.CENTER);
    }

    /**
     * 初始化事件监听器。
     */
    private void initializeEventListeners() {
        setSelectEvent();
        setRenameEvent();
        setTipEvent();
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

    private void setRenameEvent() {
        // 监听文本字段的按键事件以完成重命名
        textField.setOnKeyPressed(this::handleRenameKeyPress);

        // 添加焦点监听器，当失去焦点时触发重命名逻辑
        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { // 失去焦点
                completeRenaming();
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


    public void startRenaming() {
        textField.setEditable(true);
        textField.setVisible(true);
        textField.positionCaret(textField.getText().length());
        textField.requestFocus();
    }

    private void handleRenameKeyPress(KeyEvent event) {
        // 如果按下的键是回车键
        if (event.getCode() == KeyCode.ENTER) {
            // 调用完成重命名的方法
            completeRenaming();
        }
        // 否则，如果按下的键是ESCAPE键
        else if (event.getCode() == KeyCode.ESCAPE) {
            // 调用取消重命名的方法
            cancelRenaming();
        }
    }

    private void completeRenaming() {
        String newName = textField.getText().trim();

        if (!newName.isEmpty() && !newName.equals(item.getName())) {
            Directory directory = (Directory) DirectoryTreeController.getInstance().getCurrentDirectory();

            byte currentItemType = this.getItem().getType();

            for (Item childItem : directory.getChildren()) {
                if (childItem != item && childItem.getName().equals(newName)) {
                    if (childItem.getType() == currentItemType) {
                        cancelRenaming();
                        return;
                    }
                }
            }
            FileController.getInstance().reName(item, newName);
            DirectoryTreeController.getInstance().refreshCurrentDirectory();
            FileManagerApp.getInstance().refreshCurrentDirectory();
        }
        cancelRenaming();
    }


    private void cancelRenaming() {
        textField.setEditable(false);
        textField.setVisible(false);
        this.requestFocus();
    }
}
