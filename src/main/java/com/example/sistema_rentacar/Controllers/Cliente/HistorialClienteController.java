package com.example.sistema_rentacar.Controllers.Cliente;

import com.example.sistema_rentacar.Repository.AlquilerRepository;
import com.example.sistema_rentacar.Modelos.Alquiler;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class HistorialClienteController {

    @FXML private Label lblCliente;
    @FXML private TableView<Alquiler> tableHistorial;
    @FXML private TableColumn<Alquiler, Integer> colId;
    @FXML private TableColumn<Alquiler, String> colVehiculo;
    @FXML private TableColumn<Alquiler, String> colPlaca;
    @FXML private TableColumn<Alquiler, Timestamp> colFechaInicio;
    @FXML private TableColumn<Alquiler, Timestamp> colFechaFin;
    @FXML private TableColumn<Alquiler, Integer> colDias;
    @FXML private TableColumn<Alquiler, Double> colCosto;
    @FXML private TableColumn<Alquiler, String> colEstado;
    @FXML private Button btnFinalizar;
    @FXML private Button btnVolver;
    @FXML private Button btnRefrescar;

    @FXML private Label lblTotalAlquileres;
    @FXML private Label lblFinalizados;
    @FXML private Label lblEnCurso;
    @FXML private Label lblTotalGastado;
    @FXML private Label lblCantidadRegistros;

    private int idCliente;
    private String nombreCliente;
    private AlquilerRepository alquilerDAO;
    private ObservableList<Alquiler> alquileres;

    private boolean modoEmpleado = false;
    private Stage ventanaAnterior = null;

    @FXML
    public void initialize() {
        alquilerDAO = new AlquilerRepository();
        configurarTabla();
        // No llamar actualizarEstadisticas aquí porque la tabla está vacía
    }

    public void setDatosCliente(int idCliente, String nombreCliente) {
        this.idCliente = idCliente;
        this.nombreCliente = nombreCliente;
        this.modoEmpleado = false;
        lblCliente.setText("Historial de Alquileres - " + nombreCliente);
        configurarBotones();
        cargarHistorial();
    }

    public void setDatosClienteDesdeEmpleado(int idCliente, String nombreCliente) {
        this.idCliente = idCliente;
        this.nombreCliente = nombreCliente;
        this.modoEmpleado = true;
        lblCliente.setText("Historial de Alquileres - " + nombreCliente);
        configurarBotones();
        cargarHistorial();
    }

    private void configurarBotones() {
        if (modoEmpleado) {
            btnVolver.setText("Cerrar");
            btnRefrescar.setVisible(true);
            btnRefrescar.setManaged(true);
        } else {
            btnVolver.setText("Volver al Catálogo");
            btnRefrescar.setVisible(true);
            btnRefrescar.setManaged(true);
        }
    }

    private void configurarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idAlquiler"));
        colVehiculo.setCellValueFactory(new PropertyValueFactory<>("vehiculo"));
        colPlaca.setCellValueFactory(new PropertyValueFactory<>("placa"));
        colDias.setCellValueFactory(new PropertyValueFactory<>("diasAlquiler"));
        colCosto.setCellValueFactory(new PropertyValueFactory<>("costoTotal"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        // Formatear fecha inicio
        colFechaInicio.setCellValueFactory(new PropertyValueFactory<>("fechaInicio"));
        colFechaInicio.setCellFactory(column -> new TableCell<Alquiler, Timestamp>() {
            private SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            @Override
            protected void updateItem(Timestamp item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(format.format(item));
                }
            }
        });

        // Formatear fecha fin
        colFechaFin.setCellValueFactory(new PropertyValueFactory<>("fechaFinReal"));
        colFechaFin.setCellFactory(column -> new TableCell<Alquiler, Timestamp>() {
            private SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            @Override
            protected void updateItem(Timestamp item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("En curso");
                } else {
                    setText(format.format(item));
                }
            }
        });

        // Formatear costo
        colCosto.setCellFactory(column -> new TableCell<Alquiler, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", item));
                }
            }
        });

        // Formatear estado con colores
        colEstado.setCellFactory(column -> new TableCell<Alquiler, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    switch (item) {
                        case "Activo":
                            setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                            break;
                        case "Por Vencer":
                            setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;");
                            break;
                        case "Retrasado":
                            setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                            break;
                        case "Finalizado":
                            setStyle("-fx-text-fill: #3498db;");
                            break;
                        default:
                            setStyle("-fx-text-fill: #95a5a6;");
                            break;
                    }
                }
            }
        });

        tableHistorial.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    btnFinalizar.setDisable(newSelection == null || !newSelection.puedeFinalizarse());
                }
        );
    }

    private void cargarHistorial() {
        List<Alquiler> lista = alquilerDAO.obtenerPorCliente(idCliente);
        alquileres = FXCollections.observableArrayList(lista);
        tableHistorial.setItems(alquileres);

        if (lista.isEmpty()) {
            tableHistorial.setPlaceholder(new Label("No hay alquileres registrados"));
        }

        // ACTUALIZAR LAS ESTADÍSTICAS DESPUÉS DE CARGAR LOS DATOS
        actualizarEstadisticas();
    }

    @FXML
    private void handleFinalizarAlquiler() {
        Alquiler alquilerSeleccionado = tableHistorial.getSelectionModel().getSelectedItem();

        if (alquilerSeleccionado == null) {
            mostrarAdvertencia("Seleccione un alquiler para finalizar");
            return;
        }

        if (!alquilerSeleccionado.puedeFinalizarse()) {
            mostrarAdvertencia("Este alquiler no puede ser finalizado. Estado actual: " +
                    alquilerSeleccionado.getEstado());
            return;
        }

        Timestamp fechaFinEstimadaTS = alquilerSeleccionado.getFechaFinEstimada();
        LocalDate fechaFinEstimada = fechaFinEstimadaTS.toLocalDateTime().toLocalDate();
        LocalDate hoy = LocalDate.now();

        if (hoy.isBefore(fechaFinEstimada)) {
            abrirDialogoEntregaAnticipada(alquilerSeleccionado);
        } else {
            abrirDialogoFinalizarAlquiler(alquilerSeleccionado);
        }
    }

    private void abrirDialogoEntregaAnticipada(Alquiler alquiler) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/sistema_rentacar/Views/Cliente/DialogoEntregaAnticipada.fxml")
            );
            Parent root = loader.load();

            DialogoEntregaAnticipadaController controller = loader.getController();
            controller.setDatos(alquiler, (motivo) -> {
                handleRefrescar();
                mostrarNotificacion("Vehículo entregado anticipadamente");
            });

            Stage stage = new Stage();
            stage.setTitle("Entrega Anticipada de Vehículo");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(btnFinalizar.getScene().getWindow());
            stage.setResizable(false);
            stage.showAndWait();

        } catch (IOException e) {
            mostrarError("Error al cargar el diálogo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void abrirDialogoFinalizarAlquiler(Alquiler alquiler) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/sistema_rentacar/Views/Cliente/DialogoFinalizarAlquiler.fxml")
            );
            Parent root = loader.load();

            DialogoFinalizarAlquilerController controller = loader.getController();
            controller.setDatos(alquiler, (esRetraso) -> {
                handleRefrescar();

                if (esRetraso) {
                    mostrarNotificacion("Alquiler finalizado con penalización por retraso");
                } else {
                    mostrarNotificacion("Vehículo devuelto exitosamente");
                }
            });

            Stage stage = new Stage();
            stage.setTitle("Finalizar Alquiler");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(btnFinalizar.getScene().getWindow());
            stage.setResizable(false);
            stage.showAndWait();

        } catch (IOException e) {
            mostrarError("Error al cargar el diálogo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void mostrarAdvertencia(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Advertencia");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarNotificacion(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notificación");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.show();

        new Thread(() -> {
            try {
                Thread.sleep(2000);
                Platform.runLater(() -> alert.close());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    private void handleRefrescar() {
        cargarHistorial();
        // Las estadísticas se actualizan automáticamente en cargarHistorial()
    }

    @FXML
    private void handleVolver() {
        if (modoEmpleado) {
            Stage stage = (Stage) btnVolver.getScene().getWindow();
            stage.close();
        } else {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sistema_rentacar/Views/cliente/CatalogoCliente.fxml"));
                Parent root = loader.load();

                CatalogoClienteController controller = loader.getController();
                controller.setDatosCliente(idCliente, nombreCliente);

                Stage stage = (Stage) btnVolver.getScene().getWindow();
                stage.setScene(new Scene(root, 1200, 800));
                stage.setTitle("Catálogo - Renta Car");

            } catch (Exception e) {
                System.err.println("Error al volver: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void actualizarEstadisticas() {
        int total = tableHistorial.getItems().size();
        long finalizados = tableHistorial.getItems().stream()
                .filter(a -> "Finalizado".equals(a.getEstado()))
                .count();
        long enCurso = tableHistorial.getItems().stream()
                .filter(a -> "Activo".equals(a.getEstado()) ||
                        "Por Vencer".equals(a.getEstado()) ||
                        "Retrasado".equals(a.getEstado()))
                .count();
        double totalGastado = tableHistorial.getItems().stream()
                .mapToDouble(Alquiler::getCostoTotal)
                .sum();

        lblTotalAlquileres.setText(String.valueOf(total));
        lblFinalizados.setText(String.valueOf(finalizados));
        lblEnCurso.setText(String.valueOf(enCurso));
        lblTotalGastado.setText(String.format("$%.2f", totalGastado));
        lblCantidadRegistros.setText(total + " registros");
    }

    private void mostrarExito(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}