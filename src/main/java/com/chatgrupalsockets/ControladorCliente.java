package com.chatgrupalsockets;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ControladorCliente {
    @FXML
    private ScrollPane spVentanaChat;
    @FXML
    private VBox vbVentanaChat;
    @FXML
    private TextField tfVentanaMensaje;

    private static final String HOST = "localhost";
    private static final int PUERTO = 8080;
    private Socket socket;
    private BufferedReader entrada;
    private PrintWriter salida;
    private Scanner scanner;

    private String mensajeUsuario;


    public void initialize(String nombreUsuario) {
        spVentanaChat.setVvalue(1.0); // Auto-scroll hacia abajo
        // ESTABLECEMOS CONEXIÓN CON EL SERVIDOR
        conexionServidor();

        // NADA MÁS CONECTAR ENVIAMOS NOMBRE A SERVIDOR
        salida.println(nombreUsuario);

        // INICIAMOS HILO QUE ESCUCHA CONSTANTEMENTE
        iniciarHiloEscucha();
    }

    public void iniciarHiloEscucha() {
        Thread hiloEscucha = new Thread(() -> {
            try {
                String mensajeRecibido;
                // Este bucle se queda esperando mensajes del servidor continuamente
                while ((mensajeRecibido = entrada.readLine()) != null) {
                    final String mensajeFinal = mensajeRecibido;
                    // Actualizamos la UI de forma segura
                    javafx.application.Platform.runLater(() -> {
                        vbVentanaChat.getChildren().add(new Text(mensajeFinal));
                    });
                }
            } catch (IOException e) {
                System.err.println("Conexión perdida con el servidor.");
            }
        });
        hiloEscucha.setDaemon(true); // Se cierra automáticamente al cerrar la App
        hiloEscucha.start();
    }



    public void mensajePulsarEnter(KeyEvent ke) {
        if (ke.getCode().equals(KeyCode.ENTER) && !tfVentanaMensaje.getText().isEmpty()) {
            mensajeUsuario = tfVentanaMensaje.getText();
            comunicacionServidor(mensajeUsuario);
        }
    }

    private void conexionServidor() {
        try {
            socket = new Socket(HOST, PUERTO);
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            salida = new PrintWriter(socket.getOutputStream(), true);

        } catch (UnknownHostException e) {
            throw new RuntimeException(e);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void comunicacionServidor(String mensaje) {
        // Enviar el mensaje puede ser rápido, pero leer la respuesta es lento
        salida.println(mensaje);
        tfVentanaMensaje.setText("");

        // Creamos un hilo para esperar la respuesta sin congelar la ventana
        new Thread(() -> {
            try {
                String respuesta = entrada.readLine();
                if (respuesta != null) {
                    // Para modificar la interfaz desde otro hilo, usamos Platform.runLater
                    javafx.application.Platform.runLater(() -> {
                        Text texto = new Text(respuesta);
                        vbVentanaChat.getChildren().add(texto);
                    });
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void recibirMensaje(String mensaje) {

    }


}
