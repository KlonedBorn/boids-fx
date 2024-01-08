package com.kloneborn;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Launcher extends Application {

    private static final String VERSION = "1.0";

    private static Stage stage;

    @Override
    public void start(Stage stage) throws IOException {
        Launcher.stage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(Launcher.class.getResource("index.fxml"));
        stage.setTitle("JavaFX - Boids Ver. " + VERSION + " - By Kloneborn");
        Scene scene = (Scene) (fxmlLoader.load());
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setFullScreen(false);
        stage.show();
    }

    public static Stage getStage() {
        return stage;
    }

    public static void main(String[] args) {
        launch();
    }
}