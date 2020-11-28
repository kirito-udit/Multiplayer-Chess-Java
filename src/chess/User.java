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
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.ArrayList;

public class User {
	
	public String Username;
	public String PasswordHash;
	public Socket Socket;
	public int ServerPort;
	public String IpAddress;
	public BufferedReader input;
	public DataOutputStream output;
	public Timestamp Created;

	public User(String username, String password, String datetime) {
		Username = username;
		PasswordHash = password;
		Created = Timestamp.valueOf(datetime);
	}
	public User(String username, String password, Timestamp datetime) {
		Username = username;
		PasswordHash = password;
		Created = datetime;
	}
	public User(String[] data) {
		Username = data[0];
		PasswordHash = data[1];
		Created = Timestamp.valueOf(data[2]);
	}
	public User(String uname, int port, String ip) {
		Username = uname;
		IpAddress=ip;
		try {
			Socket = new Socket(ip, port);
			//Initialize the data streams
			input = new BufferedReader(new InputStreamReader(Socket.getInputStream()));
			output = new DataOutputStream(Socket.getOutputStream());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public User(String uname, Socket s) {
		Username = uname;
		Socket = s;
		IpAddress = Socket.getLocalAddress().toString().replace("/","");
		//Initialize the data streams
		try {
			input = new BufferedReader(new InputStreamReader(Socket.getInputStream()));
			output = new DataOutputStream(Socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	public String toString() {
		return Username + "," + PasswordHash + "," + Created.toString() + "\n";
	}
	
	
	public static void AddUser(User user) {
		File file = new File("res\\User.csv");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new FileWriter(file.getAbsoluteFile(), true));
			bw.write(user.toString());
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static User GetUser(String username) {
		ArrayList<User> users = ReadUsers("res\\User.csv");
		for(User user : users) {
			if(user.Username.equals(username)) {
				return user;
			}
		}
		return null;
	}
	
	
    public static ArrayList<User> ReadUsers(String filePath) {
        String line = "";
        final String cvsSplitBy = ",";
        ArrayList<User> users = new ArrayList<User>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            while ((line = br.readLine()) != null) {
                users.add(new User(line.split(cvsSplitBy)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }
    
    public static String IpTranslation(String address) {
    	if(address.toUpperCase().equals("LOCALHOST")) {
    		return "127.0.0.1";
    	}
    	return address;
    }
}