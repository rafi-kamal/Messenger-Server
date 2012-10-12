package messenger.server.controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import messenger.Constants;
import messenger.server.ServerConnection;
import messenger.server.controller.filetransfer.TransferFile;

/** Handle client requests and sends non-message data to the client (e.g. user lists or user info). */
public class InfoProvider extends ServerConnection implements Runnable, Constants {

	private Server server;
	private static int fileTransferPort = 5557;
	private ServerSocket fileServer;
	private int clientID;
	
	public InfoProvider(Server server, Socket connection, int clientID) {
		this.server = server;
		this.connection = connection;
		this.clientID = clientID;
	}
	
	@Override
	public void run() {
		getStreams();
		server.updateLists();
		processConnection();
	}
	
	private void getStreams() {
		try {
			output = new ObjectOutputStream(connection.getOutputStream());
			output.flush();
			input = new ObjectInputStream(connection.getInputStream());
		}
		catch(IOException ioException) {
			System.err.println("Error getting streams in infoprovider");
			closeConnection();
		}
	}
	
	protected void processConnection() {
		try {
			int request = (Integer) input.readObject();
			
			switch(request) {
			case ALL_LIST:
				sendAllUserList();
				break;
			case SEND_FILE:
				int recieverID = (Integer) input.readObject();
				ClientHandler recieverHandler = Server.clientConnections.get(recieverID);
				
				recieverHandler.infoProvider.sendDelayedMessage(RECEIVE_FILE);
				server.serverGUI.showMessage(clientID + 
						" is trying to send a file to " + recieverID);
				Socket recieverConnection = establishFileTransferConnection();
				int recieverResponse = (Integer) input.readObject();
				if(recieverResponse == READY) {
					sendDelayedMessage(READY);
					Socket senderConnection = establishFileTransferConnection();
					TransferFile transferFile = new TransferFile(senderConnection, 
							recieverConnection, server.serverGUI);
					Thread transferThread = new Thread(transferFile);
					transferThread.start();
				} else {
					sendData(CANCEL);
				}
				break;
			}
		}
		catch(ClassNotFoundException classNotFoundException) {
			server.serverGUI.showMessage("Unknown object type recieved");
		}
		catch(IOException ioException) {
			server.serverGUI.showMessage("Error sending data");
		}
	}
	
	private void sendDelayedMessage(final Object message) {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				sendData(message);
			}
		}, 10);
	}
	
	private Socket establishFileTransferConnection() {
		try {
			if(fileServer == null || fileServer.isClosed())
				fileServer = new ServerSocket(fileTransferPort);
			return fileServer.accept();
		} catch(IOException exception) {
			server.serverGUI.showMessage("Error setting up fileServer");
			return null;
		}
	}
	
	/*** Sends the <b>All User list.</b> */
	public void sendAllUserList() throws IOException {
		
		Map<Integer, String> allOnline = Server.getAllOnlineUsers();
		
		/*server.serverGUI.showMessage("All user list sending from " + connection.getLocalPort() + " to " + 
				connection.getInetAddress() + ":" + connection.getPort());*/
		
		output.writeObject(ALL_LIST);
		output.flush();
		output.writeObject(allOnline);
		output.flush();
	
	}
}
