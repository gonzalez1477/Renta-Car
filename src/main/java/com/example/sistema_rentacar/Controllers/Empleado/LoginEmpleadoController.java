package com.example.sistema_rentacar.Controllers.Empleado;

import com.example.sistema_rentacar.Controllers.Empleado.DashboardEmpleadoController;
import com.example.sistema_rentacar.Modelos.Empleado;
import com.example.sistema_rentacar.Repository.EmpleadoRepository;
import com.example.sistema_rentacar.Utilidades.CambiarScena;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class LoginEmpleadoController {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtContrasena;
    @FXML private Label lblError;
    @FXML private Button btnLogin;
    @FXML private Hyperlink linkVolver;
    @FXML private ImageView logoImage;
    @FXML private HBox hboxCargando;

    @FXML private TextField txtContrasenaVisible;
    @FXML private Button btnToggleContrasena;

    // Unicode Font Awesome
    private final String ICON_SHOW = "\uf06e";  // ojo abierto
    private final String ICON_HIDE = "\uf070";  // ojo tachado

    private EmpleadoRepository empleadoRepository;

    @FXML
    public void initialize() {
        empleadoRepository = new EmpleadoRepository();
        lblError.setVisible(false);
        hboxCargando.setVisible(false);

        // Cargar logo
        try {
            Image logo = new Image(getClass().getResourceAsStream("/com/example/sistema_rentacar/images/logo/logo1.png"));
            logoImage.setImage(logo);
        } catch (Exception e) {
            System.err.println("No se pudo cargar el logo: " + e.getMessage());
        }

        // Login con Enter
        txtContrasena.setOnAction(event -> handleLogin());
        txtContrasenaVisible.setOnAction(event -> handleLogin());

        btnToggleContrasena.setFont(Font.font("Font Awesome 7 Free Solid", 16));
    }

    @FXML
    private void toggleContrasena() {

        if (txtContrasenaVisible.isVisible()) {

            txtContrasena.setText(txtContrasenaVisible.getText());
            txtContrasenaVisible.setVisible(false);
            txtContrasenaVisible.setManaged(false);

            txtContrasena.setVisible(true);
            txtContrasena.setManaged(true);

            btnToggleContrasena.setText(ICON_SHOW);

        } else {

            txtContrasenaVisible.setText(txtContrasena.getText());
            txtContrasena.setVisible(false);
            txtContrasena.setManaged(false);

            txtContrasenaVisible.setVisible(true);
            txtContrasenaVisible.setManaged(true);

            btnToggleContrasena.setText(ICON_HIDE);
        }
    }

    @FXML
    private void handleLogin() {
        String usuario = txtUsuario.getText().trim();
        String contrasena = txtContrasena.getText();

        if (usuario.isEmpty() || contrasena.isEmpty()) {
            mostrarError("Por favor complete todos los campos");
            return;
        }

        deshabilitarControles(true);
        lblError.setVisible(false);
        hboxCargando.setVisible(true);

        Task<Empleado> loginTask = new Task<Empleado>() {
            @Override
            protected Empleado call() throws Exception {
                return empleadoRepository.autenticar(usuario, contrasena);
            }
        };

        loginTask.setOnSucceeded(event -> {
            Empleado empleado = loginTask.getValue();

            if (empleado != null) {

                if (!empleado.isActivo()) {
                    Platform.runLater(() -> {
                        deshabilitarControles(false);
                        hboxCargando.setVisible(false);
                        mostrarAlertaCuentaDesactivada();
                    });
                    return;
                }

                abrirDashboardEmpleado(empleado);

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

    private void abrirDashboardEmpleado(Empleado empleado) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/sistema_rentacar/Views/empleado/DashboardEmpleado.fxml")
            );
            Parent root = loader.load();

            DashboardEmpleadoController controller = loader.getController();
            controller.setDatosEmpleado(empleado);

            Stage stage = (Stage) btnLogin.getScene().getWindow();

            CambiarScena.cambiarMaximizado(stage, root, "Panel de Empleado - VSB Renta Car");

        } catch (Exception e) {
            System.err.println("Error al abrir dashboard: " + e.getMessage());
            e.printStackTrace();
            mostrarError("Error al abrir el dashboard");
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

            CambiarScena.cambiar(stage, root, "VSB Renta Car");

        } catch (Exception e) {
            System.err.println("Error al volver: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void mostrarAlertaCuentaDesactivada() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Cuenta Desactivada");
        alert.setHeaderText("Acceso Denegado");
        alert.setContentText(
                "Su cuenta ha sido desactivada por un administrador.\n\n" +
                        "Si considera que es un error, comuníquese con recursos humanos."
        );

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-font-size: 14px;");

        alert.showAndWait();

        txtContrasena.clear();
        txtUsuario.requestFocus();
    }

    private void deshabilitarControles(boolean deshabilitar) {
        txtUsuario.setDisable(deshabilitar);
        txtContrasena.setDisable(deshabilitar);
        btnLogin.setDisable(deshabilitar);
        linkVolver.setDisable(deshabilitar);
    }

    private void mostrarError(String mensaje) {
        lblError.setText(mensaje);
        lblError.setVisible(true);
    }
}
