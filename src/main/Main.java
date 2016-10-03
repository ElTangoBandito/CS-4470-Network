package main;
import java.lang.*;
import java.io.*;
import java.net.*;
import java.util.*;


public class Main {
	// TODO: Get port number from user input
	private static int PORTNUMBER;
	private static boolean terminated;
	private static Scanner baseScanner;
	
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
		//System.out.println(getMyIP());
		ServerSocket listener = new ServerSocket(PORTNUMBER);
		ProcessThread pt = new ProcessThread(listener, peerList);
		pt.start();
		
		//UserThread userInputThread = new UserThread();
		//userInputThread.start();
		
		
		terminated = false;
		while(!terminated){
			if (baseScanner.hasNext()){
				String userArgs = baseScanner.nextLine();
				String[] userInput = userArgs.split("\\s+");
				//String userInput[] = userInputThread.getUserInput();
				if (userInput != null){
					if (userInput[0].equals("help")){
						showHelp();
					}
					else if(userInput[0].equals("myip")){
						System.out.println(getMyIP());
					}
					else if(userInput[0].equals("myport")){
						System.out.println(getPort());
					}
					else if(userInput[0].equals("connect")){
						//needs to do checking
						connect(userInput[1], Integer.parseInt(userInput[2]));
					}
					else if(userInput[0].equals("list")){
						//peerList.addAll(pt.getPeerList());
						listPeers();
					}
					else if(userInput[0].equals("terminate")){
						//do checking
						terminate(Integer.parseInt(userInput[1]));
					}
					else if(userInput[0].equals("send")){
						//do checking
						send(Integer.parseInt(userInput[1]), userInput[2]);
					}
					else if(userInput[0].equals("exit")){
						exit();
					}
					else{
						System.out.println("Invalid command or parameters, type in 'help' for details");
					}
				}
			}
		}
		
		System.out.println("Messenger shutting down...");
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
		baseScanner = new Scanner(System.in);
		while(baseScanner.hasNextLine()){
			String userInput = baseScanner.nextLine();
			String[] userArgs = userInput.split("\\s+");
			
			if (userArgs.length == 2){
				if (userArgs[0].equals("./chat")){
					try{
						userPort = Integer.parseInt(userArgs[1]);
						if (userPort >= 1000 && userPort <= 65536){
							//baseScanner.close();
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
	public static void showHelp() {
		
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
	public static int getMyPortNumber() {
		return PORTNUMBER;
	}
	
	// REQUIEMENT # 4: connect <destination> <port no>
	public static void connect(String destinationIP, int portNumber) {
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
	public static void terminate(int connectionID) {
		for ( Peer peer : peerList) {
			if (peer.getId() == connectionID) {
				peer.terminate();
				return;
			}
		}
	}
	
	// REQUIEMENT # 7: send <connection id.> <message>
	public static void send(int connectionID, String message) {
		for ( Peer peer : peerList) {
			if (peer.getId() == connectionID) {
				peer.sendMessage(message);
				return;
			}
		}
	}
	
	// REQUIEMENT # 8: exit
	public static void exit() {
		for ( Peer peer : peerList) {
			peer.terminate();
			terminated = true;
		}
	}
	
	// ==============  HELPER FUNCTIONS  ==============
	
}
