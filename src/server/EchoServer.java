package server;


import java.io.*;
import ocsf.server.*;
import common.*;


public class EchoServer extends AbstractServer {
	
	final public static int DEFAULT_PORT = 5555;
	ChatIF serverUI;
	private boolean closing = false;

	public EchoServer(int port, ChatIF serverUI) throws IOException {
		super(port);
		this.serverUI = serverUI;
		listen();

	}


	public void handleMessageFromClient(Object msg, ConnectionToClient client) {
		String command = (String)msg;
		serverUI.display("Message: \""+command+"\" from" + client.getInfo("loginID"));
		
		if (command.startsWith("#login")){
			if(client.getInfo("loginID") == null){
				try{
				client.setInfo("loginID", command.substring(7));
				serverUI.display(client.getInfo("loginID") + " has logged on.");
				sendToAllClients(client.getInfo("loginID") + "has logged on.");
				}catch (IndexOutOfBoundsException e){
				try{
				client.sendToClient("Error -invalid login command");
				
				} catch(IOException ex){}
				try{
				client.close();
				}
				catch(IOException exc){
				serverUI.display("ERROR- cannot remove client");
				}
				serverUI.display("no login, terminating connection");
				}
				}
				 else{
				serverUI.display(client.getInfo("loginID") + "attempted to login twice");
				try{
				client.sendToClient("Cannot login twice.");
				} catch(IOException e){
				try{
				client.close();
				} catch(IOException ex){
				serverUI.display("Error- cannot remove client");
				}
			}
		}
	
	}else{
	if(client.getInfo("loginID") == null){
	serverUI.display("unknown client did not login." + "terminating connections");
	try{
	client.sendToClient("No login recorded, disconnecting from server");
	 }catch(IOException e){}
	finally{
	try{
	client.close();
 } catch (IOException ex){
 serverUI.display("Error - cannot remove client");
 }
 }
 }
 
	else{
 		this.sendToAllClients(client.getInfo("loginID") + ">" + msg);
 	}
 }
 }
 


	public void quit(){
	try{
		closing = true;
		sendToAllClients("Server is quitting.");
		sendToAllClients("You will be disconnecting");
		close();
	}
	catch(IOException e){}
	System.exit(0);
}


	protected void serverStarted() {
		if(getNumberOfClients() != 0)
		sendToAllClients("Server has restarted accepting connections");

		serverUI.display("Server listening for connections on port " + getPort());
	}

	protected void serverStopped() {
		sendToAllClients("Warring- server will be stopped accepting clients");

		System.out.println("Server has stopped listening for connections.");
	}

	protected void serverClosed(){
	serverUI.display("Server is closed");
	}

	protected void clientConnected(ConnectionToClient client){
	serverUI.display("A new client is attempting to connect"  + " to the server");
	}

	protected void clientDisconnected(ConnectionToClient client){
	disconnectionNotify(client);
	}

	synchronized protected void clientException(ConnectionToClient client, Throwable exception){
	disconnectionNotify(client);
	}

	public void handleMessageFromServerUI(String message){
	if(message.startsWith("#quit"))
	quit();

	if(message.startsWith("#stop")){
		if(isListening()){
		stopListening();
		} else{
		serverUI.display("Cannot stop the server before it is restared.");
		}
		return;
		}

	if(message.startsWith("#start")){
	closing = false;
	if(!isListening()){
	try{
	listen();
	} catch (IOException e){
	serverUI.display("Cannot listen. terminating server");
	quit();
	}
} else{
serverUI.display("Server is already running.");
}
return;
}

if (message. startsWith("#close")){
closing = true;
try{
sendToAllClients("Server shutting down. you are being disconnected");
close();

}
catch(IOException e) {
	serverUI.display("Cannot close normally. terminating server");
	quit();
	}
return;
}

if(message.startsWith("#getport")){
serverUI.display("Current port: " + getPort());
return;
}

if(message.startsWith("#setport")){
	if((getNumberOfClients() != 0) || (isListening())){
	serverUI.display("Cannot change port while clients" + " are connected or while server is listening");
	}
	else{
	try{
	int port = 0;
	port = Integer.parseInt(message.substring(9));
	if((port < 1024) || (port> 65535)){
	serverUI.display("Invalid port number. Port unchanged");
	} else{
	setPort(port);
	serverUI.display("Port set to"  + port);
	}
	} catch(Exception e){
	serverUI.display("Invalid use of the #setport command.");
	serverUI.display("Port unchanged.");
	}
	}
	return;
	}

	if(!(message.startsWith("#"))){
	serverUI.display("SERVER MESSAGE> "+ message);
	sendToAllClients("SEVER MESSAGE>" + message);
	}
	else{
	serverUI.display("Invalid command.");
	}
}
	

private void disconnectionNotify(ConnectionToClient client){
if(client.getInfo("loginID") !=null){
sendToAllClients(client.getInfo("loginID")+ "has disconnected.");
	serverUI.display(client.getInfo("loginID") + "has disconnected.");
}


}
	}




