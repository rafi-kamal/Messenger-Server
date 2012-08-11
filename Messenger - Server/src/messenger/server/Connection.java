package messenger.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import messenger.Constants;

public abstract class Connection implements Constants {
	protected ObjectOutputStream output;
	protected ObjectInputStream input;
	protected Socket connection;
	protected ServerSocket server;

	
	/**
	 * Sets up connection. Return the port number if connection is successful. 
	 * Otherwise reports error. 
	 */
	public int setUpConnection(int portNumber)
	{
		try {
			server = new ServerSocket(portNumber);
		} 
		catch(IOException ioException) {			
			System.err.println("Error setting up server connection.");
			return CONNECTION_ERROR;
		}
		return portNumber;
	}

	/**
	 * Wait for the clients, set up input and output streams
	 */
	protected void waitForClient()
	{
		try
		{
			connection = server.accept();
			System.out.println("Connection established to port " + connection.getLocalPort() + " with " +
					connection.getInetAddress() + ":" + connection.getPort());

			output = new ObjectOutputStream(connection.getOutputStream());
			output.flush();
			input = new ObjectInputStream(connection.getInputStream());
		}
		catch(IOException ioException)
		{
			System.err.println("Error getting streams");
			closeConnection();
			waitForClient();
		}
	}
	
	/**
	 * Closes the connection and streams.
	 */
	public void closeConnection()
	{
		if(connection != null)
			return;
		System.out.println("Closing connection of port " + connection.getLocalPort() + " connected to "
				+ connection.getInetAddress() + ":" + connection.getLocalPort());
		try
		{
			connection.close();
			if(input != null) input.close();
			if(output != null) output.close();
		}
		catch(IOException ioException)
		{
			System.err.println("Error closing connection");
		}
	}
	


	/**
	 * Called when the user is ready to send his message
	 * @param message Message to be sent to the server
	 */
	public void sendData(Object message)
	{
		try
		{
			output.writeObject(message);
			output.flush();
		}
		catch(IOException ioException)
		{
			System.err.println("Error sending data. Please try again");
			ioException.printStackTrace();
		}
	}
	
	abstract protected void processConnection();
}