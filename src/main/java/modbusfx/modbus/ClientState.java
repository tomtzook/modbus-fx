package modbusfx.modbus;

public class ClientState {

    private boolean mIsOpen;
    private boolean mIsConnected;

    public ClientState(boolean isOpen, boolean isConnected) {
        mIsOpen = isOpen;
        mIsConnected = isConnected;
    }

    public ClientState(ClientState clientState) {
        this(clientState.mIsOpen, clientState.mIsConnected);
    }

    public ClientState() {
        this(false, false);
    }

    public boolean isOpen() {
        return mIsOpen;
    }

    public void setOpen(boolean open) {
        mIsOpen = open;
    }

    public boolean isConnected() {
        return mIsConnected;
    }

    public void setConnected(boolean connected) {
        mIsConnected = connected;
    }
}
