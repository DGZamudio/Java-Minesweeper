package co.edu.unilibre.gui;

import java.util.List;

import co.edu.unilibre.dao.DificultadDao;
import co.edu.unilibre.dao.DificultadDaoImp;
import co.edu.unilibre.dao.TemaDao;
import co.edu.unilibre.dao.TemaDaoImp;
import co.edu.unilibre.dao.UsuarioDao;
import co.edu.unilibre.dao.UsuarioDaoImp;
import co.edu.unilibre.dto.Dificultad;
import co.edu.unilibre.dto.Tema;
import co.edu.unilibre.dto.Usuario;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;


/**
 * VistaPrincipal
 */
public class VistaPrincipal extends Application {

    // Vistas
    private VistaLogin vistaLogin;
    private VistaRegister vistaRegister;
    private PanelTablero panelTablero;
    private VistaConfiguracion vistaConfiguracion;

    // Servicios
    private UsuarioDao usuarioDao;
    private DificultadDao dificultadDao;
    private TemaDao temaDao;

    // Estados
    private Usuario usuario;

    public VistaPrincipal() {
        this.usuarioDao = new UsuarioDaoImp();
        this.dificultadDao = new DificultadDaoImp();
        this.temaDao = new TemaDaoImp();
    }

    private void cargarPartida(Dificultad dificultad, Tema tema) {
        this.panelTablero = new PanelTablero(dificultad, this.usuario, tema);

        this.panelTablero.setOnReiniciar(() -> {
            System.out.println("Reiniciando el mismo nivel...");
            cargarPartida(dificultad, tema);
        });

        this.panelTablero.setOnNuevaPartida(() -> {
            ViewManager.loadView(vistaConfiguracion);
        });

        ViewManager.loadView(this.panelTablero);
    }

    @Override
    public void start(Stage primaryStage) {
        BorderPane mainLayout = new BorderPane();
        ViewManager.setRootLayout(mainLayout);

        // VistaConfiguración
        List<Dificultad> dificultades = dificultadDao.listarDificultades();
        List<Tema> temas = temaDao.listarTemas();

        vistaConfiguracion = new VistaConfiguracion(dificultades, temas);
        vistaConfiguracion.setOnIniciarPartida((dificultad, tema) -> {
            cargarPartida(dificultad, tema);
        });

        // VistaRegister
        vistaRegister = new VistaRegister();
        vistaRegister.setOnRegisterUser((usuario) -> {
            try {
                usuarioDao.registrarUsuario(usuario);
                this.usuario = usuario;
                ViewManager.loadView(vistaConfiguracion);
            } catch(RuntimeException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error al crear el usuario");
                alert.show();
            }
        });


        // Carga inicial: VistaLogin
        vistaLogin = new VistaLogin();
        vistaLogin.setOnAutenticar((usuario, password) -> {
            // Se ejecuta cuando el usuario presiona el botón en la vista
            this.usuario = usuarioDao.autenticarUsuario(usuario, password);

            if (this.usuario != null) {
                if (vistaConfiguracion != null) {
                    ViewManager.loadView(vistaConfiguracion);
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Credenciales inválidas");
                alert.show();
            }
        });
        vistaLogin.setOnIrARegistro(() -> {
            ViewManager.loadView(vistaRegister);
        });

        ViewManager.loadView(vistaLogin);


        // Configuracion de interfaz
        Scene scene = new Scene(mainLayout, 600, 650);
        primaryStage.setTitle("BuscaMinas");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
