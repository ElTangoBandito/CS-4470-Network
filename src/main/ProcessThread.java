package main;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProcessThread extends Thread{
	Socket connection;
	List<Peer> peerList;
	ServerSocket listener;
	//int count;
	ProcessThread(ServerSocket listenerIn){
		//this.connection = connection;
		listener = listenerIn;
		peerList = new ArrayList<Peer>();
		
	}
	/*
	ProcessThread(int i){
		count = i;
	}*/
	
	public void run(){
		try {
			while(true){
				connection = listener.accept();
				PrintWriter out =
                        new PrintWriter(connection.getOutputStream(), true);
				Date time = new Date();
                out.println("Connected on " + time.toString());
                System.out.print("Connected to client");
                peerList.add(new Peer(connection, listener.getLocalPort()));
				//DataInputStream packet = new DataInputStream(connection.getInputStream());
				//System.out.println(packet.readUTF());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public List<Peer> getPeerList(){
		return peerList;
	}
}
