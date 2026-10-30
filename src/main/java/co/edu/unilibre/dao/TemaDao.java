package co.edu.unilibre.dao;

import java.util.List;

import co.edu.unilibre.dto.Tema;

/**
 * TemaDao
 */
public interface TemaDao {

	public void crearTema(Tema tema);
	public List<Tema> listarTemas();
	public void actualizarTema(int id, Tema tema);
	public void borrarTema(int id);

}
