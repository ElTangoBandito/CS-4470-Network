import java.io.*;
import java.net.*;
import java.util.*;
import java.util.Map.Entry;


public class Main {
	private static int PORTNUMBER;
	private static boolean terminated;
	private static Scanner baseScanner = new Scanner(System.in);
	private static List<Peer> peerList = new ArrayList<>();

	//Project 2 variables from parsing
	private static int delay;
	private static int numberOfServers;
	private static int numerOfEdges;
	private static Map<Integer, List<String>> connectionsMap = new HashMap<Integer, List<String>>();
	private static int[][] vectorTable = new int[5][5];

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
					else if(userInput[0].equals("server")){
						if(userInput.length == 5){
							if(userInput[1].equals("-t") && userInput[3].equals("-i")){
								delay = Integer.parseInt(userInput[4]);
								parseTopology(userInput[2]);
								/* Testing prints
								System.out.println("Delay :" + Integer.toString(delay));
								System.out.println("Servers :" + Integer.toString(numberOfServers));
								System.out.println("Edges :" + Integer.toString(numerOfEdges));
								System.out.println(connectionsMap);
								System.out.println("Table costs :");
								System.out.println(vectorTable[1][2]);
								System.out.println(vectorTable[1][3]);
								System.out.println(vectorTable[1][4]);
								*/
							}
						}
						//server -t <topology-file-name> -i <routing-update-interval>
					}
					else if(userInput[0].equals("update")){
						updateVector(userInput);
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

	//Initialize 2D array
	public static void initializeVectorTable(){
		for(int[] row: vectorTable){
			Arrays.fill(row, Integer.MAX_VALUE);
		}
	}

	public static void calculatePath(int from, int to) {
		// just for testing 
		vectorTable = new int[][] {
			{0, 0, 0, 0, 0},
			{0, 0, 7, 4, 5},
			{0, 7, 0, 2, -1},
			{0, 4, 2, 0, 1},
			{0, 5, -1, 1, 0}
		};
		// path finding
		if (vectorTable.length < 1) { return; }
		boolean visited[] = new boolean[vectorTable[0].length];
		int at = from;
		visited[0] = true;
		HashMap<Integer, Integer> idDistance = new HashMap<>();
		HashMap<Integer, Integer> pathRecord = new HashMap<>();
		int distanceToHere = 0;
		while (at != to) {
			visited[at] = true;
			// go to each airport
			for(int i = 1; i < vectorTable[at].length; i++) {
				if (vectorTable[at][i] > 0 && !visited[i]) {
					if (!idDistance.containsKey(i) || idDistance.get(i) > distanceToHere+vectorTable[at][i]) {
						idDistance.put(i, distanceToHere+vectorTable[at][i]);
						pathRecord.put(i, at);
					}
				}
			}
			// find min
			int minId = 0, minDist = Integer.MAX_VALUE;
			for (Entry<Integer, Integer> entry: idDistance.entrySet()) {
				if (!visited[entry.getKey()] && entry.getValue() < minDist) {
					minId = entry.getKey();
					minDist = entry.getValue();
				}
			}
			// set at to min
			distanceToHere = minDist;
			at = minId;
			visited[at] = true;
		}
		// show path
		List<Integer> path = new ArrayList<>();
		while (at != from) {
			path.add(at);
			at = pathRecord.get(at);
		}
		Collections.reverse(path);
		for (int id: path) {
			System.out.println(id);
		}
		System.out.println("Distance: " + distanceToHere);
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

	//Parsing Topology
	public static void parseTopology(String filePath){
		int counter = 0;
		try (BufferedReader br = new BufferedReader(new FileReader(filePath)))
		{
			String nextLine;
			while ((nextLine = br.readLine()) != null) {
				if(counter == 0){
					numberOfServers = Integer.parseInt(nextLine);
				}
				else if(counter == 1){
					numerOfEdges = Integer.parseInt(nextLine);
				}
				else if(counter > 1 && counter < 6){
					String[] lineArgs = nextLine.split("\\s+");
					List<String> ipPort = new ArrayList<String>();
					ipPort.add(lineArgs[1]);
					ipPort.add(lineArgs[2]);
					connectionsMap.put(Integer.parseInt(lineArgs[0]), ipPort);
				}
				else{
					String[] args = nextLine.split("\\s+");
					int from = Integer.parseInt(args[0]);
					int to = Integer.parseInt(args[1]);
					int cost = Integer.parseInt(args[2]);
					vectorTable[from][to] = cost;
				}
				counter++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	//Update Vector
	public static void updateVector(String[] userInput){
		if(userInput.length == 4){
			try{
				int from = Integer.parseInt(userInput[1]);
				int to = Integer.parseInt(userInput[2]);
				int cost = Integer.MAX_VALUE;
				if(!userInput[3].equals("inf")){
					try{
						cost = Integer.parseInt(userInput[3]);
					} catch(NumberFormatException e){
						System.out.println("Invalid Cost");
					}
				}
				if(from > 0 && from < 5 && to > 0 && from < 5 && cost > 0){
					vectorTable[from][to] = cost;
				}
				else{
					System.out.println("Invalid parameters");
				}
			} catch (NumberFormatException e){
				System.out.println("Invalid server IDs");
			}
		}
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
	        	System.out.println("Message received from " + socket.getInetAddress());
				System.out.println("Sender's Port :  <The port no." + port
						+ " of the sender>");
				System.out.println("Message:  " + "<\"" + response + "\">");
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
				Client client = new Client(new Peer(connection));
				client.start();
				
//                listeningList.add(new Peer(connection));
//                // Reading and printing message from connected peers
//                for (int i = 0; i < listeningList.size(); i++) {
//                	listeningList.get(i).printMessage();
//                }
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

class Client extends Thread {
	private Peer peer;

	Client(Peer peer){
		this.peer = peer;
	}

	public void run(){
		peer.printMessage();
	}
}