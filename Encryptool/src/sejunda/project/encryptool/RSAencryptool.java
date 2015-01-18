package sejunda.project.encryptool;

import java.math.BigInteger;
import java.util.Random;

public class RSAencryptool {

	private BigInteger publicKey;
	private BigInteger privateKeyP;
	private BigInteger privateKeyQ;
	
	private BigInteger encryptionExponent;
	private BigInteger decryptionExponent;
	
	public RSAencryptool(){
	
	}
	/**
	 * Quickly performs an operation of the form message ^ exponenet(mod modulus)
	 * Something is currently broken
	 * @param message
	 * @param exponent
	 * @param modulus
	 * @return
	 */
	public BigInteger fastModularMultiply(BigInteger message, BigInteger exponent, BigInteger modulus){
	
	BigInteger x = new BigInteger("1");;
	BigInteger power = message.mod(modulus);
	
	String exponentBaseTwoString = exponent.toString(2);
	for(int i = 0;i < exponentBaseTwoString.length();i++){
		char c = exponentBaseTwoString.charAt(i);
	if(c == '1'){
		x = (x.multiply(power)).mod(modulus);
	}
	power = (power.pow(2)).mod(modulus);
	}	
	
	return x; // x = (message ^ exponent ) % modulus
	
	
	}
	/**
	 * 
	 */
	public void generateKeys(){
		
		// implementation to be determined 
		// THIS IS A TEMPORARY IMPLEMENTATION
		privateKeyP = BigInteger.probablePrime(64, new Random());
		privateKeyQ = BigInteger.probablePrime(64, new Random());
		
		publicKey = privateKeyP.multiply(privateKeyQ);
		
		BigInteger pTotientFactor = privateKeyP.subtract(BigInteger.ONE);
	    BigInteger qTotientFactor = privateKeyQ.subtract(BigInteger.ONE);
	    
		BigInteger eulerTotient = pTotientFactor.multiply(qTotientFactor);
		// More work necessary
		
		// generate a number totientRelativePrime such that
		// gcd( encryptionExponent, eulerTotient) = 1
		encryptionExponent = BigInteger.probablePrime(logTwo(eulerTotient), new Random());
		while( encryptionExponent.gcd(eulerTotient).compareTo(BigInteger.ONE) != 0){
			encryptionExponent = BigInteger.probablePrime(logTwo(eulerTotient), new Random());
		}
		
		// generates the decryption exponent
		decryptionExponent = encryptionExponent.modInverse(eulerTotient);
		
		
		
	}
	
	/**
	 * 
	 * @param bigInt
	 * @return
	 */
	public int logTwo(BigInteger bigInt){
		int counter = 0;
		BigInteger two = new BigInteger("2");
		while(bigInt.compareTo(BigInteger.ONE) > 0){
			bigInt = bigInt.divide(two);
			counter++;
		}
		return counter;
	}
	
	/**
	 * 
	 * @return
	 */
	public BigInteger getPublicKey(){
		return publicKey;
	}
	
	/**
	 * 
	 * @return
	 */
	public BigInteger getEncryptionExponent(){
		return encryptionExponent;
	}
	/**
	 * Encrypts a message
	 * @param message
	 * @return
	 */
	public String[] encrypt(String message){
		
		char[] messageArray = message.toCharArray();
		String[] encodedMessage = new String[messageArray.length];
		for(int i = 0;i < messageArray.length;i++){
			//cast message peice
			BigInteger messagePeice = new BigInteger( (Integer.valueOf((int) messageArray[i])).toString());
			
			//encrypt messagePeice
			messagePeice = messagePeice.modPow(encryptionExponent, publicKey); 
			//fastModularMultiply(messagePeice, encryptionExponent, publicKey);
			
			encodedMessage[i] = messagePeice.toString();
		}
		return encodedMessage;
	}
	/**
	 * Decrypts a message
	 * @param message
	 * @return
	 */
	public String decrypt(String[] message){
		String decodedMessage = "";
		for(int i = 0;i < message.length;i++){
			//decrypt messagePeice
			BigInteger messagePeice = (new BigInteger(message[i]).modPow(decryptionExponent, publicKey));
					//fastModularMultiply(new BigInteger(message[i]), decryptionExponent, publicKey);
			decodedMessage += (String.valueOf((char)Integer.parseInt(messagePeice.toString())));
		}
		return decodedMessage;
	}
	
	
	public static void main(String[] args){
		RSAencryptool tool = new RSAencryptool();
		tool.generateKeys();
		System.out.println("Encrypting");
		String[] message = tool.encrypt("The movements which work revolutions in the world are born out of the dreams and visions in a peasant's heart on the hillside.");
		System.out.println("Decrypting");
		String messageDecrypted = tool.decrypt(message);
		System.out.println(messageDecrypted);
		System.out.println("wow");
	}
	
}
