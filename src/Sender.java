// Sender
import java.io.*;
import java.net.*;

public class Sender {
       public static void main(String args[]) throws Exception {
            DatagramSocket clientSocket = new DatagramSocket();
            byte[] ipAddr = new byte[]{127, 0, 0, 1};
            InetAddress IPAddress = InetAddress.getByAddress(ipAddr);
            byte[] sendData = new byte[1024];
            String sentence = "A STRING MESSAGE";
            sendData = sentence.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
            clientSocket.send(sendPacket);
            clientSocket.close();
        }
}