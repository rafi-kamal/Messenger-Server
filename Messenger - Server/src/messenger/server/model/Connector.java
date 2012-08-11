package messenger.server.model;

import java.sql.SQLException;

import javax.sql.rowset.CachedRowSet;

import com.sun.rowset.CachedRowSetImpl;

public class Connector 
{
	private static final String DRIVER = "com.mysql.jdbc.Driver";
	private static final String DATABASE_URL = "jdbc:mysql://localhost/client";
	private static final String USERNAME = "messenger";
	private static final String PASSWORD = "1234";
	
	protected CachedRowSet rowSet;
	
	protected void connect() throws SQLException, ClassNotFoundException
	{
		Class.forName(DRIVER);
		
		rowSet = new CachedRowSetImpl();
		rowSet.setUrl(DATABASE_URL);
		rowSet.setUsername(USERNAME);
		rowSet.setPassword(PASSWORD);
	}
	
	protected void query(String query) throws SQLException
	{
		rowSet.setCommand(query);
		rowSet.execute();
	}
	
	protected void close()
	{
		try {
			if(rowSet != null)
				rowSet.close();
		}
		catch (SQLException exception) {
			System.err.println("Error closing database");
		}
	}
}
