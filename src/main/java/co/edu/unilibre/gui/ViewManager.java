package co.edu.unilibre.gui;

import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

/**
 * ViewManager
 */
public class ViewManager {

    private static BorderPane rootLayout;

    public static void setRootLayout(BorderPane layout) {
        rootLayout = layout;
    }

    public static void loadView(Node view) {
        if (rootLayout != null) {
            rootLayout.setCenter(view);
        }
    }
}
