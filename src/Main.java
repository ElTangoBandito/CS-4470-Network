import java.io.*;
import java.net.*;
import java.util.*;


public class Main {
	private static int PORTNUMBER;
	private static boolean terminated;
	private static Scanner baseScanner = new Scanner(System.in);
	private static List<Peer> peerList = new ArrayList<>();

	public static void main(String[] args) throws IOException{
		PORTNUMBER = getPort();
		System.out.println("Messenger online.");
		Server serverThread = new Server(PORTNUMBER);
		serverThread.start();

		terminated = false;
		while(!terminated){
			if (baseScanner.hasNext()){
				String userArgs = baseScanner.nextLine();
				String[] userInput = userArgs.split("\\s+");
				if (userInput != null){
					if (userInput[0].equals("help")){
						showHelp();
					}
					else if(userInput[0].equals("myip")){
						System.out.println(getMyIP());
					}
					else if(userInput[0].equals("myport")){
						System.out.println(getMyPortNumber());
					}
					else if(userInput[0].equals("connect")){
						if (userInput.length == 3){
							if (checkInt(userInput[2])){
								connect(userInput[1], Integer.parseInt(userInput[2]));
							}
						}
					}
					else if(userInput[0].equals("list")){
						listPeers();
					}
					else if(userInput[0].equals("terminate")){
						if (userInput.length == 2){
							if (checkInt(userInput[1])){
								terminate(Integer.parseInt(userInput[1]));
							}
						}
					}
					else if(userInput[0].equals("send")){
						if (checkInt(userInput[1])){
							String message = "";
							for (int i = 2; i < userInput.length; i++){
								message += userInput[i];
								message += " ";
							}
							send(Integer.parseInt(userInput[1]), message);
						}
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
	}
	
	public static boolean checkInt(String input){
		boolean result = false;
		try {
			Integer.parseInt(input);
			
			result = true;
			} catch (NumberFormatException e){
				
			}
		return result;
		
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
		try (BufferedReader br = new BufferedReader(new FileReader("help.txt")))
		{
			String helpline;
			while ((helpline = br.readLine()) != null) {
				System.out.println(helpline);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
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
			Socket socket = new Socket(destinationIP, portNumber);
			Peer peer = new Peer(socket, portNumber);
			peer.sendMessage("You have connected to " + socket.getLocalAddress().toString());
			System.out.println("You have connected to " + destinationIP);
			peerList.add(peer);
		} catch (Exception e) {
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
		for (int i = 0; i < peerList.size(); i++) {
			if (peerList.get(i).getId() == connectionID) {
				peerList.get(i).terminate();
				peerList.remove(i);
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
		}
		terminated = true;
	}
}


class Peer extends Object {
	private static int connectedByCounter = 1;
	private static int connectedToCounter = 1;
	Socket socket;
	int id;
	int port;

	public Peer(Socket socket) {
		this.id = connectedByCounter++;
		this.socket = socket;
		this.port = socket.getPort();
	}

	public Peer(Socket socket, int portNumber) {
		this.id = connectedToCounter++;
		this.socket = socket;
		this.port = portNumber;
	}

	public int getId() {
		return id;
	}

	@Override
	public String toString() {
		String ip = socket.getInetAddress().getHostAddress();
		return id + ": " + ip + "    " + port;
	}

	public void terminate() {
		try {
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
		if (socket.isOutputShutdown()) {
			return;
		}
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String response;
	        while ((response = reader.readLine()) != null)
	        {
	        	System.out.println(response);
	        }
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

class Server extends Thread{
	List<Peer> listeningList;
	ServerSocket listener;

	Server(int listeningPortNumber){
		try {
			listener = new ServerSocket(listeningPortNumber);
		} catch (IOException e) {
			e.printStackTrace();
		}
		listeningList = new ArrayList<Peer>();
	}

	public void run(){
		try {
			while(true){
				// Accepting new connections
				Socket connection = listener.accept();
                listeningList.add(new Peer(connection));
                // Reading and printing message from connected peers
                for (int i = 0; i < listeningList.size(); i++) {
                	listeningList.get(i).printMessage();
                }
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
