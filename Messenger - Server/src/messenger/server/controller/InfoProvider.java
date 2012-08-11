package messenger.server.controller;

import java.io.IOException;
import java.util.Set;

import messenger.server.Connection;

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
		try
		{
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
	
	private void sendUserInfo() {
		
	}

	private void sendFriendList() {
		
	}

	public void sendAllUserList() throws IOException {
		
		Set<Integer> allOnlineSet = getAllOnlineUserList();
		Object[] allOnlineList = allOnlineSet.toArray();
		String[] allOnline = new String[allOnlineList.length];
		
		int index = 0;		
		for(Object user : allOnlineList)
			allOnline[index++] = user.toString();
		
		output.writeObject(ALL_LIST);
		output.flush();
		output.writeObject(allOnline);
		output.flush();
		
		System.out.println("All user list sent from " + connection.getLocalPort() + " to " + 
				connection.getInetAddress() + ":" + connection.getPort());
	}
	
	private Set<Integer> getAllOnlineUserList() {
		return Server.clientConnections.keySet();
	}
}
