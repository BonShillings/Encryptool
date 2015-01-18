package sejunda.project.encryptool;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;
import android.os.Process;

public class AltClientService extends Service {
	
	private final int CONNECT = 1;
	private final int REQUEST = 2;
	private final int SENDMESSAGE = 3;
	
	
		private static final String CONNECTINTENT = "CONNECT";
		private static final String REQUESTINTENT = "REQUEST";
		private static final String MESSAGEINTENT = "MESSAGE";
		
	/**
	 * The instance of the client that created communicates with the server.
	 */
	private static AltAndroidClient androidClient;
	private static InetAddress ip;
	private static int port;
	
	private static boolean connected;
	
	private Looper mServiceLooper;
	
	private ServiceHandler mServiceHandler;
	
	private final IBinder myBinder = new LocalBinder();
	
	// static so as to pass encryptool before service is instantiated
		private static RSAencryptool encryptool;
		
		public static ContactSet contactSet = new ContactSet();
		
		
		
	  
	  // Handler that receives messages from the thread
	  private final class ServiceHandler extends Handler {
	      public ServiceHandler(Looper looper) {
	          super(looper);
	      }
	      @Override
	      public void handleMessage(Message msg) {
	          
	    	  
	    	  if(msg.arg2 == CONNECT){
	    		EncryptoolContact contact = androidClient.contact; 
	    		this.post(new Runnable(){
				 public void run(){
					 try {
						 RSAencryptool encryptTOOL = androidClient.encryptool;
						androidClient.sendToServer("#Register<" + androidClient.encryptool.getPublicKey().toString() + "<" + androidClient.encryptool.getEncryptionExponent().toString());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	    					 
				 }
				});
	    	  }
	    	  
	    	  if(msg.arg2 == REQUEST){
	    		  this.post(new Runnable(){
						 public void run(){
						try {
							androidClient.sendToServer("#Request");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						 }
				  });
	    	  }
	    	  if(msg.arg2 == SENDMESSAGE){
	    		  
	    	  }
	    	  
	          // Stop the service using the startId, so that we don't stop
	          // the service in the middle of handling another job
	          stopSelf(msg.arg1);
	      }
	  }


	  @Override
	  public void onCreate() {
	    // Start up the thread running the service.  Note that we create a
	    // separate thread because the service normally runs in the process's
	    // main thread, which we don't want to block.  We also make it
	    // background priority so CPU-intensive work will not disrupt our UI.
	    HandlerThread thread = new HandlerThread("ServiceStartArguments",
	            Process.THREAD_PRIORITY_BACKGROUND);
	    thread.start();
	    

	    // Get the HandlerThread's Looper and use it for our Handler
	    mServiceLooper = thread.getLooper();
	    mServiceHandler = new ServiceHandler(mServiceLooper);
	  }
	  
	  public AltClientService(){
		  
	  }
	public AltClientService(AndroidClient androidClient, String serverAddress, String serverPort, RSAencryptool encryptool) {
		try {
			ip = InetAddress.getByName(serverAddress);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//InetAddres ip = InetAddress.
	    port = Integer.parseInt(serverPort);
		//client = new Socket(ip, port);
		
	    
		// androidClient = new AndroidClient(serverAddress, port, this, encryptool);
		//androidClient.sendToServer("#Request");
	}
	
	// binder
			public class LocalBinder extends Binder {
		        AltClientService getService() {
		            return AltClientService.this;
		        }
			}
			
	@Override
	public IBinder onBind(Intent intent) {
		return myBinder;
		
	}
	
	 @Override
	  public int onStartCommand(Intent intent, int flags, int startId) {
	      //Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
	      	    
	      // For each start request, send a message to start a job and deliver the
	      // start ID so we know which request we're stopping when we finish the job
	      Message msg = mServiceHandler.obtainMessage();
	      msg.arg1 = startId;
	      
	      final String action = intent.getAction();
	      // translate intents to msg arguments
	      if(action == CONNECTINTENT ){
	    	msg.arg2 = CONNECT; 	    	
	    	if(!connected){
	    	
				 String serverAddress = intent.getStringExtra("SERVER").replace("/", "");
				 String serverPort = intent.getStringExtra("PORT");
				try {
					ip = InetAddress.getByName(serverAddress);
				
				//InetAddres ip = InetAddress.
			    port = Integer.parseInt(serverPort);
				//client = new Socket(ip, port);
				
			    
				androidClient = new AltAndroidClient(serverAddress, port, this, encryptool, mServiceHandler);
				connected = true;
				Toast.makeText(this, "CONNECTED", Toast.LENGTH_SHORT).show();
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				}
	      }
	      else if( action == REQUESTINTENT){
	    	  
	    	  String ipString = ip.getHostAddress().replace("/","");	    			 
	/*    	  try {
				androidClient = new AltAndroidClient(ipString, port, this, encryptool, mServiceHandler);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		*/	
	    	  msg.arg2 = REQUEST; 
	      }
	      else if( action == MESSAGEINTENT){
	    	  msg.arg2 = SENDMESSAGE; 
	      }
	      
	      if(connected){
	      mServiceHandler.sendMessage(msg);
	      }
	  
	      // If we get killed, after returning from here, restart
	      return START_STICKY;
	      
	  }
	 @Override
	  public void onDestroy() {
	    //Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
	  }

	public static void updateEncryptool(RSAencryptool encryptool2) {
		encryptool = encryptool2;
		
	}
	
	public static void setContactSet(ContactSet contacts){
		contactSet = contacts;
	}
	
	
}
