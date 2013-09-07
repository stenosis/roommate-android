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
package roommateapp.info.droid;

/* imports */
import java.io.File;
import java.util.ArrayList;
import roommateapp.info.entities.BuildingFile;
import roommateapp.info.io.DTDWriter;
import roommateapp.info.io.DirFileChecker;
import roommateapp.info.net.ClientUpdateChecker;
import roommateapp.info.net.UpdateFacade;
import roommateapp.info.R;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;

/**
 * The ActivityMain is the main and 
 * initialization entry for the Roommate App.
 */
public class ActivityMain extends Activity implements ActivityWithResponse {

	// Instance variables
	private boolean checkedOnceForClientUpdate;
	private File roommateDirectory;
	private File dtdDirectory;
	private File miscDirectory;
	private ArrayList<BuildingFile> validatedBuildingfiles = new ArrayList<BuildingFile>();
	private ContextMenuHelper contextMenuHelper;
	private BuildingFile selectedBuilding;
	private UpdateFacade updateFacade;
	private DirFileChecker dirFileChecker;

	/**
	 * Main entry for the first program start.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		// Set Layout
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	
		// Get and set data
		this.checkedOnceForClientUpdate = getIntent().getBooleanExtra("checkedOnceForClientUpdate", false);
		setUpSDCardFolder();

		DTDWriter dtdWriter = new DTDWriter(this.roommateDirectory);
		dtdWriter.createRoommateFileDTD();
//		dtdWriter.createHolidayFileDTD();

		// Get and set data
		checkForClientUpdates();
		checkForFiles();
		updateFacade = new UpdateFacade(getBaseContext());
		this.contextMenuHelper = new ContextMenuHelper(this);

		// Update check only if the app started for the first instance
		if (savedInstanceState == null && Preferences.isAutosyncEnabled((getApplicationContext()))) {

			if (RoommateConfig.VERBOSE) {
				System.out.println("Checking for sync, App was restarted");
			}	
			updateFacade.deleteOldLists();
			
			// If there's a User account, update all files or simple just the public files
			if (Preferences.hasValidUser(getApplicationContext())){
				updateFacade.downloadAllUpdateFileLists(this);
			}
			else{
				updateFacade.downloadPublicUpdateFileLists(this);
			}
		}
		
		// Open the default file
		if (savedInstanceState == null && getIntent().getBooleanExtra("openStd", true)) {
			openDefaultFile();
		}
		
		// Open ActivityAbout by clicking on the headline
		TextView title = (TextView) this.findViewById(R.id.headline);
		title.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onClickGoToAbout(v);
			}
		});		
		
		// Create the listview 
		this.createListView(this.validatedBuildingfiles);
	}
	
	/**
	 * Checks for available Roommate App updates.
	 */
	private void checkForClientUpdates() {
		
        if(!Preferences.isClientUpdateUserInteraction(getApplicationContext())
        		|| Preferences.isClientUpdateEnabled(getApplicationContext())) {
        	
        	if(!this.checkedOnceForClientUpdate) {
        		
            	Preferences.setClientUpdateEnabled(getApplicationContext(), true);
            	
    			ClientUpdateChecker updateChecker = new ClientUpdateChecker(this, false);
    			updateChecker.execute(getString(R.string.aboutVersion));
        	}
        } 
	}
	
	/**
	 * 
	 */
	public void ContinueUpdateAfterFileListDownload() {
		
		ArrayList<BuildingFile> updateableFiles = updateFacade.getAllFilesThatCanBeUpdatedforMAin();
		if (updateableFiles.size() > 0) {
			
			updateFacade.UpdateAllFilesMain(this, updateableFiles);
		} 
		
		checkForFiles();
		createListView(this.validatedBuildingfiles);
	}

	/**
	 * 
	 */
	public void showDialog(String text, String title, final boolean returnToMain){
		
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int which) {
		    	
		        switch (which) {
		        case DialogInterface.BUTTON_POSITIVE:
		            if (returnToMain){
		            	returntoMain();
		            }
		            break;

		        case DialogInterface.BUTTON_NEGATIVE:
		            //No button clicked
		            break;
		        }
		    }
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(title);
		builder.setMessage(text).setPositiveButton(getString(android.R.string.ok), dialogClickListener).create().show();
	}

	/**
	 * Refreshing the file list after updates or changes.
	 */
	public void createListView(){
		
		ContinueUpdateAfterFileListDownload();
	}
	
	/**
	 * onResume
	 */
	@Override
	protected void onResume() {
		
		super.onResume();
		
		// Searching for new files and refresh the list view
		checkForFiles();
		createListView(this.validatedBuildingfiles);
	}
	
	/**
	 * onBackPressed moves the app to the background.
	 */
	@Override
	public void onBackPressed() {
		
		moveTaskToBack(true);
	}

	/**
	 * Check for Roommate files within the 
	 * Roommate directory on the SD-Card.
	 */
	private void checkForFiles() {
		
		this.dirFileChecker = new DirFileChecker(this.roommateDirectory);
		this.validatedBuildingfiles = this.dirFileChecker.getValidatedFiles();
		
		if (this.validatedBuildingfiles == null || this.validatedBuildingfiles.size() == 0) {
			
			// Swtich to ActivityIntroduction when no files are found
			Intent intent = new Intent(this, ActivityIntroduction.class);
			intent.putExtra("roommateURL", RoommateConfig.ROOMMATE_URL);
			intent.putExtra("sdPath", this.roommateDirectory.toString());
			startActivity(intent);
		}
	}	

	/**
	 * Set up all necessarily directories on the SC-Card.
	 */
	public void setUpSDCardFolder() {
		
		// Roommate-directory
		this.roommateDirectory = new File(Environment.getExternalStorageDirectory() 
				+ RoommateConfig.ROOMMATE_SDCARD_DIR);
		this.roommateDirectory.mkdirs();
		
		// DTD-directory
		this.dtdDirectory = new File(Environment.getExternalStorageDirectory() 
				+ RoommateConfig.ROOMMATE_SDCARD_DIR + RoommateConfig.MISC_FOLDER);
		this.dtdDirectory.mkdirs();
		
		// Misc-directory
		this.miscDirectory = new File(Environment.getExternalStorageDirectory() 
				+ RoommateConfig.ROOMMATE_SDCARD_DIR + RoommateConfig.MISC_FOLDER);
		this.miscDirectory.mkdirs();
		
		Preferences.setRoommateDIR(getApplicationContext(), this.roommateDirectory.toString());
	}

	/**
	 * Create a listView with all valid Roommate files
	 * within the Roommate directory.
	 * 
	 * @param listViewItems
	 */
	public void createListView(ArrayList<BuildingFile> buildingFiles) {
		
		if (buildingFiles.size() > 0) {
			
			ArrayAdapterFileItem adapter = new ArrayAdapterFileItem(
					this, getApplicationContext(), R.layout.filerow_main, 
					Preferences.getDefaultFile(this.getApplicationContext()), 
					this.validatedBuildingfiles);
			
			ListView listView = (ListView) findViewById(R.id.lv_buildingfiles);
			listView.setAdapter(adapter);
			listView.setItemsCanFocus(true);
		}
	}
	
	/**
	 * 
	 */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	    
    	menu.add(R.string.menu_addfile);
	    menu.add(R.string.title_settings);
	    menu.add(R.string.title_about);
	    return true;
    }
    
    /**
     * 
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	
    	String selItem = (String) item.getTitle();
    	View v = getWindow().getDecorView().findViewById(android.R.id.content);
    	
    	if(selItem.equals(getString(R.string.menu_addfile))) {
    		
    		onClickGoToAddFile(v);
    		
    	} else if (selItem.equals(getString(R.string.title_settings))) {
    		
    		onClickGoToSettings(v);
    		
    	} else if (selItem.equals(getString(R.string.title_about))) {
    		
    		onClickGoToAbout(v);
    	}
    	return true;
    }
    
	/**
	 * Register the context menu.
	 * 
	 * @param v
	 * @param selectedItem
	 */
	public void registerContextButton(View v, BuildingFile selectedBuilding) {
		
		registerForContextMenu(v);
		this.selectedBuilding = selectedBuilding;
	}

	/**
	 * Create a context menu.
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {

		// Pfad zur Standard-Datei
		String defaultFile = Preferences.getDefaultFile(getApplicationContext());
		
		// Set headline
		menu.setHeaderTitle(this.selectedBuilding.getBuildingname());

		// Set default
		if (defaultFile.equals(this.selectedBuilding.getFile().toString())) {
			
			menu.add(getString(R.string.cmenu_remstd));
		} else {
			
			menu.add(getString(R.string.cmenu_setstd));
		}
		
		// Navigation
		if (!this.selectedBuilding.getLat().equals("") && !this.selectedBuilding.getLng().equals("")) {
			
			menu.add(getString(R.string.cmenu_nav));
		}
		
		// Share options
		menu.add(getString(R.string.cmenu_share));
		
		// Building information
		menu.add(getString(R.string.cmenu_info));

		// Delete file
		menu.add(getString(R.string.cmenu_del));
	}

	/**
	 * Actions of the selected context menu entry.
	 */
	@SuppressWarnings("deprecation")
	@Override
	public boolean onContextItemSelected(MenuItem item) {

		// Set file as default
		if(item.getTitle().equals(getString(R.string.cmenu_setstd))) {
			
			this.contextMenuHelper.setStandard(this.selectedBuilding);
			onResume();
			
		// Remove default entry
		} else if (item.getTitle().equals(getString(R.string.cmenu_remstd))) {
			
			this.contextMenuHelper.removeStandard(getApplicationContext());
			onResume();
			
		// Display building information
		} else if (item.getTitle().equals(getString(R.string.cmenu_info))) {
			
			this.contextMenuHelper.viewBuildingInformation(this.selectedBuilding);
			
		// Navigate
		} else if (item.getTitle().equals(getString(R.string.cmenu_nav))) {
			
			startActivity(this.contextMenuHelper.startNavigation(this.selectedBuilding));
		
		// Share options
		} else if (item.getTitle().equals(getString(R.string.cmenu_share))) {

			startActivity(Intent.createChooser(
					this.contextMenuHelper.shareFile(this.selectedBuilding),
					getString(R.string.social_headline)));
	
		// Delete file
		} else if (item.getTitle().equals(getString(R.string.cmenu_del))) {

			// Safty conformation
			AlertDialog alertDialog = new AlertDialog.Builder(this).create();
			alertDialog.setTitle(getString(R.string.cmenu_del));
			alertDialog.setMessage(getString(R.string.cmenu_delMsg1) + " "
					+ this.selectedBuilding.getBuildingname() + " " + getString(R.string.cmenu_delMsg2));
	       
			// Inner-Class for an OnClick listener.
			class OnCLickListenerDeleteButtonYes 
				implements android.content.DialogInterface.OnClickListener {

				private BuildingFile fileToDel;
				
				// Constructor
				public OnCLickListenerDeleteButtonYes(BuildingFile fileToDel) {
					this.fileToDel = fileToDel;
				}

				public void onClick(DialogInterface dialog, int which) {
					
					// Delete the selected file
					String name = this.fileToDel.getBuildingname();
					
					contextMenuHelper.deleteFile(this.fileToDel);
					onResume();
					
					printToastMessages(name + " " + getString(R.string.cmenu_delsuccess), Toast.LENGTH_LONG,false);
				}
			}
			
			// Yes-Button
			alertDialog.setButton(getString(android.R.string.yes), 
					new OnCLickListenerDeleteButtonYes(this.selectedBuilding));
			
	        // No-Button
	        alertDialog.setButton2(getString(android.R.string.no), 
	        		new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	            
	            dialog.cancel();
	            }
	        });

		    // Show dialog
		    alertDialog.show();
		}
		return super.onContextItemSelected(item);
	}
	
	/**
	 * Print a toast message.
	 * 
	 * @param text
	 * @param duration
	 */
	public void printToastMessages(String text, int duration, final boolean returnToMain) {
		
		Toast.makeText(getApplicationContext(), text, duration).show();
		if (returnToMain){
			this.returntoMain();
		}
	}
	
	/**
	 * Opens the default building file.
	 * 
	 * @return
	 */
	public boolean openDefaultFile() {
		
		boolean exists = false;
		String path = Preferences.getDefaultFile(getApplicationContext());
		
		// Check if the default file is set.
		if (path != null) {
			
			// Check if the file exists and if it's valid
			for (int i = 0; i < this.validatedBuildingfiles.size() && !exists ;i++) {
				
				BuildingFile building = this.validatedBuildingfiles.get(i);	
				
				// Check if the path is the same
				if (building.getFile().toString().equals(path)) {
					
					exists = true;
					openRoommateFile(building, false);
				}
			}
		}
		return exists;
	}
	
	/**
	 * Switch to ActivityAbout for the App information.
	 * 
	 * @param view
	 */
	public void onClickGoToAbout(View view) {
		
		Intent intent = new Intent(this, ActivityAbout.class);
		startActivity(intent);
	}

	/**
	 * Switch to ActivitySettings for the global App settings.
	 * 
	 * @param view
	 */
	public void onClickGoToSettings(View view) {
		
		Intent intent = new Intent(this, ActivitySettings.class);
		intent.putExtra("buildingCount", this.validatedBuildingfiles.size());
		startActivity(intent);
	}

	/**
	 * Switch to ActivityAddfile for downloading a Roommate file.
	 * 
	 * @param view
	 */
	public void onClickGoToAddFile(View view) {
		
		Intent intent = new Intent(this, ActivityAddFile.class);
		intent.putExtra("sdPath", this.roommateDirectory.toString());
		intent.putExtra("roommateURL", 	RoommateConfig.ROOMMATE_URL);
		intent.putExtra("buildingCount", this.validatedBuildingfiles.size());
		startActivity(intent);
	}
	
	/**
	 * Switch to ActicityBuilding and open the selected building file.
	 */
	public void openRoommateFile(BuildingFile building, boolean checkedForUpdate) {
		
		this.selectedBuilding = building;
		Intent intent = new Intent(this, ActivityBuilding.class);
				
		// Hand over some data
		intent.putExtra("selectedBuilding", this.selectedBuilding);
		intent.putExtra("checkedOnceForClientUpdate", checkedForUpdate);
		startActivity(intent);
	}

	public void returntoMain() {
		// TODO Auto-generated method stub
	}
	
	public void startUpdaterifUserisLoggedIn() {
		// TODO Auto-generated method stub
	}

	public void setSaveBox() {
		// TODO Auto-generated method stub
	}
}