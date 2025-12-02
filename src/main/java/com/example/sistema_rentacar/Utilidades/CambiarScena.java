package com.example.sistema_rentacar.Utilidades;

import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CambiarScena {

    public static void cambiar(Stage stage, Parent root, String titulo) {
        // Guardar estado de maximización antes de cambiar
        boolean wasMaximized = stage.isMaximized();

        // Cambiar la escena SIN especificar dimensiones
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle(titulo);

        // Aplicar maximización en el siguiente ciclo
        Platform.runLater(() -> {
            if (wasMaximized) {
                stage.setMaximized(true);
            } else {
                stage.centerOnScreen();
            }
        });

        stage.show();
    }


    public static void cambiarConDimensiones(Stage stage, Parent root, String titulo, double width, double height) {
        // Guardar estado de maximización
        boolean wasMaximized = stage.isMaximized();

        // Cambiar la escena con dimensiones específicas
        Scene scene = new Scene(root, width, height);
        stage.setScene(scene);
        stage.setTitle(titulo);

        Platform.runLater(() -> {
            if (wasMaximized) {
                stage.setMaximized(true);
            } else {
                stage.centerOnScreen();
            }
        });

        stage.show();
    }


    public static void cambiarMaximizado(Stage stage, Parent root, String titulo) {
        // Crear escena sin dimensiones fijas
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle(titulo);

        // Forzar maximización después de mostrar
        Platform.runLater(() -> {
            stage.setMaximized(true);
        });

        stage.show();
    }
}
