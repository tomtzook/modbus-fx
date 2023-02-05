package modbusfx.modbus;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import jmodbus.Modbus;
import jmodbus.ModbusClient;

import java.nio.ByteBuffer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TcpClient implements Client {

    private final StringProperty mIp;
    private final IntegerProperty mPort;
    private final IntegerProperty mSlaveId;
    private final BooleanProperty mIsOpen;
    private final BooleanProperty mIsConnected;

    private final ByteBuffer mBuffer;
    private final Lock mLock;
    private ModbusClient mClient;

    public TcpClient() {
        mIp = new SimpleStringProperty(null);
        mPort = new SimpleIntegerProperty(-1);
        mSlaveId = new SimpleIntegerProperty(-1);
        mIsOpen = new SimpleBooleanProperty(false);
        mIsConnected = new SimpleBooleanProperty(false);

        mBuffer = ByteBuffer.allocateDirect(64);
        mLock = new ReentrantLock();
        mClient = null;
    }

    public StringProperty ipAddressProperty() {
        return mIp;
    }

    public IntegerProperty portProperty() {
        return mPort;
    }

    public IntegerProperty slaveProperty() {
        return mSlaveId;
    }

    public ReadOnlyBooleanProperty isOpenProperty() {
        return mIsOpen;
    }

    public ReadOnlyBooleanProperty isConnectedProperty() {
        return mIsConnected;
    }

    @Override
    public Result process(ReadFunction function, ReadOp op) {
        mLock.lock();
        try {
            connectClient();

            switch (function) {
                case READ_DISCRETE_INPUTS: {
                    mClient.readDiscreteInputs(op.getAddress(), op.getCount(), mBuffer);
                    return new Result(mBuffer, op.getCount(), 1);
                }
                case READ_COILS: {
                    mClient.readCoils(op.getAddress(), op.getCount(), mBuffer);
                    return new Result(mBuffer, op.getCount(), 1);
                }
                case READ_INPUT_REGISTERS: {
                    mClient.readInputRegisters(op.getAddress(), op.getCount(), mBuffer);
                    return new Result(mBuffer, op.getCount(), 2);
                }
                case READ_HOLDING_REGISTERS: {
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
                    mBuffer.put(op.getData());
                    mBuffer.position(0);

                    mClient.writeCoils(op.getAddress(), op.getCount(), mBuffer);
                    break;
                }
                case WRITE_HOLDING_REGISTERS: {
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
        closeClient();
    }

    private void connectClient() {
        mLock.lock();
        try {
            if (mClient == null) {
                createClient();
            }

            if (!mIsConnected.get()) {
                mClient.connect();
                mIsConnected.set(true);
            }
        } finally {
            mLock.unlock();
        }
    }

    private void createClient() {
        mLock.lock();
        try {
            closeClient();

            String ip = ipAddressProperty().get();
            if (ip == null) {
                return;
            }

            int port = portProperty().get();
            if (port <= 0) {
                return;
            }

            int slave = slaveProperty().get();
            if (slave <= 0) {
                return;
            }

            mClient = Modbus.newTcpClient(ip, port);
            try {
                mClient.setSlave(slave);
            } catch (Error | RuntimeException e) {
                closeClient();
                throw e;
            }

            mIsOpen.set(true);
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

                mIsOpen.set(false);
                mIsConnected.set(false);
            }
        } finally {
            mLock.unlock();
        }
    }
}
