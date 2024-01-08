module com.kloneborn {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    opens com.kloneborn to javafx.fxml;
    exports com.kloneborn;
}
