package sejunda.project.encryptool;

import ocsf.client.*;
import android.util.Log;
import client.ChatClient;
import common.*;

import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.util.ArrayList;

public class AndroidClient extends ObservableClient {
	
	/*
	/**
	   * The interface type variable.  It allows the implementation of 
	   * the display method in the client.
	   */
	 // AndroidIF androidUI;
	
	//ClientService androidClientService;
	  
	  EncryptoolContact contact;
		
	  RSAencryptool encryptool;
	  
	  ClientService androidClientService;
	  
	  ArrayList<String> messages = new ArrayList<String>();
	  
	public AndroidClient(String ipAddress, int port, ClientService service, RSAencryptool encryptool /*, EncryptoolContact contact*/)
	throws IOException{
		super(ipAddress, port);
		//this.androidUI = aIF;
		this.androidClientService = service;
		this.encryptool = encryptool;
		openConnection();
		sendToServer("#Register<" + encryptool.getPublicKey().toString() + "<" + encryptool.getEncryptionExponent().toString());
			
	}
	
	public void handleMessageFromServer(Object msg){
		try {
			interpret((String) msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * interprets a message routed from the server
	 * @param message
	 * @throws IOException 
	 */
	public void interpret(String message) throws IOException{
		if(message.startsWith("#Contacts")){
			String[] messageSplit = message.split("<");
			ClientService.contactSet = new ContactSet();
			for(int i = 1; i < messageSplit.length; i++){
				String[] contactInfo = messageSplit[i].split(":");
				/*
			    contact.toString() format
				userName + ":" +
				iPaddress.toString() + ":" +
				String.valueOf(port) + ":" +
				publicKey.toString() + ":" +
				encryptionExponent.toString()  ;
				 */
				String userName = contactInfo[0];
				InetAddress ip = InetAddress.getByName(contactInfo[1]);
				int port = Integer.valueOf(contactInfo[2]);
				BigInteger publicKey = new BigInteger(contactInfo[3]);
				BigInteger encryptionExponent = new BigInteger(contactInfo[4]);
				//Socket contactSocket = new Socket(ip, port);
				ClientService.contactSet.contacts.add(new EncryptoolContact(ip, port, userName, publicKey, encryptionExponent));
				//contactSocket.close();
	
			}
		}
		else if(message.startsWith("#Message")){
			String[] messageSplit = message.split(">");
			String sender = messageSplit[1];
			String decryptedMessage = encryptool.decrypt(messageSplit[2].split(":"));
			
			messages.add(sender + ": " + decryptedMessage);
		}
	}
	/**
	   * This method terminates the client.
	   */
	  public void quit()
	  {
	    try
	    {
	      closeConnection();
	    }
	    catch(IOException e) {}
	    System.exit(0);
	  }
	  
	  /**
	   * Added method that overrides connectionClosed in the base class
	   * This method prints a closing message to the console
	   * 
	   */
	  public void connectionClosed()  // Changed: ADDED for E49 SB
	  {
		  setChanged();
		  notifyObservers(CONNECTION_CLOSED);
		  Log.d("SERVER ERROR", "Connection with " + getHost() +" Closed");
	  }
	  
	  /**
	   * 
	   * Added method that overrides connectionClosed in the base class
	   * This method prints a console notification message that
	   * the server has disconnected and quits
	   * 
	   */
	  public void connectionException(Exception exception)  // Changed: ADDED for E49 SB
	  {
	    setChanged();
	    notifyObservers(CONNECTION_EXCEPTION);
	    Log.d("SERVER ERROR", "Server: " + getHost() + " has shut down");
	    quit();
	  }
	

}
