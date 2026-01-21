package com.chatgrupalsockets;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

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
    private String nombreUsuario;


    public void initialize(String usuario) {
        nombreUsuario = usuario;
        // ESTABLECEMOS CONEXIÓN CON EL SERVIDOR
        conexionServidor();

        // NADA MÁS CONECTAR ENVIAMOS NOMBRE A SERVIDOR/MANEJADOR
        salida.println(nombreUsuario);

        // INICIAMOS HILO QUE ESCUCHA CONSTANTEMENTE
        iniciarEscuchaServidor();
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

    public void mensajePulsarEnter(KeyEvent ke) {
        if (ke.getCode().equals(KeyCode.ENTER) && !tfVentanaMensaje.getText().isEmpty()) {
            // OBTENEMOS LO ESCRITO EN EL TextField
            String mensajeUsuario = tfVentanaMensaje.getText();
            // MANDAMOS MENSAJE A SERVIDOR/MANEJADOR
            salida.println(mensajeUsuario);
            // LIMPIAMOS TextField PARA UNA ESCRITURA FLUIDA CON EL TECLADO
            tfVentanaMensaje.setText("");
        }
    }

    public void iniciarEscuchaServidor() {
        Thread hiloEscucha = new Thread(() -> {
            try {
                String mensajeRecibido;

                // ESTE BUCLE ESPERA MENSAJES DEL SERVIDOR DE FORMA ININTERRUMPIDA
                while ((mensajeRecibido = entrada.readLine()) != null) {
                    Text mensajeFinal = new Text(mensajeRecibido);

                    // APLICAMOS ESTILO AL TEXTO QUE SE MOSTRARÁ EN PANTALLA
                    if (mensajeRecibido.contains("[" + nombreUsuario + "]")) {
                        mensajeFinal.setStyle("-fx-font-family: 'Monaco'; -fx-font-size: 20; -fx-fill: DarkSlateBlue;");
                    } else {
                        mensajeFinal.setStyle("-fx-font-family: 'Monaco'; -fx-font-size: 20; -fx-font-style: italic; -fx-fill: DimGrey;");
                    }

                    // ACTUALIZAMOS LA VISTA DE FORMA SEGURA
                    javafx.application.Platform.runLater(() -> {
                        vbVentanaChat.getChildren().add(mensajeFinal);
                        // FORZAMOS LAYOUT PARA QUE EL ScrollPane FUNCIONE CORRECTAMENTE
                        vbVentanaChat.layout();
                        spVentanaChat.setVvalue(1.0);
                    });
                }

            } catch (IOException e) {
                System.err.println("Conexión perdida con el servidor.");
            }
        });

        // EL HILO TERMINARÁ AL CERRAR LA APLICACIÓN
        hiloEscucha.setDaemon(true);
        hiloEscucha.start();
    }

}
