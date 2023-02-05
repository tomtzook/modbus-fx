package modbusfx.modbus;

public class WriteOp {

    private final int mAddress;
    private final int mCount;
    private final byte[] mData;

    public WriteOp(int address, int count, byte[] data) {
        mAddress = address;
        mCount = count;
        mData = data;
    }

    public int getAddress() {
        return mAddress;
    }

    public int getCount() {
        return mCount;
    }

    public byte[] getData() {
        return mData;
    }
}
