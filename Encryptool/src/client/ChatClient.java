// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package client;

import ocsf.client.*;
import common.*;

import java.io.*;
import java.net.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @version July 2000
 */
public class ChatClient extends ObservableClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 
  
  /**
   * The login key for this client
   * This ID will be recognized by the server
   * for future communications
   * 
   */
  private String loginID;  // Changed for E51 SB
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String loginID, String host, int port, ChatIF clientUI)  // Changed for E51 SB
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    this.setLoginID(loginID);  // Changed for E51 SB
    openConnection();
    sendToServer("#login <" + this.getLoginID() + ">");  // Changed for E51 SB
  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {
    try
    {
      sendToServer(message);
    }
    catch(IOException e)
    {
      clientUI.display
        ("Could not send message to server.  Terminating client.");
      quit();
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
	  System.out.println("Connection with " + getHost() +" Closed");
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
    System.out.println("Server: " + getHost() + " has shut down");
    quit();
  }


public String getLoginID() {
	return loginID;
}


public void setLoginID(String loginID) {
	this.loginID = loginID;
}
}
//End of ChatClient class
