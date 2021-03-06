// Inspirations and some code taken from http://blog.jerryorr.com/2012/05/secure-password-storage-lots-of-donts.html
package chat.server;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class Account {
	
	private String userName;
	private byte[] encryptedPassword;
	private byte[] salt;
	private Set<String> ownedRooms = new HashSet<String>();
	private int failedLoginAttempts = 0;
	private final int MAXLOGIN = 3;
	private static final int PASSWORDLENGTH = 8;
	
	
	public static byte[] generateSalt() throws NoSuchAlgorithmException {
		  SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
		  byte[] salt = new byte[8];
		  random.nextBytes(salt);
		  return salt;
	}
	
	public static boolean validPassword(String password){
		  return (PASSWORDLENGTH <= password.length());
	}
	
	public Account(String userName, String password) throws NoSuchAlgorithmException, InvalidKeySpecException{
		this.userName = userName;
		this.setPassword(password);
		ConnectionsSupervisor.addAccount(this);
	}
	
	private void setPassword(String password) throws NoSuchAlgorithmException, InvalidKeySpecException{
		salt = generateSalt();
		encryptedPassword = getEncryptedPassword(password);
		return;
	}
	
	public boolean authenticate(String attemptedPassword) {
		if (failedLoginAttempts >= MAXLOGIN){
			failedLoginAttempts++;
			return false;
		}
		byte[] encryptedAttemptedPassword;
		try {
			encryptedAttemptedPassword = getEncryptedPassword(attemptedPassword);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			e.printStackTrace();
			return false;
		}
		 if (Arrays.equals(encryptedPassword, encryptedAttemptedPassword)){
			 failedLoginAttempts = 0;
			 return true;
		 } else {
		 	failedLoginAttempts++;
			 return false;
		 }
	}
	
	private byte[] getEncryptedPassword(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
	  String algorithm = "PBKDF2WithHmacSHA1";
	  int derivedKeyLength = 160;
	  int iterations = 20000;
	  KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, derivedKeyLength);
	  SecretKeyFactory f = SecretKeyFactory.getInstance(algorithm);
	  return f.generateSecret(spec).getEncoded();
	 }
	
	public void addRoomOwnership(String roomID){
		if(ownedRooms.add(roomID)){
			ChatRoom room = ConnectionsSupervisor.getChatRoomByID(roomID);
			if (room != null)
				room.setOwnedByAccount(true);
		}
	}
	
	public void removeRoomOwnership(String roomID){
		if(ownedRooms.remove(roomID)){
			ChatRoom room = ConnectionsSupervisor.getChatRoomByID(roomID);
			if (room != null)
				room.setOwnedByAccount(false);
		}
	}

	public String getUsername() {
		return userName;
	}
	
	public String[] getOwnedRooms(){
		String[] tmp = new String[ownedRooms.size()];
		return ownedRooms.toArray(tmp);
	}

	public int getFailedLoginAttempts() {
		return failedLoginAttempts;
	}

	public int getMAXLOGIN() {
		return MAXLOGIN;
	}
	
	
}
