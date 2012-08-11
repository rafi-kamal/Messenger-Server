package messenger.server.model.Test;

import java.sql.SQLException;

import junit.framework.Assert;
import messenger.server.model.ClientData;

import org.junit.BeforeClass;
import org.junit.Test;

public class ClientDataTest {

	private static ClientData data;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		data = new ClientData();
	}

	@Test
	public void test() throws ClassNotFoundException, SQLException {
		data.fetchClientData(1);		
		Assert.assertEquals(data.fullName, "Rafi Kamal");
		Assert.assertEquals(data.institution, "BUET");
		
		data.fetchClientData(2);
		Assert.assertEquals(data.address, "Mirpur");
	}

}
