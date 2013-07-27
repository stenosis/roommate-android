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
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import roommateapp.info.droid.RoommateConfig;
import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * The BuildingFile class represent a Roommate file.
 */
@SuppressLint("UseSparseArrays")
public class BuildingFile implements Parcelable {
	
	// Instance variables
	private File file;
	private String filename=new String();
	private String buildingname = new String();
	private String date = new String();
	private String lat = new String();
	private String lng = new String();
	private String release = new String();
	private String id = new String();
	private String info = new String();
	private String state = new String();
	private String semester = new String();
	private boolean publicStatus=false;
	private boolean isOnSD=true;
	private int lectuteTimeCount = 0;
	private ArrayList<String> weekdays;
	private HashMap<Integer, String[]> lectureTimes;
	private HashMap<String, Room> roomList = new HashMap<String, Room>();
	private boolean[] weekdaysBooleanArray;
	
	/**
	 * 
	 * @return
	 */
	public String getFilename() {
		
		return this.filename;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isOnSD() {
		
		return isOnSD;
	}

	/**
	 * 
	 * @param isOnSD
	 */
	public void setOnSD(boolean isOnSD) {
		
		this.isOnSD = isOnSD;
	}
	
	/**
	 * 
	 */
	public BuildingFile() {
		
		// For parcable
		super();
	}
	
	/**
	 * 
	 * @return
	 */
	public HashMap<Integer, String[]> getLectureTimes() {
		
		return lectureTimes;
	}

	/**
	 * 
	 * @return
	 */
	public Set<String> getRoomListKeySet() {
		
		return this.roomList.keySet();
	}
	
	/**
	 * 
	 * @param roomName
	 * @return
	 */
	public Room getRoom(String roomName) {
		
		return this.roomList.get(roomName);

	}
	
	/**
	 * 
	 * @param room
	 */
	public void addRoom(Room room) {
		
		this.roomList.put(room.getName(), room);
	}
	
	/**
	 * 
	 */
	public void setPublic() {
		
		this.publicStatus = true;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean getPublicStatus() {
		
		return this.publicStatus;
	}
	
	/**
	 * 
	 * @param state
	 */
	public void setState(String state) {
		
		this.state = state;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getState() {
		
		return this.state;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getLectureCount() {
		
		return this.lectuteTimeCount;
	}
	
	/**
	 * 
	 * @param start
	 * @param end
	 */
	public void setLecture(String start, String end) {
		
		if(this.lectuteTimeCount == 0) {
			this.lectureTimes = new HashMap<Integer, String[]>();
		}
		
		this.lectuteTimeCount++;
		String[] lectureTime = {start, end};
		this.lectureTimes.put(this.lectuteTimeCount, lectureTime);
	}
	
	/**
	 * 
	 * @param publicStatus
	 */
	public void setPublicStatus(boolean publicStatus) {
		
		this.publicStatus = publicStatus;
	}

	/**
	 * 
	 * @param order
	 * @return
	 */
	public String[] getLecture(int order) {
		
		return this.lectureTimes.get(order);
		
	}
	
	/**
	 * 
	 * @param order
	 * @return
	 */
	public String getLectureStart(int order) {
		
		String[] lectureTime = this.lectureTimes.get(order);
		return lectureTime[0];
	}
	
	/**
	 * 
	 * @param order
	 * @return
	 */
	public String getLectureEnd(int order) {
		
		String[] lectureTime = this.lectureTimes.get(order);
		return lectureTime[1];
	}
	
	/**
	 * 
	 * @return
	 */
	public String getSemester() {
		
		return this.semester;
	}
	
	/**
	 * 
	 * @param semester
	 */
	public void setSemester(String semester) {
		
		this.semester = semester;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getInfo() {
		
		return this.info;
	}
	
	/**
	 * 
	 * @param info
	 */
	public void setInfo(String info) {
		
		this.info = info;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getId() {
		
		return id;
	}

	/**
	 * 
	 * @param id
	 */
	public void setId(String id) {
		
		this.id = id;
	}

	/**
	 * 
	 * @return
	 */
	public String getRelease() {
		
		return release;
	}

	/**
	 * 
	 * @param release
	 */
	public void setRelease(String release) {
		
		this.release = release;
	}

	/**
	 * 
	 * @return
	 */
	public String getLat() {
		
		return lat;
	}

	/**
	 * 
	 * @param lat
	 */
	public void setLat(String lat) {
		
		this.lat = lat;
	}

	/**
	 * 
	 * @return
	 */
	public String getLng() {
		
		return lng;
	}

	/**
	 * 
	 * @param lng
	 */
	public void setLng(String lng) {
		
		this.lng = lng;
	}
	
	/**
	 * 
	 * @return
	 */
	public File getFile() {
		
		return file;
	}

	/**
	 * 
	 * @return
	 */
	public String getBuildingname() {
		
		return buildingname;
	}

	/**
	 * 
	 * @param buildingname
	 */
	public void setBuildingname(String buildingname) {
		
		this.buildingname = buildingname;
	}

	/**
	 * 
	 * @param file
	 */
	public void setFile(File file) {
		
		this.file = file;
	}

	/**
	 * 
	 * @param filename
	 */
	public void setFilename(String filename) {
		
		this.filename = filename;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getDate() {
		
		return date;
	}
	
	/**
	 * 
	 * @param date
	 */
	public void setDate(String date) {
		
		this.date = date;
	}
	
	/**
	 * 
	 * @param weekday
	 */
	public void addWeekday(String weekday) {
		
		if (this.weekdays == null) {
			this.weekdays = new ArrayList<String>();
		}
		this.weekdays.add(weekday);
	}

	/**
	 * 
	 * @return
	 */
	public String[] getWeekdaysStrings() {
		
		return this.weekdays.toArray(new String[this.weekdays.size()]);
	}
	
	/**
	 * 
	 * @return
	 */
	public ArrayList<String> getWeekdaysArrayList() {
		
		return this.weekdays;
	}

	public boolean[] getWeekdays() {
		return this.weekdaysBooleanArray;
	}
	
	public void setWeekdaysBooleanArray(boolean[] weekdays) {
		this.weekdaysBooleanArray = weekdays;
	}
	
    /**
     * 
     */
	@Override
	public String toString() {
		
		return this.buildingname + " ," +this.date + " " + this.release + " " + this.filename;
	}

	/**
	 * 
	 */
	@Override
	public boolean equals(Object o) {
		
		return  ((BuildingFile) (o)).id.equals(this.id) ;
	}
	
	/**
	 * 
	 * @param comparedFile
	 * @return
	 */
	public boolean isOlder(BuildingFile comparedFile) {
		
		if (RoommateConfig.VERBOSE) {
			System.out.println("Version: "+Integer.parseInt(this.release) + 
					"to version:"+Integer.parseInt(comparedFile.release) 
					+ "equals "+(Integer.parseInt(this.release) < Integer.parseInt(comparedFile.release)));
		}
		return (Integer.parseInt(this.release) < Integer.parseInt(comparedFile.release));
	}

	/**
	 * 
	 */
	@Override
	public int hashCode() {

		return (this.buildingname.hashCode() + this.id.hashCode());
	}	
	
	// *** Parcable-part ***

	/**
	 *  Parcable interface implementation.
	 * 
	 * @param in
	 */
	public BuildingFile(Parcel in) {
		
		readFromParcel(in);
	}
	
	/**
	 * Parcable interface implementation.
	 */
	public int describeContents() {
		
		return 0;
	}

	/**
 	 * Parcable interface implementation.
	 */
	public void writeToParcel(Parcel dest, int flags) {
		
		dest.writeString(this.date);
		dest.writeString(this.release);
		dest.writeString(this.id);
		dest.writeString(this.buildingname);
		
		dest.writeString(this.state);
		dest.writeBooleanArray(new boolean[] {this.publicStatus});
		
		dest.writeString(this.lat);
		dest.writeString(this.lng);
		dest.writeString(this.info);
		dest.writeString(this.semester);
		dest.writeString(this.file.toString());
	}
	
	/**
	 * Parcable interface implementation.
	 * 
	 * @param in
	 */
	private void readFromParcel(Parcel in) {
		 
		this.date = in.readString();
		this.release = in.readString();
		this.id = in.readString();
		this.buildingname = in.readString();
		
		this.state = in.readString();
		boolean[] publicStatus = new boolean[1];
		in.readBooleanArray(publicStatus);
		this.publicStatus = publicStatus[0];
		
		this.lat = in.readString();
		this.lng = in.readString();
		this.info = in.readString();
		this.semester = in.readString();
		this.file = new File(in.readString());
	}
	
	/**
	 *  Parcable interface implementation.
	 */
    public static final Creator<BuildingFile> CREATOR = new Creator<BuildingFile>() {
    	
        public BuildingFile createFromParcel(Parcel in) {
            return new BuildingFile(in);
        }

        public BuildingFile[] newArray(int size) {
            return new BuildingFile[size];
        }
    };
}