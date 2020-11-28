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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class PasswordHash {
	
	
	public static String Hash(String password) {
		String salt = generateSalt();
		String hashedPassword = generateHash(salt + password);
		return hashedPassword + ":" + salt;
	}
	
	
	public static boolean Compare(String passwordHash, String password) {
		String data[] = passwordHash.split(":");
		return (generateHash(data[1] + password).equals(data[0]));
	}
	
	
	public static String generateSalt() {
		final int saltLength = 50;
		Random r = new Random();
		String salt = "";
		for(short i=0; i<saltLength; i++) {
			byte id = (byte)Math.abs(r.nextInt()%36);
			id+=48;
			if(id>57) {
				id+=7;
			}
			salt += (char)id;
		}
		return salt;
	}
	
	public static String generateHash(String input) {
		StringBuilder hash = new StringBuilder();
		try {
			MessageDigest sha = MessageDigest.getInstance("SHA-512");
			byte[] hashedBytes = sha.digest(input.getBytes());
			char[] digits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
					'a', 'b', 'c', 'd', 'e', 'f' };
			for (int idx = 0; idx < hashedBytes.length; ++idx) {
				byte b = hashedBytes[idx];
				hash.append(digits[(b & 0xf0) >> 4]);
				hash.append(digits[b & 0x0f]);
			}
		} catch (NoSuchAlgorithmException e) {
		}
		return hash.toString();
	}
	
}