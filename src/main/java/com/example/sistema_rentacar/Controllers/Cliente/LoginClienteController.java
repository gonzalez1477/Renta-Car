package com.example.sistema_rentacar.Controllers.Cliente;

import com.example.sistema_rentacar.Modelos.Cliente;
import com.example.sistema_rentacar.Repository.ClienteRepository;
import com.example.sistema_rentacar.Utilidades.CambiarScena;
import javafx.animation.RotateTransition;
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
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javafx.util.Duration;

import java.io.IOException;

public class LoginClienteController {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtContrasena;
    @FXML private Label lblError;
    @FXML private Button btnLogin;
    @FXML private Hyperlink linkRegistro;
    @FXML private Hyperlink linkInvitado;
    @FXML private Hyperlink linkVolver;
    @FXML private ImageView logoImage;
    @FXML private HBox hboxCargando;

    @FXML private ImageView userIcon;



    @FXML
    private TextField txtContrasenaVisible;

    @FXML
    private Button btnToggleContrasena;

    // Unicode de Font Awesome 7 Free Solid
    private final String ICON_SHOW = "\uf06e";  // ojo abierto
    private final String ICON_HIDE = "\uf070";  // ojo tachado


    private ClienteRepository clienteRepository;

    @FXML
    public void initialize() {
        clienteRepository = new ClienteRepository();
        lblError.setVisible(false);
        hboxCargando.setVisible(false);

        // Cargar logo
        try {
            Image logo = new Image(
                    getClass().getResourceAsStream("/com/example/sistema_rentacar/images/logo/logo1.png")
            );
            logoImage.setImage(logo);
        } catch (Exception e) {
            System.err.println("No se pudo cargar el logo: " + e.getMessage());
        }

        // Animación de giro para el ícono de usuario ( no implementar en el logo porque se bugueda xd) jaja
        if (userIcon != null) {
            RotateTransition rotation = new RotateTransition(Duration.seconds(0.6), userIcon);
            rotation.setByAngle(360);
            rotation.setCycleCount(1);
            rotation.setAutoReverse(false);

            userIcon.setOnMouseEntered(event -> rotation.playFromStart());
        }

        // Login al presionar Enter
        txtContrasenaVisible.setOnAction(event -> handleLogin());
        txtContrasena.setOnAction(event -> handleLogin());


        btnToggleContrasena.setFont(Font.font("Font Awesome 7 Free Solid", 16));

    }

    @FXML
    private void toggleContrasena() {

        if (txtContrasenaVisible.isVisible()) {
            // Cambiar a PasswordField
            txtContrasena.setText(txtContrasenaVisible.getText());
            txtContrasenaVisible.setVisible(false);
            txtContrasenaVisible.setManaged(false);

            txtContrasena.setVisible(true);
            txtContrasena.setManaged(true);

            // Cambiar icono (mostrar)
            btnToggleContrasena.setText(ICON_SHOW);

        } else {
            // Cambiar a TextField
            txtContrasenaVisible.setText(txtContrasena.getText());
            txtContrasena.setVisible(false);
            txtContrasena.setManaged(false);

            txtContrasenaVisible.setVisible(true);
            txtContrasenaVisible.setManaged(true);

            // Cambiar icono (ocultar)
            btnToggleContrasena.setText(ICON_HIDE);
        }
    }

    @FXML
    private void handleLogin() {
        String usuario = txtUsuario.getText().trim();

        // CORREGIR AQUÍ: Tomar la contraseña del campo visible actualmente
        String contrasena;
        if (txtContrasenaVisible.isVisible()) {
            contrasena = txtContrasenaVisible.getText();
        } else {
            contrasena = txtContrasena.getText();
        }

        if (usuario.isEmpty() || contrasena.isEmpty()) {
            mostrarError("Por favor complete todos los campos");
            return;
        }

        deshabilitarControles(true);
        lblError.setVisible(false);
        hboxCargando.setVisible(true);

        Task<Cliente> loginTask = new Task<Cliente>() {
            @Override
            protected Cliente call() throws Exception {
                return clienteRepository.autenticar(usuario, contrasena);
            }
        };

        loginTask.setOnSucceeded(event -> {
            Cliente cliente = loginTask.getValue();

            if (cliente != null) {
                abrirCatalogo(cliente);
            } else {
                Platform.runLater(() -> {
                    deshabilitarControles(false);
                    hboxCargando.setVisible(false);
                    mostrarError("Usuario o contraseña incorrectos");
                });
            }
        });

        loginTask.setOnFailed(event -> {
            Platform.runLater(() -> {
                deshabilitarControles(false);
                hboxCargando.setVisible(false);
                mostrarError("Error al procesar la solicitud");
            });
        });

        new Thread(loginTask).start();
    }

    @FXML
    private void handleRegistro() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/sistema_rentacar/Views/cliente/RegistroCliente.fxml")
            );
            Parent root = loader.load();

            Stage stage = (Stage) btnLogin.getScene().getWindow();
            CambiarScena.cambiar(stage, root, "Registro de Cliente - VSB Renta Car");

        } catch (Exception e) {
            System.err.println("Error al abrir registro: " + e.getMessage());
            e.printStackTrace();
            mostrarError("Error al abrir el registro");
        }
    }

    @FXML
    private void handleInvitado() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/sistema_rentacar/Views/cliente/CatalogoCliente.fxml")
            );
            Parent root = loader.load();

            CatalogoClienteController controller = loader.getController();
            controller.setModoInvitado();

            Stage stage = (Stage) btnLogin.getScene().getWindow();
            CambiarScena.cambiarMaximizado(stage, root, "Catálogo - VSB Renta Car");

        } catch (Exception e) {
            System.err.println("Error al abrir catálogo: " + e.getMessage());
            e.printStackTrace();
            mostrarError("Error al abrir el catálogo");
        }
    }

    @FXML
    private void handleOlvideContrasena() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/sistema_rentacar/Views/Cliente/DialogoRecuperarContrasena.fxml")
            );
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Recuperar Contraseña");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();

        } catch (IOException e) {
            System.err.println("Error al abrir recuperación de contraseña: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleVolver() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/sistema_rentacar/Views/Inicio-View.fxml")
            );
            Parent root = loader.load();

            Stage stage = (Stage) btnLogin.getScene().getWindow();
            CambiarScena.cambiar(stage, root, "Inicio - VSB Renta Car");

        } catch (Exception e) {
            System.err.println("Error al volver: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void abrirCatalogo(Cliente cliente) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/sistema_rentacar/Views/cliente/CatalogoCliente.fxml")
            );
            Parent root = loader.load();

            CatalogoClienteController controller = loader.getController();
            controller.setDatosCliente(cliente.getIdCliente(), cliente.getNombreCompleto());

            Stage stage = (Stage) btnLogin.getScene().getWindow();
            CambiarScena.cambiarMaximizado(stage, root, "Catálogo - VSB Renta Car");

        } catch (Exception e) {
            System.err.println("Error al abrir catálogo: " + e.getMessage());
            e.printStackTrace();
            mostrarError("Error al abrir el catálogo");
        }
    }

    private void deshabilitarControles(boolean deshabilitar) {
        txtUsuario.setDisable(deshabilitar);
        txtContrasena.setDisable(deshabilitar);
        btnLogin.setDisable(deshabilitar);
        linkRegistro.setDisable(deshabilitar);
        linkInvitado.setDisable(deshabilitar);
        //linkVolver.setDisable(deshabilitar);
    }

    private void mostrarError(String mensaje) {
        lblError.setText(mensaje);
        lblError.setVisible(true);
    }
}

