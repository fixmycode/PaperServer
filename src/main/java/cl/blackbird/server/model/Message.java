package cl.blackbird.server.model;


import java.text.SimpleDateFormat;
import java.util.Date;

public class Message {
    private int id;
    private Client sender;
    private Client receiver;
    private Date date;
    private String content;

    public Message(Client sender, Client receiver, String content) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.date = new Date();
        this.id = -1;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (this.id == -1) {
            this.id = id;
        }
    }

    public Client getSender() {
        return sender;
    }

    public Client getReceiver() {
        return receiver;
    }

    public String getContent() {
        return content;
    }

    public int getLength() {
        return this.getContent().getBytes().length;
    }

    public String getDateString() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        return formatter.format(this.date);
    }

    @Override
    public String toString() {
        return String.format("%d %d %s %s", getId(), getLength(), getDateString(), getContent());
    }


}
