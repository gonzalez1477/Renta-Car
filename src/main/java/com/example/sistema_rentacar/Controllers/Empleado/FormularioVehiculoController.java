package com.example.sistema_rentacar.Controllers.Empleado;

import com.example.sistema_rentacar.Repository.TipoVehiculoRepository;
import com.example.sistema_rentacar.Repository.VehiculoRepository;
import com.example.sistema_rentacar.Modelos.Vehiculo;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;


public class FormularioVehiculoController {

    @FXML private TextField txtPlaca;
    @FXML private TextField txtMarca;
    @FXML private TextField txtModelo;
    @FXML private Spinner<Integer> spinnerAnio;
    @FXML private TextField txtColor;
    @FXML private Spinner<Integer> spinnerPasajeros;
    @FXML private ComboBox<String> cmbTransmision;
    @FXML private ComboBox<String> cmbCombustible;
    @FXML private CheckBox chkAireAcondicionado;
    @FXML private ComboBox<TipoVehiculoItem> cmbTipo;
    @FXML private ComboBox<String> cmbEstado;
    @FXML private TextField txtImagenUrl;
    @FXML private Button btnExaminar;
    @FXML private TextArea txtDescripcion;
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;
    @FXML private Label lblTitulo;
    @FXML private Label lblImagenInfo;
    @FXML private ImageView carImage;

    private VehiculoRepository vehiculoDAO;
    private Vehiculo vehiculoActual;
    private DashboardEmpleadoController dashboardController;
    private boolean esEdicion;
    private File archivoImagenSeleccionado;

    @FXML
    public void initialize() {
        vehiculoDAO = new VehiculoRepository();
        configurarControles();
        cargarTiposVehiculo();
    }

    private void configurarControles() {
        // Spinner año
        int anioActual = java.time.Year.now().getValue();
        SpinnerValueFactory<Integer> anioFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1990, anioActual + 1, anioActual);
        spinnerAnio.setValueFactory(anioFactory);

        // Spinner pasajeros
        SpinnerValueFactory<Integer> pasajerosFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(2, 12, 5);
        spinnerPasajeros.setValueFactory(pasajerosFactory);

        // ComboBox transmisión
        cmbTransmision.setItems(FXCollections.observableArrayList("Manual", "Automático"));
        cmbTransmision.setValue("Automático");

        // ComboBox combustible
        cmbCombustible.setItems(FXCollections.observableArrayList(
                "Gasolina", "Diésel", "Híbrido", "Eléctrico"
        ));
        cmbCombustible.setValue("Gasolina");

        // ComboBox estado
        cmbEstado.setItems(FXCollections.observableArrayList(
                "Disponible", "Alquilado", "Mantenimiento"
        ));
        cmbEstado.setValue("Disponible");

        chkAireAcondicionado.setSelected(true);
    }

    private void cargarTiposVehiculo() {
        TipoVehiculoRepository tipoDAO = new TipoVehiculoRepository();
        var tipos = tipoDAO.obtenerTodos();

        // Convertir los tipos de vehículo en TipoVehiculoItem usando Streams
        javafx.collections.ObservableList<TipoVehiculoItem> listaItems = FXCollections.observableArrayList(
                tipos.stream()
                        .map(t -> new TipoVehiculoItem(
                                t.getIdTipo(),
                                t.getNombreTipo(),
                                t.getTarifaPorDia()
                        ))
                        .toList()
        );

        // Asignar los ítems al ComboBox
        cmbTipo.setItems(listaItems);

        // Seleccionar el primero por defecto si la lista no está vacía
        if (!listaItems.isEmpty()) {
            cmbTipo.setValue(listaItems.get(0));
        }
    }

    public void setDatos(Vehiculo vehiculo, DashboardEmpleadoController controller) {
        this.dashboardController = controller;
        this.vehiculoActual = vehiculo;
        this.esEdicion = (vehiculo != null);

        if (esEdicion) {
            lblTitulo.setText("Editar Vehículo");
            cargarDatos();
        } else {
            lblTitulo.setText("Nuevo Vehículo");
        }
    }

    private void cargarDatos() {
        txtPlaca.setText(vehiculoActual.getPlaca());
        txtMarca.setText(vehiculoActual.getMarca());
        txtModelo.setText(vehiculoActual.getModelo());
        spinnerAnio.getValueFactory().setValue(vehiculoActual.getAnio());
        txtColor.setText(vehiculoActual.getColor());
        spinnerPasajeros.getValueFactory().setValue(vehiculoActual.getNumeroPassajeros());
        cmbTransmision.setValue(vehiculoActual.getTransmision());
        cmbCombustible.setValue(vehiculoActual.getTipoCombustible());
        chkAireAcondicionado.setSelected(vehiculoActual.isTieneAireAcondicionado());

        // Seleccionar tipo
        for (TipoVehiculoItem item : cmbTipo.getItems()) {
            if (item.getId() == vehiculoActual.getIdTipo()) {
                cmbTipo.setValue(item);
                break;
            }
        }

        cmbEstado.setValue(vehiculoActual.getEstado());
        txtImagenUrl.setText(vehiculoActual.getImagenUrl());
        txtDescripcion.setText(vehiculoActual.getDescripcion());

        // Deshabilitar placa si es edición
        txtPlaca.setDisable(true);

        // Deshabilitar estado si el vehículo está alquilado
        if ("Alquilado".equalsIgnoreCase(vehiculoActual.getEstado())) {
            cmbEstado.setDisable(true);
            cmbEstado.setStyle("-fx-opacity: 0.9;");


            Tooltip tooltip = new Tooltip("No se puede cambiar el estado mientras el vehículo está alquilado.\n" +
                    "Debe finalizar el alquiler desde la gestión de alquileres.");
            cmbEstado.setTooltip(tooltip);
        }
    }

    @FXML
    private void handleExaminar() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Imagen del Vehículo");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg", "*.gif"),
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg", "*.jpeg")
        );

        File file = fileChooser.showOpenDialog(btnExaminar.getScene().getWindow());
        if (file == null) return;

        archivoImagenSeleccionado = file;

        // Obtener tipo de vehículo seleccionado
        String categoria = obtenerNombreCarpetaCategoria();
        String extension = obtenerExtension(file.getName());

        // Generar nombre único basado en placa o timestamp
        String nombrePlaca = txtPlaca.getText().trim().isEmpty()
                ? "p" + System.currentTimeMillis()
                : txtPlaca.getText().trim().replace(" ", "_");

        String nombreArchivo = nombrePlaca.toLowerCase() + extension;
        String rutaRelativa = "/com/example/rentacarsystem/images/" + categoria + "/" + nombreArchivo;

        // Mostrar la ruta en el campo
        txtImagenUrl.setText(rutaRelativa);

        // Actualizar información visual
        if (lblImagenInfo != null) {
            lblImagenInfo.setText("✓ Imagen seleccionada: " + file.getName());
            lblImagenInfo.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
        }

        System.out.println("Imagen seleccionada: " + file.getAbsolutePath());
        System.out.println("Se copiará a: " + rutaRelativa);
    }

    private String obtenerNombreCarpetaCategoria() {
        TipoVehiculoItem tipo = cmbTipo.getValue();

        if (tipo == null || tipo.getNombre() == null) {
            return "general";
        }

        // Normalizar texto para usarlo como nombre de carpeta
        String nombre = tipo.getNombre().trim().toLowerCase();

        // Reemplazar espacios por guiones o eliminar acentos si fuera necesario
        nombre = nombre
                .replace("á", "a")
                .replace("é", "e")
                .replace("í", "i")
                .replace("ó", "o")
                .replace("ú", "u")
                .replace("ñ", "n")
                .replace(" ", "_");

        return nombre;
    }

    private String obtenerExtension(String nombreArchivo) {
        int lastIndexOf = nombreArchivo.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ".png"; // Extensión por defecto
        }
        return nombreArchivo.substring(lastIndexOf);
    }

    private boolean copiarImagenAProyecto() {
        if (archivoImagenSeleccionado == null) {
            // Si es edición y no se seleccionó nueva imagen, mantener la actual
            if (esEdicion) {
                return true;
            }
            return false;
        }

        try {
            String categoria = obtenerNombreCarpetaCategoria();
            String carpetaDestino = "src/main/resources/com/example/rentacarsystem/images/" + categoria;
            File dirDestino = new File(carpetaDestino);

            if (!dirDestino.exists()) {
                dirDestino.mkdirs();
                System.out.println("Directorio creado: " + dirDestino.getAbsolutePath());
            }

            String extension = obtenerExtension(archivoImagenSeleccionado.getName());
            String nombrePlaca = txtPlaca.getText().trim().isEmpty()
                    ? "p" + System.currentTimeMillis()
                    : txtPlaca.getText().trim().replace(" ", "_");

            String nombreArchivo = nombrePlaca.toLowerCase() + extension;
            File destino = new File(dirDestino, nombreArchivo);

            // Copiar imagen
            Files.copy(archivoImagenSeleccionado.toPath(), destino.toPath(), StandardCopyOption.REPLACE_EXISTING);

            // Guardar ruta relativa
            txtImagenUrl.setText("/com/example/rentacarsystem/images/" + categoria + "/" + nombreArchivo);
            System.out.println("Imagen copiada exitosamente a: " + destino.getAbsolutePath());

            if (lblImagenInfo != null) {
                lblImagenInfo.setText("✓ Imagen guardada correctamente");
                lblImagenInfo.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
            }

            return true;

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error al copiar imagen");
            alert.setHeaderText("No se pudo copiar la imagen");
            alert.setContentText(
                    "Error: " + e.getMessage() + "\n\n" +
                            "Verifique que:\n" +
                            "1. Tiene permisos de escritura en la carpeta del proyecto.\n" +
                            "2. La carpeta resources/images existe o puede crearse.\n" +
                            "3. El archivo de imagen no esté siendo usado por otra aplicación."
            );
            alert.showAndWait();

            return false;
        }
    }

    @FXML
    private void handleGuardar() {

        if (!validarCampos()) {
            return;
        }


        if (!copiarImagenAProyecto()) {
            mostrarError("No se pudo guardar la imagen del vehículo");
            return;
        }


        Vehiculo vehiculo;
        if (esEdicion) {
            vehiculo = vehiculoActual;
        } else {
            vehiculo = new Vehiculo();
            vehiculo.setPlaca(txtPlaca.getText().trim().toUpperCase());
        }


        vehiculo.setMarca(txtMarca.getText().trim());
        vehiculo.setModelo(txtModelo.getText().trim());
        vehiculo.setAnio(spinnerAnio.getValue());
        vehiculo.setColor(txtColor.getText().trim());
        vehiculo.setNumeroPassajeros(spinnerPasajeros.getValue());
        vehiculo.setTransmision(cmbTransmision.getValue());
        vehiculo.setTipoCombustible(cmbCombustible.getValue());
        vehiculo.setTieneAireAcondicionado(chkAireAcondicionado.isSelected());
        vehiculo.setIdTipo(cmbTipo.getValue().getId());
        vehiculo.setEstado(cmbEstado.getValue());
        vehiculo.setImagenUrl(txtImagenUrl.getText().trim());
        vehiculo.setDescripcion(txtDescripcion.getText().trim());


        boolean exito = esEdicion ? vehiculoDAO.actualizar(vehiculo) : vehiculoDAO.insertar(vehiculo);


        if (exito) {
            mostrarExito(esEdicion ? "Vehículo actualizado correctamente" : "Vehículo registrado correctamente");
            dashboardController.refrescarVehiculos();
            cerrar();
        } else {
            mostrarError("Error al guardar el vehículo en la base de datos.\n\nPor favor, intente nuevamente.");
        }
    }

    private boolean validarCampos() {
        String placa = txtPlaca.getText().trim();
        String marca = txtMarca.getText().trim();
        String modelo = txtModelo.getText().trim();
        String color = txtColor.getText().trim();
        String imagenUrl = txtImagenUrl.getText().trim();

        // Validar campos de texto obligatorios
        if (placa.isEmpty()) {
            mostrarError("La placa es obligatoria");
            txtPlaca.requestFocus();
            return false;
        }

        if (marca.isEmpty()) {
            mostrarError("La marca es obligatoria");
            txtMarca.requestFocus();
            return false;
        }

        if (modelo.isEmpty()) {
            mostrarError("El modelo es obligatorio");
            txtModelo.requestFocus();
            return false;
        }

        if (color.isEmpty()) {
            mostrarError("El color es obligatorio");
            txtColor.requestFocus();
            return false;
        }

        // Validar longitud de placa
        if (placa.length() < 6) {
            mostrarError("La placa debe tener al menos 6 caracteres");
            txtPlaca.requestFocus();
            return false;
        }

        // Validar placa única (solo para nuevos vehículos)
        if (!esEdicion) {
            if (vehiculoDAO.existePlaca(placa.toUpperCase())) {
                mostrarAlertaPlacaDuplicada(placa.toUpperCase());
                return false;
            }
        }

        // Validar ComboBoxes
        if (cmbTransmision.getValue() == null) {
            mostrarError("Debe seleccionar el tipo de transmisión");
            cmbTransmision.requestFocus();
            return false;
        }

        if (cmbCombustible.getValue() == null) {
            mostrarError("Debe seleccionar el tipo de combustible");
            cmbCombustible.requestFocus();
            return false;
        }

        if (cmbTipo.getValue() == null) {
            mostrarError("Debe seleccionar el tipo de vehículo");
            cmbTipo.requestFocus();
            return false;
        }

        if (cmbEstado.getValue() == null) {
            mostrarError("Debe seleccionar el estado del vehículo");
            cmbEstado.requestFocus();
            return false;
        }

        // Validar imagen obligatoria (solo para nuevos vehículos)
        if (!esEdicion && imagenUrl.isEmpty()) {
            mostrarError("Debe seleccionar una imagen para el vehículo.\n\n" +
                    "Use el botón 'Examinar' para seleccionar una imagen.");
            btnExaminar.requestFocus();
            return false;
        }

        // Validar descripción (opcional, pero recomendado)
        String descripcion = txtDescripcion.getText().trim();
        if (descripcion.isEmpty()) {
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Descripción vacía");
            confirmacion.setHeaderText("No ha agregado una descripción");
            confirmacion.setContentText("¿Desea continuar sin agregar una descripción del vehículo?");

            ButtonType resultado = confirmacion.showAndWait().orElse(ButtonType.CANCEL);
            if (resultado == ButtonType.CANCEL) {
                txtDescripcion.requestFocus();
                return false;
            }
        }

        return true;
    }

    private void mostrarAlertaPlacaDuplicada(String placa) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Placa Duplicada");
        alert.setHeaderText("La placa ya está registrada");
        alert.setContentText(
                "Ya existe un vehículo registrado con la placa: " + placa + "\n\n" +
                        "Por favor, verifique e ingrese una placa diferente."
        );

        alert.showAndWait();

        // Enfocar el campo de placa para que el usuario pueda corregir inmediatamente
        txtPlaca.requestFocus();
        txtPlaca.selectAll();
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


    private static class TipoVehiculoItem {
        private int id;
        private String nombre;
        private double tarifa;

        public TipoVehiculoItem(int id, String nombre, double tarifa) {
            this.id = id;
            this.nombre = nombre;
            this.tarifa = tarifa;
        }

        public int getId() {
            return id;
        }

        public String getNombre() {
            return nombre;
        }

        @Override
        public String toString() {
            return nombre + " ($" + String.format("%.2f", tarifa) + "/día)";
        }
    }
}