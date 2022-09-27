module com.ml.spatialtree {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.swing;


    opens com.ml.spatialtree to javafx.fxml;
    exports com.ml.spatialtree;
}