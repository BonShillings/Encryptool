package sejunda.project.encryptool;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;
/**
 * Container for the collection of objects needed to store contact information in the server
 * @author Sean
 *
 */
public class EncryptoolContact {

	public int port;
	public InetAddress iPaddress;
	public String userName;
	public BigInteger publicKey;
	public BigInteger encryptionExponent;
	private Socket socket;
	
	/**
	 * Constructor for a server Contact
	 * @param port
	 * @param IPaddress
	 * @param userName
	 * @param publicKey
	 * @param encryptonExponenet
	 */
	public EncryptoolContact(Socket client, String userName, BigInteger publicKey, BigInteger encryptonExponent){
	this.port = client.getPort();
	this.iPaddress = client.getInetAddress();
	this.userName = userName;
	this.publicKey = publicKey;
	this.encryptionExponent = encryptionExponent;
	this.socket = client;
	}
	public EncryptoolContact(InetAddress ip, int port, String userName, BigInteger publicKey, BigInteger encryptonExponent){
		this.port = port;
		this.iPaddress = ip;
		this.userName = userName;
		this.publicKey = publicKey;
		this.encryptionExponent = encryptionExponent;
		//this.socket = client;
		}
	
	public Socket getSocket(){
		return socket;
	}
	
	public String toString(){
		String contactString = 
				userName + ":" +
				iPaddress.toString().replace("/", "") + ":" +
				String.valueOf(port) + ":" +
				publicKey.toString() + ":" +
				encryptionExponent.toString()  ;
		
		return contactString;
	}
}
