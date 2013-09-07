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
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;
import roommateapp.info.R;

/**
 * The ActiviytPublicFiles class provides access to 
 * download the community based public building files.
 */
public class ActivityPublicFiles extends Activity implements ActivityWithResponse {

	// Instance variables
	private ArrayList<BuildingFile> filesSelectedToBeUpdated = new ArrayList<BuildingFile>();
	private UpdateFacade updateFacade;
	public boolean allFilesChecked=false;
	private ArrayList<BuildingFile> alreadyparsedfiles = new ArrayList<BuildingFile>();
	private static boolean disclaimerAgreed;
	
	/**
	 * On create of the public files activity.
	 */
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_publicfiles);
		
		updateFacade=new UpdateFacade(getBaseContext());
		updateFacade.deleteOldLists();
		
		clearFilesToBeUpdated();

		if (this.allFilesChecked) {
			allFilesChecked = false;
		}

		// Disclaimer
		if(!disclaimerAgreed) {
			showDisclaimer();
		} else {
			proceedWithUpdate();
		}
		
		// Headline returns to the prev. activity.
		TextView title = (TextView) this.findViewById(R.id.txt_sync_headline);
		title.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
	}
	
	/**
	 * On Resume.
	 */
	@Override
	protected void onResume() {
		
		super.onResume();
		
		clearFilesToBeUpdated();
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
		
		allFilesChecked = false;
		return filesSelectedToBeUpdated.remove(file);
	}

	/**
	 * addFileFromfilesToBeUpdated will add files to the update list
	 * and will be called from the async update task.
	 */
	public boolean addFileFromfilesToBeUpdated(BuildingFile file) {
		
		allFilesChecked = false;
		return filesSelectedToBeUpdated.add(file);
	}
	
	/**
	 * Creates the list view.
	 */
	public void createListView() {
		
		alreadyparsedfiles = updateFacade.getAllPublicFiles();

		ArrayAdapterUpdateFileItemForAllPublicFiles adapter = new ArrayAdapterUpdateFileItemForAllPublicFiles(
				this, getApplicationContext(), R.layout.filerow_sync,
				alreadyparsedfiles);
		ListView listView = (ListView) findViewById(R.id.listViewUpdateFiles);
		listView.setAdapter(adapter);
		listView.setItemsCanFocus(true);
		
		if (alreadyparsedfiles.size()==0){
			
			// Debug
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
		
		
		//quick bugfix
		files = updateFacade.getAllPublicFiles();
		
		if (files.size()==0){
			if (RoommateConfig.VERBOSE) {
				System.out.println("Output");
				System.out.println("file count "+files.size());
				System.out.println(files);
			}
			
			showDialog(getString(R.string.msg_nofileupdates), getString(R.string.title_update),true);
		}
		
		ArrayAdapterUpdateFileItemForAllPublicFiles adapter = new ArrayAdapterUpdateFileItemForAllPublicFiles(
				this, getApplicationContext(), R.layout.filerow_sync,
				files);
		ListView listView = (ListView) findViewById(R.id.listViewUpdateFiles);
		listView.setAdapter(adapter);
		listView.setItemsCanFocus(true);
		
		// Refresh if all files are marked
		listView.invalidateViews();
	}
	
	/**
	 * Download selected building files.
	 */
	private void proceedWithUpdate() {
		
		updateFacade.downloadPublicUpdateFileLists(this);
	}
	
	/**
	 * Displays a disclaimer for the community files.
	 */
	@SuppressWarnings("deprecation")
	private void showDisclaimer() {
		
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle(getString(R.string.publicfiles_disclaimer_hl));
		alertDialog.setMessage(getString(R.string.publicfiles_disclaimer));
				
		// OK-Button
		alertDialog.setButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	
            	dialog.cancel();
            	disclaimerAgreed = true;
            	proceedWithUpdate();
            }
        });
		
        // Cancel-Button
        alertDialog.setButton2(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	finish();
            }
        });
        alertDialog.setCancelable(false);
	    alertDialog.show();
	}
	
	/**
	 * Print a toast message.
	 */
	@Override
	public void printToastMessages(String text, int duration,
			boolean returnToMain) {
		// TODO Auto-generated method stub
	}

	/**
	 * Switch back to the AcitivtySettings.
	 * 
	 * @param view
	 */
	public void onClickGoToSettings(View view) {
		
		disclaimerAgreed = false;
		onBackPressed();
	}

	/**
	 * Select and mark all available files.
	 * 
	 * @param view
	 */
	public void onClickSelectAll(View view) {

		this.allFilesChecked = !this.allFilesChecked;
		
		if (this.allFilesChecked) {
			this.filesSelectedToBeUpdated = alreadyparsedfiles;
		} else {
			clearFilesToBeUpdated();
		}
		createListView(alreadyparsedfiles);
	}

	/**
	 * Snyc selected files.
	 * 
	 * @param view
	 */
	public void onClickDownloadNow(View view) {
		
		if (filesSelectedToBeUpdated.size() > 0) {
			
			disclaimerAgreed = false;
			this.updateFacade.UpdateSelectedFiles(this,this.filesSelectedToBeUpdated);
		}
		else{
			showDialog(this.getString(R.string.noFilesSelected), this.getString(R.string.dialog_error), false);
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
		
		disclaimerAgreed = false;
		finish();
		Intent intent = new Intent(this, ActivityMain.class);
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
