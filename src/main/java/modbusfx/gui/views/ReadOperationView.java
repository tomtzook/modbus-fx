package modbusfx.gui.views;

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
        mConfigView.setPrefWidth(100);

        mResultText = new TextArea();
        mResultText.setEditable(false);
        mResultText.setWrapText(true);
        mResultText.setMaxSize(100, 20);

        mErrorText = new TextArea();
        mErrorText.setEditable(false);
        mErrorText.setWrapText(true);
        mErrorText.setMaxSize(100, 20);

        VBox resultsView = new VBox();
        resultsView.setSpacing(5);
        resultsView.getChildren().addAll(mResultText, mErrorText);

        setLeft(mConfigView);
        setCenter(resultsView);
    }

    public boolean openEditConfigDialog() {
        ReadOperationConfigView configView = new ReadOperationConfigView();
        configView.loadFrom(mOperation);

        if (Dialogs.showCustomApplyDialog(configView)) {
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
