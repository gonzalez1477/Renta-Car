package com.example.sistema_rentacar.Controllers.Cliente;

import com.example.sistema_rentacar.Repository.ClienteRepository;
import com.example.sistema_rentacar.Modelos.Cliente;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.sql.Date;
import java.time.LocalDate;
import java.time.Period;
import java.util.regex.Pattern;

public class RegistroClienteController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTelefono;
    @FXML private TextArea txtDireccion;
    @FXML private TextField txtDui;
    @FXML private TextField txtLicencia;
    @FXML private DatePicker dpFechaNacimiento;
    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtContrasena;
    @FXML private PasswordField txtConfirmarContrasena;
    @FXML private Label lblError;
    @FXML private Label lblExito;
    @FXML private HBox hboxError;
    @FXML private HBox hboxExito;
    @FXML private Button btnRegistrar;

    private ClienteRepository clienteDAO;

    // Patrones de validación
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    private static final Pattern TELEFONO_PATTERN = Pattern.compile("^\\d{4}-\\d{4}$");
    private static final Pattern DUI_PATTERN = Pattern.compile("^\\d{8}-\\d{1}$");
    private static final Pattern NOMBRE_PATTERN = Pattern.compile("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]{2,50}$");
    private static final Pattern USUARIO_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{4,20}$");

    private static final Pattern LICENCIA_PATTERN = Pattern.compile("^\\d{8}-\\d{1}$");
    @FXML
    public void initialize() {
        clienteDAO = new ClienteRepository();
        ocultarMensajes();
        configurarValidacionesEnTiempoReal();
    }

    private void configurarValidacionesEnTiempoReal() {
        // Validación en tiempo real para campos numéricos
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

        // Validación de longitud de nombre y apellido
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

        txtLicencia.textProperty().addListener((obs, old, newVal) -> {
            if (newVal != null && !newVal.matches("[0-9-]*")) {
                txtLicencia.setText(old);
            }
        });
    }

    @FXML
    private void handleRegistrar() {
        System.out.println("Iniciando proceso de registro...");
        ocultarMensajes();

        // Deshabilitar botón temporalmente para evitar múltiples clicks
        btnRegistrar.setDisable(true);

        try {
            // Obtener y limpiar datos
            String nombre = txtNombre.getText().trim();
            String apellido = txtApellido.getText().trim();
            String email = txtEmail.getText().trim().toLowerCase();
            String telefono = txtTelefono.getText().trim();
            String direccion = txtDireccion.getText().trim();
            String dui = txtDui.getText().trim();
            String licencia = txtLicencia.getText().trim();
            LocalDate fechaNac = dpFechaNacimiento.getValue();
            String usuario = txtUsuario.getText().trim();
            String contrasena = txtContrasena.getText();
            String confirmar = txtConfirmarContrasena.getText();

            // **VALIDACIÓN  Campos vacíos**
            if (nombre.isEmpty()) {
                mostrarError("Por favor ingrese su nombre");
                txtNombre.requestFocus();
                btnRegistrar.setDisable(false);
                return;
            }
            if (apellido.isEmpty()) {
                mostrarError("Por favor ingrese su apellido");
                txtApellido.requestFocus();
                btnRegistrar.setDisable(false);
                return;
            }
            if (email.isEmpty()) {
                mostrarError("Por favor ingrese su correo electrónico");
                txtEmail.requestFocus();
                btnRegistrar.setDisable(false);
                return;
            }
            if (telefono.isEmpty()) {
                mostrarError("Por favor ingrese su número de teléfono");
                txtTelefono.requestFocus();
                btnRegistrar.setDisable(false);
                return;
            }
            if (direccion.isEmpty()) {
                mostrarError("Por favor ingrese su dirección");
                txtDireccion.requestFocus();
                btnRegistrar.setDisable(false);
                return;
            }
            if (dui.isEmpty()) {
                mostrarError("Por favor ingrese su número de DUI");
                txtDui.requestFocus();
                btnRegistrar.setDisable(false);
                return;
            }
            if (licencia.isEmpty()) {
                mostrarError("Por favor ingrese su número de licencia de conducir");
                txtLicencia.requestFocus();
                btnRegistrar.setDisable(false);
                return;
            }
            if (fechaNac == null) {
                mostrarError("Por favor seleccione su fecha de nacimiento");
                dpFechaNacimiento.requestFocus();
                btnRegistrar.setDisable(false);
                return;
            }
            if (usuario.isEmpty()) {
                mostrarError("Por favor elija un nombre de usuario");
                txtUsuario.requestFocus();
                btnRegistrar.setDisable(false);
                return;
            }
            if (contrasena.isEmpty()) {
                mostrarError("Por favor ingrese una contraseña");
                txtContrasena.requestFocus();
                btnRegistrar.setDisable(false);
                return;
            }
            if (confirmar.isEmpty()) {
                mostrarError("Por favor confirme su contraseña");
                txtConfirmarContrasena.requestFocus();
                btnRegistrar.setDisable(false);
                return;
            }

            // **Formato de nombre y apellido**
            if (!NOMBRE_PATTERN.matcher(nombre).matches()) {
                mostrarError("El nombre solo debe contener letras y tener mínimo 2 caracteres");
                txtNombre.requestFocus();
                btnRegistrar.setDisable(false);
                return;
            }
            if (!NOMBRE_PATTERN.matcher(apellido).matches()) {
                mostrarError("El apellido solo debe contener letras y tener mínimo 2 caracteres");
                txtApellido.requestFocus();
                btnRegistrar.setDisable(false);
                return;
            }

            // **Formato de email**
            if (!EMAIL_PATTERN.matcher(email).matches()) {
                mostrarError("El formato del correo no es válido. Ejemplo: usuario@correo.com");
                txtEmail.requestFocus();
                btnRegistrar.setDisable(false);
                return;
            }

            // **Formato de teléfono**
            if (!TELEFONO_PATTERN.matcher(telefono).matches()) {
                mostrarError("El teléfono debe tener el formato: 0000-0000 (ejemplo: 7890-1234)");
                txtTelefono.requestFocus();
                btnRegistrar.setDisable(false);
                return;
            }

            // **Formato de DUI**
            if (!DUI_PATTERN.matcher(dui).matches()) {
                mostrarError("El DUI debe tener el formato: 00000000-0 (ejemplo: 12345678-9)");
                txtDui.requestFocus();
                btnRegistrar.setDisable(false);
                return;
            }

            // ** Licencia de conducir**
            if (licencia.length() < 5) {
                mostrarError("El número de licencia debe tener al menos 5 caracteres");
                txtLicencia.requestFocus();
                btnRegistrar.setDisable(false);
                return;
            }

            // Validar formato de licencia salvadoreña
            if (!LICENCIA_PATTERN.matcher(licencia).matches()) {

                //podriamos cambiarla por una alerta  ( hacer cambios despues   --> victor)
                mostrarError("La licencia debe ser de El Salvador y tener el formato: 00000000-0 (8 dígitos, guión, 1 dígito)\nEjemplo: 12345678-9\nVerifique que su licencia esté vigente");
                txtLicencia.requestFocus();
                btnRegistrar.setDisable(false);
                return;
            }

            // ** Fecha de nacimiento (edad mínima 18 años)**
            LocalDate fechaActual = LocalDate.now();
            Period edad = Period.between(fechaNac, fechaActual);

            if (fechaNac.isAfter(fechaActual)) {
                mostrarError("La fecha de nacimiento no puede ser una fecha futura");
                dpFechaNacimiento.requestFocus();
                btnRegistrar.setDisable(false);
                return;
            }

            if (edad.getYears() < 18) {
                mostrarError("Debe tener al menos 18 años para registrarse. Su edad actual es: " + edad.getYears() + " años");
                dpFechaNacimiento.requestFocus();
                btnRegistrar.setDisable(false);
                return;
            }

            if (edad.getYears() > 100) {
                mostrarError("Por favor verifique la fecha de nacimiento ingresada");
                dpFechaNacimiento.requestFocus();
                btnRegistrar.setDisable(false);
                return;
            }

            // **Dirección**
            if (direccion.length() < 10) {
                mostrarError("Por favor ingrese una dirección más completa (mínimo 10 caracteres)");
                txtDireccion.requestFocus();
                btnRegistrar.setDisable(false);
                return;
            }

            // **Formato de usuario**
            if (!USUARIO_PATTERN.matcher(usuario).matches()) {
                mostrarError("El usuario debe tener entre 4 y 20 caracteres (solo letras, números y guión bajo)");
                txtUsuario.requestFocus();
                btnRegistrar.setDisable(false);
                return;
            }

            //  Longitud de contraseña**
            if (contrasena.length() < 6) {
                mostrarError("La contraseña debe tener al menos 6 caracteres");
                txtContrasena.requestFocus();
                btnRegistrar.setDisable(false);
                return;
            }

            // Fortaleza de contraseña**
            if (!esContrasenaSegura(contrasena)) {
                mostrarError("La contraseña debe tener al menos: una letra MAYÚSCULA, una minúscula y un número");
                txtContrasena.requestFocus();
                btnRegistrar.setDisable(false);
                return;
            }

            //  Contraseñas coinciden**
            if (!contrasena.equals(confirmar)) {
                mostrarError("Las contraseñas no coinciden. Por favor verifique");
                txtConfirmarContrasena.requestFocus();
                btnRegistrar.setDisable(false);
                return;
            }

            //  Usuario ya existe**
            if (clienteDAO.existeUsuario(usuario)) {
                mostrarError("El nombre de usuario '" + usuario + "' ya está en uso. Por favor elija otro");
                txtUsuario.requestFocus();
                txtUsuario.selectAll();
                btnRegistrar.setDisable(false);
                return;
            }

            // ** Email ya existe**
            if (clienteDAO.existeEmail(email)) {
                mostrarError("El correo '" + email + "' ya está registrado. Use otro correo o inicie sesión");
                txtEmail.requestFocus();
                txtEmail.selectAll();
                btnRegistrar.setDisable(false);
                return;
            }

            // Crear objeto Cliente
            Cliente cliente = new Cliente();
            cliente.setNombre(capitalizarNombre(nombre));
            cliente.setApellido(capitalizarNombre(apellido));
            cliente.setEmail(email);
            cliente.setTelefono(telefono);
            cliente.setDireccion(direccion);
            cliente.setDui(dui);
            cliente.setLicencia(licencia);
            cliente.setFechaNacimiento(Date.valueOf(fechaNac));
            cliente.setUsuario(usuario);
            cliente.setContrasena(contrasena);

            // Registrar en base de datos
            if (clienteDAO.registrar(cliente)) {
                mostrarExito("¡Registro completado exitosamente! Bienvenido " + nombre + ". Redirigiendo al inicio de sesión...");
                limpiarFormulario();

                // Esperar 3 segundos y regresar al login
                new Thread(() -> {
                    try {
                        Thread.sleep(3000);
                        javafx.application.Platform.runLater(this::handleVolverLogin);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else {
                mostrarError("Ocurrió un error al guardar sus datos. Por favor intente nuevamente");
                btnRegistrar.setDisable(false);
            }

        } catch (Exception e) {
            mostrarError("Error inesperado: " + e.getMessage() + ". Por favor contacte al soporte técnico");
            e.printStackTrace();
            btnRegistrar.setDisable(false);
        }
    }

    @FXML
    private void handleVolverLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sistema_rentacar/Views/cliente/LoginCliente.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnRegistrar.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login Cliente - VSB Renta Car");

        } catch (Exception e) {
            System.err.println("Error al volver al login: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Métodos auxiliares de validación
    private boolean esContrasenaSegura(String contrasena) {
        boolean tieneMayuscula = contrasena.matches(".*[A-Z].*");
        boolean tieneMinuscula = contrasena.matches(".*[a-z].*");
        boolean tieneNumero = contrasena.matches(".*[0-9].*");
        return tieneMayuscula && tieneMinuscula && tieneNumero;
    }

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

    private void limpiarFormulario() {
        txtNombre.clear();
        txtApellido.clear();
        txtEmail.clear();
        txtTelefono.clear();
        txtDireccion.clear();
        txtDui.clear();
        txtLicencia.clear();
        dpFechaNacimiento.setValue(null);
        txtUsuario.clear();
        txtContrasena.clear();
        txtConfirmarContrasena.clear();
    }

    private void ocultarMensajes() {
        if (hboxError != null) hboxError.setVisible(false);
        if (hboxExito != null) hboxExito.setVisible(false);
        if (lblError != null) lblError.setVisible(false);
        if (lblExito != null) lblExito.setVisible(false);
    }

    private void mostrarError(String mensaje) {
        lblError.setText(mensaje);
        if (hboxError != null) {
            hboxError.setVisible(true);
            lblError.setVisible(true);
        } else {
            lblError.setVisible(true);
        }

        // NO ocultar automáticamente - dejar que el usuario lo lea
        // El mensaje se ocultará cuando vuelva a presionar el botón
    }

    private void mostrarExito(String mensaje) {
        lblExito.setText(mensaje);
        if (hboxExito != null) {
            hboxExito.setVisible(true);
            lblExito.setVisible(true);
        } else {
            lblExito.setVisible(true);
        }
    }
}