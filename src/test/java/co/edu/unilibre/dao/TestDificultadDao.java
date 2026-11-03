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

import co.edu.unilibre.dto.Dificultad;

/**
 * TestDificultadDao
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestDificultadDao {

    private DificultadDao dificultadDao;

    @BeforeAll
    void setUp() throws SQLException {
        this.dificultadDao = new DificultadDaoImp();
        GestorConexiones.inicializarBaseDeDatos(GestorConexiones.obtenerConexion());
    }

    // ==========================================
    // 1. CASOS DE ÉXITO (HAPPY PATHS)
    // ==========================================

    @Test
    @DisplayName("Éxito: Debe guardar una dificultad correctamente en la BD")
    public void testGuardarDificultadExito() {
        Dificultad dif = new Dificultad("Dificultad prueba exito", 10, 10, 10);

        assertDoesNotThrow(() -> dificultadDao.guardarDificultad(dif),
                "La inserción de una dificultad válida no debería lanzar excepciones");

        Dificultad difGuardada = buscarPorNombre("Dificultad prueba exito");

        assertNotNull(difGuardada, "La dificultad debería existir en la BD");
        assertEquals("Dificultad prueba exito", difGuardada.getNombre());
        assertEquals(10, difGuardada.getMinas());
        assertEquals(10, difGuardada.getFilas());
        assertEquals(10, difGuardada.getColumnas());
    }

    @Test
    @DisplayName("Éxito: Debe listar las dificultades existentes en la BD")
    public void testListarDificultadesExito() {
        Dificultad dif = new Dificultad("Dificultad prueba listar", 15, 12, 12);
        dificultadDao.guardarDificultad(dif);

        List<Dificultad> dificultades = dificultadDao.listarDificultades();

        assertNotNull(dificultades, "La lista retornada no debe ser null");
        assertFalse(dificultades.isEmpty(), "La lista no debería estar vacía");
        assertTrue(dificultades.stream().anyMatch(d -> "Dificultad prueba listar".equals(d.getNombre())),
                "La dificultad insertada debe ser parte de la lista");
    }

    @Test
    @DisplayName("Éxito: Debe actualizar correctamente una dificultad existente")
    public void testActualizarDificultadExito() {
        Dificultad difOriginal = new Dificultad("Dificultad prueba actualizar", 10, 10, 10);
        dificultadDao.guardarDificultad(difOriginal);

        Dificultad registrada = buscarPorNombre("Dificultad prueba actualizar");
        assertNotNull(registrada, "La dificultad base debe existir");

        Dificultad difNuevosDatos = new Dificultad("Dificultad prueba actualizada", 25, 16, 16);

        assertDoesNotThrow(() -> dificultadDao.actualizarDificultad(registrada.getId(), difNuevosDatos));

        Dificultad difEditada = buscarPorNombre("Dificultad prueba actualizada");
        assertNotNull(difEditada, "La dificultad con el nuevo nombre debe encontrarse en la BD");
        assertEquals(25, difEditada.getMinas());
        assertEquals(16, difEditada.getFilas());
    }

    @Test
    @DisplayName("Éxito: Debe borrar una dificultad por su ID")
    public void testBorrarDificultadExito() {
        Dificultad dif = new Dificultad("Dificultad prueba borrar", 5, 8, 8);
        dificultadDao.guardarDificultad(dif);

        Dificultad registrada = buscarPorNombre("Dificultad prueba borrar");
        assertNotNull(registrada);

        assertDoesNotThrow(() -> dificultadDao.borrarDificultad(registrada.getId()));

        Dificultad eliminada = buscarPorNombre("Dificultad prueba borrar");
        assertNull(eliminada, "La dificultad eliminada ya no debe existir en la BD");
    }

    // ==========================================
    // 2. CASOS DE FALLO Y EXCEPCIONES (FAIL PATHS)
    // ==========================================

    @Test
    @DisplayName("Fallo: Debe lanzar excepción al guardar un objeto NULL")
    public void failGuardarDificultadNull() {
        assertThrows(RuntimeException.class, () -> {
            dificultadDao.guardarDificultad(null);
        }, "Guardar una dificultad null debe fallar con RuntimeException (NullPointerException envuelta)");
    }

    @Test
    @DisplayName("Fallo: Debe lanzar excepción al guardar nombre duplicado (Restricción UNIQUE)")
    public void failGuardarDificultadDuplicada() {
        Dificultad dif = new Dificultad("Dificultad prueba duplicada", 10, 10, 10);

        // Primera inserción correcta
        assertDoesNotThrow(() -> dificultadDao.guardarDificultad(dif));

        // Segunda inserción violando la restricción UNIQUE del nombre
        assertThrows(RuntimeException.class, () -> {
            dificultadDao.guardarDificultad(dif);
        }, "Intentar insertar el mismo nombre debe fallar por violación de restricción en BD");
    }

    @Test
    @DisplayName("Fallo: Debe lanzar excepción al guardar con nombre NULL")
    public void failGuardarNombreNull() {
        Dificultad dif = new Dificultad(null, 10, 10, 10);

        assertThrows(RuntimeException.class, () -> {
            dificultadDao.guardarDificultad(dif);
        }, "No se debe permitir guardar una dificultad cuyo nombre sea NULL (Restricción NOT NULL)");
    }

    @Test
    @DisplayName("Fallo: Actualizar un ID inexistente no debe alterar registros ni lanzar error fatal SQL")
    public void failActualizarIdInexistente() {
        int idFalso = -999;
        Dificultad nuevosDatos = new Dificultad("Dificultad prueba fantasma", 10, 10, 10);

        // No lanza excepción porque SQL ejecuta 0 filas afectadas, pero no debe crear nada nuevo
        assertDoesNotThrow(() -> dificultadDao.actualizarDificultad(idFalso, nuevosDatos));

        Dificultad resultado = buscarPorNombre("Dificultad prueba fantasma");
        assertNull(resultado, "No se debe haber insertado ninguna dificultad nueva");
    }

    @Test
    @DisplayName("Fallo: Borrar un ID inexistente no debe lanzar excepción SQL")
    public void failBorrarIdInexistente() {
        int idFalso = -999;

        assertDoesNotThrow(() -> {
            dificultadDao.borrarDificultad(idFalso);
        }, "Borrar un ID que no existe simplemente ejecuta 0 filas borradas sin romper la app");
    }

    private Dificultad buscarPorNombre(String nombre) {
        return dificultadDao.listarDificultades().stream()
                .filter(d -> nombre.equals(d.getNombre()))
                .findFirst()
                .orElse(null);
    }

    // ==========================================
    // LIMPIEZA
    // ==========================================

    @AfterEach
    void limpiarBaseDatos() {
        try (Connection conn = GestorConexiones.obtenerConexion();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM dificultades WHERE nombre LIKE 'Dificultad prueba%'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
