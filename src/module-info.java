module Movies {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
	requires javafx.base;

    opens application to javafx.base, javafx.fxml, javafx.graphics;
    exports application; // Allow other modules to use the application package
}
