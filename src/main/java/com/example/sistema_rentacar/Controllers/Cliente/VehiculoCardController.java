package com.example.sistema_rentacar.Controllers.Cliente;

import com.example.sistema_rentacar.Repository.AlquilerRepository;
import com.example.sistema_rentacar.Modelos.Vehiculo;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;

public class VehiculoCardController {

    @FXML private ImageView carImage;
    @FXML private Label availabilityBadge;
    @FXML private Label carModel;
    @FXML private Label carTransmission;
    @FXML private Label carPassengers;
    @FXML private Label carFuel;
    @FXML private Label carAC;
    @FXML private Label carPrice;
    @FXML private Button rentButton;

    private Vehiculo vehiculo;
    private int idCliente;
    private boolean esInvitado;
    private CatalogoClienteController catalogoController;
    private AlquilerRepository alquilerDAO;

    @FXML
    public void initialize() {
        alquilerDAO = new AlquilerRepository();
    }

    public void setDatos(Vehiculo vehiculo, int idCliente, boolean esInvitado, CatalogoClienteController catalogoController) {
        this.vehiculo = vehiculo;
        this.idCliente = idCliente;
        this.esInvitado = esInvitado;
        this.catalogoController = catalogoController;
        actualizarUI();
    }

    private void actualizarUI() {
        carModel.setText(vehiculo.getNombreCompleto());
        carTransmission.setText("🔧 " + vehiculo.getTransmision());
        carPassengers.setText("👥 " + vehiculo.getNumeroPassajeros() + " pasajeros");
        carFuel.setText("⛽ " + vehiculo.getTipoCombustible());
        carAC.setText(vehiculo.isTieneAireAcondicionado() ? "❄ A/C" : "");
        carPrice.setText(String.format("$%.2f", vehiculo.getTarifaPorDia()));

        cargarImagenAsync();

        // Configurar disponibilidad según el estado del vehículo
        String estado = vehiculo.getEstado(); // Asegúrate de que este método exista en tu clase Vehiculo

        if ("Disponible".equalsIgnoreCase(estado)) {
            availabilityBadge.setText("DISPONIBLE");
            availabilityBadge.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; " +
                    "-fx-padding: 5 10; -fx-background-radius: 5; -fx-font-size: 10px; -fx-font-weight: bold;");
            rentButton.setDisable(false);

            if (esInvitado) {
                rentButton.setText("Regístrate para Alquilar");
            } else {
                rentButton.setText("Alquilar Ahora");
            }
        } else if ("Mantenimiento".equalsIgnoreCase(estado)) {
            availabilityBadge.setText("EN MANTENIMIENTO");
            availabilityBadge.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; " +
                    "-fx-padding: 5 10; -fx-background-radius: 5; -fx-font-size: 10px; -fx-font-weight: bold;");
            rentButton.setDisable(true);
            rentButton.setText("En Mantenimiento");
            rentButton.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; " +
                    "-fx-font-size: 15px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-opacity: 0.7;");
        } else {
            // Estado "Alquilado" o cualquier otro
            availabilityBadge.setText("ALQUILADO");
            availabilityBadge.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; " +
                    "-fx-padding: 5 10; -fx-background-radius: 5; -fx-font-size: 10px; -fx-font-weight: bold;");
            rentButton.setDisable(true);
            rentButton.setText("No Disponible");
            rentButton.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; " +
                    "-fx-font-size: 15px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-opacity: 0.7;");
        }
    }


    private void cargarImagenAsync() {
        String imagePath = vehiculo.getImagenUrl();

        if (imagePath == null || imagePath.isEmpty()) {
            return;
        }

        // Primero intentar obtener del caché
        Image cachedImage = CatalogoClienteController.getImageFromCache(imagePath);
        if (cachedImage != null) {
            carImage.setImage(cachedImage);
            return;
        }

        // Si no está en caché, cargar en background
        Task<Image> loadImageTask = new Task<>() {
            @Override
            protected Image call() {
                try {
                    // Intentar cargar desde resources
                    var resource = getClass().getResourceAsStream(imagePath);
                    if (resource != null) {
                        // Pre-escalar la imagen al tamaño deseado para ahorrar memoria
                        return new Image(resource, 270, 160, true, true);
                    } else {
                        // Intentar cargar desde archivo local
                        File file = new File("src/main/resources" + imagePath);
                        if (file.exists()) {
                            return new Image(file.toURI().toString(), 270, 160, true, true);
                        } else {
                            System.err.println("Imagen no encontrada: " + file.getAbsolutePath());
                            return null;
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error cargando imagen: " + imagePath);
                    return null;
                }
            }
        };

        loadImageTask.setOnSucceeded(event -> {
            Image image = loadImageTask.getValue();
            if (image != null) {
                Platform.runLater(() -> carImage.setImage(image));
            }
        });

        loadImageTask.setOnFailed(event -> {
            System.err.println("Error en carga de imagen: " + loadImageTask.getException());
        });

        // Ejecutar en thread pool de JavaFX
        new Thread(loadImageTask).start();
    }

    @FXML
    private void handleRentButton() {
        if (esInvitado) {
            mostrarAlertaConfirmacion(
                    "Registro Requerido",
                    "Debes iniciar sesión para poder alquilar vehículos.",
                    () -> cargarVistaLoginCliente()
            );
            return;
        }

        if (alquilerDAO.tieneAlquileresActivos(idCliente)) {
            mostrarAlerta("Alquiler Activo",
                    "Ya tienes un alquiler activo. Debes finalizarlo antes de alquilar otro vehículo.",
                    Alert.AlertType.WARNING);
            return;
        }

        abrirDialogoAlquiler();
    }

    private void abrirDialogoAlquiler() {
        if (idCliente <= 0) {
            mostrarAlerta("Error de sesión",
                    "No se ha identificado el cliente. Inicia sesión nuevamente.",
                    Alert.AlertType.ERROR);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sistema_rentacar/Views/cliente/DialogoAlquiler.fxml"));
            Parent root = loader.load();

            DialogoAlquilerController controller = loader.getController();
            controller.setDatos(vehiculo, idCliente, catalogoController);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Confirmar Alquiler");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(new Scene(root));
            dialogStage.setResizable(false);
            dialogStage.showAndWait();

        } catch (Exception e) {
            System.err.println("Error al abrir diálogo de alquiler: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarAlertaConfirmacion(String titulo, String mensaje, Runnable accionAceptar) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle(null);
        alerta.setHeaderText(titulo);

        Label lblMensaje = new Label(mensaje);
        lblMensaje.setWrapText(true);
        lblMensaje.setStyle("-fx-font-size: 14px;");

        HBox contenido = new HBox(15);
        contenido.setStyle("-fx-padding: 10;");
        contenido.getChildren().add(lblMensaje);

        alerta.getDialogPane().setContent(contenido);

        ButtonType btnAceptar = new ButtonType("Continuar", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        alerta.getButtonTypes().setAll(btnAceptar, btnCancelar);

        DialogPane dialogPane = alerta.getDialogPane();

        dialogPane.setStyle(
                "-fx-background-color: white;" +
                        "-fx-border-color: #3498db;" +
                        "-fx-border-width: 2px;" +
                        "-fx-border-radius: 10px;" +
                        "-fx-background-radius: 10px;" +
                        "-fx-font-size: 14px;"
        );

        ImageView icono = new ImageView(
                new Image(getClass().getResourceAsStream(
                        "/com/example/sistema_rentacar/images/logo/logo.png"
                ), 45, 45, true, true)
        );
        dialogPane.setGraphic(icono);

        dialogPane.lookupButton(btnAceptar).setStyle(
                "-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;" +
                        "-fx-background-radius: 5;"
        );

        dialogPane.lookupButton(btnCancelar).setStyle(
                "-fx-background-color: #e0e0e0; -fx-text-fill: black; -fx-font-weight: bold;" +
                        "-fx-background-radius: 5;"
        );

        alerta.showAndWait().ifPresent(respuesta -> {
            if (respuesta == btnAceptar) {
                accionAceptar.run();
            }
        });
    }

    private void cargarVistaLoginCliente() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/example/sistema_rentacar/Views/cliente/LoginCliente.fxml"
            ));
            Parent root = loader.load();

            Stage stage = (Stage) rentButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login Cliente - Renta Car");
            stage.setMaximized(true);
            stage.centerOnScreen();

        } catch (Exception e) {
            System.err.println("Error al redirigir al inicio de sesión: " + e.getMessage());
            e.printStackTrace();
        }
    }
}