package main;
import java.lang.*;
import java.io.*;
import java.net.*;

import com.sun.org.apache.bcel.internal.generic.RETURN;

public class Main {
	public static void main(String[] args){
		
		
		System.out.println("IP:" + getMyIP());
		System.out.println("Hostname:" + getMyHostname());

	}
	
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
	
	public static String getMyHostname() {
		String hostname;
		try {
			hostname = Inet4Address.getLocalHost().getHostName();
			return hostname;
		} catch (Exception e) {
			System.out.println("Error:" + e.toString());
		}
		return "";
	}
}
