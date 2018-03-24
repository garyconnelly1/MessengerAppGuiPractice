//package ie.gmit.sw;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

//make the server threaded to start a new instance for each connection
public class Server extends Thread{
private int port;
//create a list of providers so we can broadcast messages to each provider on the connection
private ArrayList<Provider> providerList = new ArrayList<>();
	public Server(int port) {
		this.port = port;
	}
	
	//get a list of providers currently connected to the server for messaging purposes
	public List<Provider> getProviderList(){
		return providerList;
	}
	
	//start the thread
	public void run(){
		ServerSocket providerSocket;
		try {
			providerSocket = new ServerSocket(port);
			//infinite loop while this server is running
			while(true){
				Socket connection = null;
				//accept the connection
				
				connection = providerSocket.accept();
				//new instance of provider
				Provider provider = new Provider(this, connection);
				//add this instance to the providerList so that it can revieve messages once logged in
				providerList.add(provider);
				//start provider thread
				
				provider.start();
				
			}
		} catch (IOException e1) {
			
			e1.printStackTrace();
		}
		
	}
		
	

}


