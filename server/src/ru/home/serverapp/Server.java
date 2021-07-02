package ru.home.serverapp;

import ru.home.lib.Connection;
import ru.home.lib.Message;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.LinkedBlockingQueue;

public final class Server {

    private LinkedBlockingQueue<ServerMessage> readMessages = new LinkedBlockingQueue<>();
    private CopyOnWriteArraySet<Connection> connections = new CopyOnWriteArraySet<>();
    private final String stop = "/exit";

    public void start() {
        ServerSocket serverSocket;
        try {
            serverSocket = new ServerSocket(8080);
        } catch (IOException e) {
            System.out.println("Не удалось открыть серверный сокет!");
            e.printStackTrace();
            return;
        }
        new Thread(new ServerWriter()).start();
        while (!Thread.currentThread().isInterrupted()) {
            System.out.println("111111111111111111");

            try {
                Socket catchSocket = serverSocket.accept();
                Connection catchConnection = new Connection(catchSocket);
                connections.add(catchConnection);
                new Thread(new ServerReader(catchConnection)).start();
            } catch (IOException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }

    private class ServerReader implements Runnable {

        private final Connection connection;

        public ServerReader(Connection connection) {
            this.connection = connection;
        }

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Message message = connection.readMessage();
                    if (stop.equalsIgnoreCase(message.getData())) {
                        System.out.println(message.getSenderName() + " отключился от сервера");
                        connections.remove(connection);
                        connection.sendMessage(Message.getMessage("SERVER", "disconnect"));
                        connection.close();
                        break;
                    }
                    ServerMessage serverMessage = new ServerMessage(message, connection);
                    readMessages.put(serverMessage);
                    System.out.println(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class ServerWriter implements Runnable {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    ServerMessage fromQueue = readMessages.take();
                    for (Connection connection : connections) {
                        if (!fromQueue.getConnection().equals(connection))
                            connection.sendMessage(fromQueue.getMessage());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    private class ServerMessage {
        private Message message;
        private Connection connection;

        public ServerMessage(Message message, Connection connection) {
            this.message = message;
            this.connection = connection;
        }

        public Message getMessage() {
            return message;
        }

        public Connection getConnection() {
            return connection;
        }
    }
}
