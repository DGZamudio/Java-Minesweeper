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
import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import co.edu.unilibre.dto.Dificultad;
import co.edu.unilibre.dto.Partida;
import co.edu.unilibre.dto.Resultado;
import co.edu.unilibre.dto.Usuario;

/**
 * TestPartidaDao
 */
@TestInstance (TestInstance.Lifecycle.PER_CLASS)
public class TestPartidaDao {

    private PartidaDao partidaDao;
    private UsuarioDao usuarioDao;
    private DificultadDao dificultadDao;

    private Usuario usuarioPrueba;
    private Dificultad dificultadPrueba;

    @BeforeAll
    void setUp() throws SQLException {
        this.partidaDao = new PartidaDaoImp();
        this.usuarioDao = new UsuarioDaoImp();
        this.dificultadDao = new DificultadDaoImp();

        GestorConexiones.inicializarBaseDeDatos(GestorConexiones.obtenerConexion());

        String username = "user_partida_test";
        String password = "Contraseña segura";

        Usuario tempUser = new Usuario(username, password);
        usuarioDao.registrarUsuario(tempUser);
        this.usuarioPrueba = usuarioDao.autenticarUsuario(username, password);

        Dificultad tempDif = new Dificultad("Dificultad partida test", 10, 8, 8);
        dificultadDao.guardarDificultad(tempDif);
        this.dificultadPrueba = dificultadDao.listarDificultades().stream()
                .filter(d -> "Dificultad partida test".equals(d.getNombre()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No se pudo crear la dificultad base para el test"));
    }

    // ==========================================
    // 1. CASOS DE ÉXITO (HAPPY PATHS)
    // ==========================================

    @Test
    @DisplayName ("Éxito: Debe guardar una partida correctamente en la BD")
    public void testGuardarPartidaExito() {
        Partida partida = new Partida(dificultadPrueba, usuarioPrueba, null, Duration.ofSeconds(45), Resultado.GANADOR);

        assertDoesNotThrow(() -> partidaDao.guardarPartida(partida),
                "Guardar una partida válida no debería lanzar excepciones SQL");

        List<Partida> partidas = partidaDao.obtenerPartidas();
        assertNotNull(partidas);

        Partida guardada = partidas.stream()
                .filter(p -> p.getUsuario().getId() == usuarioPrueba.getId() && p.getResultado() == Resultado.GANADOR)
                .findFirst()
                .orElse(null);

        assertNotNull(guardada, "La partida registrada debe encontrarse en la BD");
        assertEquals(usuarioPrueba.getUsername(), guardada.getUsuario().getUsername());
        assertEquals(dificultadPrueba.getNombre(), guardada.getDificultad().getNombre());
        assertEquals(Duration.ofSeconds(45).toMillis(), guardada.getTiempo().toMillis());
        assertEquals(Resultado.GANADOR, guardada.getResultado());
    }

    @Test
    @DisplayName("Éxito: Debe obtener la lista de partidas con sus JOINs de Usuario y Dificultad")
    public void testObtenerPartidasExito() {
        Partida partida = new Partida(dificultadPrueba, usuarioPrueba, null, Duration.ofSeconds(120), Resultado.PERDEDOR);
        partidaDao.guardarPartida(partida);

        List<Partida> partidas = partidaDao.obtenerPartidas();

        assertNotNull(partidas, "La lista devuelta no debe ser null");
        assertFalse(partidas.isEmpty(), "La lista no debería estar vacía");

        boolean encontrada = partidas.stream()
                .anyMatch(p -> p.getUsuario().getId() == usuarioPrueba.getId() && p.getResultado() == Resultado.PERDEDOR);
        assertTrue(encontrada, "La partida con estado PERDEDOR debe existir en la lista");
    }

    @Test
    @DisplayName("Éxito: Debe eliminar una partida por su ID")
    public void testBorrarPartidaExito() {
        Partida partida = new Partida(dificultadPrueba, usuarioPrueba, null, Duration.ofSeconds(30), Resultado.GANADOR);
        partidaDao.guardarPartida(partida);

        Partida guardada = partidaDao.obtenerPartidas().stream()
                .filter(p -> p.getUsuario().getId() == usuarioPrueba.getId() && p.getTiempo().toSeconds() == 30)
                .findFirst()
                .orElse(null);

        assertNotNull(guardada, "La partida debe estar guardada antes de borrarla");

        assertDoesNotThrow(() -> partidaDao.borrarPartida(guardada.getId()));

        Partida eliminada = partidaDao.obtenerPartidas().stream()
                .filter(p -> p.getId() == guardada.getId())
                .findFirst()
                .orElse(null);

        assertNull(eliminada, "La partida eliminada ya no debe existir en la BD");
    }

    // ==========================================
    // 2. CASOS DE FALLO Y EXCEPCIONES (FAIL PATHS)
    // ==========================================

    @Test
    @DisplayName("Fallo: Debe lanzar excepción al guardar una partida NULL")
    public void failGuardarPartidaNull() {
        assertThrows(RuntimeException.class, () -> {
            partidaDao.guardarPartida(null);
        }, "Guardar una partida nula debe lanzar RuntimeException");
    }

    @Test
    @DisplayName("Fallo: Debe lanzar excepción al guardar una partida con usuario no persistido en BD (Violación FK)")
    public void failGuardarPartidaUsuarioInexistente() {
        Usuario usuarioFantasma = new Usuario(-999, "fantasma");

        Partida partida = new Partida(dificultadPrueba, usuarioFantasma, null, Duration.ofSeconds(10), Resultado.GANADOR);

        assertThrows(RuntimeException.class, () -> {
            partidaDao.guardarPartida(partida);
        }, "Insertar una partida con un usuario_id inexistente debe fallar por Foreign Key");
    }

    @Test
    @DisplayName("Fallo: Debe lanzar excepción al guardar una partida con dificultad no persistida en BD (Violación FK)")
    public void failGuardarPartidaDificultadInexistente() {
        Dificultad difFantasma = new Dificultad("Fantasma", 10, 8, 8);
        difFantasma.setId(-999); // ID inexistente en la tabla dificultades

        Partida partida = new Partida(difFantasma, usuarioPrueba, null, Duration.ofSeconds(10), Resultado.GANADOR);

        assertThrows(RuntimeException.class, () -> {
            partidaDao.guardarPartida(partida);
        }, "Insertar una partida con un dificultad_id inexistente debe fallar por Foreign Key");
    }

    @Test
    @DisplayName("Fallo: Borrar un ID de partida inexistente no debe lanzar excepción SQL")
    public void failBorrarIdInexistente() {
        int idFalso = -999;

        assertDoesNotThrow(() -> {
            partidaDao.borrarPartida(idFalso);
        }, "Borrar un ID inexistente debe ejecutar 0 filas afectadas sin romper la app");
    }

    // ==========================================
    // LIMPIEZA
    // ==========================================

    @AfterEach
    void limpiarBaseDatos() {
        try (Connection conn = GestorConexiones.obtenerConexion();
                Statement stmt = conn.createStatement()) {
            // Elimina únicamente las partidas creadas por el usuario de prueba
            stmt.executeUpdate("DELETE FROM partidas WHERE id = " + usuarioPrueba.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    void limpiarBaseDatosTotal() {
        try (Connection conn = GestorConexiones.obtenerConexion();
                Statement stmt = conn.createStatement()){
            // Elimina el usuario y la dificultad de testeo
            stmt.executeUpdate("DELETE FROM usuarios WHERE id = " + usuarioPrueba.getId());
            stmt.executeUpdate("DELETE FROM dificultades WHERE id = " + dificultadPrueba.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
