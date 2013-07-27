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
package roommateapp.info.entities;

/* imports */
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Set;

/**
 * The HolidayList class represent a list
 * of parsed holidays from the holiday filelist.
 */
public class HolidayList {

	// Instance variables
	private HashMap<String, HashMap<String, String>> holidayList;
	private int version;

	/**
	 * Constructor
	 */
	public HolidayList() {
		
		this.holidayList = new HashMap<String, HashMap<String,String>>();
		this.version = -1;
	}

	/**
	 * addHolidayList adds a new holiday list for a
	 * specified state.
	 *
	 * @param state keyvalue
	 * @param holidays Hashmap<String date, String holidayname>
	 */
	public void addHolidayList(String state, HashMap<String, String> holidays) {
		
		this.holidayList.put(state, holidays);
	}
	
	/**
	 * getHolidayListKeySet returns the entire
	 * list of states.
	 * 
	 * @return states
	 */
	public Set<String> getHolidayListKeySet() {
		
		return this.holidayList.keySet();
	}
	
	/**
	 * getHolidays returns a hashmap of all
	 * holidays for a specified state.
	 * 
	 * @param state
	 * @return Hashmap<String date, String holidayname>
	 */
	public HashMap<String, String> getHolidays(String state) {
		
		HashMap<String, String> holidays = new HashMap<String, String>();
		
		if(this.holidayList.containsKey(state)) {
			holidays = this.holidayList.get(state);
		}
		return holidays;	
	}
	
	/**
	 * getHolidayListSize returns the count
	 * of the states.
	 * 
	 * @return
	 */
	public int getHolidayListSize() {
		
		return this.holidayList.size();
	}
	
	/**
	 * Set the version number of the holiday list.
	 * 
	 * @param versionNumber
	 */
	public void setVersionNumber(int versionNumber) {
		
		this.version = versionNumber;
	}
	
	/**
	 * Get the version number of the holiday list.
	 * 
	 * @return
	 */
	public int getVersionNumber() {
		
		return this.version;
	}
	
	/**
	 * Check if the list contains data from
	 * the current year.
	 * 
	 * @return
	 */
	public boolean checkYearStatus() {
		
		int yearCurrent = new GregorianCalendar().get(Calendar.YEAR);
		int yearFile = (this.version / 100);
		
		return yearFile == yearCurrent;
	}
}