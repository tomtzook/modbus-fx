package modbusfx.gui.views;

import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import modbusfx.modbus.Client;
import modbusfx.modbus.Result;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Optional;

public class ReadOperationView extends BorderPane {

    private final ReadOperation mOperation;
    private final ReadOperationConfigView mConfigView;

    private final TextArea mResultText;
    private final TextArea mErrorText;

    public ReadOperationView() {
        mOperation = new ReadOperation();

        mConfigView = new ReadOperationConfigView();
        mConfigView.setReadOnly(true);

        mResultText = new TextArea();
        mResultText.setEditable(false);
        mResultText.setWrapText(true);

        mErrorText = new TextArea();
        mErrorText.setEditable(false);
        mErrorText.setWrapText(true);

        VBox resultsView = new VBox();
        resultsView.setSpacing(5);
        resultsView.getChildren().addAll(mResultText, mErrorText);

        setLeft(mConfigView);
        setCenter(resultsView);
    }

    public boolean openEditConfigDialog() {
        ReadOperationConfigView configView = new ReadOperationConfigView();

        Dialog<Boolean> dialog = new Dialog<>();
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.getDialogPane().setContent(configView);
        dialog.setResultConverter((dialogButton)-> {
            ButtonBar.ButtonData data = dialogButton == null ? null : dialogButton.getButtonData();
            return data == ButtonBar.ButtonData.APPLY;
        });
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.APPLY, ButtonType.CANCEL);

        configView.loadFrom(mOperation);

        Optional<Boolean> optional = dialog.showAndWait();
        if (optional.isEmpty() || !optional.get()) {
            return false;
        }

        configView.saveInto(mOperation);
        mConfigView.loadFrom(mOperation);

        return true;
    }

    public void execute(Client client) {
        try {
            Result result = mOperation.executeOnClient(client);
            loadResult(result);
        } catch (Throwable t) {
            loadError(t);
        }
    }

    private void loadResult(Result result) {
        mResultText.setText(Arrays.toString(result.getResult()));
    }

    private void loadError(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        String exceptionText = sw.toString();

        mErrorText.setText(exceptionText);
    }
}
