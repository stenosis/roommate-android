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
package roommateapp.info.io;

/* imports */
import java.util.ArrayList;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import roommateapp.info.droid.RoommateConfig;
import roommateapp.info.entities.BuildingFile;
import roommateapp.info.entities.Lecture;
import roommateapp.info.entities.Room;

/**
 * Content handler Roommate file.
 */
public class CHandlerRFile implements ContentHandler {

	// Instance variables
	private boolean openWeekday;
	private boolean openRoom;
	private boolean openRoominformation;
	private boolean openDay;

	// Tags
	private static final String WEEKDAY = "weekday";
	private static final String TIME = "time";
	private static final String LECTURESTART = "start";
	private static final String LECTUREEND = "end";
	private static final String ROOM = "room";
	private static final String ROOMNAME = "roomname";
	private static final String ROOMSIZE = "roomsize";
	private static final String ROOMINFO = "roominfo";
	private static final String PROPERTY = "property";
	private static final String PROP_ENTITY = "entity";
	private static final String PROP_BEAMER = "beamer";
	private static final String PROP_PROJECTOR = "projector";
	private static final String PROP_POWER = "powerSupply";
	private static final String PROP_NETWORK = "network";
	private static final String PROP_PCPOOL = "pcPool";
	private static final String PROP_WHITEBOARD = "whiteboard";
	private static final String EXCEPTION = "exception";
	private static final String EXC_DATE = "date";
	private static final String EXC_START = "start";
	private static final String EXC_END = "end";
	private static final String DAY = "day";
	private static final String DAY_WEEKDAY = "weekday";
	private static final String LECTURE = "lecture";
	private static final String LEC_LESSON = "lesson";
	private static final String LEC_TYPE = "type";
	private static final String LEC_HOST = "host";
	private static final String AVAILABLE = "available";
	private static final String MO = "Montag";
	private static final String TUE = "Dienstag";
	private static final String WED = "Mittwoch";
	private static final String THUR = "Donnerstag";
	private static final String FRI = "Freitag";
	private static final String SAT = "Samstag";
	private static final String SUN = "Sonntag";
	
	// Data
	private BuildingFile building;
	private Room currentRoom;
	private String currentDay = "";
	private ArrayList<Lecture> currentLectureList;
	private boolean[] weekdaysBooleanArray;
	
	/**
	 * Konstruktor
	 * 
	 * @param currentFile
	 */
	public CHandlerRFile(BuildingFile currentFile) {
		this.building = currentFile;
		this.weekdaysBooleanArray = new boolean[7];
		this.building.setWeekdaysBooleanArray(weekdaysBooleanArray);
	}
	
	/**
	 * Die Methode getBuildingFile liefert das aktuelle Gebäude
	 * zur geprüften Roommatedatei zurück.
	 * 
	 * @return
	 */
	public BuildingFile getBuildingFile() {
		
		return this.building;
	}

	// Xerces Parser Inhalt
	public void startDocument() throws SAXException {
		
		if (RoommateConfig.VERBOSE) {
			System.out.println("Start parsing Roommate file..");
		}
	}

	public void endDocument() throws SAXException {
		
		if (RoommateConfig.VERBOSE) {
			System.out.println("Stop parsing Roommate file..");
		}
	}

	/**
	 * 
	 */
	public void startElement(String uri, String localName, String qName,
			Attributes atts) throws SAXException {

		// Weekdays
		if(qName.equals(WEEKDAY)) {
			
			this.openWeekday = true;
		
		// Lecture times
		} else if(qName.equals(TIME)) {
			
			String start = atts.getValue(LECTURESTART);
			String end =  atts.getValue(LECTUREEND);
			this.building.setLecture(start, end);
		
		// Room
		} else if (qName.equals(ROOM)) {
			
			this.openRoom = true;
			this.currentRoom = new Room(atts.getValue(ROOMNAME));
			
			// Roomsize
			if(atts.getValue(ROOMSIZE) != null) {
				this.currentRoom.setRoomSize(Integer.parseInt(atts.getValue(ROOMSIZE)));
			}
			
		// Room information
		} else if (qName.equals(ROOMINFO)) {
			
			this.openRoominformation = true;
			
		// Properties
		} else if (qName.equals(PROPERTY)) {
			
			// Beamer
			if (atts.getValue(PROP_ENTITY).equals(PROP_BEAMER)) {
				this.currentRoom.setBeamerStatus(true);
				
			// LAN
			} else if (atts.getValue(PROP_ENTITY).equals(PROP_NETWORK)) {
				this.currentRoom.setNetworkStatus(true);
				
			// PC's
			} else if (atts.getValue(PROP_ENTITY).equals(PROP_PCPOOL)) {
				this.currentRoom.setPCPoolStatus(true);
				
			// Power-Supply
			} else if (atts.getValue(PROP_ENTITY).equals(PROP_POWER)) {
				this.currentRoom.setPowerSupplyStatus(true);
				
			// Projector
			} else if (atts.getValue(PROP_ENTITY).equals(PROP_PROJECTOR)) {
				this.currentRoom.setProjectorStatus(true);
				
			// Whiteboard
			} else if (atts.getValue(PROP_ENTITY).equals(PROP_WHITEBOARD)) {
				this.currentRoom.setWhiteboardStatus(true);
			}
			
		// Exception
		} else if (qName.equals(EXCEPTION)) {
			
			String[] exceptionTime = {atts.getValue(EXC_START), atts.getValue(EXC_END)};
			this.currentRoom.addException(atts.getValue(EXC_DATE), exceptionTime);
		
		// Day
		} else if (qName.equals(DAY)) {
			this.openDay = true;
			
			this.currentDay = atts.getValue(DAY_WEEKDAY);
			this.currentLectureList = new ArrayList<Lecture>();
			
		// Lecture
		} else if (qName.equals(LECTURE)) {
			
			Lecture currentLecture = new Lecture();
			
			// If the room is not available at this hour
			if(!atts.getValue(LEC_LESSON).equals(AVAILABLE)) {
				currentLecture.setAvailable(false);
			}
			
			// Create a new lecture and set the name
			currentLecture.setLectureName(atts.getValue(LEC_LESSON));
			
			// Set host
			if(atts.getValue(LEC_HOST) != null) {
				currentLecture.setHost(atts.getValue(LEC_HOST));
			}
			
			// Set type
			if(atts.getValue(LEC_TYPE) != null) {
				currentLecture.setType(atts.getValue(LEC_TYPE));
			}
			this.currentLectureList.add(currentLecture);
		}
	}

	/**
	 * Close tag-element.
	 */
	public void endElement(String uri, String localName, String qName)
			throws SAXException {

		if (this.openWeekday) {
			this.openWeekday = false;
			
		}  else if (this.openRoom) {
			
			this.building.addRoom(currentRoom);
			this.openRoom = false;
			
		} else if (this.openRoominformation) {
			
			this.openRoominformation = false;
			
		} else if (this.openDay) {
			
			this.currentRoom.addLectureDay(this.currentDay, this.currentLectureList);
			this.openDay = false;
		}
	}

	/**
	 * 
	 */
	public void characters(char[] ch, int start, int length)
			throws SAXException {

		String tagContent = new String(ch, start, length);

		// Weekdays
		if (this.openWeekday) {
			this.building.addWeekday(tagContent);
			
			if(tagContent.equals(MO)) {
				this.weekdaysBooleanArray[0] = true;
				
			} else if (tagContent.equals(TUE)) {
				this.weekdaysBooleanArray[1] = true;
				
			} else if (tagContent.equals(WED)) {
				this.weekdaysBooleanArray[2] = true;
				
			} else if (tagContent.equals(THUR)) {
				this.weekdaysBooleanArray[3] = true;
				
			} else if (tagContent.equals(FRI)) {
				this.weekdaysBooleanArray[4] = true;
				
			} else if (tagContent.equals(SAT)) {
				this.weekdaysBooleanArray[5] = true;
				
			} else if (tagContent.equals(SUN)) {
				this.weekdaysBooleanArray[6] = true;
				
			}
		}
		
		if (this.openRoominformation) {
			this.currentRoom.setRoomInformation(tagContent);
		}

	}

	public void endPrefixMapping(String prefix) throws SAXException {
		// TODO Auto-generated method stub

	}

	public void ignorableWhitespace(char[] ch, int start, int length)
			throws SAXException {
		// TODO Auto-generated method stub

	}

	public void processingInstruction(String target, String data)
			throws SAXException {
		// TODO Auto-generated method stub
	}

	public void setDocumentLocator(Locator locator) {
		// TODO Auto-generated method stub
	}

	public void skippedEntity(String name) throws SAXException {
		// TODO Auto-generated method stub
	}

	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {
		// TODO Auto-generated method stub
	}
	
	public void setBuildingFile(BuildingFile buildingFile) {
		this.building = buildingFile;
	}
}