package co.edu.unilibre.gui;

import java.util.List;
import java.util.function.BiConsumer;

import co.edu.unilibre.dto.Dificultad;
import co.edu.unilibre.dto.Tema;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
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

        HBox dificultadesContainer = new HBox(16);
        ToggleGroup grupoDificultades = new ToggleGroup();

        // Tarjetas de dificultades desde BD
        for (Dificultad dificultad : dificultades) {
            String txtBadge = switch (dificultad.getNombre().toLowerCase()) {
                case "fácil" -> "PARA EMPEZAR";
                case "medio" -> "EL FAVORITO";
                case "difícil" -> "UN GRAN RETO";
                default -> "ESTÁNDAR";
            };

            ToggleButton card = crearTarjetaDificultad(
                txtBadge,
                dificultad.getNombre(),
                dificultad.getFilas() + " × " + dificultad.getColumnas(),
                dificultad.getMinas() + " minas",
                dificultad,
                grupoDificultades
            );
            dificultadesContainer.getChildren().add(card);
        }

        // Tarjeta "Personalizada"
        Dificultad difPersonalizada = new Dificultad("Personalizada", 12, 12, 20);
        ToggleButton cardPersonalizada = crearTarjetaDificultad(
            "CONFIGÚRALO",
            "Personalizada",
            "12 × 12",
            "20 minas",
            difPersonalizada,
            grupoDificultades
        );
        dificultadesContainer.getChildren().add(cardPersonalizada);

        // ==========================================
        // PANEL DESPLEGABLE DE PERSONALIZACIÓN
        // ==========================================
        HBox panelPersonalizado = new HBox(20);
        panelPersonalizado.setAlignment(Pos.CENTER_LEFT);
        panelPersonalizado.setPadding(new Insets(16));
        panelPersonalizado.getStyleClass().add("custom-config-box");

        panelPersonalizado.setVisible(false);
        panelPersonalizado.setManaged(false);

        // Límites altos en el Spinner para permitir ingresar números y disparar el Alert al validar
        Spinner<Integer> spFilas = new Spinner<>(5, 1000, 12);
        Spinner<Integer> spColumnas = new Spinner<>(5, 1000, 12);
        Spinner<Integer> spMinas = new Spinner<>(1, 100000, 20);

        spFilas.setEditable(true);
        spColumnas.setEditable(true);
        spMinas.setEditable(true);
        spFilas.setPrefWidth(85);
        spColumnas.setPrefWidth(85);
        spMinas.setPrefWidth(90);

        configurarCommitAlPerderFoco(spFilas);
        configurarCommitAlPerderFoco(spColumnas);
        configurarCommitAlPerderFoco(spMinas);

        VBox contenidoCard = (VBox) cardPersonalizada.getGraphic();
        Label lblDimPers = (Label) contenidoCard.getChildren().get(2);
        Label lblMinasPers = (Label) contenidoCard.getChildren().get(3);

        // Declaramos descResumen antes para poder actualizarlo también cuando cambien los spinners
        Label descResumen = new Label("Selecciona tu configuración favorita");
        descResumen.getStyleClass().add("footer-desc");

        Runnable actualizarPersonalizada = () -> {
            int f = spFilas.getValue();
            int c = spColumnas.getValue();
            int m = spMinas.getValue();

            // 1. Validar dimensiones máximas (300)
            if (f > 300 || c > 300) {
                Alert alertaError = new Alert(AlertType.ERROR, "Tablero no válido: máximo de filas y columnas es 300.");
                alertaError.showAndWait();

                spFilas.getValueFactory().setValue(Math.min(f, 300));
                spColumnas.getValueFactory().setValue(Math.min(c, 300));
                return;
            }

            // 2. Validar minas máximas (dejando 9 casillas libres)
            int minasMaximas = (f * c) - 9;

            if (m > minasMaximas) {
                int minasSeguras = Math.max(1, minasMaximas);
                Alert alertaError = new Alert(AlertType.ERROR, "Tablero no válido: el número de minas excede el límite seguro (" + minasSeguras + ").");
                alertaError.showAndWait();

                spMinas.getValueFactory().setValue(minasSeguras);
                return;
            }

            // 3. Actualizar objeto y vistas
            difPersonalizada.setFilas(f);
            difPersonalizada.setColumnas(c);
            difPersonalizada.setMinas(m);

            lblDimPers.setText(f + " × " + c);
            lblMinasPers.setText(m + " minas");

            if (dificultadSeleccionada == difPersonalizada) {
                actualizarResumen(descResumen);
            }
        };

        spFilas.valueProperty().addListener((obs, o, n) -> actualizarPersonalizada.run());
        spColumnas.valueProperty().addListener((obs, o, n) -> actualizarPersonalizada.run());
        spMinas.valueProperty().addListener((obs, o, n) -> actualizarPersonalizada.run());

        panelPersonalizado.getChildren().addAll(
            crearCampoSpinner("Filas (5-300):", spFilas),
            crearCampoSpinner("Columnas (5-300):", spColumnas),
            crearCampoSpinner("Minas:", spMinas)
        );

        grupoDificultades.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            boolean esPersonalizada = (newToggle == cardPersonalizada);
            panelPersonalizado.setVisible(esPersonalizada);
            panelPersonalizado.setManaged(esPersonalizada);
        });

        contenedorDificultades.getChildren().addAll(headerDificultades, dificultadesContainer, panelPersonalizado);

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

        for (Tema tema : temas) {
            HBox contenido = new HBox(16);
            contenido.setPadding(new Insets(16));
            contenido.setAlignment(Pos.CENTER_LEFT);

            Region paletteBox = new Region();
            paletteBox.setPrefSize(40, 40);
            paletteBox.setStyle("-fx-background-color: " + tema.getBg() + "; -fx-background-radius: 4px;");

            VBox textPart = new VBox(2);
            textPart.setAlignment(Pos.CENTER_LEFT);
            Label tituloTema = new Label(tema.getNombre());
            tituloTema.getStyleClass().add("title-dificultad");
            textPart.getChildren().addAll(tituloTema);

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
        resumenTextos.getChildren().addAll(resumenTitulo, descResumen);

        Region spacerFooter = new Region();
        HBox.setHgrow(spacerFooter, Priority.ALWAYS);

        Button btnComenzarJuego = new Button("Comenzar partida   ➔");
        btnComenzarJuego.getStyleClass().add("btn-comenzar");

        btnComenzarJuego.setOnAction(event -> {
            // Confirmar cualquier valor escrito en los spinners antes de arrancar
            if (dificultadSeleccionada == difPersonalizada) {
                try {
                    spFilas.commitValue();
                    spColumnas.commitValue();
                    spMinas.commitValue();
                } catch (NumberFormatException ignored) {}
            }

            if (dificultadSeleccionada == null) {
                new Alert(AlertType.WARNING, "No se seleccionó una dificultad").show();
                return;
            }
            if (temaSeleccionado == null) {
                new Alert(AlertType.WARNING, "No se seleccionó un tema").show();
                return;
            }
            if (onIniciarPartida != null) {
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

        getChildren().addAll(
            contenedorTitulo, separador1,
            contenedorDificultades, separador2,
            contenedorTemas,
            new Region(),
            contenedorResumen
        );
    }

    private void configurarCommitAlPerderFoco(Spinner<Integer> spinner) {
        spinner.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                try {
                    spinner.commitValue();
                } catch (NumberFormatException e) {
                    spinner.getEditor().setText(String.valueOf(spinner.getValue()));
                }
            }
        });
    }

    private ToggleButton crearTarjetaDificultad(String badge, String titulo, String dimensiones, String minas, Dificultad data, ToggleGroup group) {
        VBox contenido = new VBox(4);
        contenido.setPadding(new Insets(16));

        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label lblBadge = new Label(badge);
        lblBadge.getStyleClass().add("badge-favorito");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label radioCircle = new Label();
        radioCircle.getStyleClass().add("radio-indicator");

        topRow.getChildren().addAll(lblBadge, spacer, radioCircle);

        Label tituloDificultad = new Label(titulo);
        tituloDificultad.getStyleClass().add("title-dificultad");

        Label dimensionesDificultad = new Label(dimensiones);
        dimensionesDificultad.getStyleClass().add("desc-dimensiones");

        Label minasDificultad = new Label(minas);
        minasDificultad.getStyleClass().add("desc-minas");

        contenido.getChildren().addAll(topRow, tituloDificultad, dimensionesDificultad, minasDificultad);

        ToggleButton cardButton = new ToggleButton();
        cardButton.setGraphic(contenido);
        cardButton.getStyleClass().add("card-toggle");
        cardButton.setToggleGroup(group);
        cardButton.setUserData(data);
        cardButton.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(cardButton, Priority.ALWAYS);

        return cardButton;
    }

    private VBox crearCampoSpinner(String etiqueta, Spinner<Integer> spinner) {
        VBox box = new VBox(4);
        Label lbl = new Label(etiqueta);
        lbl.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #475569;");
        box.getChildren().addAll(lbl, spinner);
        return box;
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
