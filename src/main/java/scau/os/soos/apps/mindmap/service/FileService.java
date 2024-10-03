package scau.os.soos.apps.mindmap.service;

import scau.os.soos.apps.mindmap.entity.FileDTO;
import scau.os.soos.apps.mindmap.entity.NodeEntity;
import scau.os.soos.apps.mindmap.entity.SettingEntity;
import scau.os.soos.apps.mindmap.store.SettingStore;
import scau.os.soos.apps.mindmap.store.SystemStore;
import scau.os.soos.apps.mindmap.store.ThreadsPool;
import scau.os.soos.apps.mindmap.util.AlgorithmUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Transform;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;


public class FileService {
    private static FileService instance;
    private Stage stage;
    private ObjectMapper objectMapper;
    private MenuButton menuButton;
    private Button savaFileButton;
    private Button saveAsImageButton;
    private ScrollPane scrollPane;

    private FileService() {
    }

    private FileService(Parent root, Stage stage, Scene scene) {
        this.stage = stage;
        this.objectMapper = new ObjectMapper();
        this.menuButton = (MenuButton) root.lookup("#menu");
        this.savaFileButton = (Button) root.lookup("#save-file-button");
        this.saveAsImageButton = (Button) root.lookup("#save-as-image-button");
        this.scrollPane = (ScrollPane) root.lookup("#canvas");

        addListener();
    }

    public static void init(Parent root, Stage stage, Scene scene) {
        if (null == instance) {
            instance = new FileService(root, stage, scene);
        }
    }

    public static FileService getInstance() {
        return instance;
    }

    private void addListener() {
        menuButton.getItems().forEach(menuItem -> {
            switch (menuItem.getId()) {
                case "save-as-file-menu" -> menuItem.setOnAction(actionEvent -> writeFile());
                case "save-file-menu" -> menuItem.setOnAction(actionEvent -> handleSave());
                case "new-file-menu" -> menuItem.setOnAction(actionEvent -> newFile());
                case "open-file-menu" -> menuItem.setOnAction(actionEvent -> readFile());
                case "save-as-image-menu" -> menuItem.setOnAction(actionEvent -> exportAsImage());
            }
        });
        savaFileButton.setOnAction(actionEvent -> handleSave());
        saveAsImageButton.setOnAction(actionEvent -> exportAsImage());
    }

    public void exportAsImage() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("导出为图片");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG 图片", "*.png"),
                new FileChooser.ExtensionFilter("JPG 图片", "*.jpg")
        );

        File file = fileChooser.showSaveDialog(stage);


        if (file == null) {
            return;
        }

        Pane contentPane = (Pane) scrollPane.getContent();


        Bounds contentBounds = contentPane.getLayoutBounds();
        double width = contentBounds.getWidth();
        double height = contentBounds.getHeight();


        SnapshotParameters params = new SnapshotParameters();
        params.setTransform(Transform.scale(1, 1));
        params.setViewport(new Rectangle2D(0, 0, width, height));


        WritableImage image = new WritableImage((int) width, (int) height);
        contentPane.snapshot(params, image);


        ThreadsPool.run(() -> {
            try {
                // 获取用户选择的文件扩展名
                String extension = "";
                if (file.getName().endsWith(".png")) {
                    extension = "png";
                } else if (file.getName().endsWith(".jpg")) {
                    extension = "jpg";
                }


                if (extension.isEmpty()) {
                    return;
                }


                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);


                if ("jpg".equals(extension)) {
                    BufferedImage bufferedImageRGB = new BufferedImage(
                            bufferedImage.getWidth(),
                            bufferedImage.getHeight(),
                            BufferedImage.TYPE_INT_RGB);
                    Graphics2D graphics = bufferedImageRGB.createGraphics();
                    graphics.drawImage(bufferedImage, 0, 0, null);
                    graphics.dispose();
                    bufferedImage = bufferedImageRGB;
                }


                ImageIO.write(bufferedImage, extension, file);
            } catch (IOException e) {
                e.printStackTrace();

            }
        });
    }


    public void handleSave() {
        if (SystemStore.getFile() == null) {
            writeFile();
        } else {
            writeFile(SystemStore.getFile());
        }
    }

    public void writeFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("保存到");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("json", "*.json"));
        File file = fileChooser.showSaveDialog(stage);
        if (file == null) {
            return;
        }

        ThreadsPool.run(() -> {
            try {
                if (!file.createNewFile()) {
                    throw new RuntimeException();
                }

                FileWriter fileWriter = new FileWriter(file);

                FileDTO fileDTO = new FileDTO();
                fileDTO.setNodes(SystemStore.getRootNodeList());
                fileDTO.setSetting(SettingStore.getSetting());

                objectMapper.writer().writeValue(fileWriter, fileDTO);

                fileWriter.close();
                SystemStore.setFile(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

    }

    public void writeFile(File file) {
        ThreadsPool.run(() -> {
            try {
                FileWriter fileWriter = new FileWriter(file);
                FileDTO fileDTO = new FileDTO();
                fileDTO.setNodes(SystemStore.getRootNodeList());
                fileDTO.setSetting(SettingStore.getSetting());

                objectMapper.writer().writeValue(fileWriter, fileDTO);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void readFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("打开");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("json", "*.json"));
        File file = fileChooser.showOpenDialog(stage);
        if (file == null) {
            return;
        }

        ThreadsPool.run(() -> {
            try (FileReader fileReader = new FileReader(file)) {
                FileDTO fileDTO = objectMapper.readValue(fileReader, FileDTO.class);

                List<NodeEntity> list = fileDTO.getNodes();
                SettingEntity setting = fileDTO.getSetting();

                Platform.runLater(()->SettingStore.setSetting(setting));

                SystemStore.getRootNodeList().forEach(node -> {
                    Platform.runLater(() -> NodeService.getInstance().deleteNode(node));
                });
                SystemStore.setFile(file);
                for (NodeEntity root : list) {
                    SystemStore.getRootNodeList().add(root);
                    //序列化时无parent参数，需手动添加
                    AlgorithmUtil.headMapNode(root, (parent, node) -> {
                        node.setParent(parent);
                        node.setDisabled(true);
                    });
                }
                Platform.runLater(() -> NodeService.getInstance().renderNodeTree());

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });
    }

    public void newFile() {
        //handleSave();
        UndoAndRedoService.getInstance().clear();

        SystemStore.setFile(null);
        SystemStore.setSelectedNode(null);
        SystemStore.getRootNodeList().clear();
        NodeService.getInstance().renderNodeTree();
    }
}
