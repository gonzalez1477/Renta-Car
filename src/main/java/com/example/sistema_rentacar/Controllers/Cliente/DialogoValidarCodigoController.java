package com.example.sistema_rentacar.Controllers.Cliente;

import com.example.sistema_rentacar.Modelos.Cliente;
import com.example.sistema_rentacar.Modelos.CodigoRecuperacion;
import com.example.sistema_rentacar.Repository.ClienteRepository;
import com.example.sistema_rentacar.Repository.RecuperacionRepository;
import com.example.sistema_rentacar.Servicios.EmailService;
import com.example.sistema_rentacar.Utilidades.EncriptarContraseña;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class DialogoValidarCodigoController {

    @FXML private Label lblSubtitulo;
    @FXML private Label lblTiempoRestante;
    @FXML private HBox hboxTiempoRestante;
    @FXML private TextField txtCodigo;
    @FXML private PasswordField txtNuevaContrasena;
    @FXML private PasswordField txtConfirmarContrasena;
    @FXML private Label lblMensajeCodigo;
    @FXML private Label lblMensajeContrasena;
    @FXML private HBox hboxProcesando;
    @FXML private Button btnRestablecer;
    @FXML private Button btnCancelar;

    private String emailCliente;
    private ClienteRepository clienteRepo;
    private RecuperacionRepository recuperacionRepo;
    private Timeline timeline;
    private long tiempoExpiracion;

    @FXML
    public void initialize() {
        //System.out.println("DialogoValidarCodigoController INICIADO ");

        clienteRepo = new ClienteRepository();
        recuperacionRepo = new RecuperacionRepository();

        // Verificar que los componentes FXML se cargaron
        System.out.println("lblSubtitulo: " + (lblSubtitulo != null ? "OK" : "NULL"));
        System.out.println("lblTiempoRestante: " + (lblTiempoRestante != null ? "OK" : "NULL"));
        System.out.println("txtCodigo: " + (txtCodigo != null ? "OK" : "NULL"));
        System.out.println("txtNuevaContrasena: " + (txtNuevaContrasena != null ? "OK" : "NULL"));
        System.out.println("txtConfirmarContrasena: " + (txtConfirmarContrasena != null ? "OK" : "NULL"));
        System.out.println("btnRestablecer: " + (btnRestablecer != null ? "OK" : "NULL"));
        System.out.println("btnCancelar: " + (btnCancelar != null ? "OK" : "NULL"));

        // Limitar el código a 6 dígitos numéricos
        if (txtCodigo != null) {
            txtCodigo.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")) {
                    txtCodigo.setText(oldValue);
                } else if (newValue.length() > 6) {
                    txtCodigo.setText(newValue.substring(0, 6));
                }
                if (lblMensajeCodigo != null) {
                    lblMensajeCodigo.setVisible(false);
                }
            });
        }

        // Limpiar mensajes de error al escribir
        if (txtNuevaContrasena != null) {
            txtNuevaContrasena.textProperty().addListener((obs, old, newVal) -> {
                if (lblMensajeContrasena != null) {
                    lblMensajeContrasena.setVisible(false);
                }
            });
        }

        if (txtConfirmarContrasena != null) {
            txtConfirmarContrasena.textProperty().addListener((obs, old, newVal) -> {
                if (lblMensajeContrasena != null) {
                    lblMensajeContrasena.setVisible(false);
                }
            });
        }

        //System.out.println("DialogoValidarCodigoController COMPLETADO ");
    }

    public void setEmail(String email) {
        //System.out.println("setEmail() INICIADO con email: " + email + " ");

        this.emailCliente = email;

        if (lblSubtitulo != null) {
            lblSubtitulo.setText("Código enviado a: " + enmascarEmail(email));
            System.out.println("lblSubtitulo actualizado correctamente");
        } else {
            System.err.println("ERROR: lblSubtitulo es NULL en setEmail()");
        }

        // Configurar temporizador de 15 minutos
        tiempoExpiracion = System.currentTimeMillis() + (15 * 60 * 1000);
        iniciarTemporizador();

        //System.out.println("setEmail() COMPLETADO");
    }

    private void iniciarTemporizador() {
        System.out.println("Iniciando temporizador...");

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            actualizarTiempoRestante();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        //System.out.println("Temporizador iniciado");
    }

    private void actualizarTiempoRestante() {
        long ahora = System.currentTimeMillis();
        long diferencia = tiempoExpiracion - ahora;

        if (diferencia <= 0) {
            // Código expirado
            timeline.stop();
            if (lblTiempoRestante != null) {
                lblTiempoRestante.setText("¡Código expirado!");
                lblTiempoRestante.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            }
            if (hboxTiempoRestante != null) {
                hboxTiempoRestante.setStyle("-fx-background-color: #ffebee;");
            }
            if (btnRestablecer != null) {
                btnRestablecer.setDisable(true);
            }
        } else {
            long minutos = diferencia / (60 * 1000);
            long segundos = (diferencia % (60 * 1000)) / 1000;

            if (lblTiempoRestante != null) {
                lblTiempoRestante.setText(String.format("%02d:%02d minutos", minutos, segundos));

                // Cambiar color si queda poco tiempo
                if (minutos < 3) {
                    lblTiempoRestante.setStyle("-fx-text-fill: #d32f2f; -fx-font-weight: bold;");
                    if (hboxTiempoRestante != null) {
                        hboxTiempoRestante.setStyle("-fx-background-color: #ffebee;");
                    }
                }
            }
        }
    }

    @FXML
    private void handleRestablecer() {
        System.out.println("handleRestablecer() llamado");

        String codigo = txtCodigo.getText().trim();
        String nuevaContrasena = txtNuevaContrasena.getText();
        String confirmarContrasena = txtConfirmarContrasena.getText();

        // Validaciones
        if (codigo.isEmpty() || codigo.length() != 6) {
            mostrarErrorCodigo("Ingresa el código de 6 dígitos");
            txtCodigo.requestFocus();
            return;
        }

        if (nuevaContrasena.isEmpty()) {
            mostrarErrorContrasena("Ingresa una nueva contraseña");
            txtNuevaContrasena.requestFocus();
            return;
        }

        if (nuevaContrasena.length() < 6) {
            mostrarErrorContrasena("La contraseña debe tener al menos 6 caracteres");
            txtNuevaContrasena.requestFocus();
            return;
        }

        if (!nuevaContrasena.equals(confirmarContrasena)) {
            mostrarErrorContrasena("Las contraseñas no coinciden");
            txtConfirmarContrasena.requestFocus();
            return;
        }

        // Deshabilitar botones
        btnRestablecer.setDisable(true);
        btnCancelar.setDisable(true);
        hboxProcesando.setVisible(true);
        hboxProcesando.setManaged(true);

        // Procesar en hilo separado
        Task<Boolean> restablecerTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                // Validar código
                int idCliente = recuperacionRepo.validarCodigo(codigo);

                if (idCliente == -1) {
                    return false;
                }

                // Verificar que el cliente corresponda al email
                Cliente cliente = clienteRepo.obtenerPorEmail(emailCliente);

                if (cliente == null || cliente.getIdCliente() != idCliente) {
                    return false;
                }

                // Encriptar la contraseña
                String contrasenaEncriptada = EncriptarContraseña.encryptPassword(nuevaContrasena);

                System.out.println("Contraseña encriptada correctamente");

                // Actualizar contraseña con la versión encriptada
                boolean actualizado = clienteRepo.actualizarContrasena(idCliente, contrasenaEncriptada);

                if (actualizado) {
                    // Marcar código como usado
                    recuperacionRepo.marcarComoUsado(codigo);
                }

                return actualizado;
            }

            @Override
            protected void succeeded() {
                hboxProcesando.setVisible(false);
                hboxProcesando.setManaged(false);

                if (getValue()) {
                    // Éxito
                    timeline.stop();
                    mostrarExito();
                } else {
                    // Error
                    btnRestablecer.setDisable(false);
                    btnCancelar.setDisable(false);
                    mostrarErrorCodigo("Código inválido o expirado. Verifica e intenta nuevamente.");
                }
            }

            @Override
            protected void failed() {
                hboxProcesando.setVisible(false);
                hboxProcesando.setManaged(false);
                btnRestablecer.setDisable(false);
                btnCancelar.setDisable(false);

                mostrarErrorCodigo("Error al procesar: " + getException().getMessage());
            }
        };

        new Thread(restablecerTask).start();
    }

    @FXML
    private void handleSolicitarNuevoCodigo() {
        //System.out.println("handleSolicitarNuevoCodigo() llamado");

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Nuevo Código");
        confirmacion.setHeaderText("¿Solicitar un nuevo código?");
        confirmacion.setContentText("El código actual quedará inválido y recibirás uno nuevo.");

        if (confirmacion.showAndWait().get() == ButtonType.OK) {
            solicitarNuevoCodigo();
        }
    }

    private void solicitarNuevoCodigo() {
        Cliente cliente = clienteRepo.obtenerPorEmail(emailCliente);

        if (cliente == null) {
            mostrarErrorCodigo("Error al obtener información del cliente");
            return;
        }

        Task<Boolean> enviarTask = new Task<Boolean>() {
            CodigoRecuperacion codigo;

            @Override
            protected Boolean call() throws Exception {
                codigo = recuperacionRepo.crearCodigoRecuperacion(cliente.getIdCliente());

                if (codigo == null) {
                    return false;
                }

                return EmailService.enviarCodigoRecuperacion(
                        emailCliente,
                        cliente.getNombre(),
                        codigo.getCodigo()
                );
            }

            @Override
            protected void succeeded() {
                if (getValue()) {
                    Alert info = new Alert(Alert.AlertType.INFORMATION);
                    info.setTitle("Código Enviado");
                    info.setHeaderText("Nuevo código enviado");
                    info.setContentText("Revisa tu correo electrónico. El código anterior ya no es válido.");
                    info.showAndWait();

                    // Reiniciar temporizador
                    tiempoExpiracion = System.currentTimeMillis() + (15 * 60 * 1000);
                    lblTiempoRestante.setStyle("-fx-text-fill: #856404; -fx-font-weight: bold;");
                    hboxTiempoRestante.setStyle("-fx-background-color: #fff3cd;");
                    btnRestablecer.setDisable(false);
                    txtCodigo.clear();
                } else {
                    mostrarErrorCodigo("No se pudo enviar el nuevo código");
                }
            }

            @Override
            protected void failed() {
                mostrarErrorCodigo("Error al solicitar nuevo código: " + getException().getMessage());
            }
        };

        new Thread(enviarTask).start();
    }

    @FXML
    private void handleCancelar() {
        System.out.println("handleCancelar() llamado");

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Cancelar");
        confirmacion.setHeaderText("¿Cancelar el restablecimiento?");
        confirmacion.setContentText("Los cambios no se guardarán.");

        if (confirmacion.showAndWait().get() == ButtonType.OK) {
            if (timeline != null) {
                timeline.stop();
            }
            cerrarDialogo();
        }
    }

    private void mostrarExito() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("¡Contraseña Actualizada!");
        alert.setHeaderText("Contraseña restablecida exitosamente");
        alert.setContentText("Ya puedes iniciar sesión con tu nueva contraseña.");
        alert.showAndWait();

        cerrarDialogo();
    }

    private void mostrarErrorCodigo(String mensaje) {
        if (lblMensajeCodigo != null) {
            lblMensajeCodigo.setText(mensaje);
            lblMensajeCodigo.setVisible(true);
        }
    }

    private void mostrarErrorContrasena(String mensaje) {
        if (lblMensajeContrasena != null) {
            lblMensajeContrasena.setText(mensaje);
            lblMensajeContrasena.setVisible(true);
        }
    }

    private String enmascarEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }

        String[] partes = email.split("@");
        String usuario = partes[0];
        String dominio = partes[1];

        if (usuario.length() <= 2) {
            return "*".repeat(usuario.length()) + "@" + dominio;
        }

        String visible = usuario.substring(0, 2);
        String oculto = "*".repeat(usuario.length() - 2);
        return visible + oculto + "@" + dominio;
    }

    private void cerrarDialogo() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }
}