// Inspirations and some code taken from http://blog.jerryorr.com/2012/05/secure-password-storage-lots-of-donts.html
package chat.server;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class Account {
	
	private String userName;
	private byte[] encryptedPassword;
	private byte[] salt;
	private ArrayList<String> ownedRooms = new ArrayList<String>();
	
	public Account(String userName, String password) throws NoSuchAlgorithmException, InvalidKeySpecException{
		this.userName = userName;
		this.setPassword(password);
	}
	
	private void setPassword(String password) throws NoSuchAlgorithmException, InvalidKeySpecException{
		salt = generateSalt();
		encryptedPassword = getEncryptedPassword(password);
		return;
	}
	
	public boolean authenticate(String attemptedPassword) {
		byte[] encryptedAttemptedPassword;
		try {
			encryptedAttemptedPassword = getEncryptedPassword(attemptedPassword);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			e.printStackTrace();
			return false;
		}
		 return Arrays.equals(encryptedPassword, encryptedAttemptedPassword);
	}
	
	private byte[] getEncryptedPassword(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
	  String algorithm = "PBKDF2WithHmacSHA1";
	  int derivedKeyLength = 160;
	  int iterations = 20000;
	  KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, derivedKeyLength);
	  SecretKeyFactory f = SecretKeyFactory.getInstance(algorithm);
	  return f.generateSecret(spec).getEncoded();
	 }
	
	
	public byte[] generateSalt() throws NoSuchAlgorithmException {
	  SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
	  byte[] salt = new byte[8];
	  random.nextBytes(salt);
	  return salt;
	 }
	
	public void addRoomOwnership(String roomID){
		ownedRooms.add(roomID);
		ChatRoom room = ConnectionsSupervisor.getChatRoomByID(roomID);
		room.setOwnedByAccount(true);
	}
	
	public void removeRoomOwnership(String roomID){
		ownedRooms.remove(roomID);
		ChatRoom room = ConnectionsSupervisor.getChatRoomByID(roomID);
		room.setOwnedByAccount(false);
	}

	public String getUsername() {
		return userName;
	}
	
	public String[] getOwnedRooms(){
		String[] tmp = new String[ownedRooms.size()];
		return ownedRooms.toArray(tmp);
	}

}
