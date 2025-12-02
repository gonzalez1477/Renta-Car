package com.example.sistema_rentacar.Controllers.Cliente;

import com.example.sistema_rentacar.Modelos.Alquiler;
import com.example.sistema_rentacar.Repository.AlquilerRepository;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.function.Consumer;

public class DialogoEntregaAnticipadaController {

    @FXML private Label lblVehiculo;
    @FXML private Label lblFechaInicio;
    @FXML private Label lblFechaFin;
    @FXML private Label lblDiasContratados;
    @FXML private Label lblDiasUtilizados;
    @FXML private Label lblTarifaDiaria;
    @FXML private Label lblDiasNoUtilizados;
    @FXML private Label lblMontoReembolso;
    @FXML private Label lblDepositoPagado;
    @FXML private TextArea txtMotivo;
    @FXML private Label lblErrorMotivo;
    @FXML private Button btnConfirmar;
    @FXML private Button btnCancelar;

    private Alquiler alquiler;
    private AlquilerRepository alquilerRepo;
    private Consumer<String> onConfirmar;
    private int diasUtilizados;
    private int diasNoUtilizados;
    private double montoReembolso;
    private double montoPagado;

    @FXML
    public void initialize() {
        alquilerRepo = new AlquilerRepository();
    }

    public void setDatos(Alquiler alquiler, Consumer<String> onConfirmar) {
        this.alquiler = alquiler;
        this.onConfirmar = onConfirmar;

        cargarDatosAlquiler();
        calcularReembolso();
    }

    private void cargarDatosAlquiler() {
        lblVehiculo.setText(alquiler.getVehiculo() + " - " + alquiler.getPlaca());

        lblFechaInicio.setText(formatearFecha(
                alquiler.getFechaInicio().toLocalDateTime()
        ));

        lblFechaFin.setText(formatearFecha(
                alquiler.getFechaFinEstimada().toLocalDateTime()
        ));

        lblDiasContratados.setText(alquiler.getDiasAlquiler() + " días");
    }

    private void calcularReembolso() {
        LocalDateTime fechaInicio = alquiler.getFechaInicio().toLocalDateTime();
        LocalDateTime ahora = LocalDateTime.now();

        long horas = ChronoUnit.HOURS.between(fechaInicio, ahora);
        diasUtilizados = (int) Math.ceil(horas / 24.0);

        // asegurar al menos 1 día
        if (diasUtilizados < 1) diasUtilizados = 1;

        diasNoUtilizados = alquiler.getDiasAlquiler() - diasUtilizados;
        if (diasNoUtilizados < 0) diasNoUtilizados = 0;

        // Cálculos financieros
        double costoTotal = alquiler.getTarifaDiaria() * alquiler.getDiasAlquiler();
        montoPagado = costoTotal; // El cliente pagó el 100% al inicio
        montoReembolso = alquiler.getTarifaDiaria() * diasNoUtilizados;

        // Mostrar tarifa diaria
        lblTarifaDiaria.setText(String.format("$%.2f", alquiler.getTarifaDiaria()));

        lblDiasUtilizados.setText(diasUtilizados + " días");
        lblDiasNoUtilizados.setText(diasNoUtilizados + " días");
        lblMontoReembolso.setText(String.format("$%.2f", montoReembolso));
        lblDepositoPagado.setText(String.format("$%.2f", montoPagado));

        if (montoReembolso > 0) {
            lblMontoReembolso.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: #27ae60;");
        } else {
            lblMontoReembolso.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: #e74c3c;");
        }
    }

    @FXML
    private void handleConfirmar() {
        // Validar que se haya ingresado un motivo
        String motivo = txtMotivo.getText().trim();

        if (motivo.isEmpty()) {
            lblErrorMotivo.setText("Debe ingresar el motivo de la entrega anticipada");
            lblErrorMotivo.setVisible(true);
            txtMotivo.requestFocus();
            return;
        }

        if (motivo.length() < 10) {
            lblErrorMotivo.setText("El motivo debe tener al menos 10 caracteres");
            lblErrorMotivo.setVisible(true);
            txtMotivo.requestFocus();
            return;
        }

        // Confirmar acción
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Entrega Anticipada");
        confirmacion.setHeaderText("¿Está seguro de finalizar este alquiler?");
        confirmacion.setContentText(String.format(
                "Vehículo: %s\n" +
                        "Días utilizados: %d de %d\n" +
                        "Monto pagado: $%.2f\n" +
                        "Reembolso: $%.2f\n\n" +
                        "Esta acción no se puede deshacer.",
                alquiler.getVehiculo(),
                diasUtilizados,
                alquiler.getDiasAlquiler(),
                montoPagado,
                montoReembolso
        ));

        if (confirmacion.showAndWait().get() == ButtonType.OK) {

            String observacionesCompletas = String.format(
                    "[ENTREGA ANTICIPADA]\n" +
                            "Días contratados: %d\n" +
                            "Días utilizados: %d\n" +
                            "Monto pagado: $%.2f\n" +
                            "Reembolso: $%.2f\n" +
                            "Motivo: %s",
                    alquiler.getDiasAlquiler(),
                    diasUtilizados,
                    montoPagado,
                    montoReembolso,
                    motivo
            );

            // Finalizar alquiler
            if (alquilerRepo.finalizar(alquiler.getIdAlquiler())) {
                alquilerRepo.actualizarObservaciones(
                        alquiler.getIdAlquiler(),
                        observacionesCompletas
                );

                mostrarExito();

                // Llamar callback
                if (onConfirmar != null) {
                    onConfirmar.accept(motivo);
                }

                cerrarDialogo();
            } else {
                mostrarError("No se pudo finalizar el alquiler. Intente nuevamente.");
            }
        }
    }

    @FXML
    private void handleCancelar() {
        cerrarDialogo();
    }

    private void mostrarExito() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Entrega Confirmada");
        alert.setHeaderText("Vehículo entregado exitosamente");
        alert.setContentText(String.format(
                "El alquiler ha sido finalizado.\n\n" +
                        "Días utilizados: %d\n" +
                        "Monto pagado: $%.2f\n" +
                        "Reembolso a procesar: $%.2f\n\n" +
                        "Gracias por utilizar nuestros servicios.",
                diasUtilizados,
                montoPagado,
                montoReembolso
        ));
        alert.showAndWait();
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private String formatearFecha(LocalDateTime fecha) {
        return String.format("%02d/%02d/%d %02d:%02d",
                fecha.getDayOfMonth(),
                fecha.getMonthValue(),
                fecha.getYear(),
                fecha.getHour(),
                fecha.getMinute()
        );
    }

    private void cerrarDialogo() {
        Stage stage = (Stage) btnConfirmar.getScene().getWindow();
        stage.close();
    }
}