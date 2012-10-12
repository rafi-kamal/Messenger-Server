package messenger.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import messenger.Constants;

/** 
 * Base class for server connection. Any class in the server project which will be connected to
 * any client will inherit this class.
 */
public abstract class ServerConnection implements Constants {
	protected ObjectOutputStream output;
	protected ObjectInputStream input;
	protected Socket connection;
	protected ServerSocket server;

	/**
	 * Sets up the connection to a client. Return the port number if connection is successful. 
	 * Otherwise reports error. 
	 */
	public int setUpConnection(int portNumber) {
		try {
			server = new ServerSocket(portNumber);
		} 
		catch(IOException ioException) {			
			System.err.println("Error setting up server connection.");
			return CONNECTION_ERROR;
		}
		return portNumber;
	}

	/** Wait for clients, set up input and output streams */
	protected void waitForClient() {
		try {
			connection = server.accept();

			output = new ObjectOutputStream(connection.getOutputStream());
			output.flush();
			input = new ObjectInputStream(connection.getInputStream());
		}
		catch(IOException ioException) {
			System.err.println("Error getting streams");
			closeConnection();
		}
	}
	
	/** Closes the connection and streams */
	public void closeConnection()
	{
		if(connection == null)
			return;
		try {
			connection.close();
			if(input != null) input.close();
			if(output != null) output.close();
		}
		catch(IOException ioException) {
			System.err.println("Error closing connection");
		}
	}
	


	/** Sends data to the client */
	public void sendData(Object message) {
		try {
			output.writeObject(message);
			output.flush();
		}
		catch(IOException ioException) {
			System.err.println("Error sending data. Please try again");
			ioException.printStackTrace();
		}
	}
	
	/** Processes the input stream */
	abstract protected void processConnection();
}
