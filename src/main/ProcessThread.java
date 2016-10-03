package main;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class ProcessThread extends Thread{
	Socket connection;
	//int count;
	ProcessThread(Socket connection){
		this.connection = connection;
	}
	/*
	ProcessThread(int i){
		count = i;
	}*/
	
	public void run(){
		try {
			while(true){
				DataInputStream packet = new DataInputStream(connection.getInputStream());
				System.out.println(packet.readUTF());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		/*
		for(int i = 0; i < count; i++){
			System.out.println(i);
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		*/
	}
}
