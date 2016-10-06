package main;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.List;

public class ProcessThread extends Thread {
	Socket connection;
	List<Peer> peerList;
	ServerSocket listener;

	// int count;
	ProcessThread(ServerSocket listenerIn, List<Peer> peerListIn) {
		// this.connection = connection;
		listener = listenerIn;
		peerList = peerListIn;

	}

	/*
	 * ProcessThread(int i){ count = i; }
	 */

	public void run() {
		try {
			while (true) {
				connection = listener.accept();

				PrintWriter out = new PrintWriter(connection.getOutputStream(),
						true);
				Date time = new Date();
				out.println("Connected on " + time.toString());
				System.out.print("Connected to client");
				peerList.add(new Peer(connection, listener.getLocalPort()));
				// DataInputStream packet = new
				// DataInputStream(connection.getInputStream());
				// System.out.println(packet.readUTF());
				for (Peer peer : peerList) {
					peer.printMessage();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public List<Peer> getPeerList() {
		return peerList;
	}
}
