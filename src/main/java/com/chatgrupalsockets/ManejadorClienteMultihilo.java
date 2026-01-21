package com.chatgrupalsockets;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;
/**
 * ManejadorClienteMultihilo
 * ------------------------
 * Clase que implementa Runnable para manejar la comunicación con un
 * único cliente en un hilo separado. Este patrón permite que el servidor
 * acepte múltiples clientes y atienda cada uno concurrentemente.
 *
 * Responsabilidades:
 * - Leer mensajes enviados por el cliente a través del socket.
 * - Responder con un mensaje "ECHO" para cada línea recibida.
 * - Detectar la palabra especial "salir" para terminar la conexión.
 * - Gestionar correctamente los recursos (streams y socket).
 */
public class ManejadorClienteMultihilo implements Runnable {
    // Socket para comunicarse con el cliente asignado a este manejador
    private final Socket socket;
    // Identificador simple del cliente (usado solo para logs/amigabilidad)
    private final int numeroCliente;
    private String nombreUsuario;

    /**
     * Constructor
     * @param socket Socket ya conectado al cliente
     * @param numeroCliente Número identificador del cliente (para logs)
     */
    public ManejadorClienteMultihilo(Socket socket, int numeroCliente) {
        this.socket = socket;
        this.numeroCliente = numeroCliente;
        this.nombreUsuario = "Cliente #" + numeroCliente; // Nombre provisional
    }
    /**
     * Punto de entrada del hilo: gestiona la comunicación con el cliente.
     *
     * Implementación clave:
     * - Usamos try-with-resources para asegurar el cierre de los streams
     *   (BufferedReader y PrintWriter). El socket se cierra en el finally
     *   porque cerrar los streams no siempre cierra el socket en todas las
     *   implementaciones o si ocurre una excepción antes de crear los streams.
     * - PrintWriter se crea con autoflush=true para que cada println se envíe
     *   inmediatamente sin necesidad de llamar a flush() explícitamente.
     */
    @Override
    public void run() {
        try (
            // Flujo de entrada: lee líneas de texto desde el cliente
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // Flujo de salida: envía texto al cliente; autoflush activado
            PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);
        ) {

            // RECIBIMOS NOMBRE DESDE INITIALIZE() DE CLIENTE
            this.nombreUsuario = entrada.readLine();

            // AL CONECTAR, AÑADIMOS ESTE CLIENTE A LA LISTA GLOBAL
            EchoServerMultihilo.listaUsuarios.add(salida);


//            broadcast("📢 " + nombreUsuario + " se ha unido al chat.");


            String mensaje;

            // Bucle de lectura: procesa cada línea enviada por el cliente
            while ((mensaje = entrada.readLine()) != null) {
                String formatoMensaje = "[" + this.nombreUsuario + "] " + mensaje;
                // DIFUSIÓN (BROADCAST): Enviamos el mensaje a TODOS los conectados
                for (PrintWriter pw : EchoServerMultihilo.listaUsuarios) {
                    pw.println(formatoMensaje);
                }
            }

        } catch (IOException e) {
            // Informar errores de E/S relacionados con este cliente
            System.err.println("Error con cliente #" + numeroCliente + ": " + e.getMessage());

        } finally {
            // En el finally cerramos el socket para liberar el descriptor de
            // fichero aunque los streams ya hayan sido cerrados arriba.
            try {
                socket.close();
                System.out.println("❌ Cliente #" + numeroCliente + " desconectado");

            } catch (IOException e) {
                // Si falla el cierre, mostramos la traza para depuración
                e.printStackTrace();
            }
        }
    }
}
