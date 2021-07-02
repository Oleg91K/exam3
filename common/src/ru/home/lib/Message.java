package ru.home.lib;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Message implements Serializable {
    private String senderName;
    private String msgText;
    private LocalDateTime dateTime;

    private Message(String sender, String msgText) {
        this.senderName = sender;
        this.msgText = msgText;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getData() {
        return msgText;
    }

    public void setTime() {
        this.dateTime = LocalDateTime.now();
    }

    public static Message getMessage(String sender, String data){
        return new Message(sender, data);
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");
        return "Сообщение от клиента: " + senderName + ","
                + "  Дата и время отправления: " + msgText + " (" + dateTime.format(formatter) + ")";
    }
}