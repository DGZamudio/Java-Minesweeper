package co.edu.unilibre.gui;

import java.time.Duration;
import java.util.List;

import co.edu.unilibre.dto.Partida;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/**
 * PanelClasificacion
 */
public class PanelClasificacion extends VBox {

    // JavaFX
    private TableView<Partida> tabla;
    private ObservableList<Partida> partidasObservable;

    // Estados
    private List<Partida> partidas;

    public PanelClasificacion(List<Partida> partidas) {
        this.partidas = partidas;
        this.partidasObservable = FXCollections.observableArrayList(partidas != null ? partidas : List.of());

        getStylesheets().add(getClass().getResource("/styles/config.css").toExternalForm());
        getStylesheets().add(getClass().getResource("/styles/ranking.css").toExternalForm());

        setSpacing(20);
        setPadding(new Insets(40, 50, 40, 50));
        setStyle("-fx-background-color: #F8FAFC;");

        // ==========================================
        // 1. ENCABEZADO CON FILTROS (SUPERIOR)
        // ==========================================
        HBox headerArea = new HBox();
        headerArea.setAlignment(Pos.CENTER_LEFT);

        VBox contenedorTitulo = new VBox(4);
        Label subtituloClasificacion = new Label("REGISTROS");
        subtituloClasificacion.getStyleClass().add("subtitle-uppercase");

        Label tituloClasificacion = new Label("Tabla general");
        tituloClasificacion.getStyleClass().add("title-main");

        contenedorTitulo.getChildren().addAll(subtituloClasificacion, tituloClasificacion);

        Region spacerHeader = new Region();
        HBox.setHgrow(spacerHeader, Priority.ALWAYS);

        // Barra de botones tipo filtro (Todas, Fácil, Medio, Difícil)
        HBox filterBar = new HBox(2);
        filterBar.getStyleClass().add("filter-bar");

        ToggleButton btnTodas = new ToggleButton("Todas");
        ToggleButton btnFacil = new ToggleButton("Fácil");
        ToggleButton btnMedio = new ToggleButton("Medio");
        ToggleButton btnDificil = new ToggleButton("Difícil");

        ToggleGroup filterGroup = new ToggleGroup();
        btnTodas.setToggleGroup(filterGroup);
        btnFacil.setToggleGroup(filterGroup);
        btnMedio.setToggleGroup(filterGroup);
        btnDificil.setToggleGroup(filterGroup);
        btnTodas.setSelected(true);

        btnTodas.getStyleClass().add("filter-button");
        btnFacil.getStyleClass().add("filter-button");
        btnMedio.getStyleClass().add("filter-button");
        btnDificil.getStyleClass().add("filter-button");

        filterBar.getChildren().addAll(btnTodas, btnFacil, btnMedio, btnDificil);
        headerArea.getChildren().addAll(contenedorTitulo, spacerHeader, filterBar);

        // ==========================================
        // 2. TABLA DE PUNTAJES CON ESTILO DE LA FOTO
        // ==========================================
        tabla = new TableView<>();
        //tabla.setColumnResizePolicy(TableView.CONSTRAIN_RESIZE_POLICY_FLEXIBLE);
        tabla.getStyleClass().add("custom-table");
        VBox.setVgrow(tabla, Priority.ALWAYS);

        // --- COLUMNA 1: POSICIÓN ---
        TableColumn<Partida, Integer> colPosicion = new TableColumn<>("POS.");
        colPosicion.setCellValueFactory(cellData ->
            new ReadOnlyObjectWrapper<>(tabla.getItems().indexOf(cellData.getValue()) + 1)
        );
        colPosicion.setCellFactory(col -> new TableCell<Partida, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() < 0) {
                    setText(null);
                } else {
                    setText(String.valueOf(getIndex() + 1));
                    getStyleClass().add("col-posicion-text");
                }
            }
        });

        // --- COLUMNA 2: JUGADOR (Avatar circular con iniciales + Nombre) ---
        TableColumn<Partida, Partida> colJugador = new TableColumn<>("JUGADOR");
        colJugador.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue()));
        colJugador.setCellFactory(col -> new TableCell<Partida, Partida>() {
            @Override
            protected void updateItem(Partida partida, boolean empty) {
                super.updateItem(partida, empty);
                if (empty || partida == null) {
                    setGraphic(null);
                } else {
                    HBox cellBox = new HBox(10);
                    cellBox.setAlignment(Pos.CENTER_LEFT);

                    String nombre = (partida.getUsuario() != null) ? partida.getUsuario().getUsername() : "Anónimo";

                    // Extraer iniciales (Ej. Diego Z -> DZ)
                    String iniciales = obtenerIniciales(nombre);

                    Label avatar = new Label(iniciales);
                    avatar.getStyleClass().add("avatar-circle");

                    Label lblNombre = new Label(nombre);
                    lblNombre.getStyleClass().add("player-name-text");

                    cellBox.getChildren().addAll(avatar, lblNombre);
                    setGraphic(cellBox);
                }
            }
        });

        // --- COLUMNA 3: DIFICULTAD (Pill / Badge de color) ---
        TableColumn<Partida, String> colDificultad = new TableColumn<>("DIFICULTAD");
        colDificultad.setCellValueFactory(cellData -> {
            if (cellData.getValue().getDificultad() != null) {
                return new ReadOnlyObjectWrapper<>(cellData.getValue().getDificultad().getNombre());
            }
            return new ReadOnlyObjectWrapper<>("-");
        });
        colDificultad.setCellFactory(col -> new TableCell<Partida, String>() {
            @Override
            protected void updateItem(String dif, boolean empty) {
                super.updateItem(dif, empty);
                if (empty || dif == null) {
                    setGraphic(null);
                } else {
                    Label badge = new Label(dif);
                    badge.getStyleClass().add("badge-dificultad");

                    // Clases dinámicas según la dificultad
                    if ("Fácil".equalsIgnoreCase(dif)) badge.getStyleClass().add("badge-facil");
                    else if ("Medio".equalsIgnoreCase(dif)) badge.getStyleClass().add("badge-medio");
                    else if ("Difícil".equalsIgnoreCase(dif)) badge.getStyleClass().add("badge-dificil");

                    HBox container = new HBox(badge);
                    container.setAlignment(Pos.CENTER_LEFT);
                    setGraphic(container);
                }
            }
        });

        // --- COLUMNA 4: TIEMPO ---
        TableColumn<Partida, String> colTiempo = new TableColumn<>("TIEMPO");
        colTiempo.setCellValueFactory(cellData -> {
            Duration duracion = cellData.getValue().getTiempo();
            if (duracion != null) {
                long totalSegundos = duracion.toSeconds();
                long minutos = totalSegundos / 60;
                long segundos = totalSegundos % 60;
                return new ReadOnlyObjectWrapper<>(String.format("%02d:%02d", minutos, segundos));
            }
            return new ReadOnlyObjectWrapper<>("00:00");
        });
        colTiempo.setCellFactory(col -> new TableCell<Partida, String>() {
            @Override
            protected void updateItem(String time, boolean empty) {
                super.updateItem(time, empty);
                if (empty || time == null) {
                    setText(null);
                } else {
                    setText(time);
                    getStyleClass().add("time-text-bold");
                }
            }
        });

        // Anchos proporcionales
        colPosicion.setMaxWidth(1f * Integer.MAX_VALUE * 10);  // 10%
        colJugador.setMaxWidth(1f * Integer.MAX_VALUE * 45);   // 45%
        colDificultad.setMaxWidth(1f * Integer.MAX_VALUE * 25); // 25%
        colTiempo.setMaxWidth(1f * Integer.MAX_VALUE * 20);     // 20%

        tabla.getColumns().addAll(colPosicion, colJugador, colDificultad, colTiempo);
        tabla.setItems(partidasObservable);

        // Lógica de filtrado
        filterGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                ToggleButton selected = (ToggleButton) newVal;
                filtrarTabla(selected.getText());
            }
        });

        getChildren().addAll(headerArea, tabla);
    }

    private void filtrarTabla(String filtro) {
        if ("Todas".equalsIgnoreCase(filtro)) {
            tabla.setItems(partidasObservable);
        } else {
            ObservableList<Partida> filtradas = partidasObservable.filtered(p ->
                p.getDificultad() != null && filtro.equalsIgnoreCase(p.getDificultad().getNombre())
            );
            tabla.setItems(filtradas);
        }
    }

    private String obtenerIniciales(String nombre) {
        if (nombre == null || nombre.isBlank()) return "?";
        String[] partes = nombre.trim().split("\\s+");
        if (partes.length >= 2) {
            return (partes[0].substring(0, 1) + partes[1].substring(0, 1)).toUpperCase();
        }
        return nombre.length() >= 2 ? nombre.substring(0, 2).toUpperCase() : nombre.toUpperCase();
    }

}
