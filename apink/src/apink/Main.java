package apink;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

	public static void main(String[] args) throws IOException {
		String serverAddr = "holywar.hanirc.org";
		int serverPort = 6669;
		int clientPort = 16669;
		
		ServerSocket sock = new ServerSocket(clientPort);
		
		try
		{
			while(true)
			{
				Socket client = sock.accept();
				if(client == null)
					break;
				Socket server = new Socket(serverAddr, serverPort);
				
				Connection connection = new Connection(server, "EUC-KR", client, "UTF-8");
				connection.start();
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		sock.close();
	}

}
