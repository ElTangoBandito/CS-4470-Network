package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client {
	private static int PORTNUMBER = 4470;

	public static void main(String[] args) throws IOException{
		
		String MacPro = "192.168.1.21";
        Socket s = new Socket(MacPro, PORTNUMBER);
        BufferedReader reader =
            new BufferedReader(new InputStreamReader(s.getInputStream()));
        String response = reader.readLine();
        System.out.println(response);
		
	}

}
