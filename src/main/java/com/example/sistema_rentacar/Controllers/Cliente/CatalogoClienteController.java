package com.example.sistema_rentacar.Controllers.Cliente;

import com.example.sistema_rentacar.Repository.AlquilerRepository;
import com.example.sistema_rentacar.Repository.ClienteRepository;
import com.example.sistema_rentacar.Repository.TipoVehiculoRepository;
import com.example.sistema_rentacar.Repository.VehiculoRepository;
import com.example.sistema_rentacar.Modelos.Alquiler;
import com.example.sistema_rentacar.Modelos.Cliente;
import com.example.sistema_rentacar.Modelos.TipoVehiculo;
import com.example.sistema_rentacar.Modelos.Vehiculo;
import com.example.sistema_rentacar.Utilidades.CambiarScena;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CatalogoClienteController {

    @FXML private Label lblBienvenida;
    @FXML private Button btnMiPerfil;
    @FXML private Button btnHistorial;
    @FXML private Button btnAlquilerActivo;
    @FXML private Label lblAlquilerActivo;
    @FXML private Label lblTiempoRestante;
    @FXML private Label lblFechaFin;
    @FXML private VBox vboxAlquilerActivo;
    @FXML private VBox contenedorCategorias;
    @FXML private Button btnIniciarSesion;
    @FXML private Separator separadorSesion;

    private VehiculoRepository vehiculoRepository;
    private AlquilerRepository alquilerRepository;
    private TipoVehiculoRepository tipoVehiculoRepository;
    private ClienteRepository clienteRepository;
    private String nombreCliente;
    private int idCliente;
    private boolean esInvitado;

    // Para el cronómetro
    private Timeline timeline;
    private Timestamp fechaFinAlquiler;

    private Map<String, FlowPane> categoriaPanes = new HashMap<>();

    // Caché de imágenes para evitar recargas
    private static final Map<String, Image> imageCache = new HashMap<>();

    @FXML
    public void initialize() {
        vehiculoRepository = new VehiculoRepository();
        alquilerRepository = new AlquilerRepository();
        tipoVehiculoRepository = new TipoVehiculoRepository();
        clienteRepository = new ClienteRepository();

        crearSeccionesDinamicas();
    }

    @FXML
    private void handleIniciarSesion() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/sistema_rentacar/Views/cliente/LoginCliente.fxml")
            );
            Parent root = loader.load();

            Stage stage = (Stage) lblBienvenida.getScene().getWindow();
            stage.setScene(new Scene(root, 900, 600));
            stage.setTitle("Inicio de Sesión - Cliente");
            stage.centerOnScreen();

        } catch (Exception e) {
            System.err.println("Error al abrir inicio de sesión: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setDatosCliente(int idCliente, String nombreCliente) {
        this.idCliente = idCliente;
        this.nombreCliente = nombreCliente;
        this.esInvitado = false;

        lblBienvenida.setText("Bienvenido, " + nombreCliente);

        btnMiPerfil.setVisible(true);
        btnMiPerfil.setManaged(true);
        btnHistorial.setVisible(true);
        btnHistorial.setManaged(true);
        btnIniciarSesion.setVisible(false);
        btnIniciarSesion.setManaged(false);

        if (separadorSesion != null) {
            separadorSesion.setVisible(false);
            separadorSesion.setManaged(false);
        }

        crearSeccionesDinamicas();
        cargarVehiculosAsync();
        verificarAlquilerActivo();
    }

    public void setModoInvitado() {
        this.esInvitado = true;
        lblBienvenida.setText("Navegando como invitado - Regístrate para alquilar");

        btnMiPerfil.setVisible(false);
        btnMiPerfil.setManaged(false);
        btnHistorial.setVisible(false);
        btnHistorial.setManaged(false);

        if (vboxAlquilerActivo != null) {
            vboxAlquilerActivo.setVisible(false);
            vboxAlquilerActivo.setManaged(false);
        }

        btnIniciarSesion.setVisible(true);
        btnIniciarSesion.setManaged(true);

        if (separadorSesion != null) {
            separadorSesion.setVisible(true);
            separadorSesion.setManaged(true);
        }

        crearSeccionesDinamicas();
        cargarVehiculosAsync();
    }

    private void verificarAlquilerActivo() {
        if (timeline != null) {
            timeline.stop();
        }

        if (alquilerRepository.tieneAlquileresEnCurso(idCliente)) {
            Alquiler alquiler = alquilerRepository.obtenerAlquilerEnCursoCliente(idCliente);

            if (alquiler != null) {
                String estadoTexto = "🚗 Alquiler activo: ";

                switch (alquiler.getEstado()) {
                    case "Por Vencer":
                        estadoTexto = "⚠️ Alquiler por vencer: ";
                        break;
                    case "Retrasado":
                        estadoTexto = "🚨 Alquiler retrasado: ";
                        break;
                }

                lblAlquilerActivo.setText(estadoTexto + alquiler.getVehiculo() + " (" + alquiler.getPlaca() + ")");
                fechaFinAlquiler = alquiler.getFechaFinEstimada();

                if (lblFechaFin != null && fechaFinAlquiler != null) {
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                    lblFechaFin.setText("📅 Fecha límite: " + formatter.format(fechaFinAlquiler));
                }

                if (vboxAlquilerActivo != null) {
                    vboxAlquilerActivo.setVisible(true);
                    vboxAlquilerActivo.setManaged(true);
                }

                if (btnAlquilerActivo != null) {
                    btnAlquilerActivo.setVisible(true);
                    btnAlquilerActivo.setManaged(true);
                }

                iniciarCronometro();
            }
        } else {
            if (vboxAlquilerActivo != null) {
                vboxAlquilerActivo.setVisible(false);
                vboxAlquilerActivo.setManaged(false);
            }
        }
    }

    private void iniciarCronometro() {
        if (fechaFinAlquiler == null || lblTiempoRestante == null) {
            return;
        }

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            actualizarTiempoRestante();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        actualizarTiempoRestante();
    }

    private void actualizarTiempoRestante() {
        if (fechaFinAlquiler == null || lblTiempoRestante == null) {
            return;
        }

        long ahoraMillis = System.currentTimeMillis();
        long finMillis = fechaFinAlquiler.getTime();
        long totalSegundos = (finMillis - ahoraMillis) / 1000;

        if (totalSegundos <= 0) {
            long totalSegundosRetraso = Math.abs(totalSegundos);
            long dias = totalSegundosRetraso / (24 * 3600);
            long horas = (totalSegundosRetraso % (24 * 3600)) / 3600;
            long minutos = (totalSegundosRetraso % 3600) / 60;
            long segundos = totalSegundosRetraso % 60;

            String tiempoTexto;
            if (dias > 0) {
                tiempoTexto = String.format("🚨 RETRASADO: %d día%s %02d:%02d:%02d",
                        dias, dias == 1 ? "" : "s", horas, minutos, segundos);
            } else {
                tiempoTexto = String.format("🚨 RETRASADO: %02d:%02d:%02d",
                        horas, minutos, segundos);
            }

            lblTiempoRestante.setText(tiempoTexto);
            lblTiempoRestante.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px; -fx-background-color: rgba(231, 76, 60, 0.9); -fx-padding: 6; -fx-background-radius: 5;");

            if (vboxAlquilerActivo != null) {
                vboxAlquilerActivo.setStyle("-fx-background-color: linear-gradient(to right, #e74c3c, #c0392b); -fx-background-radius: 8; -fx-padding: 12;");
            }

        } else {
            long dias = totalSegundos / (24 * 3600);
            long horas = (totalSegundos % (24 * 3600)) / 3600;
            long minutos = (totalSegundos % 3600) / 60;
            long segundos = totalSegundos % 60;

            String tiempoTexto;

            if (dias > 0) {
                tiempoTexto = String.format("⏱ %d día%s %02d:%02d:%02d",
                        dias, dias == 1 ? "" : "s", horas, minutos, segundos);
            } else {
                tiempoTexto = String.format("⏱ %02d:%02d:%02d", horas, minutos, segundos);
            }

            lblTiempoRestante.setText(tiempoTexto);

            if (dias == 0 && horas < 1) {
                lblTiempoRestante.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px; -fx-background-color: rgba(231, 76, 60, 0.8); -fx-padding: 6; -fx-background-radius: 5;");
                if (vboxAlquilerActivo != null) {
                    vboxAlquilerActivo.setStyle("-fx-background-color: linear-gradient(to right, #e74c3c, #c0392b); -fx-background-radius: 8; -fx-padding: 12;");
                }
            } else if (dias == 0 && horas < 24) {
                lblTiempoRestante.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px; -fx-background-color: rgba(243, 156, 18, 0.7); -fx-padding: 6; -fx-background-radius: 5;");
                if (vboxAlquilerActivo != null) {
                    vboxAlquilerActivo.setStyle("-fx-background-color: linear-gradient(to right, #f39c12, #e67e22); -fx-background-radius: 8; -fx-padding: 12;");
                }
            } else if (dias <= 1) {
                lblTiempoRestante.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px; -fx-background-color: rgba(241, 196, 15, 0.7); -fx-padding: 6; -fx-background-radius: 5;");
                if (vboxAlquilerActivo != null) {
                    vboxAlquilerActivo.setStyle("-fx-background-color: linear-gradient(to right, #f1c40f, #f39c12); -fx-background-radius: 8; -fx-padding: 12;");
                }
            } else {
                lblTiempoRestante.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 12px;");
                if (vboxAlquilerActivo != null) {
                    vboxAlquilerActivo.setStyle("-fx-background-color: linear-gradient(to right, #27ae60, #229954); -fx-background-radius: 8; -fx-padding: 12;");
                }
            }
        }
    }

    private void crearSeccionesDinamicas() {
        if (contenedorCategorias != null) {
            contenedorCategorias.getChildren().clear();
        }
        categoriaPanes.clear();

        List<TipoVehiculo> tipos = tipoVehiculoRepository.obtenerTodos();

        for (TipoVehiculo tipo : tipos) {
            crearSeccionCategoria(tipo);
        }
    }

    /**
     * MODIFICADO: Crea sección con FlowPane optimizado para 3 tarjetas por fila
     */
    private void crearSeccionCategoria(TipoVehiculo tipo) {
        VBox seccion = new VBox();
        seccion.setSpacing(15); // Reducido de 20 a 15
        VBox.setMargin(seccion, new Insets(0, 0, 0, 0));

        HBox header = new HBox();
        header.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        header.setSpacing(8); // Reducido de 10 a 8

        Label icono = new Label(obtenerIconoParaTipo(tipo.getNombreTipo()));
        icono.setStyle("-fx-font-size: 20px;"); // Reducido de 24px a 20px

        Label titulo = new Label(tipo.getNombreTipo());
        titulo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;"); // Reducido de 24px a 20px

        Separator separator = new Separator();
        HBox.setHgrow(separator, javafx.scene.layout.Priority.ALWAYS);

        header.getChildren().addAll(icono, titulo, separator);

        // OPTIMIZADO PARA 3 TARJETAS POR FILA
        FlowPane flowPane = new FlowPane();
        flowPane.setHgap(15);  // Espacio horizontal entre tarjetas
        flowPane.setVgap(15);  // Espacio vertical entre filas
        flowPane.setPrefWrapLength(1136); // Ancho disponible: 1366px - 230px (sidebar) = 1136px

        // Asegurar que las tarjetas se ajusten correctamente
        flowPane.setStyle("-fx-alignment: center-left;");

        categoriaPanes.put(tipo.getNombreTipo(), flowPane);

        seccion.getChildren().addAll(header, flowPane);

        if (contenedorCategorias != null) {
            contenedorCategorias.getChildren().add(seccion);
        }
    }

    private String obtenerIconoParaTipo(String nombreTipo) {
        String nombreLower = nombreTipo.toLowerCase();

        if (nombreLower.contains("económico")) return "💰";
        if (nombreLower.contains("sedan")) return "🚘";
        if (nombreLower.contains("suv") || nombreLower.contains("camioneta")) return "🚙";
        if (nombreLower.contains("pickup")) return "🚚";
        if (nombreLower.contains("lujo") || nombreLower.contains("premium")) return "⭐";
        if (nombreLower.contains("deportivo")) return "🏎";
        if (nombreLower.contains("eléctrico")) return "🔋";
        if (nombreLower.contains("clásico") || nombreLower.contains("vintage")) return "🚗";
        if (nombreLower.contains("van") || nombreLower.contains("minivan")) return "🚐";

        return "🚗";
    }

    /**
     * CARGA ASÍNCRONA DE VEHÍCULOS CON INDICADOR DE PROGRESO
     */
    private void cargarVehiculosAsync() {
        // Limpiar tarjetas anteriores
        for (FlowPane pane : categoriaPanes.values()) {
            pane.getChildren().clear();
        }

        // Mostrar indicador de carga
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setMaxSize(50, 50);

        StackPane loadingPane = new StackPane(progressIndicator);
        loadingPane.setStyle("-fx-padding: 50;");

        if (contenedorCategorias != null && !contenedorCategorias.getChildren().isEmpty()) {
            contenedorCategorias.getChildren().add(0, loadingPane);
        }

        // Crear tarea en segundo plano
        Task<List<Vehiculo>> task = new Task<>() {
            @Override
            protected List<Vehiculo> call() {
                return vehiculoRepository.obtenerTodos();
            }
        };

        task.setOnSucceeded(event -> {
            // Eliminar indicador de carga
            if (contenedorCategorias != null) {
                contenedorCategorias.getChildren().remove(loadingPane);
            }

            List<Vehiculo> vehiculos = task.getValue();
            cargarTarjetasProgresivamente(vehiculos);
        });

        task.setOnFailed(event -> {
            if (contenedorCategorias != null) {
                contenedorCategorias.getChildren().remove(loadingPane);
            }
            System.err.println("Error al cargar vehículos: " + task.getException().getMessage());
        });

        new Thread(task).start();
    }

    /**
     * Carga las tarjetas en pequeños grupos para mantener lo responsivo
     */
    private void cargarTarjetasProgresivamente(List<Vehiculo> vehiculos) {
        final int BATCH_SIZE = 6; // Cargar 6 tarjetas a la vez (2 filas de 3)
        final int[] index = {0};

        Timeline batchTimeline = new Timeline(new KeyFrame(Duration.millis(50), event -> {
            int endIndex = Math.min(index[0] + BATCH_SIZE, vehiculos.size());

            for (int i = index[0]; i < endIndex; i++) {
                Vehiculo vehiculo = vehiculos.get(i);
                try {
                    VBox tarjeta = crearTarjetaVehiculo(vehiculo);
                    FlowPane paneCategoria = categoriaPanes.get(vehiculo.getNombreTipo());

                    if (paneCategoria != null) {
                        paneCategoria.getChildren().add(tarjeta);
                    } else {
                        TipoVehiculo tipo = new TipoVehiculo();
                        tipo.setNombreTipo(vehiculo.getNombreTipo());
                        crearSeccionCategoria(tipo);

                        paneCategoria = categoriaPanes.get(vehiculo.getNombreTipo());
                        if (paneCategoria != null) {
                            paneCategoria.getChildren().add(tarjeta);
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Error al crear tarjeta: " + e.getMessage());
                }
            }

            index[0] = endIndex;
        }));

        batchTimeline.setCycleCount((int) Math.ceil((double) vehiculos.size() / BATCH_SIZE));
        batchTimeline.play();
    }

    private VBox crearTarjetaVehiculo(Vehiculo vehiculo) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sistema_rentacar/Views/cliente/VehiculoCard.fxml"));
        VBox tarjeta = loader.load();

        VehiculoCardController controller = loader.getController();
        controller.setDatos(vehiculo, idCliente, esInvitado, this);

        return tarjeta;
    }

    public void refrescarCatalogo() {
        crearSeccionesDinamicas();
        cargarVehiculosAsync();
        if (!esInvitado) {
            verificarAlquilerActivo();
        }
    }

    @FXML
    private void handleRefrescar() {
        refrescarCatalogo();
    }

    @FXML
    private void handleMiPerfil() {
        if (!esInvitado) {
            try {
                if (timeline != null) {
                    timeline.stop();
                }

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sistema_rentacar/Views/cliente/PerfilCliente.fxml"));
                Parent root = loader.load();

                PerfilClienteController controller = loader.getController();
                Cliente cliente = clienteRepository.obtenerPorId(idCliente);

                if (cliente != null) {
                    controller.setCliente(cliente);
                    Stage stage = (Stage) btnMiPerfil.getScene().getWindow();
                    stage.setScene(new Scene(root, 1000, 700));
                    stage.setTitle("Mi Perfil - Renta Car");
                } else {
                    System.err.println("Error: No se pudo cargar el cliente");
                }

            } catch (Exception e) {
                System.err.println("Error al abrir perfil: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleCerrarSesion() {
        try {
            if (timeline != null) {
                timeline.stop();
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sistema_rentacar/Views/Inicio-View.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) lblBienvenida.getScene().getWindow();

            CambiarScena.cambiar(stage, root, "Inicio - VSB Renta Car");

        } catch (Exception e) {
            System.err.println("Error al cerrar sesión: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleVerHistorial() {
        if (!esInvitado) {
            try {
                if (timeline != null) {
                    timeline.stop();
                }

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/sistema_rentacar/Views/cliente/HistorialCliente.fxml"));
                Parent root = loader.load();

                HistorialClienteController controller = loader.getController();
                controller.setDatosCliente(idCliente, nombreCliente);

                Stage stage = (Stage) lblBienvenida.getScene().getWindow();
                stage.setScene(new Scene(root, 1000, 700));
                stage.setTitle("Historial de Alquileres - VSB Renta Car");

                try {
                    stage.getIcons().add(new Image(getClass().getResourceAsStream("/com/example/sistema_rentacar/images/logo/logo.png")));
                } catch (Exception e) {
                    System.out.println("No se pudo cargar el icono de la aplicación");
                }

            } catch (Exception e) {
                System.err.println("Error al abrir historial: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleVerAlquilerActivo() {
        handleVerHistorial();
    }

    @FXML
    private void handleMouseEntered(MouseEvent event) {
        Button btn = (Button) event.getSource();
        btn.setStyle(btn.getStyle() + "; -fx-opacity: 0.8; -fx-scale-x: 1.05; -fx-scale-y: 1.05;");
    }

    @FXML
    private void handleMouseExited(MouseEvent event) {
        Button btn = (Button) event.getSource();
        btn.setStyle(btn.getStyle().replace("; -fx-opacity: 0.8; -fx-scale-x: 1.05; -fx-scale-y: 1.05;", ""));
    }

    /**
     * OPTIMIZADO: Pre-escala imágenes a tamaño reducido
     */
    public static Image getImageFromCache(String imagePath) {
        if (imageCache.containsKey(imagePath)) {
            return imageCache.get(imagePath);
        }

        try {
            // Reducido de 270x160 a 312x160 para las tarjetas más pequeñas
            Image image = new Image(CatalogoClienteController.class.getResourceAsStream(imagePath),
                    312, 160, true, true);
            imageCache.put(imagePath, image);
            return image;
        } catch (Exception e) {
            System.err.println("Error cargando imagen: " + imagePath);
            return null;
        }
    }
}