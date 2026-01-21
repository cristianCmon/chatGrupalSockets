package com.chatgrupalsockets;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("vista-configCliente.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        // Carga de estilos css por código
        String css = this.getClass().getResource("/estilos/vista-cliente.css").toExternalForm();
        scene.getStylesheets().add(css);

        stage.setTitle("Introducir Usuario");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.centerOnScreen();
        stage.show();
    }
}
