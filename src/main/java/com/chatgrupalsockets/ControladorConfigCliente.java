package com.chatgrupalsockets;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class ControladorConfigCliente {
    @FXML
    private TextField tfNombreUsuario;
    @FXML
    private Button btnAccederChat;


    public void clicAccederChat(ActionEvent actionEvent) {
        if (!tfNombreUsuario.getText().isEmpty()) {
            String nombreUsuario = tfNombreUsuario.getText();

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/chatgrupalsockets/vista-cliente.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root);
                Stage stage = new Stage();

                // Pasamos por parámetro el nombre al controlador Cliente
                ControladorCliente vistaChat = loader.getController();
                vistaChat.initialize(nombreUsuario);

                stage.setTitle("CHAT GRUPAL");
                stage.setScene(scene);
                stage.setResizable(false);
                stage.centerOnScreen();
                stage.show();

            } catch (IOException e) {
                    System.out.println("ERROR - " + e);

            } finally {
                // Cerramos la ventana automáticamente
                Stage vistaActual = (Stage) btnAccederChat.getScene().getWindow();
                vistaActual.close();
            }

        } else {
            // Recordamos al usuario que debe introducir un nombre
            tfNombreUsuario.setPromptText("INTRODUCE ALGO");
        }
    }
}
