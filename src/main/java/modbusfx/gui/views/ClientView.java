package modbusfx.gui.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import modbusfx.modbus.Client;

import java.io.Closeable;
import java.util.LinkedList;
import java.util.List;

public class ClientView extends BorderPane implements Closeable {

    private final Client mClient;
    private final ClientControl mClientControl;
    private final FlowPane mReadViewsPane;
    private final List<ReadOperationView> mReadOperations;

    public ClientView(Client client, ClientControl clientControl) {
        mClient = client;
        mClientControl = clientControl;

        mReadViewsPane = new FlowPane();
        mReadOperations = new LinkedList<>();

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
        controlPanel.setAlignment(Pos.BOTTOM_LEFT);
        controlPanel.setPadding(new Insets(2));
        controlPanel.getChildren().addAll(mClientControl, buttonsPane);

        setLeft(controlPanel);
        setCenter(mReadViewsPane);
    }

    public void openEditConfigDialog() {
        mClientControl.openEditConfigDialog();
    }

    public void update() {
        for (ReadOperationView view : mReadOperations) {
            view.execute(mClient);
        }
    }

    private void addNewReadOp() {
        ReadOperationView view = new ReadOperationView();
        if (view.openEditConfigDialog()) {
            mReadOperations.add(view);
            mReadViewsPane.getChildren().add(view);
        }
    }

    @Override
    public void close() {
        mClient.close();
    }
}
