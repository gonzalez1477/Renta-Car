package com.example.sistema_rentacar.Controllers.Empleado;

import com.example.sistema_rentacar.Controllers.Cliente.DialogoEntregaAnticipadaController;
import com.example.sistema_rentacar.Controllers.Cliente.DialogoFinalizarAlquilerController;
import com.example.sistema_rentacar.Controllers.Cliente.HistorialClienteController;
import com.example.sistema_rentacar.Repository.TipoVehiculoRepository;
import com.example.sistema_rentacar.Repository.*;
import com.example.sistema_rentacar.Modelos.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import javafx.application.Platform;


import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

public class DashboardEmpleadoController {

    // SIDEBAR
    @FXML private Label lblEmpleado;
    @FXML private Label lblCargo;
    @FXML private Button btnDashboard;
    @FXML private Button btnVehiculos;
    @FXML private Button btnClientes;
    @FXML private Button btnAlquileres;
    @FXML private Button btnEmpleados;
    @FXML private Button btnCategorias;
    @FXML private Separator separadorAdmin;
    @FXML private Label lblAdminSection;

    @FXML private Label lblTituloSeccion;
    @FXML private Label lblSubtituloSeccion;

    @FXML private Label lblIngresosMes;

    // CONTENEDOR DE VISTAS
    @FXML private StackPane contenedorVistas;
    @FXML private javafx.scene.control.ScrollPane vistaDashboard;
    @FXML private VBox vistaVehiculos;
    @FXML private VBox vistaClientes;
    @FXML private VBox vistaAlquileres;
    @FXML private VBox vistaEmpleados;
    @FXML private VBox vistaCategorias;

    //  VISTA DASHBOARD
    @FXML private Label lblTotalVehiculos;
    @FXML private Label lblVehiculosDisponibles;
    @FXML private Label lblVehiculosAlquilados;
    @FXML private Label lblAlquileresActivos;
    @FXML private Label lblTotalClientes;
    @FXML
    private VBox seccionAdmin;

    // VISTA VEHÍCULOS
    @FXML private TableView<Vehiculo> tableVehiculos;
    @FXML private TableColumn<Vehiculo, Integer> colVehId;
    @FXML private TableColumn<Vehiculo, String> colVehPlaca;
    @FXML private TableColumn<Vehiculo, String> colVehMarca;
    @FXML private TableColumn<Vehiculo, String> colVehModelo;
    @FXML private TableColumn<Vehiculo, Integer> colVehAnio;
    @FXML private TableColumn<Vehiculo, String> colVehTipo;
    @FXML private TableColumn<Vehiculo, Double> colVehTarifa;
    @FXML private TableColumn<Alquiler, Timestamp> colAlqFechaFin;

    @FXML private TableColumn<Vehiculo, String> colVehEstado;
    @FXML private TextField txtBuscarVehiculo;
    @FXML private Button btnAgregarVehiculo;
    @FXML private Button btnEditarVehiculo;
    @FXML private Button btnEliminarVehiculo;

    // VISTA CLIENTES
    @FXML private TableView<Cliente> tableClientes;
    @FXML private TableColumn<Cliente, Integer> colCliId;
    @FXML private TableColumn<Cliente, String> colCliNombre;
    @FXML private TableColumn<Cliente, String> colCliEmail;
    @FXML private TableColumn<Cliente, String> colCliTelefono;
    @FXML private TableColumn<Cliente, String> colCliDui;
    @FXML private TableColumn<Cliente, String> colCliLicencia;
    @FXML private TableColumn<Cliente, Boolean> colCliActivo;
    @FXML private TextField txtBuscarCliente;
    @FXML private Button btnVerHistorialCliente;
    @FXML private Button btnAgregarCliente;

    //  VISTA ALQUILERES
    @FXML private TableView<Alquiler> tableAlquileres;
    @FXML private TableColumn<Alquiler, Integer> colAlqId;
    @FXML private TableColumn<Alquiler, String> colAlqCliente;
    @FXML private TableColumn<Alquiler, String> colAlqVehiculo;
    @FXML private TableColumn<Alquiler, String> colAlqPlaca;
    @FXML private TableColumn<Alquiler, Timestamp> colAlqFechaInicio;
    @FXML private TableColumn<Alquiler, Integer> colAlqDias;
    @FXML private TableColumn<Alquiler, Double> colAlqCosto;
    @FXML private TableColumn<Alquiler, String> colAlqEstado;
    @FXML private Button btnFinalizarAlquiler;
    @FXML private Button btnRefrescarAlquileres;
    @FXML private TextField txtBuscarAlquiler;

    //  VISTA EMPLEADOS
    @FXML private TableView<Empleado> tableEmpleados;
    @FXML private TableColumn<Empleado, Integer> colEmpId;
    @FXML private TableColumn<Empleado, String> colEmpNombre;
    @FXML private TableColumn<Empleado, String> colEmpEmail;
    @FXML private TableColumn<Empleado, String> colEmpTelefono;
    @FXML private TableColumn<Empleado, String> colEmpUsuario;
    @FXML private TableColumn<Empleado, String> colEmpCargo;
    @FXML private TableColumn<Empleado, Date> colEmpFechaContratacion;
    @FXML private TableColumn<Empleado, Boolean> colEmpActivo;
    @FXML private TextField txtBuscarEmpleado;
    @FXML private Button btnAgregarEmpleado;
    @FXML private Button btnEditarEmpleado;
    @FXML private Button btnDesactivarEmpleado;
    @FXML private Button btnEliminarEmpleado;

    // VISTA CATEGORÍAS
    @FXML private TableView<TipoVehiculo> tableTipos;
    @FXML private TableColumn<TipoVehiculo, Integer> colTipoId;
    @FXML private TableColumn<TipoVehiculo, String> colTipoNombre;
    @FXML private TableColumn<TipoVehiculo, Double> colTipoTarifa;
    @FXML private TableColumn<TipoVehiculo, String> colTipoDescripcion;
    @FXML private Button btnAgregarTipo;
    @FXML private Button btnEditarTipo;
    @FXML private Button btnEliminarTipo;
    @FXML private TextField txtBuscarCategoria;

    private VehiculoRepository VehiculoRepository;
    private ClienteRepository ClienteRepository;
    private AlquilerRepository AlquilerRepository;
    private EmpleadoRepository EmpleadoRepository;
    private TipoVehiculoRepository tipoVehiculoRepository;


    private Empleado empleadoActual;
    private ObservableList<Vehiculo> vehiculos;
    private ObservableList<Cliente> clientes;
    private ObservableList<Alquiler> alquileres;
    private ObservableList<Empleado> empleados;
    private ObservableList<TipoVehiculo> tiposVehiculo;

    // Estilo para botón activo
    private static final String ESTILO_BOTON_ACTIVO = "-fx-background-color: rgba(52, 152, 219, 0.3); -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12; -fx-cursor: hand; -fx-background-radius: 8; -fx-alignment: CENTER-LEFT; -fx-font-size: 14px;";
    private static final String ESTILO_BOTON_INACTIVO = "-fx-background-color: rgba(255,255,255,0.1); -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12; -fx-cursor: hand; -fx-background-radius: 8; -fx-alignment: CENTER-LEFT; -fx-font-size: 14px;";

    @FXML
    public void initialize() {
        VehiculoRepository = new VehiculoRepository();
        ClienteRepository = new ClienteRepository();
        AlquilerRepository = new AlquilerRepository();
        EmpleadoRepository = new EmpleadoRepository();
        tipoVehiculoRepository = new TipoVehiculoRepository();

        configurarTablas();
        configurarBusquedas();

        // Mostrar dashboard por defecto
        mostrarVista(vistaDashboard, "Dashboard", "Resumen General", btnDashboard);
    }

    public void setDatosEmpleado(Empleado empleado) {
        this.empleadoActual = empleado;
        lblEmpleado.setText(empleado.getNombreCompleto());
        lblCargo.setText(empleado.getCargo());

        // Configurar permisos según el cargo
        boolean esAdmin = "Administrador".equalsIgnoreCase(empleado.getCargo());
        configurarPermisos(esAdmin);

        // Cargar datos iniciales
        cargarDashboard();
        cargarVehiculos();
        cargarClientes();
        cargarAlquileres();
        cargarTiposVehiculo();


        if (esAdmin) {
            cargarEmpleados();
            cargarTiposVehiculo();
        }

    }


    private void configurarPermisos(boolean esAdmin) {
        // Categorías siempre visible para todos los empleados
        btnCategorias.setVisible(true);
        btnCategorias.setManaged(true);

        // Sección de administración
        seccionAdmin.setVisible(esAdmin);
        seccionAdmin.setManaged(esAdmin);
    }

    // NAVEGACIÓN ENTRE VISTAS

    private void mostrarVista(Node vistaActiva, String titulo, String subtitulo, Button botonActivo) {
        // Ocultar todas las vistas
        vistaDashboard.setVisible(false);
        vistaDashboard.setManaged(false);
        vistaVehiculos.setVisible(false);
        vistaVehiculos.setManaged(false);
        vistaClientes.setVisible(false);
        vistaClientes.setManaged(false);
        vistaAlquileres.setVisible(false);
        vistaAlquileres.setManaged(false);
        vistaEmpleados.setVisible(false);
        vistaEmpleados.setManaged(false);
        vistaCategorias.setVisible(false);
        vistaCategorias.setManaged(false);

        // Mostrar vista seleccionada
        vistaActiva.setVisible(true);
        vistaActiva.setManaged(true);

        // Actualizar header
        lblTituloSeccion.setText(titulo);
        lblSubtituloSeccion.setText(subtitulo);

        // Resaltar botón activo
        resetearEstilosBotones();
        if (botonActivo != null) {
            botonActivo.setStyle(ESTILO_BOTON_ACTIVO);
        }
    }

    private void resetearEstilosBotones() {
        btnDashboard.setStyle(ESTILO_BOTON_INACTIVO);
        btnVehiculos.setStyle(ESTILO_BOTON_INACTIVO);
        btnClientes.setStyle(ESTILO_BOTON_INACTIVO);
        btnAlquileres.setStyle(ESTILO_BOTON_INACTIVO);

        if (btnEmpleados.isVisible()) {
            btnEmpleados.setStyle(ESTILO_BOTON_INACTIVO);
        }
        if (btnCategorias.isVisible()) {
            btnCategorias.setStyle(ESTILO_BOTON_INACTIVO);
        }
    }

    @FXML
    private void handleNavigateToDashboard() {
        cargarDashboard(); // Refrescar datos
        mostrarVista(vistaDashboard, "Dashboard", "Resumen General", btnDashboard);
    }

    @FXML
    private void handleNavigateToVehiculos() {
        mostrarVista(vistaVehiculos, "Gestión de Vehículos", "Administra tu flota", btnVehiculos);
    }

    @FXML
    private void handleNavigateToClientes() {
        mostrarVista(vistaClientes, "Gestión de Clientes", "Información de clientes registrados", btnClientes);
    }

    @FXML
    private void handleNavigateToAlquileres() {
        txtBuscarAlquiler.clear(); // Limpiar búsqueda
        cargarAlquileres(); // Refrescar datos
        mostrarVista(vistaAlquileres, "Alquileres Activos", "Gestiona alquileres en curso", btnAlquileres);
    }

    @FXML
    private void handleNavigateToEmpleados() {
        mostrarVista(vistaEmpleados, "Gestión de Empleados", "Administra el personal", btnEmpleados);
    }

    @FXML
    private void handleNavigateToCategorias() {
        if (txtBuscarCategoria != null) {
            txtBuscarCategoria.clear(); // Limpiar búsqueda
        }
        cargarTiposVehiculo(); // Refrescar datos
        mostrarVista(vistaCategorias, "Gestión de Categorías", "Tipos de vehículos disponibles", btnCategorias);
    }

    // Agrega este nuevo método:
    @FXML
    private void handleAgregarCliente() {
        abrirFormularioCliente(null);
    }

    // Agrega también (si aún no existe):
    @FXML
    private void handleEditarCliente() {
        Cliente seleccionado = tableClientes.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            abrirFormularioCliente(seleccionado);
        }
    }

    // ============ CONFIGURACIÓN DE TABLAS ============

    private void configurarTablas() {
        configurarTablaVehiculos();
        configurarTablaClientes();
        configurarTablaAlquileres();
        configurarTablaEmpleados();
        configurarTablaTipos();
    }

    private void configurarTablaVehiculos() {
        colVehId.setCellValueFactory(new PropertyValueFactory<>("idVehiculo"));
        colVehPlaca.setCellValueFactory(new PropertyValueFactory<>("placa"));
        colVehMarca.setCellValueFactory(new PropertyValueFactory<>("marca"));
        colVehModelo.setCellValueFactory(new PropertyValueFactory<>("modelo"));
        colVehAnio.setCellValueFactory(new PropertyValueFactory<>("anio"));
        colVehTipo.setCellValueFactory(new PropertyValueFactory<>("nombreTipo"));
        colVehTarifa.setCellValueFactory(new PropertyValueFactory<>("tarifaPorDia"));
        colVehEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        colVehTarifa.setCellFactory(column -> new TableCell<Vehiculo, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("$%.2f", item));
            }
        });

        colVehEstado.setCellFactory(column -> new TableCell<Vehiculo, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    switch (item) {
                        case "Disponible":
                            setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                            break;
                        case "Alquilado":
                            setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                            break;
                        case "Mantenimiento":
                            setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;");
                            break;
                    }
                }
            }
        });

        tableVehiculos.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, selection) -> {
                    btnEditarVehiculo.setDisable(selection == null);
                    btnEliminarVehiculo.setDisable(selection == null || "Alquilado".equals(selection.getEstado()));
                }
        );
    }

    private void configurarTablaClientes() {
        colCliId.setCellValueFactory(new PropertyValueFactory<>("idCliente"));
        colCliNombre.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNombreCompleto())
        );
        colCliEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colCliTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colCliDui.setCellValueFactory(new PropertyValueFactory<>("dui"));
        colCliLicencia.setCellValueFactory(new PropertyValueFactory<>("licencia"));
        colCliActivo.setCellValueFactory(new PropertyValueFactory<>("activo"));

        colCliActivo.setCellFactory(column -> new TableCell<Cliente, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item ? "Activo" : "Inactivo");
                    setStyle(item ? "-fx-text-fill: #27ae60;" : "-fx-text-fill: #e74c3c;");
                }
            }
        });

        tableClientes.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, selection) -> btnVerHistorialCliente.setDisable(selection == null)
        );

        tableClientes.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, selection) -> {
                    btnVerHistorialCliente.setDisable(selection == null);
                    //btnEditarCliente.setDisable(selection == null); // Si tienes este botón
                }
        );
    }


    private void configurarTablaAlquileres() {

        colAlqId.setCellValueFactory(new PropertyValueFactory<>("idAlquiler"));
        colAlqCliente.setCellValueFactory(new PropertyValueFactory<>("nombreCliente"));
        colAlqVehiculo.setCellValueFactory(new PropertyValueFactory<>("vehiculo"));
        colAlqPlaca.setCellValueFactory(new PropertyValueFactory<>("placa"));
        colAlqFechaInicio.setCellValueFactory(new PropertyValueFactory<>("fechaInicio"));


        colAlqFechaFin.setCellValueFactory(new PropertyValueFactory<>("fechaFinEstimada"));

        colAlqDias.setCellValueFactory(new PropertyValueFactory<>("diasAlquiler"));
        colAlqCosto.setCellValueFactory(new PropertyValueFactory<>("costoTotal"));
        colAlqEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));


        colAlqFechaInicio.setCellFactory(column -> new TableCell<Alquiler, Timestamp>() {
            private final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            @Override
            protected void updateItem(Timestamp item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : format.format(item));
            }
        });


        colAlqFechaFin.setCellFactory(column -> new TableCell<Alquiler, Timestamp>() {
            private final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            @Override
            protected void updateItem(Timestamp item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                    return;
                }

                setText(format.format(item));

                // Aplicar estilo según si está vencido o próximo a vencer
                TableRow<Alquiler> currentRow = getTableRow();
                if (currentRow != null && !currentRow.isEmpty()) {
                    Alquiler alquiler = currentRow.getItem();
                    if (alquiler != null) {
                        LocalDate fechaFin = item.toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate();
                        LocalDate hoy = LocalDate.now();

                        boolean isSelected = currentRow.isSelected();

                        if (hoy.isAfter(fechaFin)) {
                            // Vencido
                            setStyle(isSelected ?
                                    "-fx-text-fill: white; -fx-font-weight: bold;" :
                                    "-fx-text-fill: #c62828; -fx-font-weight: bold;");
                        } else if (hoy.equals(fechaFin) || hoy.plusDays(1).equals(fechaFin)) {
                            // Por vencer
                            setStyle(isSelected ?
                                    "-fx-text-fill: white; -fx-font-weight: bold;" :
                                    "-fx-text-fill: #f57f17; -fx-font-weight: bold;");
                        } else {
                            // Normal
                            setStyle(isSelected ?
                                    "-fx-text-fill: white;" :
                                    "-fx-text-fill: #2c3e50;");
                        }
                    }
                }
            }
        });

        // FORMATO DE COSTO
        colAlqCosto.setCellFactory(column -> new TableCell<Alquiler, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("$%.2f", item));
            }
        });


        colAlqEstado.setCellFactory(column -> new TableCell<Alquiler, String>() {
            @Override
            protected void updateItem(String estado, boolean empty) {
                super.updateItem(estado, empty);

                if (empty || estado == null) {
                    setText(null);
                    setStyle("");
                    return;
                }

                TableRow<Alquiler> currentRow = getTableRow();
                Alquiler alquiler = currentRow != null ? currentRow.getItem() : null;

                if (alquiler != null && alquiler.getFechaFinEstimada() != null) {
                    Timestamp tsFin = alquiler.getFechaFinEstimada();
                    LocalDate fechaLimite = tsFin.toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();

                    LocalDate hoy = LocalDate.now();

                    String estadoReal = estado;
                    String icono = "";
                    String estilo = "";

                    boolean isSelected = currentRow.isSelected();

                    if (estado.contains("Finalizado")) {
                        icono = "✓ ";
                        estilo = isSelected ?
                                "-fx-text-fill: white; -fx-font-weight: bold;" :
                                "-fx-text-fill: #95a5a6; -fx-font-weight: bold;";
                        setText(icono + estadoReal);

                    } else if (hoy.isAfter(fechaLimite)) {
                        long diasRetraso = ChronoUnit.DAYS.between(fechaLimite, hoy);
                        estadoReal = "Retrasado";
                        icono = "🚨 ";
                        estilo = isSelected ?
                                "-fx-text-fill: white; -fx-font-weight: bold;" :
                                "-fx-text-fill: #c62828; -fx-font-weight: bold;";
                        setText(icono + estadoReal + " (" + diasRetraso + " días)");

                    } else if (hoy.equals(fechaLimite) || hoy.plusDays(1).equals(fechaLimite)) {
                        long diasRestantes = ChronoUnit.DAYS.between(hoy, fechaLimite);
                        estadoReal = "Por Vencer";
                        icono = "⏰ ";
                        estilo = isSelected ?
                                "-fx-text-fill: white; -fx-font-weight: bold;" :
                                "-fx-text-fill: #f57f17; -fx-font-weight: bold;";
                        setText(icono + estadoReal + " (" + (diasRestantes == 0 ? "hoy" : "mañana") + ")");

                    } else {
                        icono = "⏳ ";
                        estilo = isSelected ?
                                "-fx-text-fill: white; -fx-font-weight: bold;" :
                                "-fx-text-fill: #27ae60; -fx-font-weight: bold;";
                        setText(icono + estadoReal);
                    }

                    setStyle(estilo);
                }
            }
        });

        tableAlquileres.setRowFactory(tv -> new TableRow<Alquiler>() {
            @Override
            protected void updateItem(Alquiler alquiler, boolean empty) {
                super.updateItem(alquiler, empty);

                if (empty || alquiler == null) {
                    setStyle("");
                    return;
                }

                Timestamp tsFin = alquiler.getFechaFinEstimada();
                if (tsFin != null) {
                    LocalDate fechaLimite = tsFin.toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();

                    LocalDate hoy = LocalDate.now();
                    String estado = alquiler.getEstado();

                    boolean seleccionada = isSelected();

                    if (estado != null && estado.contains("Finalizado")) {
                        setStyle(seleccionada ?
                                "" :
                                "-fx-background-color: #f5f5f5; -fx-text-fill: #2c3e50;");

                    } else if (hoy.isAfter(fechaLimite)) {
                        setStyle(seleccionada ?
                                "-fx-background-color: #c62828; -fx-text-fill: white;" :
                                "-fx-background-color: #ffebee; -fx-text-fill: #2c3e50;");

                    } else if (hoy.equals(fechaLimite) || hoy.plusDays(1).equals(fechaLimite)) {
                        setStyle(seleccionada ?
                                "-fx-background-color: #f57f17; -fx-text-fill: white;" :
                                "-fx-background-color: #fff9c4; -fx-text-fill: #2c3e50;");

                    } else {
                        setStyle(seleccionada ?
                                "-fx-background-color: #27ae60; -fx-text-fill: white;" :
                                "-fx-background-color: #e8f5e9; -fx-text-fill: #2c3e50;");
                    }
                }
            }

            @Override
            public void updateSelected(boolean selected) {
                super.updateSelected(selected);
                updateItem(getItem(), isEmpty());
            }
        });

        tableAlquileres.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, selection) -> {
                    boolean habilitarFinalizar = selection != null &&
                            (selection.getEstado().equals("Activo") ||
                                    selection.getEstado().equals("Por Vencer") ||
                                    selection.getEstado().equals("Retrasado"));

                    btnFinalizarAlquiler.setDisable(!habilitarFinalizar);

                    tableAlquileres.refresh();
                }
        );
    }


    private void configurarTablaEmpleados() {
        colEmpId.setCellValueFactory(new PropertyValueFactory<>("idEmpleado"));
        colEmpNombre.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNombreCompleto())
        );
        colEmpEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colEmpTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colEmpUsuario.setCellValueFactory(new PropertyValueFactory<>("usuario"));
        colEmpCargo.setCellValueFactory(new PropertyValueFactory<>("cargo"));
        colEmpFechaContratacion.setCellValueFactory(new PropertyValueFactory<>("fechaContratacion"));
        colEmpActivo.setCellValueFactory(new PropertyValueFactory<>("activo"));

        colEmpFechaContratacion.setCellFactory(column -> new TableCell<Empleado, Date>() {
            private SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            @Override
            protected void updateItem(Date item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : format.format(item));
            }
        });

        colEmpActivo.setCellFactory(column -> new TableCell<Empleado, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item ? "✅ Activo" : "❌ Inactivo");
                    setStyle(item ? "-fx-text-fill: #27ae60; -fx-font-weight: bold;" : "-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                }
            }
        });

        tableEmpleados.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, selection) -> {
                    boolean seleccionado = selection != null;
                    boolean esUsuarioActual = seleccionado && selection.getIdEmpleado() == empleadoActual.getIdEmpleado();
                    boolean estaActivo = seleccionado && selection.isActivo();

                    btnEditarEmpleado.setDisable(!seleccionado);
                    btnDesactivarEmpleado.setDisable(!seleccionado || esUsuarioActual);
                    btnEliminarEmpleado.setDisable(!seleccionado || esUsuarioActual);

                    if (seleccionado) {
                        btnDesactivarEmpleado.setText(estaActivo ? "🚫 Desactivar" : "✅ Reactivar");
                    }
                }
        );
    }

    private void configurarTablaTipos() {
        colTipoId.setCellValueFactory(new PropertyValueFactory<>("idTipo"));
        colTipoNombre.setCellValueFactory(new PropertyValueFactory<>("nombreTipo"));
        colTipoTarifa.setCellValueFactory(new PropertyValueFactory<>("tarifaPorDia"));
        colTipoDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        colTipoTarifa.setCellFactory(column -> new TableCell<TipoVehiculo, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("$%.2f/día", item));
            }
        });

        tableTipos.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, selection) -> {
                    btnEditarTipo.setDisable(selection == null);
                    btnEliminarTipo.setDisable(selection == null);
                }
        );
    }

    // ============ BÚSQUEDAS ============

    private void configurarBusquedas() {
        txtBuscarVehiculo.textProperty().addListener((obs, old, text) -> buscarVehiculos(text));
        txtBuscarCliente.textProperty().addListener((obs, old, text) -> buscarClientes(text));
        txtBuscarAlquiler.textProperty().addListener((obs, old, text) -> buscarAlquileres(text));

        if (txtBuscarEmpleado != null) {
            txtBuscarEmpleado.textProperty().addListener((obs, old, text) -> buscarEmpleados(text));
        }

        // NUEVO: Listener para categorías
        if (txtBuscarCategoria != null) {
            txtBuscarCategoria.textProperty().addListener((obs, old, text) -> buscarCategorias(text));
        }
    }

    private void buscarVehiculos(String criterio) {
        if (criterio == null || criterio.trim().isEmpty()) {
            cargarVehiculos();
        } else {
            List<Vehiculo> resultado = VehiculoRepository.buscar(criterio);
            vehiculos = FXCollections.observableArrayList(resultado);
            tableVehiculos.setItems(vehiculos);
        }
    }
    // Agregar después del método buscarEmpleados()
    private void buscarCategorias(String criterio) {
        if (criterio == null || criterio.trim().isEmpty()) {
            cargarTiposVehiculo();
        } else {
            List<TipoVehiculo> resultado = tipoVehiculoRepository.buscar(criterio);
            tiposVehiculo = FXCollections.observableArrayList(resultado);
            tableTipos.setItems(tiposVehiculo);
        }
    }

    private void buscarClientes(String criterio) {
        if (criterio == null || criterio.trim().isEmpty()) {
            cargarClientes();
        } else {
            List<Cliente> resultado = ClienteRepository.buscar(criterio);
            clientes = FXCollections.observableArrayList(resultado);
            tableClientes.setItems(clientes);
        }
    }

    private void buscarEmpleados(String criterio) {
        if (criterio == null || criterio.trim().isEmpty()) {
            cargarEmpleados();
        } else {
            List<Empleado> resultado = EmpleadoRepository.buscar(criterio);
            empleados = FXCollections.observableArrayList(resultado);
            tableEmpleados.setItems(empleados);
        }
    }

    // ============ CARGA DE DATOS ============

    private void cargarDashboard() {
        List<Vehiculo> todosVehiculos = VehiculoRepository.obtenerTodos();
        int total = todosVehiculos.size();
        int disponibles = (int) todosVehiculos.stream().filter(Vehiculo::isDisponible).count();
        int alquilados = (int) todosVehiculos.stream().filter(v -> "Alquilado".equals(v.getEstado())).count();

        lblTotalVehiculos.setText(String.valueOf(total));
        lblVehiculosDisponibles.setText(String.valueOf(disponibles));
        lblVehiculosAlquilados.setText(String.valueOf(alquilados));

        List<Alquiler> activos = AlquilerRepository.obtenerActivos();
        lblAlquileresActivos.setText(String.valueOf(activos.size()));

        List<Cliente> todosClientes = ClienteRepository.obtenerTodos();
        lblTotalClientes.setText(String.valueOf(todosClientes.size()));

        // Calcular ingresos del mes actual
        double ingresosMes = AlquilerRepository.obtenerIngresosDelMesActual();
        lblIngresosMes.setText(String.format("$%.2f", ingresosMes));

    }

    private void cargarVehiculos() {
        List<Vehiculo> lista = VehiculoRepository.obtenerTodos();
        vehiculos = FXCollections.observableArrayList(lista);
        tableVehiculos.setItems(vehiculos);
    }

    private void cargarClientes() {
        List<Cliente> lista = ClienteRepository.obtenerTodos();
        clientes = FXCollections.observableArrayList(lista);
        tableClientes.setItems(clientes);
    }


    private void cargarAlquileres() {
        // Cargar todos los alquileres activos (incluyendo retrasados y por vencer)
        List<Alquiler> lista = AlquilerRepository.obtenerActivosYRetrasados();
        alquileres = FXCollections.observableArrayList(lista);
        tableAlquileres.setItems(alquileres);
    }

    private void cargarEmpleados() {
        List<Empleado> lista = EmpleadoRepository.obtenerTodos();
        empleados = FXCollections.observableArrayList(lista);
        tableEmpleados.setItems(empleados);
    }

    private void cargarTiposVehiculo() {
        List<TipoVehiculo> lista = tipoVehiculoRepository.obtenerTodos();
        tiposVehiculo = FXCollections.observableArrayList(lista);
        tableTipos.setItems(tiposVehiculo);
    }

    // ============ ACCIONES VEHÍCULOS ============

    @FXML
    private void handleAgregarVehiculo() {
        abrirFormularioVehiculo(null);
    }

    @FXML
    private void handleEditarVehiculo() {
        Vehiculo seleccionado = tableVehiculos.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            abrirFormularioVehiculo(seleccionado);
        }
    }

    @FXML
    private void handleEliminarVehiculo() {
        Vehiculo seleccionado = tableVehiculos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAdvertencia("Debes seleccionar un vehículo para eliminar.");
            return;
        }

        boolean tieneAlquileres = AlquilerRepository.tieneAlquileresPorVehiculo(seleccionado.getIdVehiculo());

        if (tieneAlquileres) {
            mostrarError("No se puede eliminar este vehículo porque tiene alquileres registrados.\n"
                    + "Debes eliminar o archivar los alquileres asociados antes de eliminarlo.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Eliminar Vehículo");
        confirmacion.setHeaderText("¿Deseas eliminar el vehículo?");
        confirmacion.setContentText(seleccionado.getNombreCompleto() + " - " + seleccionado.getPlaca());

        Optional<ButtonType> resultado = confirmacion.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            if (VehiculoRepository.eliminar(seleccionado.getIdVehiculo())) {
                mostrarExito("Vehículo eliminado correctamente.");
                cargarVehiculos();
                cargarDashboard();
            } else {
                mostrarError("Error al eliminar el vehículo.");
            }
        }
    }

    private void abrirFormularioVehiculo(Vehiculo vehiculo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sistema_rentacar/Views/empleado/FormularioVehiculo.fxml"));
            Parent root = loader.load();

            FormularioVehiculoController controller = loader.getController();
            controller.setDatos(vehiculo, this);

            Stage dialog = new Stage();
            dialog.setTitle(vehiculo == null ? "Nuevo Vehículo" : "Editar Vehículo");
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setScene(new Scene(root));
            dialog.setResizable(false);
            dialog.showAndWait();

        } catch (Exception e) {
            System.err.println("Error al abrir formulario: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @FXML
    private void handleVerHistorialCliente() {
        Cliente seleccionado = tableClientes.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sistema_rentacar/Views/cliente/HistorialCliente.fxml"));
                Parent root = loader.load();

                HistorialClienteController controller = loader.getController();
                controller.setDatosClienteDesdeEmpleado(seleccionado.getIdCliente(), seleccionado.getNombreCompleto());

                Stage dialog = new Stage();
                dialog.setTitle("Historial de " + seleccionado.getNombreCompleto());
                dialog.initModality(Modality.APPLICATION_MODAL);
                dialog.setScene(new Scene(root, 1000, 700));
                dialog.showAndWait();

                // Refrescar datos después de cerrar el historial
                cargarAlquileres();
                cargarVehiculos();
                cargarDashboard();

            } catch (Exception e) {
                System.err.println("Error al abrir historial: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleRefrescarClientes() {
        cargarClientes();
    }

    // ============ ACCIONES ALQUILERES ============

    @FXML
    private void handleFinalizarAlquiler() {
        Alquiler alquilerSeleccionado = tableAlquileres.getSelectionModel().getSelectedItem();

        if (alquilerSeleccionado == null) {
            mostrarAdvertencia("Seleccione un alquiler para finalizar");
            return;
        }

        // Verificar si el alquiler ya está finalizado
        if (alquilerSeleccionado.getEstado().contains("Finalizado")) {
            mostrarAdvertencia("Este alquiler ya está finalizado");
            return;
        }

        Timestamp tsFin = alquilerSeleccionado.getFechaFinEstimada();

        LocalDate fechaFinEstimada = tsFin
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        LocalDate hoy = LocalDate.now();

        // Verificar el tipo de finalización
        if (hoy.isBefore(fechaFinEstimada)) {
            //ENTREGA ANTICIPADA
            abrirDialogoEntregaAnticipada(alquilerSeleccionado);
        } else {
            //ENTREGA NORMAL O CON RETRASO
            abrirDialogoFinalizarAlquiler(alquilerSeleccionado);
        }
    }

    @FXML
    private void handleRefrescarAlquileres() {
        cargarAlquileres();
        cargarDashboard();
    }

    // ============ ACCIONES EMPLEADOS ============

    @FXML
    private void handleAgregarEmpleado() {
        abrirFormularioEmpleado(null);
    }

    @FXML
    private void handleEditarEmpleado() {
        Empleado seleccionado = tableEmpleados.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            abrirFormularioEmpleado(seleccionado);
        }
    }

    @FXML
    private void handleDesactivarEmpleado() {
        Empleado seleccionado = tableEmpleados.getSelectionModel().getSelectedItem();
        if (seleccionado == null) return;

        if (seleccionado.getIdEmpleado() == empleadoActual.getIdEmpleado()) {
            mostrarError("No puedes desactivar tu propia cuenta");
            return;
        }

        boolean estaActivo = seleccionado.isActivo();
        String accion = estaActivo ? "desactivar" : "reactivar";
        String icono = estaActivo ? "🚫" : "✅";

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle(Character.toUpperCase(accion.charAt(0)) + accion.substring(1) + " Empleado");
        confirmacion.setHeaderText(icono + " ¿" + Character.toUpperCase(accion.charAt(0)) + accion.substring(1) + " empleado?");
        confirmacion.setContentText(
                seleccionado.getNombreCompleto() + " - " + seleccionado.getCargo() + "\n\n" +
                        (estaActivo ?
                                "El empleado no podrá iniciar sesión hasta que sea reactivado." :
                                "El empleado podrá volver a iniciar sesión normalmente.")
        );

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            boolean exito;
            if (estaActivo) {
                exito = EmpleadoRepository.desactivar(seleccionado.getIdEmpleado());
            } else {
                exito = EmpleadoRepository.reactivar(seleccionado.getIdEmpleado());
            }

            if (exito) {
                mostrarExito("Empleado " + (estaActivo ? "desactivado" : "reactivado") + " correctamente");
                cargarEmpleados();
            } else {
                mostrarError("Error al " + accion + " empleado");
            }
        }
    }

    @FXML
    private void handleEliminarEmpleado() {
        Empleado seleccionado = tableEmpleados.getSelectionModel().getSelectedItem();
        if (seleccionado == null) return;

        if (seleccionado.getIdEmpleado() == empleadoActual.getIdEmpleado()) {
            mostrarError("No puedes eliminar tu propia cuenta");
            return;
        }

        if (EmpleadoRepository.tieneAlquileresAsociados(seleccionado.getIdEmpleado())) {
            Alert advertencia = new Alert(Alert.AlertType.WARNING);
            advertencia.setTitle("No se puede eliminar");
            advertencia.setHeaderText("⚠ Empleado tiene registros asociados");
            advertencia.setContentText(
                    "Este empleado tiene alquileres asociados en el sistema.\n\n" +
                            "No es posible eliminarlo permanentemente.\n" +
                            "Puede desactivarlo en su lugar."
            );
            advertencia.showAndWait();
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.WARNING);
        confirmacion.setTitle("Eliminar Empleado Permanentemente");
        confirmacion.setHeaderText("¿Está seguro de eliminar permanentemente este empleado?");
        confirmacion.setContentText(
                seleccionado.getNombreCompleto() + " - " + seleccionado.getCargo() + "\n\n" +
                        "ADVERTENCIA: Esta acción NO se puede deshacer.\n" +
                        "El empleado será eliminado permanentemente de la base de datos.\n\n" +
                        "Se recomienda usar la opción 'Desactivar' en su lugar."
        );

        ButtonType btnEliminar = new ButtonType("Eliminar Permanentemente", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirmacion.getButtonTypes().setAll(btnEliminar, btnCancelar);

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == btnEliminar) {
            if (EmpleadoRepository.eliminarPermanente(seleccionado.getIdEmpleado())) {
                mostrarExito("Empleado eliminado permanentemente");
                cargarEmpleados();
            } else {
                mostrarError("Error al eliminar empleado");
            }
        }
    }

    private void abrirFormularioEmpleado(Empleado empleado) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sistema_rentacar/Views/empleado/FormularioEmpleado.fxml"));
            Parent root = loader.load();

            FormularioEmpleadoController controller = loader.getController();
            controller.setDatos(empleado, this);

            Stage dialog = new Stage();
            dialog.setTitle(empleado == null ? "Nuevo Empleado" : "Editar Empleado");
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setScene(new Scene(root));
            dialog.setResizable(false);
            dialog.showAndWait();

        } catch (Exception e) {
            System.err.println("Error al abrir formulario: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRefrescarEmpleados() {
        cargarEmpleados();
    }

    // ============ ACCIONES CATEGORÍAS ============

    @FXML
    private void handleAgregarTipo() {
        abrirFormularioTipo(null);
    }

    @FXML
    private void handleEditarTipo() {
        TipoVehiculo seleccionado = tableTipos.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            abrirFormularioTipo(seleccionado);
        }
    }

    @FXML
    private void handleEliminarTipo() {
        TipoVehiculo seleccionado = tableTipos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) return;

        if (tipoVehiculoRepository.tieneVehiculosAsociados(seleccionado.getIdTipo())) {
            mostrarError("No se puede eliminar esta categoría porque tiene vehículos asociados");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Eliminar Categoría");
        confirmacion.setHeaderText("¿Eliminar categoría?");
        confirmacion.setContentText(seleccionado.getNombreTipo());

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            if (tipoVehiculoRepository.eliminar(seleccionado.getIdTipo())) {
                mostrarExito("Categoría eliminada correctamente");
                cargarTiposVehiculo();
            } else {
                mostrarError("Error al eliminar categoría");
            }
        }
    }

    private void abrirFormularioTipo(TipoVehiculo tipo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sistema_rentacar/Views/empleado/FormularioTipoVehiculo.fxml"));
            Parent root = loader.load();

            FormularioTipoVehiculoController controller = loader.getController();
            controller.setDatos(tipo, this);

            Stage dialog = new Stage();
            dialog.setTitle(tipo == null ? "Nueva Categoría" : "Editar Categoría");
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setScene(new Scene(root));
            dialog.setResizable(false);
            dialog.showAndWait();

        } catch (Exception e) {
            System.err.println("Error al abrir formulario: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ============ MÉTODOS PÚBLICOS PARA REFRESCAR ============

    public void refrescarVehiculos() {
        cargarVehiculos();
        cargarDashboard();
    }

    public void refrescarEmpleados() {
        cargarEmpleados();
    }

    public void refrescarTiposVehiculo() {
        cargarTiposVehiculo();
    }
    @FXML
    private void handleRefrescarCategorias() {
        txtBuscarCategoria.clear();
        cargarTiposVehiculo();
    }

    // ============ CERRAR SESIÓN ============

    @FXML
    private void handleCerrarSesion() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sistema_rentacar/Views/Inicio-View.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) lblEmpleado.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Inicio - VSB Renta Car");
            stage.setMaximized(true);
            stage.centerOnScreen();

        } catch (Exception e) {
            System.err.println("Error al cerrar sesión: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void abrirDialogoEntregaAnticipada(Alquiler alquiler) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/sistema_rentacar/Views/Cliente/DialogoEntregaAnticipada.fxml")
            );
            Parent root = loader.load();

            DialogoEntregaAnticipadaController controller = loader.getController();
            controller.setDatos(alquiler, (motivo) -> {
                cargarAlquileres();
                cargarVehiculos();
                cargarDashboard();
                mostrarNotificacion("Vehículo entregado anticipadamente");
            });

            Stage stage = new Stage();
            stage.setTitle("Entrega Anticipada de Vehículo");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(btnFinalizarAlquiler.getScene().getWindow());
            stage.setResizable(false);
            stage.showAndWait();

        } catch (Exception e) {
            mostrarError("Error al cargar el diálogo: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void abrirDialogoFinalizarAlquiler(Alquiler alquiler) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/sistema_rentacar/Views/Cliente/DialogoFinalizarAlquiler.fxml")
            );
            Parent root = loader.load();

            DialogoFinalizarAlquilerController controller = loader.getController();

            //Pasar el rol del empleado actual
            controller.setDatos(alquiler, empleadoActual.getCargo(), (esRetraso) -> {
                cargarAlquileres();
                cargarVehiculos();
                cargarDashboard();

                if (esRetraso) {
                    mostrarNotificacion("Alquiler finalizado con penalización por retraso");
                } else {
                    mostrarNotificacion("Vehículo devuelto exitosamente");
                }
            });

            Stage stage = new Stage();
            stage.setTitle("Finalizar Alquiler");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(btnFinalizarAlquiler.getScene().getWindow());
            stage.setResizable(false);
            stage.showAndWait();

        } catch (Exception e) {
            mostrarError("Error al cargar el diálogo: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void mostrarNotificacion(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notificación");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.show();

        // Cerrar automáticamente después de 2 segundos
        new Thread(() -> {
            try {
                Thread.sleep(2000);
                Platform.runLater(() -> alert.close());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
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

    private void mostrarAdvertencia(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Advertencia");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void buscarAlquileres(String criterio) {
        if (criterio == null || criterio.trim().isEmpty()) {
            cargarAlquileres();
        } else {
            List<Alquiler> resultado = AlquilerRepository.buscarActivosPorCliente(criterio);
            alquileres = FXCollections.observableArrayList(resultado);
            tableAlquileres.setItems(alquileres);
        }
    }

    // Método para abrir el formulario
    private void abrirFormularioCliente(Cliente cliente) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/sistema_rentacar/Views/empleado/FormularioCliente.fxml")
            );
            Parent root = loader.load();

            FormularioClienteController controller = loader.getController();
            controller.setDatos(cliente, this);

            Stage dialog = new Stage();
            dialog.setTitle(cliente == null ? "Nuevo Cliente" : "Editar Cliente");
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setScene(new Scene(root, 650, 700));
            dialog.setResizable(false);
            dialog.showAndWait();

        } catch (Exception e) {
            System.err.println("Error al abrir formulario de cliente: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Método público para refrescar desde el formulario
    public void refrescarClientes() {
        cargarClientes();
        cargarDashboard(); // Actualiza también las estadísticas
    }


}