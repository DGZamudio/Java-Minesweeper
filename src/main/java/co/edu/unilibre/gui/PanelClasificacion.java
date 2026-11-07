package co.edu.unilibre.gui;

import javafx.scene.layout.VBox;

/**
 * PanelClasificacion
 */
public class PanelClasificacion {

    // JavaFX
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
