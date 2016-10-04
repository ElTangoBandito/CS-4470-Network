package main;
import java.io.*;
import java.net.*;
import java.util.*;

class Peer extends Object {
	Socket socket;
	private static int counter = 1;
	int id;
	String ip;
	int port;
	
	public Peer(Socket socket, int port) {
		this.id = counter++;
		this.socket = socket;
		this.port = port;
		this.ip = socket.getInetAddress().getHostAddress();
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getId() {
		return id;
	}

	@Override
	public String toString() {
		return id + ": " + ip + "    " + port;
	}
	
	public void terminate() {
		try {
			// TODO: send termination message to peer
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendMessage(String message) {
		PrintWriter out;
		try {
			out = new PrintWriter(socket.getOutputStream(), true);
			out.println(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void printMessage() {
        BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String message = reader.readLine();
            System.out.println("From: " + id);
            System.out.println(message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}