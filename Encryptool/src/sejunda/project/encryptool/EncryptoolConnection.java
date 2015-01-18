package sejunda.project.encryptool;

import java.io.IOException;
import java.net.Socket;

import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;

public class EncryptoolConnection extends ConnectionToClient {
	
	public EncryptoolContact contact;

	public EncryptoolConnection(ThreadGroup group, Socket clientSocket,
			AbstractServer server, EncryptoolContact contact) throws IOException {
		super(group, clientSocket, server);
		this.contact = contact;
	}
	

}
