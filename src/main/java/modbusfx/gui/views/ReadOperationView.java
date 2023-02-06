package modbusfx.gui.views;

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

    private ReadOperation mOperation;

    private final TextArea mResultText;
    private final TextArea mErrorText;

    public ReadOperationView() {
        mResultText = new TextArea();
        mResultText.setEditable(false);
        mResultText.setWrapText(true);
        mResultText.setMaxSize(300, 200);

        mErrorText = new TextArea();
        mErrorText.setEditable(false);
        mErrorText.setWrapText(true);
        mErrorText.setMaxSize(400, 100);

        VBox resultsView = new VBox();
        resultsView.setSpacing(5);
        resultsView.setAlignment(Pos.CENTER);
        resultsView.getChildren().addAll(mResultText);

        VBox errorView = new VBox();
        errorView.setSpacing(5);
        errorView.setPadding(new Insets(2));
        errorView.setAlignment(Pos.BOTTOM_CENTER);
        errorView.getChildren().addAll(mErrorText);

        setCenter(resultsView);
        setBottom(errorView);
    }

    public void setOperation(ReadOperation operation) {
        mOperation = operation;

        mResultText.setText("");
        mErrorText.setText("");
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
            return true;
        }

        return false;
    }

    public void loadResult(Result result) {
        mResultText.setText(Arrays.toString(result.getResult()));
    }

    public void loadError(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        String exceptionText = sw.toString();

        mErrorText.setText(exceptionText);
    }
}
