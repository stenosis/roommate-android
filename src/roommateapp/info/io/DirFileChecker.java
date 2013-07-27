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
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import org.xml.sax.SAXException;
import roommateapp.info.entities.BuildingFile;
import android.annotation.SuppressLint;

/**
 * Finds and checks files in the Roommate 
 * directory on the SD-Card.
 */
public class DirFileChecker {

	// Instance variables
	private File roommateDirectory;
	private static final String CUSTOM_FILE_EXTENSION = ".xml";
	private Parser parser;
	private ArrayList<File> allXMLFiles;
	private ArrayList<BuildingFile> validatedFiles;
	
	/**
	 * Constructor
	 * 
	 * @param roommateDirectory
	 */
	public DirFileChecker(File roommateDirectory) {
		
		this.roommateDirectory = roommateDirectory;
		
		// Create parser
		try {
			this.parser = new XercesParser();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.allXMLFiles = getAllXmlFiles();
		this.validatedFiles = getValidatedBuildingfilesFiles(this.allXMLFiles);
	}
	
	/**
	 * Finds and returns all XML files in the Roommate diretcory.
	 * 
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	private ArrayList<File> getAllXmlFiles() {
		
		// File filter
		FileFilter fileFilter = new FileFilter() {
			
			public boolean accept(File pathname) {
				return pathname.toString().endsWith(".xml");
			}
		};
		
		File[] allFiles = this.roommateDirectory.listFiles(fileFilter);
		ArrayList<File> notvalidatedXmlFiles = new ArrayList<File>();
		
		// Check if the folder isnt empty and exists
		if (allFiles == null || allFiles.length == 0) {
			
			notvalidatedXmlFiles = new ArrayList<File>();
			
		} else {

			for (File file : allFiles) {

				String filename = file.toString();
				filename = filename.substring(filename.length() - 4,filename.length());
				if (filename.toLowerCase().equals(new String(CUSTOM_FILE_EXTENSION))) {
					
					notvalidatedXmlFiles.add(file);
				}
			}
		}
		return notvalidatedXmlFiles;
	}

	/**
	 * Validation of the found XML files
	 * 
	 * @param allXmlFiles
	 * @return gültige Roommatedatein
	 */
	private ArrayList<BuildingFile> getValidatedBuildingfilesFiles(ArrayList<File> allXmlFiles) {
		
		ArrayList<BuildingFile> validatedBuildingFiles = new ArrayList<BuildingFile>();

		for (File file : allXmlFiles) {

			this.parser.setRoommateFileCheckPath(file.toString());

			Parser fileCheckParser;
			try {
				fileCheckParser = new XercesParser();
				fileCheckParser.setRoommateFileCheckPath(file.toString());
				fileCheckParser.parseRoommateFileCheck();
				BuildingFile currentFile = fileCheckParser.getBuildingFile();

				if (currentFile != null) {
					currentFile.setFile(file);
					validatedBuildingFiles.add(currentFile);
				}

			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
		return validatedBuildingFiles;
	}

	/**
	 * Returns all XML files in the Roommate directory.
	 * 
	 * @return
	 */
	public ArrayList<File> getAllXMLFiles() {
		
		return allXMLFiles;
	}

	/**
	 * Returns all valid Roommate files.
	 * 
	 * @return
	 */
	public ArrayList<BuildingFile> getValidatedFiles() {
		
		return validatedFiles;
	}
	
	/**
	 * 
	 * @param path
	 * @return
	 */
	public ArrayList<BuildingFile> getUpdateBuildingfileList(String path) {
		
		ArrayList<BuildingFile> updateFiles = new ArrayList<BuildingFile>();
		Parser fileCheckParser;
		try {
			fileCheckParser = new XercesParser();
			fileCheckParser.setUpdateFilePath(path);
			fileCheckParser.parseUpdateFile();
			updateFiles = fileCheckParser.getUpdateFileList();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return updateFiles;
	}
	
	/**
	 * 
	 * @param path
	 * @return
	 */
	public ArrayList<BuildingFile> getUpdatePublicBuildingfileList(String path) {
		
		ArrayList<BuildingFile> updateFiles = new ArrayList<BuildingFile>();
		Parser fileCheckParser;
		try {
			fileCheckParser = new XercesParser();
			fileCheckParser.setUpdateFilePath(path);
			fileCheckParser.parseUpdateFile();
			updateFiles = fileCheckParser.getUpdateFileList();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return updateFiles;
	}
}