package co.edu.unilibre.dao;

import java.util.List;

import co.edu.unilibre.dto.Partida;

/**
 * PartidaDao
 */
public interface PartidaDao {

	public void guardarPartida(Partida partida);
	public List<Partida> obtenerPartidas();
	public void borrarPartida(int id);

}
