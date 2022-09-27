package com.ml.spatialtree;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SpatialApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SpatialApplication.class.getResource("app-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 691, 535);
        stage.setTitle("Spatial tree algorithm");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}