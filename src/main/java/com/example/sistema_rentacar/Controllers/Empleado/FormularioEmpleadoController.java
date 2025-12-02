package com.example.sistema_rentacar.Controllers.Empleado;

import com.example.sistema_rentacar.Repository.EmpleadoRepository;
import com.example.sistema_rentacar.Modelos.Empleado;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Date;
import java.time.LocalDate;
import java.util.regex.Pattern;

public class FormularioEmpleadoController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtContrasena;
    @FXML private PasswordField txtConfirmarContrasena;
    @FXML private ComboBox<String> cmbCargo;
    @FXML private DatePicker dpFechaContratacion;
    @FXML private CheckBox chkActivo;
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;
    @FXML private Label lblTitulo;
    @FXML private Label lblContrasenaInfo;
    @FXML private HBox hboxContrasenaInfo; // Contenedor del mensaje de info
    @FXML private VBox vboxContrasena;

    private EmpleadoRepository empleadoDAO;
    private Empleado empleadoActual;
    private DashboardEmpleadoController dashboardController;
    private boolean esEdicion;

    private static final Pattern TELEFONO_PATTERN = Pattern.compile("^\\d{4}-\\d{4}$");


    @FXML
    public void initialize() {
        empleadoDAO = new EmpleadoRepository();
        configurarControles();
    }

    private void configurarControles() {
        // ComboBox cargo
        cmbCargo.setItems(FXCollections.observableArrayList(
                "Administrador",
                "Empleado"
        ));
        cmbCargo.setValue("Empleado");

        // DatePicker fecha de contratación
        dpFechaContratacion.setValue(LocalDate.now());

        chkActivo.setSelected(true);
    }

    public void setDatos(Empleado empleado, DashboardEmpleadoController controller) {
        this.dashboardController = controller;
        this.empleadoActual = empleado;
        this.esEdicion = (empleado != null);

        if (esEdicion) {
            lblTitulo.setText("Editar Empleado");
            cargarDatos();

            // Mostrar mensaje informativo sobre la contraseña
            lblContrasenaInfo.setText("Dejar en blanco para mantener la contraseña actual");
            hboxContrasenaInfo.setVisible(true);
            hboxContrasenaInfo.setManaged(true);

            txtContrasena.setPromptText("Nueva contraseña (opcional)");
            txtConfirmarContrasena.setPromptText("Confirmar nueva contraseña");
        } else {
            lblTitulo.setText("Nuevo Empleado");
            hboxContrasenaInfo.setVisible(false);
            hboxContrasenaInfo.setManaged(false);
        }
    }

    private void cargarDatos() {
        txtNombre.setText(empleadoActual.getNombre());
        txtApellido.setText(empleadoActual.getApellido());
        txtEmail.setText(empleadoActual.getEmail());
        txtTelefono.setText(empleadoActual.getTelefono());
        txtUsuario.setText(empleadoActual.getUsuario());
        cmbCargo.setValue(empleadoActual.getCargo());

        if (empleadoActual.getFechaContratacion() != null) {
            dpFechaContratacion.setValue(empleadoActual.getFechaContratacion().toLocalDate());
        }

        chkActivo.setSelected(empleadoActual.isActivo());

        // Deshabilitar usuario en edición
        txtUsuario.setDisable(true);
    }

    @FXML
    private void handleGuardar() {
        if (!validarCampos()) {
            return;
        }

        // Crear o actualizar empleado
        Empleado empleado;
        if (esEdicion) {
            empleado = empleadoActual;
        } else {
            empleado = new Empleado();
            empleado.setUsuario(txtUsuario.getText().trim());
        }

        empleado.setNombre(txtNombre.getText().trim());
        empleado.setApellido(txtApellido.getText().trim());
        empleado.setEmail(txtEmail.getText().trim());
        empleado.setTelefono(txtTelefono.getText().trim());
        empleado.setCargo(cmbCargo.getValue());
        empleado.setFechaContratacion(Date.valueOf(dpFechaContratacion.getValue()));
        empleado.setActivo(chkActivo.isSelected());

        boolean exito;
        if (esEdicion) {
            exito = empleadoDAO.actualizar(empleado);

            // Si cambió la contraseña, actualizarla
            String nuevaContrasena = txtContrasena.getText();
            if (!nuevaContrasena.isEmpty()) {
                empleadoDAO.actualizarContrasena(empleado.getIdEmpleado(), nuevaContrasena);
            }
        } else {
            empleado.setContrasena(txtContrasena.getText());
            exito = empleadoDAO.insertar(empleado);
        }

        if (exito) {
            mostrarExito(esEdicion ? "Empleado actualizado correctamente" : "Empleado registrado correctamente");
            dashboardController.refrescarEmpleados();
            cerrar();
        } else {
            mostrarError("Error al guardar el empleado");
        }
    }

    private boolean validarCampos() {
        String nombre = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String email = txtEmail.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String usuario = txtUsuario.getText().trim();
        String contrasena = txtContrasena.getText();
        String confirmar = txtConfirmarContrasena.getText();

        // Validaciones básicas
        if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty() ||
                telefono.isEmpty() || usuario.isEmpty()) {
            mostrarError("Complete todos los campos obligatorios");
            return false;
        }

        // Validar email
        if (!email.contains("@")) {
            mostrarError("Email inválido");
            return false;
        }



        // **VALIDACIÓN 4: Formato de teléfono**
        if (!TELEFONO_PATTERN.matcher(telefono).matches()) {
            mostrarError("El teléfono debe tener el formato: 0000-0000 (ejemplo: 7890-1234)");
            txtTelefono.requestFocus();

            return false;
        }

        // Validar contraseña (solo en nuevo o si se está cambiando)
        if (!esEdicion) {
            // Nuevo empleado: contraseña obligatoria
            if (contrasena.isEmpty()) {
                mostrarError("La contraseña es obligatoria");
                return false;
            }

            if (contrasena.length() < 6) {
                mostrarError("La contraseña debe tener al menos 6 caracteres");
                return false;
            }

            if (!contrasena.equals(confirmar)) {
                mostrarError("Las contraseñas no coinciden");
                return false;
            }

            // Verificar si usuario ya existe
            if (empleadoDAO.existeUsuario(usuario)) {
                mostrarError("El nombre de usuario ya está registrado");
                return false;
            }

            // Verificar si email ya existe
            if (empleadoDAO.existeEmail(email)) {
                mostrarError("El email ya está registrado");
                return false;
            }
        } else {
            // Edición: validar solo si se está cambiando la contraseña
            if (!contrasena.isEmpty()) {
                if (contrasena.length() < 6) {
                    mostrarError("La contraseña debe tener al menos 6 caracteres");
                    return false;
                }

                if (!contrasena.equals(confirmar)) {
                    mostrarError("Las contraseñas no coinciden");
                    return false;
                }
            }
        }

        // Validar cargo
        if (cmbCargo.getValue() == null) {
            mostrarError("Seleccione un cargo");
            return false;
        }

        // Validar fecha
        if (dpFechaContratacion.getValue() == null) {
            mostrarError("Seleccione la fecha de contratación");
            return false;
        }

        if (dpFechaContratacion.getValue().isAfter(LocalDate.now())) {
            mostrarError("La fecha de contratación no puede ser futura");
            return false;
        }

        return true;
    }

    @FXML
    private void handleCancelar() {
        cerrar();
    }

    private void cerrar() {
        Stage stage = (Stage) btnGuardar.getScene().getWindow();
        stage.close();
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarExito(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}