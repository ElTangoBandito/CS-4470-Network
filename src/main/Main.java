package main;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Main {
	//private static int userPort;
	public static void main(String[] args){
		
		/*
		Scanner baseScanner = new Scanner(System.in);
		String userInput = baseScanner.next();
		userPort = Integer.parseInt(userInput);	
		*/
		
		try {
			ServerSocket socket = new ServerSocket(4470);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
