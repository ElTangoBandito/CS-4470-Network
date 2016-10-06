import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

class Peer extends Object {
	Socket socket;
	private static int counter = 1;
	int id;
	String ip;
	int port;
	private static BufferedReader reader;

	public Peer(Socket socket, int port) {
		this.id = counter++;
		this.socket = socket;
		this.port = socket.getPort();
		this.ip = socket.getInetAddress().getHostAddress();
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getId() {
		return id;
	}

	@Override
	public String toString() {
		return id + ": " + ip + "    " + port;
	}

	public void terminate() {
		try {
			// TODO: send termination message to peer
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendMessage(String message) {
		PrintWriter out;
		try {
			out = new PrintWriter(socket.getOutputStream(), true);

			out.println(message);
			System.out.println("Send successfully£¡");
			System.out.println();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void printMessage() {
		try {
			reader = new BufferedReader(new InputStreamReader(socket
					.getInputStream()));
			while (true) {
				String message = reader.readLine();
				System.out.println("Message received from " + ip);
				System.out.println("Sender's Port :  <The port no." + port
						+ " of the sender>");
				System.out.println("Message:  " + "<\"" + message + "\">");
				System.out.println();

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}