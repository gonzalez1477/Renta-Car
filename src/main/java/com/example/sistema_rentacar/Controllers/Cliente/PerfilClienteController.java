package com.example.sistema_rentacar.Controllers.Cliente;

import com.example.sistema_rentacar.Repository.ClienteRepository;
import com.example.sistema_rentacar.Modelos.Cliente;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.example.sistema_rentacar.Utilidades.EncriptarContraseña;

import java.sql.Date;
import java.time.LocalDate;
import java.time.Period;
import java.util.regex.Pattern;

public class PerfilClienteController {

    @FXML private Label lblTitulo;
    @FXML private Label lblNombreCompleto;
    @FXML private Label lblEmail;
    @FXML private Label lblTelefono;
    @FXML private Label lblDireccion;
    @FXML private Label lblDui;
    @FXML private Label lblLicencia;
    @FXML private Label lblFechaNacimiento;
    @FXML private Label lblUsuario;
    @FXML private Label lblFechaRegistro;

    @FXML private VBox vboxVisualizacion;
    @FXML private VBox vboxEdicion;

    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTelefono;
    @FXML private TextArea txtDireccion;
    @FXML private TextField txtDui;
    @FXML private TextField txtLicencia;
    @FXML private DatePicker dpFechaNacimiento;

    @FXML private CheckBox chkCambiarContrasena;
    @FXML private VBox vboxContrasena;
    @FXML private PasswordField txtContrasenaActual;
    @FXML private PasswordField txtContrasenaNueva;
    @FXML private PasswordField txtConfirmarContrasena;

    @FXML private Button btnEditar;
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;
    @FXML private Button btnVolver;

    private Cliente clienteActual;
    private ClienteRepository clienteDAO;
    private boolean modoEdicion = false;

    // Patrones de validación
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    private static final Pattern TELEFONO_PATTERN = Pattern.compile("^\\d{4}-\\d{4}$");
    private static final Pattern DUI_PATTERN = Pattern.compile("^\\d{8}-\\d{1}$");
    private static final Pattern LICENCIA_PATTERN = Pattern.compile("^\\d{8}-\\d{1}$");
    private static final Pattern NOMBRE_PATTERN = Pattern.compile("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]{2,50}$");

    @FXML
    public void initialize() {
        clienteDAO = new ClienteRepository();

        // Ocultar campos de edición inicialmente
        vboxEdicion.setVisible(false);
        vboxEdicion.setManaged(false);
        vboxContrasena.setVisible(false);
        vboxContrasena.setManaged(false);

        // Listener para mostrar/ocultar campos de contraseña
        chkCambiarContrasena.selectedProperty().addListener((obs, old, nuevo) -> {
            vboxContrasena.setVisible(nuevo);
            vboxContrasena.setManaged(nuevo);

            // Limpiar campos si se desmarca
            if (!nuevo) {
                txtContrasenaActual.clear();
                txtContrasenaNueva.clear();
                txtConfirmarContrasena.clear();
            }
        });

        // Configurar validaciones en tiempo real
        configurarValidacionesEnTiempoReal();
    }

    private void configurarValidacionesEnTiempoReal() {
        // Validación para campos numéricos
        txtTelefono.textProperty().addListener((obs, old, newVal) -> {
            if (newVal != null && !newVal.matches("[0-9-]*")) {
                txtTelefono.setText(old);
            }
        });

        txtDui.textProperty().addListener((obs, old, newVal) -> {
            if (newVal != null && !newVal.matches("[0-9-]*")) {
                txtDui.setText(old);
            }
        });

        txtLicencia.textProperty().addListener((obs, old, newVal) -> {
            if (newVal != null && !newVal.matches("[0-9-]*")) {
                txtLicencia.setText(old);
            }
        });

        // Validación para campos de texto (solo letras)
        txtNombre.textProperty().addListener((obs, old, newVal) -> {
            if (newVal != null && !newVal.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]*")) {
                txtNombre.setText(old);
            }
        });

        txtApellido.textProperty().addListener((obs, old, newVal) -> {
            if (newVal != null && !newVal.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]*")) {
                txtApellido.setText(old);
            }
        });
    }

    public void setCliente(Cliente cliente) {
        this.clienteActual = cliente;
        cargarDatos();
    }

    private void cargarDatos() {
        // Actualizar información del cliente desde la BD
        Cliente clienteActualizado = clienteDAO.obtenerPorId(clienteActual.getIdCliente());
        if (clienteActualizado != null) {
            this.clienteActual = clienteActualizado;
        }

        // Mostrar en labels (modo visualización)
        lblTitulo.setText("Perfil de " + clienteActual.getNombreCompleto());
        lblNombreCompleto.setText(clienteActual.getNombreCompleto());
        lblEmail.setText(clienteActual.getEmail());
        lblTelefono.setText(clienteActual.getTelefono());
        lblDireccion.setText(clienteActual.getDireccion() != null ? clienteActual.getDireccion() : "No especificada");
        lblDui.setText(clienteActual.getDui());
        lblLicencia.setText(clienteActual.getLicencia());

        if (clienteActual.getFechaNacimiento() != null) {
            lblFechaNacimiento.setText(clienteActual.getFechaNacimiento().toString());
        }

        lblUsuario.setText(clienteActual.getUsuario());

        if (clienteActual.getFechaRegistro() != null) {
            lblFechaRegistro.setText(clienteActual.getFechaRegistro().toString());
        }

        // Cargar en campos de edición
        txtNombre.setText(clienteActual.getNombre());
        txtApellido.setText(clienteActual.getApellido());
        txtEmail.setText(clienteActual.getEmail());
        txtTelefono.setText(clienteActual.getTelefono());
        txtDireccion.setText(clienteActual.getDireccion());
        txtDui.setText(clienteActual.getDui());
        txtLicencia.setText(clienteActual.getLicencia());

        if (clienteActual.getFechaNacimiento() != null) {
            dpFechaNacimiento.setValue(clienteActual.getFechaNacimiento().toLocalDate());
        }
    }

    @FXML
    private void handleEditar() {
        activarModoEdicion();
    }

    private void activarModoEdicion() {
        modoEdicion = true;

        // Mostrar campos de edición
        vboxVisualizacion.setVisible(false);
        vboxVisualizacion.setManaged(false);
        vboxEdicion.setVisible(true);
        vboxEdicion.setManaged(true);

        // Cambiar botones
        btnEditar.setVisible(false);
        btnGuardar.setVisible(true);
        btnCancelar.setVisible(true);

        // Cambiar título
        lblTitulo.setText("Editar Perfil");
    }

    @FXML
    private void handleGuardar() {
        // Validar todos los campos
        if (!validarCampos()) {
            return;
        }

        // Verificar si hay cambios reales
        if (!hayCambios()) {
            mostrarAdvertencia("No se detectaron cambios para guardar");
            return;
        }

        // Confirmar cambios en datos sensibles
        if (hayCambiosSensibles()) {
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmar Cambios");
            confirmacion.setHeaderText("Está modificando información sensible");
            confirmacion.setContentText("¿Está seguro de cambiar DUI, Licencia o Email?\nEstos cambios son importantes.");

            if (confirmacion.showAndWait().get() != ButtonType.OK) {
                return;
            }
        }

        // Actualizar datos del cliente
        clienteActual.setNombre(capitalizarNombre(txtNombre.getText().trim()));
        clienteActual.setApellido(capitalizarNombre(txtApellido.getText().trim()));
        clienteActual.setEmail(txtEmail.getText().trim().toLowerCase());
        clienteActual.setTelefono(txtTelefono.getText().trim());
        clienteActual.setDireccion(txtDireccion.getText().trim());
        clienteActual.setDui(txtDui.getText().trim());
        clienteActual.setLicencia(txtLicencia.getText().trim());

        if (dpFechaNacimiento.getValue() != null) {
            clienteActual.setFechaNacimiento(Date.valueOf(dpFechaNacimiento.getValue()));
        }

        // Actualizar en la base de datos
        boolean exito = clienteDAO.actualizar(clienteActual);

        // ✅ CORRECCIÓN: Encriptar la contraseña ANTES de enviarla
        if (chkCambiarContrasena.isSelected() && exito) {
            String nuevaContrasenaPlana = txtContrasenaNueva.getText();
            String nuevaContrasenaEncriptada = EncriptarContraseña.encryptPassword(nuevaContrasenaPlana);
            exito = clienteDAO.actualizarContrasena(clienteActual.getIdCliente(), nuevaContrasenaEncriptada);
        }

        if (exito) {
            mostrarExito("Perfil actualizado correctamente");
            desactivarModoEdicion();
            cargarDatos(); // Recargar datos actualizados
        } else {
            mostrarError("Error al actualizar el perfil. Por favor intente nuevamente");
        }
    }

    @FXML
    private void handleCancelar() {

        // Confirmar cancelación si hay cambios
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Cancelar Edición");
        confirmacion.setHeaderText("¿Descartar cambios?");
        confirmacion.setContentText("Los cambios no guardados se perderán");

        if (confirmacion.showAndWait().get() == ButtonType.OK) {
            desactivarModoEdicion();
            cargarDatos();
        }
    }

    private void desactivarModoEdicion() {
        modoEdicion = false;

        // Mostrar modo visualización
        vboxVisualizacion.setVisible(true);
        vboxVisualizacion.setManaged(true);
        vboxEdicion.setVisible(false);
        vboxEdicion.setManaged(false);

        // Cambiar botones
        btnEditar.setVisible(true);
        btnGuardar.setVisible(false);
        btnCancelar.setVisible(false);

        // Ocultar sección de contraseña
        chkCambiarContrasena.setSelected(false);


        lblTitulo.setText("Perfil de " + clienteActual.getNombreCompleto());
    }

    private boolean validarCampos() {
        String nombre = txtNombre.getText().trim();
        String apellido = txtApellido.getText().trim();
        String email = txtEmail.getText().trim().toLowerCase();
        String telefono = txtTelefono.getText().trim();
        String direccion = txtDireccion.getText().trim();
        String dui = txtDui.getText().trim();
        String licencia = txtLicencia.getText().trim();
        LocalDate fechaNac = dpFechaNacimiento.getValue();

        // ** Campos vacíos**
        if (nombre.isEmpty()) {
            mostrarError("Por favor ingrese su nombre");
            txtNombre.requestFocus();
            return false;
        }
        if (apellido.isEmpty()) {
            mostrarError("Por favor ingrese su apellido");
            txtApellido.requestFocus();
            return false;
        }
        if (email.isEmpty()) {
            mostrarError("Por favor ingrese su correo electrónico");
            txtEmail.requestFocus();
            return false;
        }
        if (telefono.isEmpty()) {
            mostrarError("Por favor ingrese su número de teléfono");
            txtTelefono.requestFocus();
            return false;
        }
        if (direccion.isEmpty()) {
            mostrarError("Por favor ingrese su dirección");
            txtDireccion.requestFocus();
            return false;
        }
        if (dui.isEmpty()) {
            mostrarError("Por favor ingrese su número de DUI");
            txtDui.requestFocus();
            return false;
        }
        if (licencia.isEmpty()) {
            mostrarError("Por favor ingrese su número de licencia");
            txtLicencia.requestFocus();
            return false;
        }
        if (fechaNac == null) {
            mostrarError("Por favor seleccione su fecha de nacimiento");
            dpFechaNacimiento.requestFocus();
            return false;
        }

        // ** Formato de nombre y apellido**
        if (!NOMBRE_PATTERN.matcher(nombre).matches()) {
            mostrarError("El nombre solo debe contener letras y tener entre 2 y 50 caracteres");
            txtNombre.requestFocus();
            return false;
        }
        if (!NOMBRE_PATTERN.matcher(apellido).matches()) {
            mostrarError("El apellido solo debe contener letras y tener entre 2 y 50 caracteres");
            txtApellido.requestFocus();
            return false;
        }

        // **Formato de email**
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            mostrarError("El formato del correo no es válido. Ejemplo: usuario@correo.com");
            txtEmail.requestFocus();
            return false;
        }

        // **Formato de teléfono**
        if (!TELEFONO_PATTERN.matcher(telefono).matches()) {
            mostrarError("El teléfono debe tener el formato: 0000-0000 (ejemplo: 7890-1234)");
            txtTelefono.requestFocus();
            return false;
        }

        // ** Formato de DUI**
        if (!DUI_PATTERN.matcher(dui).matches()) {
            mostrarError("El DUI debe tener el formato: 00000000-0 (ejemplo: 12345678-9)");
            txtDui.requestFocus();
            return false;
        }

        // ** Formato de licencia salvadoreña**
        if (!LICENCIA_PATTERN.matcher(licencia).matches()) {
            mostrarError("La licencia debe ser de El Salvador y tener el formato: 00000000-0\n(8 dígitos + guión + 1 dígito)\nEjemplo: 12345678-9\nVerifique que su licencia esté vigente");
            txtLicencia.requestFocus();
            return false;
        }

        // **Fecha de nacimiento (edad mínima 18 años)**
        LocalDate fechaActual = LocalDate.now();
        Period edad = Period.between(fechaNac, fechaActual);

        if (fechaNac.isAfter(fechaActual)) {
            mostrarError("La fecha de nacimiento no puede ser una fecha futura");
            dpFechaNacimiento.requestFocus();
            return false;
        }

        if (edad.getYears() < 18) {
            mostrarError("Debe tener al menos 18 años para usar el sistema. Su edad actual es: " + edad.getYears() + " años");
            dpFechaNacimiento.requestFocus();
            return false;
        }

        if (edad.getYears() > 100) {
            mostrarError("Por favor verifique la fecha de nacimiento ingresada");
            dpFechaNacimiento.requestFocus();
            return false;
        }

        // ** Cambio de contraseña**
        if (direccion.length() < 10) {
            mostrarError("Por favor ingrese una dirección más completa (mínimo 10 caracteres)");
            txtDireccion.requestFocus();
            return false;
        }

        if (direccion.length() > 200) {
            mostrarError("La dirección no puede exceder 200 caracteres");
            txtDireccion.requestFocus();
            return false;
        }

        // ** Email único (si cambió el email)**
        if (!email.equals(clienteActual.getEmail().toLowerCase())) {
            if (clienteDAO.existeEmail(email)) {
                mostrarError("El correo '" + email + "' ya está registrado por otro usuario");
                txtEmail.requestFocus();
                txtEmail.selectAll();
                return false;
            }
        }

        // ** DUI único (si cambió el DUI)**
        if (!dui.equals(clienteActual.getDui())) {
            if (clienteDAO.existeDui(dui)) {
                mostrarError("El DUI '" + dui + "' ya está registrado por otro usuario");
                txtDui.requestFocus();
                txtDui.selectAll();
                return false;
            }
        }

        // ** Licencia única (si cambió la licencia)**
        if (!licencia.equals(clienteActual.getLicencia())) {
            if (clienteDAO.existeLicencia(licencia)) {
                mostrarError("La licencia '" + licencia + "' ya está registrada por otro usuario");
                txtLicencia.requestFocus();
                txtLicencia.selectAll();
                return false;
            }
        }

        // ** Cambio de contraseña**
        if (chkCambiarContrasena.isSelected()) {
            String contrasenaActual = txtContrasenaActual.getText();
            String nuevaContrasena = txtContrasenaNueva.getText();
            String confirmar = txtConfirmarContrasena.getText();

            if (contrasenaActual.isEmpty()) {
                mostrarError("Ingrese su contraseña actual");
                txtContrasenaActual.requestFocus();
                return false;
            }
            if (nuevaContrasena.isEmpty()) {
                mostrarError("Ingrese la nueva contraseña");
                txtContrasenaNueva.requestFocus();
                return false;
            }
            if (confirmar.isEmpty()) {
                mostrarError("Confirme la nueva contraseña");
                txtConfirmarContrasena.requestFocus();
                return false;
            }

            // Verificar contraseña actual usando BCrypt
            if (!clienteDAO.verificarContrasena(clienteActual.getIdCliente(), contrasenaActual)) {
                mostrarError("La contraseña actual es incorrecta");
                txtContrasenaActual.requestFocus();
                txtContrasenaActual.selectAll();
                return false;
            }

            if (nuevaContrasena.length() < 6) {
                mostrarError("La nueva contraseña debe tener al menos 6 caracteres");
                txtContrasenaNueva.requestFocus();
                return false;
            }

            // Validar fortaleza de contraseña
            if (!esContrasenaSegura(nuevaContrasena)) {
                mostrarError("La contraseña debe tener al menos:\n- Una letra MAYÚSCULA\n- Una letra minúscula\n- Un número");
                txtContrasenaNueva.requestFocus();
                return false;
            }

            if (!nuevaContrasena.equals(confirmar)) {
                mostrarError("Las contraseñas nuevas no coinciden");
                txtConfirmarContrasena.requestFocus();
                return false;
            }

            // Verificar que no sea igual a la actual
            if (clienteDAO.verificarContrasena(clienteActual.getIdCliente(), nuevaContrasena)) {
                mostrarError("La nueva contraseña debe ser diferente a la actual");
                txtContrasenaNueva.requestFocus();
                return false;
            }
        }

        return true;
    }

    // Verificar si hay cambios reales
    private boolean hayCambios() {
        String nombre = capitalizarNombre(txtNombre.getText().trim());
        String apellido = capitalizarNombre(txtApellido.getText().trim());
        String email = txtEmail.getText().trim().toLowerCase();
        String telefono = txtTelefono.getText().trim();
        String direccion = txtDireccion.getText().trim();
        String dui = txtDui.getText().trim();
        String licencia = txtLicencia.getText().trim();
        LocalDate fechaNac = dpFechaNacimiento.getValue();

        boolean cambiosDatos = !nombre.equals(clienteActual.getNombre()) ||
                !apellido.equals(clienteActual.getApellido()) ||
                !email.equals(clienteActual.getEmail().toLowerCase()) ||
                !telefono.equals(clienteActual.getTelefono()) ||
                !direccion.equals(clienteActual.getDireccion()) ||
                !dui.equals(clienteActual.getDui()) ||
                !licencia.equals(clienteActual.getLicencia());

        if (clienteActual.getFechaNacimiento() != null && fechaNac != null) {
            cambiosDatos = cambiosDatos ||
                    !fechaNac.equals(clienteActual.getFechaNacimiento().toLocalDate());
        }

        return cambiosDatos || chkCambiarContrasena.isSelected();
    }

    // Verificar si hay cambios en datos sensibles
    private boolean hayCambiosSensibles() {
        String email = txtEmail.getText().trim().toLowerCase();
        String dui = txtDui.getText().trim();
        String licencia = txtLicencia.getText().trim();

        return !email.equals(clienteActual.getEmail().toLowerCase()) ||
                !dui.equals(clienteActual.getDui()) ||
                !licencia.equals(clienteActual.getLicencia());
    }

   //validar contraseña segura
    private boolean esContrasenaSegura(String contrasena) {
        boolean tieneMayuscula = contrasena.matches(".*[A-Z].*");
        boolean tieneMinuscula = contrasena.matches(".*[a-z].*");
        boolean tieneNumero = contrasena.matches(".*[0-9].*");
        return tieneMayuscula && tieneMinuscula && tieneNumero;
    }

    //capitalizar nombres
    private String capitalizarNombre(String texto) {
        if (texto == null || texto.isEmpty()) return texto;
        String[] palabras = texto.toLowerCase().split("\\s+");
        StringBuilder resultado = new StringBuilder();
        for (String palabra : palabras) {
            if (!palabra.isEmpty()) {
                resultado.append(Character.toUpperCase(palabra.charAt(0)))
                        .append(palabra.substring(1))
                        .append(" ");
            }
        }
        return resultado.toString().trim();
    }

    @FXML
    private void handleVolver() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sistema_rentacar/Views/cliente/CatalogoCliente.fxml"));
            Parent root = loader.load();

            CatalogoClienteController controller = loader.getController();
            controller.setDatosCliente(clienteActual.getIdCliente(), clienteActual.getNombreCompleto());

            Stage stage = (Stage) btnVolver.getScene().getWindow();
            stage.setScene(new Scene(root, 1200, 800));
            stage.setTitle("Catálogo - Renta Car");

        } catch (Exception e) {
            System.err.println("Error al volver al catálogo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error de Validación");
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

    private void mostrarAdvertencia(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Advertencia");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}