package co.edu.unilibre.gui;

import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

/**
 * ViewManager
 */
public class ViewManager {

    private static BorderPane rootLayout;
    private static Node currentView;

    public static void setRootLayout(BorderPane layout) {
        rootLayout = layout;
    }

    public static void loadView(Node newView) {
        if (rootLayout != null) {
            if (currentView != null) {
                currentView = null;
            }

            currentView = newView;
            rootLayout.setCenter(currentView);
        }
    }

    public static Node getCurrentView() {
        return currentView;
    }

    public static void clearCenter() {
        if (rootLayout != null) {
            rootLayout.setCenter(null);
            currentView = null;
        }
    }
}
