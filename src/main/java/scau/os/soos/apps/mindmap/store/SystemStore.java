package scau.os.soos.apps.mindmap.store;

import scau.os.soos.apps.mindmap.entity.Command;
import scau.os.soos.apps.mindmap.entity.NodeEntity;
import scau.os.soos.apps.mindmap.view.AuxiliaryNodeView;
import scau.os.soos.apps.mindmap.view.NodeContext;
import scau.os.soos.apps.mindmap.view.NodeView;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class SystemStore {
    static {
        nodeContext = new NodeContext();
    }
    private static final List<NodeEntity> rootNodeList = new ArrayList<>();
    private static NodeEntity selectedNode;
    private static AuxiliaryNodeView auxiliaryNode;
    private static NodeContext nodeContext;
    private static final SimpleIntegerProperty canvasScale = new SimpleIntegerProperty(100);
    private static final Stack<Command> undoStack = new Stack<>();
    private static final Stack<Command> redoStack = new Stack<>();
    private static final SimpleObjectProperty<File> file = new SimpleObjectProperty<>();
    private static volatile SimpleBooleanProperty loadingState = new SimpleBooleanProperty(false);

    public static NodeContext getNodeContext() {
        return nodeContext;
    }

    public static void setNodeContext(NodeContext nodeContext) {
        SystemStore.nodeContext = nodeContext;
    }

    public static int getCanvasScale() {
        return canvasScale.get();
    }

    public static SimpleIntegerProperty canvasScaleProperty() {
        return canvasScale;
    }

    public static void setCanvasScale(int canvasScale) {
        SystemStore.canvasScale.set(canvasScale);
    }

    public static Stack<Command> getUndoStack() {
        return undoStack;
    }

    public static Stack<Command> getRedoStack() {
        return redoStack;
    }

    public static List<NodeEntity> getRootNodeList() {
        return rootNodeList;
    }

    public static AuxiliaryNodeView getAuxiliaryNode() {
        return auxiliaryNode;
    }

    public static void setAuxiliaryNode(AuxiliaryNodeView auxiliaryNode) {
        SystemStore.auxiliaryNode = auxiliaryNode;
    }

    public static NodeEntity getSelectedNode() {
        return selectedNode;
    }

    public static void setSelectedNode(NodeEntity selectedNode) {
        SystemStore.selectedNode = selectedNode;
    }

    public static void setFile(File file) {
        SystemStore.file.set(file);
    }

    public static File getFile() {
        return SystemStore.file.get();
    }

    public static SimpleObjectProperty<File> file() {
        return SystemStore.file;
    }

    public static boolean isLoadingState() {
        return loadingState.get();
    }

    public static SimpleBooleanProperty loadingStateProperty() {
        return loadingState;
    }

    public static void setLoadingState(boolean loadingState) {
        SystemStore.loadingState.set(loadingState);
    }
}
