package cl.blackbird.server.listen;

import cl.blackbird.server.log.PaperLog;
import cl.blackbird.server.model.Client;
import cl.blackbird.server.protocol.PaperProtocol;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Logger;


public class ClientThread implements Runnable {
    private Client client;
    private Logger logger;
    public ClientThread(Socket clientSocket){
        logger = Logger.getLogger(PaperLog.class.getName());
        this.client = new Client(clientSocket);
    }

    @Override
    public void run() {
        logger.info("Cliente " + getClient().toString() + " conectado.");
        try {
            PaperProtocol protocol = new PaperProtocol(getClient());
            protocol.dialog();
            logger.info("Cerrando sesión con cliente " + getClient().toString());
            this.client.close();
        } catch (IOException e){
            logger.severe("Error al comunicarse con cliente " + getClient().toString());
        }
    }

    private Client getClient(){
        return this.client;
    }
}
