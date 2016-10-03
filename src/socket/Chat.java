package socket;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Chat {

	public static void main(final String args[]) throws IOException { 
		final Selector selector = Selector.open();
		Map<String, ArrayList<String>> peers = new HashMap<String, ArrayList<String>>();
		    Socket send_socket = null; 
			
			String input =null;
			Scanner sc=new Scanner(System.in);  
			
		if (args.length != 1) {
            System.err.println(
                "Usage: <port number>");
            System.exit(1);
        }
		else{//start the listening thread
			new Thread(new Runnable() {
                public void run() {
                	try {
                		ServerSocketChannel channel = ServerSocketChannel.open();
            			channel.configureBlocking(false);
            			channel.bind(new InetSocketAddress(Integer.parseInt(args[0])));
//            			System.out.println("Listening to connections...\n");
                		while(true){
                			
                			SocketChannel inChannel = channel.accept();
                			if(inChannel !=null){

                				inChannel.configureBlocking(false);
                				inChannel.register(selector, SelectionKey.OP_READ);
                				String [] peerInfo = inChannel.getRemoteAddress().toString().split(":");
                				System.out.println(peerInfo[0].substring(1, peerInfo[0].length()) + " connected\n");
                			}
                		}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                   }
                }).start();
            	
			new Thread(new Runnable(){
				public void run(){
					try {
//						System.out.println("Waiting for messages...");
						while(true){
						int channelReady = selector.selectNow();
						if(channelReady !=0){
//						System.out.println(channelReady + " Channel(s) ready to be read!!!!!!!!!!!!!!!!!!");
						Set<SelectionKey> readyKeys = selector.selectedKeys();
						Iterator<SelectionKey> selectedKeysIterator = readyKeys.iterator();
						ByteBuffer buffer = ByteBuffer.allocate(100);
						
						
						
						while(selectedKeysIterator.hasNext()){
							SelectionKey key = selectedKeysIterator.next();
							SocketChannel channel = (SocketChannel) key.channel();
							int bytesRead = channel.read(buffer);
							
							String [] peerInfo =channel.getRemoteAddress().toString().split(":");
							System.out.print("\nMessage received from " + peerInfo[0].substring(1, peerInfo[0].length()) + "\n"
									+ "Sender’s Port: " + peerInfo[1]
									+ "\nMessage: ");
            				
							
							while (bytesRead != 0) {
								  buffer.flip();  

								  while(buffer.hasRemaining()){
								      System.out.print((char) buffer.get()); 
								  }
								 
								  buffer.clear(); 
								  bytesRead = channel.read(buffer);
								}
						 System.out.print(">\n");
							selectedKeysIterator.remove();
							 
						}
						
						}//end of 1+ channels
					}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}).start();
		}//end of listening threads
		
		
		
		
		
		
		do{
			System.out.println("please enter command: \n");
        input = sc.nextLine();
	
	    	if(input.equals("help")){
	    		System.out.println("myip     display IP address");
	    		System.out.println("myport   Show the port number that the process runs on");
	    		System.out.println("connect  connect to another peer");
	    		System.out.println("send     send messages to peers");
	    		System.out.println("list     List all the connected peers");
	    		System.out.println("terminate     Terminate the connection");
	    		System.out.println("exit     Terminate all the connections and exit the program");
	    		System.out.println("please enter some command:");
	    	}
	    	else if(input.equals("myip")){
	    		System.out.println(send_socket.getLocalSocketAddress());
	    	}
	    	else if(input.equals("myport")){
	    		System.out.println("The program runs on port number "+args[1]);
	    	}
	    	else if(input.startsWith("connect")){ // eg. connect '8.8.8.8' 4545
	    		String[] splited = input.split("\\s");
	    		
	    		int portnum= Integer.parseInt(splited[2]);
	    		String ip =splited[1];
	            send_socket = new Socket(ip, portnum);
	            System.out.println("Connected to client: " + ip + "\n");
	    	}
	    	else if(input.startsWith("send")){
	    	
	    		String userInput = null;
	    		PrintWriter writer =
	                    new PrintWriter(send_socket.getOutputStream(), true);
	    		
	    		String inputParse[] =  input.split("\\s"); 
	            int charsToSkip = inputParse[0].length() + inputParse[1].length() + 2;
	            String ip = inputParse[1];
	            
	            userInput = input.substring(charsToSkip, input.length());
	            
	           if(userInput !=null) {
	                writer.println(userInput);
	                System.out.println("Message sent to  " + ip + ": <" + userInput + ">\n");
	            }
	           else{
	        	   System.out.println("Please enter a non empty string\n");
	           }
	            }
	    	
	    	else if(input.equals("list")){
	    		
	    	}
	    	else if(input.startsWith("terminate")){
	    		String userInput = null;
	    		
	    		String inputParse[] =  input.split("\\s"); 
	            int charsToSkip = inputParse[0].length() + inputParse[1].length() + 2;
	            int id = Integer.parseInt(inputParse[1]);
	            
	            userInput = input.substring(charsToSkip, input.length());
	            if(userInput !=null) {
	            	
	            }
	            else{
	        	   System.out.println("Please enter a non empty string\n");
	            }
	    	}
	    	else if(input.equals("exit")){
	  	      System.exit(1);
	    	}
	    	else{
	    		System.out.println("Your input is incorrect! pl type in again");
	    	}
	    	
	}while(!input.equals("exit"));

}
}


