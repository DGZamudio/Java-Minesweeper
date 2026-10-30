package co.edu.unilibre.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Usuario
 */
@Getter
@Setter
public class Usuario {

    private int id;
	private String username;
	private String password;

	public Usuario(String username) {
	    this.username = username;
	}

}
