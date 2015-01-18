package sejunda.project.encryptool;
/*
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

import org.apache.http.conn.util.InetAddressUtils;

import sejunda.project.encryptool.ServerActivity.AndroidServer;
import android.os.Handler;
*/
public class EncryptoolTestCases {

	/*
	 * Note: these test cases are very simplified
	 * Emulating the entire app behavior would be very difficult
	 * 
	 */
	public static void main(String[] args){
		testEncryption();
		System.out.println();
		/*
		if(testCase1()){
			System.out.println("Test case 1: Passed");
		}
		else{
			System.out.println("Test case 1: Failed");
		}
		System.out.println();
		if(testCase2()){
			System.out.println("Test case 2: Passed");
		}
		else{
			System.out.println("Test case 2: Failed");
		}
		System.out.println();
		if(testCase3()){
			System.out.println("Test case 3: Passed");
		}
		else{
			System.out.println("Test case 3: Failed");
		}
		*/
	}
	/*
	// initialize a server on port 5560
	public static boolean testCase1(){
		try{
		ServerActivity activity = new ServerActivity();
		ServerActivity.AndroidServer server = activity.new AndroidServer(5560, new Handler());
		server.listen();
		return true;
		}
		catch(Exception e){
			return false;
		}
	}
	
	public static boolean testCase2(){
		try{
			
			RSAencryptool encryptool = new RSAencryptool();
			encryptool.generateKeys();
			
			ServerActivity activity = new ServerActivity();
			ServerActivity.AndroidServer server = activity.new AndroidServer(5560, new Handler());
			server.listen();
			
			AndroidClient client = new AndroidClient(getIPAddress(), 5560, null, encryptool);
			// "#register" is send in the constructor
			
			return true;
			}
			catch(Exception e){
				return false;
			}
	}
	
	public static boolean testCase3(){
		try{
			
			RSAencryptool encryptool = new RSAencryptool();
			encryptool.generateKeys();
			
			ServerActivity activity = new ServerActivity();
			ServerActivity.AndroidServer server = activity.new AndroidServer(5560, new Handler());
			server.listen();
			
			AndroidClient client1 = new AndroidClient(getIPAddress(), 5560, null, encryptool);
			// "#register" is send in the constructor
			
			client1.sendToServer("#request");
			
			AndroidClient client2 = new AndroidClient(getIPAddress(), 5560, null, encryptool);
			// "#register" is send in the constructor
			
			client2.sendToServer("#request");
			
			String message = "Winter is Coming.";
					
			String[] encryptedMessageBits = encryptool.encrypt(message);
			String encryptedMessage = "";
			for(int i = 0; i < encryptedMessageBits.length;i++){
				encryptedMessage += encryptedMessageBits[i]+":";
			}
			String messageToSend = "#SendTo>" + 
									client2.getInetAddress() + ">" 
									+ client2.getPort() + ">"
									+ encryptedMessage;
			 
			 //new methodology
			 try{
			 client1.sendToServer("messageToSend");
			 }
			 catch(Exception e){}
			  
			for(String messageStored : client2.messages){
				System.out.println(messageStored);
				return true;
			}
			
			
			return false;
			}
			catch(Exception e){
				return false;
			}
	}
	*/
	public static void testEncryption(){
			RSAencryptool tool = new RSAencryptool();
			tool.generateKeys();
			System.out.println("Encrypting");
			String[] message = tool.encrypt("The movements which work revolutions in the world are born out of the dreams and visions in a peasant's heart on the hillside.");
			System.out.println("Decrypting");
			String messageDecrypted = tool.decrypt(message);
			System.out.println(messageDecrypted);
			System.out.println("wow");
		
	}
	
	
	/**
     * adapted from http://stackoverflow.com/questions/6064510/how-to-get-ip-address-of-the-device
     * Get IP address from first non-localhost interface
     * @param ipv4  true=return ipv4, false=return ipv6
     * @return  address or empty string
     */
   /* public static String getIPAddress() {
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
    
 */
}
