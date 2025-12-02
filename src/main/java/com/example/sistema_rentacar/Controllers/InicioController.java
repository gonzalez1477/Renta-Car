package com.example.sistema_rentacar.Controllers;

import com.example.sistema_rentacar.Controllers.Cliente.CatalogoClienteController;
import com.example.sistema_rentacar.Utilidades.CambiarScena;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class InicioController {

    @FXML
    private Button btnEmpleado;

    @FXML
    private Button btnCliente;

    @FXML
    private void onEmpleadoClick() {
        System.out.println("Acceso de Empleado");

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/sistema_rentacar/Views/empleado/LoginEmpleado.fxml")
            );
            Parent root = loader.load();

            Stage stage = (Stage) btnEmpleado.getScene().getWindow();
            CambiarScena.cambiar(stage, root, "Login Empleado - VSB Renta Car");

        } catch (Exception e) {
            System.err.println("Error al abrir login de empleado: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onClienteClick() {
        System.out.println("Acceso de Cliente");

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/sistema_rentacar/Views/cliente/CatalogoCliente.fxml")
            );
            Parent root = loader.load();
            CatalogoClienteController controller = loader.getController();
            controller.setModoInvitado();

            Stage stage = (Stage) btnCliente.getScene().getWindow();
            CambiarScena.cambiar(stage, root, "Catálogo - VSB Renta Car");

        } catch (Exception e) {
            System.err.println("Error al abrir catálogo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}