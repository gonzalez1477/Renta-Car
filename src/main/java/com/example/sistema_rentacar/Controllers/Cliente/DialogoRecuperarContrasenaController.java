package com.example.sistema_rentacar.Controllers.Cliente;

import com.example.sistema_rentacar.Modelos.Cliente;
import com.example.sistema_rentacar.Modelos.CodigoRecuperacion;
import com.example.sistema_rentacar.Repository.ClienteRepository;
import com.example.sistema_rentacar.Repository.RecuperacionRepository;
import com.example.sistema_rentacar.Servicios.EmailService;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class DialogoRecuperarContrasenaController {

    @FXML private TextField txtEmail;
    @FXML private Label lblMensajeEmail;
    @FXML private HBox hboxEnviando;
    @FXML private Button btnEnviarCodigo;
    @FXML private Button btnCancelar;

    private ClienteRepository clienteRepo;
    private RecuperacionRepository recuperacionRepo;

    @FXML
    public void initialize() {
        System.out.println("DialogoRecuperarContrasenaController inicializado");
        clienteRepo = new ClienteRepository();
        recuperacionRepo = new RecuperacionRepository();

        // Configurar validación en tiempo real del email
        txtEmail.textProperty().addListener((observable, oldValue, newValue) -> {
            lblMensajeEmail.setVisible(false);
        });
    }

    @FXML
    private void handleEnviarCodigo() {
        String email = txtEmail.getText().trim();

        // Validar email
        if (email.isEmpty()) {
            mostrarError("Ingresa tu correo electrónico");
            txtEmail.requestFocus();
            return;
        }

        if (!validarFormatoEmail(email)) {
            mostrarError("Formato de correo inválido");
            txtEmail.requestFocus();
            return;
        }

        // Verificar que el email exista en la base de datos
        Cliente cliente = clienteRepo.obtenerPorEmail(email);

        if (cliente == null) {
            mostrarError("No existe una cuenta con este correo electrónico");
            txtEmail.requestFocus();
            return;
        }

        // Deshabilitar botones y mostrar indicador
        btnEnviarCodigo.setDisable(true);
        btnCancelar.setDisable(true);
        hboxEnviando.setVisible(true);
        hboxEnviando.setManaged(true);

        // Generar y enviar código en un hilo separado
        Task<Boolean> enviarTask = new Task<Boolean>() {
            CodigoRecuperacion codigo;

            @Override
            protected Boolean call() throws Exception {
                // Generar código de recuperación
                codigo = recuperacionRepo.crearCodigoRecuperacion(cliente.getIdCliente());

                if (codigo == null) {
                    return false;
                }

                // Enviar email con el código
                return EmailService.enviarCodigoRecuperacion(
                        email,
                        cliente.getNombre(),
                        codigo.getCodigo()
                );
            }

            @Override
            protected void succeeded() {
                hboxEnviando.setVisible(false);
                hboxEnviando.setManaged(false);
                btnEnviarCodigo.setDisable(false);
                btnCancelar.setDisable(false);

                if (getValue()) {
                    // Éxito - Mostrar diálogo de validación
                    mostrarDialogoValidacion(email);
                } else {
                    mostrarError("No se pudo enviar el código. Intenta nuevamente.");
                }
            }

            @Override
            protected void failed() {
                hboxEnviando.setVisible(false);
                hboxEnviando.setManaged(false);
                btnEnviarCodigo.setDisable(false);
                btnCancelar.setDisable(false);

                mostrarError("Error al procesar la solicitud: " + getException().getMessage());
            }
        };

        new Thread(enviarTask).start();
    }

    private void mostrarDialogoValidacion(String email) {
        try {
            System.out.println("Intentando cargar DialogoValidarCodigo.fxml...");
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/sistema_rentacar/Views/cliente/DialogoValidarCodigo.fxml")
            );
            Parent root = loader.load();
            System.out.println("FXML cargado exitosamente");

            DialogoValidarCodigoController controller = loader.getController();
            controller.setEmail(email);

            System.out.println("Email configurado en el controlador: " + email);

            Stage dialogoStage = new Stage();
            dialogoStage.setTitle("Validar Código");
            dialogoStage.initOwner(btnEnviarCodigo.getScene().getWindow());
            dialogoStage.initModality(Modality.APPLICATION_MODAL);
            dialogoStage.setResizable(false);

            // Crear una nueva Scene con dimensiones específicas
            Scene scene = new Scene(root, 500, 650);
            dialogoStage.setScene(scene);

            System.out.println("Stage configurado, mostrando diálogo...");

            // Mostrar el diálogo y esperar
            dialogoStage.showAndWait();

            System.out.println("Diálogo cerrado");

            // Cerrar la ventana
            cerrarDialogo();

        } catch (IOException e) {
            System.err.println("ERROR al cargar DialogoValidarCodigo.fxml:");
            e.printStackTrace();
            mostrarError("Error al cargar el siguiente paso: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("ERROR inesperado:");
            e.printStackTrace();
            mostrarError("Error inesperado: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancelar() {
        cerrarDialogo();
    }

    private void mostrarError(String mensaje) {
        lblMensajeEmail.setText(mensaje);
        lblMensajeEmail.setVisible(true);
    }

    private boolean validarFormatoEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(regex);
    }

    private void cerrarDialogo() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }
}