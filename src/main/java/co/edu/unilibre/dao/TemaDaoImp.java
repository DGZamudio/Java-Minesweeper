package co.edu.unilibre.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import co.edu.unilibre.dto.Tema;

/**
 * TemaDaoImp
 */
public class TemaDaoImp implements TemaDao {

	@Override
	public void crearTema(Tema tema) {
	    GestorConexiones gc = new GestorConexiones();
		String sql = "INSERT INTO temas (nombre, bg, fg, mina, bandera, bgImage) VALUES (?, ?, ?, ?, ?, ?);";

		try (Connection conn = gc.obtenerConexion();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, tema.getNombre());
            stmt.setString(2, tema.getBg());
            stmt.setString(3, tema.getFg());
            stmt.setString(4, tema.getMina());
            stmt.setString(5, tema.getBandera());
            stmt.setString(6, tema.getBgImage());

            stmt.executeUpdate();
        } catch (SQLException e) {
		    throw new RuntimeException("Error al guardar la partida", e);
		}
	}

	@Override
	public List<Tema> listarTemas() {
    	GestorConexiones gc = new GestorConexiones();
        List<Tema> temas = new ArrayList<>();
        String sql = "SELECT * " +
                "FROM temas;";

        try (Connection conn = gc.obtenerConexion();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet res = stmt.executeQuery()) {

            while (res.next()) {
                int id = res.getInt("id");
                String nombre = res.getString("nombre");
                String bg = res.getString("bg");
                String fg = res.getString("fg");
                String mina = res.getString("mina");
                String bandera = res.getString("bandera");
                String bgImage = res.getString("bgImage");
                Tema t = new Tema(nombre, bg, fg, mina, bandera, bgImage);
                t.setId(id);
                temas.add(t);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al consultar las partidas en la base de datos", e);
        }

		return temas;
	}

	@Override
	public void actualizarTema(int id, Tema tema) {
        GestorConexiones gc = new GestorConexiones();
        String sql = "UPDATE temas SET nombre = ?, bg = ?, fg = ?, mina = ?, bandera = ?, bg_image = ? WHERE id = ?;";

        try (Connection conn = gc.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, tema.getNombre());
            stmt.setString(2, tema.getBg());
            stmt.setString(3, tema.getFg());
            stmt.setString(4, tema.getMina());
            stmt.setString(5, tema.getBandera());
            stmt.setString(6, tema.getBgImage());
            stmt.setInt(7, id);

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar el tema", e);
        }
	}

	@Override
	public void borrarTema(int id) {
    	GestorConexiones gc = new GestorConexiones();
        String sql = "DELETE FROM temas WHERE id = ?;";

        try (Connection conn = gc.obtenerConexion();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            stmt.executeUpdate();
        } catch (SQLException e) {
		    throw new RuntimeException("Error al eliminar el tema", e);
		}
	}


}
