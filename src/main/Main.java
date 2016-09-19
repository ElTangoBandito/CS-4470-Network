package main;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Main {
	
	public static void main(String[] args){
		try {
			ServerSocket socket = new ServerSocket(getPort());
			System.out.println(socket.getLocalPort());
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static int getPort(){
		int userPort = 0;
		while(true){
			Scanner baseScanner = new Scanner(System.in);
			String userInput = baseScanner.nextLine();
			String[] userArgs = userInput.split("\\s+");
			
			if (userArgs.length == 2){
				if (userArgs[0].equals("/chat")){
					try{
						userPort = Integer.parseInt(userArgs[1]);
						if (userPort >= 1000 && userPort <= 65536){
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
	
}
