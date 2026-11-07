package co.edu.unilibre.servicios;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import co.edu.unilibre.dto.Casilla;
import co.edu.unilibre.dto.Dificultad;
import co.edu.unilibre.dto.Partida;
import co.edu.unilibre.dto.Resultado;
import co.edu.unilibre.dto.Tema;
import co.edu.unilibre.dto.Usuario;

/**
 * TestJugabilidad
 */
@TestInstance (TestInstance.Lifecycle.PER_CLASS)
public class TestJugabilidad {

    private Juego juego;
    private Partida partida;
    private Usuario usuario;
    private Tema tema;
    private Dificultad dificultad;

    @BeforeEach
    void setUp() {
        // Tablero de 5x5 con 3 minas para pruebas controladas
        dificultad = new Dificultad("Prueba", 3, 5, 5);
        usuario = new Usuario(1, "Pablo");
        tema = new Tema("Tema test", "", "", "", "", "");

        partida = new Partida(dificultad, usuario, tema);

        juego = new JuegoImp(partida);
    }

    // ==========================================
    // 1. CASOS DE ÉXITO (HAPPY PATHS)
    // ==========================================

    @Test
    @DisplayName("Éxito (RF-01): El primer clic debe generar zona segura sin minas")
    public void testIniciarJuegoZonaSegura() {
        Casilla primerClic = partida.getTablero()[2][2];

        assertDoesNotThrow(() -> juego.iniciarJuego(primerClic));

        // La casilla seleccionada y sus 8 vecinas no deben ser minas
        for (int i = 1; i <= 3; i++) {
            for (int j = 1; j <= 3; j++) {
                assertFalse(partida.getTablero()[i][j].isMina(), "La zona alrededor del primer clic debe ser segura");
            }
        }
        assertTrue(primerClic.isDescubierta(), "La casilla inicial debe quedar descubierta");
    }

    @Test
    @DisplayName("Éxito (RF-03): Alternar bandera incrementa/decrementa las banderas restantes")
    public void testAlternarBanderaExito() {
        Casilla casilla = partida.getTablero()[0][0];

        juego.alternarBandera(casilla);
        assertTrue(casilla.isBandera(), "La casilla debe tener bandera");

        juego.alternarBandera(casilla);
        assertFalse(casilla.isBandera(), "Al alternar de nuevo, la bandera debe quitarse");
    }

    @Test
    @DisplayName("Éxito (RF-02): Al hacer clic en mina la partida debe terminar en PERDEDOR")
    public void testDescubrirMinaPerdedor() {
        Casilla casillaMina = partida.getTablero()[4][4];
        casillaMina.setMina(true);

        juego.descubrirCasilla(casillaMina);

        assertEquals(Resultado.PERDEDOR, partida.getResultado(), "El estado de la partida debe ser PERDEDOR");
        assertTrue(casillaMina.isDescubierta());
    }

    @Test
    @DisplayName("Éxito (RF-04): Chording debe despejar casillas adyacentes si las banderas coinciden")
    public void testHacerChordingExito() {
        // Configuramos casilla central con 1 mina adyacente en (0,1)
        Casilla centro = partida.getTablero()[1][1];
        centro.setDescubierta(true);
        centro.setMinasAdyacentes(1);

        Casilla minaAdyacente = partida.getTablero()[0][1];
        minaAdyacente.setMina(true);

        // Colocamos bandera en la mina
        juego.alternarBandera(minaAdyacente);

        // Ejecutamos Chording sobre el centro
        juego.hacerChording(centro);

        // Las demás vecinas seguras deben haberse descubierto
        assertTrue(partida.getTablero()[0][0].isDescubierta());
        assertTrue(partida.getTablero()[1][0].isDescubierta());
    }

    // ==========================================
    // 2. CASOS DE FALLO Y EXCEPCIONES (FAIL PATHS)
    // ==========================================

    @Test
    @DisplayName("Fallo (RF-01): El numero de minas no puede ser mayor al numero de casillas -9")
    public void failDificultadImposible() {
        dificultad = new Dificultad("Imposible", 99, 10, 10);
        partida = new Partida(dificultad, usuario, tema);
        juego = new JuegoImp(partida);

        Casilla primerClic = partida.getTablero()[2][2];

        assertThrows(RuntimeException.class, () -> juego.iniciarJuego(primerClic));
    }

    @Test
    @DisplayName("Fallo: Debe lanzar excepción si las coordenadas de la casilla están fuera de límites")
    public void failCoordenadasFueraDeLimites() {
        Casilla casillaInvalida = new Casilla(10, 10); // Tablero es de 5x5

        assertThrows(RuntimeException.class, () -> {
            juego.descubrirCasilla(casillaInvalida);
        }, "Debe lanzar RuntimeException si la casilla sobrepasa las filas/columnas del tablero");
    }

    @Test
    @DisplayName("Fallo: Chording con banderas mal puestas debe hacer perder al jugador al revelar una mina")
    public void failChordingConBanderaErronea() {
        Casilla centro = partida.getTablero()[1][1];
        centro.setDescubierta(true);
        centro.setMinasAdyacentes(1);

        // Ponemos mina real en (0,0) pero colocamos la bandera en (0,1) equivocadamente
        partida.getTablero()[0][0].setMina(true);
        juego.alternarBandera(partida.getTablero()[0][1]); // Bandera mal puesta

        juego.hacerChording(centro);

        // Como la bandera estaba mal puesta, el chording descubre (0,0) que tenía mina y pierde
        assertEquals(Resultado.PERDEDOR, partida.getResultado(), "Chording mal ejecutado sobre mina debe causar derrota");
    }
}
