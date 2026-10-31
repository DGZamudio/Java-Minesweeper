package co.edu.unilibre.servicios;

import java.time.Duration;
import java.time.Instant;

import co.edu.unilibre.dto.Casilla;
import co.edu.unilibre.dto.Partida;
import co.edu.unilibre.dto.Resultado;

/**
 * JuegoImp
 */
public class JuegoImp implements Juego {

    private Instant tiempoInicio;
    private Partida partida;
    private int minasRestantes;
    private int banderasRestantes;


    public JuegoImp(Partida partida) {
   	    this.tiempoInicio = null;
		this.partida = partida;
		this.minasRestantes = partida.getDificultad().getMinas();
		this.banderasRestantes = partida.getDificultad().getMinas();
    }

    private int obtenerAdyacentes(Casilla casilla) {
	    Casilla[][] tablero = partida.getTablero();
		int columnas = partida.getDificultad().getColumnas();
		int filas = partida.getDificultad().getFilas();
	    int x = casilla.getX();
		int y = casilla.getY();

		int adyacentes = 0;

		int[][] coordenadas = {{x,y+1}, {x,y-1}, {x+1, y}, {x-1, y}, {x+1, y+1}, {x-1, y+1}, {x-1, y-1}, {x+1, y-1}};

		for (int i = 0; i < coordenadas.length; i++) {
            if (coordenadas[i][0] < 0 || coordenadas[i][0] > filas || coordenadas[i][1] < 0 || coordenadas[i][1] > columnas) {
                continue;
            } else {
                if (tablero[coordenadas[i][0]][coordenadas[i][1]].isMina()) {
                    adyacentes += 1;
                }
            }
		}

		return adyacentes;
	}

	private void esparcirCasillasVacias(Casilla casilla) {
	    if (!casilla.isMina()) {
			casilla.setDescubierta(true);
               Casilla[][] tablero = partida.getTablero();
               int columnas = partida.getDificultad().getColumnas();
               int filas = partida.getDificultad().getFilas();
               int x = casilla.getX();
               int y = casilla.getY();

			int[][] coordenadas = {{x,y+1}, {x,y-1}, {x+1, y}, {x-1, y}, {x+1, y+1}, {x-1, y+1}, {x-1, y-1}, {x+1, y-1}};

			for (int i = 0; i < coordenadas.length; i++) {
                if (coordenadas[i][0] < 0 || coordenadas[i][0] > filas || coordenadas[i][1] < 0 || coordenadas[i][1] > columnas) {
                    continue;
                } else {
                    if (!tablero[coordenadas[i][0]][coordenadas[i][1]].isMina()) {
                        if (obtenerAdyacentes(tablero[coordenadas[i][0]][coordenadas[i][1]]) == 0) {
                            esparcirCasillasVacias(casilla);
                        }
                    }
                }
			}
		}
	}

	private void verificarPartida() {
	    if (minasRestantes == 0) {
			terminarJuego(Resultado.GANADOR);
		}
	}

	public int getBanderasRestantes() {
	    return this.banderasRestantes;
	}

	public Duration getTiempoActual() {
	    return Duration.between(this.tiempoInicio, Instant.now());
	}

	@Override
	public void iniciarJuego(Casilla casilla) {

	}

	@Override
	public void terminarJuego(Resultado resultado) {
	    partida.setResultado(resultado);
		partida.setTiempo(Duration.between(this.tiempoInicio, Instant.now()));
	}

	@Override
	public void descubrirCasilla(Casilla casilla) {
	    if (casilla.isMina()) {
			terminarJuego(Resultado.PERDEDOR);
		}

		int adyacentes = obtenerAdyacentes(casilla);

		casilla.setMinasAdyacentes(adyacentes);
		casilla.setDescubierta(true);

		if (adyacentes == 0) {
		    esparcirCasillasVacias(casilla);
		}

		verificarPartida();
	}

	@Override
	public void hacerChording(Casilla casilla) {
	    Casilla[][] tablero = partida.getTablero();
	    int x = casilla.getX();
		int y = casilla.getY();
		int filas = partida.getDificultad().getFilas();
		int columnas = partida.getDificultad().getColumnas();
	    int adyacentes = casilla.getMinasAdyacentes();
		int banderas = 0;

		int[][] coordenadas = {{x,y+1}, {x,y-1}, {x+1, y}, {x-1, y}, {x+1, y+1}, {x-1, y+1}, {x-1, y-1}, {x+1, y-1}};
		for (int i = 0; i < coordenadas.length; i++) {
            if (coordenadas[i][0] < 0 || coordenadas[i][0] > filas || coordenadas[i][1] < 0 || coordenadas[i][1] > columnas) {
                continue;
            } else {
                if (tablero[coordenadas[i][0]][coordenadas[i][1]].isBandera()) {
                    banderas++;
                }
            }
		}

		if (adyacentes == banderas) {
            for (int i = 0; i < coordenadas.length; i++) {
                if (coordenadas[i][0] < 0 || coordenadas[i][0] > filas || coordenadas[i][1] < 0 || coordenadas[i][1] > columnas) {
                    continue;
                } else {
                    descubrirCasilla(tablero[coordenadas[i][0]][coordenadas[i][1]]);
                }
    		}
		}
	}

	@Override
	public void alternarBandera(Casilla casilla) {
	    if (!casilla.isDescubierta()) {
			casilla.setBandera(!casilla.isBandera());
			this.banderasRestantes --;
			if (casilla.isMina()) {
			    this.minasRestantes --;
			}
		}
	}

}
