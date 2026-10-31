package co.edu.unilibre.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import co.edu.unilibre.dto.Dificultad;

/**
 * DificultadDaoImp
 */
public class DificultadDaoImp implements DificultadDao {

	@Override
	public void guardarDificultad(Dificultad dificultad) {
	    GestorConexiones gc = new GestorConexiones();
		String sql = "INSERT INTO (nombre, minas, filas, columnas) dificultades VALUES (?, ?, ?, ?);";

		try (Connection conn = gc.obtenerConexion();
		PreparedStatement stmt = conn.prepareStatement(sql)) {
		    stmt.setString(1, dificultad.getNombre());
			stmt.setInt(2, dificultad.getMinas());
			stmt.setInt(3, dificultad.getFilas());
			stmt.setInt(4, dificultad.getColumnas());

            stmt.executeUpdate();
		} catch (SQLException e) {
		    throw new RuntimeException("Error al guardar la dificultad", e);
		}
	}

	@Override
	public List<Dificultad> listarDificultades() {
	    GestorConexiones gc = new GestorConexiones();
		String sql = "SELECT * FROM dificultades;";
		List<Dificultad> dificultades = new ArrayList<>();

		try (Connection conn = gc.obtenerConexion();
		PreparedStatement stmt = conn.prepareStatement(sql);
		ResultSet res = stmt.executeQuery()) {
		    while (res.next()) {
				int id = res.getInt("id");
				String nombre = res.getString("nombre");
				int minas = res.getInt("minas");
				int filas = res.getInt("filas");
				int columnas = res.getInt("columnas");

				Dificultad dif = new Dificultad(nombre, minas, columnas, filas);
				dif.setId(id);

				dificultades.add(dif);
			}
		} catch (SQLException e) {
		    throw new RuntimeException("Error al listar las dificultades", e);
		}

		return dificultades;
	}

	@Override
	public void actualizarDificultad(int id, Dificultad dificultad) {
	    GestorConexiones gc = new GestorConexiones();
		String sql = "UPDATE dificultades SET nombre = ?, minas = ?, columnas = ?, filas = ? WHERE id = ?;";

		try (Connection conn = gc.obtenerConexion();
		PreparedStatement stmt = conn.prepareStatement(sql)) {
		    stmt.setString(1, dificultad.getNombre());
			stmt.setInt(2, dificultad.getMinas());
			stmt.setInt(3, dificultad.getColumnas());
			stmt.setInt(4, dificultad.getFilas());
			stmt.setInt(5, id);

			stmt.executeUpdate();
		} catch (SQLException e) {
		    throw new RuntimeException("Error al actualiar dificultad", e);
		}
	}

	@Override
	public void borrarDificultad(int id) {
        GestorConexiones gc = new GestorConexiones();
        String sql = "DELETE FROM usuarios WHERE id = ?;";

        try (Connection conn = gc.obtenerConexion();
        PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al borrar la dificultad", e);
        }
	}


}
