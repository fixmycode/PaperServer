package cl.blackbird.server.model;


import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Document {
    private int id;
    private Client sender;
    private Client receiver;
    private Date date;
    private String fileName;
    private int length;
    private File file;

    public Document(Client sender, Client receiver, String fileName) {
        this.id = -1;
        this.sender = sender;
        this.receiver = receiver;
        this.fileName = fileName;
        this.date = new Date();
        this.file = null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) throws IOException {
        if(this.id == -1){
            this.id = id;
            this.file = File.createTempFile(id+"_"+getFileName(), null);
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
        FileOutputStream outputStream = new FileOutputStream(this.file);
        while(remaining-- > 0){
            outputStream.write(input.read());
        }
        outputStream.flush();
        outputStream.close();
    }

    public void writeContent(PrintStream output) throws IOException {
        int remaining = this.length;
        FileInputStream inputStream = new FileInputStream(this.file);
        while(remaining-- > 0) {
            output.write(inputStream.read());
        }
        inputStream.close();
    }
}
