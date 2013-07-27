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
package roommateapp.info.net;

/* imports */
import roommateapp.info.droid.Preferences;
import roommateapp.info.entities.BuildingFile;
import android.content.Context;

/**
 * DownloadDeleteHelper is designed to 
 * deleting building files.
 */
public class DownloadDeleteHelper {
	
	/**
	 * Packaging the erasing of a building file.
	 *  
	 * @param selectedBuilding which shall be deleted
	 * @param context
	 * 
	 * @return state of the operation
	 */
	public boolean deleteFile(BuildingFile selectedBuilding, Context context) {
		
		boolean status = false;
		String defaultFile = Preferences.getDefaultFile(context);
		
		// Checks if the file was set as the default file
		if (defaultFile.equals(selectedBuilding.getFile().toString())) {
			status = selectedBuilding.getFile().delete();
			if (status) {
				Preferences.setDefaultFile(context, "");
			}
			
		// Else operate a normal file delete.
		} else {
			status = selectedBuilding.getFile().delete();
		}
		return status;
	}
}