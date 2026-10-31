package co.edu.unilibre.gui;

import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 * PanelClasificacion
 */
public class PanelClasificacion {

    // JavaFX
    private VistaPrincipal vistaPrincipal;
    private VBox contenedor;

    public PanelClasificacion(VistaPrincipal vistaPrincipal) {
        // Inicializar el contenedor de JavaFX
        contenedor = new VBox(10);

        // Agregar los componentes al VBox
        // contenedor.getChildren().addAll(
        //     new Label("Usuario:"), txtUsuario,
        //     new Label("Contraseña:"), txtPassword,
        //     btnIngresar
        // );
    }

    public VBox getVista() {
        return contenedor;
    }

}
