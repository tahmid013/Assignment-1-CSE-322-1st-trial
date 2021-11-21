package Threading;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Server {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        ServerSocket welcomeSocket = new ServerSocket(6666);
        HashMap<String, String> map = new HashMap<>();
        ArrayList<String> Atleast_Once = new ArrayList<String>();
        ArrayList<String> ActiveUser = new ArrayList<String>();


        while(true) {
            System.out.println("Waiting for connection...");
            Socket socket = welcomeSocket.accept();
            System.out.println("Connection established");

            // open thread
            Thread worker = new Worker(socket, map, Atleast_Once, ActiveUser);
            worker.start();


        }

    }
}
