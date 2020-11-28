/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chess;

/**
 *
 * @author Uditk
 */

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Timestamp;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;


public class Account extends JFrame implements ActionListener {
	
	
	private static final long serialVersionUID = 5744889896837508130L;
	/**A collection of JTextfields for data input.*/
	private static JTextField textFields[] = {new JTextField(15),new JPasswordField(15),new JTextField(15),new JPasswordField(15)};
	/**A collection of JButtons for screen input.*/
	private static JButton buttons[] = {new JButton("Login"),new JButton("Register")};
	/**An input ip address that is the destination of a running chess server.*/
	public static String Ip;
	/**An open socket with the chess server. The connection stays open until logout.*/
	public static Socket socket;
	public static BufferedReader input;
	public static DataOutputStream output;
	public static ServerSocket server;
	
	public static void main(String args[]){
	    do {
	    	//Get the server ip.
	    	Ip = JOptionPane.showInputDialog(null, "Input a chess server ip to connect to.");
			try {
		    	if(Ip != null) {
					socket = new Socket(Ip, Server.PORT);
					//Get the data streams to and from the server.
					input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					output = new DataOutputStream(socket.getOutputStream());
			    	String response [] = Server.SendMessage(input, output, "VERIFY||").split("\\|\\|");
					//If valid proceed to login prompt
			    	if(response[0].equals("CONNECTION_PROCEED")) {
		    			server = new ServerSocket(0);
		    			new Account();
		    			break;
		    		}else {
		    			JOptionPane.showMessageDialog(null, "Not a valid chess server");
		    		}
		    	}
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
	    } while(Ip!=null);
	}
	
	public Account() {
		setVisible(true);
		setTitle("Account login");
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		
		JTabbedPane pane = new JTabbedPane();
		
		Container UserPass = new Container();
		SpringLayout layout = new SpringLayout();
		UserPass.setLayout(layout);
		
		JLabel UserLabel = new JLabel("Username: ");
		UserLabel.setFont(new Font(getFont().getFontName(),Font.PLAIN,20));
		UserPass.add(UserLabel);
		layout.putConstraint(SpringLayout.WEST, UserLabel, 60, SpringLayout.WEST, UserPass);
		layout.putConstraint(SpringLayout.NORTH, UserLabel, 35, SpringLayout.NORTH, UserPass);
		JTextField Username = textFields[0];
		UserPass.add(Username);
		layout.putConstraint(SpringLayout.WEST, Username, 10, SpringLayout.EAST, UserLabel);
		layout.putConstraint(SpringLayout.NORTH, Username, 40, SpringLayout.NORTH, UserPass);
		JLabel PWLabel = new JLabel("Password: ");
		PWLabel.setFont(new Font(getFont().getFontName(),Font.PLAIN,20));
		UserPass.add(PWLabel);
		layout.putConstraint(SpringLayout.WEST, PWLabel, 60, SpringLayout.WEST, UserPass);
		layout.putConstraint(SpringLayout.NORTH, PWLabel, 55, SpringLayout.NORTH, UserLabel);
		JPasswordField Password = (JPasswordField) textFields[1];
		UserPass.add(Password);
		layout.putConstraint(SpringLayout.WEST, Password, 10, SpringLayout.EAST, PWLabel);
		layout.putConstraint(SpringLayout.NORTH, Password, 55, SpringLayout.NORTH, Username);
		JButton SignIn = buttons[0];
		UserPass.add(SignIn);
		layout.putConstraint(SpringLayout.WEST, SignIn, 150, SpringLayout.WEST, UserPass);
		layout.putConstraint(SpringLayout.NORTH, SignIn, 45, SpringLayout.NORTH, Password);
		pane.add("Login",UserPass);
		
		Container Register = new Container();
		Register.setLayout(layout);
		
		JLabel UserLabel2 = new JLabel("Username: ");
		UserLabel2.setFont(new Font(getFont().getFontName(),Font.PLAIN,20));
		Register.add(UserLabel2);
		layout.putConstraint(SpringLayout.WEST, UserLabel2, 60, SpringLayout.WEST, Register);
		layout.putConstraint(SpringLayout.NORTH, UserLabel2, 35, SpringLayout.NORTH, Register);
		JTextField Username2 = textFields[2];
		Register.add(Username2);
		layout.putConstraint(SpringLayout.WEST, Username2, 10, SpringLayout.EAST, UserLabel2);
		layout.putConstraint(SpringLayout.NORTH, Username2, 40, SpringLayout.NORTH, Register);
		JLabel PWLabel2 = new JLabel("Password: ");
		PWLabel2.setFont(new Font(getFont().getFontName(),Font.PLAIN,20));
		Register.add(PWLabel2);
		layout.putConstraint(SpringLayout.WEST, PWLabel2, 60, SpringLayout.WEST, Register);
		layout.putConstraint(SpringLayout.NORTH, PWLabel2, 55, SpringLayout.NORTH, UserLabel2);
		JPasswordField Password2 = (JPasswordField) textFields[3];
		Register.add(Password2);
		layout.putConstraint(SpringLayout.WEST, Password2, 10, SpringLayout.EAST, PWLabel2);
		layout.putConstraint(SpringLayout.NORTH, Password2, 55, SpringLayout.NORTH, Username2);
		JButton SignUp = buttons[1];
		Register.add(SignUp);
		layout.putConstraint(SpringLayout.WEST, SignUp, 150, SpringLayout.WEST, Register);
		layout.putConstraint(SpringLayout.NORTH, SignUp, 45, SpringLayout.NORTH, Password2);
		pane.add("Register",Register);
		
		
		add(pane,BorderLayout.CENTER);
		setSize(400,250);
		for(JButton button : buttons) {
			button.addActionListener(this);
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		//Register
		if(((JButton)e.getSource())==buttons[1]) {
			String username = textFields[2].getText();
			String passphrase = textFields[3].getText();
			if(!username.isEmpty() && !passphrase.isEmpty()) {
				String response = Server.SendMessage(
						Account.input, 
						Account.output, 
						"REGISTER||"+username+"||"+passphrase+"||");
				if(response.equals("OK")) {
					JOptionPane.showMessageDialog(null, "Account Created Successfully");
				} else {
					JOptionPane.showMessageDialog(null, response);
				}
			} else {
				JOptionPane.showMessageDialog(null, "Username and Passphrase cannot be empty.");
			}
		}
		//Login
		else if(((JButton)e.getSource())==buttons[0]) {
			String username = textFields[0].getText();
			String passphrase = textFields[1].getText();
			if(!username.isEmpty() && !passphrase.isEmpty()) {
				String response[]=Server.SendMessage(
						Account.input, 
						Account.output, 
						"LOGIN||"+username+"||"+passphrase+"||"+server.getLocalPort()+"||").split("\\|\\|");
				//Validate a successful login
				if(response[0]!= null && response[0].equals("OK")) {
					User user = new User(textFields[0].getText(),textFields[1].getText(), new Timestamp(System.currentTimeMillis()));
					login(user);
				} else {
					JOptionPane.showMessageDialog(null, response);
				}
			} else {
				JOptionPane.showMessageDialog(null, "Username and Passphrase cannot be empty.");
			}
		}
	}
	
	public void login(User user) {
		setVisible(false);
		dispose();
		new Multiplayer(user, Account.server);
	}
}
