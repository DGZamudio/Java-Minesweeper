package co.edu.unilibre.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.mindrot.jbcrypt.BCrypt;

import co.edu.unilibre.dto.Usuario;

/**
 * UsuarioDaoImp
 */
public class UsuarioDaoImp implements UsuarioDao {

	@Override
	public void registrarUsuario(Usuario usr) {
	    GestorConexiones gc = new GestorConexiones();
		String sql = "INSERT INTO usuarios (username, password) VALUES (?, ?);";

		try (Connection conn = gc.obtenerConexion();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usr.getUsername());
            stmt.setString(2, BCrypt.hashpw(usr.getPassword(), BCrypt.gensalt(10)));

            stmt.executeUpdate();
        }
        catch (SQLException e) {
            throw new RuntimeException("Error al registrar al usuario", e);
        }
	}

	@Override
	public Usuario autenticarUsuario(String username, String password) {
	    GestorConexiones gc = new GestorConexiones();
		String sql = "SELECT * FROM usuarios WHERE username = ?;";

		try (Connection conn = gc.obtenerConexion();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet res = stmt.executeQuery();

            if (res.next()) {
                int id = res.getInt("id");
                String passHash = res.getString("password");

                if (BCrypt.checkpw(password, passHash)) {
                    Usuario usr = new Usuario(username);
                    usr.setId(id);
                    return usr;
                }
            }
            return null;
        }
        catch (SQLException e) {
            throw new RuntimeException("Error al registrar al usuario", e);
        }

	}

	@Override
	public void actualizarUsuario(int id, Usuario usr) {
	    GestorConexiones gc = new GestorConexiones();
		String sql = "UPDATE usuarios SET username = ?, password = ? WHERE id = ?;";

		try (Connection conn = gc.obtenerConexion();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usr.getUsername());
            stmt.setString(2, BCrypt.hashpw(usr.getPassword(), BCrypt.gensalt(10)));
            stmt.setInt(3, id);

            stmt.executeUpdate();
        }
        catch (SQLException e) {
            throw new RuntimeException("Error al registrar al usuario", e);
        }
	}

	@Override
	public void borrarUsuario(int id) {
        GestorConexiones gc = new GestorConexiones();
        String sql = "DELETE FROM usuarios WHERE id = ?;";

        try (Connection conn = gc.obtenerConexion();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            stmt.executeUpdate();
        }
        catch (SQLException e) {
            throw new RuntimeException("Error al registrar al usuario", e);
        }
	}


}
