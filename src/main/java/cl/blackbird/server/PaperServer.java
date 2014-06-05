package cl.blackbird.server;


import cl.blackbird.server.listen.ClientThread;
import cl.blackbird.server.log.PaperLog;
import cl.blackbird.server.model.Storage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class PaperServer {
    public static void main(String[] args){
        PaperLog.init();
        Logger logger = Logger.getLogger(PaperLog.class.getName());
        int port = 0;
        if(args.length > 1){
            System.err.println("Uso: java PaperServer <puerto>");
            System.exit(1);
        } else if (args.length == 1) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e){
                System.err.println("<puerto> debe ser un número");
                System.exit(1);
            }
        } else {
            port = 7777;
        }

        logger.info("Iniciando servidor en el puerto " + port + "...");

        Storage storage = new Storage();
        ExecutorService executor = Executors.newFixedThreadPool(10);

        try {
            ServerSocket serverSocket = new ServerSocket(port);
            logger.finest("Servidor iniciado con éxito en el puerto " + port);
            Socket client;
            int count = 0;
            while(count < 3){
                ClientThread worker = new ClientThread(serverSocket.accept(), storage);
                executor.execute(worker);
                count++;
            }
            executor.shutdown();
            logger.info("Atención finalizada. Cerrando conexión.");
            serverSocket.close();
        } catch (IOException e){
            logger.severe("Ocurrió un error");
            logger.severe(e.getMessage());
        }
    }
}
