import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Calendar;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.swing.JLabel;

public class Chatroom extends JFrame {

	private ClientStatus clientStatus;
	private Login client;

	private JPanel contentPane;
	private JTextArea display;
	private JScrollPane jscrollPane;
	private JPanel messageArea;
	private JTextField message;
	private JLabel sendbtn;
	private JLabel gamebtn;
	private String target;


	public Chatroom(String contact, ClientStatus cs,
			Login mc) {
      super( cs.getUser() + "'s conversation with " + contact );
      target = contact;
      clientStatus = cs;
      client = mc;
		setBounds(0, 0, 550, 350);
		contentPane = new JPanel();	
		contentPane.setLayout(null);
		setContentPane(contentPane);

		display = new JTextArea();
		display.setBackground(Color.YELLOW);
		display.setBounds(0, 0, 550, 300);
		contentPane.add(display);

		jscrollPane = new JScrollPane(display);
		jscrollPane.setBounds(0, 0, 550, 300);
		contentPane.add(jscrollPane);
		messageArea = new JPanel();
		messageArea.setBounds(0, 300, 550, 25);
		messageArea.setBackground(Color.YELLOW);
		messageArea.setLayout(null);
		message = new JTextField( 35);
		message.setBounds(-5, -5, 450, 35);
		message.setText( "" );
		messageArea.add( message );
		contentPane.add(messageArea);

		sendbtn = new JLabel("");
		sendbtn.setIcon(new ImageIcon(Login.class.getResource("/images/send.png")));
		sendbtn.setBounds(445, -5, 50, 35);
		messageArea.add(sendbtn);

		gamebtn = new JLabel("");
		gamebtn.setIcon(new ImageIcon(Login.class.getResource("/images/gameicon.png")));
		gamebtn.setBounds(495, -5, 50, 35);
		messageArea.add(gamebtn);
		
		client.addConversation( this );
		setVisible(true);

		addWindowListener(
				new WindowAdapter() {
					public void windowClosing( WindowEvent e ) {

						// remove conversation from client's 
						// conversations Vector
						client.removeConversation( target );
					}
				}
				);

		message.addActionListener (
				new ActionListener () {
					public void actionPerformed ( ActionEvent e ) {
						submitMessage();
					}
				}
				);
		sendbtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e){
				sendbtn.setIcon(new ImageIcon(Login.class.getResource("/images/sendentered.png")));
				sendbtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
			}
			@Override
			public void mouseExited(MouseEvent e){
				sendbtn.setIcon(new ImageIcon(Login.class.getResource("/images/send.png")));
				sendbtn.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
			@Override
			public void mousePressed(MouseEvent e){
				submitMessage();

			}

		});
		gamebtn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e){
				gamebtn.setIcon(new ImageIcon(Login.class.getResource("/images/gameicon.png")));
				gamebtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
			}
			@Override
			public void mouseExited(MouseEvent e){
				gamebtn.setIcon(new ImageIcon(Login.class.getResource("/images/gameiconentered.png")));
				gamebtn.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
			@Override
			public void mousePressed(MouseEvent e){
				new Rpsgame(target,	clientStatus, client);
			}

		});

	}
	public String getTarget() 
	{
		return target;
	}

	public void disableConversation() 
	{
		message.setEnabled( false );
		sendbtn.setEnabled( false );
		gamebtn.setEnabled( false );
	}

	public void updateGUI( String dialog )
	{
		display.append( dialog + "\n" );
	}
	public void submitMessage()
	{
		String messageToSend = message.getText();

		// do nothing if the user has not typed a message
		if ( !messageToSend.equals( "" ) ) {

			Document sendMessage;
			DocumentBuilderFactory factory =
					DocumentBuilderFactory.newInstance();

			try {

				Calendar cal = Calendar.getInstance();
				String dateString, timeString;
				dateString = String.format("%04d-%02d-%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
				timeString = String.format("%02d:%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));

				// get DocumentBuilder
				DocumentBuilder builder = 
						factory.newDocumentBuilder();

				// create xml message
				sendMessage = builder.newDocument();
				Element root = sendMessage.createElement( "message" );

				root.setAttribute( "to", target );
				root.setAttribute( "from", clientStatus.getUser() );
				root.setAttribute("time", dateString + " " + timeString);
				root.appendChild( 
						sendMessage.createTextNode( messageToSend ) );
				sendMessage.appendChild( root );

				client.send( sendMessage );

				updateGUI( clientStatus.getUser() +
						":  " + messageToSend + " (" +dateString + " " + timeString + ")");
				message.setText( "" );
			} 
			catch ( ParserConfigurationException pce ) {
				pce.printStackTrace();
			}
		}
	}
}
