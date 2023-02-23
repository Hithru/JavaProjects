package hithru.javaprojects.simple_tcp_multithread;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerThread implements Runnable{

    private Socket socket;
    private ServerMain server_main;
    public ServerThread (Socket socket,ServerMain server_main){
        this.socket = socket;
        this.server_main =server_main;
    }

    @Override
    public void run(){
        try {
            int client_number = server_main.getClientnumber();

            System.out.println("Client "+client_number+ " has connected");
            BufferedReader in_socket = new BufferedReader(new InputStreamReader((socket.getInputStream())));
            PrintWriter out_socket = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()),true);



            out_socket.println("Welcome ! what's your name");
            String message = in_socket.readLine();
            System.out.println("Client says: "+ message);


            socket.close();
            System.out.println("Socket is closed");
        }catch (Exception e){

        }
    }
}
