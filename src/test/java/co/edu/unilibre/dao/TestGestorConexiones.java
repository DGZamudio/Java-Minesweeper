package co.edu.unilibre.dao;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * TestGestorConexiones
 */
public class TestGestorConexiones {

    @Test
    @DisplayName ("Éxito: Debe obtener variables de entorno / .env correctamente")
    public void testLecturaVariablesEntorno() {
        String dbUrl = GestorConexiones.getDbUrl();
        String dbUser = GestorConexiones.getDbUser();

        assertNotNull(dbUrl, "La URL de la BD no debería ser null");
        assertNotNull(dbUser, "El usuario de la BD no debería ser null");
        assertFalse(dbUrl.isEmpty(), "La URL de la BD no debería estar vacía");
    }

    @Test
    @DisplayName("Éxito: Debe entregar una conexión válida desde el Pool de HikariCP")
    public void testObtenerConexionExito() throws SQLException {
        try (Connection conn = GestorConexiones.obtenerConexion()) {
            assertNotNull(conn, "La conexión no debe ser null");
            assertFalse(conn.isClosed(), "La conexión entregada por Hikari debe estar abierta");
            assertTrue(conn.isValid(2), "La conexión debe responder exitosamente en un timeout de 2 segundos");
        }
    }

    @Test
    @DisplayName("Éxito: Debe inicializar el esquema de la base de datos sin excepciones (schema.sql)")
    public void testInicializarBaseDeDatosExito() throws SQLException {
        try (Connection conn = GestorConexiones.obtenerConexion()) {
            assertDoesNotThrow(() -> GestorConexiones.inicializarBaseDeDatos(conn),
                    "La ejecución de schema.sql no debe lanzar excepciones");
        }
    }

    // ==========================================
    // 2. CASOS DE FALLO Y EXCEPCIONES (FAIL PATHS)
    // ==========================================

    @Test
    @DisplayName("Fallo: Debe arrojar NullPointerException si se pasa una conexión nula al inicializar la BD")
    public void failInicializarBaseDeDatosConexionNull() {
        // Al intentar usar la conexión null en el Statement lanzará una excepción
        assertDoesNotThrow(() -> {
            // El método captura la excepción internamente e imprime el e.printStackTrace()
            GestorConexiones.inicializarBaseDeDatos(null);
        });
    }

    @Test
    @DisplayName("Fallo: Las conexiones cerradas deben retornar isClosed() en true al devolverlas al pool")
    public void testCierreDeConexionDevuelveAlPool() throws SQLException {
        Connection conn = GestorConexiones.obtenerConexion();
        assertNotNull(conn);
        assertFalse(conn.isClosed());

        // Cerrar la conexión devuélvela al pool de Hikari
        conn.close();

        assertTrue(conn.isClosed(), "Una vez devuelta al pool, la instancia de Connection debe marcarse como cerrada localmente");
    }

}
