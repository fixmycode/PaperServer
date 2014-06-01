package cl.blackbird.server;


import cl.blackbird.server.listen.ClientThread;
import cl.blackbird.server.log.PaperLog;

import java.io.IOException;
import java.net.ServerSocket;
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

        try {
            ServerSocket serverSocket = new ServerSocket(port);
            logger.finest("Servidor iniciado con éxito en el puerto " + port);
            while(true){
                new ClientThread(serverSocket.accept()).run();
                break;
            }
        } catch (IOException e){
            logger.severe("Ocurrió un error");
            logger.severe(e.getMessage());
        }
    }
}
