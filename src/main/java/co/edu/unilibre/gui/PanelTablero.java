package co.edu.unilibre.gui;

import co.edu.unilibre.dto.Casilla;
import co.edu.unilibre.dto.Partida;
import co.edu.unilibre.servicios.JuegoImp;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

/**
 * PanelTablero
 */
public class PanelTablero {

    // JavaFX
    private BorderPane vistaPrincipal;
    private GridPane gridTablero;
    private Button[][] botones;
    private Label lblTiempo;
    private Label lblMinasRestantes;

    // Servicios
    private JuegoImp juego;
    private Partida partidaActual;

    public PanelTablero() {
        this.partidaActual = new Partida(dificultad, usuario, tema);
        this.juego = new JuegoImp(this.partidaActual);
        this.vistaPrincipal = new BorderPane();

        inicializarEncabezado();
        construirTablero();
    }

    private void inicializarEncabezado() {
        HBox topBar = new HBox(20);
        lblTiempo = new Label("Tiempo: 00:00");
        lblMinasRestantes = new Label("Minas: " + partidaActual.getDificultad().getMinas());

        topBar.getChildren().addAll(lblTiempo, lblMinasRestantes);
        vistaPrincipal.setTop(topBar);
    }

    private void construirTablero() {
        gridTablero = new GridPane();
        int filas = partidaActual.getDificultad().getFilas();
        int columnas = partidaActual.getDificultad().getColumnas();

        botones = new Button[filas][columnas];

        for (int r = 0; r < filas; r++) {
            for (int c = 0; c < columnas; c++) {
                Button btn = new Button();
                btn.setPrefSize(35, 35); // Tamaño del botón en píxeles

                final int fila = r;
                final int col = c;

                // Capturar clics de ratón
                btn.setOnMouseClicked((MouseEvent event) -> {
                    Casilla casilla = partidaActual.getTablero()[fila][col];

                    if (event.getButton() == MouseButton.PRIMARY) {
                        // CLIC IZQUIERDO: Descubrir casilla
                        juego.descubrirCasilla(casilla);
                    } else if (event.getButton() == MouseButton.SECONDARY) {
                        // CLIC DERECHO: Poner/Quitar Bandera
                        juego.alternarBandera(casilla);
                    } else if (event.getButton() == MouseButton.MIDDLE) {
                        // CLIC CENTRAL / SCROLL: Hacer Chording
                        juego.hacerChording(casilla);
                    }

                    // Después de cualquier acción, refrescamos el pintado de la matriz
                    renderizarTablero();
                });

                botones[r][c] = btn;
                gridTablero.add(btn, c, r); // GridPane añade en formato (columna, fila)
            }
        }

        vistaPrincipal.setCenter(gridTablero);
    }

    // Método para sincronizar la GUI con el estado de tu matriz en Java
    public void renderizarTablero() {
        int filas = partidaActual.getDificultad().getFilas();
        int columnas = partidaActual.getDificultad().getColumnas();

        for (int r = 0; r < filas; r++) {
            for (int c = 0; c < columnas; c++) {
                Casilla casilla = partidaActual.getTablero()[r][c];
                Button btn = botones[r][c];

                if (casilla.isDescubierta()) {
                    btn.setDisable(true); // Deshabilitar para que cambie el aspecto visual

                    if (casilla.isMina()) {
                        btn.setText("💣");
                        btn.setStyle("-fx-background-color: #ff4d4d;");
                    } else if (casilla.getMinasAdyacentes() > 0) {
                        btn.setText(String.valueOf(casilla.getMinasAdyacentes()));
                    } else {
                        btn.setText(""); // Espacio en blanco (0 minas)
                    }
                } else if (casilla.isBandera()) {
                    btn.setText("🚩");
                } else {
                    btn.setText("");
                    btn.setDisable(false);
                }
            }
        }
    }

    public BorderPane getVista() {
        return vistaPrincipal;
    }
}
