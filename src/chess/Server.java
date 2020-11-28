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
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.ArrayList;


public class Server implements Runnable {
	
	
	////Global Variables
	/**The port which the server listens on for commands.*/
	public static final int PORT = 9001;
	/**A collection of all the players online*/
	private static ArrayList<User> users = new ArrayList<User>();
	
	////Class Variables
	/**An instance of a connection between a client.*/
	private Socket socket;
	/**An input stream of data from a client*/
	private BufferedReader input;
	/**An output stream of data to a client*/
	private DataOutputStream output;
	
	
	public static void main(String[] args) {
        ServerSocket server;
		try {
			//Listen for connections and continually accept new connections and handle them accordingly.
			server = new ServerSocket(PORT);
			while(true) {
				Thread t = new Thread(new Server(server.accept()));
				t.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * An instance of a connection and conversation between remote client chess software and a the server.
	 * @param s the open socket connection that was accepted in the main thread.
	 */
	public Server(Socket s) {
		socket = s;
		try {
			//Initialize the data streams
			input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			output = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * The main listen and respond loop for the connection
	 */
	public void run() {
		try {
			String command = "";
			while(!command.contains("LOGOUT")) {
					command = input.readLine();
		            System.out.println("> " + command);
					String response = Server.ParseCommand(command, socket.getInetAddress().toString().replace("/",""), socket.getPort());
		            System.out.println("< " + response);
					output.writeBytes(response + "\n");
			}
			input.close();
			output.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Take input command streams and splice them by delimiter and perform various commands
	 * @param command the input data from the client as a string
	 * @param address the string representation of the client ip
	 * @param port the remote port of the connecting client
	 * @return a response string that will be sent back to the client
	 */
	public static String ParseCommand(String command, String address, int port) {
		String data[] = command.split("\\|\\|");
		switch(data[0]) {
		// REGISTER||Username||Password||
		case "REGISTER":
			//Save a new user with the given data if one does not already exist.
			User user = new User(data[1],PasswordHash.Hash(data[2]), new Timestamp(System.currentTimeMillis()));
			if(User.GetUser(user.Username)==null) {
				User.AddUser(user);
				return "OK";
			}
			return "Username Already Exists";
		// LOGIN||Username||Password||
		case "LOGIN":
			//Verify that a user has proper credentials and add them to the list of online players.
			user = User.GetUser(data[1]);
			if(user!=null) {
				if(PasswordHash.Compare(user.PasswordHash, data[2])) {
					user.ServerPort = Integer.parseInt(data[3]);
					user.IpAddress = address;
					users.add(user);
					return "OK||" + port + "||";
				} else {
					return "Password Incorrect";
				}
			}
			return "Username Does Not Exist";
		// GET_LIST||Username||
		case "GET_LIST":
			//Return a list of all the players online except the querying username.
			String onlinePlayers = "";
			for(User player : users) {
				if(!player.Username.equals(data[1])) {
					onlinePlayers += player.Username + "||";
				}
			}
			onlinePlayers += "Refresh||";
			return onlinePlayers;
		// GET_USER||Username||
		case "GET_USER":
			//Get information on a specific user such as their address and port number.
			user = null;
			for(User player : users) {
				if(player.Username.equals(data[1])) {
					user = player;
				}
			}
			if(user!=null) {
				return user.Username + "||" + 
						user.ServerPort + "||" + 
						user.IpAddress + "||";
			}
			return "No Valid User";
		// LOGOUT||Username||
		case "LOGOUT":
			//Remove a user from the list of online players
			user = null;
			for(User player : users) {
				if(player.Username.equals(data[1])) {
					user = player;
				}
			}
			if(user!=null) {
				users.remove(user);
				return "OK";
			}
			return "Logout Failure";
		// VERIFY||
		case "VERIFY":
			//Used to determine if this is a valid chess server.
			return "CONNECTION_PROCEED||";
		}
		return "Unknown Command Error";
	}
	
	/**
	 * Send a message on an output data stream and then listen for a response back.
	 * @param input the input stream reader
	 * @param output an output stream writer
	 * @param data the data to be sent over the wire.
	 * @return the response from the Chess server
	 */
	public static String SendMessage(BufferedReader input, DataOutputStream output, String data) {
		try {
			output.writeBytes(data + "\n");
			String response = input.readLine();
			return response;
		} catch (IOException e) {
			e.printStackTrace();
		}
        return null;
	}
	
	
	/**
	 * Convert a byte array to a string.
	 * @param buffer an input array of characters
	 * @return the string represented and ended by a null byte
	 */
	public static String bufferToString(byte[] buffer) {
		short i=0;
		String result = "";
		while(buffer[i]!=0) {
			result += (char)buffer[i];
			i++;
		}
		return new String(result);
	}
	
}
