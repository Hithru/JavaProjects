package hithru.javaprojects.simple_tcp_multithread;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerThread implements Runnable{

    private Socket socket;
    public ServerThread (Socket socket){
        this.socket = socket;
    }

    @Override
    public void run(){
        try {
            System.out.println("Client has connected");
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
