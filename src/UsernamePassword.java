/*
 *
 * NWEN 304 : Dictionary.
 *
 * Supports put(key, value) and get (key).
 *
 * Saves the contents of the Dictionary into a file in the local file system.
 *
 */

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Johns code for usernames and passwords
 * @author John G
 *
 */
public class UsernamePassword {
	private  HashMap<String, Object> map = new HashMap<String, Object>();

	private  HashMap<String, byte[]> usernamesPasswords = new HashMap<String, byte[]>();

	private  String USERNAMES = "USERNAMES.TXT";

	private File f;

	private static UsernamePassword usernameService;

	public static UsernamePassword getUsernameService(){
		if(usernameService == null)	usernameService = new UsernamePassword();
		return usernameService;
	}


	public UsernamePassword() {

		// check if exists, if it doesn't create it
		try {
			f = new File(USERNAMES);
			if (!f.exists()) {
				f.createNewFile();
				writeMap();
			}
		} catch (IOException ex) {
			System.out.println("Unable to open the file " + USERNAMES);
		}

		// reload the previous state
		try {
			readMap();
		} catch (IOException ex) {
			System.out.println("Unable to reload state");
		}
	}

//	public Set getUsernames(){
//		Set keyset;
//		if(map.keySet()!=null){
//			keyset = map.keySet();
//			return keyset;
//		}
//		return null;
//	}

	/* Put stuff in the map and serialize to disk */
	private synchronized void put(String key, byte[] value) {
		map.put(key, value);
		writeMap();
	}

	/* Store <String, String> as String, SHA1HASH */
	public boolean put(String key, String value) {
		if (map.get(key.toLowerCase()) == null) {
			put(key.toLowerCase(), hasher(value));
			return true;
		}
		else{
			System.out.println("Username already exists, consider using putNew(Username, OLD,NEW)");
			return false;
		}

	}

	public String putNew(String key, String oldPass,String newPass){
		if (map.get(key.toLowerCase()) == null) {
			System.out.println("Username not present");
			return "Username not present";
		}
		else{

			if(matchPassword(key,oldPass)){
				put(key.toLowerCase(), hasher(newPass));
				return "Success";
			}
			else{
				System.out.println("Current Password does not match supplied Password");
				return "Supplied old password does not match current password";
			}

		}
	}

	/* Retrieve hashed SHA1 in hex */
	public String getHashed(String key) {
		byte[] value = (byte[]) map.get(key.toLowerCase());

		if (value != null) {

			return getHexString(value);

		}

		return "Username not present";
	}

	public boolean matchPassword(String key, String value) {

		byte[] provided = hasher(value);
		byte[] existing = (byte[]) map.get(key.toLowerCase());
		if ((Arrays.equals(existing, provided))) {

			return true;
		}

		return false;
	}




	// Hash SHA-1 byte array
	 private byte[] hasher(String password) {
		byte[] sha1 = null;

		try {

			MessageDigest digest = MessageDigest.getInstance("SHA-1");
			digest.update(password.getBytes("utf8"));
			sha1 = digest.digest();

		} catch (NoSuchAlgorithmException e) {

			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sha1;
	}


	private String getHexString(byte[] b) {
		String result = "";
		for (int i = 0; i < b.length; i++) {
			result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
		}
		return result;
	}

	private void writeMap() {
		try {
			// use buffering
			OutputStream file = new FileOutputStream(f);
			OutputStream buffer = new BufferedOutputStream(file);
			ObjectOutput output = new ObjectOutputStream(buffer);
			try {
				output.writeObject(map);
			} finally {
				output.close();
			}
		} catch (IOException ex) {
			System.out.println("Unable to write to " + USERNAMES);
		}

	}

	private void readMap() throws IOException {
		try {
			// use buffering
			InputStream file = new FileInputStream(f);
			InputStream buffer = new BufferedInputStream(file);
			ObjectInput input = new ObjectInputStream(buffer);
			try {
				// deserialize the map
				Object readObject = input.readObject();
				map = (HashMap<String, Object>) readObject;
			} finally {
				input.close();
			}
		} catch (ClassNotFoundException ex) {
			System.out.println("Cannot perform input. Class not found.");
		} catch (IOException ex) {
			System.out.println("Cannot perform input.");
		}
	}

	// some test methods
	public static void main(String[] args) {
		// UsernamePassword usernames = new UsernamePassword();
		// int count = 0;
		//
		// // retrieve stored values for A and B (if present in the map)
		// System.out.println(dictionary.getString("A"));
		// System.out.println(dictionary.getString("B"));
		//
		// // retrieve count, initialise if it doesn't exist otherwise increment
		// count
		// count = dictionary.getInt("count");
		// if (count == INT_NOT_FOUND) {
		// System.out.println("count uninitialized");
		// count = 0;
		// } else {
		// System.out.println("Count =" + count);
		// count = count + 1;
		// }
		// dictionary.putInt("count", count);
		//
		// // store (possibly overwriting) A and B
		// dictionary.putString("A", "A-value-new");
		// dictionary.putString("B", "B-value");

	}
}
