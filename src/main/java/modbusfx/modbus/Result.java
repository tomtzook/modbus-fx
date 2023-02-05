package modbusfx.modbus;

import java.nio.ByteBuffer;

public class Result {

    private final byte[] mResult;

    public Result(byte[] result) {
        mResult = result;
    }

    public Result(ByteBuffer buffer, int count, int dataSize) {
        mResult = new byte[count * dataSize];
        buffer.get(mResult);
    }

    public byte[] getResult() {
        return mResult;
    }
}
