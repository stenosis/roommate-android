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
package roommateapp.info.droid;

/**
 * The RoommateConfig contains key values
 * used all over the Application.
 */
public class RoommateConfig {
	
	// Display debug messages
	public static final boolean VERBOSE = false;
	
	// Roommate directory on the SD-Card
	public static final String ROOMMATE_SDCARD_DIR = "/Roommate/";

	// Roommate webpage URL
	public static final String ROOMMATE_URL = "http://www.roommateapp.info";
	public static final String URL = "http://www.roommateapp.info";
	
	// Roommate appversion file URL
	public static final String URL_APPVERSION = "http://www.roommateapp.info/appversion.txt";
	
	// Path to the user directorys on the webpage
	public static final String USER_PATH_PREFIX = "/downloads/xml/users/";
	
	// Path to the official public files on the webpage
	public static final String OFFICIAL_PATH_PREFIX = "/downloads/xml/public";
	
	// Website port
	public static final String PORT = "80";
	
	// Filelist of public building files
	public static final String FILELIST_FILENAME = "/filelist.xml";
	
	// Filename saved on the SD-Card where the files are saved
	public static final String PUBLIC_FILELIST_FILENAME_ON_SD = "publicfilelist.xml";
	public static final String USER_FILELIST_FILENAME_ON_SD = "userfilelist.xml";
	
	// Holidayfile list URL
	public static final String HOLIDAYFILE_URL = "http://www.roommateapp.info:/downloads/xml/public/holidayfiles/holidaylist.xml";
	
	// Holidayfile name
	public static final String HOLIDAYFILE = "holidaylist.xml";
	
	// Path to the holidayfile list on the SD-Card
	public static final String HOLIDAYFILE_URI = "/.misc/holidaylist.xml";
	
	// Misc directory name
	public static final String MISC_FOLDER = ".misc";
	public static final String DTD_FOLDER = ".dtd";
	
	// Roommatefiles tag start
	public static final String XML_HEADER = "<roommatefiles>";
	public static final String XML_HEADER2 = "<roommatefiles/>";
	
	// Download buffer
    public static final int BUFFER_SIZE=1024;
    
    // GPLv3 URL
    public static final String GPL_URL = "http://www.gnu.org/licenses/gpl-3.0.html";
    
    // Google Plus profile URL
    public static final String GOOGLEPLUS_URL = "https://plus.google.com/107063390698022207347";
    
    // Facebook profile URL
    public static final String FACEBOOK_URL = "https://www.facebook.com/roommateapp";
    
    // GitHub profile URL
    public static final String GITHUB_URL = "https://github.com/stenosis/roommate";
    
    // Zoom level for Google Maps
    public static final int MAPZOOMLVL = 16;
}
