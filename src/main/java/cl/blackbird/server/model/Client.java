package cl.blackbird.server.model;

import cl.blackbird.server.log.PaperLog;

import java.io.IOException;
import java.net.Socket;
import java.text.ParseException;
import java.util.logging.Logger;

/**
 * Created by oni on 6/1/14.
 */
public class Client {
    private Socket socket;
    private String address;
    private int port;

    public Client(Socket socket){
        this(socket.getInetAddress().getHostAddress(), socket.getPort());
        this.socket = socket;
    }

    public Client(String fullAddress) throws ParseException {
        try {
            this.socket = null;
            String[] parts = fullAddress.split(":");
            this.address = parts[0];
            this.port = Integer.parseInt(parts[1]);
        } catch (Exception e) {
            Logger.getLogger(PaperLog.class.getName()).severe("Error al traducir dirección de cliente "+fullAddress);
            throw new ParseException(fullAddress, 0);
        }
    }

    public Client(String address, int port){
        this.socket = null;
        this.address = address;
        this.port = port;
    }

    @Override
    public String toString() {
        return address + ":" + port;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public Socket getSocket() {
        return socket;
    }

    public void close() throws IOException {
        if(socket != null) {
            this.socket.close();
        }
    }
}
