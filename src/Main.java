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
	private static int myId;
	private static int delay;
	private static int numberOfServers;
	private static int numerOfEdges;
	private static ArrayList<ArrayList<Integer> > paths = new ArrayList<>();

	private static Map<Integer, List<String>> connectionsMap = new HashMap<Integer, List<String>>();
	private static int[][] vectorTable = new int[5][5];
	private static int[][] tempTable = new int[5][5];
	private static int[] originVector = new int[5];

	public static void main(String[] args) throws IOException{
		initializeVectorTable();
		PORTNUMBER = getPort();
		System.out.println("Messenger online.");
		Server serverThread = new Server(PORTNUMBER);
		serverThread.start();
		
		// UDP receiver
		UDPReceiver receiver = new UDPReceiver(vectorTable, PORTNUMBER, delay);

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
					else if (userInput[0].equals("packet")) {
						System.out.println(receiver.getPacketCounter());
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
								for(int i = 1; i < 5; i++){
									originVector[i] = vectorTable[myId][i];
								}
								receiver.setMyId(myId);
								receiver.setOriginVector(originVector);
								receiver.start();
							}
						}
//						printVectorTable(vectorTable);
//						System.out.println("Server command completed");

						// server -t <topology-file-name> -i <routing-update-interval>
						// server -t topology.txt -i 5
					}
					else if(userInput[0].equals("update")){
						updateVector(userInput);
					}
					else if(userInput[0].equals("beginR")){
						sendAll();
					}
					else if(userInput[0].equals("step")){
						while(true){
							try {
								if(compareTables()){
									break;
								}
								System.out.println("Step intiated");
								Thread.sleep(delay * 1000);
								printVectorTable(vectorTable);
								sendAll();
								for(int i = 1; i < 5; i ++){
									if (myId != i){
										vectorTable[myId][i] = calculatePath(myId, i);
									}
								}
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						printPaths();
					}
					else if(userInput[0].equals("disable")){
						if(userInput.length == 2){
							int index = Integer.parseInt(userInput[1]);
							if(index > 0 && index < 5){
								if (index != myId && originVector[index] != 100){
									resetAll();
									originVector[index] = 100;
									for(int j = 0; j < 5; j++){
										vectorTable[myId][j] = originVector[j];
									}
									for (int i = 1; i < 5; i++){
										if (i != myId){
											List<String> ipAndPort = connectionsMap.get(i);
											String ip = ipAndPort.get(0);
											int port = Integer.parseInt(ipAndPort.get(1));
											String message = "disable " + String.valueOf(myId) + " " + String.valueOf(index) + " ";
											try {
												sendMessage(message, ip, port);
											} catch (Exception e) {
												e.printStackTrace();
											}
										}
									}
								}
							}
						}
					}
					else{
						System.out.println("Invalid command or parameters, type in 'help' for details");
					}
				}
			}
		}
		System.out.println("Messenger shutting down...");
	}

	private static final int INF = -1;
	private static final int MIN_LENGTH = 3;
	
	public static void display(){
		display(vectorTable,myId);
	}
	
	public static void display(int [][]array, int myId){
		if(array.length < 1)
			return;
		//Default row name is to i
		String [] rowName = new String[array.length];
		//colunm name is to j
		String [] columnName = new String[array[0].length];
		for (int i = 0; i < rowName.length; i++) {
			rowName[i] = "to " + i;
		}
		for (int i = 0; i < columnName.length; i++) {
			columnName[i] = "via " + i;
		}
		
		display(rowName,columnName,array,myId);
	}
	
	public static void resetAll(){
		initializeVectorTable();
	}
	
	private static void rep(String str, int times) {
		for (int i = 0; i < times; i++) {
			System.out.print(str);
		}
	}
	
	private static void drawLine(int[] length, int type) {
		if (type == 0)
			System.out.print("|");
		else if (type == 1)
			System.out.print("|");
		else if (type == 2)
			System.out.print("|");
		for (int i = 0; i < length.length; i++) {
			rep("─", length[i]);
			if (i == length.length - 1) {
				if (type == 0)
					System.out.print("|");
				else if (type == 1)
					System.out.print("|");
				else if (type == 2)
					System.out.print("|");
			} else {
				if (type == 0)
					System.out.print("|");
				else if (type == 1)
					System.out.print("|");
				else if (type == 2)
					System.out.print("|");
			}
		}
		System.out.println();
	}
	
	private static int maxLength(String[] names){
		int max = 0;
		for (int i = 0; i < names.length; i++) {
			if (max < names[i].length()){
				max = names[i].length();
			}
		}
		return max;
	}

	public static void sendAll(){
		for (int i = 1; i < 5; i++){
			if (i != myId){
				List<String> ipAndPort = connectionsMap.get(i);
				String ip = ipAndPort.get(0);
				int port = Integer.parseInt(ipAndPort.get(1));
				String message = initializeNeighborTable();
				try {
					sendMessage(message, ip, port);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void display(String[] rowName, String[] columnName, int[][] array, int myId) {
	
		//calculate columnName length
		int [] length = new int [columnName.length + 1];
		length[0] = maxLength(rowName);
		
		//calculate length of each row
		for (int i = 0; i < columnName.length; i++) {
			length[i+1] = columnName[i].length();
			for (int j = 0; j < rowName.length; j++) {
				int numberLength = String.valueOf(array[j][i]).length();
				if(numberLength > length[i+1]){
					length[i+1] = numberLength;
				}
			}
		}
		
		//check if less than MIN_LENGTH
		for (int i = 0; i < length.length; i++) {
			if(length[i] < MIN_LENGTH)
				length[i] = MIN_LENGTH;
		}
		
		
		//draw top line
		drawLine(length, 0);
		System.out.print("│");
		rep(" ", length[0]);
		for (int i = 0; i < columnName.length; i++) {
			System.out.print("│");
			System.out.print(columnName[i]);
		}
		System.out.println("│");
		
		
		for (int i = 0; i < rowName.length; i++) {
			
			//draw seperating line
			drawLine(length, 1);
			
			System.out.print("│");
			//draw for rowName
			System.out.print(String.format("%"+length[0]+"s", rowName[i]));
			for (int j = 0; j < columnName.length; j++) {
				System.out.print("│");

				//draw data
				//if the line the same as ID then leave it empty
				if(i == myId || j == myId){
					rep(" ", length[j+1]);
				}else if(array[i][j] == INF){
					//check if the data is infinite, INF definition on top
					System.out.print(String.format("%"+length[j+1]+"s", "INF"));
				}else{
					System.out.print(String.format("%"+length[j+1]+"d", array[i][j]));					
				}
				
			}
			System.out.println("│");
		}
	

		//draw end
		drawLine(length, 2);

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
			Arrays.fill(row, 100);
		}
		
		// TODO: move this to somewhere else
		for (int i = 0; i < 5; i++) {
			paths.add(new ArrayList<>());
		}
	}

	public static int calculatePath(int from, int to) {
		// path finding
//		if (vectorTable.length < 1) { return 0; }
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
		ArrayList<Integer> path = new ArrayList<>();
		while (at != from) {
			path.add(at);
			at = pathRecord.get(at);
		}
		path.add(from);
		Collections.reverse(path);
		paths.set(to, path);
		return distanceToHere;
	}
	
	public static void printPaths() {
		for (int i = 0; i < 5; i++) {
			ArrayList<Integer> path = paths.get(i);
			System.out.println("Path to " + i + ":");
			for (Integer node: path) {
				System.out.print(node + " ");
			}
			System.out.println();
		}
	}
	
	public static void sendMessage(String message, String ip, int port) throws Exception {
        DatagramSocket clientSocket = new DatagramSocket();
        InetAddress addr = InetAddress.getByName(ip);
        byte[] sendData = new byte[1024];
        sendData = message.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, addr, port);
        clientSocket.send(sendPacket);
        clientSocket.close();
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
	
	//Requirement #9 : Step
	public static void sendv(int myip, int myport, String messsage) {
		
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
					myId = from;
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

	// initialize neighbor table 
	  public static String initializeNeighborTable() {
			StringBuilder sb = new StringBuilder();
			int[] row = vectorTable[myId];
			int counter = 0;
				for (int col : row){
					if (counter == 0){
						sb.append(String.valueOf(myId) + " ");
						counter ++;
					}
					else{
						sb.append(col + " ");
					}
				}
			return sb.toString();
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
	
	public static void printVectorTable(int[][] vectorTable) {
		System.out.println("Vector Table:");
		for (int[] arr: vectorTable) {
			for (int distance: arr) {
				if (distance == 100) {
					System.out.print("X ");
				} else {
					System.out.print(distance + " ");
				}
			}
			System.out.println();
		}
		System.out.println("\n==========");
	}
	public static boolean compareTables() {
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j ++) {
				if(tempTable[i][j] != vectorTable[i][j]){
					copyTable();
					return false;
				}
			}
		}
		
		copyTable();
		return true;
	}
	
	public static void copyTable(){
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j ++) {
				tempTable[i][j] = vectorTable[i][j];
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

class UDPReceiver extends Thread {
	private int [][] vectorTable;
	private DatagramSocket serverSocket;
	private int packetCounter;
	private int[] originVector;
	private int myId;
	
	public UDPReceiver(int[][] vectorTable, int port, int delay) throws SocketException {
		this.vectorTable = vectorTable;
		serverSocket = new DatagramSocket(port);
		packetCounter = 0;
	}
	
	public void run() {
        byte[] receiveData = new byte[1024];
        while (true) {
        	System.out.println("udp receiver running");
            try {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				serverSocket.receive(receivePacket);
				
	            String sentence = new String(receivePacket.getData());
	            String[] distances = sentence.split(" ");
	            System.out.println("Sentence:" + distances[0]);
	            if (distances[0].equals("disable")) {
	            	int sender = Integer.valueOf(distances[1]);
	            	int cutOf  = Integer.valueOf(distances[2]);
	            	resetAll(vectorTable);
	            	vectorTable[sender][cutOf] = 100;
	            	for(int i = 1; i < 5; i ++){
	            		vectorTable[myId][i] = originVector[i];
	            	}
	            }
	            else {
					int sender = Integer.valueOf(distances[0]);
					for (int i = 1; i < 5; i++) {
						vectorTable[sender][i] = Integer.valueOf(distances[i]);
					}
					System.out.println("Vector Revceived from: " + sender);
		            // increment packet counter
		            packetCounter++;
	            }
	            
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
	}
	
	public void printVectorTable() {
		System.out.println("Vector Table:");
		for (int[] arr: vectorTable) {
			for (int distance: arr) {
				if (distance == 100) {
					System.out.print("X ");
				} else {
					System.out.print(distance + " ");
				}
			}
			System.out.println();
		}
		System.out.println("\n==========");
	}
	
	public int getPacketCounter() {
		return packetCounter;
	}
	
	public void resetAll(int[][] vectorTable){
		initializeVectorTable(vectorTable);
	}
	public void initializeVectorTable(int[][] vectorTable){
		for(int[] row: vectorTable){
			Arrays.fill(row, 100);
		}
	}
	
	public void setOriginVector(int[] vectorIn){
		this.originVector = vectorIn;
	}
	
	public void setMyId(int myId){
		this.myId = myId;
	}
}
