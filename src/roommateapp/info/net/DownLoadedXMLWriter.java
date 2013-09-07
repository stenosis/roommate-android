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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import roommateapp.info.droid.RoommateConfig;
import android.os.Environment;

/**
 * DownLoadedXMLWriter can write downloaded
 * files to the SD-Card.
 */
public class DownLoadedXMLWriter {
	
	/**
	 * writeDownloadedFileToDisk writes already downloaded
	 * files to a specific directory.
	 * 
	 * @param filename of the file
	 * @param folder destination directory
	 * @param content of the file
	 * 
	 * @return true falls das schreiben erfolgreich war, false wenn nicht.
	 */
	public static boolean writeDownloadedFileToDisk(String filename, String folder, String content) {
		
		boolean succes = false;
		File roommateDirectory = new File(Environment.getExternalStorageDirectory() +RoommateConfig.ROOMMATE_SDCARD_DIR+folder) ;
		roommateDirectory.mkdirs();

		try {
			
			File downloadedFile = new File(roommateDirectory, filename);
			BufferedWriter bWriterFileDTD = 
					new BufferedWriter(new FileWriter(downloadedFile));
			bWriterFileDTD.write(content);
			bWriterFileDTD.flush();
			bWriterFileDTD.close();
			succes = true;
		} catch (Exception e) {
			
			e.printStackTrace();
			System.out.println("ERROR: Failed to write downloaded file!");
		}
		return succes;
	}
}