package messenger.server.model.Test;

import junit.framework.Assert;
import junit.framework.TestCase;
import messenger.server.model.ClientLogger;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ClientLoggerTest{
	
	static ClientLogger logger;
	int clientID;

	@BeforeClass 
	public static void init() {
		logger = new ClientLogger();	      
	}
	
	@Test
	public void testLogIn() {
		clientID = logger.logIn("Rafi", "1234");
		Assert.assertEquals(clientID, 1);
		clientID = logger.logIn("Sami", "qwerty");
		Assert.assertEquals(clientID, 15);
	}
	
	@Test
	public void testSignUp() {
		boolean signUpSuccessful = logger.signUp("Sami", "qwerty");
		Assert.assertEquals(signUpSuccessful, false);
	}
}
