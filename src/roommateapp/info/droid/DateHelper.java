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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import roommateapp.info.entities.BuildingFile;
import android.content.Context;
import roommateapp.info.R;

/**
 * The DateHelper class provides several 
 * methods to determine the current date and time.
 */
public class DateHelper {
	
	/**
	 * getDayOfDate returns a formated date 
	 * string dd.mm.yyyy of a selected weekday. 
	 * 
	 * @param day
	 * @return
	 */
	public static String getDateOfDay(String day, Context c) {
		
		String[] weekdays = {"Sonntag","Montag","Dienstag","Mittwoch","Donnerstag","Freitag","Samstag"};
    	int index = new GregorianCalendar().get(Calendar.DAY_OF_WEEK) - 1;
    	int steps = 0;
    	while(!day.equals(weekdays[index])) {
    		index = (index < weekdays.length - 1) ? index + 1 : 0;  	
    		steps++;
    	}
    	return getDate(steps);
	}
	
	/**
	 * getDateOfDayandYear returns a dd.mm.yyyy
	 * fromated string.
	 * 
	 * @param day
	 * @return
	 */
	public static String getDateOfDayandYear(String day, Context c) {
		
		String[] weekdays = {"Sonntag","Montag","Dienstag","Mittwoch","Donnerstag","Freitag","Samstag"};
    	int index = new GregorianCalendar().get(Calendar.DAY_OF_WEEK) - 1;
    	int steps = 0;
    	while(!day.equals(weekdays[index])) {
    		index = (index < weekdays.length - 1) ? index + 1 : 0;  	
    		steps++;
    	}
    	return getDateandYear(steps);
	}
	
	/**
	 * getDate returns a dd.mm formated string.
	 * 
	 * @param steps
	 * @return
	 */
	private static String getDate(int steps) {
		
		GregorianCalendar cal = new GregorianCalendar();
		cal.set(Calendar.DAY_OF_MONTH, (cal.get(Calendar.DAY_OF_MONTH) + steps));
		return getDateString(cal.get(Calendar.DAY_OF_MONTH), (cal.get(Calendar.MONTH) + 1));
	}
	
	/**
	 * getDateandYear returns a dd.mm.yyyy
	 * fromated string.
	 * 
	 * @param
	 * @return
	 */
	private static String getDateandYear(int steps) {
		
		GregorianCalendar cal = new GregorianCalendar();
		cal.set(Calendar.DAY_OF_MONTH, (cal.get(Calendar.DAY_OF_MONTH) + steps));
		return getDateString(cal.get(Calendar.DAY_OF_MONTH), (cal.get(Calendar.MONTH) + 1)) + "." + cal.get(Calendar.YEAR);
	}
	
	/**
	 * getDayString creates a mm.dd formated string.
	 * 
	 * @param day
	 * @param month
	 */
	public static String getDateString(int day, int month){
		
		String dateString = "";    	
    	String days = "";
    	if(day < 10) {
    		days = "0" + day;
    	} else {
    		days = "" + day;
    	}
    	String months = "";
    	if(month < 10) {
    		months = "0" + month;
    	} else {
    		months = "" + month;
    	}
    	
    	dateString = "" + days + "." + months;	
		return dateString;
	}
	
	/**
	 * GetCurrentDay returns the name of the 
	 * current weekday.
	 * 
	 * @return
	 */
	public static String getCurrentDay(Context c){

		String[] weekdays = {"Sonntag","Montag","Dienstag","Mittwoch","Donnerstag","Freitag","Samstag"};
		return weekdays[(new GregorianCalendar().get(Calendar.DAY_OF_WEEK) - 1)];
	}
	
	/**
	 * getHour returns the hour of a day from
	 * a complete time string: hh:mm -> hh
	 * 
	 * @param time
	 * @return
	 */
	public static int getHour(String time) {
		
		String[] timeSplit = time.split(":");
		return Integer.parseInt(timeSplit[0]);
	}
	
	/**
	 * getMinute returns the minute time
	 * of a complete time string: hh:mm -> mm
	 * 
	 * @param time
	 * @return
	 */
	public static int getMinute(String time) {
		
		String[] timeSplit = time.split(":");
		return Integer.parseInt(timeSplit[1]);
	}
	
	/**
	 * getCurrentDayID returns the the number
	 * of the current weekday: Mo-So
	 * 
	 * @return
	 */
	public static int getCurrentDayID(){
		
		return (new GregorianCalendar().get(Calendar.DAY_OF_WEEK) - 1);
	}
	
	/**
	 * getCurrentHour returns the current 
	 * hour of the day: hh
	 * 
	 * @return
	 */
	public static int getCurrentHour(){
		
		return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
	}	
	
	/**
	 * getCurrentMinute returns the 
	 * current minute of the hour: mm
	 * 
	 * @return
	 */
	public static int getCurrentMinute(){
		
		return Calendar.getInstance().get(Calendar.MINUTE);
	}
	
	/**
	 * getTimeString the current time: hh:mm
	 * 
	 * @param hours
	 * @param minutes 
	 */
	public static String getTimeString(int hour, int minute) {
		
		String dateString = "";    	
    	String mins = "";
    	if (minute < 10) {
    		mins = "0" + minute;
    	} else {
    		mins = "" + minute;
    	}
    	dateString = "" + hour + ":" + mins;	
    	
		return dateString;
	}
    
	/**
	 * 
	 * 
	 * @param buildingDays
	 * @return
	 */
    public static String getClosestDay(ArrayList<String> buildingDays, Context c) {
    	
    	String[] weekdays = {"Sonntag","Montag","Dienstag","Mittwoch","Donnerstag","Freitag","Samstag"};
    	
    	String id = getCurrentDay(c);
    	if(!buildingDays.contains(getCurrentDay(c))) {
    		boolean foundId = false;
    		int allDaysid = getCurrentDayID();
    		while(!foundId) {
    			allDaysid = (allDaysid < weekdays.length - 1) ? allDaysid + 1 : 0;
    			foundId = buildingDays.contains(weekdays[allDaysid]);
    		}
    		id = weekdays[allDaysid];   		
    	}
    	return id;
    }
    
    /**
     * 
     * 
     * @param day
     * @param weekdays
     * @return
     */
    public static boolean isDayAvailable(String day, ArrayList<String> weekdays) {
    	
    	boolean operation = (weekdays == null || day == null);
    	
    	if(!operation) {
    		operation = weekdays.contains(day);
    	}
    	
    	return operation;
    }
    
    /**
     * getNextAvailableDay checks in which day
     * the next available lession will start.
     * 
     * @param currentDay
     * @param buildingDays
     * @return
     */
    public static String getNextAvailableDay(String currentDay, ArrayList<String> buildingDays, Context c) {
    	
    	String[] weekdays = {"Sonntag","Montag","Dienstag","Mittwoch","Donnerstag","Freitag","Samstag"};
    	
    	int id;
    	boolean idfound = false;
    	for(id = 0; id < weekdays.length && !idfound; id++) {
    		idfound = weekdays[id].equals(currentDay);
    	}
    	id--;
    	idfound = false;
    	while(!idfound) {
			id = (id < weekdays.length - 1) ? id + 1 : 0;
			idfound = buildingDays.contains(weekdays[id]);
		}
    	
		return weekdays[id]; 
    }
    
    /**
     * getClosestsLecture determine which lecture number
     * belongs to the selected time.
     * 
     * @param currentHour
     * @param currentMinute
     * @param building
     * @return
     */
    public static int getClosestLecture(int currentHour, int currentMinute, BuildingFile building) {
		
		HashMap<Integer, String[]>lectureTimes = building.getLectureTimes();

		// Default value for knowing it wasnt set yet = last lecture.
		int closestHour = -1;
		String hourEnd;

		for(int i = 1; i < lectureTimes.size() + 1 && closestHour == -1; i++) {
			hourEnd = lectureTimes.get(i)[1];
			
			int startHour = Integer.parseInt(hourEnd.split(":")[0]);
			int startMinute = Integer.parseInt(hourEnd.split(":")[1]);
			
			if ((startHour > currentHour) || (startHour == currentHour) && (startMinute >= currentMinute)) {
				
				// When this match than we're in the current lession or in the break
				closestHour = i;
			}
		}
		return closestHour;
	}
    
    /**
     * getClosingTime returns the closing time
     * of the last lecture of the day.
     * 
     * @param building
     * @return closing time
     */
    public static String getClosingTime(BuildingFile building)  {
    	
    	String closingTime = "";
    	if(building.getLectureCount() > 0) {
    		closingTime = building.getLectureEnd(building.getLectureCount());
    	}
    	
    	return closingTime;
    }
    
	/**
	 * 
	 * @param day
	 * @param c
	 * @return
	 */
	public static String translateWeekday(String day, Context c) {
		
		// German weekdays within the datafile
		String mon = "Montag";
		String die = "Dienstag";
		String mit = "Mittwoch";
		String don = "Donnerstag";
		String fre = "Freitag";
		String sam = "Samstag";
		String son = "Sonntag";
		String transDay = "";
		
		if(day.equals(mon)) {
			transDay = c.getString(R.string.monday);
		} else if (day.equals(die)) {
			transDay = c.getString(R.string.tuesday);
		} else if (day.equals(mit)) {
			transDay = c.getString(R.string.wednesday);
		} else if (day.equals(don)) {
			transDay = c.getString(R.string.thursday);
		} else if (day.equals(fre)) {
			transDay = c.getString(R.string.friday);
		} else if (day.equals(sam)) {
			transDay = c.getString(R.string.saturday);
		} else if (day.equals(son)) {
			transDay = c.getString(R.string.sunday);
		} else if (day.equals(c.getString(R.string.monday))) {
			transDay = mon;
		} else if (day.equals(c.getString(R.string.tuesday))) {
			transDay = die;
		} else if (day.equals(c.getString(R.string.wednesday))) {
			transDay = mit;
		} else if (day.equals(c.getString(R.string.thursday))) {
			transDay = don;
		} else if (day.equals(c.getString(R.string.friday))) {
			transDay = fre;
		} else if (day.equals(c.getString(R.string.saturday))) {
			transDay = fre;
		} else if (day.equals(c.getString(R.string.sunday))) {
			transDay = son;
		}
		
		return transDay;
	}
	
	/**
	 * 
	 * @param days
	 * @param c
	 * @return
	 */
	public static String[] translateWeekdays(String[] days, Context c) {
		
		String[] transDays = new String[days.length];
		
		for(int i = 0; i < days.length; i++) {
			transDays[i] = translateWeekday(days[i], c);
		}
		
		return transDays;
	}    
}
