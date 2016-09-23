package main;
import java.lang.*;
import java.io.*;
import java.net.*;
import java.util.Date;

public class Server {
	
	public static int PORT_NUMBER = 4470;

    public static void main(String[] args) throws IOException {
        ServerSocket listener = new ServerSocket(PORT_NUMBER);
        try {
            while (true) {
                Socket socket = listener.accept();
                try {
                    PrintWriter out =
                        new PrintWriter(socket.getOutputStream(), true);
                    out.println(new Date().toString());
                } finally {
                    socket.close();
                }
            }
        }
        finally {
            listener.close();
        }
    }
}
