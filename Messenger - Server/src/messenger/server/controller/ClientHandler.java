package messenger.server.controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.List;

import messenger.server.ServerConnection;
import messenger.server.model.ClientData;


/**
 * After a client connection is received by the server,
 * it creates a new thread with a <code>ClientHandler.</code>
 * It is responsible for conducting client-to-client connection.<p>
 * 
 * <strong>Date:</strong> <i>17 June 2012</i>
 * @author Rafi
 *
 */
public class ClientHandler extends ServerConnection implements Runnable
{ 
	InfoProvider infoProvider;
	
	private int clientID;
	private int portNumber;
	private ClientData clientData;
	private Server server;
	private Socket connection;
	
	public ClientHandler(Server server, int clientID, Socket connection,
			ObjectOutputStream output, ObjectInputStream input, InfoProvider infoProvider) {
		this.server = server;
		this.clientID = clientID;
		this.connection = connection;
		this.input = input;
		this.output = output;
		this.infoProvider = infoProvider;
		
		try {
			clientData = new ClientData();
			clientData.fetchClientData(clientID);
		} catch(SQLException exception) {
			server.serverGUI.showMessage("Client Data not found");
		} catch(ArrayIndexOutOfBoundsException exception) {
			server.serverGUI.showMessage("Client Data not found");
		}
	}
	
	public void run() {

		Thread infoThread = new Thread(infoProvider);
		infoThread.start();
		processConnection();
		closeConnection();
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
					sendData("Not available");
				}
				else {
					recieverHandler.sendData(clientID);
					recieverHandler.sendData(message);
				}
			}
			catch(ClassNotFoundException classNotFoundException) {
				server.serverGUI.showMessage("Unknown object type recieved.");
			}
			catch(IOException ioException) {
				server.serverGUI.showMessage("Client terminated connection.\n");
				closeConnection();
				break;
			}
		}
	}
	
	@Override
	public void closeConnection() {
		super.closeConnection();
		
		server.serverGUI.showMessage("Connection terminated for client " + clientID + "\n");
		infoProvider.closeConnection();
		Server.clientConnections.remove(clientID);
		server.updateLists();
	}
	
	public String getClientName() {
		if(clientData != null)	
			return clientData.fullName;
		else 
			return "";
	}
	
	public String getIP() {
		return connection.getInetAddress() + "";
	}
	
	public int getPort() {
		return connection.getPort();
	}
}
