package messenger.server.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import messenger.server.Connection;

/** Handle client requests and sends non-message data to the client (e.g. user lists or user info). */
public class InfoProvider extends Connection implements Runnable {

	private int infoPortNumber;
	
	public InfoProvider(int infoPortNumber) {
		this.infoPortNumber = infoPortNumber;
	}
	
	@Override
	public void run() {
		setUpConnection(infoPortNumber);
		waitForClient();
		Server.updateLists();
		processConnection();
	}
	
	@Override
	protected void processConnection() {
		try {
			int request = (Integer) input.readObject();
			
			switch(request) {
			case ALL_LIST:
				sendAllUserList();
				break;
			case FRIEND_LIST:
				sendFriendList();
				break;
			case USER_INFO:
				sendUserInfo();
				break;
			}
		}
		catch(ClassNotFoundException classNotFoundException) {
			System.err.println("Unknown object type recieved");
		}
		catch(IOException ioException) {
			System.err.println("Error sending data");
		}
	}
	
	/** Sends info (name, address e.g.) about a client. */
	private void sendUserInfo() {
		
	}

	/*** Sends the <b>Friend list</b>. */
	private void sendFriendList() {
		
	}
	
	/*** Sends the <b>All User list.</b> */
	public void sendAllUserList() throws IOException {
		
		Map<Integer, String> allOnline = getAllOnlineUsers();
		
		output.writeObject(ALL_LIST);
		output.flush();
		output.writeObject(allOnline);
		output.flush();
		
		System.out.println("All user list sent from " + connection.getLocalPort() + " to " + 
				connection.getInetAddress() + ":" + connection.getPort());
	}
	
	/** Gets the list of all online users. */
	private Map<Integer, String> getAllOnlineUsers() {
		Set<Integer> clientIDs = Server.clientConnections.keySet();
		Map<Integer, String> clients = new HashMap<Integer, String>();
		
		for(Integer client : clientIDs) {
			String clientName = Server.clientConnections.get(client).getClientName();
			clients.put(client, clientName);
		}
		return clients;
	}
}
