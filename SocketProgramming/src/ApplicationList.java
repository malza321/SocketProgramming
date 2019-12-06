import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.FlowLayout;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Color;
import java.awt.Cursor;

public class ApplicationList extends JFrame {

	private JPanel contentPane;
	private JLabel applist;
	private JLabel Chatbutton;
	private JLabel Gamebutton;

	public ApplicationList(String contact, ClientStatus cs,
            Login mc) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 200);
		contentPane = new JPanel();
		contentPane.setBackground(Color.YELLOW);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		applist = new JLabel("Application List");
		applist.setFont(new Font("Lucida Grande", Font.BOLD, 40));
		applist.setBounds(60, 5, 400, 40);
		contentPane.add(applist);
		
		Chatbutton = new JLabel(new ImageIcon(Login.class.getResource("/images/Chatbutton.png")));
		Chatbutton.setBounds(5, 10, 200, 200);
		contentPane.add(Chatbutton);
		
		Gamebutton = new JLabel(new ImageIcon(Login.class.getResource("/images/Gamebutton.png")));
		Gamebutton.setBounds(230, 10, 200, 200);
		contentPane.add(Gamebutton);
		
		Chatbutton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e){
				Chatbutton.setIcon(new ImageIcon(Login.class.getResource("/images/Chatbuttonentered.png")));
				Chatbutton.setCursor(new Cursor(Cursor.HAND_CURSOR));
			}
			@Override
			public void mouseExited(MouseEvent e){
				Chatbutton.setIcon(new ImageIcon(Login.class.getResource("/images/Chatbutton.png")));
				Chatbutton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
			@Override
			public void mousePressed(MouseEvent e){
				new Chatroom( contact, cs, mc );
				setVisible(false);
			}

		});
		Gamebutton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e){
				Gamebutton.setIcon(new ImageIcon(Login.class.getResource("/images/Gamebuttonentered.png")));
				Gamebutton.setCursor(new Cursor(Cursor.HAND_CURSOR));
			}
			@Override
			public void mouseExited(MouseEvent e){
				Gamebutton.setIcon(new ImageIcon(Login.class.getResource("/images/Gamebutton.png")));
				Gamebutton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
			@Override
			public void mousePressed(MouseEvent e){
				new Rpsgame(contact, cs, mc);
				setVisible(false);
			}

		});
		setVisible(true);
	}
	
}
