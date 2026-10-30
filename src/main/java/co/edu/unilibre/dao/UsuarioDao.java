package co.edu.unilibre.dao;

import co.edu.unilibre.dto.Usuario;

/**
 * UsuarioDao
 */
public interface UsuarioDao {

	public void registrarUsuario(Usuario usr);
	public void autenticarUsuario(String username, String password);
	public void actualizarUsuario(int id, Usuario usr);
	public void borrarUsuario(int id);

}
