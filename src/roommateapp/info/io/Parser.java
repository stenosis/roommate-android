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
import roommateapp.info.entities.BuildingFile;
import roommateapp.info.entities.HolidayList;


/**
 * Interface for parsing the Roommate files.
 */
public interface Parser {

	// ROOMMATE-FILE CHECK
	public void setRoommateFileCheckPath(String uri);

	public void parseRoommateFileCheck() throws Exception;

	public BuildingFile getBuildingFile();

	// ROOMMATE-FILE
	public void setBuildingFile(BuildingFile buildingFile);
	
	public void setRoommateFilePath(String uri);

	public void parseRoommateFile();
	
	// HOLIDAY-FILE
	public void setHolidayFilePath(String uri);
	
	public void parseHolidayFile() throws Exception;
	
	public HolidayList getHolidayList();
	
	public void setUpdateFilePath(String uri);
	
	public void parseUpdateFile() throws Exception;
	
	public ArrayList<BuildingFile> getUpdateFileList();
}