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
import java.util.HashMap;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import roommateapp.info.droid.RoommateConfig;
import roommateapp.info.entities.HolidayList;

/**
 * CHandlerHolidayFile is a content handler for the
 * holiday file.
 */
public class CHandlerHolidayFile implements ContentHandler {
	
	// Instance variables
	private HolidayList holidayList = new HolidayList();
	private HashMap<String, String> holidays;
	private String currentState = "";
	private boolean openHolidays;
	private static final String ROOMMATEHOLIDAY = "roommateHolidayList";
	private static final String VERSION = "version";
	private static final String HOLIDAYS = "holidays";
	private static final String STATE = "state";
	private static final String HOLIDAY = "holiday"; 
	private static final String DATE = "date";
	private static final String HOLIDAYNAME = "name";
	
	/**
	 * Returns the Roommate holiday file.
	 * 
	 * @return HolidayList.
	 */
	public HolidayList getHolidayList() {
		
		return this.holidayList;
	}
	
	/**
	 * Start parsing the Roommate holiday file.
	 */
	public void startDocument() throws SAXException {
		
		if (RoommateConfig.VERBOSE) {
			System.out.println("Start checking Roommate-Holiday file..");
		}
	}
	
	/**
	 * Stop parsing the Roommate holiday file.
	 */
	public void endDocument() throws SAXException {
		if (RoommateConfig.VERBOSE) {
			System.out.println("Stop checking Roommate-Holiday file..");
		}
	}

	/**
	 * Checks the xml-tag and get the appropriate  data.
	 */
	public void startElement(String uri, String localName, String qName,
			Attributes atts) throws SAXException {
		
		// Roommate-Tag
		if(qName.equals(ROOMMATEHOLIDAY)) {
			this.holidayList.setVersionNumber(Integer.parseInt(atts.getValue(VERSION)));
			
	    // Holidays-Tag
		} else if (qName.equals(HOLIDAYS)) {
			this.openHolidays = true;
			this.currentState = atts.getValue(STATE);
			this.holidays = new HashMap<String, String>();
		
		// Holiday-Tag
		} else if (qName.equals(HOLIDAY)) {
			this.holidays.put(atts.getValue(DATE), atts.getValue(HOLIDAYNAME));
		}
	}
	
	/**
	 * Close tag-element.
	 */
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		
		if(this.openHolidays) {
			
			this.holidayList.addHolidayList(this.currentState, this.holidays);
			this.openHolidays = false;
		}
	}
	
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		// TODO Auto-generated method stub
		
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
}