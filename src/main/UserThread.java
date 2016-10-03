package main;

import java.io.IOException;
import java.util.Scanner;

public class UserThread extends Thread{

	String[] userArgs;
	boolean newInput;
	Scanner baseScanner;
	String userInput;
	public UserThread(){
		newInput = false;
		baseScanner = new Scanner(System.in);
	}
	
	public void run(){
		while(true){
			userInput = baseScanner.hasNext()? baseScanner.nextLine():"nothing";
			if (userInput.equals("nothing")){
				continue;
			}
			else{
				System.out.println(userInput);
			}
			try{
				String[] userArgs = userInput.split("\\s+");
				newInput = true;
			} catch(Exception e){
				//e.printStackTrace();
			}
		}
	}
	
	public String[] getUserInput(){
		if (newInput){
			newInput = false;
			return userArgs;
		}
		return null;
	}
}
