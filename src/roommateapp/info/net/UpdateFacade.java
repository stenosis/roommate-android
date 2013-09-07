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
import java.io.File;
import java.util.ArrayList;
import roommateapp.info.droid.ActivitySync;
import roommateapp.info.droid.ActivityWithResponse;
import roommateapp.info.droid.Preferences;
import roommateapp.info.droid.RoommateConfig;
import roommateapp.info.entities.BuildingFile;
import roommateapp.info.io.DirFileChecker;
import android.content.Context;
import android.os.Environment;

/**
 * The UpdateFacade class provides several methods for
 * checking if there are building file updates available 
 * on the Roommate webservice. Public and user files.
 */
public class UpdateFacade {

	// Instance variables
	private Context applicationContext;

	/**
	 * Consctructor.
	 * 
	 * @param applicationContext
	 */
	public UpdateFacade(Context applicationContext) {
		
		this.applicationContext = applicationContext;
	}
	
	/**
	 * Returns a list with all valid (DTD based) Roommate files
	 * which are saved on the SD-card.
	 * 
	 * @return List with all valid Roommate files
	 */
	public ArrayList<BuildingFile> getValidatetFilesfromSD() {
		
		File roommateDir = new File(Preferences.getRoommateDir(applicationContext));DirFileChecker dirFileChecker = new DirFileChecker(roommateDir);
		return dirFileChecker.getValidatedFiles();
	}
	
	/**
	 * Parsing the userfilelist.xml which was downloaded
	 * from the ActivitySync anc checks for available updates
	 * of those building files.
	 * 
	 * @return List with user files
	 */
	public ArrayList<BuildingFile> getDownloadedUserFileList() {
		
		File userFileListOnSd = new File(Preferences.getRoommateDir(this.applicationContext) + "/"+ RoommateConfig.MISC_FOLDER + "/" + RoommateConfig.USER_FILELIST_FILENAME_ON_SD);
		 ArrayList<BuildingFile> result= new ArrayList<BuildingFile>();
		if (userFileListOnSd.canRead()){
			result =new DirFileChecker(userFileListOnSd).getUpdateBuildingfileList(userFileListOnSd.toString());
		}
		return result;
	}
	
	/**
	 * Parsing the publicfilelist.xml which was downloaded
	 * from the ActivitySync and checks for available updaes
	 * of those building files.
	 * 
	 * @return List with official public files from the webservice.
	 */
	public ArrayList<BuildingFile> getDownloadedPublicFileList() {
		
		File publicFileListOnSd = new File(	Preferences.getRoommateDir(this.applicationContext) + "/"+ RoommateConfig.MISC_FOLDER + "/"+ RoommateConfig.PUBLIC_FILELIST_FILENAME_ON_SD);
		return new DirFileChecker(publicFileListOnSd).getUpdateBuildingfileList(publicFileListOnSd.toString());
	}

	/**
	 * Checks if user files are saved on the SD-card and if so,
	 * if those local files are outdated. Returns the outdated list.
	 * 
	 * @return User files that can be updated.
	 */
	public ArrayList<BuildingFile> getNewerVersionsOfUserFiles() {

		ArrayList<BuildingFile> newerVersionsOfUserFiles = new ArrayList<BuildingFile>();
		ArrayList<BuildingFile> userAccountFilesFromServer = getDownloadedUserFileList();
		ArrayList<BuildingFile> validatedFilesFromSd = getValidatetFilesfromSD();

		for (BuildingFile file : userAccountFilesFromServer) {
			if (validatedFilesFromSd.contains(file)) {
				if (validatedFilesFromSd.get(validatedFilesFromSd.indexOf(file)).isOlder(file)) {
					
					file.setPublicStatus(false);
					file.setFile(validatedFilesFromSd.get(validatedFilesFromSd.indexOf(file)).getFile());
					newerVersionsOfUserFiles.add(file);
				}

			} else {
				
				// Checks user files that are not saved on the device (Online only)
				file.setOnSD(false);
				file.setPublicStatus(false);
				newerVersionsOfUserFiles.add(file);
			}
		}
		return newerVersionsOfUserFiles;
	}

	/**
	 * Checks if the user file is saved on the SD-card (with the same ID)
	 * If so, the method will return a list with those outdated files.
	 * 
	 * @return List with user files that can be updated.
	 */
	public ArrayList<BuildingFile> getNewerVersionsOfUserFilesMain() {

		ArrayList<BuildingFile> newerVersionsOfUserFiles = new ArrayList<BuildingFile>();
		ArrayList<BuildingFile> userAccountFilesFromServer = getDownloadedUserFileList();
		ArrayList<BuildingFile> validatedFilesFromSd = getValidatetFilesfromSD();

		for (BuildingFile file : userAccountFilesFromServer) {
			if (validatedFilesFromSd.contains(file)) {
				if (validatedFilesFromSd
						.get(validatedFilesFromSd.indexOf(file)).isOlder(file)) {
					
					file.setPublicStatus(false);
					file.setFile(validatedFilesFromSd.get(
							validatedFilesFromSd.indexOf(file)).getFile());
					newerVersionsOfUserFiles.add(file);
				}
			}
		}
		return newerVersionsOfUserFiles;
	}
		
	/**
	 * Returns a list with files that can be updated. (Public and user files combined)
	 * Does not check if the files really saved on the SD-card.
	 * 
	 * 
	 * @return List with files that can be updated.
	 */
	public ArrayList<BuildingFile> getAllFilesThatCanBeUpdated() {
		
		ArrayList<BuildingFile> allFilesThatCanBeUpdated = new ArrayList<BuildingFile>();
		allFilesThatCanBeUpdated.addAll(getNewerVersionsOfUserFiles());
		allFilesThatCanBeUpdated.addAll(getNewerVersionsOfPublicFiles());
		return allFilesThatCanBeUpdated;
	}
	
	
	/**
	 * Returns a list with files that can be updated. (Public and user files combined)
	 * Does not check if the files really saved on the SD-card.
	 * 
	 * 
	 * @return List with files that can be updated.
	 */
	public ArrayList<BuildingFile> getAllPublicFilesThatCanBeUpdated() {
		
		ArrayList<BuildingFile> allFilesThatCanBeUpdated = new ArrayList<BuildingFile>();
		allFilesThatCanBeUpdated.addAll(getNewerVersionsOfPublicFiles());
		return allFilesThatCanBeUpdated;
	}
	
	
	/**
	 * Returns a list with all lokal files that can
	 * be updated. Important for the main.
	 * 
	 * @return List with files that can be updated.
	 */
	public ArrayList<BuildingFile> getAllFilesThatCanBeUpdatedforMAin() {
		
		ArrayList<BuildingFile> allFilesThatCanBeUpdated = new ArrayList<BuildingFile>();
		allFilesThatCanBeUpdated.addAll(getNewerVersionsOfUserFilesMain());
		allFilesThatCanBeUpdated.addAll(getNewerVersionsOfPublicFiles());
		return allFilesThatCanBeUpdated;
	}
	
	/**
	 * Checks if there are public files on the SD-Card with the
	 * same ID and if so, which are outdated.
	 * 
	 * @return List with all public files that can be updated.
	 */
	public ArrayList<BuildingFile> getNewerVersionsOfPublicFiles() {
		
		ArrayList<BuildingFile> newerVersionsOfPublicFiles = new ArrayList<BuildingFile>();
		ArrayList<BuildingFile> publicFilesFromServer = getDownloadedPublicFileList();
		ArrayList<BuildingFile> validatedFilesFromSd = getValidatetFilesfromSD();
		
		// Check if the file could be the same as in the user and public dir
		ArrayList<BuildingFile> allUserFilesOnServer =getDownloadedUserFileList();
		newerVersionsOfPublicFiles = new ArrayList<BuildingFile>();
		for (BuildingFile file : publicFilesFromServer) {
			if (validatedFilesFromSd.contains(file) && !allUserFilesOnServer.contains(file) ) {
				if (validatedFilesFromSd.get(validatedFilesFromSd.indexOf(file)).isOlder(file)) {
					file.setPublicStatus(true);				
					newerVersionsOfPublicFiles.add(file);
				}
			}
		}
		return newerVersionsOfPublicFiles;
	}
	
	/**
	 * Retuns all Public files, which are not on the SD card.
	 * 
	 * @return List with all public files that can be updated.
	 */
	public ArrayList<BuildingFile> getAllPublicFiles() {

		ArrayList<BuildingFile> allPublicFiles = new ArrayList<BuildingFile>();
		ArrayList<BuildingFile> publicFilesFromServer = getDownloadedPublicFileList();
		ArrayList<BuildingFile> validatedFilesFromSd = getValidatetFilesfromSD();

		allPublicFiles = new ArrayList<BuildingFile>();
		for (BuildingFile file : publicFilesFromServer) {

			if (!validatedFilesFromSd.contains(file)) {
				file.setPublicStatus(true);
				allPublicFiles.add(file);
			} 
		}
		return allPublicFiles;
	}
	

	/**
	 * Update all files.
	 */
	public void UpdateAllFiles(ActivityWithResponse activity) {
		
		ArrayList<BuildingFile> filesThatCanBeUpdated = getAllFilesThatCanBeUpdated();
		
		// Debug
		if (RoommateConfig.VERBOSE) {
			System.out.println("All files shall be updated: "+ filesThatCanBeUpdated);
		}
		String tempEmail = Preferences.getEmail(activity
				.getApplicationContext());
		String tempPw = Preferences.getPw(activity.getApplicationContext());

		// Debug
		if (RoommateConfig.VERBOSE) {
			System.out.println("Those files shall be updated: "+ filesThatCanBeUpdated );
		}

		new FileDownloadTask(activity,filesThatCanBeUpdated,this).execute(tempEmail, tempPw,"");
	}
	
	/**
	 * Update all files.
	 */
	public void UpdateAllFilesMain(ActivityWithResponse activity,ArrayList<BuildingFile> files ) {
		
		// Debug
		if (RoommateConfig.VERBOSE) {
			System.out.println("All files shall be updated: "+ files);
		}
		String tempEmail = Preferences.getEmail(activity
				.getApplicationContext());
		String tempPw = Preferences.getPw(activity.getApplicationContext());

		// Debug
		if (RoommateConfig.VERBOSE) {
			System.out.println("Those files need to be updated: "+ files );
		}

		new FileDownloadTask(activity,files,this).execute(tempEmail, tempPw,"");
	}
	
	/**
	 * Only update selected files.
	 * 
	 * @param activity 
	 * @param filesToBeUpdated
	 */
	public void UpdateSelectedFiles(ActivityWithResponse activity,ArrayList<BuildingFile> filesToBeUpdated) {
		
		// Debug
		if (RoommateConfig.VERBOSE) {
			System.out.println("Those files need to be update: "+ filesToBeUpdated);
		}
		String tempEmail = Preferences.getEmail(activity.getApplicationContext());
		String tempPw = Preferences.getPw(activity.getApplicationContext());

		// Task for downloading the collection
		new FileDownloadTask(activity, filesToBeUpdated, this).execute(tempEmail, tempPw, "");
	}
	
	/**
	 * Deletes a file from the SD-Card.
	 * 
	 * @param fileToDelete
	 * @param activity
	 */
	public boolean deleteFile(BuildingFile fileToDelete,ActivitySync activity){
		
		return new File(Preferences.getRoommateDir(activity.getApplicationContext()) + "/"+ fileToDelete).delete();
	}
	
	/**
	 * Downloads the filelist from the Roommate webservice.
	 * 
	 * @param activity
	 */
	public void downloadAllUpdateFileLists(ActivityWithResponse activity) {
		
		String tempEmail = Preferences.getEmail(activity.getApplicationContext());
		String tempPw = Preferences.getPw(activity.getApplicationContext());
		new FileListDownloader(activity,true).execute(tempEmail, tempPw, "");
	}
	
	/**
	 * Downloads only the official public file list.
	 * 
	 * @param activity
	 */
	public void downloadPublicUpdateFileLists(ActivityWithResponse activity) {
		
		String tempEmail = Preferences.getEmail(activity.getApplicationContext());
		String tempPw = Preferences.getPw(activity.getApplicationContext());
		new FileListDownloader(activity,false).execute(tempEmail, tempPw, "");
	}
		
	/**
	 * Deltes old update lists.
	 */
	public boolean deleteOldLists() {
		
		File publicFileList = new File(Environment.getExternalStorageDirectory() + RoommateConfig.ROOMMATE_SDCARD_DIR+ RoommateConfig.MISC_FOLDER + "/"+ RoommateConfig.PUBLIC_FILELIST_FILENAME_ON_SD);
		File userFileList = new File(Environment.getExternalStorageDirectory() + RoommateConfig.ROOMMATE_SDCARD_DIR+ RoommateConfig.MISC_FOLDER + "/"+ RoommateConfig.USER_FILELIST_FILENAME_ON_SD);

		if (publicFileList != null) {
			publicFileList.delete();
		}
		if (userFileList != null) {
			userFileList.delete();
		}
		return true;
	}
}