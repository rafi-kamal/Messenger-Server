package messenger.server.controller;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import messenger.Constants;
import messenger.server.model.ClientLogger;
import messenger.server.view.ServerGUI;


/**
 * Main server class. It has a specific address and port number on which
 * it waits for the clients. After a client connection is being received, 
 * it creates a separate thread for that client. A <code>ClientHandler</code> object
 * then serves for that client. <code>Server</code> again waits for the clients.<p>
 * 
 * <b>Date:</b> <i>16 June 2012</i>
 * @author Rafi
 *
 */
public class Server implements Constants
{	
	/** Maintains a <code>ClientID<code>-{@link ClientHandler} list for all online clients. */
	public static Map<Integer, ClientHandler> clientConnections = new ConcurrentHashMap<Integer, ClientHandler>();
	static int portNumber = 5555;
	private static int infoPortNumber = 5556;
	private ExecutorService threadExecutor = Executors.newCachedThreadPool();
	private ClientLogger clientLogger;
	ServerGUI serverGUI;

	private ObjectOutputStream output;
	private ObjectInputStream input;
	private Socket connection;
	private ServerSocket server;
	
	private ServerSocket infoServer;
	private Socket infoProviderConnection;
	private InfoProvider infoProvider;
	
	public Server() {
		clientLogger = new ClientLogger();
		serverGUI = new ServerGUI();
		serverGUI.setVisible(true);
	}

	public ServerSocket setUpConnection(int portNumber) {
		try {
			return new ServerSocket(portNumber);
		}
		catch(IOException exception) {
			System.err.println("Error setting up server connection");
			System.exit(1);
			return null;
		}
	}
	
	/**
	 * Run the server, wait for the client, process the connection,
	 * close the connection and then wait for the client again.
	 */
	private void run() {
		serverGUI.showMessage("Server has set-up.\n");	
		
		server = setUpConnection(portNumber);
		infoServer = setUpConnection(infoPortNumber);
		while(true) {
			waitForClient();
			processConnection();
		}
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
	
	/**
	 * Processes the log in and sign up requests received from the clients. 
	 * Gets the client ID and creates a new <code>ClientHandler</code> on successful login.
	 * Further processing is done by the <code>ClientHandler</code>.
	 * Assigns a new port to the <code>ClientHandler</code>
	 * by its <code>setUpConnection</code> method where the new client will communicate.
	 * Sends this client ID and port number to the client.
	 * Client then reconnects to the <code>ClientHandler</code> with that port number.
	 * Server puts a new entry (<code>clientID</code>, <code>ClientHandler</code>)
	 * in the <code>clientConnections</code>.
	 */
	protected void processConnection() {
		
		boolean loginSuccessful = false;
		try {
			int messageCode = (Integer) input.readObject();
			String userName = (String) input.readObject();
			String password = (String) input.readObject();
			
			switch(messageCode) {				
			case LOGIN_REQUEST:
				serverGUI.showMessage("Login request found from user " + userName +
						"@" + connection.getInetAddress() + ":" + connection.getPort());
				final int clientID = clientLogger.logIn(userName, password);
				infoProvider = null;
				if(clientID != LOGIN_FAILED) {
					loginSuccessful = true;
					Thread infoThread = new Thread(new Runnable() {
						
						@Override
						public void run() {
							try {
								infoProvider = startInfoProvider(clientID);
							} catch (IOException e) {
								serverGUI.showMessage("Error starting infoprovider");
							}
						}
					});
					infoThread.start();
					sendData(LOGIN_SUCCESSFUL);
					while(infoProvider == null) {};
					ClientHandler newClient = 
							new ClientHandler(this, clientID, connection, output, input, infoProvider);
					threadExecutor.execute(newClient);
					
					clientConnections.put(clientID, newClient);
					sendData(clientID);
					sendData(newClient.getClientName());
				}
				else
					sendData(LOGIN_FAILED);
				break;
			case SIGNUP_REQUEST:
				serverGUI.showMessage("Signup request found from user " + userName +
						"@" + connection.getInetAddress() + ":" + connection.getPort());
				boolean isSuccessful = clientLogger.signUp(userName, password);
				if(isSuccessful)
					sendData(SIGNUP_SUCCESSFUL);
				else
					sendData(SIGNUP_FAILED);
				break;
			}
		}
		catch(ClassNotFoundException classNotFoundException) {
			serverGUI.showMessage("Unknown object type recieved");
		}
		catch(IOException ioException) {
			serverGUI.showMessage("Error with input stream");
		}
		
		if(!loginSuccessful)
			processConnection();
	}
	

	/** Closes the connection and streams */
	public void closeConnection()
	{
		if(connection == null)
			return;
		try {
			connection.close();
			if(infoProviderConnection != null) infoProviderConnection.close();
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
	
	private InfoProvider startInfoProvider(int clientID) throws IOException {
		infoProviderConnection = infoServer.accept();
		InfoProvider infoProvider = new InfoProvider(this, infoProviderConnection, clientID);
		return infoProvider;
	}
	
	/** When a new client logs in or logs out, sends the new online user list to every logged in client. */ 
	synchronized public void updateLists() {
		Map<Integer, String> allUsers = getAllOnlineUsers();
		serverGUI.setListValues(allUsers);
		Set<Integer> clients = clientConnections.keySet();
		for(int clientID : clients) {
			InfoProvider infoProvider = clientConnections.get(clientID).infoProvider;
			
			try {
				infoProvider.sendAllUserList();
				serverGUI.showMessage("Userlist sent to client " + clientID);
			} 
			catch (IOException exception) {
				serverGUI.showMessage("Error sending userlist to client " + clientID);
			}
		}
	}
	
	/** Gets the list of all online users. */
	public static Map<Integer, String> getAllOnlineUsers() {
		Set<Integer> clientIDs = Server.clientConnections.keySet();
		Map<Integer, String> clients = new HashMap<Integer, String>();
		
		for(Integer client : clientIDs) {
			String clientName = Server.clientConnections.get(client).getClientName();
			clients.put(client, clientName);
		}
		System.out.println(clientIDs);
		System.out.println(clients);
		return clients;
	}
	
	
	public static void main(String args[]) {
		Server server = new Server();
		server.run();
	}
}
