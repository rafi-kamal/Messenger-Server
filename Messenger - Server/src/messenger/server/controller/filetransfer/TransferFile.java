package messenger.server.controller.filetransfer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;

import messenger.server.view.ServerGUI;

public class TransferFile implements Runnable
{	
	private Socket recieverConnection;
	private ObjectInputStream recieverInput;
	private ObjectOutputStream recieverOutput;

	private Socket senderConnection;
	private ObjectInputStream senderInput;
	private ObjectOutputStream senderOutput;
	
	int bytesRead;
	private ServerGUI userInterface;

	
	public TransferFile(Socket recieverConnection, 
			Socket senderConnection, ServerGUI userInterface)
	{
		this.recieverConnection = recieverConnection;
		this.senderConnection = senderConnection;
		this.userInterface = userInterface;
	}
	
	
	private void getStreams() throws IOException
	{
		recieverOutput = new ObjectOutputStream(recieverConnection.getOutputStream());
		recieverOutput.flush();
		recieverInput = new ObjectInputStream(recieverConnection.getInputStream());
		

		senderOutput = new ObjectOutputStream(senderConnection.getOutputStream());
		senderOutput.flush();
		senderInput = new ObjectInputStream(senderConnection.getInputStream());
		System.out.println("Got streams");
	}
	
	private void transferFile() throws IOException, ClassNotFoundException
	{	
		String fileName = (String) senderInput.readObject();
		int bufferLength = (Integer) senderInput.readObject();

		recieverOutput.writeObject(fileName);
		recieverOutput.writeObject(bufferLength);
		
		userInterface.showMessage("File " + fileName + " being recieved");
		
		byte[] buffer = new byte[bufferLength];
		do
		{
			bytesRead = (Integer) senderInput.readObject();
			buffer = (byte []) senderInput.readObject();
			
			recieverOutput.writeObject(bytesRead);
			recieverOutput.writeObject(Arrays.copyOf(buffer, buffer.length));
		} 
		while(bytesRead==buffer.length);
		
		userInterface.showMessage("File " + fileName + " recieved");
	}
	
	private void closeConnection()
	{
		try
		{
			recieverInput.close();
			recieverOutput.close();
			recieverConnection.close();
			
			senderInput.close();
			senderOutput.close();
			senderConnection.close();
		}
		catch(IOException exception)
		{
			userInterface.showMessage("Error closing streams");
		}
	}
	
	public void run()
	{
		try
		{
			getStreams();
			transferFile();
		}
		catch(IOException exception)
		{
			userInterface.showMessage("Error in I/O during file trasfer");
		}
		catch(ClassNotFoundException exception)
		{
			userInterface.showMessage("Inappropriate type of object recieved");
		}
		finally
		{
			closeConnection();
		}
	}
}
