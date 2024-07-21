package ru.blackhedge.otus.chat.server;

import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.util.*;

public class Server {
    private int port;
    private List<ClientHandler> clients;
    private AuthenticationService authenticationService;

    public AuthenticationService getAuthenticationService() {
        return authenticationService;
    }

    public Server(int port) {
        this.port = port;
        this.clients = new ArrayList<>();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            this.authenticationService = new AuthenticationServiceImpl(this);
            System.out.println("Сервис аутентификации запущен: " + authenticationService.getClass().getSimpleName()
                    + "/" + authenticationService.getConnection());
            System.out.printf("Сервер запущен на порту: %d, ожидаем подключения клиентов\n", port);
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    new ClientHandler(this, socket);
                } catch (Exception e) {
                    System.out.println("Возникла ошибка при обработке подключившегося клиента");
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (SQLException ex){
            ex.printStackTrace();
        }
    }

    public synchronized void subscribe(ClientHandler clientHandler) {
        broadcastMessage("К чату присоединился " + clientHandler.getNickname());
        clients.add(clientHandler);
    }

    public synchronized void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        broadcastMessage("Из чата вышел " + clientHandler.getNickname());
    }

    public synchronized void kick(String kickNickname) {
        ClientHandler kickedUser = searchByNickname(kickNickname);
        if (kickedUser != null) {
            kickedUser.sendMessage("\u001B[41m" + "Вас исключили из чата" + "\033[0m");
            kickedUser.disconnect();
            broadcastMessage("\u001B[41mПользователь  " + kickedUser.getNickname() + " удален из чата\033[0m");
        }
    }

    public synchronized ClientHandler searchByNickname(String nickname) {
        for (ClientHandler c : clients) {
            if (c.getNickname().equals(nickname)) {
                return c;
            }
        }
        return null;
    }

    public synchronized void broadcastMessage(String message) {
        for (ClientHandler c : clients) {
            c.sendMessage(message);
        }
    }

    public void whisperMessage(ClientHandler sender, String receiver, String message) {
        ClientHandler receiverClient = null;
        for (ClientHandler c : clients) {
            if (c.getNickname().equals(receiver)) {
                c.sendMessage("\u001B[42m" + sender.getNickname() + "[w]" + ":\033[0m " + message.split(" ", 3)[2]);
                receiverClient = c;
                break;
            }
        }
        if (receiverClient == null) {
            broadcastMessage(sender.getNickname() + ": " + message);
        } else {
            sender.sendMessage("\u001B[42m" + sender.getNickname() + "[w]" + ":\033[0m " + message.split(" ", 3)[2]);
        }
    }

    public synchronized boolean isNicknameBusy(String nickname) {
        for (ClientHandler c : clients) {
            if (c.getNickname().equals(nickname)) {
                return true;
            }
        }
        return false;
    }
}
