package co.edu.unilibre.servicios;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import co.edu.unilibre.dto.Casilla;
import co.edu.unilibre.dto.Dificultad;
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
            if (coordenadas[i][0] <= 0 || coordenadas[i][0] >= filas || coordenadas[i][1] <= 0 || coordenadas[i][1] >= columnas) {
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
	    if (casilla.isDescubierta()) return;
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
                            esparcirCasillasVacias(tablero[coordenadas[i][0]][coordenadas[i][1]]);
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

	private void verificarCoordenadasValidas(Dificultad dificultad, Casilla casilla) {
	    if (casilla.getX() > dificultad.getFilas() - 1 || casilla.getX() < 0 || casilla.getY() > dificultad.getColumnas() - 1 || casilla.getY() < 0) {
			throw new RuntimeException("Casilla fuera de los limites");
		}
	}

	private void verificarTableroValido(Dificultad dificultad) {
	    int minasMaximas = dificultad.getFilas() * dificultad.getColumnas() - 9;
	    if (dificultad.getMinas() > minasMaximas) {
			throw new RuntimeException("Tablero no valido, mas minas que casillas");
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
	    Dificultad dificultadActual = partida.getDificultad();
	    verificarTableroValido(dificultadActual);

        int x = casilla.getX();
        int y = casilla.getY();
        int filas = dificultadActual.getFilas();
        int columnas = dificultadActual.getColumnas();
        Casilla[][] tablero = partida.getTablero();

        List<int[]> posiciones = new ArrayList<>();

        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {

                // Si la distancia horizontal y vertical es mayor a 1, NO es la casilla ni una de sus 8 vecinas
                boolean esZonaSegura = Math.abs(i - x) <= 1 && Math.abs(j - y) <= 1;

                if (!esZonaSegura) {
                    posiciones.add(new int[]{i, j});
                }
            }
        }

        // Barajar la lista de posiciones
        Collections.shuffle(posiciones);

        // Posicionar minas
        for (int i = 0; i < dificultadActual.getMinas(); i++) {
            int[] localPos = posiciones.get(i);
            tablero[localPos[0]][localPos[1]].setMina(true);
        }

        // Iniciar descubrimiento
        descubrirCasilla(casilla);
	}

	@Override
	public void terminarJuego(Resultado resultado) {
	    partida.setResultado(resultado);
		partida.setTiempo(Duration.between(this.tiempoInicio, Instant.now()));
	}

	@Override
	public void descubrirCasilla(Casilla casilla) {
	    verificarCoordenadasValidas(this.partida.getDificultad(), casilla);

		if (this.tiempoInicio == null) {
		    tiempoInicio = Instant.now();
		}

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
	    verificarCoordenadasValidas(this.partida.getDificultad(), casilla);

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
	    verificarCoordenadasValidas(this.partida.getDificultad(), casilla);

	    if (!casilla.isDescubierta()) {
			casilla.setBandera(!casilla.isBandera());

			if (casilla.isBandera()) {
			    this.banderasRestantes++;
				if (casilla.isMina()) {
				    this.minasRestantes++;
				}
			} else {
			    this.banderasRestantes--;
				if (casilla.isMina()) {
				    this.minasRestantes--;
				}
			}
		}
	}

}
