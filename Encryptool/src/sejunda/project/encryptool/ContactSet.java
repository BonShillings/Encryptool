package sejunda.project.encryptool;

import java.net.InetAddress;
import java.util.ArrayList;

public class ContactSet {

	public ArrayList<EncryptoolContact> contacts = new ArrayList<EncryptoolContact>();
	
	public EncryptoolContact findContactInfo(EncryptoolContact contact){
		for(int i =0; i < contacts.size();i++){
			EncryptoolContact contactFile = contacts.get(i);
			if(contactFile.iPaddress.equals(contact.iPaddress) && contactFile.port == contact.port){
				return contactFile;
			}
		}
		contacts.add(contact);
		return contact;
	}
	public EncryptoolContact findContactByIPandPort(InetAddress ip, int port){
		for(int i =0; i < contacts.size();i++){
			EncryptoolContact contactFile = contacts.get(i);
			if(contactFile.iPaddress.equals(ip) && contactFile.port == port){
				return contactFile;
			}
		}
		return null;
	}
	
	public String toString(){
		String stringRepresentation = "";
		for(EncryptoolContact contact:contacts){
			if(contact.encryptionExponent != null){
			stringRepresentation += ("<" + contact.toString() );
			}
		}
		return stringRepresentation;
	}
	/**
	 * updates a current contact
	 * or
	 * adds if contact not found
	 * @param contactInSet
	 */
	public void updateContact(EncryptoolContact updatedContact) {
		boolean found = false;
		for(EncryptoolContact contactFile: contacts){
			if(contactFile.iPaddress.equals(updatedContact.iPaddress) && contactFile.port == updatedContact.port){
				contactFile = updatedContact;
				contactFile.encryptionExponent = updatedContact.encryptionExponent;
				contactFile.publicKey = updatedContact.publicKey;
				found = true;
				break;
			}
		}
		if(!found){
			contacts.add(updatedContact);
		}
	}
	
	public void addContact(EncryptoolContact contact){
		contacts.add(contact);
	}
}
