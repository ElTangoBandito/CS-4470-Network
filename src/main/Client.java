import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client {
	private static int PORTNUMBER = 4470;

	public static void main(String[] args) throws IOException {

		String MacPro = "192.168.1.30";
		Socket s = new Socket(MacPro, PORTNUMBER);
		BufferedReader reader = new BufferedReader(new InputStreamReader(s
				.getInputStream()));
		new chat(s).start();
		// The client is always in the state of receiving messages.
		while (true) {
			String response = reader.readLine();
			System.out.println("Message received from " + MacPro);
			System.out.println("Sender's Port :  <The port no." + PORTNUMBER
					+ " of the sender>");
			System.out.println("Message:  " + "<\"" + response + "\">");
			System.out.println();

		}

	}
}
