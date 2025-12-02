package com.example.sistema_rentacar.Controllers.Empleado;

import com.example.sistema_rentacar.Repository.TipoVehiculoRepository;
import com.example.sistema_rentacar.Modelos.TipoVehiculo;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class FormularioTipoVehiculoController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtTarifa;
    @FXML private TextArea txtDescripcion;
    @FXML private Label lblTitulo;
    @FXML private Label lblNombreCarpeta;
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;

    private TipoVehiculoRepository tipoVehiculoDAO;
    private TipoVehiculo tipoActual;
    private DashboardEmpleadoController dashboardController;
    private boolean esEdicion;

    @FXML
    public void initialize() {
        tipoVehiculoDAO = new TipoVehiculoRepository();

        // Actualizar el nombre de carpeta en tiempo real
        txtNombre.textProperty().addListener((obs, old, nuevo) -> {
            if (nuevo != null && !nuevo.isEmpty()) {
                String carpeta = normalizarNombreCarpeta(nuevo);
                lblNombreCarpeta.setText("Carpeta de imágenes: /images/" + carpeta + "/");
            } else {
                lblNombreCarpeta.setText("Carpeta de imágenes: /images/");
            }
        });

        // Configurar TextField de tarifa para aceptar solo números y punto decimal
        configurarCampoNumerico();
    }

    private void configurarCampoNumerico() {
        txtTarifa.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*\\.?\\d*")) {
                txtTarifa.setText(oldValue);
            }
        });
    }

    public void setDatos(TipoVehiculo tipo, DashboardEmpleadoController controller) {
        this.dashboardController = controller;
        this.tipoActual = tipo;
        this.esEdicion = (tipo != null);

        if (esEdicion) {
            lblTitulo.setText("Editar Categoría de Vehículo");
            cargarDatos();
        } else {
            lblTitulo.setText("Nueva Categoría de Vehículo");
        }
    }

    private void cargarDatos() {
        txtNombre.setText(tipoActual.getNombreTipo());
        txtTarifa.setText(String.valueOf(tipoActual.getTarifaPorDia()));
        txtDescripcion.setText(tipoActual.getDescripcion());

        // Actualizar label de carpeta
        String carpeta = normalizarNombreCarpeta(tipoActual.getNombreTipo());
        lblNombreCarpeta.setText("Carpeta de imágenes: /images/" + carpeta + "/");
    }

    @FXML
    private void handleGuardar() {
        if (!validarCampos()) {
            return;
        }

        TipoVehiculo tipo;
        if (esEdicion) {
            tipo = tipoActual;
        } else {
            tipo = new TipoVehiculo();
        }

        tipo.setNombreTipo(txtNombre.getText().trim());
        tipo.setTarifaPorDia(Double.parseDouble(txtTarifa.getText().trim()));
        tipo.setDescripcion(txtDescripcion.getText().trim());

        boolean exito;
        if (esEdicion) {
            // Si está editando, verificar que el nuevo nombre no exista (excepto el actual)
            if (!tipo.getNombreTipo().equalsIgnoreCase(tipoActual.getNombreTipo())) {
                if (tipoVehiculoDAO.existeNombre(tipo.getNombreTipo())) {
                    mostrarAlertaNombreDuplicado(tipo.getNombreTipo());
                    return;
                }
            }
            exito = tipoVehiculoDAO.actualizar(tipo);
        } else {
            // Verificar que no exista el nombre
            if (tipoVehiculoDAO.existeNombre(tipo.getNombreTipo())) {
                mostrarAlertaNombreDuplicado(tipo.getNombreTipo());
                return;
            }
            exito = tipoVehiculoDAO.insertar(tipo);
        }

        if (exito) {
            mostrarExito(esEdicion ? "Categoría actualizada correctamente" : "Categoría creada correctamente");

            // Crear carpeta de imágenes
            crearCarpetaImagenes(tipo.getNombreTipo());

            if (dashboardController != null) {
                dashboardController.refrescarTiposVehiculo();
            }
            cerrar();
        } else {
            mostrarError("Error al guardar la categoría en la base de datos.\n\nPor favor, intente nuevamente.");
        }
    }

    private void crearCarpetaImagenes(String nombreTipo) {
        try {
            String rutaProyecto = System.getProperty("user.dir");
            String carpeta = normalizarNombreCarpeta(nombreTipo);

            java.nio.file.Path directorioDestino = java.nio.file.Paths.get(
                    rutaProyecto, "src", "main", "resources", "com", "example", "rentacarsystem", "images", carpeta
            );

            if (!java.nio.file.Files.exists(directorioDestino)) {
                java.nio.file.Files.createDirectories(directorioDestino);
                System.out.println("✓ Carpeta creada: " + directorioDestino);
            }
        } catch (Exception e) {
            System.err.println("Advertencia: No se pudo crear carpeta de imágenes: " + e.getMessage());

        }
    }

    private String normalizarNombreCarpeta(String nombre) {
        return nombre.toLowerCase()
                .replace("á", "a").replace("é", "e").replace("í", "i")
                .replace("ó", "o").replace("ú", "u")
                .replace(" ", "_")
                .replace("ñ", "n")
                .replaceAll("[^a-z0-9_]", "");
    }

    private boolean validarCampos() {
        String nombre = txtNombre.getText().trim();
        String tarifaStr = txtTarifa.getText().trim();
        String descripcion = txtDescripcion.getText().trim();

        // Validar nombre
        if (nombre.isEmpty()) {
            mostrarError("El nombre de la categoría es obligatorio");
            txtNombre.requestFocus();
            return false;
        }

        if (nombre.length() < 3) {
            mostrarError("El nombre de la categoría debe tener al menos 3 caracteres");
            txtNombre.requestFocus();
            return false;
        }

        if (nombre.length() > 50) {
            mostrarError("El nombre de la categoría no puede exceder 50 caracteres");
            txtNombre.requestFocus();
            return false;
        }

        // Validar que el nombre no contenga solo números
        if (nombre.matches("^[0-9]+$")) {
            mostrarError("El nombre de la categoría no puede contener solo números");
            txtNombre.requestFocus();
            return false;
        }

        // Validar tarifa
        if (tarifaStr.isEmpty()) {
            mostrarError("La tarifa por día es obligatoria");
            txtTarifa.requestFocus();
            return false;
        }

        try {
            double tarifa = Double.parseDouble(tarifaStr);

            if (tarifa <= 0) {
                mostrarError("La tarifa debe ser mayor a $0.00");
                txtTarifa.requestFocus();
                return false;
            }

            if (tarifa > 10000) {
                mostrarError("La tarifa no puede exceder $10,000.00");
                txtTarifa.requestFocus();
                return false;
            }

            // Validar que no tenga más de 2 decimales
            String[] partes = tarifaStr.split("\\.");
            if (partes.length > 1 && partes[1].length() > 2) {
                mostrarError("La tarifa no puede tener más de 2 decimales");
                txtTarifa.requestFocus();
                return false;
            }

        } catch (NumberFormatException e) {
            mostrarError("La tarifa debe ser un número válido");
            txtTarifa.requestFocus();
            return false;
        }


        if (descripcion.isEmpty()) {
            mostrarError("La descripción de la categoría es obligatoria.\n\n" +
                    "Por favor, agregue una descripción que explique las características " +
                    "de este tipo de vehículo.");
            txtDescripcion.requestFocus();
            return false;
        }

        if (descripcion.length() < 10) {
            mostrarError("La descripción debe tener al menos 10 caracteres.\n\n" +
                    "Por favor, proporcione una descripción más detallada.");
            txtDescripcion.requestFocus();
            return false;
        }

        if (descripcion.length() > 500) {
            mostrarError("La descripción no puede exceder 500 caracteres");
            txtDescripcion.requestFocus();
            return false;
        }

        return true;
    }


    private void mostrarAlertaNombreDuplicado(String nombre) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Nombre Duplicado");
        alert.setHeaderText("La categoría ya existe");
        alert.setContentText(
                "Ya existe una categoría con el nombre: " + nombre + "\n\n" +
                        "Por favor, ingrese un nombre diferente para esta categoría."
        );

        alert.showAndWait();

        // Enfocar el campo de nombre para que el usuario pueda corregir
        txtNombre.requestFocus();
        txtNombre.selectAll();
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