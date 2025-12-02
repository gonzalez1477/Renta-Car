package com.example.sistema_rentacar.Controllers.Cliente;

import com.example.sistema_rentacar.Modelos.Alquiler;
import com.example.sistema_rentacar.Repository.AlquilerRepository;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.function.Consumer;

public class DialogoFinalizarAlquilerController {

    @FXML private Label lblTitulo;
    @FXML private Label lblVehiculo;
    @FXML private Label lblCliente;
    @FXML private Label lblFechaInicio;
    @FXML private Label lblFechaLimite;
    @FXML private Label lblFechaDevolucion;
    @FXML private VBox boxAlertaRetraso;
    @FXML private Label lblDiasRetraso;
    @FXML private Label lblTarifaDiaria;
    @FXML private Label lblRecargoDiario;
    @FXML private Label lblCostoAlquiler;
    @FXML private Label lblDeposito;
    @FXML private Label lblTextoPenalizacion;
    @FXML private Label lblPenalizacion;
    @FXML private Label lblTotalPagar;
    @FXML private Label lblNotaRetraso;
    @FXML private TextArea txtObservaciones;
    @FXML private Button btnConfirmar;
    @FXML private Button btnCancelar;
    @FXML private VBox vboxMensajePermisos; // Nuevo: contenedor del mensaje para clientes sin permisos
    @FXML private Label lblMensajePermisos; // Nuevo: mensaje para clientes sin permisos

    private Alquiler alquiler;
    private AlquilerRepository alquilerRepo;
    private Consumer<Boolean> onConfirmar;

    private boolean esRetraso = false;
    private int diasRetraso = 0;
    private double penalizacion = 0.0;
    private double totalPagar = 0.0;

    // Nuevo: almacenar el rol del usuario actual
    private String rolUsuario;

    // Factores de recargo ADICIONAL sobre la tarifa diaria
    private static final double RECARGO_NORMAL = 0.50;    // +50% de recargo (1-3 días)
    private static final double RECARGO_MODERADO = 1.00;  // +100% de recargo (4-7 días)
    private static final double RECARGO_ALTO = 1.50;      // +150% de recargo (8+ días)

    @FXML
    public void initialize() {
        alquilerRepo = new AlquilerRepository();

        // Inicialmente ocultar el mensaje de permisos
        if (vboxMensajePermisos != null) {
            vboxMensajePermisos.setVisible(false);
            vboxMensajePermisos.setManaged(false);
        }
    }

 //establecer el rol del usuario
    public void setRolUsuario(String rol) {
        this.rolUsuario = rol;
    }

    public void setDatos(Alquiler alquiler, Consumer<Boolean> onConfirmar) {
        this.alquiler = alquiler;
        this.onConfirmar = onConfirmar;

        cargarDatosAlquiler();
        verificarRetraso();
        calcularTotales();
        verificarPermisos(); // Verificar permisos después de cargar datos
    }


    public void setDatos(Alquiler alquiler, String rolUsuario, Consumer<Boolean> onConfirmar) {
        this.rolUsuario = rolUsuario;
        setDatos(alquiler, onConfirmar);
    }

    private void cargarDatosAlquiler() {
        lblVehiculo.setText(alquiler.getVehiculo() + " - " + alquiler.getPlaca());
        lblCliente.setText(alquiler.getNombreCliente());

        LocalDateTime fechaInicio = alquiler.getFechaInicio().toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        lblFechaInicio.setText(fechaInicio.format(formatter));

        LocalDate fechaLimite = alquiler.getFechaFinEstimada().toLocalDateTime().toLocalDate();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        lblFechaLimite.setText(fechaLimite.format(dateFormatter));

        lblFechaDevolucion.setText(LocalDate.now().format(dateFormatter) + " (HOY)");
    }

    private void verificarRetraso() {
        LocalDate fechaLimite = alquiler.getFechaFinEstimada().toLocalDateTime().toLocalDate();
        LocalDate hoy = LocalDate.now();

        if (hoy.isAfter(fechaLimite)) {
            esRetraso = true;
            diasRetraso = (int) ChronoUnit.DAYS.between(fechaLimite, hoy);

            // Determinar el factor de recargo adicional según días de retraso
            double factorRecargoAdicional;
            if (diasRetraso >= 8) {
                factorRecargoAdicional = RECARGO_ALTO;
            } else if (diasRetraso >= 4) {
                factorRecargoAdicional = RECARGO_MODERADO;
            } else {
                factorRecargoAdicional = RECARGO_NORMAL;
            }

            // Cálculo correcto: solo el recargo adicional por día
            double recargoPorDia = alquiler.getTarifaDiaria() * factorRecargoAdicional;
            penalizacion = recargoPorDia * diasRetraso;

            boxAlertaRetraso.setVisible(true);
            boxAlertaRetraso.setManaged(true);

            lblDiasRetraso.setText(diasRetraso + " día(s)");
            lblTarifaDiaria.setText(String.format("$%.2f", alquiler.getTarifaDiaria()));

            lblRecargoDiario.setText(String.format("$%.2f (+%.0f%% de recargo)",
                    recargoPorDia, factorRecargoAdicional * 100));

            String politicaAplicada;
            if (diasRetraso >= 8) {
                politicaAplicada = "8+ días: +150% de recargo";
            } else if (diasRetraso >= 4) {
                politicaAplicada = "4-7 días: +100% de recargo";
            } else {
                politicaAplicada = "1-3 días: +50% de recargo";
            }

            lblTextoPenalizacion.setVisible(true);
            lblTextoPenalizacion.setManaged(true);
            lblPenalizacion.setVisible(true);
            lblPenalizacion.setManaged(true);
            lblPenalizacion.setText(String.format("+ $%.2f", penalizacion));

            lblNotaRetraso.setVisible(true);
            lblNotaRetraso.setManaged(true);

            lblTitulo.setText("Finalizar Alquiler con Retraso");
            lblTitulo.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #c62828;");
        }
    }

    // verificar permisos para finalizar alquileres con retraso
    private void verificarPermisos() {
        System.out.println("📋 Verificando permisos...");
        System.out.println("   - Rol usuario: " + (rolUsuario != null ? rolUsuario : "NULL"));
        System.out.println("   - Es retraso: " + esRetraso);
        System.out.println("   - Es cliente: " + esCliente());

        if (esRetraso && esCliente()) {
            System.out.println("BLOQUEANDO botón - Cliente con retraso");
            // Deshabilitar el botón de confirmar para clientes con alquileres retrasados
            btnConfirmar.setDisable(true);

            // Mostrar mensaje explicativo
            if (vboxMensajePermisos != null) {
                vboxMensajePermisos.setVisible(true);
                vboxMensajePermisos.setManaged(true);
            }
        } else {
            //System.out.println("HABILITANDO botón - Admin/Empleado o sin retraso");
            // Habilitar el botón para admin/empleados o alquileres sin retraso
            btnConfirmar.setDisable(false);

            if (vboxMensajePermisos != null) {
                vboxMensajePermisos.setVisible(false);
                vboxMensajePermisos.setManaged(false);
            }
        }
    }

    // verificar si el usuario es cliente
    private boolean esCliente() {
        if (rolUsuario == null || rolUsuario.trim().isEmpty()) {
            // Si no se especifica rol, por seguridad tratarlo como cliente
            //System.out.println("ADVERTENCIA: No se especificó rol de usuario, tratando como cliente por defecto");
            return true;
        }

        // Verificar si el rol es "Cliente" (case-insensitive)
        String rolNormalizado = rolUsuario.trim().toLowerCase();
        boolean esCliente = rolNormalizado.equals("cliente");

        //System.out.println(" Verificando rol: '" + rolUsuario + "' -> Es cliente: " + esCliente);

        return esCliente;
    }

    // verificar si tiene permisos de admin
    private boolean tienePermisosFinalizacion() {
        if (rolUsuario == null) {
            return false;
        }

        String rolNormalizado = rolUsuario.trim().toLowerCase();
        return rolNormalizado.equals("administrador") ||
                rolNormalizado.equals("empleado") ||
                rolNormalizado.equals("admin");
    }

    private void calcularTotales() {
        double costoAlquiler = alquiler.getCostoTotal();
        double depositoPagado = alquiler.getDeposito();

        lblCostoAlquiler.setText(String.format("$%.2f", costoAlquiler));
        lblDeposito.setText(String.format("$%.2f", depositoPagado));

        if (esRetraso) {
            totalPagar = penalizacion;
            lblTotalPagar.setText(String.format("$%.2f", totalPagar));
            lblTotalPagar.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: #e74c3c;");
        } else {
            totalPagar = 0.0;
            lblTotalPagar.setText("$0.00");
            lblTotalPagar.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: #27ae60;");
        }
    }

    @FXML
    private void handleConfirmar() {
        // Verificación adicional por seguridad
        if (esRetraso && esCliente()) {
            mostrarError("No tiene permisos para finalizar alquileres con retraso.\n" +
                    "Por favor, contacte a un empleado o administrador.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Devolución");

        if (esRetraso) {
            double porcentajeRecargo = 0;
            String rangoAplicado = "";

            if (diasRetraso >= 8) {
                porcentajeRecargo = 150;
                rangoAplicado = "8+ días";
            } else if (diasRetraso >= 4) {
                porcentajeRecargo = 100;
                rangoAplicado = "4-7 días";
            } else {
                porcentajeRecargo = 50;
                rangoAplicado = "1-3 días";
            }

            confirmacion.setHeaderText("¿Confirmar devolución con retraso?");
            confirmacion.setContentText(String.format(
                    "ATENCIÓN: Este alquiler tiene retraso\n\n" +
                            "Días de retraso: %d\n" +
                            "Política aplicada: +%.0f%% de recargo (%s)\n" +
                            "Penalización: $%.2f\n" +
                            "Total a cobrar: $%.2f\n\n" +
                            "Política de recargos por retraso:\n" +
                            "  • 1-3 días: +50%% de recargo\n" +
                            "  • 4-7 días: +100%% de recargo\n" +
                            "  • 8+ días: +150%% de recargo\n\n" +
                            "¿Ha recibido el pago de la penalización del cliente?",
                    diasRetraso,
                    porcentajeRecargo,
                    rangoAplicado,
                    penalizacion,
                    totalPagar
            ));
        } else {
            confirmacion.setHeaderText("¿Confirmar devolución del vehículo?");
            confirmacion.setContentText(String.format(
                    "Vehículo: %s\n" +
                            "Cliente: %s\n\n" +
                            "El alquiler ya fue pagado completamente.\n" +
                            "Esta acción finalizará el alquiler.",
                    alquiler.getVehiculo(),
                    alquiler.getNombreCliente()
            ));
        }

        ButtonType btnSi = new ButtonType("Sí, finalizar");
        ButtonType btnNo = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirmacion.getButtonTypes().setAll(btnSi, btnNo);

        confirmacion.showAndWait().ifPresent(response -> {
            if (response == btnSi) {
                finalizarAlquiler();
            }
        });
    }

    private void finalizarAlquiler() {
        String observaciones = txtObservaciones.getText().trim();
        String estado = esRetraso ? "Finalizado con Retraso" : "Finalizado";

        StringBuilder obsCompletas = new StringBuilder();

        if (esRetraso) {
            obsCompletas.append("[FINALIZADO CON RETRASO]\n");
            obsCompletas.append(String.format("Días de retraso: %d\n", diasRetraso));
            obsCompletas.append(String.format("Penalización cobrada: $%.2f\n", penalizacion));
            obsCompletas.append(String.format("Total cobrado (penalización): $%.2f\n", totalPagar));
            obsCompletas.append(String.format("Finalizado por: %s\n", rolUsuario != null ? rolUsuario : "No especificado"));
            obsCompletas.append(String.format("Fecha devolución: %s\n",
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
        } else {
            obsCompletas.append("[FINALIZADO A TIEMPO]\n");
            obsCompletas.append(String.format("Fecha devolución: %s\n",
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
        }

        if (!observaciones.isEmpty()) {
            obsCompletas.append("\nObservaciones de devolución:\n");
            obsCompletas.append(observaciones);
        }

        if (alquilerRepo.finalizar(alquiler.getIdAlquiler())) {
            alquilerRepo.cambiarEstado(alquiler.getIdAlquiler(), estado);

            if (esRetraso) {
                alquilerRepo.actualizarPenalizacion(
                        alquiler.getIdAlquiler(),
                        penalizacion,
                        diasRetraso
                );
            }

            if (obsCompletas.length() > 0) {
                alquilerRepo.actualizarObservaciones(
                        alquiler.getIdAlquiler(),
                        obsCompletas.toString()
                );
            }

            mostrarExito();

            if (onConfirmar != null) {
                onConfirmar.accept(esRetraso);
            }

            cerrarDialogo();
        } else {
            mostrarError("No se pudo finalizar el alquiler. Intente nuevamente.");
        }
    }

    @FXML
    private void handleCancelar() {
        cerrarDialogo();
    }

    private void mostrarExito() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Alquiler Finalizado");

        if (esRetraso) {
            alert.setHeaderText("Devolución con retraso procesada");
            alert.setContentText(String.format(
                    "El alquiler ha sido finalizado con retraso.\n\n" +
                            "Días de retraso: %d\n" +
                            "Penalización cobrada: $%.2f\n\n" +
                            "El vehículo está disponible nuevamente.",
                    diasRetraso,
                    penalizacion
            ));
        } else {
            alert.setHeaderText("Vehículo devuelto exitosamente");
            alert.setContentText("El alquiler ha sido finalizado a tiempo.\n\n" +
                    "El vehículo está disponible nuevamente.");
        }

        alert.showAndWait();
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void cerrarDialogo() {
        Stage stage = (Stage) btnConfirmar.getScene().getWindow();
        stage.close();
    }
}