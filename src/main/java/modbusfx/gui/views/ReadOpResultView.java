package modbusfx.gui.views;

import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import modbusfx.modbus.ReadFunction;
import modbusfx.modbus.ReadOp;
import modbusfx.modbus.Result;

public class ReadOpResultView extends VBox {

    private final TableView<Data> mView;

    public ReadOpResultView() {
        mView = new TableView<>();

        TableColumn<Data, String> addressColumn = new TableColumn<>("Address");
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("addressHex"));
        TableColumn<Data, Long> valueColumn = new TableColumn<>("Value");
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        TableColumn<Data, String> valueBitsColumn = new TableColumn<>("Bits");
        valueBitsColumn.setCellValueFactory(new PropertyValueFactory<>("valueBits"));

        //noinspection unchecked
        mView.getColumns().addAll(addressColumn, valueColumn, valueBitsColumn);

        getChildren().add(mView);
    }

    public void clear() {
        mView.getItems().clear();
    }

    public void loadResult(ReadFunction function, ReadOp op, Result result) {
        clear();

        byte[] resultData = result.getResult();
        RegisterSize registerSize = getRegisterSizeForFunction(function);
        for (int i = 0; i < op.getCount(); i++) {
            long value = 0;
            for (int j = 0; j < registerSize.byteStorageCount(); j++) {
                byte byteV = resultData[i * registerSize.byteStorageCount() + j];
                value |= ((long) byteV) << (8 * j);
            }

            Data data = new Data(
                    op.getAddress() + i,
                    value,
                    registerSize
            );
            mView.getItems().add(data);
        }
    }

    private RegisterSize getRegisterSizeForFunction(ReadFunction function) {
        switch (function) {
            case READ_DISCRETE_INPUTS:
            case READ_COILS:
                return RegisterSize.BIT;
            case READ_INPUT_REGISTERS:
            case READ_HOLDING_REGISTERS:
                return RegisterSize.WORD;
            default:
                throw new AssertionError();
        }
    }

    public enum RegisterSize {
        BIT(1, 1),
        WORD(2, 16);

        private final int mByteStorageCount;
        private final int mBitCount;

        RegisterSize(int byteStorageCount, int bitCount) {
            mByteStorageCount = byteStorageCount;
            mBitCount = bitCount;
        }

        public int byteStorageCount() {
            return mByteStorageCount;
        }

        public int bitCount() {
            return mBitCount;
        }
    }

    public static class Data {
        private final int mAddress;
        private final long mData;
        private final RegisterSize mSize;

        private Data(int address, long data, RegisterSize size) {
            mAddress = address;
            mData = data;
            mSize = size;
        }

        public String getAddressHex() {
            return "0x".concat(Integer.toHexString(mAddress));
        }

        public long getValue() {
            return mData;
        }

        public String getValueBits() {
            StringBuilder builder = new StringBuilder(mSize.bitCount());
            for (int i = mSize.bitCount(); i >= 0; i--) {
                builder.append((mData & (1L << i)) != 0 ? '1' : '0');
            }

            return builder.toString();
        }
    }
}
