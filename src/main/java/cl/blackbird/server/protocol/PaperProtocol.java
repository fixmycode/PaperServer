package cl.blackbird.server.protocol;

import cl.blackbird.server.log.PaperLog;
import cl.blackbird.server.model.Client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.ParseException;
import java.util.logging.Logger;


public class PaperProtocol {
    private InputStream inputStream;
    private OutputStream outputStream;
    private Client source;
    private Logger logger;

    public PaperProtocol(Client client) throws IOException {
        this.source = client;
        Socket socket = client.getSocket();
        this.inputStream = socket.getInputStream();
        this.outputStream = socket.getOutputStream();
        this.logger = Logger.getLogger(PaperLog.class.getName());
    }

    public void dialog() throws IOException {
        while(true) {
            String instruction;
            byte[] rawInstruction = new byte[4];
            this.inputStream.read(rawInstruction, 0, 4);
            instruction = new String(rawInstruction);
            if(instruction.equals("BYEE")){
                logger.info("Cliente "+source+" solicita cierre de sesión.");
                break;
            } else if(instruction.equals("PULL")) {
                try {
                    pull(new Client(readAddress()));
                } catch (ParseException e) {
                    sendError(400);
                }
            } else if(instruction.equals("PUSH")) {
                try {
                    push(new Client(readAddress()), readMessage(readInt()));
                } catch (ParseException e) {
                    sendError(400);
                } catch (NumberFormatException e) {
                    sendError(411);
                }
            } else if(instruction.equals("FILE")) {
                try {
                    file(new Client(readAddress()), readMessage(readInt()), readInt());
                } catch (ParseException e) {
                    sendError(400);
                } catch (NumberFormatException e) {
                    sendError(411);
                }
            } else if(instruction.equals("GETF")) {
                try {
                    getf(readInt());
                } catch (NumberFormatException e) {
                    sendError(400);
                }
            } else if(instruction.equals('\n') || instruction.equals('\r')){
                System.out.println(instruction);
                break;
            }
        }
    }

    private void push(Client destiny, String message) {
        logger.info("Cliente "+source+" envía el siguiente mensaje a "+destiny);
        logger.info(message);
    }

    private void pull(Client sender) {
        logger.info("Cliente "+source+" solicita mensajes enviados por "+sender);
    }

    private void file(Client destiny, String filename, int fileLength) {
        logger.info("Cliente "+source+" solicita enviar el archivo \""+filename+"\" al cliente "+destiny);
        logger.info("Largo del archivo: "+fileLength+" bytes");
    }

    private void getf(int fileId) {
        logger.info("Cliente "+source+" solicita el archivo con ID "+fileId);
    }

    private void sendError(int code) throws IOException {
        try {
            this.outputStream.write(("ERRN "+code+"\r\n").getBytes());
        } catch (IOException e) {
            logger.severe("Error al enviar código de error a cliente "+source);
            throw new IOException();
        }
    }

    private String readAddress() throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Boolean trailing = true;
        int ch;
        while((ch = this.inputStream.read()) >= 0){
            if((ch >= '0' && ch <= '9') || ch == '.' || ch == ':'){
                output.write(ch);
                if(trailing) trailing = false;
            } else if(trailing) {
                continue;
            } else {
                break;
            }
        }
        return new String(output.toByteArray(), "UTF-8");
    }

    private int readInt() throws IOException, NumberFormatException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Boolean trailing = true;
        int ch;
        while((ch = this.inputStream.read()) != -1){
            if(ch == ' ' && trailing){
                continue;
            } else if(ch >= '0' && ch <= '9'){
                trailing = false;
                output.write(ch);
            } else {
                break;
            }
        }
        return Integer.parseInt(new String(output.toByteArray(), "UTF-8"));
    }

    private String readMessage(int length) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        int remaining = length;
        while(remaining-- > 0){
            output.write(this.inputStream.read());
        }
        return new String(output.toByteArray(), "UTF-8");
    }
}
