package modbusfx.modbus;

public class ReadOp {

    private final int mAddress;
    private final int mCount;

    public ReadOp(int address, int count) {
        mAddress = address;
        mCount = count;
    }

    public int getAddress() {
        return mAddress;
    }

    public int getCount() {
        return mCount;
    }
}
