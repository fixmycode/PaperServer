package cl.blackbird.server.protocol;

import cl.blackbird.server.log.PaperLog;
import cl.blackbird.server.model.Client;
import cl.blackbird.server.model.Document;
import cl.blackbird.server.model.Message;
import cl.blackbird.server.model.Storage;

import java.io.*;
import java.net.Socket;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.logging.Logger;


public class PaperProtocol {
    private BufferedReader input;
    private PrintStream output;
    private Client source;
    private Logger logger;
    private final Storage storage;

    public PaperProtocol(Client client, Storage storage) throws IOException {
        this.source = client;
        this.storage = storage;
        Socket socket = client.getSocket();
        this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.output = new PrintStream(socket.getOutputStream());
        this.logger = Logger.getLogger(PaperLog.class.getName());
    }

    public void dialog() throws IOException {
        while(true){
            String instruction;
            char[] rawInstruction = new char[4];
            this.input.read(rawInstruction, 0, 4);
            instruction = new String(rawInstruction);
            try {
                if (instruction.equals("BYEE")) {
                    logger.info("Cliente " + source + " solicita cierre de sesión");
                    break;
                } else if (instruction.equals("PULL")) {
                    pull(new Client(readAddress()));
                } else if (instruction.equals("PUSH")) {
                    push(new Client(readAddress()), readMessage(readInt()));
                } else if (instruction.equals("FILE")) {
                    file(new Client(readAddress()), readMessage(readInt()), readInt());
                } else if (instruction.equals("GETF")) {
                    getf(readInt());
                } else if (instruction.equals('\n') || instruction.equals('\r')) {
                    System.out.println(instruction);
                    break;
                }
            } catch (ParseException e) {
                logger.warning("Error al interpretar dirección IP");
                sendError(400);
            } catch (FileNotFoundException e) {
                logger.warning("Archivo no encontrado");
                sendError(404);
            } catch (NumberFormatException e) {
                logger.warning("Error al leer un número");
                sendError(411);
            } catch (IOException e) {
                logger.warning("Error de entrada/salida");
                sendError(500);
            }
        }
    }

    private void push(Client destiny, String message) throws IOException {
        logger.info("Cliente " + source + " envía mensaje a " + destiny);
        Message msg;
        msg = new Message(source, destiny, message);
        storage.saveMessage(msg);
        this.output.println(String.format("OK %d %s", msg.getId(), msg.getDateString()));
    }

    private void pull(Client sender) throws IOException {
        logger.info("Cliente " + source + " solicita mensajes enviados por " + sender);
        ArrayList<Message> messages = storage.getMessages(sender, source);
        ArrayList<Document> documents = storage.getDocuments(sender, source);
        this.output.println(String.format("OK %d messages %d files", messages.size(), documents.size()));
        for(Message m : messages) {
            this.output.println(m);
        }
        for(Document d : documents) {
            this.output.println(d);
        }
    }

    private void file(Client destiny, String filename, int fileLength) throws IOException {
        logger.info("Cliente " + source + " solicita enviar el archivo \"" + filename + "\" al cliente " + destiny);
        logger.info("Largo del archivo: " + fileLength + " bytes");
        Document document = new Document(source, destiny, filename);
        storage.saveDocument(document);
        document.saveContent(fileLength, this.input);
        this.output.println(String.format("OK %d %s", document.getId(), document.getDateString()));
    }

    private void getf(int fileId) throws IOException {
        logger.info("Cliente " + source + " solicita el archivo con ID " + fileId);
        Document document = storage.getDocument(source, fileId);
        if(document != null) {
            this.output.print(String.format("OK %d ", document.getLength()));
            document.writeContent(this.output);
            storage.removeDocument(source, fileId);
        } else {
            throw new FileNotFoundException();
        }
    }

    private void sendError(int code) throws IOException {
        this.output.println("ERRN "+code);
    }

    private String readAddress() throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        Boolean trailing = true;
        int ch;
        while ((ch = this.input.read()) >= 0) {
            if ((ch >= '0' && ch <= '9') || ch == '.' || ch == ':') {
                output.write(ch);
                if (trailing) trailing = false;
            } else if (trailing) {
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
        while ((ch = this.input.read()) != -1) {
            if (ch == ' ' && !trailing) {
                break;
            } else if (ch >= '0' && ch <= '9') {
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
        while (remaining-- > 0) {
            output.write(this.input.read());
        }
        return new String(output.toByteArray(), "UTF-8");
    }
}
