package co.edu.unilibre.dao;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import co.edu.unilibre.dto.Usuario;

/**
 * TestUsuarioDao
 */
@TestInstance (TestInstance.Lifecycle.PER_CLASS)
public class TestUsuarioDao {

    private UsuarioDao usuarioDao;

    @BeforeAll
    void setUp() throws SQLException {
        this.usuarioDao = new UsuarioDaoImp();
        GestorConexiones.inicializarBaseDeDatos(GestorConexiones.obtenerConexion());
    }

    // ==========================================
    // 1. CASOS DE ÉXITO (HAPPY PATHS)
    // ==========================================

    @Test
    @DisplayName ("Éxito: Debe registrar un nuevo usuario correctamente en la BD")
    public void testRegistrarUsuarioExito() {
        Usuario u = crearUsuarioInstancia("usr_registro", "pass123");

        assertDoesNotThrow(() -> usuarioDao.registrarUsuario(u),
                "El registro de usuario no debe lanzar excepciones SQL");

        Usuario autenticado = usuarioDao.autenticarUsuario("usr_registro", "pass123");
        assertNotNull(autenticado, "El usuario recién registrado debe poder autenticarse");
        assertEquals("usr_registro", autenticado.getUsername());
        assertTrue(autenticado.getId() > 0, "El ID asignado debe ser válido");
    }

    @Test
    @DisplayName("Éxito: Debe autenticar credenciales correctas con BCrypt")
    public void testAutenticarUsuarioExito() {
        Usuario u = crearUsuarioInstancia("usr_login", "ClaveSegura123!");
        usuarioDao.registrarUsuario(u);

        Usuario autenticado = usuarioDao.autenticarUsuario("usr_login", "ClaveSegura123!");

        assertNotNull(autenticado, "Debe retornar el objeto Usuario al ingresar contraseña correcta");
        assertEquals("usr_login", autenticado.getUsername());
    }

    @Test
    @DisplayName("Éxito: Debe actualizar el username y la contraseña hasheada")
    public void testActualizarUsuarioExito() {
        Usuario uOriginal = crearUsuarioInstancia("usr_viejo", "claveVieja");
        usuarioDao.registrarUsuario(uOriginal);

        Usuario registrado = usuarioDao.autenticarUsuario("usr_viejo", "claveVieja");
        assertNotNull(registrado, "Debe existir el usuario previo a actualizar");

        Usuario uNuevosDatos = crearUsuarioInstancia("usr_nuevo", "claveNueva");

        assertDoesNotThrow(() -> usuarioDao.actualizarUsuario(registrado.getId(), uNuevosDatos));

        // Debe fallar con la clave vieja y el nombre viejo
        assertNull(usuarioDao.autenticarUsuario("usr_viejo", "claveVieja"));

        // Debe autenticarse exitosamente con los nuevos datos
        Usuario actualizado = usuarioDao.autenticarUsuario("usr_nuevo", "claveNueva");
        assertNotNull(actualizado, "El usuario actualizado debe autenticarse con la nueva contraseña");
    }

    @Test
    @DisplayName("Éxito: Debe borrar un usuario por su ID")
    public void testBorrarUsuarioExito() {
        Usuario u = crearUsuarioInstancia("usr_borrar", "eliminar123");
        usuarioDao.registrarUsuario(u);

        Usuario registrado = usuarioDao.autenticarUsuario("usr_borrar", "eliminar123");
        assertNotNull(registrado);

        assertDoesNotThrow(() -> usuarioDao.borrarUsuario(registrado.getId()));

        Usuario borrado = usuarioDao.autenticarUsuario("usr_borrar", "eliminar123");
        assertNull(borrado, "El usuario borrado ya no puede autenticarse en el sistema");
    }

    // ==========================================
    // 2. CASOS DE FALLO Y EXCEPCIONES (FAIL PATHS)
    // ==========================================

    @Test
    @DisplayName("Fallo: Debe retornar null si la contraseña es incorrecta")
    public void failAutenticarPasswordIncorrecta() {
        Usuario u = crearUsuarioInstancia("usr_cred_err", "passwordCorrecta");
        usuarioDao.registrarUsuario(u);

        Usuario resultado = usuarioDao.autenticarUsuario("usr_cred_err", "passwordIncorrecta");

        assertNull(resultado, "Autenticar con contraseña errónea debe retornar null");
    }

    @Test
    @DisplayName("Fallo: Debe retornar null si el username no existe")
    public void failAutenticarUsuarioInexistente() {
        Usuario resultado = usuarioDao.autenticarUsuario("usuario_fantasma_xyz", "123456");

        assertNull(resultado, "Autenticar un usuario inexistente debe retornar null");
    }

    @Test
    @DisplayName("Fallo: Debe lanzar excepción al registrar username duplicado (Restricción UNIQUE)")
    public void failRegistrarUsernameDuplicado() {
        Usuario u1 = crearUsuarioInstancia("usr_duplicado", "pass1");
        Usuario u2 = crearUsuarioInstancia("usr_duplicado", "pass2");

        assertDoesNotThrow(() -> usuarioDao.registrarUsuario(u1));

        assertThrows(RuntimeException.class, () -> {
            usuarioDao.registrarUsuario(u2);
        }, "Intentar registrar dos usuarios con el mismo username debe arrojar excepción por UNIQUE");
    }

    @Test
    @DisplayName("Fallo: Debe lanzar excepción al registrar usuario NULL")
    public void failRegistrarUsuarioNull() {
        assertThrows(RuntimeException.class, () -> {
            usuarioDao.registrarUsuario(null);
        }, "Registrar un usuario nulo debe arrojar RuntimeException");
    }

    @Test
    @DisplayName("Fallo: Borrar un ID inexistente no debe lanzar excepción SQL")
    public void failBorrarIdInexistente() {
        int idFalso = -999;

        assertDoesNotThrow(() -> {
            usuarioDao.borrarUsuario(idFalso);
        }, "Borrar un ID inexistente debe ejecutar 0 filas borradas sin arrojar excepción");
    }

    // ==========================================
    // METODOS AUXILIARES Y LIMPIEZA
    // ==========================================

    private Usuario crearUsuarioInstancia(String username, String password) {
        Usuario u = new Usuario(username);
        u.setPassword(password); // Asumiendo que tu DTO tiene setPassword()
        return u;
    }

    @AfterEach
    void limpiarBaseDatos() {
        try (Connection conn = GestorConexiones.obtenerConexion();
                Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM usuarios WHERE username LIKE 'usr_%'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
