package messenger.server.model;

import java.sql.SQLException;

import javax.sql.rowset.CachedRowSet;

import com.sun.rowset.CachedRowSetImpl;

/**
 * Base class for database connection. Any class connecting to the database will
 * inherit it.
 */
public class Connector {
	private static final String DRIVER = "com.mysql.jdbc.Driver";
	private static final String DATABASE_URL = "jdbc:mysql://localhost/client";
	private static final String USERNAME = "root";
	private static final String PASSWORD = "dmjajgma@";

	protected CachedRowSet rowSet;

	protected void connect() throws SQLException {
		try {
			Class.forName(DRIVER);
		} catch (ClassNotFoundException classNotFoundException) {
			System.err.println("Database driver not found.");
		}

		rowSet = new CachedRowSetImpl();
		rowSet.setUrl(DATABASE_URL);
		rowSet.setUsername(USERNAME);
		rowSet.setPassword(PASSWORD);
	}

	protected void query(String query) throws SQLException {
		rowSet.setCommand(query);
		rowSet.execute();
	}

	protected void close() {
		try {
			if (rowSet != null)
				rowSet.close();
		} catch (SQLException exception) {
			System.err.println("Error closing database");
		}
	}
}
