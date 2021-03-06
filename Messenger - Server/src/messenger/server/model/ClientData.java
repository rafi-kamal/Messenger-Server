package messenger.server.model;

import java.sql.SQLException;

/** Gets and sets information of the clients from the database. */
public class ClientData extends Connector {
	public String fullName;
	public String address;
	public String dataOfBirth;
	public String institution;
	public String quote;
	public String userName;

	public void fetchClientData(int ID) throws SQLException {
		/*connect();
		super.query("SELECT `Full Name`, `Address`, `Date of Birth`, `Institution`, `Quote` FROM client_info WHERE ID = "
				+ ID);

		rowSet.next();
		fullName = rowSet.getString(1);
		address = rowSet.getString(2);
		dataOfBirth = rowSet.getString(3);
		institution = rowSet.getString(4);
		quote = rowSet.getString(5);

		close();*/
		fetchClientUserName(ID);
	}
	
	public void fetchClientUserName(int ID) throws SQLException {
		connect();
		super.query("SELECT Username FROM client_logger WHERE ID = "
				+ ID);
		rowSet.next();
		fullName = rowSet.getString(1);

		System.out.println(userName);
		close();
	}

	public void setClientData(int ID, String fullName, String address,
			String dateOfBirth, String institution, String quote)
			throws SQLException {
		connect();
		super.query("UPDATE TABLE client_info SET `Full Name` = fullName, `Address` = address, `Date of Birth` = dateOfBirth, "
				+ "`Institution` = institution, `Quote` = quote WHERE ID = "
				+ ID);
		close();
	}
}
