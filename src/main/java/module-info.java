module com.example.sistema_rentacar {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jbcrypt;
    requires javafx.graphics;
    requires java.mail;
    //requires com.example.sistema_rentacar;

    // Paquete base
    opens com.example.sistema_rentacar to javafx.fxml;
    exports com.example.sistema_rentacar;

    // Modelos
    exports com.example.sistema_rentacar.Modelos;

    // Controladores generales
    exports com.example.sistema_rentacar.Controllers;
    opens com.example.sistema_rentacar.Controllers to javafx.fxml;

    // Controladores de cliente
    exports com.example.sistema_rentacar.Controllers.Cliente;
    opens com.example.sistema_rentacar.Controllers.Cliente to javafx.fxml;

    // Controladores de empleado
    exports com.example.sistema_rentacar.Controllers.Empleado;
    opens com.example.sistema_rentacar.Controllers.Empleado to javafx.fxml;
}