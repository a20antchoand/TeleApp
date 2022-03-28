module es.lumsoft.teleapp {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;

    opens es.lumsoft.teleapp to javafx.fxml;
    exports es.lumsoft.teleapp;
}