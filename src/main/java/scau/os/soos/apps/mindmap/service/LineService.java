package scau.os.soos.apps.mindmap.service;

import scau.os.soos.apps.mindmap.entity.NodeEntity;
import scau.os.soos.apps.mindmap.store.SettingStore;
import scau.os.soos.apps.mindmap.view.Line.CurveLine;
import scau.os.soos.apps.mindmap.view.Line.StraightLine;
import scau.os.soos.apps.mindmap.view.Line.TwoPolyLine;
import scau.os.soos.apps.mindmap.view.LineView;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.Pane;

import java.util.concurrent.atomic.AtomicReference;

public class LineService {
    private static LineService instance;
    private Pane canvas;

    private LineService() {

    }

    private LineService(Pane canvas) {
        this.canvas = canvas;
    }

    public static void init(Pane canvas) {
        if (null == instance) {
            instance = new LineService(canvas);
        }
    }

    public static LineService getInstance() {
        return instance;
    }

    public void addLine(NodeEntity head, NodeEntity tail) {
        if (head == null) return;

        //前后节点宽高渲染是否完毕信号量
        AtomicReference<Integer> semaphore = new AtomicReference<>(0);

        //如果前后节点均不是新建的则直接添加线条
        if (head.getActualHeight() != 0 && head.getActualWidth() != 0 && tail.getActualWidth() != 0 && tail.getActualHeight() != 0) {
            doAddLine(head, tail);
            return;
        }

        //否则监听属性变化，根据信号量来新增
        head.actualHeightProperty().addListener(new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                semaphore.getAndSet(semaphore.get() + 1);
                head.actualHeightProperty().removeListener(this);
                if (semaphore.get() >= 4) {
                    doAddLine(head, tail);
                }
            }
        });
        head.actualWidthProperty().addListener(new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                semaphore.getAndSet(semaphore.get() + 1);
                head.actualWidthProperty().removeListener(this);
                if (semaphore.get() >= 4) {
                    doAddLine(head, tail);
                }
            }
        });
        tail.actualHeightProperty().addListener(new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                semaphore.getAndSet(semaphore.get() + 1);
                tail.actualHeightProperty().removeListener(this);
                if (semaphore.get() >= 4) {
                    doAddLine(head, tail);
                }
            }
        });
        tail.actualWidthProperty().addListener(new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                semaphore.getAndSet(semaphore.get() + 1);
                tail.actualWidthProperty().removeListener(this);
                if (semaphore.get() >= 4) {
                    doAddLine(head, tail);
                }
            }
        });


    }

    private void doAddLine(NodeEntity head, NodeEntity tail) {
        //先删除原有的线
        if (tail.getLine() != null) {
            deleteLine(tail.getLine());
            tail.setLine(null);
        }

        LineView lineView = generateLine(head, tail);
        tail.setLine(lineView);
        canvas.getChildren().add(lineView.render());
    }

    private LineView generateLine(NodeEntity head, NodeEntity tail) {
        LayoutService layoutService = SettingStore.getLayoutService();

        switch (SettingStore.getLine()) {
            case "StraightLine" -> {
                return new StraightLine(head, tail, layoutService.getLineHead(head, tail), layoutService.getLineTail(tail, tail));
            }
            case "CurveLine" -> {
                return new CurveLine(head, tail, layoutService.getLineHead(head, tail), layoutService.getLineTail(tail, tail));
            }
            default -> {
                return new TwoPolyLine(head, tail, layoutService.getLineHead(head, tail), layoutService.getLineTail(tail, tail));
            }
        }
    }

    public void deleteLine(LineView lineView) {
        canvas.getChildren().remove(lineView.render());
        lineView.setHead(null);
        lineView.setTail(null);
    }

}
