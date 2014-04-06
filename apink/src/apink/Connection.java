package apink;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.Socket;

public class Connection{
	private Reader clientIn;
	private Reader serverIn;
	private Writer clientOut;
	private Writer serverOut;
	
	private Thread clientToServer;
	private Thread serverToClient;
	
	public Connection(Socket server, String serverCharset, Socket client, String clientCharset) throws UnsupportedEncodingException, IOException
	{
		clientIn = new InputStreamReader(client.getInputStream(), clientCharset);
		clientOut = new OutputStreamWriter(client.getOutputStream(), clientCharset);
		serverIn = new InputStreamReader(server.getInputStream(), serverCharset);
		serverOut = new OutputStreamWriter(server.getOutputStream(), serverCharset);
	}
	
	
	public synchronized void start()
	{
		clientToServer = new Pipe(clientIn, serverOut);
		serverToClient = new Pipe(serverIn, clientOut);
		
		clientToServer.start();
		serverToClient.start();
		
		try
		{
			clientToServer.join();
			serverToClient.join();
		}
		catch(InterruptedException e)
		{
			
		}
	}

	public synchronized void stop()
	{
		clientToServer.interrupt();
		serverToClient.interrupt();
		try
		{
			clientToServer.join();
			serverToClient.join();
		}
		catch(InterruptedException e)
		{

		}
	}
	
	private class Pipe extends Thread
	{
		private Reader in;
		private Writer out;
		
		private char[] buffer;
		public Pipe(Reader in, Writer out)
		{
			this(in, out, 4096);
		}
		public Pipe(Reader in, Writer out, int bufferSize)
		{
			this.in = in;
			this.out = out;
			this.buffer = new char[bufferSize];
		}
		
		public void run()
		{
			while(!this.isInterrupted())
			{
				try
				{
					int readLen = in.read(buffer);
					if(readLen < 0)
						break;
					out.write(buffer, 0, readLen);
					out.flush();
				}
				catch(IOException e)
				{
					break;
				}
			}
			
			try
			{
				in.close();
				out.close();
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}
	
}
