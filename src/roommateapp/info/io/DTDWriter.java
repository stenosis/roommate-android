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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import roommateapp.info.droid.RoommateConfig;

/**
 * DTDWriter writes the dtd files for the
 * Roommate files to the SD-Card into the Roommate folder.
 */
public class DTDWriter {

	// Instance variables
	private File dtdDirectory;
	private File miscDiretcory;
	private static final String ERRORMSG = "ERROR: Could not write roommate.dtd";

	/**
	 * Constructor
	 * 
	 * @param roommateDirectory
	 */
	public DTDWriter(File roommateDirectory) {
		
		this.dtdDirectory = new File(roommateDirectory.toString() + "/" + RoommateConfig.DTD_FOLDER);
		this.dtdDirectory.mkdir();
		this.miscDiretcory = new File(roommateDirectory.toString() + "/" + RoommateConfig.MISC_FOLDER);
		this.miscDiretcory.mkdir();
		
		if (RoommateConfig.VERBOSE) {
			System.out.println("Directory: " + this.dtdDirectory.toString());
		}
	}

	/**
	 * Write the RoommateFile.dtd to the SD-Card.
	 * 
	 * @return Status Operation
	 */
	public boolean createRoommateFileDTD() {
		
		boolean succes = false;
		try {

			// Write RoommateFile.dtd file
			File roommateDTD = new File(dtdDirectory, "roommate.dtd");
			BufferedWriter bWriterFileDTD = new BufferedWriter(new FileWriter(roommateDTD));
			bWriterFileDTD.write("<!ELEMENT roommate (config, roomlist)>\n\n<!ELEMENT config (geo*, info*, weekdays, lectureTimes)>\n<!ATTLIST config\n\tdate CDATA #REQUIRED\n\trelease CDATA #REQUIRED\n\tbuildingID CDATA #REQUIRED\n\tbuildingname CDATA #REQUIRED\n\tstate CDATA #IMPLIED\n\tstate CDATA #IMPLIED\n\tpublic CDATA #IMPLIED\n>\n\n");
			bWriterFileDTD.write("<!ELEMENT geo EMPTY>\n<!ATTLIST geo\n\tlat CDATA #IMPLIED\n\tlng CDATA #IMPLIED\n>\n\n");
			bWriterFileDTD.write("<!ELEMENT info (#PCDATA)>\n\n<!ELEMENT weekdays (weekday+)>\n<!ELEMENT weekday (#PCDATA)>\n\n");
			bWriterFileDTD.write("<!ELEMENT lectureTimes (time+)>\n<!ELEMENT time EMPTY>\n<!ATTLIST time\n\tstart CDATA #REQUIRED\n\tend CDATA #REQUIRED\n>\n\n");
			bWriterFileDTD.write("<!ELEMENT roomlist (room+)>\n<!ATTLIST roomlist\n\tsemester CDATA #IMPLIED\n>\n\n");
			bWriterFileDTD.write("<!ELEMENT room (roominfo*, properties+, exceptions*, lectureList*)>\n<!ATTLIST room\n\troomname CDATA #REQUIRED\n\troomsize CDATA #IMPLIED\n>\n<!ELEMENT roominfo (#PCDATA)>\n\n");
			bWriterFileDTD.write("<!ELEMENT properties (property+)>\n<!ELEMENT property EMPTY>\n<!ATTLIST property\n\tentity CDATA #REQUIRED\n>\n\n");
			bWriterFileDTD.write("<!ELEMENT exceptions (exception+)>\n<!ELEMENT exception EMPTY>\n<!ATTLIST exception\n\tdate CDATA #REQUIRED\n\tstart CDATA #REQUIRED\n\tend CDATA #REQUIRED\n>\n\n");
			bWriterFileDTD.write("<!ELEMENT lectureList (day+)>\n<!ELEMENT day (lecture*)>\n<!ATTLIST day\n\tweekday CDATA #REQUIRED\n>\n\n");
			bWriterFileDTD.write("<!ELEMENT lecture EMPTY>\n<!ATTLIST lecture\n\tlesson CDATA #REQUIRED\n\ttype CDATA #IMPLIED\n\thost CDATA #IMPLIED\n>");
			bWriterFileDTD.flush();
			bWriterFileDTD.close();
			succes = true;
			
		} catch (IOException e) {
			
			e.printStackTrace();
			System.out.println(ERRORMSG);
		}
		return succes;
	}
	
	/**
	 * Write the holidays.dtd file to the SD-Card.
	 * 
	 * @return
	 */
	public boolean createHolidayFileDTD() {
		boolean succes = false;
		try {

			// Write holidays.dtd file
			File holydaysDTD = new File(miscDiretcory, "holidays.dtd");
			BufferedWriter bWriterFileDTD = new BufferedWriter(new FileWriter(holydaysDTD));
			bWriterFileDTD.write("<!ELEMENT roommateHolidayList (holidays+)>\n<!ELEMENT holidays (holiday+)>\n");
			bWriterFileDTD.write("<!ATTLIST roommateHolidayList\n\tversion CDATA #REQUIRED\n>\n");
			bWriterFileDTD.write("<!ATTLIST holidays\n\tstate CDATA #REQUIRED\n>\n");
			bWriterFileDTD.write("<!ELEMENT holiday EMPTY>\n<!ATTLIST holiday\n\tdate CDATA #REQUIRED\n\tname CDATA #REQUIRED\n>");
			bWriterFileDTD.flush();
			bWriterFileDTD.close();
			succes = true;
			
		} catch (IOException e) {
			
			e.printStackTrace();
			if (RoommateConfig.VERBOSE) {
				System.out.println("DTD ging schief!");
			}
		}
		return succes;
	}
}