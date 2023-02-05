package modbusfx.modbus;

import jmodbus.Modbus;
import jmodbus.ModbusClient;

import java.nio.ByteBuffer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TcpClient implements Client {

    private TcpConnectionProps mConnectionProps;
    private boolean mPropsModified;
    private final ClientState mClientState;
    private boolean mDisable;

    private final ByteBuffer mBuffer;
    private final Lock mLock;
    private ModbusClient mClient;

    public TcpClient() {
        mConnectionProps = new TcpConnectionProps();
        mPropsModified = false;
        mClientState = new ClientState();
        mDisable = false;

        mBuffer = ByteBuffer.allocateDirect(64);
        mLock = new ReentrantLock();
        mClient = null;
    }

    public void setConnectionProps(TcpConnectionProps props) {
        mLock.lock();
        try {
            mConnectionProps = props;
            mPropsModified = true;
        } finally {
            mLock.unlock();
        }
    }

    public ClientState getState() {
        mLock.lock();
        try {
            return new ClientState(mClientState);
        } finally {
            mLock.unlock();
        }
    }

    @Override
    public Result process(ReadFunction function, ReadOp op) {
        mLock.lock();
        try {
            connectClient();

            switch (function) {
                case READ_DISCRETE_INPUTS: {
                    mBuffer.rewind();
                    mClient.readDiscreteInputs(op.getAddress(), op.getCount(), mBuffer);
                    return new Result(mBuffer, op.getCount(), 1);
                }
                case READ_COILS: {
                    mBuffer.rewind();
                    mClient.readCoils(op.getAddress(), op.getCount(), mBuffer);
                    return new Result(mBuffer, op.getCount(), 1);
                }
                case READ_INPUT_REGISTERS: {
                    mBuffer.rewind();
                    mClient.readInputRegisters(op.getAddress(), op.getCount(), mBuffer);
                    return new Result(mBuffer, op.getCount(), 2);
                }
                case READ_HOLDING_REGISTERS: {
                    mBuffer.rewind();
                    mClient.readHoldingRegisters(op.getAddress(), op.getCount(), mBuffer);
                    return new Result(mBuffer, op.getCount(), 2);
                }
                default:
                    throw new UnsupportedOperationException();
            }
        } finally {
            mLock.unlock();
        }
    }

    @Override
    public void process(WriteFunction function, WriteOp op) {
        mLock.lock();
        try {
            connectClient();

            switch (function) {
                case WRITE_COILS:  {
                    mBuffer.rewind();
                    mBuffer.put(op.getData());
                    mBuffer.position(0);

                    mClient.writeCoils(op.getAddress(), op.getCount(), mBuffer);
                    break;
                }
                case WRITE_HOLDING_REGISTERS: {
                    mBuffer.rewind();
                    mBuffer.put(op.getData());
                    mBuffer.position(0);

                    mClient.writeHoldingRegisters(op.getAddress(), op.getCount(), mBuffer);
                    break;
                }
                default:
                    throw new UnsupportedOperationException();
            }
        } finally {
            mLock.unlock();
        }
    }

    @Override
    public void close() {
        mLock.lock();
        try {
            closeClient();
            mDisable = true;
        } finally {
            mLock.unlock();
        }
    }

    private void connectClient() {
        mLock.lock();
        try {
            if (mDisable) {
                return;
            }

            if (mPropsModified) {
                closeClient();
                mPropsModified = false;
            }

            if (mClient == null) {
                createClient();

                if (mClient == null) {
                    return;
                }
            }

            if (!mClientState.isConnected()) {
                mClient.connect();
                mClientState.setConnected(true);
            }
        } finally {
            mLock.unlock();
        }
    }

    private void createClient() {
        mLock.lock();
        try {
            closeClient();

            if (mDisable) {
                return;
            }

            if (!mConnectionProps.isValid()) {
                return;
            }

            mClient = Modbus.newTcpClient(mConnectionProps.getIp(), mConnectionProps.getPort());
            try {
                mClient.setSlave(mConnectionProps.getSlaveId());
            } catch (Error | RuntimeException e) {
                closeClient();
                throw e;
            }

            mClientState.setOpen(true);
        } finally {
            mLock.unlock();
        }
    }

    private void closeClient() {
        mLock.lock();
        try {
            if (mClient != null) {
                mClient.close();
                mClient = null;

                mClientState.setOpen(false);
                mClientState.setConnected(false);
            }
        } finally {
            mLock.unlock();
        }
    }
}
