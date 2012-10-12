package messenger;

/** Holds the constants that are common in both client and server */ 
public interface Constants {
	int FRIEND_LIST = 1001;
	int ALL_LIST = 1002;
	int USER_INFO = 1003;
	
	int LOGIN_REQUEST = 1;
	int SIGNUP_REQUEST = 2;
	int LOGIN_SUCCESSFUL = 3;
	int LOGIN_FAILED = -1;
	int SIGNUP_SUCCESSFUL = 4;
	int SIGNUP_FAILED = -2;
	
	int CONNECTION_ERROR = -1;
	int ERROR_CODE = -1;
	
	int SEND_FILE = 2001;
	int RECEIVE_FILE = 2002;
	int READY = 2000;
	int CANCEL = 0000;
}
