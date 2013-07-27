/*
 *  Roommate
 *  Copyright (C) 2012,2013 Roommate Team (info@roommateapp.info)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/* package */
package roommateapp.info.net;

/* imports */
import java.security.MessageDigest;

/**
 * The Crypto helper class provides methods to hash
 * and convert the username for connecting to the
 * Roommate webservice.
 */
public class CryptoHelper {

	
	/**
	 * Convert a byte array to a hex string.
	 * 
	 * @param data
	 * @return
	 */
	private static String convertToHex(byte[] data) {
		
		StringBuilder buf = new StringBuilder();
		for (byte b : data) {
			int halfbyte = (b >>> 4) & 0x0F;
			int two_halfs = 0;
			do {
				buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte)
						: (char) ('a' + (halfbyte - 10)));
				halfbyte = b & 0x0F;
			} while (two_halfs++ < 1);
		}
		return buf.toString();
	}

	/**
	 * Creates a secure hash with the SHA-1 algorithm.
	 * 
	 * @param text
	 * @return
	 */
	private static String SHA1(String text) {
		
		String result = "";
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			md.update(text.getBytes("iso-8859-1"), 0, text.length());
			byte[] sha1hash = md.digest();
			result = convertToHex(sha1hash);
		} catch (Exception e) {

			result = "";
		}
		return result;
	}

	/**
	 * This method encapsulates the transforming proces, 
	 * so that it can be easily changed if nescessary.
	 * 
	 * @param username String that should be hashed
	 * 
	 * @return hashed String
	 */
	public static String transformUsername(String username){
		
		return SHA1(username);
	}
}