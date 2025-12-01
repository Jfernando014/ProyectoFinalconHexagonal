module co.edu.unicauca.dekstopclient {
    requires javafx.controls;
    requires javafx.fxml;


    opens co.edu.unicauca.dekstopclient to javafx.fxml;
    exports co.edu.unicauca.dekstopclient;
}