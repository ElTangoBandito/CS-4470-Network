package main;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
//To achieve the client to send messages to the server
public class chat extends Thread {
	private Socket s;

	chat(Socket ss) {
		s = ss;
	}

	public void run() {
		PrintWriter out;
		while (true) {
			Scanner input = new Scanner(System.in);
			String message = input.nextLine();
			//Limited number of words
			if (message.length() >= 100) {
				System.out
						.println("The message can not be more than 100  characters!");
			} else {
				try {
					out = new PrintWriter(s.getOutputStream(), true);
					out.println(message);
					System.out.println("Send successfully£¡");
					System.out.println();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}