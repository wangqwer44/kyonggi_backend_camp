package client;

import ocsf.client.*;
import common.*;
import java.io.*;

public class ChatClient extends AbstractClient {
	
	ChatIF clientUI;
	String loginID;

	public ChatClient(String host, int port, ChatIF clientUI) {
		super(host, port);
		this.clientUI = clientUI;
		
	try{
	openConnection();
	
	}
	catch(IOException e){
		handleMessageFromClientUI("logoff");
	clientUI.display("Cannot open connection. Awaiting command");

	}
	}


	public void handleMessageFromServer(Object msg) {
		clientUI.display(msg.toString());
	}

	public void handleMessageFromClientUI(String message) {
		if (message.startsWith("#login") && !isConnected()){
		try{
			openConnection();
			System.out.println("connect");
			}
		catch(IOException e){
			clientUI.display("Cannot establish connection." + "Awaiting command");
			return;
		}
		}
		if(message.startsWith("#quit")){
			quit();
		}
		
		if(message.startsWith("#logoff")){
			try{
				closeConnection();
			} catch(IOException e){
				clientUI.display("Cannot logoff normally. Terminating client.");
				quit();
			}

			//connectionClosed(false);
			return;
			}

		if(message.startsWith("#gethost")){
			clientUI.display("Current host: " + getHost());
			return;
		}


	    if(message.startsWith("#getport")){
			clientUI.display("Current port: " + getPort());
			return;
		}

		if(message.startsWith("#sethost")){
			if(isConnected())
				clientUI.display("Cannot change host while connected.");
			else{
				try{
					setHost(message.substring(9));
					clientUI.display("Host set to:" + getHost());
				}catch(IndexOutOfBoundsException e){
		clientUI.display("Invalid host. Use #sethost <host>");
		}
	}
	return;
	}

		if (message.startsWith("#setport")){
			if(isConnected())
			clientUI.display("Cannot change port while connected.");
			else{
				try{
					int port = 0;
					port = Integer.parseInt(message.substring(9));

					if ((port < 1024) || (port>65535)){
					clientUI.display("Worng port number. port unchanged");

					} else{
					setPort(port);
					clientUI.display("Port set to" + port);
					}
					}
				catch(Exception e){
				clientUI.display("Invalid port. Use #setport <port>");
				clientUI.display("Port unchanged.");
				}
			}
				return;
		}

		if(message.startsWith("#help") || message.startsWith("#?")){
			clientUI.display("\nClient - side command list:"+
			"\n#block <loginID> -- Block message"+
			"\n#channel <channel> -- Connects to the specifed client"+
			"\n#fwd <loginID> -- Forward all message to the specifed client"+
			"\n#getchannel -- Returns the client to the main channel"+
			"\n#gethost -- Gets the host to which the client will connect/is connected"+
			"\n#getport -- Gets the port on which the client will connect/is connected"+
			"\n#help OR #? -- List all commands and their use."+
			"\n#login -- Connects to server"+
			"\n#logoff -- Disconnect from server"+
			"\n#nochannel -- Returns the client to the main channel."+
			"\n#private <loginID> <msg> -- Sends a private message to specified cliend "+
			"\n#pub --  sends a public message"+
			"\n#quit -- Terminates the client and discoonnect from server"+
			"\n#sethost <newhost> -- Specify the host to connect to."+
			"\n#unblock -- Unblock message from a specific client"+
			"\n#nublock <loginID> -- Unblock message from a specific client"+
			"\n#unfwd --Stop forwarding message."+
			"\n#whoblocksme -- List all user who blocks me"+
			"\n#whoiblock -- List all users are you block message from"+
			"\n#whoison -- gets a list of all users and the channel they are");
			return;
		}
		if(!(message.startsWith("#"))||
			message.startsWith("#whoison")||
			message.startsWith("#private")||
			message.startsWith("#channel")||
			message.startsWith("#pub")||
			message.startsWith("#nochannel")||
			message.startsWith("#fwd")||
			message.startsWith("#unfwd")||
			message.startsWith("#block")||
			message.startsWith("#unblock")||
			message.startsWith("#whoiblock")||
			message.startsWith("#whoblocksme"))
			{
				try{
					sendToServer(message);

				}catch (IOException e){
					clientUI.display("Cannot send message to server");


				}
				/*try{
					closeConnection();

				}catch(IOException ex){
					clientUI.display("cannot logoff normally. Terminating client");
					quit();


				}
*/

			}
			else{
				clientUI.display("invalid command");
			}
		}
	/* 
		if((message.startsWith("#login")) || (!(message.startsWith("#")))){
			try{
			sendToServer(message);
			} catch(IOException e){
			clientUI.display("cannot send the message to server. " + " disconnecting");
			try{
				closeConnection();
				} catch(IOException ex){
				clientUI.display("Cannot logoff normally. Terminating server");
				quit();
				}
			}
		} else{
		clientUI.display("Invalid command.");
		}
		

*/
	public void quit() {
		try {
			closeConnection();
		} catch(IOException e) { }
		System.exit(0);
	}

	
	protected void connectionClosed(boolean isAbnormal){
		if(isAbnormal)
			clientUI.display("Abnormal termination of connection.");
		else
			clientUI.display("Connection closed.");
	}

	
	protected void connectionEstablished(){
		clientUI.display("Connection established with" + getHost() + " on port" + getPort());
		}

	protected void connectionException(Exception exception){
		clientUI.display("Connection to sercer terminated");
		}
	}



