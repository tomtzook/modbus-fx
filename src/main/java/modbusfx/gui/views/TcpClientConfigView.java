package modbusfx.gui.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import modbusfx.gui.controls.NumericField;
import modbusfx.modbus.TcpClient;
import modbusfx.modbus.TcpConnectionProps;

public class TcpClientConfigView extends GridPane {

    private final TextField mIpField;
    private final NumericField mPortField;
    private final NumericField mIdField;

    public TcpClientConfigView() {
        mIpField = new TextField("127.0.0.1");
        add(new Label("IP"), 0, 0);
        add(mIpField, 1, 0);

        mPortField = new NumericField(int.class);
        mPortField.valueProperty().setValue(5000);
        mPortField.setText("5000");
        add(new Label("Port"), 0, 1);
        add(mPortField, 1, 1);

        mIdField = new NumericField(int.class);
        mIdField.valueProperty().setValue(0);
        mIdField.setText("0");
        add(new Label("ID"), 0, 2);
        add(mIdField, 1, 2);

        setHgap(5);
        setVgap(10);
        setAlignment(Pos.CENTER);
        setPadding(new Insets(2));
    }

    public void setReadOnly(boolean readOnly) {
        mIpField.setEditable(!readOnly);
        mIpField.setDisable(readOnly);

        mPortField.setEditable(!readOnly);
        mPortField.setDisable(readOnly);

        mIdField.setEditable(!readOnly);
        mIdField.setDisable(readOnly);
    }

    public TcpConnectionProps getProps() {
        TcpConnectionProps props = new TcpConnectionProps();
        props.setIp(mIpField.getText());
        props.setPort(mPortField.valueProperty().getValue().intValue());
        props.setSlaveId(mIdField.valueProperty().getValue().intValue());

        return props;
    }

    public void setProps(TcpConnectionProps props) {
        mIpField.setText(props.getIp());

        mPortField.valueProperty().setValue(props.getPort());
        mPortField.setText(String.valueOf(props.getPort()));

        mIdField.valueProperty().setValue(props.getSlaveId());
        mIdField.setText(String.valueOf(props.getSlaveId()));
    }
}
