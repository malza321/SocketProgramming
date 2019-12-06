import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Calendar;

import java.awt.Color;

public class Rpsgame extends JFrame {
	private ClientStatus clientStatus;
	private Login client;

	private JPanel contentPane;
	private JLabel Rps;
	JLabel scissors;
	JLabel rock;
	JLabel paper;
	JLabel statusms;
	boolean bet = true;
	boolean waiting = true;
	private String target;
	int mybet = 0;
	int theotherbet = 0;


	public Rpsgame(String contact, ClientStatus cs,
			Login mc) {		
		super(cs.getUser() + "'s Rpsgame with " + contact );
		target = contact;
		clientStatus = cs;
		client = mc;
		setBounds(100, 100, 450, 200);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(0, 128, 128));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		Rps = new JLabel("Rock-Paper-Scissors");
		Rps.setFont(new Font("Lucida Grande", Font.BOLD, 40));
		Rps.setBounds(0, 5, 450, 40);
		contentPane.add(Rps);

		scissors = new JLabel(new ImageIcon(Login.class.getResource("/images/Scissors.png")));
		scissors.setBounds(0, 0, 150, 200);
		contentPane.add(scissors);


		rock = new JLabel(new ImageIcon(Login.class.getResource("/images/Rock.png")));
		rock.setBounds(150, 0, 150, 200);
		contentPane.add(rock);

		paper = new JLabel(new ImageIcon(Login.class.getResource("/images/Paper.png")));
		paper.setBounds(300, 0, 150, 200);
		contentPane.add(paper);

		statusms = new JLabel("Place your bet");
		statusms.setFont(new Font("Lucida Grande", Font.BOLD, 20));
		statusms.setBounds(0, 155, 450, 20);
		contentPane.add(statusms);
		client.addGame( this );

		addWindowListener(
				new WindowAdapter() {
					public void windowClosing( WindowEvent e ) {

						// remove conversation from client's 
						// conversations Vector
						client.removeGame( target );
					}
				}
				);


		scissors.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e){
				scissors.setIcon(new ImageIcon(Login.class.getResource("/images/Scissorsentered.png")));
				scissors.setCursor(new Cursor(Cursor.HAND_CURSOR));
			}
			@Override
			public void mouseExited(MouseEvent e){
				scissors.setIcon(new ImageIcon(Login.class.getResource("/images/Scissors.png")));
				scissors.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
			@Override
			public void mousePressed(MouseEvent e){
				if(bet){
					bet = false;
					Document sendMessage;
					DocumentBuilderFactory factory =
							DocumentBuilderFactory.newInstance();
					Calendar cal = Calendar.getInstance();
					String dateString, timeString;
					dateString = String.format("%04d-%02d-%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
					timeString = String.format("%02d:%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));


					try {

						// get DocumentBuilder
						DocumentBuilder builder = 
								factory.newDocumentBuilder();

						// create xml message
						sendMessage = builder.newDocument();
						Element root = sendMessage.createElement( "gameinfo" );

						root.setAttribute( "to", target );
						root.setAttribute( "from", clientStatus.getUser() );
						root.setAttribute("time", dateString + " " + timeString);
						root.appendChild( 
								sendMessage.createTextNode( "1" ) );
						rock.setVisible(false);
						paper.setVisible(false);
						mybet = 1;
						sendMessage.appendChild( root );

						if(waiting){
							statusms.setText("waiting for result....");
						}
						if(theotherbet ==1){
							statusms.setText(" !!!! t i e !!!!");
							scissors.setVisible(true);
							rock.setVisible(true);
							paper.setVisible(true);
							bet = true;
							mybet = 0;
							theotherbet = 0;
							waiting = true;
						}else if(theotherbet == 2){
							statusms.setText(" !!!! You lose !!!!");
							theotherbet = 0;
						}else if(theotherbet == 3){
							statusms.setText(" !!!! You win !!!!");
							theotherbet = 0;
						}


						client.send( sendMessage );

					} 
					catch ( ParserConfigurationException pce ) {
						pce.printStackTrace();
					}
				}
			}

		});

		rock.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e){
				rock.setIcon(new ImageIcon(Login.class.getResource("/images/Rockentered.png")));
				rock.setCursor(new Cursor(Cursor.HAND_CURSOR));
			}
			@Override
			public void mouseExited(MouseEvent e){
				rock.setIcon(new ImageIcon(Login.class.getResource("/images/Rock.png")));
				rock.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
			@Override
			public void mousePressed(MouseEvent e){
				if(bet){
					bet = false;
					Document sendMessage;
					DocumentBuilderFactory factory =
							DocumentBuilderFactory.newInstance();

					Calendar cal = Calendar.getInstance();
					String dateString, timeString;
					dateString = String.format("%04d-%02d-%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
					timeString = String.format("%02d:%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));

					try {

						// get DocumentBuilder
						DocumentBuilder builder = 
								factory.newDocumentBuilder();

						// create xml message
						sendMessage = builder.newDocument();
						Element root = sendMessage.createElement( "gameinfo" );

						root.setAttribute( "to", target );
						root.setAttribute( "from", clientStatus.getUser() );
						root.setAttribute("time", dateString + " " + timeString);
						root.appendChild( 
								sendMessage.createTextNode( "2" ) );
						scissors.setVisible(false);
						paper.setVisible(false);
						mybet = 2;
						sendMessage.appendChild( root );

						if(waiting){
							statusms.setText("waiting for result....");
						}
						if(theotherbet ==2){
							statusms.setText(" !!!! t i e !!!!");
							scissors.setVisible(true);
							rock.setVisible(true);
							paper.setVisible(true);
							bet = true;
							mybet = 0;
							theotherbet = 0;
							waiting = true;
						}else if(theotherbet == 3){
							statusms.setText(" !!!! You lose !!!!");
							theotherbet = 0;
						}else if(theotherbet == 1){
							statusms.setText(" !!!! You win !!!!");
							theotherbet = 0;
						}



						client.send( sendMessage );

					} 
					catch ( ParserConfigurationException pce ) {
						pce.printStackTrace();
					}
				}
			}

		});

		paper.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e){
				paper.setIcon(new ImageIcon(Login.class.getResource("/images/Paperentered.png")));
				paper.setCursor(new Cursor(Cursor.HAND_CURSOR));
			}
			@Override
			public void mouseExited(MouseEvent e){
				paper.setIcon(new ImageIcon(Login.class.getResource("/images/Paper.png")));
				paper.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
			@Override
			public void mousePressed(MouseEvent e){
				if(bet){
					bet = false;
					Document sendMessage;
					DocumentBuilderFactory factory =
							DocumentBuilderFactory.newInstance();
					Calendar cal = Calendar.getInstance();
					String dateString, timeString;
					dateString = String.format("%04d-%02d-%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
					timeString = String.format("%02d:%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));

					try {

						// get DocumentBuilder
						DocumentBuilder builder = 
								factory.newDocumentBuilder();

						// create xml message
						sendMessage = builder.newDocument();
						Element root = sendMessage.createElement( "gameinfo" );

						root.setAttribute( "to", target );
						root.setAttribute( "from", clientStatus.getUser() );
						root.setAttribute("time", dateString + " " + timeString);
						root.appendChild( 
								sendMessage.createTextNode( "3" ) );
						scissors.setVisible(false);
						rock.setVisible(false);
						mybet = 3;
						sendMessage.appendChild( root );

						if(waiting){
							statusms.setText("waiting for result....");
						}
						if(theotherbet == 3){
							statusms.setText(" !!!! t i e !!!!");
							scissors.setVisible(true);
							rock.setVisible(true);
							paper.setVisible(true);
							bet = true;	
							mybet = 0;
							theotherbet = 0;
							waiting = true;
						}else if(theotherbet == 1){
							statusms.setText(" !!!! You lose !!!!");
							theotherbet = 0;
						}else if(theotherbet == 2){
							statusms.setText(" !!!! You win !!!!");
							theotherbet = 0;
						}


						client.send( sendMessage );

					} 
					catch ( ParserConfigurationException pce ) {
						pce.printStackTrace();
					}
				}
			}

		});
		setVisible(true);

	}
	public String getTarget() 
	{
		return target;
	}

	public void updateGUI( String dialog )
	{
		statusms.setText( dialog );
	}

}
