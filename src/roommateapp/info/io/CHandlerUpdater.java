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
package roommateapp.info.io;

/* imports */
import java.util.ArrayList;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import roommateapp.info.droid.RoommateConfig;
import roommateapp.info.entities.BuildingFile;

/**
 * Content handler of the Roommate filelist.
 * 
 */
public class CHandlerUpdater implements ContentHandler {

	// Instance variables
	private boolean openInfoTag;
	private static final String FILE = "file";
	private static final String BUILDINGNAME = "buildingname";
	private static final String RELEASE = "release";
	private static final String DATE = "date";
	private static final String FILENAME = "filename";
	private static final String ID = "buildingid";
	
	private ArrayList<BuildingFile> validatedBuildingfiles = new ArrayList<BuildingFile>();
	private BuildingFile currentFile = new BuildingFile();

	/**
	 * Returns the parsed filelist.
	 * 
	 * @return
	 */
	public  ArrayList<BuildingFile> getBuildingFiles() {
		return this.validatedBuildingfiles;
	}

	/**
	 * Start parsing the Roommate filelist.
	 */
	public void startDocument() throws SAXException {
		if (RoommateConfig.VERBOSE) {
			System.out.println("Start checking Roommate online fileList");
		}
	}

	/**
	 * Stop parsing the Roommate filelist.
	 */
	public void endDocument() throws SAXException {
		if (RoommateConfig.VERBOSE) {
			System.out.println("Stop checking Roommate online fileList");
		}
	}

	/**
	 * Checks the xml-tag and get the appropriate data.
	 */
	public void startElement(String uri, String localName, String qName,
			Attributes atts) throws SAXException {

		// Config-Tag
		if (qName.equals(FILE)) {
			
			currentFile.setBuildingname(atts.getValue(BUILDINGNAME));
			currentFile.setDate(atts.getValue(DATE));
			currentFile.setRelease(atts.getValue(RELEASE));
			currentFile.setId(atts.getValue(ID));
			currentFile.setFilename(atts.getValue(FILENAME));
			
			if (currentFile.getBuildingname() != null && currentFile.getDate() != null && currentFile.getRelease() != null && currentFile.getId() != null && currentFile.getFilename() != null){
				validatedBuildingfiles.add(currentFile);
			}
			currentFile = new BuildingFile();
		}
	}

	/**
	 * Close tag-element.
	 */
	public void endElement(String uri, String localName, String qName) throws SAXException {

		// Closing the info-tag
		if (this.openInfoTag) {
			this.openInfoTag = false;
		}
	}

	/**
	 * Get the content string of the tag.
	 * <tag>content</tag>
	 */
	public void characters(char[] ch, int start, int length) throws SAXException {
		
		// Content of the tag
		String tagString = new String(ch, start, length);
		
		// Info-tag content
		if (this.openInfoTag) {
			currentFile.setInfo(tagString);
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
}