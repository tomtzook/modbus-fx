package modbusfx.gui.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import modbusfx.modbus.Client;

import java.io.Closeable;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;

public class ClientView extends BorderPane implements Closeable {

    private final Client mClient;
    private final ClientControl mClientControl;
    private final StackPane mDisplayedReadViewPane;
    private final ListView<ReadOperationView> mReadOperationsList;
    private final AtomicReference<ReadOperationView> mSelectedReadOp;

    public ClientView(Client client, ClientControl clientControl) {
        mClient = client;
        mClientControl = clientControl;

        mDisplayedReadViewPane = new StackPane();
        mSelectedReadOp = new AtomicReference<>();
        mReadOperationsList = new ListView<>();
        mReadOperationsList.setCellFactory((param)-> {
            return new ListCell<>() {
                @Override
                protected void updateItem(ReadOperationView item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        setText(item.getName());
                    }
                }
            };
        });
        mReadOperationsList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        mReadOperationsList.getSelectionModel().selectedItemProperty().addListener((obs, o, n)-> {
            mDisplayedReadViewPane.getChildren().clear();

            if (n == null) {
                mSelectedReadOp.set(null);
            } else {
                mSelectedReadOp.set(n);
                mDisplayedReadViewPane.getChildren().add(n);
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
        setCenter(mDisplayedReadViewPane);
    }

    public void openEditConfigDialog() {
        mClientControl.openEditConfigDialog();
    }

    public void update() {
        ReadOperationView view = mSelectedReadOp.get();
        if (view != null) {
            view.execute(mClient);
        }
    }

    private void addNewReadOp() {
        ReadOperationView view = new ReadOperationView();
        if (view.openEditConfigDialog()) {
            mReadOperationsList.getItems().add(view);
        }
    }

    @Override
    public void close() {
        mClient.close();
    }
}
