package co.edu.unilibre.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * VistaPrincipal
 */
public class VistaPrincipal extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
	    PanelTablero panelTablero = new PanelTablero();

        Scene scene = new Scene(panelTablero.getVista(), 600, 650);

        primaryStage.setTitle("Buscaminas");
        primaryStage.setScene(scene);
        primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

}
