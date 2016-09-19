package main;
import java.lang.*;
import java.io.*;
import java.net.*;

public class Server {

	public static void main(String[] args) {
		String data = "hello world";
		try {
			ServerSocket srvr = new ServerSocket(1234);
			Socket skt = srvr.accept();
			System.out.println("Server has connected");
			PrintWriter out = new PrintWriter(skt.getOutputStream(), true);
			out.print(data);
			out.close();
			skt.close();
			srvr.close();
		}
		catch (Exception e) {
			System.out.print("Error:" + e.toString());
		}

	}

}
