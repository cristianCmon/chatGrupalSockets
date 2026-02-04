package com.chatgrupalsockets;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;


public class EchoServerMultihilo {

    private static final int PUERTO = 8080;

    // Tamaño del pool de hilos - número máximo de clientes atendidos simultáneamente
    // Si hay más clientes, esperan en cola hasta que se libere un hilo
    private static final int MAX_CLIENTES = 10;

    // LISTA GLOBAL DE TODOS LOS FLUJOS DE SALIDA (PrintWriter) PARA PODER ENVIAR MENSAJES A TODOS LOS CLIENTES
    public static List<PrintWriter> listaUsuarios = new CopyOnWriteArrayList<>();

    // LISTA GLOBAL CHATS
    public static List<String> listaChats = new ArrayList<>();
    // LISTA USUARIOS CONECTADOS CHAT
    public static List<ChatUsuario> listaChatUsuarios = new ArrayList<>();

    // AtomicInteger: Variable thread-safe para contar clientes sin sincronización explícita
    private static final AtomicInteger clientesConectados = new AtomicInteger(0);

    public static void main(String[] args) {
        // ExecutorService: Framework de alto nivel para gestionar hilos
        // newFixedThreadPool(): Crea un pool con número fijo de hilos reutilizables
        // - Los hilos permanecen vivos esperando nuevas tareas
        // - Si todos están ocupados, las tareas esperan en una cola interna
        // - Más eficiente que crear un hilo nuevo por cada cliente
        ExecutorService pool = Executors.newFixedThreadPool(MAX_CLIENTES);

        System.out.println("Servidor multihilo iniciado en puerto " + PUERTO);
        System.out.println("📊 Pool de hilos: " + MAX_CLIENTES);

        // try-with-resources: Garantiza cierre automático del ServerSocket al finalizar
        // ServerSocket: Socket pasivo que escucha conexiones entrantes en un puerto
        // - Vincula (bind) el puerto 8080 a esta aplicación
        // - Solo escucha, no establece conexiones por sí mismo
        // - Puede lanzar IOException si el puerto ya está en uso
        try (ServerSocket serverSocket = new ServerSocket(PUERTO)) {

            // Bucle infinito: El servidor permanece activo aceptando nuevas conexiones
            // Se detendrá solo si ocurre una excepción o se interrumpe el proceso
            while (true) {
                // accept(): Método BLOQUEANTE que espera una conexión entrante
                // - Detiene la ejecución hasta que un cliente se conecta
                // - Cuando un cliente se conecta, crea un nuevo Socket para comunicarse
                // - El ServerSocket sigue escuchando nuevas conexiones
                // - Retorna un Socket activo conectado al cliente específico
                Socket clienteSocket = serverSocket.accept();

                // incrementAndGet(): Operación atómica que incrementa y retorna el nuevo valor
                // Thread-safe: Múltiples hilos pueden llamarlo sin problemas de sincronización
                int numCliente = clientesConectados.incrementAndGet();

                System.out.println("✅ Cliente #" + numCliente + " conectado: " +
                        clienteSocket.getInetAddress());

                // execute(): Envía la tarea al pool de hilos
                // - Si hay un hilo libre, lo ejecuta inmediatamente
                // - Si todos están ocupados, la tarea espera en cola
                // - El hilo principal (main) no se bloquea, vuelve a accept() inmediatamente
                // - Esto permite aceptar nuevas conexiones mientras se atienden las existentes
                // ManejadorClienteMultihilo: Runnable que encapsula la lógica de atención al cliente
                pool.execute(new ManejadorClienteMultihilo(clienteSocket, numCliente));
            }

        } catch (IOException e) {
            // Captura errores de red o del socket
            System.err.println("Error en servidor: " + e.getMessage());

        } finally {
            // shutdown(): Inicia el apagado ordenado del pool
            // - No acepta nuevas tareas
            // - Los hilos activos finalizan sus tareas actuales
            // - Alternativa: shutdownNow() interrumpe inmediatamente todos los hilos
            pool.shutdown();
        }
    }

    static class ChatUsuario {
        String chat = "";
        String nombre = "";
//        AÑADIMOS NUEVA PROPIEDAD
        PrintWriter salida;

        public ChatUsuario(String chat, String nombre, PrintWriter salida) {
            this.chat = chat;
            this.nombre = nombre;
//            PARA PODER FILTRAR CHAT POSTERIORMENTE
            this.salida = salida;
        }

    }

}
