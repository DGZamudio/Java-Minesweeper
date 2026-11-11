package co.edu.unilibre.gui;

import java.util.List;

import co.edu.unilibre.dao.*;
import co.edu.unilibre.dto.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;


/**
 * VistaPrincipal
 */
public class VistaPrincipal extends Application {

    // JavaFX
    private HBox header;
    private Button btnUserStat;

    // Servicios
    private UsuarioDao usuarioDao;
    private PartidaDao partidaDao;
    private DificultadDao dificultadDao;
    private TemaDao temaDao;

    // Estados
    private Usuario usuario;


    public VistaPrincipal() {
        this.usuarioDao = new UsuarioDaoImp();
        this.partidaDao = new PartidaDaoImp();
        this.dificultadDao = new DificultadDaoImp();
        this.temaDao = new TemaDaoImp();
    }

    // PanelTablero
    private void cargarPartida(Dificultad dificultad, Tema tema) {
        header.setVisible(false);
        PanelTablero panelTablero = new PanelTablero(dificultad, this.usuario, tema);

        panelTablero.setOnReiniciar(() -> {
            cargarPartida(dificultad, tema);
        });

        panelTablero.setOnNuevaPartida(() -> {
            header.setVisible(true);
            cargarConfiguracion();
        });

        panelTablero.setOnVerRanking(() -> {
            header.setVisible(true);
            cargarClasificacion();
        });

        panelTablero.setOnSubirPartida((partida) -> {
            if (partida != null) {
                partidaDao.guardarPartida(partida);
                header.setVisible(true);
                cargarClasificacion();
            }
        });

        ViewManager.loadView(panelTablero);
    }

    // VistaRegister
    private void cargarRegister() {
        header.setVisible(false);
        VistaRegister vistaRegister = new VistaRegister();
        vistaRegister.setOnRegisterUser((usuario) -> {
            try {
                usuarioDao.registrarUsuario(usuario);
                this.usuario = usuario;
                btnUserStat.setText("Jugador: " + this.usuario.getUsername());

                header.setVisible(true);
                cargarConfiguracion();
            } catch(RuntimeException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error al crear el usuario");
                alert.show();
            }
        });
        vistaRegister.setOnIrALogin(() -> {
            cargarLogin();
        });

        ViewManager.loadView(vistaRegister);
    }

    // Carga inicial: VistaLogin
    private void cargarLogin() {
        header.setVisible(false);
        VistaLogin vistaLogin = new VistaLogin(usuario);
        vistaLogin.setOnAutenticar((usuario, password) -> {
            // Se ejecuta cuando el usuario presiona el botón en la vista
            this.usuario = usuarioDao.autenticarUsuario(usuario, password);

            if (this.usuario != null) {
                header.setVisible(true);
                btnUserStat.setText("Jugador: " + this.usuario.getUsername());

                cargarConfiguracion();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Credenciales inválidas");
                alert.show();
            }
        });
        vistaLogin.setOnIrARegistro(() -> {
            cargarRegister();
        });
        vistaLogin.setOnCerrarLogin(() -> {
            header.setVisible(true);
            cargarConfiguracion();
        });

        ViewManager.loadView(vistaLogin);
    }

    // VistaConfiguración
    private void cargarConfiguracion() {
        List<Dificultad> dificultades = dificultadDao.listarDificultades();
        List<Tema> temas = temaDao.listarTemas();

        VistaConfiguracion vistaConfiguracion = new VistaConfiguracion(dificultades, temas);
        vistaConfiguracion.setOnIniciarPartida((dificultad, tema) -> {
            cargarPartida(dificultad, tema);
        });

        ViewManager.loadView(vistaConfiguracion);
    }

    // PanelClasificacion
    private void cargarClasificacion() {
        List<Partida> partidas = partidaDao.obtenerPartidas();
        PanelClasificacion panelClasificacion = new PanelClasificacion(partidas);

        ViewManager.loadView(panelClasificacion);
    }



    private void crearHeaderGlobal() {
        this.header = new HBox();
        header.getStylesheets().add(getClass().getResource("/styles/general.css").toExternalForm());
        header.getStyleClass().add("global-header");
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(14, 32, 14, 32));
        header.setSpacing(15);

        // --- 1. BRANDING / LOGO ---
        HBox logoBox = new HBox(8);
        logoBox.setAlignment(Pos.CENTER_LEFT);

        Label logoIcon = new Label("⎈");
        logoIcon.getStyleClass().add("logo-icon");

        Label logoTextBold = new Label("Busca");
        logoTextBold.getStyleClass().add("logo-text-bold");

        Label logoTextLight = new Label("Minas");
        logoTextLight.getStyleClass().add("logo-text-light");

        HBox logoTitleBox = new HBox(logoTextBold, logoTextLight);
        logoTitleBox.setAlignment(Pos.CENTER_LEFT);

        logoBox.getChildren().addAll(logoIcon, logoTitleBox);

        // --- 2. NAVEGACIÓN PRINCIPAL ---
        HBox navBox = new HBox(6);
        navBox.setAlignment(Pos.CENTER_LEFT);

        Button btnNuevaPartida = new Button("Nueva partida");
        btnNuevaPartida.getStyleClass().add("nav-btn");
        btnNuevaPartida.setOnAction(event -> {
            cargarConfiguracion();
        });

        Button btnVerClasificacion = new Button("Clasificación");
        btnVerClasificacion.getStyleClass().add("nav-btn");
        btnVerClasificacion.setOnAction(event -> {
            cargarClasificacion();
        });

        navBox.getChildren().addAll(btnNuevaPartida, btnVerClasificacion);

        // Separador elástico
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // --- 3. BADGE DE USUARIO ---
        HBox userBox = new HBox(8);
        userBox.setAlignment(Pos.CENTER_LEFT);

        Label userAvatar = new Label(usuario != null ? obtenerIniciales(usuario.getUsername()) : "?");
        userAvatar.getStyleClass().add("header-avatar");

        btnUserStat = new Button(usuario != null ? usuario.getUsername() : "Iniciar sesión");
        btnUserStat.getStyleClass().add("header-user-btn");
        btnUserStat.setOnAction(event -> {
            cargarLogin();
        });

        userBox.getChildren().addAll(userAvatar, btnUserStat);

        // Agregamos todo al header
        header.getChildren().addAll(logoBox, navBox, spacer, userBox);
    }

    // Helper rápido para iniciales del avatar
    private String obtenerIniciales(String nombre) {
        if (nombre == null || nombre.isBlank()) return "?";
        return nombre.substring(0, Math.min(2, nombre.length())).toUpperCase();
    }

    @Override
    public void start(Stage primaryStage) {
        BorderPane mainLayout = new BorderPane();
        ViewManager.setRootLayout(mainLayout);

        crearHeaderGlobal();
        mainLayout.setTop(header); // Se fija arriba para siempre

        // Vista inicial
        cargarLogin();

        // Configuracion de interfaz
        Scene scene = new Scene(mainLayout, 600, 650);
        primaryStage.setTitle("BuscaMinas");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
