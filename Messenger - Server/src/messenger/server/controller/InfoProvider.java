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
	private static ServerSocket fileServer;
	private int clientID;
	private static Socket recieverConnection;
	private static Socket senderConnection;
	
	static {
		try {
			fileServer = new ServerSocket(fileTransferPort);
		} catch (IOException exception) {
			System.err.println("Error setting up file transfer server");
		}
	}
	
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
		while(true) {
			try {
				int request = (Integer) input.readObject();
				
				switch(request) {
				case ALL_LIST:
					sendAllUserList();
					break;
				case SEND_FILE:
					int recieverID = (Integer) input.readObject();
					ClientHandler recieverHandler = Server.clientConnections.get(recieverID);
					ClientHandler senderHandler = Server.clientConnections.get(clientID);
					
					server.serverGUI.showMessage(senderHandler.getClientName() + 
							" is trying to send a file to " + recieverHandler.getClientName());
					recieverHandler.infoProvider.sendDelayedMessage(RECEIVE_FILE, clientID, senderHandler.getClientName());
					recieverConnection = establishFileTransferConnection();
					System.out.println("Reciever Connection: " + recieverConnection);
					break;
				case READY:
					System.out.println("Reciever is ready");
					int senderID = (Integer) input.readObject();
					InfoProvider senderInfoProvider = Server.clientConnections.get(senderID).infoProvider;
					senderInfoProvider.sendDelayedMessage(READY);
					senderConnection = establishFileTransferConnection();
					System.out.println("Reciever Connection: " + recieverConnection);
					System.out.println("Sender Connection: " + senderConnection);
					TransferFile transferFile = new TransferFile(recieverConnection, 
							senderConnection, server.serverGUI);
					Thread transferThread = new Thread(transferFile);
					transferThread.start();
					break;
				case CANCEL:
					sendData(CANCEL);
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
	}
	
	private void sendDelayedMessage(final Object... messages) {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				for(Object message : messages)
					sendData(message);
			}
		}, 1000);
	}
	
	private Socket establishFileTransferConnection() {
		try {
			Socket connection = fileServer.accept();
			return connection;
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
