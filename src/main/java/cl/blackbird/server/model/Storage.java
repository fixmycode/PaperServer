package cl.blackbird.server.model;

import java.io.IOException;
import java.util.ArrayList;

public class Storage {
    private ArrayList<Message> messageList;
    private ArrayList<Document> documentList;
    private int documentIndex;

    public Storage() {
        this.messageList = new ArrayList<Message>();
        this.documentList = new ArrayList<Document>();
        this.documentIndex = 1;
    }

    public synchronized int saveMessage(Message message) {
        int id = messageList.size()+1;
        message.setId(id);
        this.messageList.add(message);
        return message.getId();
    }

    public synchronized ArrayList<Message> getMessages(Client sender, Client receiver) {
        ArrayList<Message> response = new ArrayList<Message>();
        for(Message m : this.messageList) {
            if(m.getReceiver().equals(receiver) && m.getSender().equals(sender)){
                response.add(m);
            }
        }
        this.messageList.removeAll(response);
        return response;
    }

    public synchronized int saveDocument(Document document) throws IOException {
        document.setId(documentIndex++);
        this.documentList.add(document);
        return document.getId();
    }

    public synchronized Document getDocument(Client receiver, int id) {
        for(Document d : this.documentList) {
            if(d.getReceiver().equals(receiver) && d.getId() == id){
                return d;
            }
        }
        return null;
    }

    public synchronized void removeDocument(Client receiver, int id) {
        for(Document d : this.documentList) {
            if(d.getReceiver().equals(receiver) && d.getId() == id){
                this.documentList.remove(d);
                return;
            }
        }
    }

    public synchronized ArrayList<Document> getDocuments(Client sender, Client receiver) {
        ArrayList<Document> documents = new ArrayList<Document>();
        for(Document d : this.documentList) {
            if(d.getReceiver().equals(receiver) && d.getSender().equals(sender)){
                documents.add(d);
            }
        }
        return documents;
    }
}

