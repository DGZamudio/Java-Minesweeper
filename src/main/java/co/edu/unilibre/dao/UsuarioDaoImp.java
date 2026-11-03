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
		String sql = "INSERT INTO usuarios (username, password_hash) VALUES (?, ?);";

		try (Connection conn = GestorConexiones.obtenerConexion();
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
		String sql = "SELECT * FROM usuarios WHERE username = ?;";

		try (Connection conn = GestorConexiones.obtenerConexion();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet res = stmt.executeQuery();

            if (res.next()) {
                int id = res.getInt("id");
                String passHash = res.getString("password_hash");

                if (BCrypt.checkpw(password, passHash)) {
                    Usuario usr = new Usuario(username);
                    usr.setId(id);
                    return usr;
                }
            }
            return null;
        }
        catch (SQLException e) {
            throw new RuntimeException("Error al autenticar al usuario", e);
        }

	}

	@Override
	public void actualizarUsuario(int id, Usuario usr) {
		String sql = "UPDATE usuarios SET username = ?, password_hash = ? WHERE id = ?;";

		try (Connection conn = GestorConexiones.obtenerConexion();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usr.getUsername());
            stmt.setString(2, BCrypt.hashpw(usr.getPassword(), BCrypt.gensalt(10)));
            stmt.setInt(3, id);

            stmt.executeUpdate();
        }
        catch (SQLException e) {
            throw new RuntimeException("Error al actualizar al usuario", e);
        }
	}

	@Override
	public void borrarUsuario(int id) {
        String sql = "DELETE FROM usuarios WHERE id = ?;";

        try (Connection conn = GestorConexiones.obtenerConexion();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            stmt.executeUpdate();
        }
        catch (SQLException e) {
            throw new RuntimeException("Error al borrar al usuario", e);
        }
	}


}
