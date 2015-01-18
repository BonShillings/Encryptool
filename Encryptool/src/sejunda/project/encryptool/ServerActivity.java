package sejunda.project.encryptool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;

import org.apache.http.conn.util.InetAddressUtils;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.os.storage.OnObbStateChangeListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
public class ServerActivity extends Activity {
	 
    private TextView serverStatus;
    
    private ListView serverContacts;
    private ContactSet contactSet;
 
    // DEFAULT IP
    public static String SERVERIP = "10.0.2.15";
 
    // DESIGNATE A PORT
    public static final int SERVERPORT = 5560;
 
    private Handler handler = new Handler();
 
    private ServerSocket serverSocket;
    
    private AndroidServer androidServer; 
    
    private ArrayList<String> serverMessages= new ArrayList<String>();
    
    boolean connected = true;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
        
        contactSet = new ContactSet();
        serverStatus = (TextView) findViewById(R.id.server_status);
        serverContacts = (ListView) findViewById(R.id.serverContacts);
        //SERVERIP = getLocalIpAddress(); //other option but may return ipv6
        SERVERIP = getIPAddress();
        //((TextView) findViewById(R.id.server_local_ip)).setText(SERVERIP.toString());
        
        androidServer = new AndroidServer(SERVERPORT, handler);
        
        try 
        {
          androidServer.listen(); //Start listening for connections
        } 
        catch (Exception ex) 
        {
        	 Log.d("ServerActivity", "ERROR - Could not listen for clients!");
        }
       // Thread fst = new Thread(new ServerThread());
       // fst.start();
       
    }
    
    
 
    public class AndroidServer extends AbstractServer{

    	public AndroidServer(int port, Handler handler) {
    		super(port);
    		this.handler = handler;
    		
    		if (SERVERIP != null) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        serverStatus.setText("Listening on IP: " + SERVERIP);
                    }
                });
    		}
    	}
    	
    	private Handler handler;
    	
    	public void interpret(String message, ConnectionToClient connection, EncryptoolContact contact) throws UnknownHostException{
    		if(message.startsWith("#")){
    			//server message
    			if (message.startsWith("#Request")){
    				serverMessages.add("#Request from " + contact.userName + " at " + contact.iPaddress.toString());    
    				try{ 	
    				//connection.sendToClient("#Contacts<" + " ");
    				connection.sendToClient("#Contacts" + getContactSet().toString());
    				}
    				catch(IOException e){
    					Log.d("SERVER ERROR", e.getMessage());
    				}
    				//this.serverActivity.updateServerMessages();
    			}
    			else if(message.startsWith("#SendTo")){
    				
    				String[] messageSplit = message.split(">");
    				String ip = messageSplit[1];
    				int port = Integer.valueOf(messageSplit[2]);
    				String messageToSend = messageSplit[3];
    				InetAddress ipAddress = InetAddress.getByName(ip);
    				EncryptoolContact recipient = getContactSet().findContactByIPandPort(ipAddress, port);
    				serverMessages.add("#Message from " + contact.userName + "at " + contact.iPaddress.toString() 
    						+"to " + recipient.userName + " at " + recipient.iPaddress);
    				if(recipient != null){
    					try{
    						PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(recipient.getSocket()
    			                    .getOutputStream())), true);
    						String sender = "";
    						if(contact.userName != null){
    							sender = contact.userName;
    						}
    						else{
    							sender = ip;
    						}
    						out.println("#Message>" + sender + ">"+ messageToSend);
    					}
    					catch (IOException e) {
    						e.printStackTrace();
    					}
    				}
    				else{
    					String sender = "SERVER";			
						try {
							connection.sendToClient("#Message>" + sender + ">"+ "RECIPIENT AT " + ip + " NOT FOUND");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
    				}
    			}
    			else if(message.startsWith("#Register")){
    				serverMessages.add(message + "<" + contact.iPaddress.toString() + "<" + String.valueOf(contact.port) );
    				
    				String[] messageSplit = message.split("<");
    				contact.publicKey = new BigInteger(messageSplit[1]);
    				contact.encryptionExponent = new BigInteger(messageSplit[2]);
    				getContactSet().updateContact(contact);
    				getContactSet().contacts.add(contact);
    				
    				
    				}
    		}
    		handler.post( new Runnable(){
				public void run(){
					updateServerMessages();
    				updateServerContacts();
				}
			});
    	}


    	@Override
    	protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
    		
    		EncryptoolContact contact = new EncryptoolContact(client.clientSocket, "newUser", null, null);
    		try {
    			interpret((String)msg, client, contact);
    		} catch (UnknownHostException e) {
    			
    			e.printStackTrace();
    		}
    		handler.post(new Runnable(){
    			public void run() {
    			updateServerMessages();
    		}
    		});
    		
    	}
    	
    	 protected void clientConnected(final ConnectionToClient client) {
    		 handler.post(new Runnable() {
                 @Override
                 public void run() {
                 	try{	
                            
                         ((TextView) findViewById(R.id.connectedText)).setText("Connected.");                         
                         EncryptoolContact contact = new EncryptoolContact(client.clientSocket, "newUser", null, null);
                         contact = contactSet.findContactInfo(contact); 
                         updateServerContacts();
                        	}catch(Exception e){                   		
                        	}	 
                 }
         	});
    	  }
    	 /**
    	  * 
    	  * @param contact
    	  */
    	    public void publishContacts(ConnectionToClient connection){
    	    	try{
    	    	connection.sendToClient("#Contacts<" + contactSet.toString());
    	    	}
    	    	catch(Exception e){
    	    		Log.d("error", e.getMessage()); 
    	    		e.printStackTrace();
    	    	}
    	    }
    }
    
    // GETS THE IP ADDRESS OF YOUR PHONE'S NETWORK
    private String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) { return inetAddress.getHostAddress().toString(); }
                }
            }
        } catch (SocketException ex) {
            Log.e("ServerActivity", ex.toString());
        }
        return null;
    }
    
    /**
     * adapted from http://stackoverflow.com/questions/6064510/how-to-get-ip-address-of-the-device
     * Get IP address from first non-localhost interface
     * @param ipv4  true=return ipv4, false=return ipv6
     * @return  address or empty string
     */
    public static String getIPAddress() {
    	boolean useIPv4 = true;
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress().toUpperCase();
                        boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr); 
                        if (useIPv4) {
                            if (isIPv4) 
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 port suffix
                                return delim<0 ? sAddr : sAddr.substring(0, delim);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) { } // for now eat exceptions
        return "";
    }
  
    
    public ContactSet getContactSet(){
    	return contactSet;
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        try {
             if(serverSocket != null){
             serverSocket.close();
             }
         } catch (IOException e) {
             e.printStackTrace();
         }
    }
/**
 * adds a new contact to the Server contactSet
 * @param contact
 */
	public void updateContact(EncryptoolContact contact) {
				contactSet.updateContact(contact);
		
		
	}
	
	public void updateServerContacts(){
		ListView contactListView = (ListView) findViewById(R.id.serverContacts);
		ArrayList<String> contactList = new ArrayList<String>();
		for(EncryptoolContact contact: contactSet.contacts){
			if(contact.userName == null){
				contact.userName = "newUser";
			}
			contactList.add(contact.userName + "  " + contact.iPaddress.toString());
		}
		
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this, 
                android.R.layout.simple_list_item_1,
                contactList );

        contactListView.setAdapter(arrayAdapter);
	}
	
	public void updateServerMessages(/*ArrayList<String> serverMessages*/){
		ListView serverMessageView = (ListView) findViewById(R.id.serverMessages);
		
		
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this, 
                android.R.layout.simple_list_item_1,
                this.serverMessages );

        serverMessageView.setAdapter(arrayAdapter);
	}
	/**
	 * Deprecated
	 *  Listen to a the inputStreams from Clients
	 */
	/*
	private void listen() {
		for(EncryptoolContact contactClient: contactSet.contacts){
			try {
                BufferedReader in = new BufferedReader(new InputStreamReader(contactClient.getSocket().getInputStream()));
                String line = null; 
                while ((line=in.readLine())  != null) {
                    Log.d("ServerActivity", line);
                    
                /////////IMPORTANT////////////////////
                    interpret(line, contactClient);	
                   
                }
			} catch (Exception e) { }
        }
	}
	*/
 
}