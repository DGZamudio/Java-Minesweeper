package co.edu.unilibre;

import java.sql.SQLException;

import co.edu.unilibre.dao.GestorConexiones;
import co.edu.unilibre.gui.VistaPrincipal;
import javafx.application.Application;

/**
 * Main
 */
public class Main {
    public static void main(String[] args) {
        GestorConexiones gc = new GestorConexiones();
        try {
            gc.inicializarBaseDeDatos(gc.obtenerConexion());
        } catch (SQLException e) {
            throw new RuntimeException("Error al conectar a la base de datos", e);
        }

        Application.launch(VistaPrincipal.class, args);
    }
}
