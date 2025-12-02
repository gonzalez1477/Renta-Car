package com.example.sistema_rentacar;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;

//importamos el servicio de programador
import com.example.sistema_rentacar.Servicios.AlquilerScheduler;

import com.example.sistema_rentacar.Conexion.ConexionDB;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        //archivo para poder mostrar el ojo de visible para la contraseña
        Font font = Font.loadFont(
                getClass().getResourceAsStream(
                        "/com/example/sistema_rentacar/fonts/Font Awesome 7 Free-Solid-900.otf"
                ),
                16
        );

        // Probar conexión a base de datos
        if (!ConexionDB.testConnection()) {
            mostrarErrorConexion();
            return;
        }

        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("Views/Inicio-View.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        stage.setTitle("VSB RENTA CAR!");
        stage.setScene(scene);

        // Obtener dimensiones de la pantalla

        Screen screen = Screen.getPrimary();
        //límites visibles de la pantalla
        Rectangle2D limitesPantalla = screen.getVisualBounds();

        // Establecer posición y tamaño de pantalla completa
        stage.setX(limitesPantalla.getMinX());
        stage.setY(limitesPantalla.getMinY());
        stage.setWidth(limitesPantalla.getWidth());
        stage.setHeight(limitesPantalla.getHeight());

        // Agregar icono del sistema
        try {
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/com/example/sistema_rentacar/images/logo/logo.png")));
        } catch (Exception e) {
            System.out.println("No se pudo cargar el icono de la aplicación");
        }

        stage.show();

        // INICIAR EL SCHEDULER DE ALQUILERES
        AlquilerScheduler.iniciar();

        //Maximizar después en el siguiente ciclo
        Platform.runLater(() -> stage.setMaximized(true));
    }

    private void mostrarErrorConexion() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error de Conexión");
        alert.setHeaderText("No se pudo conectar a la base de datos");
        alert.setContentText("Verificar credenciales de la base de datos");
        alert.showAndWait();
        System.exit(1);
    }

    @Override
    public void stop() {
        //DETENER EL SCHEDULER AL CERRAR
        AlquilerScheduler.detener();
        System.out.println("Aplicación cerrada");
    }

    public static void main(String[] args) {
        launch();
    }
}
