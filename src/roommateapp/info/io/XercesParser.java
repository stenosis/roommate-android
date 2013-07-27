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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import org.xml.sax.DTDHandler;
import org.xml.sax.DocumentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;
import roommateapp.info.entities.BuildingFile;
import roommateapp.info.entities.HolidayList;

/**
 * Implementation of the Xerces SAX Parser.
 */
@SuppressWarnings("deprecation")
public class XercesParser implements Parser, org.xml.sax.Parser {

	// Instance variables Xerces SAX Parser
	private XMLReader parser = new org.apache.xerces.parsers.SAXParser();

	private String rommmateFileURI;
	private CHandlerRFile cHandlerRFile;
	private EHandlerRFile eHandlerRFile;

	private String roommateFileCheckURI;
	private CHandlerRFileCheck cHandlerRFileCheck;
	private EHandlerRFileCheck eHandlerRFileCheck;
	
	private BuildingFile buildingFile;
	
	private String holidayListFileURI;
	private CHandlerHolidayFile cHandlerHolidayFile;
	private EHandlerHolidayFile eHandlerHolidayFile;
	
	private String UserUpdateFileURI;
	private CHandlerUpdater cHandlerUpdater;
	private EHandlerUpdater eHandlerUpdater;

	/**
	 * Constructor
	 * 
	 * @param uri Pfad
	 * @param cHandler ContentHandler
	 * @param eHandler ErroerHandler
	 * @throws SAXException
	 * @throws IOException
	 */
	public XercesParser() throws SAXException, IOException {
		
		this.parser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
		this.parser.setFeature("http://xml.org/sax/features/validation", true);
	}
	
	// **** UPDATE-FILE ****
	
	public void setUpdateFilePath(String uri) {
		this.UserUpdateFileURI = uri;
	}
	
	public void parseUpdateFile() throws Exception {
		this.parser.setFeature("http://xml.org/sax/features/validation", false);
		this.eHandlerUpdater = new EHandlerUpdater();
		this.cHandlerUpdater=new CHandlerUpdater();
		
		this.parser.setErrorHandler(this.eHandlerUpdater);
		this.parser.setContentHandler(this.cHandlerUpdater);
		
		try {
			this.parser.parse(this.UserUpdateFileURI);
		} catch (IOException e) {
			this.parser.setFeature("http://xml.org/sax/features/validation", true);
			e.printStackTrace();
			throw e;
		} catch (SAXException e) {
			this.parser.setFeature("http://xml.org/sax/features/validation", true);
			e.printStackTrace();
			throw e;
		
		}
		this.parser.setFeature("http://xml.org/sax/features/validation", true);
	}
	
	public ArrayList<BuildingFile>  getUpdateFileList() {
		return this.cHandlerUpdater.getBuildingFiles();
	}
	
	// **** HOLIDAY-FILE ****
	
	public void setHolidayFilePath(String uri) {
		this.holidayListFileURI = uri;
	}
	
	public void parseHolidayFile() throws Exception {
		this.eHandlerHolidayFile = new EHandlerHolidayFile();
		this.cHandlerHolidayFile = new CHandlerHolidayFile();
		this.parser.setErrorHandler(this.eHandlerHolidayFile);
		this.parser.setContentHandler(this.cHandlerHolidayFile);
		
		try {
			this.parser.parse(this.holidayListFileURI);
		} catch (IOException e) {
			e.printStackTrace();
			throw e;
		} catch (SAXException e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public HolidayList getHolidayList() {
		return this.cHandlerHolidayFile.getHolidayList();
	}

	// **** ROOMMATE-FILE CHECKER ****

	public void setRoommateFileCheckPath(String uri) {
		this.roommateFileCheckURI = uri;
	}

	public void parseRoommateFileCheck() throws Exception {
		this.eHandlerRFileCheck = new EHandlerRFileCheck();
		this.cHandlerRFileCheck = new CHandlerRFileCheck();
		this.parser.setErrorHandler(this.eHandlerRFileCheck);
		this.parser.setContentHandler(this.cHandlerRFileCheck);

		try {
			this.parser.parse(this.roommateFileCheckURI);
		} catch (IOException e) {
			e.printStackTrace();
			throw e;	
		} catch (SAXException e) {
			e.printStackTrace();
			throw e;	
		}
	}

	public BuildingFile getBuildingFile() {
		return this.cHandlerRFileCheck.getBuildingFile();
	}

	// **** ROOMMATE-FILE ****

	public void setBuildingFile(BuildingFile buildingFile) {
		this.buildingFile = buildingFile;
	}
	
	public void setRoommateFilePath(String uri) {
		this.rommmateFileURI = uri;
	}

	public void parseRoommateFile() {		
		this.eHandlerRFile = new EHandlerRFile();
		this.cHandlerRFile = new CHandlerRFile(this.buildingFile);
		this.parser.setErrorHandler(this.eHandlerRFile);
		this.parser.setContentHandler(this.cHandlerRFile);
		try {
			this.parser.parse(this.rommmateFileURI);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}

	
	// **** MISC ****

	public void parse(InputSource source) throws SAXException, IOException {
		// TODO Auto-generated method stub

	}

	public void parse(String systemId) throws SAXException, IOException {
		// TODO Auto-generated method stub

	}

	public void setDTDHandler(DTDHandler handler) {
		// TODO Auto-generated method stub

	}

	public void setDocumentHandler(DocumentHandler handler) {
		// TODO Auto-generated method stub

	}

	public void setEntityResolver(EntityResolver resolver) {
		// TODO Auto-generated method stub

	}

	public void setErrorHandler(ErrorHandler handler) {
		// TODO Auto-generated method stub

	}

	public void setLocale(Locale locale) throws SAXException {
		// TODO Auto-generated method stub

	}
}