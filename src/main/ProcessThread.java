package main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class ProcessThread extends Thread{
	List<Peer> peerList;
	ServerSocket listener;
	
	ProcessThread(ServerSocket listenerIn, List<Peer> peerListIn){
		listener = listenerIn;
		peerList = peerListIn;
	}
	
	public void run(){
		try {
			while(true){
				// Accepting new connections
				Socket connection = listener.accept();
                System.out.println("Connected to peer at " + connection.getInetAddress());
                peerList.add(new Peer(connection));

                // Reading and printing message from connected peers
                for (Peer peer: peerList) {
                	peer.printMessage();
                }
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
