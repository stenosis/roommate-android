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
import java.util.ArrayList;
import roommateapp.info.entities.BuildingFile;
import roommateapp.info.net.UpdateFacade;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import roommateapp.info.R;

/**
 * Sync Activity to synchronize files between
 * the Roommate website and the android client.
 */
@SuppressLint("NewApi")
public class ActivitySync extends Activity implements ActivityWithResponse {

	// Instance variables
	private ArrayList<BuildingFile> filesSelectedToBeUpdated = new ArrayList<BuildingFile>();
	private ArrayList<BuildingFile> alreadyparsedfiles = new ArrayList<BuildingFile>();
	private UpdateFacade updateFacade;
	public boolean allFilesChecked;
	
	/**
	 * On create of the activity.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sync);

		updateFacade=new UpdateFacade(getBaseContext());
		updateFacade.deleteOldLists();

		// Debug
		if (RoommateConfig.VERBOSE) {	
			String debug = "files count: " + updateFacade.getValidatetFilesfromSD().size();
		}
		
		// Headline returns to the prev. activity.
		TextView title = (TextView) this.findViewById(R.id.txt_sync_headline);
		title.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		
		clearFilesToBeUpdated();

		if (Preferences.hasValidUser(getApplicationContext())) {
			updateFacade.downloadAllUpdateFileLists(this);
		} else {
			updateFacade.downloadPublicUpdateFileLists(this);
		}

		if (this.allFilesChecked) {
			allFilesChecked = false;
		}
	}
	
	/**
	 * On Resume.
	 */
	@Override
	protected void onResume() {
		
		super.onResume();
				
		if (this.allFilesChecked) {
			allFilesChecked = false;
		}
	}	
	
	/**
	 * clearFilesToBeUpdated will be caled within the onCreate for
	 * clearing the necessary cache. 
	 */
	private void clearFilesToBeUpdated() {
		
		filesSelectedToBeUpdated = new ArrayList<BuildingFile>();
	}

	/**
	 * removeFileFromfilesToBeUpdated removes files from the update list
	 * and will be called from the async update task.
	 */
	public boolean removeFileFromfilesToBeUpdated(BuildingFile file) {
		
		return filesSelectedToBeUpdated.remove(file);
	}

	/**
	 * addFileFromfilesToBeUpdated will add files to the update list
	 * and will be called from the async update task.
	 */
	public boolean addFileFromfilesToBeUpdated(BuildingFile file) {
		
		return filesSelectedToBeUpdated.add(file);
	}

	/**
	 * Creates the list view.
	 */
	public void createListView() {
		
		alreadyparsedfiles = updateFacade.getAllFilesThatCanBeUpdated();

		ArrayAdapterUpdateFileItem adapter = new ArrayAdapterUpdateFileItem(
				this, getApplicationContext(), R.layout.filerow_sync,
				alreadyparsedfiles);
		ListView listView = (ListView) findViewById(R.id.listViewUpdateFiles);
		listView.setAdapter(adapter);
		listView.setItemsCanFocus(true);
		
		
		if (alreadyparsedfiles.size()==0){
			if (RoommateConfig.VERBOSE) {
				System.out.println("Output");
				System.out.println("file count " + alreadyparsedfiles.size());
				System.out.println(alreadyparsedfiles);
			}
			showDialog(getString(R.string.msg_nofileupdates), getString(R.string.title_update),true);
			
		}
		
		// refresh if files are marked
		listView.invalidateViews();
	}

	/**
	 * Creates the list view.
	 */
	public void createListView(ArrayList<BuildingFile> files) {	
		
		ArrayAdapterUpdateFileItem adapter = new ArrayAdapterUpdateFileItem(
				this, getApplicationContext(), R.layout.filerow_sync,
				files);
		ListView listView = (ListView) findViewById(R.id.listViewUpdateFiles);
		listView.setAdapter(adapter);
		listView.setItemsCanFocus(true);
		
		// Refresh if all files are marked
		listView.invalidateViews();
		
		if (files.size()==0){
			if (RoommateConfig.VERBOSE) {
				System.out.println("Output");
				System.out.println("file count "+files.size());
				System.out.println(files);
			}
			
			showDialog(getString(R.string.msg_nofileupdates), getString(R.string.title_update),true);
		}
	}

	/**
	 * Switch back to the AcitivtySettings.
	 * 
	 * @param view
	 */
	public void onClickGoToSettings(View view) {

		onBackPressed();
	}

	/**
	 * Select and mark all available files.
	 * 
	 * @param view
	 */
	public void onClickSelectAll(View view) {
		
		this.allFilesChecked = !allFilesChecked;
		// for not parsing the files again
		createListView(alreadyparsedfiles);
	}

	/**
	 * Snyc selected files.
	 * 
	 * @param view
	 */
	public void onClickSyncNow(View view) {
		
		if (this.allFilesChecked) {
			
			this.updateFacade.UpdateAllFiles(this);
		} else {
			
			this.updateFacade.UpdateSelectedFiles(this,
					this.filesSelectedToBeUpdated);
		}
	}
	
	/**
	 * Prints a toast message.
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
	 * Displays a dialog.
	 */
	public void showDialog(String text, String title, final boolean returnToMain){
		
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int which) {
		        switch (which){
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
	 * Return to the ActivityMain.
	 */
	public void returntoMain(){
		
		finish();
		Intent intent = new Intent(this, ActivityMain.class);
		intent.putExtra("openStd", false);
		startActivity(intent);
	}

	/**
	 * Return to the ActivitySettings.
	 */
	@Override
	public void onBackPressed() {

		Intent intent = new Intent(this.getApplicationContext(), ActivitySettings.class);
		intent.putExtra("update", false);
		intent.putExtra("openStd", false);
		startActivity(intent);
	}

	/**
	 * Dummy for the interface.
	 */
	public void startUpdaterifUserisLoggedIn() {
		
		// TODO Auto-generated method stub
	}

	/**
	 * Dummy for the interface.
	 */
	public void setSaveBox() {
		
		// TODO Auto-generated method stub
	}
}
