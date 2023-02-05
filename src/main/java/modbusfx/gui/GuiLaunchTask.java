package modbusfx.gui;

import javafx.application.Application;

public class GuiLaunchTask implements Runnable {

    @Override
    public void run() {
        Application.launch(ApplicationGui.class);
    }
}
