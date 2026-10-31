package co.edu.unilibre.dao;

import java.util.List;

import co.edu.unilibre.dto.Dificultad;

/**
 * DificultadDao
 */
public interface DificultadDao {

	public void guardarDificultad(Dificultad dificultad);
	public List<Dificultad> listarDificultades();
	public void actualizarDificultad(int id, Dificultad dificultad);
	public void borrarDificultad(int id);

}
