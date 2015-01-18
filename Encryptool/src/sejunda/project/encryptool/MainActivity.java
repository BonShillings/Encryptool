package sejunda.project.encryptool;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import sejunda.project.encryptool.AltClientService.LocalBinder;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.internal.view.menu.MenuBuilder.Callback;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity implements ActionBar.OnNavigationListener, MessageFragment.OnFragmentInteractionListener, ConnectFragment.OnFragmentInteractionListener {

    /**
     * The serialization (saved instance state) Bundle key representing the
     * current dropdown position.
     */
    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

    private ArrayAdapter arrayAdapter;
    
    private ConnectFragment connectFragment; 
    private MessageFragment messageFragment; 
    private EncryptoolFragment encryptoolFragment; 
    
    private ClientService client;
    
    private AltClientService altClient;
    
    private ContactSet contactSet = new ContactSet();
    
    private RSAencryptool encryptool;
    
    // not implemented yet
    private AndroidClient androidClient;
    
    private boolean connected = false;
    
    AltClientService clientService;
    boolean isBound = false;

	private ServiceConnection serviceConnection = new ServiceConnection() {

	    public void onServiceConnected(ComponentName className,
	            IBinder service) {
	        LocalBinder binder = (LocalBinder) service;
	        clientService = binder.getService();
	        isBound = true;
	    }
	    
	    public void onServiceDisconnected(ComponentName arg0) {
	        isBound = false;
	    }
	    
	   };
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the action bar to show a dropdown list.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        // Set up the dropdown list navigation in the action bar.
        actionBar.setListNavigationCallbacks(
                // Specify a SpinnerAdapter to populate the dropdown list.
                new ArrayAdapter<String>(
                        actionBar.getThemedContext(),
                        android.R.layout.simple_list_item_1,
                        android.R.id.text1,
                        new String[] {
                                getString(R.string.title_section1),
                                getString(R.string.title_section2),
                                getString(R.string.title_section3),
                        }),
                this);
        
        
        connectFragment = ConnectFragment.newInstance("first", "second");
        messageFragment = MessageFragment.newInstance("first", "second");
        
        encryptoolFragment = EncryptoolFragment.newInstance(1);
       
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();  
        fragmentTransaction.add(R.id.fragmentContainer, encryptoolFragment);
        fragmentTransaction.commit();
        
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Restore the previously serialized current dropdown position.
        if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
            getSupportActionBar().setSelectedNavigationItem(
                    savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // Serialize the current dropdown position.
        outState.putInt(STATE_SELECTED_NAVIGATION_ITEM,
                getSupportActionBar().getSelectedNavigationIndex());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public boolean onNavigationItemSelected(int position, long id) {
        // When the given dropdown item is selected, show its contents in the
        // container view.
    	
    	
    	FragmentManager fragmentManager = getFragmentManager();
    	FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    	
    	if(position +1 == 2){
    	// Replace whatever is in the fragment_container view with this fragment,
    	// and add the transaction to the back stack
    	fragmentTransaction.replace(R.id.fragmentContainer, messageFragment);
    	fragmentTransaction.addToBackStack(null);

    	// Commit the transaction
    	fragmentTransaction.commit();
    	
    	}
    	if(position + 1 == 3){
    	// Replace whatever is in the fragment_container view with this fragment,
    	// and add the transaction to the back stack
    	fragmentTransaction.replace(R.id.fragmentContainer, connectFragment);
    	fragmentTransaction.addToBackStack(null);

    	// Commit the transaction
    	fragmentTransaction.commit();
    	
    	}
    	if(position + 1 == 1){
    	// Replace whatever is in the fragment_container view with this fragment,
    	// and add the transaction to the back stack
    	fragmentTransaction.replace(R.id.fragmentContainer, encryptoolFragment);
    	fragmentTransaction.addToBackStack(null);

    	// Commit the transaction
    	fragmentTransaction.commit();
    	
    	}
    	
        return true;
        
    }
    
    /**
     * initialize the encryptool 
     * pass the encryptool to clientService
     * @param view
     */
    public void generateKeys(View view){
    	
        encryptool = new RSAencryptool();
        encryptool.generateKeys();
        
		TextView publicKey = (TextView) findViewById(R.id.publicKey);
        publicKey.setText(encryptool.getPublicKey().toString());
        
        ClientService.updateEncryptool(encryptool);
        
        AltClientService.updateEncryptool(encryptool);
        
    }
    
    /**
	 * Connect method initiated by connect_fragment "connect to server"
     * @throws IOException 
	 * 
	 * 
	 */
	public void connect(View view) throws IOException{
		
		//client.initialize(server, client, encryptoolFragment.encryptool, contactSet);
		if(!connected){
			
		String ipString = ((EditText) this.findViewById(R.id.server_ip)).getText().toString();
		InetAddress ip = InetAddress.getByName( ipString);
		//ClientService.startActionConnect(this, ipString, "5560"); //temporary hardcoding	
		
		

		Intent intent = new Intent(this, AltClientService.class);
		intent.putExtra("SERVER", ipString);
		intent.putExtra("PORT", "5560");
		intent.setAction("CONNECT");
		bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
		startService(intent);
		
		
		((TextView) findViewById(R.id.textView2)).setText("Connected to Server at " + ipString);
		
		connected = true;
		}		
		
		
	}
	
	/**
	 * update the client side contact set
	 * @param view
	 * the contactSet is managed by the refreshContacts side
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public void refreshContacts(View view) throws IOException, InterruptedException{
		/*
		//ClientService.listening = false;
		ClientService.startActionRequest(this);
		
		//String ipString = ((EditText) this.findViewById(R.id.server_ip)).getText().toString();
		
		//ClientService.startActionListen(this, ipString, "5560" );
		contactSet = AltClientService.contactSet;
		connectFragment.updateContacts(contactSet);
		*/
		
		// new methodology
		Intent intent = new Intent(this, AltClientService.class);
		intent.setAction("REQUEST");
		startService(intent);
		//bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
		
		contactSet = AltClientService.contactSet;
		connectFragment.updateContacts(contactSet);
		
	}
	
	public void updateContactList(){
		contactSet = ClientService.contactSet;
		connectFragment.updateContacts(contactSet);
	}
	
	/**
	 * Send a message to the target selected in connectionsList
	 * @param view
	 * @throws IOException 
	 */
	public void sendMessage(View view) throws IOException{
		
		String ipString = ((EditText) this.findViewById(R.id.server_ip)).getText().toString();
		//getContact
		ListView connectionsList = (ListView) view.findViewById(R.id.connectionsList);
		//ensure contact is select
		if(connectionsList.getSelectedItemPosition() != ListView.INVALID_POSITION){
			
		String target = connectionsList.getSelectedItem().toString();
		String[] userNameIPandPort = target.split(":");
		InetAddress ip = InetAddress.getByName( userNameIPandPort[2].replace(" ", "")) ;
		int port = Integer.valueOf( userNameIPandPort[3].replace(" ", "") );
		
		EncryptoolContact contact = contactSet.findContactByIPandPort(ip, port);
		
		//getMessage from messageText in MessageFragment
		String message = ((EditText) view.findViewById(R.id.messageText)).getText().toString();
		/*
		ClientService.startActionMessage(this, ip.toString().replace("/", ""), String.valueOf(port), message);
		
		ClientService.startActionListen(this, ipString, "5560" );
		*/
		
		Intent intent = new Intent(this, AltClientService.class);
		intent.putExtra("PORT", port);
		intent.putExtra("IP", ip);
		intent.setAction("MESSAGE");
		startService(intent);
		//bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
		}
		
	}
	/**
	 * Transition to Server Mode
	 * @param view
	 */
	public void initializeServer(View view){
		Intent intent = new Intent(this, ServerActivity.class);
		//start the server
		startActivity(intent);
		
	}

    /**
     * The fragment containing a simple view summarizing Encryptool data 
     * and allowing the user to initialize keys.
     */
    public static class EncryptoolFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

       
        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static EncryptoolFragment newInstance(int sectionNumber) {
            EncryptoolFragment fragment = new EncryptoolFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }
///
        
		
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
           
            ListView listview = (ListView) rootView.findViewById(R.id.listSpecs);
            
            ArrayList<String> spec = new ArrayList<String>();
            spec.add("Encryptool: 			version 1.00");
            spec.add("Encryption Scheme:		RSA");
            spec.add("User Version:			Client 1.00");
    	 
    	    // initialize the adapted
    	    ArrayAdapter arrayAdapter = new ArrayAdapter(this.getActivity(), android.R.layout.simple_list_item_1, spec);

    		// add the strings to the listview
    		listview.setAdapter(arrayAdapter);
            
            return rootView;
        }
    }

	@Override
	public void onFragmentInteraction(String id) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		clientService.stopSelf();
	}

}
