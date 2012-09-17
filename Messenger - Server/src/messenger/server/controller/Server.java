package messenger.server.controller;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import messenger.server.Connection;
import messenger.server.model.ClientLogger;


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
public class Server extends Connection
{	
	/** Maintains a <code>ClientID<code>-{@link ClientHandler} list for all online clients. */
	static Map<Integer, ClientHandler> clientConnections = new ConcurrentHashMap<Integer, ClientHandler>();
	static int portNumber = 5555;
	private ExecutorService threadExecutor = Executors.newCachedThreadPool();
	private ClientLogger clientLogger;
	
	public Server()
	{
		clientLogger = new ClientLogger();
		setUpConnection(portNumber);
	}

	@Override
	public int setUpConnection(int portNumber) {
		int returnValue = super.setUpConnection(portNumber);
		if(returnValue == CONNECTION_ERROR) {
			System.err.println("\nSystem exiting.");
			System.exit(1);
		}		
		return returnValue;
	}
	
	/**
	 * Run the server, wait for the client, process the connection,
	 * close the connection and then wait for the client again.
	 */
	private void run() {
		System.out.println("Server has set-up.\n");	
		
		while(true) {
			waitForClient();
			processConnection();
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
	@Override
	protected void processConnection() {
		
		try {
			int messageCode = (Integer) input.readObject();
			String userName = (String) input.readObject();
			String password = (String) input.readObject();
			
			switch(messageCode) {				
			case LOGIN_REQUEST:
				System.out.println("Login request found from user " + userName +
						"@" + connection.getInetAddress() + ":" + connection.getPort());
				int clientID = clientLogger.logIn(userName, password);
				
				if(clientID != LOGIN_FAILED) {
					sendData(LOGIN_SUCCESSFUL);
					ClientHandler newClient = new ClientHandler(clientID);
					int newPort = newClient.setUpConnection();
					threadExecutor.execute(newClient);
					
					clientConnections.put(clientID, newClient);
					newClient.startInfoProvider();
					
					sendData(newPort);
					sendData(clientID);
					System.out.println("Reconnectng in port " + newPort);
				}
				else
					sendData(LOGIN_FAILED);
				break;
			case SIGNUP_REQUEST:
				System.out.println("Signup request found from user " + userName +
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
			System.err.println("Unknown object type recieved");
		}
		catch(IOException ioException) {
			System.err.println("Error with input stream");
		}
	}
	
	/** When a new client logs in or logs out, sends the new online user list to every logged in client. */ 
	static synchronized public void updateLists() {
		
		Set<Integer> clients = clientConnections.keySet();
		for(int clientID : clients) {
			InfoProvider infoProvider = clientConnections.get(clientID).infoProvider;
			
			try {
				infoProvider.sendAllUserList();
				System.out.println("Userlist sent to client " + clientID);
			} 
			catch (IOException exception) {
				System.err.println("Error sending userlist to client " + clientID);
			}
		}
	}
	
	public static void main(String args[]) {
		Server server = new Server();
		server.run();
	}
}
