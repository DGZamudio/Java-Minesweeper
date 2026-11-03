package co.edu.unilibre.dao;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import co.edu.unilibre.dto.Tema;

/**
 * TestTemaDao
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestTemaDao {

    private TemaDao temaDao;

    @BeforeAll
    void setUp() throws SQLException {
        this.temaDao = new TemaDaoImp();
        GestorConexiones.inicializarBaseDeDatos(GestorConexiones.obtenerConexion());
    }

    // ==========================================
    // 1. CASOS DE ÉXITO (HAPPY PATHS)
    // ==========================================

    @Test
    @DisplayName("Éxito: Debe crear un tema correctamente en la BD")
    public void testCrearTemaExito() {
        Tema tema = new Tema("Minecraft", "#FFFFFF", "#000000", "tnt.png", "bandera.png", "bg_mc.png");

        assertDoesNotThrow(() -> temaDao.crearTema(tema),
                "Crear un tema válido no debería lanzar excepciones");

        Tema guardado = buscarPorNombre("Minecraft");

        assertNotNull(guardado, "El tema guardado debería encontrarse en la BD");
        assertEquals("Minecraft", guardado.getNombre());
        assertEquals("#FFFFFF", guardado.getBg());
        assertEquals("#000000", guardado.getFg());
        assertEquals("tnt.png", guardado.getMina());
        assertEquals("bandera.png", guardado.getBandera());
        assertEquals("bg_mc.png", guardado.getBgImage());
    }

    @Test
    @DisplayName("Éxito: Debe listar los temas registrados")
    public void testListarTemasExito() {
        Tema tema = new Tema("Tema Listar", "#111", "#222", "mina.png", "flag.png", "bg.png");
        temaDao.crearTema(tema);

        List<Tema> temas = temaDao.listarTemas();

        assertNotNull(temas, "La lista devuelta no debe ser null");
        assertFalse(temas.isEmpty(), "La lista no debe estar vacía");
        assertTrue(temas.stream().anyMatch(t -> "Tema Listar".equals(t.getNombre())),
                "El tema creado debe figurar en la lista");
    }

    @Test
    @DisplayName("Éxito: Debe actualizar los datos de un tema existente")
    public void testActualizarTemaExito() {
        Tema temaOriginal = new Tema("Tema Editar", "#000", "#FFF", "m1.png", "b1.png", "bg1.png");
        temaDao.crearTema(temaOriginal);

        Tema registrado = buscarPorNombre("Tema Editar");
        assertNotNull(registrado, "El tema inicial debe existir para poder editarlo");

        Tema nuevosDatos = new Tema("Tema Editado", "#333", "#444", "m2.png", "b2.png", "bg2.png");

        assertDoesNotThrow(() -> temaDao.actualizarTema(registrado.getId(), nuevosDatos));

        Tema modificado = buscarPorNombre("Tema Editado");
        assertNotNull(modificado, "El tema actualizado debe ubicarse con el nuevo nombre");
        assertEquals("#333", modificado.getBg());
        assertEquals("m2.png", modificado.getMina());
    }

    @Test
    @DisplayName("Éxito: Debe eliminar un tema por ID")
    public void testBorrarTemaExito() {
        Tema tema = new Tema("Tema Borrar", "#000", "#FFF", "m.png", "b.png", "bg.png");
        temaDao.crearTema(tema);

        Tema registrado = buscarPorNombre("Tema Borrar");
        assertNotNull(registrado);

        assertDoesNotThrow(() -> temaDao.borrarTema(registrado.getId()));

        Tema eliminado = buscarPorNombre("Tema Borrar");
        assertNull(eliminado, "El tema borrado ya no debe figurar en la BD");
    }

    // ==========================================
    // 2. CASOS DE FALLO Y EXCEPCIONES (FAIL PATHS)
    // ==========================================

    @Test
    @DisplayName("Fallo: Debe lanzar excepción al crear un tema NULL")
    public void failCrearTemaNull() {
        assertThrows(RuntimeException.class, () -> {
            temaDao.crearTema(null);
        }, "Crear un tema nulo debe arrojar una RuntimeException");
    }

    @Test
    @DisplayName("Fallo: Debe lanzar excepción al crear un tema con nombre duplicado (Si UNIQUE)")
    public void failCrearTemaNombreDuplicado() {
        Tema tema1 = new Tema("Tema Duplicado", "#111", "#222", "m.png", "b.png", "bg.png");
        Tema tema2 = new Tema("Tema Duplicado", "#333", "#444", "m.png", "b.png", "bg.png");

        assertDoesNotThrow(() -> temaDao.crearTema(tema1));

        assertThrows(RuntimeException.class, () -> {
            temaDao.crearTema(tema2);
        }, "Insertar un tema con nombre duplicado debe fallar si la columna tiene restricción UNIQUE");
    }

    @Test
    @DisplayName("Fallo: Actualizar un ID inexistente no debe alterar registros")
    public void failActualizarIdInexistente() {
        int idFalso = -999;
        Tema nuevosDatos = new Tema("Tema Fantasma", "#000", "#000", "m.png", "b.png", "bg.png");

        assertDoesNotThrow(() -> temaDao.actualizarTema(idFalso, nuevosDatos));

        Tema resultado = buscarPorNombre("Tema Fantasma");
        assertNull(resultado, "No se debió registrar ningún tema nuevo");
    }

    @Test
    @DisplayName("Fallo: Borrar un ID inexistente no debe lanzar excepción SQL")
    public void failBorrarIdInexistente() {
        int idFalso = -999;

        assertDoesNotThrow(() -> {
            temaDao.borrarTema(idFalso);
        }, "Borrar un ID que no existe debe ejecutar 0 filas afectadas sin romper la app");
    }

    // ==========================================
    // AUXILIARES Y LIMPIEZA
    // ==========================================

    private Tema buscarPorNombre(String nombre) {
        return temaDao.listarTemas().stream()
                .filter(t -> nombre.equals(t.getNombre()))
                .findFirst()
                .orElse(null);
    }

    @AfterEach
    void limpiarBaseDatos() {
        try (Connection conn = GestorConexiones.obtenerConexion();
                Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM temas WHERE nombre LIKE 'Tema%' OR nombre = 'Minecraft'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
