package ru.blackhedge.otus.chat.server;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private int port;
    private List<ClientHandler> clients;

    public Server(int port) {
        this.port = port;
        this.clients = new ArrayList<>();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.printf("Сервер запущен на порту: %d, ожидаем подключения клиентов\n", port);
            while (true) {
                Socket socket = serverSocket.accept();
                subscribe(new ClientHandler(this, socket));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public synchronized void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
    }

    public synchronized void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }

    public synchronized void broadcastMessage(String message) {
        for (ClientHandler c : clients) {
            c.sendMessage(message);
        }
    }

    public void personalMessage(ClientHandler sender, String receiver, String message) {
        ClientHandler receiverClient = null;
        for (ClientHandler c : clients) {
            if (c.getUsername().equals(receiver)) {
                c.sendMessage("\u001B[42m" + sender.getUsername() + "[w]" + ":\033[0m " + message.split(" ")[2]);
                receiverClient = c;
                break;
            }
        }
        if (receiverClient == null) {
            broadcastMessage(sender.getUsername() + ": " + message);
        } else {
            sender.sendMessage("\u001B[42m" + sender.getUsername() + "[w]" + ":\033[0m " + message.split(" ")[2]);
        }
    }
}
