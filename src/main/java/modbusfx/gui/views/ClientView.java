package modbusfx.gui.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import modbusfx.modbus.TcpClient;
import modbusfx.modbus.TcpConnectionProps;

import java.io.Closeable;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class ClientView extends BorderPane implements Closeable {

    private final TcpClient mClient;
    private TcpConnectionProps mProps;
    private final TcpClientConfigView mConfigView;
    private final FlowPane mReadViewsPane;
    private final List<ReadOperationView> mReadOperations;

    public ClientView() {
        mClient = new TcpClient();
        mProps = new TcpConnectionProps();

        mConfigView = new TcpClientConfigView();
        mConfigView.setReadOnly(true);
        mConfigView.setProps(mProps);

        mReadViewsPane = new FlowPane();
        mReadOperations = new LinkedList<>();

        Button editClientConfig = new Button("Edit Client Config");
        editClientConfig.setOnAction((e)-> openEditConfigDialog());

        Button addNewReadOps = new Button("Add Read Operation");
        addNewReadOps.setOnAction((e)-> addNewReadOp());

        HBox buttonsPane = new HBox();
        buttonsPane.setSpacing(5);
        buttonsPane.setAlignment(Pos.CENTER);
        buttonsPane.setPadding(new Insets(2));
        buttonsPane.getChildren().addAll(editClientConfig, addNewReadOps);

        VBox controlPanel = new VBox();
        controlPanel.setSpacing(5);
        controlPanel.setAlignment(Pos.CENTER);
        controlPanel.setPadding(new Insets(2));
        controlPanel.getChildren().addAll(mConfigView, buttonsPane);

        setLeft(controlPanel);
        setCenter(mReadViewsPane);
    }

    public boolean openEditConfigDialog() {
        TcpClientConfigView configView = new TcpClientConfigView();

        Dialog<Boolean> dialog = new Dialog<>();
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.getDialogPane().setContent(configView);
        dialog.setResultConverter((dialogButton)-> {
            ButtonBar.ButtonData data = dialogButton == null ? null : dialogButton.getButtonData();
            return data == ButtonBar.ButtonData.APPLY;
        });
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.APPLY, ButtonType.CANCEL);

        configView.setProps(mProps);

        Optional<Boolean> optional = dialog.showAndWait();
        if (optional.isEmpty() || !optional.get()) {
            return false;
        }

        mProps = configView.getProps();
        mClient.setConnectionProps(mProps);
        mConfigView.setProps(mProps);

        return true;
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
