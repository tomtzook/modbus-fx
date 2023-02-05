package modbusfx.gui.views;

import modbusfx.gui.Dialogs;
import modbusfx.modbus.TcpClient;
import modbusfx.modbus.TcpConnectionProps;

public class TcpClientControl extends ClientControl {

    private final TcpClient mClient;
    private final TcpClientConfigView mConfigView;
    private TcpConnectionProps mProps;

    public TcpClientControl(TcpClient client) {
        mClient = client;
        mProps = new TcpConnectionProps();

        mConfigView = new TcpClientConfigView();
        mConfigView.setReadOnly(true);
        mConfigView.setProps(mProps);

        getChildren().add(mConfigView);
    }

    @Override
    public void openEditConfigDialog() {
        TcpClientConfigView configView = new TcpClientConfigView();
        configView.setProps(mProps);

        if (Dialogs.showCustomApplyDialog(configView)) {
            mProps = configView.getProps();
            mClient.setConnectionProps(mProps);
            mConfigView.setProps(mProps);
        }
    }
}
