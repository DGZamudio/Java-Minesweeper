package co.edu.unilibre.dto;

import lombok.Getter;

/**
 * Usuario
 */
@Getter
public class Usuario {

    private int id;
	private String username;
	private String password;

	public Usuario(int id, String username) {
	    this.id = id;
	    this.username = username;
	}

	public Usuario(String username, String password) {
	    this.username = username;
		this.password = password;
	}

}
