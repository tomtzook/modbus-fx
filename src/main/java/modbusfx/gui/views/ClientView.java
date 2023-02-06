package modbusfx.gui.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import modbusfx.modbus.Client;
import modbusfx.modbus.ReadFunction;
import modbusfx.modbus.ReadOp;
import modbusfx.modbus.Result;

import java.io.Closeable;
import java.util.concurrent.atomic.AtomicReference;

public class ClientView extends BorderPane implements Closeable {

    private final Client mClient;
    private final ClientControl mClientControl;
    private final ReadOperationView mDisplayedReadViewPane;
    private final TableView<ReadOperation> mReadOperationsList;
    private final AtomicReference<ReadOperation> mSelectedReadOp;

    public ClientView(Client client, ClientControl clientControl) {
        mClient = client;
        mClientControl = clientControl;

        mDisplayedReadViewPane = new ReadOperationView();
        mSelectedReadOp = new AtomicReference<>();
        mReadOperationsList = new TableView<>();
        mReadOperationsList.setPrefSize(300, 400);

        StackPane displayReadOpNode = new StackPane();
        displayReadOpNode.getChildren().add(mDisplayedReadViewPane);

        TableColumn<ReadOperation, String> nameColumn = new TableColumn<>("name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<ReadOperation, ReadFunction> functionColumn = new TableColumn<>("Function");
        functionColumn.setCellValueFactory(new PropertyValueFactory<>("function"));
        TableColumn<ReadOperation, Integer> addressColumn = new TableColumn<>("Address");
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        TableColumn<ReadOperation, Integer> countColumn = new TableColumn<>("Count");
        countColumn.setCellValueFactory(new PropertyValueFactory<>("count"));

        //noinspection unchecked
        mReadOperationsList.getColumns().addAll(nameColumn, functionColumn, addressColumn, countColumn);

        mReadOperationsList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        mReadOperationsList.getSelectionModel().selectedItemProperty().addListener((obs, o, n)-> {
            if (n == null) {
                mDisplayedReadViewPane.setOperation(null);
                mSelectedReadOp.set(null);
            } else {
                mDisplayedReadViewPane.setOperation(n);
                mSelectedReadOp.set(n);
            }
        });

        Button editClientConfig = new Button("Edit Client Config");
        editClientConfig.setOnAction((e)-> openEditConfigDialog());

        Button addNewReadOps = new Button("Add Read Operation");
        addNewReadOps.setOnAction((e)-> addNewReadOp());

        HBox buttonsPane = new HBox();
        buttonsPane.setSpacing(5);
        buttonsPane.setAlignment(Pos.BOTTOM_LEFT);
        buttonsPane.setPadding(new Insets(2));
        buttonsPane.getChildren().addAll(editClientConfig, addNewReadOps);

        VBox controlPanel = new VBox();
        controlPanel.setSpacing(5);
        controlPanel.setAlignment(Pos.CENTER_LEFT);
        controlPanel.setPadding(new Insets(2));
        controlPanel.getChildren().addAll(mClientControl, mReadOperationsList, buttonsPane);

        setLeft(controlPanel);
        setCenter(displayReadOpNode);
    }

    public void openEditConfigDialog() {
        mClientControl.openEditConfigDialog();
    }

    public void update() {
        ReadOperation operation = mSelectedReadOp.get();
        if (operation != null) {
            try {
                Result result = operation.executeOnClient(mClient);
                mDisplayedReadViewPane.loadResult(
                        operation.functionProperty().getValue(),
                        new ReadOp(operation.addressProperty().getValue(), operation.countProperty().getValue()),
                        result);
            } catch (Throwable t) {
                mDisplayedReadViewPane.loadError(t);
            }
        }
    }

    private void addNewReadOp() {
        ReadOperation operation = new ReadOperation();
        ReadOperationView view = new ReadOperationView();
        view.setOperation(operation);

        if (view.openEditConfigDialog()) {
            mReadOperationsList.getItems().add(operation);
        }
    }

    @Override
    public void close() {
        mClient.close();
    }
}
