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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * The class Room represents a classroom which contains 
 * a name and all scheduled lecture information for the week.
 */
public class Room implements Comparable<Room>, Parcelable {

	// Instance variables
	private boolean beamer;
	private boolean projector;
	private boolean powerSupply;
	private boolean network;
	private boolean pcPool;
	private boolean whiteboard;
	private String[] customProperties = new String[5];
	private String info = new String();
	private String roomName = new String();
	private int roomSize;
	private HashMap<String, ArrayList<Lecture>> lectureList = new HashMap<String, ArrayList<Lecture>>();
	private HashMap<String, String[]> exceptionList = new HashMap<String, String[]>();
	
	/**
	 * 
	 * @param roomName
	 */
	public Room(String roomName) {
		
		this.roomName = roomName;
	}

	/**
	 * Parcel interface implementation.
	 * 
	 * @param parcel
	 */
	public Room(Parcel parcel) {
		
		readFromParcel(parcel);
	}
	
	/**
	 * Parcel interface implementation.
	 */
	public void writeToParcel(Parcel dest, int flags) {
		
		// Common settings
		dest.writeString(this.roomName);
		dest.writeString(this.info);
		dest.writeInt(this.roomSize);
		
		// Properties
		dest.writeBooleanArray(new boolean[] 
				{this.beamer, this.projector, this.powerSupply,
				this.network, this.pcPool, this.whiteboard});
		dest.writeStringArray(customProperties);

		// LectureList 
        final int N = lectureList.size();
        dest.writeInt(N);
        
        // Weekdays of the LectureList
        if (N > 0) {
            for (Map.Entry<String, ArrayList<Lecture>> entry : lectureList.entrySet()) {
            	
            	dest.writeString(entry.getKey());
            	ArrayList<Lecture> weekdayLectures = this.getLecturesOfDay(entry.getKey());

            	final int M = weekdayLectures.size();
            	dest.writeInt(M);
            	
            	// Lectures on the day
            	if (M > 0) {

            		for(Lecture currenLecture: weekdayLectures) {
            			
            			dest.writeString(currenLecture.getLectureName());
            			dest.writeString(currenLecture.getType());
            			dest.writeString(currenLecture.getHost());
            			dest.writeBooleanArray(new boolean[] {currenLecture.isAvailable()});
            		}		
            	}
            }
        }
        
        // Exceptions
        final int X = this.exceptionList.size();
        dest.writeInt(X);
        
		if (X > 0) {

			for (String date : this.exceptionList.keySet()) {

				dest.writeString(date);
				dest.writeInt(this.exceptionList.get(date).length);
				dest.writeStringArray(this.exceptionList.get(date));
			}
		}
	}
	
	/**
	 * Parcel interface implementation.
	 * 
	 * @param in
	 */
	public void readFromParcel(Parcel in) {
		
		// Common settings
		this.roomName = in.readString();
		this.info = in.readString();
		this.roomSize = in.readInt();
		
		// Properties
		boolean[] properties = new boolean[6];
		in.readBooleanArray(properties);
		this.beamer = properties[0];
		this.projector = properties[1];
		this.powerSupply = properties[2];
		this.network = properties[3];
		this.pcPool = properties[4];
		this.whiteboard = properties[5];
		in.readStringArray(this.customProperties);

		// LectureList
		final int N = in.readInt();
		
		// Weekdays of the LectureList
		this.lectureList = new HashMap<String, ArrayList<Lecture>>();
		if (N > 0) {
			
			for(int i = 0; i < N; i++) {
				String weekday = in.readString();
				
				// Lectures on the day
				final int M = in.readInt();
				if(M > 0) {
					
					ArrayList<Lecture> lecutureList = new ArrayList<Lecture>();
					for(int j = 0; j < M; j++) {
						Lecture currentLecture = new Lecture();
						currentLecture.setLectureName(in.readString());
						currentLecture.setType(in.readString());
						currentLecture.setHost(in.readString());
						boolean[] availableStatus = new boolean[1];
						in.readBooleanArray(availableStatus);
						currentLecture.setAvailable(availableStatus[0]);
						lecutureList.add(currentLecture);
					}
					this.lectureList.put(weekday, lecutureList);
				}	
			}
		}
		
		// Exceptions
		final int X = in.readInt();
		
		this.exceptionList = new HashMap<String, String[]>();
		
		if(X > 0) {
			
			for(int i = 0; i < X; i++) {
				
				String date = in.readString();
				String[] times = new String[in.readInt()];
				in.readStringArray(times);
				
				this.exceptionList.put(date, times);
			}
		}
	}
	
	/**
	 * Parcel interface implementation.
	 */
    public static final Creator<Room> CREATOR = new Creator<Room>() {
    	
        public Room createFromParcel(Parcel in) {
            return new Room(in);
        }

        public Room[] newArray(int size) {
            return new Room[size];
        }
    };
    
    /**
     * 
     * @param day
     * @param lectures
     */
	public void addLectureDay(String day, ArrayList<Lecture> lectures) {
		
		this.lectureList.put(day, lectures);
	}
	
	/**
	 * 
	 * @param day
	 * @return
	 */
	public ArrayList<Lecture> getLecturesOfDay(String day) {
		
		return this.lectureList.get(day);
	}
	
	/**
	 * 
	 * @return
	 */
	public Set<String> getLectureListKeySet() {
		
		return this.lectureList.keySet();
	}
    
	/**
	 * 
	 */
	public int describeContents() {
		
		return 0;
	}

	/**
	 * The method getName returns the name of the current room object.
	 * 
	 * @return room name
	 */
	public String getName() {
		
		return this.roomName;
	}
	
	/**
	 * 
	 * @param size
	 */
	public void setRoomSize(int size) {
		
		this.roomSize = size;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getRoomSize() {
		
		return this.roomSize;
	}

	/**
	 * 
	 * @param date
	 * @param times
	 */
	public void addException(String date, String[] times) {
		
		if(this.exceptionList == null) {
			
			this.exceptionList = new HashMap<String, String[]>();
		}
		// Increase
		if(this.exceptionList.containsKey(date)) {

			int size = this.exceptionList.get(date).length;
			String[] newTimes = new String[(size + 2)];
			
			for(int i = 0; i < size; i++) {
				newTimes[i] = this.exceptionList.get(date)[i];
			}
			newTimes[(size + 1)] = times[0];
			newTimes[(size + 2)] = times[1];

			this.exceptionList.remove(date);
			this.exceptionList.put(date, newTimes);			
			
		} else {
			this.exceptionList.put(date, times);
		}
	} 
	
	/**
	 * 
	 * @param date
	 * @return
	 */
	public String[] getExecptionTime(String date) {
		
		return this.exceptionList.get(date);
	}
	
	/**
	 * 
	 * @return
	 */
	public Set<String> getExceptionListKeys() {
		
		return this.exceptionList.keySet();
	}
	
	/**
	 * 
	 * @param information
	 */
	public void setRoomInformation(String information) {
		
		this.info = information;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getRoomInformation() {
		
		return this.info;
	}
	
	/**
	 * 
	 * @param available
	 */
	public void setBeamerStatus(boolean available) {
		
		this.beamer = available;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean getBeamerStatus() {
		
		return this.beamer;
	}
	
	/**
	 * 
	 * @param available
	 */
	public void setProjectorStatus(boolean available) {
		
		this.projector = available;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean getProjectorStatus() {
		
		return this.projector;
	}
	
	/**
	 * 
	 * @param available
	 */
	public void setPowerSupplyStatus(boolean available) {
		
		this.powerSupply = available;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean getPowerSupplyStatus() {
		
		return this.powerSupply;
	}
	
	/**
	 * 
	 * @param available
	 */
	public void setNetworkStatus(boolean available) {
		
		this.network = available;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean getNetworkStatus() {
		
		return this.network;
	}
	
	/**
	 * 
	 * @param avavilable
	 */
	public void setPCPoolStatus(boolean avavilable) {
		
		this.pcPool = avavilable;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean getPCPoolStatus() {
		
		return this.pcPool;
	}
	
	/**
	 * 
	 * @param available
	 */
	public void setWhiteboardStatus(boolean available) {
		
		this.whiteboard = available;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean getWhiteboardStatus() {
		
		return this.whiteboard;
	}
	
	/**
	 * 
	 * @param propertie
	 */
	public void setCustomPropertie1(String propertie) {
		
		this.customProperties[0] = propertie;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getCustomPropertie1() {
		
		return this.customProperties[0];
	}
	
	/**
	 * 
	 * @param propertie
	 */
	public void setCustomPropertie2(String propertie) {
		
		this.customProperties[1] = propertie;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getCustomPropertie2() {
		
		return this.customProperties[1];
	}

	/**
	 * 
	 * @param propertie
	 */
	public void setCustomPropertie3(String propertie) {
		
		this.customProperties[2] = propertie;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getCustomPropertie3() {
		
		return this.customProperties[2];
	}
	
	/**
	 * 
	 * @param propertie
	 */
	public void setCustomPropertie4(String propertie) {
		
		this.customProperties[3] = propertie;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getCustomPropertie4() {
		
		return this.customProperties[3];
	}
	
	/**
	 * 
	 * @param propertie
	 */
	public void setCustomPropertie5(String propertie) {
		
		this.customProperties[4] = propertie;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getCustomPropertie5() {
		
		return this.customProperties[4];
	}
	
	/**
	 * 
	 */
	@Override
	public String toString() {
		
		return this.roomName;
	}

	/**
	 * 
	 */
	public int compareTo(Room room) {
		
		return this.roomName.compareTo(room.toString());
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean hasPropteries() {
		
		return this.beamer || this.network || this.pcPool
				|| this.powerSupply || this.projector || this.whiteboard;
	}
}