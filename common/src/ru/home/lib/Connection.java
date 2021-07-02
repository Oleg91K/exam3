package ru.home.lib;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public final class Connection implements Closeable {
    private final Socket socket;
    private ObjectInputStream input;
    private ObjectOutputStream output;

    public Connection (Socket socket){
        this.socket = socket;
        try {
            output = new ObjectOutputStream(this.socket.getOutputStream());
            input = new ObjectInputStream(this.socket.getInputStream());
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void sendMessage(Message message) throws IOException {
        message.setTime();
        output.writeObject(message);
        output.flush();
    }

    public Message readMessage() throws IOException, ClassNotFoundException {
        return (Message) input.readObject();
    }

    @Override
    public void close() throws IOException {
        input.close();
        output.close();
        socket.close();
    }
}