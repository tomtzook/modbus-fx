package modbusfx.gui;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import modbusfx.gui.views.ClientView;

public class MainWindow implements AutoCloseable {

    private final double mWidth;
    private final double mHeight;

    private final Stage mOwner;
    private final BorderPane mRoot;
    private final ClientView mClientView;

    public MainWindow(Stage owner, double width, double height) {
        mOwner = owner;
        mWidth = width;
        mHeight = height;
        mRoot = new BorderPane();

        mClientView = new ClientView();
    }

    public Scene createScene() {
        mRoot.setCenter(mClientView);
        return new Scene(mRoot, mWidth, mHeight);
    }

    public void updateClients() {
        Platform.runLater(mClientView::update);
    }

    @Override
    public void close() throws Exception {
        mClientView.close();
    }
}
