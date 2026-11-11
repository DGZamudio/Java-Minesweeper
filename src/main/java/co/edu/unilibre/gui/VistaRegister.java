package co.edu.unilibre.gui;

import java.util.function.Consumer;

import co.edu.unilibre.dto.Usuario;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/**
 * VistaRegister
 */
public class VistaRegister extends VBox {

    // Callbacks
    private Consumer<Usuario> onRegisterUser;
    private Runnable onIrALogin;

    public VistaRegister() {
        // Cargar estilos
        getStylesheets().add(getClass().getResource("/styles/general.css").toExternalForm());
        getStylesheets().add(getClass().getResource("/styles/auth.css").toExternalForm());

        // Contenedor principal: Centrado en pantalla
        setAlignment(Pos.CENTER);
        getStyleClass().add("main-background"); // Fondo gris completo

        // --- TARJETA BLANCA CENTRAL ---
        VBox cardCentral = new VBox(16); // Espaciado entre bloques
        cardCentral.getStyleClass().add("auth-card");
        cardCentral.setMaxWidth(420);
        cardCentral.setPadding(new Insets(32, 36, 32, 36));

        // --- HEADER: Logo y botón Cerrar ---
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        HBox logoBox = new HBox(4);
        logoBox.setAlignment(Pos.CENTER_LEFT);
        Label logoIcon = new Label("⎈ ");
        logoIcon.getStyleClass().add("logo-icon");
        Label logoText1 = new Label("Busca");
        logoText1.getStyleClass().add("logo-title");
        Label logoText2 = new Label("Minas");
        logoText2.getStyleClass().add("logo-subtitle");
        logoBox.getChildren().addAll(logoIcon, logoText1, logoText2);

        Region spacerHeader = new Region();
        HBox.setHgrow(spacerHeader, Priority.ALWAYS);

        header.getChildren().addAll(logoBox, spacerHeader);

        // --- TÍTULOS ---
        VBox titleBox = new VBox(4);
        titleBox.setPadding(new Insets(10, 0, 10, 0));
        Label subTitle = new Label("NUEVO JUGADOR");
        subTitle.getStyleClass().add("subtitle-uppercase");
        Label mainTitle = new Label("Crea tu cuenta");
        mainTitle.getStyleClass().add("title-main");
        Label descTitle = new Label("Empieza a mejorar tus tiempos.");
        descTitle.getStyleClass().add("desc-text");
        titleBox.getChildren().addAll(subTitle, mainTitle, descTitle);

        // --- FORMULARIO ---
        VBox inputUserBox = new VBox(8);
        Label lblUser = new Label("Nombre de usuario");
        lblUser.getStyleClass().add("field-label");
        TextField txtUser = new TextField();
        txtUser.getStyleClass().add("custom-input");
        inputUserBox.getChildren().addAll(lblUser, txtUser);

        VBox inputPassBox = new VBox(8);
        Label lblPass = new Label("Contraseña");
        lblPass.getStyleClass().add("field-label");
        PasswordField txtPass = new PasswordField();
        txtPass.getStyleClass().add("custom-input");
        inputPassBox.getChildren().addAll(lblPass, txtPass);

        // Opciones secundarias (Recordarme / Olvidaste)
        HBox optionsBox = new HBox();
        optionsBox.setAlignment(Pos.CENTER_LEFT);
        CheckBox chkRecordarme = new CheckBox("Recordarme");
        chkRecordarme.getStyleClass().add("custom-checkbox");
        chkRecordarme.setSelected(true);

        Region spacerOptions = new Region();
        HBox.setHgrow(spacerOptions, Priority.ALWAYS);

        Hyperlink linkOlvidaste = new Hyperlink("¿Olvidaste tu contraseña?");
        linkOlvidaste.getStyleClass().add("link-bold");
        optionsBox.getChildren().addAll(chkRecordarme, spacerOptions, linkOlvidaste);

        // --- BOTÓN PRINCIPAL ---
        Button btnEntrar = new Button("Crear cuenta   ➔"); // o "Entrar al juego ➔"
        btnEntrar.setMaxWidth(Double.MAX_VALUE);
        btnEntrar.getStyleClass().add("btn-primary");

        // --- SEPARADOR 'o' ---
        HBox separatorBox = new HBox(10);
        separatorBox.setAlignment(Pos.CENTER);
        separatorBox.setPadding(new Insets(10, 0, 10, 0));

        Region line1 = new Region();
        line1.getStyleClass().add("line-separator");
        HBox.setHgrow(line1, Priority.ALWAYS);

        Label lblO = new Label("o");
        lblO.getStyleClass().add("text-separator");

        Region line2 = new Region();
        line2.getStyleClass().add("line-separator");
        HBox.setHgrow(line2, Priority.ALWAYS);

        separatorBox.getChildren().addAll(line1, lblO, line2);

        // --- FOOTER (Link a la otra vista) ---
        HBox footerBox = new HBox(4);
        footerBox.setAlignment(Pos.CENTER);
        Label lblFooter = new Label("¿Ya tienes una cuenta?");
        lblFooter.getStyleClass().add("desc-text");
        Hyperlink linkCambio = new Hyperlink("Inicia sesión");
        linkCambio.getStyleClass().add("link-bold");
        footerBox.getChildren().addAll(lblFooter, linkCambio);

        // Ensamblar la tarjeta central
        cardCentral.getChildren().addAll(
            header,
            titleBox,
            inputUserBox,
            inputPassBox,
            optionsBox,
            btnEntrar,
            separatorBox,
            footerBox
        );

        // Agregar la tarjeta al VBox principal centrado
        getChildren().add(cardCentral);

        // --- EVENTOS ---
        btnEntrar.setOnAction(event -> {
            Usuario usuario = new Usuario(txtUser.getText().trim(), txtPass.getText().trim());
            if (onRegisterUser != null) {
                onRegisterUser.accept(usuario);
            }
        });

        linkCambio.setOnAction(event -> {
            if (onIrALogin != null) {
                onIrALogin.run();
            }
        });
    }

    public void setOnRegisterUser(Consumer<Usuario> onRegisterUser) {
        this.onRegisterUser = onRegisterUser;
    }

    public void setOnIrALogin(Runnable onIrALogin) {
        this.onIrALogin = onIrALogin;
    }

}
