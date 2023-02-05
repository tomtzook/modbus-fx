package modbusfx.modbus;

public class TcpConnectionProps {

    private String mIp;
    private int mPort;
    private int mSlaveId;

    public TcpConnectionProps(String ip, int port, int slaveId) {
        mIp = ip;
        mPort = port;
        mSlaveId = slaveId;
    }

    public TcpConnectionProps() {
        this(null, -1, -1);
    }

    public String getIp() {
        return mIp;
    }

    public void setIp(String ip) {
        mIp = ip;
    }

    public int getPort() {
        return mPort;
    }

    public void setPort(int port) {
        mPort = port;
    }

    public int getSlaveId() {
        return mSlaveId;
    }

    public void setSlaveId(int slaveId) {
        mSlaveId = slaveId;
    }

    public boolean isValid() {
        return mIp != null && mPort > 0 && mSlaveId >= 0;
    }
}
