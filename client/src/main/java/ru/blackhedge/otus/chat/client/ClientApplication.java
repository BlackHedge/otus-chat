package ru.blackhedge.otus.chat.client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientApplication {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try (Socket socket = new Socket("localhost", 8189);
             DataInputStream in = new DataInputStream(socket.getInputStream());
             DataOutputStream out = new DataOutputStream(socket.getOutputStream())
        ) {
            System.out.println("Подключились к серверу");
            new Thread(() -> {
                try {
                    while (true) {
                        String inMessage = in.readUTF();
                        System.out.println(inMessage);
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }).start();
            while (true) {
                String msg = scanner.nextLine();
                out.writeUTF(msg);
                if (msg.equals("/exit")) {
                    break;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
