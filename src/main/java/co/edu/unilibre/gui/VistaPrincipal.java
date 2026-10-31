package co.edu.unilibre.gui;

import co.edu.unilibre.dao.DificultadDao;
import co.edu.unilibre.dao.DificultadDaoImp;
import co.edu.unilibre.dao.TemaDao;
import co.edu.unilibre.dao.TemaDaoImp;
import co.edu.unilibre.dto.Dificultad;
import co.edu.unilibre.dto.Tema;
import co.edu.unilibre.dto.Usuario;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * VistaPrincipal
 */
public class VistaPrincipal extends Application {

    private BorderPane contenedorRaiz;
    private Usuario usuarioAutenticado;

    // Servicios y DAOs
    private DificultadDao dificultadDao;
    private TemaDao temaDao;

    public VistaPrincipal() {
        this.contenedorRaiz = new BorderPane();
        this.dificultadDao = new DificultadDaoImp();
        this.temaDao = new TemaDaoImp();
        mostrarLogin();
    }

    // Pantalla de inicio de sesion
    public void mostrarLogin() {
        VistaLogin loginView = new VistaLogin(this);
        contenedorRaiz.setCenter(loginView.getVista());
    }

    public void alAutenticarUsuario(Usuario usuario) {
        this.usuarioAutenticado = usuario;
        mostrarConfiguracionJuego();
    }

    // Pantalla de configuracion
    public void mostrarConfiguracionJuego() {
        VBox panelConfig = new VBox(15);

        ComboBox<Dificultad> comboDificultad = new ComboBox<>();
        comboDificultad.getItems().addAll(dificultadDao.listarDificultades());

        ComboBox<Tema> comboTema = new ComboBox<>();
        comboTema.getItems().addAll(temaDao.listarTemas());

        Button btnEmpezar = new Button("¡Jugar!");
        btnEmpezar.setOnAction(e -> {
            Dificultad dif = comboDificultad.getValue();
            Tema tema = comboTema.getValue();
            mostrarTablero(dif, tema);
        });

        panelConfig.getChildren().addAll(
            new Label("Bienvenido " + usuarioAutenticado.getUsername()),
            new Label("Selecciona Dificultad:"), comboDificultad,
            new Label("Selecciona Tema:"), comboTema,
            btnEmpezar
        );

        contenedorRaiz.setCenter(panelConfig);
    }

    // Pantalla de tablero
    public void mostrarTablero(Dificultad dificultad, Tema tema) {
        PanelTablero panelTablero = new PanelTablero(dificultad, usuarioAutenticado, tema);
        contenedorRaiz.setCenter(panelTablero.getVista());
    }

    // Pantalla de clasificación
    public void mostrarClasificacion() {
        PanelClasificacion clasificacionView = new PanelClasificacion(this);
        contenedorRaiz.setCenter(clasificacionView.getVista());
    }

    public BorderPane getContenedorRaiz() {
        return contenedorRaiz;
    }

    @Override
    public void start(Stage primaryStage) {
        VistaPrincipal vistaPrincipal = new VistaPrincipal();
        Scene scene = new Scene(vistaPrincipal.getContenedorRaiz(), 600, 650);

        primaryStage.setTitle("Buscaminas Unilibre");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
