package ru.blackhedge.otus.chat.server;

import java.io.*;
import java.net.*;


public class ClientHandler {
    private Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String username;

    private static int usersCounter = 0;

    private void generateUserName() {
        usersCounter++;
        this.username = "user" + usersCounter;
    }

    public ClientHandler(Server server, Socket socket) throws IOException {
        this.server = server;
        this.socket = socket;
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
        this.generateUserName();
        new Thread(() -> {
            try {
                System.out.println("Подключился новый клиент");
                while (true) {
                    String msg = in.readUTF();
                    if (msg.startsWith("/")) {
                        if (msg.startsWith("/exit")) {
                            disconnect();
                            break;
                        }
                        continue;
                    }
                    server.broadcastMessage(username + ": " + msg);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                disconnect();
            }
        }).start();
    }

    public void sendMessage(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void disconnect() {
        server.unsubscribe(this);
        try {
            if (in != null) {
                in.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        try {
            if (out != null) {
                out.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
