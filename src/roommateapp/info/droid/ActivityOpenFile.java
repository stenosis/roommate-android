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

/* imports */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.xml.sax.SAXException;
import roommateapp.info.entities.BuildingFile;
import roommateapp.info.io.DTDWriter;
import roommateapp.info.io.Parser;
import roommateapp.info.io.XercesParser;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import roommateapp.info.R;

/**
 * The "ActivityOpenFile" checks opend
 * files if they are valid Roommate files.
 */
public class ActivityOpenFile extends Activity {

	// Instance variables
	private boolean valide;
	private boolean openFromRoommateDir;
	private File fileToOpen;
	private File roommateDirectory;
	private BuildingFile currenBuilding;
	
	/**
	 * On create of the activity
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_openfile);

		// Set text shadow effects
		setTextShadow();
		
		// Getting last intents name
		Intent intent = getIntent();
		String filepath = intent.getData().getEncodedPath();
		this.fileToOpen = new File(filepath);
		
		// Setting up the SD-Card directorys
		setUpSDCardFolder();
		
		// Debug
		if (RoommateConfig.VERBOSE) {
			System.out.println("DEBUG");
			System.out.println("RoomateDir Absolute Path: " + this.roommateDirectory.getAbsolutePath());
			System.out.println("RoommateDir Path:" + this.roommateDirectory.getPath());
			System.out.println("File Absolute Path: " + this.fileToOpen.getAbsolutePath());
			System.out.println("File Path: " + this.fileToOpen.getPath());
			System.out.println("SD dir: " + Environment.getExternalStorageDirectory().getAbsolutePath());
			System.out.println("SD state: " + Environment.getExternalStorageState());
		}
		
		// If the file wasnt open from the Roommate directory, copy the file to it
		if(!this.fileToOpen.getParent().equals(this.roommateDirectory.getAbsolutePath())) {
			if (RoommateConfig.VERBOSE) {
				System.out.println("DEBUG: Copy file into the Roommate directory.");
			}
			copyFileIntoRoomateDir(fileToOpen);
		} else {
		
			this.openFromRoommateDir = true;
		}
		
		// Check if selected file is valid
		this.valide = checkValidate();
		if(this.valide) {
						
			// Displaying file information
			TextView infotext = (TextView) this.findViewById(R.id.openfileBuildingInfo);
			
			// Name and date
			String text = getString(R.string.dialog_file) + ": " + fileToOpen.getName() + "\n"
					+ getString(R.string.dialog_building) + ": " + this.currenBuilding.getBuildingname() + "\n"
					+ getString(R.string.dialog_date) + ": " +this.currenBuilding.getDate() + "\n"
					+ getString(R.string.dialog_version) + ": " + this.currenBuilding.getRelease();
			
			// Infostring of the file
			if(this.currenBuilding.getInfo() != null && this.currenBuilding.getInfo().length() > 0) {
				text = text + "\n\nInformation:\n" + this.currenBuilding.getInfo();
			}
			
			infotext.setText(text);
						
		// If the file is not valid
		} else {

			// Delete the file
			this.fileToOpen.delete();
			
			if (RoommateConfig.VERBOSE) {
				System.out.println("ERROR: failed to open file");
			}
			
			// Deaktivate Buttons
			Button btnYes = (Button) this.findViewById(R.id.btnOpenFileYes);
			btnYes.setEnabled(false);
			
			Button btnNo = (Button) this.findViewById(R.id.btnOpenFileNo);
			btnNo.setEnabled(false);
			
			// Dialog to quit the program
	        AlertDialog.Builder eDialog = new AlertDialog.Builder(this);
	        eDialog.setTitle(getString(R.string.dialog_error));
	        eDialog.setMessage(getString(R.string.novalidfile));
	        eDialog.setCancelable(false);
	        
	        eDialog.setPositiveButton(getString(R.string.dialog_close), 
	        		
	        		new DialogInterface.OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
							
							finish();
							
						}
					});
	        
	        eDialog.create();
	        eDialog.show();
		}
    }
    
	/**
	 * Check if the downloaded file is valid.
	 * 
	 * @return validation 
	 */
	public boolean checkValidate() {
		
		boolean valide = false;
		
		try {
			Parser checkParser = new XercesParser();
			checkParser.setRoommateFileCheckPath(this.fileToOpen.toString());
			
			try {
				checkParser.parseRoommateFileCheck();
				this.currenBuilding = checkParser.getBuildingFile();
				valide = true;	
				
			} catch (Exception e) {
				
				// Validation failed
				valide = false;
				e.printStackTrace();
			}
			
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return valide;
	}
	
	/**
	 * 
	 * Setting up the necessary directorys 
	 * and files on the SD-Card.
	 */
	public void setUpSDCardFolder() {
		
		// Setting up SD-Cart directorys
		String path = Preferences.getRoommateDir(getApplicationContext());
		if (path != null) {
			this.roommateDirectory = new File(path);
			
		} else {
			
			this.roommateDirectory = new File(Environment.getExternalStorageDirectory() + RoommateConfig.ROOMMATE_SDCARD_DIR);
			roommateDirectory.mkdirs();
			Preferences.setRoommateDIR(getApplicationContext(), this.roommateDirectory.toString());
		}
		
		// Write DTD-files
		File dtdDirectory = new File(this.roommateDirectory.toString() + "/.dtd");
		if(!dtdDirectory.exists()) {
			
			DTDWriter dtdWriter = new DTDWriter(roommateDirectory);
			dtdWriter.createRoommateFileDTD();
			dtdWriter.createHolidayFileDTD();
		}
	}
    
	/**
	 * Copy a file to the Roommate diretcory
	 * and check if the file is valid.
	 * 
	 * @return
	 */
	public void copyFileIntoRoomateDir(File source) {
			
		// Create a file on the destination
		String filename = source.getName();
		File outputFile = new File(this.roommateDirectory, filename);
		
		// If the file already exists
		if(outputFile.exists()) {
			
			if(outputFile.getPath().contains("-copy")) {
			
				String file = outputFile.getPath();
				String number = file.substring((file.length() - 6), (file.length() - 4));
				if (RoommateConfig.VERBOSE) {
					System.out.println("Substring: " + number);
				}
				
				try {
					
					Integer counter = Integer.parseInt(number);
					counter++;
					String count = "" + counter;
					
					if(counter < 10) {
						count = "0" + counter;
					}
					
					String ending = "-copy" + count + ".xml";
					filename = filename.substring(0, (filename.length() - 11));
					filename = filename + ending;
					if (RoommateConfig.VERBOSE) {
						System.out.println(filename);
					}
					outputFile = new File(this.roommateDirectory, filename);
					
				} catch (NumberFormatException e) {

					String ending = "-copy01.xml";
					filename = filename.replace(".xml", ending);
					outputFile = new File(this.roommateDirectory, filename);
				}				
				
			} else {
				
				String ending = "-copy01.xml";
				filename = filename.replace(".xml", ending);
				outputFile = new File(this.roommateDirectory, filename);
			}
		}
		
		// Copy the file data
		try {
			
			InputStream in = new FileInputStream(source);
			OutputStream out = new FileOutputStream(outputFile);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0){
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        	if (RoommateConfig.VERBOSE) {
            	System.out.println("File copied to " + outputFile.getAbsolutePath());
            }
            this.fileToOpen = outputFile;
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch(IOException e){
			if (RoommateConfig.VERBOSE) {
            System.out.println(e.getMessage());
			}
        }
	}

	/**
	 * Create some text shadow effects.
	 */
	private void setTextShadow() {
		
		TextView headline = (TextView) this.findViewById(R.id.openfileBuildinginfoHeadline);
		headline.setShadowLayer(1, 0, 1, Color.WHITE);
	}

	/**
	 * Return to the ActivityMain by clicking
	 * on the Yes-Button.
	 * 
	 * @param view
	 */
	public void onClickYes(View view) {

		Intent intent = new Intent(this.getApplicationContext(), ActivityMain.class);
		intent.putExtra("openStd", false);
		startActivity(intent);
	}
	
	/**
	 * Exit the Roommate by clicking on the
	 * No-Button.
	 * 
	 * @param view
	 */
	public void onClickNo(View view) {

		// Delete the file if it wasnt opend from the Roomate directory
		if(!this.openFromRoommateDir) {
			this.fileToOpen.delete();
		}
		finish();
	}
	
	/**
	 * Quit the Application.
	 */
	@Override
	public void onBackPressed() {
		
		System.exit(0);
	}
	
	/**
	 * Return to the MainActivity.
	 * 
	 * @param v
	 */
	public void onClickGoToAbout(View v) {
		
		Intent intent = new Intent(v.getContext(), ActivityAbout.class);
		startActivity(intent);
	}
    
}
