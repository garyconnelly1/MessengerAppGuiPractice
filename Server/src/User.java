
//package ie.gmit.sw;

/*
 simple class to create new users for the system. Will later be connected to a database. For now
 users are hardcoded in the providerMain class using this class' constructor.
  */



public class User {
	private String userName;
	private String password;
	
	//constructor to create a new user with a username and password attribute
	public User(String userName, String password) {
		super();
		this.userName = userName;
		this.password = password;
	}


	//getters and setters
	public String getUserName() {
		return userName;
	}


	public void setUserName(String userName) {
		this.userName = userName;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}
	
	
	
	

}
