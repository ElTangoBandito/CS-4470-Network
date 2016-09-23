package main;
import java.lang.*;
import java.io.*;
import java.net.*;
import java.util.Scanner;


public class Client {

	public static int PORT_NUMBER = 4470;
	
    public static void main(String[] args) throws IOException {
    	Scanner input = new Scanner(System.in);
    	System.out.println("Enter Server IP Address.");
    	String serverIP = input.next();
        Socket s = new Socket(serverIP, PORT_NUMBER);
        BufferedReader reader =
            new BufferedReader(new InputStreamReader(s.getInputStream()));
        String response = reader.readLine();
        System.out.println(response);
        
        System.exit(0);
    }

}
