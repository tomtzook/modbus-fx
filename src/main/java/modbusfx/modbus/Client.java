package modbusfx.modbus;

import java.io.Closeable;

public interface Client extends Closeable {

    ClientState getState();

    Result process(ReadFunction function, ReadOp op);
    void process(WriteFunction function, WriteOp op);

    @Override
    void close();
}
