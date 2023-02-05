package modbusfx.gui.views;

import modbusfx.modbus.TcpClient;

public enum ClientType {
    TCP {
        @Override
        public ClientView createView() {
            TcpClient client = new TcpClient();
            return new ClientView(client, new TcpClientControl(client));
        }
    }
    ;

    public abstract ClientView createView();
}
