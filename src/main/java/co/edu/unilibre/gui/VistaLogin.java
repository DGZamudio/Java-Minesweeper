package co.edu.unilibre.gui;

import co.edu.unilibre.dao.UsuarioDao;
import co.edu.unilibre.dao.UsuarioDaoImp;
import co.edu.unilibre.dto.Usuario;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

/**
 * VistaLogin
 */
public class VistaLogin {

    // Java FX
    private VistaPrincipal vistaPrincipal;
    private VBox contenedor;
    private TextField txtUsuario;
    private PasswordField txtPassword;
    private Button btnIngresar;

    // Servicios
    private UsuarioDao usuarioDao;


    public VistaLogin(VistaPrincipal vistaPrincipal) {
        this.usuarioDao = new UsuarioDaoImp();
        // Inicializar el contenedor de JavaFX
        contenedor = new VBox(10);

        txtUsuario = new TextField();
        txtPassword = new PasswordField();
        btnIngresar = new Button("Iniciar Sesión");
        btnIngresar.setOnMouseClicked((MouseEvent event) -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                alDarClicEnIngresar();
            }
        });

        // Agregar los componentes al VBox
        contenedor.getChildren().addAll(
            new Label("Usuario:"), txtUsuario,
            new Label("Contraseña:"), txtPassword,
            btnIngresar
        );
    }

    private void alDarClicEnIngresar() {
        String username = txtUsuario.getText();
        String pass = txtPassword.getText();

        Usuario usuario = usuarioDao.autenticarUsuario(username, pass);

        if (usuario != null) {
            vistaPrincipal.alAutenticarUsuario(usuario);
        } else {
            // Mostrar alerta de error de credenciales
        }
    }

    public VBox getVista() {
        return contenedor;
    }
}
