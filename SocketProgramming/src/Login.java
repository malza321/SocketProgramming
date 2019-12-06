import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.w3c.dom.*;
import org.xml.sax.*;
import javax.xml.parsers.*;
//import com.sun.xml.tree.XmlDocument;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import java.awt.Color;

public class Login extends JFrame {

	private JPanel contentPane;
	private JTextField id;
	private JLabel lblLogin;
	private JLabel status;
	private JLabel stnbutton;
	private Socket clientSocket;
	private OutputStream output;
	private InputStream input;
	private boolean keepListening;
	private ClientStatus clientStatus;
	private Document users;
	private Vector conversations;
	private Vector games;
	private DocumentBuilderFactory factory;
	private DocumentBuilder builder;


	public Login() {
		super ("Login");
		try
		{
			// obtain the default parser
			factory = DocumentBuilderFactory.newInstance();

			// get DocumentBuilder
			builder = factory.newDocumentBuilder();
		} 
		catch ( ParserConfigurationException pce ) {
			pce.printStackTrace();
		}
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 240);
		contentPane = new JPanel();
		contentPane.setBackground(Color.YELLOW);
		setContentPane(contentPane);
		contentPane.setLayout(null);

		lblLogin = new JLabel("Set your nickname");
		lblLogin.setBounds(170, 28, 150, 16);
		contentPane.add(lblLogin);

		id = new JTextField();
		id.setBounds(162, 65, 130, 26);
		contentPane.add(id);
		id.setColumns(15);

		stnbutton = new JLabel(new ImageIcon(Login.class.getResource("/images/Startbasic.png")));
		stnbutton.setBounds(6, 86, 438, 132);
		contentPane.add(stnbutton);

		status = new JLabel("Status: Not connected");
		status.setBounds(5, 200, 250, 15);
		contentPane.add(status);
		stnbutton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e){
				stnbutton.setIcon(new ImageIcon(Login.class.getResource("/images/Startentered.png")));
				stnbutton.setCursor(new Cursor(Cursor.HAND_CURSOR));
			}
			@Override
			public void mouseExited(MouseEvent e){
				stnbutton.setIcon(new ImageIcon(Login.class.getResource("/images/Startbasic.png")));
				stnbutton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
			@Override
			public void mousePressed(MouseEvent e){
				loginUser();

			}

		});
		this.setVisible(true);
		runMessengerClient();
	}
	public void runMessengerClient()
	{
		try {
			clientSocket = new Socket(
					InetAddress.getByName( "127.0.0.1" ), 5000 );
			status.setText( "Status: Connected to " +
					clientSocket.getInetAddress().getHostName() );

			// get input and output streams
			output = clientSocket.getOutputStream();
			input = clientSocket.getInputStream();

			stnbutton.setEnabled( true );
			keepListening = true;

			int bufferSize = 0;

			while ( keepListening ) {

				bufferSize = input.available();

				if ( bufferSize > 0 ) {
					byte buf[] = new byte[ bufferSize ];

					input.read( buf );

					InputSource source = new InputSource( 
							new ByteArrayInputStream( buf ) );
					Document message;

					try {

						// obtain document object from XML document
						message = builder.parse( source );

						if ( message != null ) 
							messageReceived( message );

					} 
					catch ( SAXException se ) {
						se.printStackTrace();         
					}
					catch ( Exception e ) {
						e.printStackTrace();
					}
				} 
			}

			input.close();
			output.close();
			clientSocket.close();
			System.exit( 0 );
		} 
		catch ( IOException e ) {
			e.printStackTrace();
			System.exit( 1 );
		}
	}
	public void loginUser()
	{
		// create Document with user login
		Document submitName = builder.newDocument();
		Element root = submitName.createElement( "user" );

		submitName.appendChild( root );
		root.appendChild( 
				submitName.createTextNode( id.getText() ) );

		send( submitName );
	}

	public Document getUsers()
	{ 
		return users;
	}

	public void stopListening()
	{
		keepListening = false;
	}
	public void messageReceived( Document message )
	{
		Element root = message.getDocumentElement();

		if ( root.getTagName().equals( "nameInUse" ) ) 
			// did not enter a unique name
			JOptionPane.showMessageDialog( this,
					"That name is already in use." +
					"\nPlease enter a unique name." );
		else if ( root.getTagName().equals( "users" ) ) {
			// entered a unique name for login
			users = message;
			clientStatus = new ClientStatus( id.getText(), this );
			conversations = new Vector();
			games = new Vector();
			hide();
		} 
		else if ( root.getTagName().equals( "update" ) ) {

			// either a new user login or a user logout
			String type = root.getAttribute( "type" );
			NodeList userElt = root.getElementsByTagName( "user" );
			String updatedUser = 
					userElt.item( 0 ).getFirstChild().getNodeValue();

			// test for login or logout
			if ( type.equals( "login" ) )   
				// login
				// add user to onlineUsers Vector
				// and update usersList
				clientStatus.add( updatedUser );
			else {
				// logout
				// remove user from onlineUsers Vector
				// and update usersList
				clientStatus.remove( updatedUser );

				// if there is an open conversation, inform user
				int index = findConversationIndex( updatedUser );

				if ( index != -1 ) {
					Chatroom receiver = 
							( Chatroom ) conversations.elementAt( index );

					receiver.updateGUI( updatedUser + " logged out" );
					receiver.disableConversation();
				}
			}
		} 
		else if ( root.getTagName().equals( "message" ) ) {
			String from = root.getAttribute( "from" );
			String time = root.getAttribute("time");
			String messageText = root.getFirstChild().getNodeValue();

			// test if conversation already exists
			int index = findConversationIndex( from );

			if ( index != -1 ) { 
				// conversation exists
				Chatroom receiver = 
						( Chatroom ) conversations.elementAt( index );
				receiver.updateGUI( from + ":  " + messageText + " ("+ time + ")" );
			} 
			else { 
				// conversation does not exist
				Chatroom newConv =
						new Chatroom( from, clientStatus, this );
				newConv.updateGUI( from + ":  " + messageText+ " ("+ time + ")" );
			}
		} 
		else if ( root.getTagName().equals( "gameinfo" ) ) {
			String from = root.getAttribute( "from" );
			String messageText = root.getFirstChild().getNodeValue();

			// test if conversation already exists
			int index = findGameIndex( from );

			if ( index != -1 ) { 
				// conversation exists
				Rpsgame receiver = 
						( Rpsgame ) games.elementAt( index );
				receiver.theotherbet = Integer.parseInt(messageText);
				
				if(receiver.mybet==1){
					if(Integer.parseInt(messageText)==1){
						receiver.updateGUI(" !!!! t i e !!!!" );
						receiver.scissors.setVisible(true);
						receiver.rock.setVisible(true);
						receiver.paper.setVisible(true);
						receiver.bet = true;
						receiver.mybet = 0;
						receiver.theotherbet = 0;
					}else if(Integer.parseInt(messageText)==2){
						receiver.updateGUI(" !!!! You lose !!!!" );
					}
					else if(Integer.parseInt(messageText)==3){
						receiver.updateGUI(" !!!! You win !!!!" );
					}
				}else if(receiver.mybet==2){
					if(Integer.parseInt(messageText)==1){
						receiver.updateGUI(" !!!! You win !!!!" );
					}else if(Integer.parseInt(messageText)==2){
						receiver.updateGUI(" !!!! t i e !!!!" );
						receiver.scissors.setVisible(true);
						receiver.rock.setVisible(true);
						receiver.paper.setVisible(true);
						receiver.bet = true;
						receiver.mybet = 0;
						receiver.theotherbet = 0;
					}else if(Integer.parseInt(messageText)==3){
						receiver.updateGUI(" !!!! You lose !!!!" );
					}
				}else if(receiver.mybet==3){
					if(Integer.parseInt(messageText)==1){
						receiver.updateGUI(" !!!! You lose !!!!" );
					}else if(Integer.parseInt(messageText)==2){
						receiver.updateGUI(" !!!! You win !!!!" );
					}else if(Integer.parseInt(messageText)==3){
						receiver.updateGUI(" !!!! t i e !!!!" );
						receiver.scissors.setVisible(true);
						receiver.rock.setVisible(true);
						receiver.paper.setVisible(true);
						receiver.bet = true;
						receiver.mybet = 0;
						receiver.theotherbet = 0;
					}
				}else{
					receiver.updateGUI( from +" is waiting for your bet.." );
				}
			} 
			else { 
				// conversation does not exist
				Rpsgame newRpsgame =
						new Rpsgame( from, clientStatus, this );
				newRpsgame.waiting = false;
				newRpsgame.theotherbet = Integer.parseInt(messageText);
				newRpsgame.updateGUI( from +" is waiting for your bet.." );
			}
		} 
	}
	public int findConversationIndex( String userName ) 
	{
		// find index of specified Conversation
		// in Vector conversations
		// if no corresponding Conversation is found, return -1
		for ( int i = 0; i < conversations.size(); i++ ) {
			Chatroom current = 
					( Chatroom ) conversations.elementAt( i );

			if ( current.getTarget().equals( userName ) ) 
				return i;	
		}

		return -1;
	}
	public int findGameIndex( String userName ) 
	{
		// find index of specified Conversation
		// in Vector conversations
		// if no corresponding Conversation is found, return -1
		for ( int i = 0; i < games.size(); i++ ) {

			Rpsgame currentgame = 
					( Rpsgame ) games.elementAt( i );

			if ( currentgame.getTarget().equals( userName ) ) 
				return i;
		}

		return -1;
	}

	public void addConversation( Chatroom newConversation ) 
	{
		conversations.add( newConversation );
	}
	public void addGame( Rpsgame newGame ) 
	{
		games.add( newGame );
	}

	public void removeConversation( String userName ) 
	{
		conversations.removeElementAt(
				findConversationIndex( userName ) );
	}
	public void removeGame( String userName ) 
	{
		games.removeElementAt(
				findGameIndex( userName ) );
	}

	public void send( Document message )
	{
		try {

			// write to output stream          
			//( ( XmlDocument ) message).write( output );
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer serializer = transformerFactory.newTransformer();
			serializer.transform( new DOMSource (message), new StreamResult(output));

		}
		//catch ( IOException e ) {
		catch ( Exception e ) {
			e.printStackTrace();
		}
	}  
	public static void main(String[] args) {
		new Login();

	}
}

