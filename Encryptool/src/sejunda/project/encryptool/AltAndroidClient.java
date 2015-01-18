package sejunda.project.encryptool;

import ocsf.client.*;
import android.os.Handler;
import android.os.StrictMode;
import client.ChatClient;
import common.*;

import java.io.*;
import java.math.BigInteger;
import java.net.*;

public class AltAndroidClient extends ObservableClient {
	
	/*
	/**
	   * The interface type variable.  It allows the implementation of 
	   * the display method in the client.
	   */
	 // AndroidIF androidUI;
	
	//ClientService androidClientService;
	  
	  EncryptoolContact contact;
		
	  RSAencryptool encryptool;
	  
	  AltClientService androidClientService;
	  
	public AltAndroidClient(String ipAddress, int port, AltClientService service, RSAencryptool encryptool, /*, EncryptoolContact contact*/ Handler handler)
	throws IOException{
		super(ipAddress, port);
		//this.androidUI = aIF;
		this.androidClientService = service;
		this.encryptool = encryptool;
		
		handler.post(new Runnable(){
			public void run(){
				try {
					openConnection();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}	
			}
		});		
		//sendToServer("#Register<" + encryptool.getPublicKey().toString() + "<" + encryptool.getEncryptionExponent().toString());
			
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
			AltClientService.contactSet = new ContactSet();
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
				AltClientService.contactSet.addContact(new EncryptoolContact(ip, port, userName, publicKey, encryptionExponent));
				//contactSocket.close();
			}
			
			//AltClientService.setContactSet(androidClientService.contactSet);
		}
		else if(message.startsWith("#Message")){
			String[] messageSplit = message.split(">");
			String sender = messageSplit[1];
			String decryptedMessage = encryptool.decrypt(messageSplit[2].split(":"));
			
			// TODO: return message to message view
		}
	}

	

}
