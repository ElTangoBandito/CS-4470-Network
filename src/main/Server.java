package main;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Server {

	public static void main(String[] args) {
		int PORTNUMBER = 12346;
		List<Peer> peerList = new ArrayList<>();
		System.out.println("BEGIN");
		System.out.println(getMyIP());
		try {
			ServerSocket listener = new ServerSocket(PORTNUMBER);
			while(true){
				Socket connection = listener.accept();
				PrintWriter out =
                        new PrintWriter(connection.getOutputStream(), true);
				Date time = new Date();
                out.println("Connected on " + time.toString());
                System.out.print("Connected to client");
                peerList.add(new Peer(connection));
                for (Peer peer: peerList) {
                	for(int i = 0; i < 10; i++) {
                		peer.sendMessage("Message " + i);
                	}
                }
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public static String getMyIP() {
		InetAddress ip;
		try {
			ip = Inet4Address.getLocalHost();
			return ip.getHostAddress();
		} catch (Exception e) {
			System.out.println("Can not get ip address:" + e.toString());
		}
		return "";
	}

}
