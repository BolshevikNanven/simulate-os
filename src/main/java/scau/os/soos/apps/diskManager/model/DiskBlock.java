package scau.os.soos.apps.diskManager.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DiskBlock {
    private final IntegerProperty blockNumber = new SimpleIntegerProperty(this, "blockNumber");
    private final StringProperty state = new SimpleStringProperty(this, "state");
    private final IntegerProperty nextIndex = new SimpleIntegerProperty(this, "nextIndex");

    public DiskBlock(Integer blockNumber, String state,Integer nextIndex) {
        this.blockNumber.set(blockNumber);
        this.state.set(state);
        this.nextIndex.set(nextIndex);
    }

    public IntegerProperty blockNumberProperty() {
        return blockNumber;
    }

    public int getBlockNumber() {
        return blockNumber.get();
    }

    public StringProperty stateProperty() {
        return state;
    }

    public String getState() {
        return state.get();
    }

    public int getNextIndex() {
        return nextIndex.get();
    }

    public IntegerProperty nextIndexProperty() {
        return nextIndex;
    }
}
