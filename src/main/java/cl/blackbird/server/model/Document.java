package cl.blackbird.server.model;


import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Document {
    private int id;
    private Client sender;
    private Client receiver;
    private Date date;
    private String fileName;
    private int length;

    public Document(Client sender, Client receiver, String fileName) {
        this.id = -1;
        this.sender = sender;
        this.receiver = receiver;
        this.fileName = fileName;
        this.date = new Date();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if(this.id == -1){
            this.id = id;
        }
    }

    public Client getSender() {
        return sender;
    }

    public Client getReceiver() {
        return receiver;
    }

    public String getFileName() {
        return fileName;
    }

    public int getNameLength() {
        return this.getFileName().getBytes().length;
    }

    public String getDateString() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        return formatter.format(this.date);
    }

    @Override
    public String toString() {
        return String.format("%d %d %s %s", getId(), getNameLength(), getDateString(), getFileName());
    }

    public void saveContent(int fileLength, BufferedReader input) throws IOException {
        this.length = fileLength;
        int remaining = this.length;
        FileOutputStream outputStream = null;
        while(remaining-- > 0){
            outputStream.write(input.read());
        }
    }

    public void writeContent(PrintStream output) {
        int remaining = this.length;
        while(remaining-- > 0) {
            output.write(0);
        }
    }
}
