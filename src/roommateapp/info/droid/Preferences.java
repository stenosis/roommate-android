/*
 *  Roommate
 *  Copyright (C) 2012,2013 Team Roommate (info@roommateapp.info)
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
package roommateapp.info.droid;

/* imports */
import android.content.Context;

/**
 * The Preferences class is used to set all the
 * user preferences for the Roommate application.
 */
public class Preferences {
	
	// Instance variables
	private static final String EMAIL = "email";
	private static final String PW = "pw";
	private static final String VALIDUSER = "validuser";
	private static final String AUTOSYNC = "autosync";
	private static final String CHECKCLIENTUPDATE = "clientupdate";
	private static final String CHECKCLIENTUPDATEUSER = "clientupdateuserinteraction";
	private static final String DEFAULTFILE = "defaultfile";
	private static final String HOLIDAYS = "holidays";
	private static final String REMEMBER = "remember";
	private static final String ROOMMATEDIR = "roommateDir";
	
	/* Filter */
	private static final String INFOSTRING = "infostring";
	private static final String WHITEBOARD = "whiteboard";
	private static final String POWERSUPPLY = "powersupply";
	private static final String NETWORK = "network";
	private static final String PC = "pc";
	private static final String PROJEKTOR = "projektor";
	private static final String BEAMER = "beamer";
	private static final String SIZE = "size";
	private static final String CHECKED = "checked";
	
	private static String eMail; 
	private static String pw;
	private static String defaultFile;
	private static String roommateDIR;
	private static int size = PreferencesAdapter.DEFINT;
	private static int sizeCheckedItem = PreferencesAdapter.DEFINT;
	
	/**
	 * 
	 * @param context
	 * @return
	 */
	public static boolean hasValidUser(Context context) {
		
		return PreferencesAdapter.getBool(context, VALIDUSER);
	}
	
	/**
	 * 
	 * @param context
	 * @param userIsValid
	 */
	public static void setValidUser(Context context, boolean userIsValid) {
		
		PreferencesAdapter.write(context, VALIDUSER, userIsValid);
	}
	
	/**
	 * 
	 * @param context
	 * @return
	 */
	public static boolean[] getFilters(Context context) {
		
		boolean[] filters = new boolean[7];
		filters[0] = isInfostring(context);
		filters[1] = isWhiteboard(context);
		filters[2] = isPowersupply(context);
		filters[3] = isNetwork(context);
		filters[4] = isPc(context);
		filters[5] = isProjektor(context);
		filters[6] = isBeamer(context);
		return filters;
	}
	
	/**
	 * 
	 * @param context
	 * @param filters
	 */
	public static void setFilters(Context context, boolean[] filters) {
		
		setInfostring(context, filters[0]);
		setWhiteboard(context, filters[1]);
		setPowersupply(context, filters[2]);
		setNetwork(context, filters[3]);
		setPc(context, filters[4]);
		setProjektor(context, filters[5]);
		setBeamer(context, filters[6]);
	}
	
	/**
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isFilterActive(Context context) {
		
		getSize(context);
		return (isBeamer(context) || isProjektor(context) || isPc(context) 
				|| isNetwork(context) || isPowersupply(context) 
				|| isWhiteboard(context) || isInfostring(context) || !(size == -1 || size == 0));
	}
	
	/**
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isBeamer(Context context) {		
		
		return PreferencesAdapter.getBool(context, BEAMER);
	}

	/**
	 * 
	 * @param context
	 * @param beamer
	 */
	public static void setBeamer(Context context, boolean beamer) {
		
		PreferencesAdapter.write(context, BEAMER, beamer);
	}
	
	/**
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isProjektor(Context context) {		
		
		return PreferencesAdapter.getBool(context, PROJEKTOR);
	}

	/**
	 * 
	 * @param context
	 * @param projektor
	 */
	public static void setProjektor(Context context, boolean projektor) {
		
		PreferencesAdapter.write(context, PROJEKTOR, projektor);
	}
	
	/**
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isPc(Context context) {		
		
		return PreferencesAdapter.getBool(context, PC);
	}

	/**
	 * 
	 * @param context
	 * @param pc
	 */
	public static void setPc(Context context, boolean pc) {
		
		PreferencesAdapter.write(context, PC, pc);
	}
	
	/**
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetwork(Context context) {
		
		return PreferencesAdapter.getBool(context, NETWORK);
	}

	/**
	 * 
	 * @param context
	 * @param network
	 */
	public static void setNetwork(Context context, boolean network) {
		
		PreferencesAdapter.write(context, NETWORK, network);
	}
	
	/**
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isPowersupply(Context context) {
		
		return PreferencesAdapter.getBool(context, POWERSUPPLY);
	}

	/**
	 * 
	 * @param context
	 * @param powersupply
	 */
	public static void setPowersupply(Context context, boolean powersupply) {
		
		PreferencesAdapter.write(context, POWERSUPPLY, powersupply);
	}
	
	/**
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isInfostring(Context context) {
		
		return PreferencesAdapter.getBool(context, INFOSTRING);
	}

	/**
	 * 
	 * @param context
	 * @param infostring
	 */
	public static void setInfostring(Context context, boolean infostring) {
		
		PreferencesAdapter.write(context, INFOSTRING, infostring);
	}
	
	/**
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isWhiteboard(Context context) {
		
		return PreferencesAdapter.getBool(context, WHITEBOARD);
	}

	/**
	 * 
	 * @param context
	 * @param whiteboard
	 */
	public static void setWhiteboard(Context context, boolean whiteboard) {
		
		PreferencesAdapter.write(context, WHITEBOARD, whiteboard);
	}

	/**
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isFilterHolidaysOn(Context context) {
		
		return PreferencesAdapter.getBool(context, HOLIDAYS);
	}

	/**
	 * 
	 * @param context
	 * @param filterHolidays
	 */
	public static void setFilterHolidays(Context context, boolean filterHolidays) {
		
		PreferencesAdapter.write(context, HOLIDAYS, filterHolidays);
	}
	
	/**
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isRememberOn(Context context) {
		
		return PreferencesAdapter.getBool(context, REMEMBER);
	}

	/**
	 * 
	 * @param context
	 * @param vibrate
	 */
	public static void setRemember(Context context, boolean vibrate) {
		
		PreferencesAdapter.write(context, REMEMBER, vibrate);
	}

	/**
	 * 
	 * @param context
	 * @return
	 */
	public static String getEmail(Context context) {
		
		if (eMail == null){
			eMail = PreferencesAdapter.getString(context, EMAIL);
		}
		return eMail;
	}

	/**
	 * 
	 * @param context
	 * @param newEMail
	 */
	public static void setEmail(Context context, String newEMail) {
		
		PreferencesAdapter.write(context, EMAIL, newEMail);
		eMail = newEMail;
	}

	/**
	 * 
	 * @param context
	 * @return
	 */
	public static String getPw(Context context) {
		
		if (pw == null){
			pw = PreferencesAdapter.getString(context, PW);
		}
		return pw;
	}

	/**
	 * 
	 * @param context
	 * @param newPw
	 */
	public static void setPw(Context context, String newPw) {
		
		PreferencesAdapter.write(context, PW, newPw);
		pw = newPw;
	}

	/**
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isAutosyncEnabled(Context context) {
		
		return PreferencesAdapter.getBool(context, AUTOSYNC);
	}

	/**
	 * 
	 * @param context
	 * @param autosync
	 */
	public static void setAutosync(Context context, boolean autosync) {
		
		PreferencesAdapter.write(context, AUTOSYNC, autosync);
	}
	
	/**
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isClientUpdateEnabled(Context context) {
		
		return PreferencesAdapter.getBool(context, CHECKCLIENTUPDATE);
	}
	
	/**
	 * 
	 * @param context
	 * @param clientUpdate
	 */
	public static void setClientUpdateEnabled(Context context, boolean clientUpdate) {
		
		PreferencesAdapter.write(context, CHECKCLIENTUPDATE, clientUpdate);
	}
	
	/**
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isClientUpdateUserInteraction(Context context) {
		
		return PreferencesAdapter.getBool(context, CHECKCLIENTUPDATEUSER);
	}
	
	/**
	 * 
	 * @param context
	 * @param clientUpdate
	 */
	public static void setClientUpdateUserInteraction(Context context, boolean clientUpdate) {
		
		PreferencesAdapter.write(context, CHECKCLIENTUPDATEUSER, clientUpdate);
	}

	/**
	 * 
	 * @param context
	 * @return
	 */
	public static String getDefaultFile(Context context) {
		
		if(defaultFile == null) {
			defaultFile = PreferencesAdapter.getString(context, DEFAULTFILE);
		}
		return defaultFile;
	}

	/**
	 * 
	 * @param context
	 * @param newDefaultFile
	 */
	public static void setDefaultFile(Context context, String newDefaultFile) {
		
		PreferencesAdapter.write(context, DEFAULTFILE, newDefaultFile);
		defaultFile = newDefaultFile;
	}
	
	/**
	 * 
	 * @param context
	 * @param newRoommateDIR
	 */
	public static void setRoommateDIR(Context context, String newRoommateDIR) {
		
		PreferencesAdapter.write(context, ROOMMATEDIR, newRoommateDIR);
		roommateDIR = newRoommateDIR;
	}
	
	/**
	 * 
	 * @param context
	 * @return
	 */
	public static String getRoommateDir(Context context) {
		
		if(roommateDIR == null) {
			roommateDIR = PreferencesAdapter.getString(context, ROOMMATEDIR);
		}
		return roommateDIR;
	}
	
	/**
	 * 
	 * @return
	 */
	public static boolean hasDefaultFile() {
		
		return !defaultFile.equals(PreferencesAdapter.DEFSTRING);
	}
	
	/**
	 * 
	 * @param context
	 * @return
	 */
	public static int getSize(Context context) {
		
		if(size == PreferencesAdapter.DEFINT) {
			size = PreferencesAdapter.getInt(context, SIZE);
		}
		return size;
	}
	
	public static void setSize(Context context, int newSize) {
		
		PreferencesAdapter.write(context, SIZE, newSize);
		size = newSize;
	}
	
	/**
	 * 
	 * @param context
	 * @return
	 */
	public static int getSizeCheckedItem(Context context) {
		
		if(sizeCheckedItem == PreferencesAdapter.DEFINT) {
			sizeCheckedItem = PreferencesAdapter.getInt(context, CHECKED);
		}
		return sizeCheckedItem;
	}
	
	/**
	 * 
	 * @param context
	 * @param newsizeCheckedItem
	 */
	public static void setSizeCheckedItem(Context context, int newsizeCheckedItem) {
		
		PreferencesAdapter.write(context, CHECKED, newsizeCheckedItem);
		sizeCheckedItem = newsizeCheckedItem;
	}
}