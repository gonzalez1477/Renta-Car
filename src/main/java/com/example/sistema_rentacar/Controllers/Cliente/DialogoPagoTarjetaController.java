package com.example.sistema_rentacar.Controllers.Cliente;

import com.example.sistema_rentacar.Modelos.Pago;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Random;
import java.util.function.Consumer;

public class DialogoPagoTarjetaController {

    @FXML private Label lblMontoPagar;
    @FXML private Label lblConcepto;
    @FXML private ComboBox<String> cmbTipoTarjeta;
    @FXML private TextField txtNumeroTarjeta;
    @FXML private TextField txtNombreTitular;
    @FXML private TextField txtMesExpiracion;
    @FXML private TextField txtAnioExpiracion;
    @FXML private TextField txtCVV;
    @FXML private HBox hboxProcesando;
    @FXML private Button btnProcesarPago;
    @FXML private Button btnCancelar;

    private double montoPagar;
    private Consumer<Pago> onPagoExitoso;
    private Pago pagoResultado;

    @FXML
    public void initialize() {
        // Configurar tipos de tarjeta
        cmbTipoTarjeta.getItems().addAll(
                "Visa",
                "Mastercard",
                "American Express",
                "Discover"
        );
        cmbTipoTarjeta.setValue("Visa");

        // Configurar listeners para formato de tarjeta
        configurarFormatoNumeroTarjeta();
        configurarSoloNumeros(txtMesExpiracion);
        configurarSoloNumeros(txtAnioExpiracion);
        configurarSoloNumeros(txtCVV);
        configurarMayusculas(txtNombreTitular);
    }

    public void setDatos(double monto, String concepto, Consumer<Pago> callback) {
        this.montoPagar = monto;
        this.onPagoExitoso = callback;

        lblMontoPagar.setText(String.format("$%.2f", monto));
        lblConcepto.setText(concepto);
    }

    @FXML
    private void handleProcesarPago() {
        // Validar campos
        if (!validarCampos()) {
            return;
        }

        // Deshabilitar botones
        btnProcesarPago.setDisable(true);
        btnCancelar.setDisable(true);

        // Mostrar indicador de procesamiento
        hboxProcesando.setVisible(true);
        hboxProcesando.setManaged(true);

        // Simular procesamiento del pago
        PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
        pause.setOnFinished(event -> {

            boolean exito = new Random().nextInt(100) < 95;

            if (exito) {
                procesarPagoExitoso();
            } else {
                procesarPagoRechazado();
            }
        });
        pause.play();
    }

    private void procesarPagoExitoso() {
        // Crear objeto Pago con los datos de la tarjeta
        String numeroCompleto = txtNumeroTarjeta.getText().replaceAll("\\s", "");
        String ultimosDigitos = numeroCompleto.substring(numeroCompleto.length() - 4);
        String referencia = generarReferencia();

        pagoResultado = new Pago(
                0,
                montoPagar,
                cmbTipoTarjeta.getValue(),
                ultimosDigitos,
                txtNombreTitular.getText().toUpperCase().trim(),
                referencia
        );

        // Mostrar mensaje de éxito
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Pago Exitoso");
            alert.setHeaderText("¡Pago Procesado Correctamente!");
            alert.setContentText(String.format(
                    "Monto: $%.2f\n" +
                            "Tarjeta: %s\n" +
                            "Últimos dígitos: **** %s\n" +
                            "Código de autorización: %s\n\n" +
                            "Recibirá un comprobante por correo electrónico.",
                    montoPagar,
                    cmbTipoTarjeta.getValue(),
                    ultimosDigitos,
                    referencia
            ));
            alert.showAndWait();


            if (onPagoExitoso != null) {
                onPagoExitoso.accept(pagoResultado);
            }
            cerrarDialogo();
        });
    }

    private void procesarPagoRechazado() {
        Platform.runLater(() -> {
            hboxProcesando.setVisible(false);
            hboxProcesando.setManaged(false);
            btnProcesarPago.setDisable(false);
            btnCancelar.setDisable(false);

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Pago Rechazado");
            alert.setHeaderText("No se pudo procesar el pago");
            alert.setContentText(
                    "La transacción ha sido rechazada por su entidad bancaria.\n\n" +
                            "Posibles causas:\n" +
                            "• Fondos insuficientes\n" +
                            "• Tarjeta vencida\n" +
                            "• Límite de crédito alcanzado\n\n" +
                            "Por favor, intente con otra tarjeta o método de pago."
            );
            alert.showAndWait();
        });
    }

    private boolean validarCampos() {
        // Validar tipo de tarjeta
        if (cmbTipoTarjeta.getValue() == null) {
            mostrarError("Seleccione el tipo de tarjeta");
            return false;
        }

        // Validar número de tarjeta
        String numeroTarjeta = txtNumeroTarjeta.getText().replaceAll("\\s", "");
        if (numeroTarjeta.isEmpty()) {
            mostrarError("Ingrese el número de tarjeta");
            txtNumeroTarjeta.requestFocus();
            return false;
        }

        if (numeroTarjeta.length() < 13 || numeroTarjeta.length() > 19) {
            mostrarError("Número de tarjeta inválido (debe tener entre 13 y 19 dígitos)");
            txtNumeroTarjeta.requestFocus();
            return false;
        }

        if (!validarLuhn(numeroTarjeta)) {
            mostrarError("Número de tarjeta inválido");
            txtNumeroTarjeta.requestFocus();
            return false;
        }

        // Validar nombre del titular
        if (txtNombreTitular.getText().trim().isEmpty()) {
            mostrarError("Ingrese el nombre del titular");
            txtNombreTitular.requestFocus();
            return false;
        }

        // Validar fecha de expiración
        String mes = txtMesExpiracion.getText().trim();
        String anio = txtAnioExpiracion.getText().trim();

        if (mes.isEmpty() || anio.isEmpty()) {
            mostrarError("Ingrese la fecha de expiración completa");
            txtMesExpiracion.requestFocus();
            return false;
        }

        try {
            int mesInt = Integer.parseInt(mes);
            if (mesInt < 1 || mesInt > 12) {
                mostrarError("Mes inválido (debe estar entre 01 y 12)");
                txtMesExpiracion.requestFocus();
                return false;
            }

            int anioInt = Integer.parseInt(anio);
            int anioActual = java.time.Year.now().getValue() % 100;

            if (anioInt < anioActual || (anioInt == anioActual && mesInt < java.time.LocalDate.now().getMonthValue())) {
                mostrarError("La tarjeta está vencida");
                txtAnioExpiracion.requestFocus();
                return false;
            }

        } catch (NumberFormatException e) {
            mostrarError("Fecha de expiración inválida");
            return false;
        }

        // Validar CVV
        String cvv = txtCVV.getText().trim();
        if (cvv.isEmpty()) {
            mostrarError("Ingrese el código CVV");
            txtCVV.requestFocus();
            return false;
        }

        if (cvv.length() < 3 || cvv.length() > 4) {
            mostrarError("CVV inválido (debe tener 3 o 4 dígitos)");
            txtCVV.requestFocus();
            return false;
        }

        return true;
    }

    // Algoritmo de Luhn para validar números de tarjeta
    private boolean validarLuhn(String numero) {
        int sum = 0;
        boolean alternate = false;

        for (int i = numero.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(numero.substring(i, i + 1));

            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n = (n % 10) + 1;
                }
            }

            sum += n;
            alternate = !alternate;
        }

        return (sum % 10 == 0);
    }

    private void configurarFormatoNumeroTarjeta() {
        txtNumeroTarjeta.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[\\d\\s]*")) {
                txtNumeroTarjeta.setText(oldValue);
                return;
            }

            String cleaned = newValue.replaceAll("\\s", "");
            if (cleaned.length() > 19) {
                txtNumeroTarjeta.setText(oldValue);
                return;
            }

            StringBuilder formatted = new StringBuilder();
            for (int i = 0; i < cleaned.length(); i++) {
                if (i > 0 && i % 4 == 0) {
                    formatted.append(" ");
                }
                formatted.append(cleaned.charAt(i));
            }

            if (!formatted.toString().equals(newValue)) {
                int caretPos = txtNumeroTarjeta.getCaretPosition();
                txtNumeroTarjeta.setText(formatted.toString());
                txtNumeroTarjeta.positionCaret(Math.min(caretPos, formatted.length()));
            }
        });
    }

    private void configurarSoloNumeros(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                textField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    private void configurarMayusculas(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.equals(newValue.toUpperCase())) {
                textField.setText(newValue.toUpperCase());
            }
        });
    }

    private String generarReferencia() {
        // Generar código de autorización simulado
        Random random = new Random();
        return String.format("AUTH-%d%06d",
                System.currentTimeMillis() % 1000000,
                random.nextInt(1000000));
    }

    @FXML
    private void handleCancelar() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cancelar Pago");
        alert.setHeaderText("¿Está seguro de cancelar el pago?");
        alert.setContentText("No se procesará ningún cargo a su tarjeta.");

        if (alert.showAndWait().get() == ButtonType.OK) {
            cerrarDialogo();
        }
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Validación");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void cerrarDialogo() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    public Pago getPagoResultado() {
        return pagoResultado;
    }
}