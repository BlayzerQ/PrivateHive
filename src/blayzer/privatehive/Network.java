package blayzer.privatehive;

import java.io.*;
import java.net.Socket;

public class Network {

    private static Resender resend = new Resender();
    public static BufferedReader in;
    public static PrintWriter out;
    private static Socket socket;
    public static boolean isConnected = false;

    public static void connect(){
        try {
            // Подключаемся к серверу и получаем потоки(in и out) для передачи сообщений
            socket = new Socket("localhost", 8283);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF8"));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF8"), true);

            // Запускаем вывод всех входящих сообщений
            resend.start();
            isConnected = true;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Закрывает входной и выходной потоки и сокет
    public static void close() {
        try {
            in.close();
            out.close();
            socket.close();
            isConnected = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Принимает все сообщения от сервера, в отдельном потоке.
    private static class Resender extends Thread {
        // Считывает все сообщения от сервера и выводит их в окно чата.
        @Override
        public void run() {
            try {
                while (isConnected) {
                    String[] input = in.readLine().split(" ");
                    StringBuilder out = new StringBuilder();
                    for(int i = 0; i < input.length; i++){
                        if(i == 3 && input[3].contains("=") || input[3].contains("+")) {
                            input[3] = AES.decrypt(input[3], Controller.keyHash);
                        }
                        out.append(" " + input[i]);
                    }
                    Controller.message = out.toString().trim();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
