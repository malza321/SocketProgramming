import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.swing.JScrollPane;
import java.awt.FlowLayout;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import javax.swing.JTextArea;
import javax.swing.JScrollBar;
import java.awt.Color;

public class MainServer extends JFrame {

	private JPanel contentPane;
	private Vector onlineUsers;
	private DocumentBuilderFactory factory;
	private DocumentBuilder builder;
	private Document users;
	private JTextArea display;
	private JScrollPane jscrollPane;

	public static void main(String[] args) {
		new MainServer();
	}


	public MainServer() {
		setBackground(Color.WHITE);
		try {

			// obtain the default parser
			factory = DocumentBuilderFactory.newInstance();

			// get DocumentBuilder
			builder = factory.newDocumentBuilder();
		} 
		catch ( ParserConfigurationException pce ) {
			pce.printStackTrace();
		}
		setAlwaysOnTop(true);
		setTitle("Serverlog");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 550, 300);
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		setContentPane(contentPane);
		contentPane.setLayout(null);

		display = new JTextArea();
		display.setBackground(Color.YELLOW);
		display.setBounds(0, 0, 550, 300);
		contentPane.add(display);

		jscrollPane = new JScrollPane(display);
		jscrollPane.setBounds(0, 0, 550, 300);
		contentPane.add(jscrollPane);
		display.append( "Server is waiting for connections\n" );

		onlineUsers = new Vector();
		users = initUsers();
		setVisible(true);
		runServer();
	}
	public void runServer()
	{
		ServerSocket server;

		try {
			// create a ServerSocket
			server = new ServerSocket( 5000, 100 );

			// wait for connections
			while ( true ) {
				Socket clientSocket = server.accept();

				display.append( "\nConnection received from: " +
						clientSocket.getInetAddress().getHostName() );

				UserThread newUser = 
						new UserThread( clientSocket, this );

				newUser.start();
			}
		} 
		catch ( IOException e ) {
			e.printStackTrace();
			System.exit( 1 );
		}
	}
	private Document initUsers()
	{
		// initialize users xml document with root element users
		Document init = builder.newDocument();

		init.appendChild( init.createElement( "users" ) );
		return init;
	}
	public void updateGUI( String s )
	{
		display.append( "\n" + s );
	}

	public Document getUsers()
	{
		return users;
	}
	public void addUser( UserThread newUserThread ) 
	{
		// get new user's name
		String userName = newUserThread.getUsername();

		updateGUI( "Received new user: " + userName );

		// notify all users of user's login
		updateUsers( userName, "login" );

		// add new user element to Document users
		Element usersRoot = users.getDocumentElement();
		Element newUser = users.createElement( "user" );

		newUser.appendChild( 
				users.createTextNode( userName ) );
		usersRoot.appendChild( newUser );

		updateGUI( "Added user: " + userName );

		// add to Vector onlineUsers
		onlineUsers.addElement( newUserThread );
	}
	public void sendMessage( Document message )
	{
		// transfer message to specified receiver 
		Element root = message.getDocumentElement();
		String from = root.getAttribute( "from" );
		String to = root.getAttribute( "to" );
		String time = root.getAttribute("time");
		int index = findUserIndex( to );

		updateGUI( "Received message To: " + to + ",  From: " + from + ", time :" + time );

		// send message to corresponding user
		UserThread receiver = 
				( UserThread ) onlineUsers.elementAt( index );
		receiver.send( message );
		updateGUI( "Sent message To: " + to +
				",  From: " + from + ", time :" + time);
	}
	
	public void sendGameinfo( Document message )
	{
		// transfer message to specified receiver 
		Element root = message.getDocumentElement();
		String from = root.getAttribute( "from" );
		String to = root.getAttribute( "to" );
		int index1 = findUserIndex( to );
		int index2 = findUserIndex( from );

		updateGUI( "Sent game info To player1 : " + to + ",palyer2 :" + from );

		// send message to corresponding user
		UserThread player1 = 
				( UserThread ) onlineUsers.elementAt( index1 );
		UserThread player2 = 
				( UserThread ) onlineUsers.elementAt( index2 );
		player1.send( message );
		player2.send( message );

	}

	public void updateUsers( String userName, String type )
	{
		// create xml update document
		Document doc = builder.newDocument();
		Element root = doc.createElement( "update" );
		Element userElt = doc.createElement( "user" );

		doc.appendChild( root );
		root.setAttribute( "type", type );
		root.appendChild( userElt );
		userElt.appendChild( doc.createTextNode( userName ) );

		// send to all users
		for ( int i = 0; i < onlineUsers.size(); i++ ) {
			UserThread receiver = 
					( UserThread ) onlineUsers.elementAt( i );
			receiver.send( doc );
		}

		updateGUI( "Notified online users of " + 
				userName + "'s " + type );
	}

	public int findUserIndex( String userName )
	{
		// find index of specified UserThread in Vector onlineUsers
		// return -1 if no corresponding UserThread is found
		for ( int i = 0; i < onlineUsers.size(); i++ ) {
			UserThread current = 
					( UserThread ) onlineUsers.elementAt( i );

			if ( current.getUsername().equals( userName ) ) 
				return i;
		}

		return -1;
	}
	public void removeUser( String userName )
	{
		// remove user from Vector onlineUsers
		int index = findUserIndex( userName );

		onlineUsers.removeElementAt( index );

		// remove this user's element from Document users
		NodeList userElements =
				users.getDocumentElement().getElementsByTagName(
						"user" );

		for ( int i = 0; i < userElements.getLength(); i++ ) {
			String str = 
					userElements.item( i ).getFirstChild().getNodeValue();

			if ( str.equals( userName ) )
				users.getDocumentElement().removeChild( 
						userElements.item( i ) );

		}

		updateGUI( "Removed user: " + userName );

		// update all users of user's logout
		updateUsers( userName, "logout" );
	}
}

