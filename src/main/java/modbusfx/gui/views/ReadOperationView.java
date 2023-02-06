package modbusfx.gui.views;

import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import modbusfx.gui.Dialogs;
import modbusfx.modbus.Client;
import modbusfx.modbus.Result;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;

public class ReadOperationView extends BorderPane {

    private final ReadOperation mOperation;
    private final ReadOperationConfigView mConfigView;

    private final TextArea mResultText;
    private final TextArea mErrorText;

    public ReadOperationView() {
        mOperation = new ReadOperation();

        mConfigView = new ReadOperationConfigView();
        mConfigView.setReadOnly(true);
        mConfigView.setPrefWidth(250);

        mResultText = new TextArea();
        mResultText.setEditable(false);
        mResultText.setWrapText(true);
        mResultText.setMaxSize(300, 200);

        mErrorText = new TextArea();
        mErrorText.setEditable(false);
        mErrorText.setWrapText(true);
        mErrorText.setMaxSize(400, 100);

        VBox configView = new VBox();
        configView.setSpacing(5);
        configView.setPadding(new Insets(2));
        configView.setAlignment(Pos.CENTER_LEFT);
        configView.getChildren().addAll(mConfigView);

        VBox resultsView = new VBox();
        resultsView.setSpacing(5);
        resultsView.setAlignment(Pos.CENTER);
        resultsView.getChildren().addAll(mResultText);

        VBox errorView = new VBox();
        errorView.setSpacing(5);
        errorView.setPadding(new Insets(2));
        errorView.setAlignment(Pos.BOTTOM_CENTER);
        errorView.getChildren().addAll(mErrorText);

        setTop(configView);
        setCenter(resultsView);
        setBottom(errorView);
    }

    public String getName() {
        return mOperation.nameProperty().getValue();
    }

    public boolean openEditConfigDialog() {
        ReadOperationConfigView configView = new ReadOperationConfigView();
        configView.loadFrom(mOperation);

        if (Dialogs.showCustomApplyDialog(configView)) {
            if (!configView.areValuesValid()) {
                Dialogs.showInfo("Error", "Bad values entered");
                return false;
            }

            configView.saveInto(mOperation);
            mConfigView.loadFrom(mOperation);
            return true;
        }

        return false;
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
