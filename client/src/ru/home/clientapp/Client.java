package ru.home.clientapp;

import ru.home.lib.Connection;
import ru.home.lib.Message;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Properties;
import java.util.Scanner;

public class Client {
    private Connection connection;
    private String clientName;
    private final Scanner scanner = new Scanner(System.in);

    public Client() {
        Properties properties = new Properties();
        try {
            this.connection = new Connection(new Socket("127.0.0.1", 8080));
        } catch (IOException  | NullPointerException e) {
            e.printStackTrace();
        }
    }

    public void clientAppStart() {
        new Thread(new Writer()).start();
        new Thread(new Reader()).start();
    }

    private class Reader implements Runnable {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Message read = connection.readMessage();
                    if (!("SERVER".equalsIgnoreCase(read.getSenderName())
                            && "disconnect".equalsIgnoreCase(read.getData()))) {
                        System.out.println(read);
                    } else {
                        System.out.println("Отключение от сервера");
                        break;
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    private class Writer implements Runnable {
        @Override
        public void run() {
            while (true) {
                System.out.println("Введите ваше имя: ");
                String string = scanner.nextLine();
                if ("server".equalsIgnoreCase(string)) {
                    System.out.println("Нельзя использовать данное имя!!!");
                } else {
                    clientName = string;
                    break;
                }
            }
            while (!Thread.currentThread().isInterrupted()) {
                System.out.println("Введите текст сообщения: ");
                String text = scanner.nextLine();
                Message newMessage = Message.getMessage(Client.this.clientName, text);
                try {
                    connection.sendMessage(newMessage);
                } catch (IOException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
                if ("/exit".equalsIgnoreCase(text)) {
                    break;
                }
            }
        }
    }
}






