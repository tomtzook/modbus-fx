package modbusfx.gui.views;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import modbusfx.modbus.Client;
import modbusfx.modbus.ReadFunction;
import modbusfx.modbus.ReadOp;
import modbusfx.modbus.Result;

public class ReadOperation {

    private final StringProperty mName;
    private final Property<ReadFunction> mFunction;
    private final IntegerProperty mAddress;
    private final IntegerProperty mCount;

    public ReadOperation() {
        mName = new SimpleStringProperty();
        mFunction = new SimpleObjectProperty<>(ReadFunction.READ_COILS);
        mAddress = new SimpleIntegerProperty(0);
        mCount = new SimpleIntegerProperty(1);
    }

    public StringProperty nameProperty() {
        return mName;
    }

    public Property<ReadFunction> functionProperty() {
        return mFunction;
    }

    public IntegerProperty addressProperty() {
        return mAddress;
    }

    public IntegerProperty countProperty() {
        return mCount;
    }

    public Result executeOnClient(Client client) {
        ReadFunction function = mFunction.getValue();
        int address = mAddress.getValue();
        int count = mCount.getValue();

        return client.process(function, new ReadOp(address, count));
    }
}
