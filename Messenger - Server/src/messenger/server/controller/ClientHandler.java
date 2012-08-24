package messenger.server.controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import messenger.server.Connection;


/**
 * After a client connection is received by the server,
 * it creates a new thread with a <code>ClientHandler.</code>
 * It is responsible for conducting client-to-client connection.<p>
 * 
 * <strong>Date:</strong> <i>17 June 2012</i>
 * @author Rafi
 *
 */
public class ClientHandler extends Connection implements Runnable
{ 
	InfoProvider infoProvider;
	
	private int clientID;
	private int portNumber;
	
	public ClientHandler(int clientID) {
		this.clientID = clientID;
	}
	
	public void run() {
		waitForClient();
		processConnection();
		closeConnection();
	}
	
	/**
	 * Sets up e new connection with a different and unique port number.
	 * 
	 * @return The port number on which the connection is being made.
	 */
	public int setUpConnection() {
		portNumber = ++Server.portNumber;
		return super.setUpConnection(portNumber);
	}
	
	public void startInfoProvider() {
		infoProvider = new InfoProvider(++Server.portNumber);
		Thread infoThread = new Thread(infoProvider);
		infoThread.start();
	}
	
	/**
	 * Receives input data from from the client.
	 * If the client is connected to another client (<i>friend</i>),
	 * send this data to that client. Otherwise reports error.
	 */
	@Override
	protected void processConnection() {
		
		while(true) {
			
			try {
				int receiverID = (Integer) input.readObject();
				String message = (String) input.readObject();
				
				ClientHandler recieverHandler = Server.clientConnections.get(receiverID);
				if(recieverHandler == null) {
					sendData(ERROR_CODE);
					sendData(receiverID + " is not available");
				}
				else {
					recieverHandler.sendData(clientID);
					recieverHandler.sendData(message);
				}
				
				System.out.println(message);
			}
			catch(ClassNotFoundException classNotFoundException) {
				System.err.println("Unknown object type recieved.");
			}
			catch(IOException ioException) {
				System.err.println("Client terminated connection.\n");
				closeConnection();
				break;
			}
		}
	}
	
	@Override
	public void closeConnection() {
		super.closeConnection();
		
		System.out.println("Connection terminated for client " + clientID + "\n");
		infoProvider.closeConnection();
		Server.clientConnections.remove(clientID);
		Server.updateLists();
	}
}
