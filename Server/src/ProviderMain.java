//main
//package ie.gmit.sw;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.net.*;




public class ProviderMain {
	public static void main(String args[]) throws IOException
	{
		//1. creating a server socket
		int port = 8123;
		int port1 = 2004;
		
		Server server = new Server(port);
		//Server server = new Server(port1);
		
		server.start();
		
		
	}

}// end main


class Provider extends Thread{
	ServerSocket providerSocket;
	Socket connection = null;
	ObjectOutputStream out;
	ObjectInputStream in;
	Server server;
	String message = "x";
	String userName;
	boolean loggedIn = true;
	//private HashSet<String> groupSet = new HashSet<>();
	ArrayList<String> groupList = new ArrayList<String>();
	 List<ArrayList<String>> groups = new ArrayList<ArrayList<String>>();
	Provider(){}
	
	// provider constructor
	Provider(Server server, Socket s) {
		connection = s;
		this.server = server;
	  }
	public void run()
	{
		try{
			
			//2. Wait for connection
			System.out.println("Waiting for connection");
			
			System.out.println("Connection received from " + connection.getInetAddress().getHostName());
			//3. get Input and Output streams
			out = new ObjectOutputStream(connection.getOutputStream());
			out.flush();
			in = new ObjectInputStream(connection.getInputStream());
			sendMessage("Connection successful");
			//4. The two parts communicate via the input and output streams
			sendMessage("Please the phrase you wish to echo or the word FINISHED to exit");
			//--------------------NORMAL CONVERSATION-----------------------
			
			//sendMessage("FINISHED");
			
			
			handleConversation();
			
			
			
		}//end try
		catch(IOException ioException){
			ioException.printStackTrace();
		}
		finally{
			//4: Closing connection
			try{
				in.close();
				out.close();
				connection.close();
				System.out.println("server> Finished");
			}
			//handle the exception
			catch(IOException ioException){
				ioException.printStackTrace();
			}
		}
	}
	//method to send message
	void sendMessage(String msg)
	{
		try{
			out.writeObject(msg);
			//flush outputstream
			out.flush();
			//System.out.println("server>" + msg);
		}
		catch(IOException ioException){
			ioException.printStackTrace();
		}
	}//end send message
	
	//conversation
	//final String FINISHED = "FINISHED";
	//while (message.equals(FINISHED) == false) {
	void handleConversation(){
		final String FINISHED = "FINISHED";
		int i = 0;
		while (message.equals(FINISHED) == false) {
			i++;
			//testing
			//System.out.println("///////////////////////////////////////////////////" + i);
			
			try{
				
				//read from inputstream
				message = (String)in.readObject();
				System.out.println(message);
				
				// https://stackoverflow.com/questions/11726023/split-string-into-individual-words-java
				String[] words = message.split(" ");
//////IGNORE THIS DARRAGH-----------------------------------------------------------------				
				//checking if the first word is login
				if(words[0].equals("login") && words.length == 3){
					System.out.println("attempting login");
					loginUser(words);
					
				}
				else{
					if(loggedIn){
						//------------------Direct Messaging--------------------------
						if(words[0].equals("DM")) {
							//get a list of the server instances
							List<Provider> userList = server.getProviderList();
							//for each provider in the list, where the user name = the target of the direct message(words[1], send a message to that connection)
							for(Provider provider : userList){
								if(provider.getUsername().equals(words[1])){
									if(!(message.equals(""))){
										provider.sendMessage("Private message ------- " + getUsername() + " " + message);
									}//end if
									
								}//end if
							
							}//end for
						}//end if
	
//////IGNORE THIS DARRAGH-----------------------------------------------------------------							
						//to creat a group
						else if(words[0].equals("CREATEGROUP")) {
							createGroup(words);
						}
						
//////IGNORE THIS DARRAGH-----------------------------------------------------------------							
						//to return the list of groups
						else if(words[0].equals("RETURNGROUPS")) {
							System.out.println("Groups: " + getGroups());
						}
						else{
//DARRAGH THIS IS WHERE THE MESSAGE THAT IS TYPED INTO THE TEXT BOX ON MESSAGECHATYURT ENDS UP
//THIS METHOD IS WORKING AND IT SENDS A MESSAGE BACK TO THE CLIENT BUT IS NEVER READ AS THE CLIENT IS STUCK IN THE LOOP							
							//-------------------attempting a broadcast message--------------------------
							List<Provider> userList = server.getProviderList();
							//for every connection to the server, send a message if they are logged in(if(loggedIn))
							for(Provider provider : userList){
								//send it to every instance, except the current instance(Because that would be just sending a message to yourself).
								//if(provider.getUsername() != this.getUsername()){
								//FOR NOW IT SENDS THE MESSAGE TO EVERY INSTANCE THAT IS ON THE CONNECTION, SO IT WILL ECHO THE SAME MESSAGE BACK TO THE CLIENT
									if(!(message.equals(""))){
										System.out.println("at broadcast" + message);
										//provider.sendMessage("Broadcast message ------- " + getUsername() + " " +  message);
										provider.sendMessage("Broadcast message ------- " +  message);
									}//end if
									
								//}//end if
									
								
								
							}//end for
							
						}//end else
						
						
					}//end if logged in
					else
					{
						sendMessage("please log in to continue");
					}
					
					//sending a null message in order to continue the loop on the client side
					sendMessage(null);
					
				}
				
				//re-initialize words to null in preparation for the next inputstream
				words = null;

			}//end try
			catch(ClassNotFoundException classnot){
				System.err.println("Data received in unknown format");
			} catch (IOException e) {
				
				e.printStackTrace();
			}
			
			
		}//end while
		
	}//end handleConversation
	
	//create group method
	private void createGroup(String[] words) {
		String groupName = words[1];
		
		groupList.add(groupName);
		//groups.add(groupName);
		System.out.println("Successfully added group: " + groupName);
	}
	
	//return the groups
	public ArrayList<String> getGroups() {
		return groupList;
	}
	
	/*
	 /////////////////////////////////////////////////
	  * for testing only
	 */
	public void outPut(String text) {
		System.out.println(text);
	}
	
	
	//function to return the user
	public String getUsername(){
		return userName;
	}
	// loginUser function
//IGNORE ALL THIS AS I USED TO AUTHENTICATE USERNAME AND PASSWORD ON THE SERVER BUT WILL LATER BE DOING IT ON THE CLIENT SIDE CONNECTED TO A DATABASE
	public void loginUser(String[] words) {
		System.out.println("Login method");
		//login username password = 3 words
		if(words.length == 3){
			String userName = words[1];
			String password = words[2];
			//hardcoded user objects later to be queried dynamically from a database
			User gary = new User("GaryConnelly", "gary");
			User dave = new User("DaveClarke", "dave");
			User yogan = new User("EoghanConner", "yogan");
			//new list of users and add each instance of user object
			ArrayList<User> users = new ArrayList<User>();
			users.add(gary);
			users.add(dave);
			users.add(yogan);
			boolean isUser = false;
			for(User user: users){
				//if(userName.equals("GaryConnelly") && password.equals("gary") || userName.equals("DaveClarke") && password.equals("dave") || userName.equals("EoghanConnor") && password.equals("yogan"))
				//checking username and password against the hardcoded user objects
				if(userName.equals(user.getUserName()) && password.equals(user.getPassword())){
					this.userName = userName;
					
					List<Provider> providerList = server.getProviderList();
					//Provider provider;
					//send all users on the connection a message that a new user has logged on
					for(Provider provider : providerList){
						provider.sendMessage("log " + userName + " Is online");
					}
					
					
					for(Provider provider : providerList){
						//again send a null message to continue client side loop
						provider.sendMessage(null);
					}
					//set logged in booleans to true
					isUser = true;
					loggedIn = true;
					//sendMessage("You are logged in as: " + userName);
				}//end if for user validation	
			}
			if(isUser == false){
				sendMessage("Invalid username or password!");
			}
			
			
		}//end if
		
		
	}
	
}

