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
import java.util.HashMap;
import java.util.Iterator;
import roommateapp.info.entities.BuildingFile;
import roommateapp.info.entities.Lecture;
import roommateapp.info.entities.Room;
import android.content.Context;

/**
 * The FilterHelper class provides several 
 * methods to determine room properties.
 */
public class FilterHelper {

	
	/**
	 * getAllRooms return all rooms from a building file.
	 * 
	 * @param building
	 * @return
	 */
	public static ArrayList<Room> getAllRooms(BuildingFile building) {
		
  		Iterator<String> iter = building.getRoomListKeySet().iterator();
  		ArrayList<Room> rooms = new ArrayList<Room>();
  		while(iter.hasNext()) {
  			String key = (String) iter.next();
  			rooms.add(building.getRoom(key));
  		}
  		return rooms;
	}
	
	/**
	 * getRoomsStateFilter determine the rooms from the 
	 * building file which match the filters.
	 * 
	 * 0 = infostring
	 * 1 = whiteboard
	 * 2 = power supply
	 * 3 = network
	 * 4 = pcs
	 * 5 = projector
	 * 6 = beamer
	 * 
	 * @param building
	 * @param filters
	 * @param size
	 * @return
	 */
	public static ArrayList<Room> getRoomsStateFilter(BuildingFile building, boolean[] filters, int size) {
		
		Iterator<String> iter = building.getRoomListKeySet().iterator();
  		ArrayList<Room> rooms = new ArrayList<Room>();
  		Room currentRoom;
  		
  		while(iter.hasNext()) {
  			
  			String key = (String) iter.next();
  			currentRoom = building.getRoom(key);
  			if(currentRoom.getRoomSize() >= size || size == -1) {
  				boolean infostring = (filters[0])? currentRoom.getRoomInformation() != null : false;
  	  	  		boolean whiteboard = (filters[1])? currentRoom.getWhiteboardStatus() : false;
  	  	  		boolean powersupply = (filters[2])? currentRoom.getPowerSupplyStatus() : false;
  	  	  		boolean network = (filters[3])? currentRoom.getNetworkStatus() : false;
  	  	  		boolean pc = (filters[4])? currentRoom.getPCPoolStatus() : false;
  	  	  		boolean projektor = (filters[5])? currentRoom.getProjectorStatus() : false;
  	  	  		boolean beamer = (filters[6])? currentRoom.getBeamerStatus() : false;
  	  	  		
  	  			if(filters[0] == infostring &&
  				  				filters[1] == whiteboard &&
  				  				filters[2] == powersupply &&
  				  				filters[3] == network &&
  				  				filters[4] == pc &&
  				  				filters[5] == projektor &&
  				  				filters[6] == beamer) {
  	  				rooms.add(currentRoom);
  	  			} 
  			}	
  		}
  		return rooms;
	}
	
	/**
	 * getRoomsTimeFilter determine the rooms of building file
	 * which are available at a specific day and lecture number.
	 * 
	 * 
	 * @param roomList
	 * @param lectureIndex
	 * @param day
	 * @return
	 */
	public static HashMap<Room, Integer> getRoomsTimeFilter(ArrayList<Room> roomList, 
			int lectureIndex, String day, BuildingFile building, Context c) {
		
		HashMap<Room, Integer> rooms = new HashMap<Room, Integer>();
		for(Room currentRoom: roomList) {
			
			int freeLectures = 0;
			ArrayList<Lecture> lectures = currentRoom.getLecturesOfDay(day);
			boolean foundNext = lectures.get(lectureIndex).isAvailable();
			
			// Check if there's an exception
			String currentDate = DateHelper.getDateOfDayandYear(day, c);
			if(currentRoom.getExceptionListKeys().contains(currentDate) && foundNext) {
				
				String[] exceptiontimes = currentRoom.getExecptionTime(currentDate);
				int lectureException = 1000;
				boolean roompossible = true;
				for(int i = 0; i< exceptiontimes.length && roompossible; i = i + 2){
					
					int firstLectureException = DateHelper.getClosestLecture(DateHelper.getHour(exceptiontimes[i]), DateHelper.getMinute(exceptiontimes[i]), building) - 1;
					int lastLectureException = DateHelper.getClosestLecture(DateHelper.getHour(exceptiontimes[i+1]), DateHelper.getMinute(exceptiontimes[i+1]), building) - 1;
					if(firstLectureException <= lectureIndex) {
						
						roompossible = lastLectureException < lectureIndex;
					} else {
						
						lectureException = (lectureException > firstLectureException) ? firstLectureException : lectureException;
					}
				}
				
				if(roompossible) {
					for(int i = lectureIndex; i < lectures.size() && foundNext; i++) {
						
						foundNext = lectures.get(i).isAvailable() && i < lectureException;
						if(foundNext) {
							freeLectures++;
						}
					}
				}
			} else {
				for(int i = lectureIndex; i < lectures.size() && foundNext; i++) {
					
					foundNext = lectures.get(i).isAvailable();
					if(foundNext) {
						
						freeLectures++;
					}
				}
			}
			
			if(freeLectures != 0) {
				
				rooms.put(currentRoom, freeLectures);
			}			
		}
		return rooms;
	}
}