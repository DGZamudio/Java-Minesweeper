package co.edu.unilibre.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import co.edu.unilibre.dto.Dificultad;
import co.edu.unilibre.dto.Partida;
import co.edu.unilibre.dto.Resultado;
import co.edu.unilibre.dto.Usuario;

/**
 * PartidaDaoImp
 */
public class PartidaDaoImp implements PartidaDao {

	@Override
	public void guardarPartida(Partida partida) {
	    GestorConexiones gc = new GestorConexiones();
		String sql = "INSERT INTO partidas (usuario_id, dificultad_id, tiempo, resultado) VALUES (?, ?, ?, ?);";

		try (Connection conn = gc.obtenerConexion();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, partida.getUsuario().getId());
            stmt.setInt(2, partida.getDificultad().getId());
            stmt.setLong(3, partida.getTiempo().toMillis());
            stmt.setString(4, partida.getResultado().name());

            stmt.executeUpdate();
        } catch (SQLException e) {
		    throw new RuntimeException("Error al guardar la partida", e);
		}
	}

	@Override
	public List<Partida> obtenerPartidas() {
	    GestorConexiones gc = new GestorConexiones();
		List<Partida> partidas = new ArrayList<>();
		String sql = "SELECT p.id, p.resultado, p.tiempo, " +
                 "u.id AS id_usr, u.username, " +
                 "d.id AS df_id, d.nombre, d.minas, d.columnas, d.filas " +
                 "FROM partidas p " +
                 "JOIN usuarios u ON u.id = p.usuario_id " +
                 "JOIN dificultades d ON d.id = p.dificultad_id;";

		try (Connection conn = gc.obtenerConexion();
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet res = stmt.executeQuery()) {

        while (res.next()) {
            int id = res.getInt("id");
            String r = res.getString("resultado");
            Resultado resultado = Resultado.valueOf(r);

            int usrId = res.getInt("id_usr");
            String nombreU = res.getString("username");
            Usuario u = new Usuario(nombreU);
            u.setId(usrId);

            int dfId = res.getInt("df_id");
            String nombreD = res.getString("nombre");
            int minas = res.getInt("minas");
            int columnas = res.getInt("columnas");
            int filas = res.getInt("filas");
            Dificultad d = new Dificultad(nombreD, minas, columnas, filas);
            d.setId(dfId);

            long tiempoMs = res.getLong("tiempo");
            Duration tiempo = Duration.ofMillis(tiempoMs);

            Partida p = new Partida(d, u, null, tiempo, resultado);
            p.setId(id);
            partidas.add(p);
        }

        } catch (SQLException e) {
            throw new RuntimeException("Error al consultar las partidas en la base de datos", e);
        }

		return partidas;
	}

	@Override
	public void borrarPartida(int id) {
	    GestorConexiones gc = new GestorConexiones();
		String sql = "DELETE FROM partidas WHERE id = ?;";

		try (Connection conn = gc.obtenerConexion();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            stmt.executeUpdate();
        } catch (SQLException e) {
		    throw new RuntimeException("Error al eliminar la partida", e);
		}
	}


}
