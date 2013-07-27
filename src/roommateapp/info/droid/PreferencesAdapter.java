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
package roommateapp.info.droid;

/* imports */
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * PreferenceAdapter is an adapter to write and recieve
 * values to the android device storage.
 */
public class PreferencesAdapter {
	
	// Instance variables
	public static final String DEFSTRING = "-1";
	public static final boolean DEFBOOL = false;
	public static final int DEFINT = -1;
	
	/**
	 * Write a string to the device.
	 * 
	 * @param context
	 * @param key
	 * @param value
	 */
	public static void write(Context context, String key, String value) {
		
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
		sharedPreferencesEditor.putString(key, value);
		sharedPreferencesEditor.commit();
	}
	
	/**
	 * Get a string from the device.
	 * 
	 * @param context
	 * @param key
	 * @return value
	 */
	public static String getString(Context context, String key) {
		
		SharedPreferences sharedPreferences =  PreferenceManager.getDefaultSharedPreferences(context);
		return sharedPreferences.getString(key, DEFSTRING);
	}
	
	/**
	 * Writes a int value to the device.
	 * 
	 * @param context
	 * @param key
	 * @param value
	 */
	public static void write(Context context, String key, int value) {
		
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
		sharedPreferencesEditor.putInt(key, value);
		sharedPreferencesEditor.commit();
	}
	
	/**
	 * Gets a int value from the device.
	 * 
	 * @param context
	 * @param key
	 * @return value
	 */
	public static int getInt(Context context, String key) {
		
		SharedPreferences sharedPreferences =  PreferenceManager.getDefaultSharedPreferences(context);
		return sharedPreferences.getInt(key, DEFINT);
	}
	
	/**
	 * Writes bool values to the device.
	 * 
	 * @param context
	 * @param key
	 * @param value
	 */
	public static void write(Context context, String key, boolean value) {
		
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
		sharedPreferencesEditor.putBoolean(key, value);
		sharedPreferencesEditor.commit();
	}
	
	/**
	 * Gets bool values from the device.
	 * 
	 * @param context
	 * @param key
	 * @return value
	 */
	public static boolean getBool(Context context, String key) {
		
		SharedPreferences sharedPreferences =  PreferenceManager.getDefaultSharedPreferences(context);
		return sharedPreferences.getBoolean(key, DEFBOOL);
	}
}
