package modbusfx.gui.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import modbusfx.gui.controls.NumericField;
import modbusfx.modbus.ReadFunction;

public class ReadOperationConfigView extends GridPane {

    private final ComboBox<ReadFunction> mFunctionsBox;
    private final NumericField mAddressField;
    private final NumericField mCountField;

    public ReadOperationConfigView() {
        mFunctionsBox = new ComboBox<>();
        mFunctionsBox.getItems().addAll(ReadFunction.values());
        mFunctionsBox.setValue(ReadFunction.READ_DISCRETE_INPUTS);
        add(mFunctionsBox, 0, 0, 2, 1);

        mAddressField = new NumericField(int.class);
        mAddressField.setText("0");
        mAddressField.valueProperty().setValue(0);
        add(new Label("Address"), 0, 1, 1, 1);
        add(mAddressField, 1, 1, 1, 1);

        mCountField = new NumericField(int.class);
        mCountField.valueProperty().setValue(1);
        mCountField.setText("1");
        add(new Label("Count"), 0, 2, 1, 1);
        add(mCountField, 1, 2, 1, 1);

        setHgap(5);
        setVgap(10);
        setAlignment(Pos.CENTER);
        setPadding(new Insets(2));
    }

    public void setReadOnly(boolean readOnly) {
        mFunctionsBox.setEditable(!readOnly);
        mFunctionsBox.setDisable(readOnly);

        mAddressField.setEditable(!readOnly);
        mAddressField.setDisable(readOnly);

        mCountField.setEditable(!readOnly);
        mCountField.setDisable(readOnly);
    }

    public void loadFrom(ReadOperation operation) {
        mFunctionsBox.setValue(operation.functionProperty().getValue());

        int address = operation.addressProperty().getValue();
        mAddressField.valueProperty().setValue(address);
        mAddressField.setText(String.valueOf(address));

        int count = operation.countProperty().getValue();
        mCountField.valueProperty().setValue(count);
        mCountField.setText(String.valueOf(count));
    }

    public void saveInto(ReadOperation operation) {
        operation.functionProperty().setValue(mFunctionsBox.getValue());
        operation.addressProperty().set(mAddressField.valueProperty().getValue().intValue());
        operation.countProperty().set(mCountField.valueProperty().getValue().intValue());
    }
}
