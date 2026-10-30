package co.edu.unilibre.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Tema
 */
@Getter
@Setter
public class Tema {

    private int id;
    private String nombre;
	private String bg;
	private String fg;
	private String mina;
	private String bandera;
	private String bgImage;

	public Tema(String nombre, String bg, String fg, String mina, String bandera, String bgImage) {
	    this.nombre = nombre;
		this.bg = bg;
		this.fg = fg;
		this.mina = mina;
		this.bandera = bandera;
		this.bgImage = bgImage;
	}

}
