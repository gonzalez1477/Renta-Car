package com.example.sistema_rentacar.Controllers.Empleado;

import com.example.sistema_rentacar.Modelos.Cliente;
import com.example.sistema_rentacar.Repository.ClienteRepository;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Date;
import java.time.LocalDate;
import java.time.Period;

public class FormularioClienteController {

    @FXML private Label lblTitulo;
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtDui;
    @FXML private TextField txtLicencia;
    @FXML private DatePicker dpFechaNacimiento;
    @FXML private TextArea txtDireccion;
    @FXML private TextField txtUsuario;
    @FXML private TextField txtContrasena;
    @FXML private CheckBox chkActivo;
    @FXML private VBox vboxEstado;
    @FXML private VBox vboxCredenciales;
    @FXML private HBox hboxMensaje;
    @FXML private Label lblIconoMensaje;
    @FXML private Label lblMensaje;
    @FXML private Button btnGuardar;

    private ClienteRepository clienteRepository;
    private DashboardEmpleadoController dashboardController;
    private Cliente clienteEditar;
    private boolean esNuevo;

    @FXML
    public void initialize() {
        clienteRepository = new ClienteRepository();
        configurarValidaciones();
    }

    public void setDatos(Cliente cliente, DashboardEmpleadoController controller) {
        this.dashboardController = controller;
        this.clienteEditar = cliente;
        this.esNuevo = (cliente == null);

        if (esNuevo) {
            // ============================
            //     MODO CREAR
            // ============================
            lblTitulo.setText("Nuevo Cliente");
            btnGuardar.setText("Crear Cliente");

            vboxCredenciales.setVisible(true);
            vboxCredenciales.setManaged(true);

            vboxEstado.setVisible(false);
            vboxEstado.setManaged(false);

            // CONTRASEÑA POR DEFECTO
            txtContrasena.setText("cliente123");
            txtContrasena.setDisable(true);
            txtContrasena.setEditable(false);

        } else {
            // ============================
            //    MODO EDITAR
            // ============================
            lblTitulo.setText("Editar Cliente");
            btnGuardar.setText("Guardar Cambios");

            vboxCredenciales.setVisible(false);
            vboxCredenciales.setManaged(false);

            vboxEstado.setVisible(true);
            vboxEstado.setManaged(true);

            cargarDatosCliente(cliente);
        }
    }

    private void cargarDatosCliente(Cliente cliente) {
        txtNombre.setText(cliente.getNombre());
        txtApellido.setText(cliente.getApellido());
        txtEmail.setText(cliente.getEmail());
        txtTelefono.setText(cliente.getTelefono());
        txtDui.setText(cliente.getDui());
        txtLicencia.setText(cliente.getLicencia());
        txtDireccion.setText(cliente.getDireccion());
        chkActivo.setSelected(cliente.isActivo());

        if (cliente.getFechaNacimiento() != null) {
            dpFechaNacimiento.setValue(cliente.getFechaNacimiento().toLocalDate());
        }
    }

    private void configurarValidaciones() {
        // Validación de nombre
        txtNombre.textProperty().addListener((obs, old, newVal) -> {
            if (newVal != null && !newVal.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]*")) {
                txtNombre.setText(old);
            }
        });

        txtApellido.textProperty().addListener((obs, old, newVal) -> {
            if (newVal != null && !newVal.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]*")) {
                txtApellido.setText(old);
            }
        });

        // Validación teléfono
        txtTelefono.textProperty().addListener((obs, old, newVal) -> {
            if (newVal != null && !newVal.matches("[0-9-]*")) {
                txtTelefono.setText(old);
            }
            if (newVal != null && newVal.length() > 9) {
                txtTelefono.setText(old);
            }
        });

        // Validación DUI
        txtDui.textProperty().addListener((obs, old, newVal) -> {
            if (newVal != null && !newVal.matches("[0-9-]*")) {
                txtDui.setText(old);
            }
            if (newVal != null && newVal.length() > 10) {
                txtDui.setText(old);
            }
        });

        // Usuario sin espacios
        if (txtUsuario != null) {
            txtUsuario.textProperty().addListener((obs, old, newVal) -> {
                if (newVal != null && newVal.contains(" ")) {
                    txtUsuario.setText(newVal.replace(" ", ""));
                }
            });
        }
    }

    @FXML
    private void handleGuardar() {
        if (!validarCampos()) {
            return;
        }

        if (esNuevo) {
            crearCliente();
        } else {
            actualizarCliente();
        }
    }

    private boolean validarCampos() {
        StringBuilder errores = new StringBuilder();

        if (txtNombre.getText().trim().isEmpty()) errores.append("• El nombre es obligatorio\n");
        if (txtApellido.getText().trim().isEmpty()) errores.append("• El apellido es obligatorio\n");

        if (txtEmail.getText().trim().isEmpty()) {
            errores.append("• El correo electrónico es obligatorio\n");
        } else if (!validarEmail(txtEmail.getText())) {
            errores.append("• El formato del correo no es válido\n");
        }

        if (txtTelefono.getText().trim().isEmpty()) {
            errores.append("• El teléfono es obligatorio\n");
        } else if (!validarTelefono(txtTelefono.getText())) {
            errores.append("• El formato del teléfono no es válido (0000-0000)\n");
        }

        if (txtDui.getText().trim().isEmpty()) {
            errores.append("• El DUI es obligatorio\n");
        } else if (!validarDui(txtDui.getText())) {
            errores.append("• El formato del DUI no es válido (00000000-0)\n");
        }

        if (txtLicencia.getText().trim().isEmpty()) errores.append("• La licencia de conducir es obligatoria\n");

        if (dpFechaNacimiento.getValue() == null) {
            errores.append("• La fecha de nacimiento es obligatoria\n");
        } else if (!validarEdad(dpFechaNacimiento.getValue())) {
            errores.append("• El cliente debe tener al menos 18 años\n");
        }

        if (txtDireccion.getText().trim().isEmpty()) errores.append("• La dirección es obligatoria\n");

        // Validaciones SOLO para nuevo
        if (esNuevo) {
            if (txtUsuario.getText().trim().isEmpty()) {
                errores.append("• El nombre de usuario es obligatorio\n");
            } else if (txtUsuario.getText().length() < 4) {
                errores.append("• El usuario debe tener al menos 4 caracteres\n");
            }
        }

        // Validaciones duplicados
        if (esNuevo || !txtEmail.getText().equals(clienteEditar.getEmail())) {
            if (clienteRepository.existeEmail(txtEmail.getText()))
                errores.append("• El correo ya está registrado\n");
        }
        if (esNuevo || !txtDui.getText().equals(clienteEditar.getDui())) {
            if (clienteRepository.existeDui(txtDui.getText()))
                errores.append("• El DUI ya está registrado\n");
        }
        if (esNuevo || !txtLicencia.getText().equals(clienteEditar.getLicencia())) {
            if (clienteRepository.existeLicencia(txtLicencia.getText()))
                errores.append("• La licencia ya está registrada\n");
        }
        if (esNuevo && clienteRepository.existeUsuario(txtUsuario.getText()))
            errores.append("• El nombre de usuario ya existe\n");

        if (errores.length() > 0) {
            mostrarError(errores.toString());
            return false;
        }

        return true;
    }

    private void crearCliente() {
        Cliente nuevoCliente = new Cliente();
        llenarDatosCliente(nuevoCliente);
        nuevoCliente.setUsuario(txtUsuario.getText().trim());

        // CONTRASEÑA FIJA
        nuevoCliente.setContrasena("cliente123");


        nuevoCliente.setActivo(true);

        if (clienteRepository.registrar(nuevoCliente)) {
            mostrarExito("Cliente registrado exitosamente");
            dashboardController.refrescarClientes();

            new Thread(() -> {
                try {
                    Thread.sleep(1500);
                    javafx.application.Platform.runLater(this::cerrarVentana);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        } else {
            mostrarError("Error al registrar el cliente");
        }
    }

    private void actualizarCliente() {
        llenarDatosCliente(clienteEditar);
        clienteEditar.setActivo(chkActivo.isSelected());

        if (clienteRepository.actualizar(clienteEditar)) {
            mostrarExito("Cliente actualizado exitosamente");
            dashboardController.refrescarClientes();

            new Thread(() -> {
                try {
                    Thread.sleep(1500);
                    javafx.application.Platform.runLater(this::cerrarVentana);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        } else {
            mostrarError("Error al actualizar el cliente");
        }
    }

    private void llenarDatosCliente(Cliente cliente) {
        cliente.setNombre(txtNombre.getText().trim());
        cliente.setApellido(txtApellido.getText().trim());
        cliente.setEmail(txtEmail.getText().trim().toLowerCase());
        cliente.setTelefono(txtTelefono.getText().trim());
        cliente.setDui(txtDui.getText().trim());
        cliente.setLicencia(txtLicencia.getText().trim());
        cliente.setDireccion(txtDireccion.getText().trim());
        cliente.setFechaNacimiento(Date.valueOf(dpFechaNacimiento.getValue()));
    }

    private boolean validarEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    private boolean validarTelefono(String telefono) {
        return telefono.matches("\\d{4}-\\d{4}") || telefono.matches("\\d{8}");
    }

    private boolean validarDui(String dui) {
        return dui.matches("\\d{8}-\\d");
    }

    private boolean validarEdad(LocalDate fechaNacimiento) {
        return Period.between(fechaNacimiento, LocalDate.now()).getYears() >= 18;
    }

    private void mostrarExito(String mensaje) {
        hboxMensaje.setVisible(true);
        hboxMensaje.setManaged(true);
        hboxMensaje.setStyle("-fx-background-color: #d4edda; -fx-border-color: #28a745;"
                + "-fx-padding: 12; -fx-background-radius: 8; -fx-border-radius: 8;");
        lblIconoMensaje.setText("✓");
        lblIconoMensaje.setStyle("-fx-text-fill: #28a745;");
        lblMensaje.setText(mensaje);
        lblMensaje.setStyle("-fx-text-fill: #155724;");
    }

    private void mostrarError(String mensaje) {
        hboxMensaje.setVisible(true);
        hboxMensaje.setManaged(true);
        hboxMensaje.setStyle("-fx-background-color: #f8d7da; -fx-border-color: #dc3545;"
                + "-fx-padding: 12; -fx-background-radius: 8; -fx-border-radius: 8;");
        lblIconoMensaje.setText("✕");
        lblIconoMensaje.setStyle("-fx-text-fill: #dc3545;");
        lblMensaje.setText(mensaje);
        lblMensaje.setStyle("-fx-text-fill: #721c24;");
    }

    @FXML
    private void handleCancelar() {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btnGuardar.getScene().getWindow();
        stage.close();
    }
}
