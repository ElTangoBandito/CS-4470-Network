package main;
import java.lang.*;
import java.io.*;
import java.net.*;
import java.util.*;


public class Main {
	// TODO: Get port number from user input
	private static int PORTNUMBER;
	
	// TODO: Using Map instead of List
	private static List<Peer> peerList = new ArrayList<>();
	
	public static void main(String[] args) throws IOException{
		// TODO: Process user input
		PORTNUMBER = getPort();
		// TODO: Multithreading
		/*
		ProcessThread pt = new ProcessThread(21);
		ProcessThread pt2 = new ProcessThread(22);
		ProcessThread pt3 = new ProcessThread(22);
		pt.start();
		pt2.start();
		pt3.start();
		*/
		System.out.println(getMyIP());
		ServerSocket listener = new ServerSocket(PORTNUMBER);
		ProcessThread pt = new ProcessThread(listener);
		pt.start();
		/*
        try {
            while (true) {
            	
                Socket socket = listener.accept();
                try {
                    PrintWriter out =
                        new PrintWriter(socket.getOutputStream(), true);
                    Date time = new Date();
                    out.println("Connected on " + time.toString());
                    System.out.print("Connected to client");
                    peerList.add(new Peer(socket, PORTNUMBER));
                    
                    while (true) {
                    	
                    }
                    
                    
                    listPeers();
                } finally {
                    socket.close();
                }
            }
        }
        finally {
            listener.close();
        }
        */
	}
	
	public static int getPort(){
		int userPort = 0;
		while(true){
			Scanner baseScanner = new Scanner(System.in);
			String userInput = baseScanner.nextLine();
			String[] userArgs = userInput.split("\\s+");
			
			if (userArgs.length == 2){
				if (userArgs[0].equals("./chat")){
					try{
						userPort = Integer.parseInt(userArgs[1]);
						if (userPort >= 1000 && userPort <= 65536){
							baseScanner.close();
							System.out.println(userPort);
							break;
						}
					}catch(Exception e){
						e.printStackTrace();
						userPort = 0;
					}
				}
			}
			else{
				continue;
			}
		}
		return userPort;
		
	}
	// ==============  REQUIRMENT FUNCTIONS  ================
	
	// REQUIEMENT # 1: help
	public void showHelp() {
		// TODO: show help information
	}
	
	// REQUIEMENT # 2: myip
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
	
	// REQUIEMENT # 3: myport
	public int getMyPortNumber() {
		return PORTNUMBER;
	}
	
	// REQUIEMENT # 4: connect <destination> <port no>
	public void connect(String destinationIP, int portNumber) {
		// TODO: Check if IP address is valid
		try {
			Socket peer = new Socket(destinationIP, portNumber);
			peerList.add(new Peer(peer, portNumber));
			// TODO: Send message to indicate connection is successful.
		} catch (Exception e) {
			// TODO: Show fail to connect message
			System.out.println("Can not connect to " + destinationIP + ". Error:" + e.toString());
		}
	}
	
	// REQUIEMENT # 5: list
	public static void listPeers() {
		System.out.println("id: IPaddress   PortNo.");
		for ( Peer peer : peerList) {
			System.out.println(peer.toString());
		}
	}
	
	// REQUIEMENT # 6: terminate <connection id.>
	public void terminate(int connectionID) {
		for ( Peer peer : peerList) {
			if (peer.getId() == connectionID) {
				peer.terminate();
				return;
			}
		}
	}
	
	// REQUIEMENT # 7: send <connection id.> <message>
	public void send(int connectionID, String message) {
		for ( Peer peer : peerList) {
			if (peer.getId() == connectionID) {
				peer.sendMessage(message);
				return;
			}
		}
	}
	
	// REQUIEMENT # 8: exit
	public void exit() {
		for ( Peer peer : peerList) {
			peer.terminate();
		}
	}
	
	// ==============  HELPER FUNCTIONS  ==============
	
}
