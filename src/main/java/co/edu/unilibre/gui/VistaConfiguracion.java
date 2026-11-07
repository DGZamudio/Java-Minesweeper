package co.edu.unilibre.gui;

import java.util.List;
import java.util.function.BiConsumer;

import co.edu.unilibre.dto.Dificultad;
import co.edu.unilibre.dto.Tema;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/**
 * VistaConfiguracion
 */
public class VistaConfiguracion extends VBox {

    // Callbacks
    private BiConsumer<Dificultad, Tema> onIniciarPartida;

    // Estados
    private Dificultad dificultadSeleccionada;
    private Tema temaSeleccionado;

    public VistaConfiguracion(List<Dificultad> dificultades, List<Tema> temas) {
        getStylesheets().add(getClass().getResource("/styles/general.css").toExternalForm());
        getStylesheets().add(getClass().getResource("/styles/config.css").toExternalForm());

        // Espaciado principal de toda la vista
        setSpacing(30);
        setPadding(new Insets(40, 50, 0, 50));
        setStyle("-fx-background-color: #F8FAFC;");

        // --- 1. TÍTULO PRINCIPAL ---
        VBox contenedorTitulo = new VBox(4);
        Label subtituloConfiguracion = new Label("CONFIGURACIÓN");
        subtituloConfiguracion.getStyleClass().add("subtitle-uppercase");

        Label tituloConfiguracion = new Label("Prepara el campo");
        tituloConfiguracion.getStyleClass().add("title-main");

        Label descripcionConfiguracion = new Label("Elige el nivel y el aspecto de tu próxima partida.");
        descripcionConfiguracion.getStyleClass().add("desc-main");

        contenedorTitulo.getChildren().addAll(subtituloConfiguracion, tituloConfiguracion, descripcionConfiguracion);

        Region separador1 = new Region();
        separador1.getStyleClass().add("section-divider");

        // --- 2. SECCIÓN DIFICULTADES ---
        VBox contenedorDificultades = new VBox(12);

        VBox headerDificultades = new VBox(2);
        Label tituloDificultades = new Label("Dificultad");
        tituloDificultades.getStyleClass().add("title-secondary");
        Label descripcionDificultades = new Label("Selecciona el tamaño del tablero.");
        descripcionDificultades.getStyleClass().add("desc-secondary");
        headerDificultades.getChildren().addAll(tituloDificultades, descripcionDificultades);

        HBox dificultadesContainer = new HBox(16); // Espacio entre tarjetas
        ToggleGroup grupoDificultades = new ToggleGroup();

        for (Dificultad dificultad : dificultades) {
            VBox contenido = new VBox(4);
            contenido.setPadding(new Insets(16));

            HBox topRow = new HBox();
            topRow.setAlignment(Pos.CENTER_LEFT);

            // Asignar el subtítulo dinámico según el nombre
            String txtBadge = "";
            if ("Fácil".equalsIgnoreCase(dificultad.getNombre())) txtBadge = "PARA EMPEZAR";
            else if ("Medio".equalsIgnoreCase(dificultad.getNombre())) txtBadge = "EL FAVORITO";
            else if ("Difícil".equalsIgnoreCase(dificultad.getNombre())) txtBadge = "UN GRAN RETO";
            else txtBadge = "CONFIGÚRALO";

            Label lblBadge = new Label(txtBadge);
            lblBadge.getStyleClass().add("badge-favorito");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Label radioCircle = new Label();
            radioCircle.getStyleClass().add("radio-indicator");

            topRow.getChildren().addAll(lblBadge, spacer, radioCircle);

            Label tituloDificultad = new Label(dificultad.getNombre());
            tituloDificultad.getStyleClass().add("title-dificultad");

            Label dimensionesDificultad = new Label(dificultad.getFilas() + " × " + dificultad.getColumnas());
            dimensionesDificultad.getStyleClass().add("desc-dimensiones");

            Label minasDificultad = new Label(dificultad.getMinas() + " minas");
            minasDificultad.getStyleClass().add("desc-minas");

            contenido.getChildren().addAll(topRow, tituloDificultad, dimensionesDificultad, minasDificultad);

            ToggleButton cardButton = new ToggleButton();
            cardButton.setGraphic(contenido);
            cardButton.getStyleClass().add("card-toggle");
            cardButton.setToggleGroup(grupoDificultades);
            cardButton.setUserData(dificultad);
            cardButton.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(cardButton, Priority.ALWAYS); // Hace que ocupen el mismo ancho

            dificultadesContainer.getChildren().add(cardButton);
        }

        contenedorDificultades.getChildren().addAll(headerDificultades, dificultadesContainer);

        Region separador2 = new Region();
        separador2.getStyleClass().add("section-divider");

        // --- 3. SECCIÓN TEMAS ---
        VBox contenedorTemas = new VBox(12);

        VBox headerTemas = new VBox(2);
        Label tituloTemas = new Label("Tema visual");
        tituloTemas.getStyleClass().add("title-secondary");
        Label descripcionTemas = new Label("Personaliza la apariencia del campo.");
        descripcionTemas.getStyleClass().add("desc-secondary");
        headerTemas.getChildren().addAll(tituloTemas, descripcionTemas);

        HBox temasContainer = new HBox(16);
        ToggleGroup grupoTemas = new ToggleGroup();

        for (Tema tema: temas) {
            // En los temas, el layout interno es horizontal (Color -> Texto -> Radio)
            HBox contenido = new HBox(16);
            contenido.setPadding(new Insets(16));
            contenido.setAlignment(Pos.CENTER_LEFT);

            // Simulador de paleta de colores (Un cuadrito visual)
            Region paletteBox = new Region();
            paletteBox.setPrefSize(40, 40);
            paletteBox.setStyle("-fx-background-color: #CBD5E1; -fx-background-radius: 4px;"); // Gris temporal

            VBox textPart = new VBox(2);
            textPart.setAlignment(Pos.CENTER_LEFT);
            Label tituloTema = new Label(tema.getNombre());
            tituloTema.getStyleClass().add("title-dificultad");
            Label subtituloTema = new Label("Claro y atemporal"); // Podrías sacarlo de la BD luego
            subtituloTema.getStyleClass().add("desc-dimensiones");
            textPart.getChildren().addAll(tituloTema, subtituloTema);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Label radioCircle = new Label();
            radioCircle.getStyleClass().add("radio-indicator");

            contenido.getChildren().addAll(paletteBox, textPart, spacer, radioCircle);

            ToggleButton cardButton = new ToggleButton();
            cardButton.setGraphic(contenido);
            cardButton.getStyleClass().add("card-toggle");
            cardButton.setToggleGroup(grupoTemas);
            cardButton.setUserData(tema);
            cardButton.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(cardButton, Priority.ALWAYS);

            temasContainer.getChildren().add(cardButton);
        }

        contenedorTemas.getChildren().addAll(headerTemas, temasContainer);

        // --- 4. SECCIÓN RESUMEN (FOOTER DARK NAVY) ---
        HBox contenedorResumen = new HBox();
        contenedorResumen.getStyleClass().add("footer-navy");
        contenedorResumen.setAlignment(Pos.CENTER_LEFT);

        VBox resumenTextos = new VBox(4);
        Label resumenTitulo = new Label("RESUMEN");
        resumenTitulo.getStyleClass().add("footer-title");

        // Este label debe ser global en tu clase si quieres que actualizarResumen() funcione
        Label descResumen = new Label("Selecciona tu configuración favorita");
        descResumen.getStyleClass().add("footer-desc");
        resumenTextos.getChildren().addAll(resumenTitulo, descResumen);

        Region spacerFooter = new Region();
        HBox.setHgrow(spacerFooter, Priority.ALWAYS);

        Button btnComenzarJuego = new Button("Comenzar partida   ➔");
        btnComenzarJuego.getStyleClass().add("btn-comenzar");

        btnComenzarJuego.setOnAction(event -> {
            if (dificultadSeleccionada != null && temaSeleccionado != null) {
                onIniciarPartida.accept(dificultadSeleccionada, temaSeleccionado);
            }
        });

        contenedorResumen.getChildren().addAll(resumenTextos, spacerFooter, btnComenzarJuego);

        // Listeners
        grupoDificultades.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                this.dificultadSeleccionada = (Dificultad) newVal.getUserData();
                actualizarResumen(descResumen);
            }
        });

        grupoTemas.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                this.temaSeleccionado = (Tema) newVal.getUserData();
                actualizarResumen(descResumen);
            }
        });

        // Ensamblaje final
        getChildren().addAll(
            contenedorTitulo, separador1,
            contenedorDificultades, separador2,
            contenedorTemas,
            new Region(), // Un espaciador extra antes del footer si se desea
            contenedorResumen
        );
    }

	private void actualizarResumen(Label descResumen) {
        String resumenTxt = "";

        if (dificultadSeleccionada != null) {
            resumenTxt += dificultadSeleccionada.getNombre() + " · " +
                          dificultadSeleccionada.getFilas() + "x" +
                          dificultadSeleccionada.getColumnas() + " · " +
                          dificultadSeleccionada.getMinas() + " minas";
        }

        if (temaSeleccionado != null) {
            resumenTxt += (resumenTxt.isEmpty() ? "" : " · ") + temaSeleccionado.getNombre();
        }

        if (resumenTxt.isEmpty()) {
            resumenTxt = "Selecciona tu configuración favorita";
        }

        descResumen.setText(resumenTxt);
	}

	public void setOnIniciarPartida(BiConsumer<Dificultad, Tema> onIniciarPartida) {
	    this.onIniciarPartida = onIniciarPartida;
	}
}
