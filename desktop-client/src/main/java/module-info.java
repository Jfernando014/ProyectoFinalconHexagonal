module co.edu.unicauca.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires java.net.http;


    opens co.edu.unicauca.client to javafx.fxml;
    exports co.edu.unicauca.client;
}