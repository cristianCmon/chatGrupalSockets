package com.chatgrupalsockets;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class ControladorConfigChats {
    @FXML
    private TextField tfNombreUsuario, tfNombreChat, tfEscogerChat;
    @FXML
    private Button btnCrear, btnUnirseChat;


    public void clicCrearChat(ActionEvent actionEvent) {
        if (!tfNombreUsuario.getText().isEmpty() && !tfNombreChat.getText().isEmpty()) {
            String nombreUsuario = tfNombreUsuario.getText();
            String nombreChat = tfNombreChat.getText();
            crearChat(nombreUsuario, nombreChat);

        } else {
            // Recordamos al usuario que debe introducir un nombre
            if (tfNombreUsuario.getText().isEmpty()) {
                tfNombreUsuario.setPromptText("CAMPO OBLIGATORIO");
            }
            if (tfNombreChat.getText().isEmpty()) {
                tfNombreChat.setPromptText("CAMPO OBLIGATORIO");
            }
        }
    }

    public void clicUnirseChat(ActionEvent actionEvent) {
        if (!tfNombreUsuario.getText().isEmpty() && !tfEscogerChat.getText().isEmpty()) {
            String nombreUsuario = tfNombreUsuario.getText();
            String nombreChat = tfEscogerChat.getText();
            accederChat(nombreUsuario, nombreChat);

        } else {
            // Recordamos al usuario que debe introducir un nombre
            tfNombreUsuario.setPromptText("INTRODUCE ALGO");
        }
    }

    public void accederChat(String nombreUsuario, String nombreChat) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/chatgrupalsockets/vista-cliente.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();

            // Pasamos por parámetro el nombre al controlador Cliente
            ControladorCliente vistaChat = loader.getController();
            vistaChat.initialize(nombreUsuario, nombreChat);

            stage.setTitle("CHAT GRUPAL - " + nombreChat);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            System.out.println("ERROR - " + e);

        } finally {
            // Cerramos la ventana automáticamente
            Stage vistaActual = (Stage) btnUnirseChat.getScene().getWindow();
            vistaActual.close();
        }
    }

    public void crearChat(String nombreUsuario, String nombreChat) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/chatgrupalsockets/vista-cliente.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();

            // Pasamos por parámetro el nombre al controlador Cliente
            ControladorCliente vistaChat = loader.getController();
            vistaChat.initialize(nombreUsuario, nombreChat);

            stage.setTitle("CHAT GRUPAL - " + nombreChat);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            System.out.println("ERROR - " + e);

        } finally {
            // Cerramos la ventana automáticamente
            Stage vistaActual = (Stage) btnUnirseChat.getScene().getWindow();
            vistaActual.close();
        }
    }
}
