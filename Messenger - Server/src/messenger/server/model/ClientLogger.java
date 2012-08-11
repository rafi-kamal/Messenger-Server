package messenger.server.model;

import java.sql.SQLException;

import messenger.Constants;


public class ClientLogger extends Connector implements Constants
{
	public int logIn(String userName, String password) {
		try {
		connect();
			query("SELECT `ID`, `Password` FROM client_logger WHERE `Username` LIKE '" + userName + "'");
			
			if(rowSet.first()) {
				int ID = rowSet.getInt(1);
				String userPass = rowSet.getString(2);
		
				if(userPass.equals(password))
					return ID;
			}
		}
		catch(Exception exception) {
			System.err.println("Error querying database");
		}
		finally {
			close();
		}
		return LOGIN_FAILED;
	}

	public boolean signUp(String userName, String password) {
		
		try {
			connect();
			query("INSERT INTO client_logger (`Username`, `Password`) VALUES ('" + userName + "', '" + password + "')");			
			return true;
		}
		catch(Exception exception) {
			return false;
		}
		finally {
			close();
		}
	}
}

		
 

