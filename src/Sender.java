// Sender
import java.io.*;
import java.net.*;

public class Sender {
       public static void main(String args[]) throws Exception {
            DatagramSocket clientSocket = new DatagramSocket();
            InetAddress addr = InetAddress.getByName("172.20.10.7");
            byte[] sendData = new byte[1024];
            String sentence = "A MESSAGE from lihao";
            sendData = sentence.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, addr, 9876);
            clientSocket.send(sendPacket);
            clientSocket.close();
        }
}