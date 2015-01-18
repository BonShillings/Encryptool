package sejunda.project.encryptool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.widget.TextView;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class ClientService extends IntentService {
	
	
	private static final String ACTION_LISTEN = "sejunda.project.encryptool.action.LISTEN";
	
	private static final String ACTION_MESSAGE = "sejunda.project.encryptool.action.MESSAGE";
	
	private static final String ACTION_REQUEST = "sejunda.project.encryptool.action.REQUEST";
	
	private static final String ACTION_CONNECT = "sejunda.project.encryptool.action.CONNECT";
	
	
			
			
	// properties for ACTION_LISTEN
	private static final String ADDRESS = "sejunda.project.encryptool.extra.ADDRESS";
	private static final String PORT = "sejunda.project.encryptool.extra.PORT";
	

	// properties for ACTION_MESSAGE
	private static final String MESSAGE = "sejunda.project.encryptool.extra.MESSAGE";
	
	private static ClientService service;
	
	 /**
	   * The instance of the client that created communicates with the server.
	   */
	private static AndroidClient androidClient;
	
	// Connection variable 
	private static Socket client;
	
    private static ObjectOutputStream output;
	
	private static ObjectInputStream input;
	
	private static InetAddress ip;
	
	private static int port;
	//
	
	// static so as to pass encryptool before service is instantiated
	private static RSAencryptool encryptool;
	public static ContactSet contactSet = new ContactSet();
	
	private static Boolean connected = false;
	
	public static Boolean listening = false;
	
	// handles message interpretation delegation
	private Handler handler = new Handler();
	
	
	
	
	/**
	 * Starts this service to perform Listen with the given parameters. If
	 * the service is already performing a task this action will be queued.
	 * 
	 * @see IntentService
	 */
	public static void startActionListen(Context context, String serverAddress,
			String port) {
		Intent intent = new Intent(context, ClientService.class);
		intent.setAction(ACTION_LISTEN);
		intent.putExtra(ADDRESS, serverAddress);
		intent.putExtra(PORT, port);
		context.startService(intent);
		
	}
	/**
	 * Starts this service to perform Connect with the given parameters. If
	 * the service is already performing a task this action will be queued.
	 * 
	 * @see IntentService
	 */
	public static void startActionConnect(Context context, String serverAddress,
			String port) {
		Intent intent = new Intent(context, ClientService.class);
		intent.setAction(ACTION_CONNECT);
		intent.putExtra(ADDRESS, serverAddress);
		intent.putExtra(PORT, port);
		context.startService(intent);
		
		
	}
	
	/**
	 * Starts this service to perform Listen with the given parameters. If
	 * the service is already performing a task this action will be queued.
	 * 
	 * @see IntentService
	 */
	public static void startActionMessage(Context context, String serverAddress,
			String port, String message) {
		Intent intent = new Intent(context, ClientService.class);
		intent.setAction(ACTION_MESSAGE);
		intent.putExtra(ADDRESS, serverAddress);
		intent.putExtra(PORT, port);
		intent.putExtra(MESSAGE, message);
		context.startService(intent);
		
	}
	
	/**
	 * Starts this service to perform Listen with the given parameters. If
	 * the service is already performing a task this action will be queued.
	 * 
	 * @see IntentService
	 */
	public static void startActionRequest(Context context) {
		Intent intent = new Intent(context, ClientService.class);
		intent.setAction(ACTION_REQUEST);
		//intent.putExtra(ADDRESS, serverAddress);
		//intent.putExtra(PORT, port);
		//intent.putExtra(MESSAGE, message);
		context.startService(intent);
		
	}

	

	public ClientService() {
		super("ClientService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		service = this;
		if (intent != null) {
			final String action = intent.getAction();
			if (ACTION_LISTEN.equals(action)) {
				final String serverAddress = intent.getStringExtra(ADDRESS);
				final String port = intent.getStringExtra(PORT);
				
					try {
						handleActionListen(serverAddress, port);
					} catch (IOException e) {} 
					catch (InterruptedException e) {}
				
			} 
			else if (ACTION_MESSAGE.equals(action)) {
				final String serverAddress = intent.getStringExtra(ADDRESS);
				final String port = intent.getStringExtra(PORT);
				final String message = intent.getStringExtra(MESSAGE);
				handleActionMessage(serverAddress, port, message);
			} 
			else if (ACTION_REQUEST.equals(action)) {
				//final String serverAddress = intent.getStringExtra(ADDRESS);
				//final String port = intent.getStringExtra(PORT);
				//final String message = intent.getStringExtra(MESSAGE);
				
				handleActionRequest();
				
			} 
			else if (ACTION_CONNECT.equals(action)) {
				final String serverAddress = intent.getStringExtra(ADDRESS);
				final String port = intent.getStringExtra(PORT);
				try{
				handleActionConnect(serverAddress, port);
				}catch(Exception e){}
			} 
		}
	}
	// connects to a server
	private void handleActionConnect(String serverAddress, String serverPort) throws Exception {	
			
		 //new methodology
		 if(!connected){
			 
			ip = InetAddress.getByName(serverAddress);
			//InetAddres ip = InetAddress.
		    port = Integer.parseInt(serverPort);
			//client = new Socket(ip, port);
			
		    
			androidClient = new AndroidClient(serverAddress, port, this, encryptool);
			androidClient.sendToServer("#Request");
			}
			
		    /* //old methodology
			try {
				client = new Socket(ip, port);
				output = new ObjectOutputStream(client.getOutputStream());
				input = new ObjectInputStream(client.getInputStream());
			} catch (IOException ex)
			{					 
			} 
			output.writeObject("#Register<" + encryptool.getPublicKey().toString() + "<" + encryptool.getEncryptionExponent().toString());
			//out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
			//out.println("#Register<" + encryptool.getPublicKey().toString() + "<" + encryptool.getEncryptionExponent().toString() );
			output.flush();
			*/
			
			connected = true;				
			
	}

	/**
	 * handler for sending a request to the server
	 */
	private void handleActionRequest() {
		
			//new methodology
			 try{
			 androidClient.sendToServer("#Request");
			 }
			 catch(Exception e){
				 e.printStackTrace();
			 }
			  
			 
		/*
		try {
			//output = new ObjectOutputStream(client.getOutputStream());
			output.writeObject("#Request");
			output.flush();
			
			// create new async thread to interpret messages
				String line = null;
	        
				while ((line = (String) input.readObject()) != null) {                 
		                  
					interpret(line);
					listening = false;
				}
			
		} catch (IOException e) {
			e.printStackTrace();		
        } catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		*/
		
	}

	/**
	 * send a message to the server
	 * @param iPaddress
	 * @param port2
	 * @param message
	 */
	private void handleActionMessage(String iPaddress, String port2, String message) {
		String[] encryptedMessageBits = encryptool.encrypt(message);
		String encryptedMessage = "";
		for(int i = 0; i < encryptedMessageBits.length;i++){
			encryptedMessage += encryptedMessageBits[i]+":";
		}
		String messageToSend = "#SendTo>" + 
								iPaddress + ">" 
								+ port2 + ">"
								+ encryptedMessage;
		 
		 //new methodology
		 try{
		 androidClient.sendToServer("messageToSend");
		 }
		 catch(Exception e){}
		  
		 
		/*
		try {
			output.writeObject(messageToSend);
			output.flush();
		} catch (IOException e) {
			
			//e.printStackTrace();
		}
		*/
	}

	/**
	 *  MAYBE DEPRECATED
	 * Handle action Listen in the provided background thread with the provided
	 * parameters.
	 * 
	 * LISTENS FOR MESSAGES FROM THE SERVER
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	private void handleActionListen(String serverAddress, String serverPort) throws IOException, InterruptedException {
		   
			  //client = new Socket(ip, port);
		   
              BufferedReader in = new BufferedReader(new InputStreamReader(input));
              String line = null;
            listening = true;
  			while(listening){
	          while ((line = in.readLine()) != null) {                 
		                  
            	interpret(line);
            	listening = false;
              }	
  			}
	         // in.close();
	        
	  //this.stopSelf();
				
	}
           		
			
		

	// initialize the encryptool 
	public static void updateEncryptool(RSAencryptool encryptoolPassed){
		encryptool = encryptoolPassed;
	}
	
	/**
	 * interprets a message routed from the server
	 * @param message
	 * @throws IOException 
	 */
	public static void interpret(String message) throws IOException{
		if(message.startsWith("#Contacts")){
			String[] messageSplit = message.split("<");
			contactSet = new ContactSet();
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
				contactSet.contacts.add(new EncryptoolContact(ip, port, userName, publicKey, encryptionExponent));
				//contactSocket.close();
			}
		}
		else if(message.startsWith("#Message")){
			String[] messageSplit = message.split(">");
			String sender = messageSplit[1];
			String decryptedMessage = encryptool.decrypt(messageSplit[2].split(":"));
			
			// TODO: return message to message view
		}
	}
	public Handler getHandler() {
		return handler;
	}
	public void setHandler(Handler handler) {
		this.handler = handler;
	}
}
