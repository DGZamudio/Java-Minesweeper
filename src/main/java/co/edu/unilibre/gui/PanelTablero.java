package co.edu.unilibre.gui;

import co.edu.unilibre.dto.Casilla;
import co.edu.unilibre.dto.Dificultad;
import co.edu.unilibre.dto.Partida;
import co.edu.unilibre.dto.Resultado;
import co.edu.unilibre.dto.Tema;
import co.edu.unilibre.dto.Usuario;
import co.edu.unilibre.servicios.JuegoImp;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.util.Duration;

/**
 * PanelTablero
 */
public class PanelTablero extends VBox {

    // Callbacks
    private Runnable onReiniciar;
    private Runnable onNuevaPartida;

    // JavaFX
    private GridPane tablero;
    private HBox topBar;
    private Button[][] casillas;
    private Label lblTiempo;
    private Label lblBanderasRestantes;
    private Image imgBandera;
    private Image imgMina;

    // Servicios
    private JuegoImp juego;

    // Estados
    private Partida partida;
    private boolean isPrimeraMina;
    private int segundosTranscurridos = 0;
    private Timeline temporizador;

    public PanelTablero(Dificultad dificultad, Usuario usuario, Tema tema) {
        this.partida = new Partida(dificultad, usuario, tema);
        this.juego = new JuegoImp(this.partida);
        this.isPrimeraMina = true;

        // Cargar imagenes
        java.io.InputStream stream = getClass().getResourceAsStream("/images/" + tema.getMina());

        if (stream == null) {
            imgMina = null;
        } else {
            imgMina = new Image(stream);
        }

        stream = getClass().getResourceAsStream("/images/" + tema.getBandera());

        if (stream == null) {
            imgBandera = null;
        } else {
            imgBandera = new Image(stream);
        }

        getStylesheets().add(getClass().getResource("/styles/general.css").toExternalForm());
        getStylesheets().add(getClass().getResource("/styles/game.css").toExternalForm());

        setSpacing(0);
        setAlignment(Pos.TOP_CENTER);
        getStyleClass().add("main-background");

        // ==========================================
        // 1. ENCABEZADO SUPERIOR (Master Header)
        // ==========================================
        HBox masterHeader = new HBox();
        masterHeader.setAlignment(Pos.CENTER);
        masterHeader.setPadding(new Insets(20, 30, 20, 30));

        // Izquierda: Logo
        HBox logoBox = new HBox(6);
        logoBox.setAlignment(Pos.CENTER_LEFT);
        Label lblIconLogo = new Label("⎈");
        lblIconLogo.getStyleClass().add("logo-icon");
        Label lblLogo1 = new Label("Busca");
        lblLogo1.getStyleClass().add("logo-text-bold");
        Label lblLogo2 = new Label("Minas");
        lblLogo2.getStyleClass().add("logo-text-light");
        logoBox.getChildren().addAll(lblIconLogo, lblLogo1, lblLogo2);

        Region spacerLeft = new Region();
        HBox.setHgrow(spacerLeft, Priority.ALWAYS);

        // Centro: Títulos de la partida
        VBox titleBox = new VBox(4);
        titleBox.setAlignment(Pos.CENTER);
        Label lblEstado = new Label("PARTIDA EN CURSO");
        lblEstado.getStyleClass().add("header-subtitle");
        Label lblNombreCampo = new Label("Campo " + dificultad.getNombre().toLowerCase());
        lblNombreCampo.getStyleClass().add("header-title");
        Label lblMeta = new Label(dificultad.getNombre() + " · " + dificultad.getFilas() + " × " + dificultad.getColumnas() + " casillas");
        lblMeta.getStyleClass().add("header-meta");
        titleBox.getChildren().addAll(lblEstado, lblNombreCampo, lblMeta);

        Region spacerRight = new Region();
        HBox.setHgrow(spacerRight, Priority.ALWAYS);

        // Derecha: Botones de acción
        HBox actionBox = new HBox(12);
        actionBox.setAlignment(Pos.CENTER_RIGHT);
        Button btnReiniciar = new Button("Reiniciar");
        btnReiniciar.getStyleClass().add("btn-secondary-outline");
        btnReiniciar.setOnAction(event -> {
            if (onReiniciar != null) {
                onReiniciar.run();
            }
        });
        Button btnNuevaPartida = new Button("Nueva partida");
        btnNuevaPartida.getStyleClass().add("btn-primary-solid");
        btnNuevaPartida.setOnAction(event -> {
            if (onNuevaPartida != null) {
                onNuevaPartida.run();
            }
        });
        actionBox.getChildren().addAll(btnReiniciar, btnNuevaPartida);

        masterHeader.getChildren().addAll(logoBox, spacerLeft, titleBox, spacerRight, actionBox);

        // Línea divisoria principal
        Region mainDivider = new Region();
        mainDivider.getStyleClass().add("main-divider");

        // ==========================================
        // 2. ÁREA DE CONTENIDO (Tablero + Sidebar)
        // ==========================================
        HBox contentArea = new HBox(20);
        contentArea.setAlignment(Pos.TOP_CENTER);
        contentArea.setPadding(new Insets(24, 30, 30, 30));

        // --- TARJETA IZQUIERDA (JUEGO) ---
        VBox leftCard = new VBox();
        leftCard.getStyleClass().add("game-card");
        HBox.setHgrow(leftCard, Priority.ALWAYS); // Para que ocupe el espacio disponible

        inicializarEncabezado(); // Inicializa topBar (Barra azul navy)
        crearTablero(dificultad.getFilas(), dificultad.getColumnas());

        // Contenedor interno para darle padding al tablero dentro de la tarjeta blanca
        VBox tableroWrapper = new VBox(tablero);
        tableroWrapper.setAlignment(Pos.CENTER);
        tableroWrapper.setPadding(new Insets(40));
        leftCard.getChildren().addAll(topBar, tableroWrapper);

        // --- SIDEBAR DERECHA ---
        VBox sideBar = new VBox(16);
        sideBar.getStyleClass().add("sidebar-container");

        // ==========================================
        // 1. TARJETA: DETALLES
        // ==========================================
        VBox cardDetalles = new VBox(10);
        cardDetalles.getStyleClass().add("sidebar-card");

        Label tituloDetalles = new Label("DETALLES");
        tituloDetalles.getStyleClass().add("sidebar-title-uppercase");

        Region line1 = new Region();
        line1.getStyleClass().add("sidebar-divider");

        // Fila: Dificultad
        HBox rowDificultad = new HBox();
        rowDificultad.setAlignment(Pos.CENTER_LEFT);
        Label lblDificultadTitulo = new Label("Dificultad");
        lblDificultadTitulo.getStyleClass().add("sidebar-label");
        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);
        Label lblDificultadVal = new Label(dificultad.getNombre());
        lblDificultadVal.getStyleClass().add("sidebar-value-bold");
        rowDificultad.getChildren().addAll(lblDificultadTitulo, spacer1, lblDificultadVal);

        Region line2 = new Region();
        line2.getStyleClass().add("sidebar-divider");

        // Fila: Tablero
        HBox rowTablero = new HBox();
        rowTablero.setAlignment(Pos.CENTER_LEFT);
        Label lblTableroTitulo = new Label("Tablero");
        lblTableroTitulo.getStyleClass().add("sidebar-label");
        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);
        Label lblTableroVal = new Label(dificultad.getFilas() + " × " + dificultad.getColumnas());
        lblTableroVal.getStyleClass().add("sidebar-value-bold");
        rowTablero.getChildren().addAll(lblTableroTitulo, spacer2, lblTableroVal);

        Region line3 = new Region();
        line3.getStyleClass().add("sidebar-divider");

        // Fila: Minas
        HBox rowMinas = new HBox();
        rowMinas.setAlignment(Pos.CENTER_LEFT);
        Label lblMinasTitulo = new Label("Minas");
        lblMinasTitulo.getStyleClass().add("sidebar-label");
        Region spacer3 = new Region();
        HBox.setHgrow(spacer3, Priority.ALWAYS);
        Label lblMinasVal = new Label(String.valueOf(dificultad.getMinas()));
        lblMinasVal.getStyleClass().add("sidebar-value-bold");
        rowMinas.getChildren().addAll(lblMinasTitulo, spacer3, lblMinasVal);

        cardDetalles.getChildren().addAll(
            tituloDetalles, line1,
            rowDificultad, line2,
            rowTablero, line3,
            rowMinas
        );

        // ==========================================
        // 2. TARJETA: CONTROLES
        // ==========================================
        VBox cardControles = new VBox(10);
        cardControles.getStyleClass().add("sidebar-card");

        Label tituloControles = new Label("CONTROLES");
        tituloControles.getStyleClass().add("sidebar-title-uppercase");

        VBox itemClicIzquierdo = new VBox(2);
        Label lblIzquierdoTitle = new Label("Clic izquierdo");
        lblIzquierdoTitle.getStyleClass().add("sidebar-control-title");
        Label lblIzquierdoDesc = new Label("Descubrir una casilla");
        lblIzquierdoDesc.getStyleClass().add("sidebar-control-desc");
        itemClicIzquierdo.getChildren().addAll(lblIzquierdoTitle, lblIzquierdoDesc);

        VBox itemClicRueda = new VBox(2);
        Label lblRuedaTitle = new Label("Clic rueda del raton");
        lblRuedaTitle.getStyleClass().add("sidebar-control-title");
        Label lblRuedaDesc = new Label("Abir todo un rango de casillas si las banderas estan completas");
        lblRuedaDesc.getStyleClass().add("sidebar-control-desc");
        itemClicRueda.getChildren().addAll(lblRuedaTitle, lblRuedaDesc);

        VBox itemClicDerecho = new VBox(2);
        Label lblDerechoTitle = new Label("Clic derecho");
        lblDerechoTitle.getStyleClass().add("sidebar-control-title");
        Label lblDerechoDesc = new Label("Colocar o quitar bandera");
        lblDerechoDesc.getStyleClass().add("sidebar-control-desc");
        itemClicDerecho.getChildren().addAll(lblDerechoTitle, lblDerechoDesc);

        cardControles.getChildren().addAll(tituloControles, itemClicIzquierdo, itemClicRueda, itemClicDerecho);

        // ==========================================
        // 3. TARJETA: TIP / HINT
        // ==========================================
        HBox cardTip = new HBox(8);
        cardTip.getStyleClass().add("sidebar-tip-card");
        cardTip.setAlignment(Pos.CENTER_LEFT);

        Label lblCheckIcon = new Label("✓");
        lblCheckIcon.getStyleClass().add("tip-icon");

        Label lblTipText = new Label("El primer clic nunca contiene una mina.");
        lblTipText.getStyleClass().add("tip-text");
        lblTipText.setWrapText(true);

        cardTip.getChildren().addAll(lblCheckIcon, lblTipText);

        // --- ENSAMBLAR SIDEBAR COMPLETO ---
        sideBar.getChildren().addAll(cardDetalles, cardControles, cardTip);

        contentArea.getChildren().addAll(leftCard, sideBar);

        // ENSAMBLE FINAL
        getChildren().addAll(masterHeader, mainDivider, contentArea);
        renderizarTablero();
    }

    public void setOnReiniciar(Runnable onReiniciar) {
        this.onReiniciar = onReiniciar;
    }

    public void setOnNuevaPartida(Runnable onNuevaPartida) {
        this.onNuevaPartida = onNuevaPartida;
    }

    private void inicializarEncabezado() {
        topBar = new HBox();
        topBar.getStyleClass().add("navy-top-bar");
        topBar.setAlignment(Pos.CENTER);

        // Izquierda
        Label lblMinasText = new Label("MINAS RESTANTES");
        lblMinasText.getStyleClass().add("navy-text-light");
        Region sp1 = new Region();
        HBox.setHgrow(sp1, Priority.ALWAYS);

        // Centro (Iconos y Banderas)
        HBox centerStats = new HBox(8);
        centerStats.setAlignment(Pos.CENTER);
        Label lblIconoCentro = new Label("💣"); // Puedes cambiarlo por ImageView
        lblBanderasRestantes = new Label(String.valueOf(juego.getBanderasRestantes()));
        lblBanderasRestantes.getStyleClass().add("navy-text-bold");
        centerStats.getChildren().addAll(lblIconoCentro, lblBanderasRestantes);

        Region sp2 = new Region();
        HBox.setHgrow(sp2, Priority.ALWAYS);

        // Derecha (Tiempo)
        HBox rightStats = new HBox(8);
        rightStats.setAlignment(Pos.CENTER);
        Label lblTiempoText = new Label("TIEMPO");
        lblTiempoText.getStyleClass().add("navy-text-light");
        lblTiempo = new Label("00:00");
        lblTiempo.getStyleClass().add("navy-text-bold");
        rightStats.getChildren().addAll(lblTiempoText, lblTiempo);

        topBar.getChildren().addAll(lblMinasText, sp1, centerStats, sp2, rightStats);
    }

    private void crearTablero(int filas, int columnas) {
        tablero = new GridPane();
        tablero.setAlignment(Pos.CENTER);
        casillas = new Button[filas][columnas];

        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                Button btn = new Button();
                btn.setPrefSize(35, 35);

                final int fila = i;
                final int col = j;

                btn.setOnMouseClicked((MouseEvent event) -> {
                    Casilla casilla = partida.getTablero()[fila][col];

                    if (event.getButton() == MouseButton.PRIMARY) {
                        if (isPrimeraMina) {
                            juego.iniciarJuego(casilla);
                            iniciarTemporizador();
                            this.isPrimeraMina=false;
                        } else {
                            juego.descubrirCasilla(casilla);
                        }
                    } else if (event.getButton() == MouseButton.SECONDARY) {
                        juego.alternarBandera(casilla);
                    } else if (event.getButton() == MouseButton.MIDDLE) {
                        juego.hacerChording(casilla);
                    }

                    renderizarTablero();
                });

                casillas[i][j] = btn;
                tablero.add(btn, j, i);
            }
        }
    }

    public void renderizarTablero() {
        switch (partida.getResultado()) {
            case Resultado.GANADOR:
                temporizador.stop();
                Alert alertWin = new Alert(Alert.AlertType.INFORMATION, "Ganaste!\nTiempo: " + partida.getTiempo());
                alertWin.show();
                break;
            case Resultado.PERDEDOR:
                temporizador.stop();
                Alert alertLoose = new Alert(Alert.AlertType.INFORMATION, "Perdiste!\nTiempo: " + partida.getTiempo());
                alertLoose.show();
                break;
            case Resultado.EN_PROGRESO:
                break;
        }
        lblBanderasRestantes.setText("Banderas: " + juego.getBanderasRestantes());
        int filas = partida.getDificultad().getFilas();
        int columnas = partida.getDificultad().getColumnas();

        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                Casilla casilla = partida.getTablero()[i][j];
                Button btn = casillas[i][j];

                if (casilla.isDescubierta()) {
                    if (casilla.isMina()) {
                        if (imgBandera != null) {
                            ImageView viewBandera = new ImageView(imgBandera);
                            viewBandera.setFitWidth(24);
                            viewBandera.setFitHeight(24);
                            viewBandera.setPreserveRatio(true);
                            btn.setGraphic(viewBandera);
                        } else {
                            btn.setText("💣");
                        }
                        btn.setStyle("-fx-background-color: #ff4d4d; -fx-opacity: 1;"); // Opacity 1 para que no se vea gris al deshabilitar
                    } else if (casilla.getMinasAdyacentes() > 0) {
                        btn.setText(String.valueOf(casilla.getMinasAdyacentes()));
                    } else {
                        btn.setDisable(true);
                        btn.setText(""); // Espacio en blanco (0 minas)
                    }
                } else if (casilla.isBandera()) {
                    if (imgMina != null) {
                        ImageView viewMina = new ImageView(imgMina);
                        viewMina.setFitWidth(24);
                        viewMina.setFitHeight(24);
                        viewMina.setPreserveRatio(true);
                        btn.setGraphic(viewMina);
                    } else {
                        btn.setText("🚩");
                    }
                } else {
                    btn.setText("");
                }
            }
        }
    }

    private void iniciarTemporizador() {
        // KeyFrame define qué hacer y cada cuánto tiempo (1 segundo)
        KeyFrame frame = new KeyFrame(Duration.seconds(1), event -> {
            segundosTranscurridos++;

            // Matemáticas simples para sacar minutos y segundos
            int minutos = segundosTranscurridos / 60;
            int segundos = segundosTranscurridos % 60;

            // Formato con ceros a la izquierda (ej. 05:09)
            lblTiempo.setText(String.format("%02d:%02d", minutos, segundos));
        });

        temporizador = new Timeline(frame);
        temporizador.setCycleCount(Timeline.INDEFINITE); // Para que corra infinito
        temporizador.play(); // Arranca el reloj
    }
}
