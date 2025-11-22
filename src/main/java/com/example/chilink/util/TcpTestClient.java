package com.example.chilink.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TcpTestClient {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 9090;

        try (Socket socket = new Socket(host, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Device ON
            out.println("1:ON");
            System.out.println("Server reply: " + in.readLine());

            // Device OFF
            out.println("1:OFF");
            System.out.println("Server reply: " + in.readLine());

            // Device TOGGLE
            out.println("1:TOGGLE");
            System.out.println("Server reply: " + in.readLine());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
