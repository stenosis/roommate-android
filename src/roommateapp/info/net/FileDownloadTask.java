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
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import roommateapp.info.droid.ActivityMain;
import roommateapp.info.droid.ActivityWithResponse;
import roommateapp.info.droid.RoommateConfig;
import roommateapp.info.entities.BuildingFile;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Base64;
import android.widget.Toast;
import roommateapp.info.R;

/**
 * FileDownloadTask is able to download
 * builing files from the Roommate webservice.
 */
public class FileDownloadTask extends AsyncTask<String, String, Void> {
	
	// Instance variables
	private ProgressDialog dialog = null;
	private ActivityWithResponse callingActivity;
	private ArrayList<BuildingFile> filestobeupdated;
	private boolean succes = true;
	private UpdateFacade updateFacade;

	/**
	 * Constructor.
	 * 
	 * @param callingActivity
	 * 
	 */
	public FileDownloadTask(ActivityWithResponse callingActivity,
			ArrayList<BuildingFile> filestobeupdated,UpdateFacade updateFacade) {
		
		this.callingActivity = callingActivity;
		this.filestobeupdated = filestobeupdated;
		this.updateFacade=updateFacade;
	}

	/**
	 * Do in background.
	 * 
	 * @param arg0
	 * 
	 */
	@Override
	protected Void doInBackground(String... arg0) {

		if (this.filestobeupdated.size() > 0) {

			// For task
			String username = arg0[0];
			String pw = arg0[1];
			
			// Convert password + username into HTACCESS data
			String authData = new String(Base64.encode(	(username + ":" + pw).getBytes(), Base64.NO_WRAP));
			for (BuildingFile currentfile : this.filestobeupdated) {
				String filename = currentfile.getFilename();
				if (currentfile.getPublicStatus()) {
					
					HttpURLConnection urlConnection;
					try {
						
						// Custom path without user hash
						URL url2 = new URL(RoommateConfig.URL + ":" + RoommateConfig.PORT+ RoommateConfig.OFFICIAL_PATH_PREFIX + "/" + filename);
						urlConnection = (HttpURLConnection) url2.openConnection();
						urlConnection.setRequestProperty("Authorization","Basic " + authData);
						urlConnection.setDoInput(true);
						InputStream in = new BufferedInputStream(urlConnection.getInputStream());
						byte[] contents = new byte[1024];
						int bytesRead = 0;
						StringBuffer buf = new StringBuffer("");
						while ((bytesRead = in.read(contents)) != -1) {
							buf.append(new String(contents, 0, bytesRead));
						}

						try{
							// Delete file
							ArrayList<BuildingFile> validatedFiles=updateFacade.getValidatetFilesfromSD();
							if (RoommateConfig.VERBOSE) {
								System.out.println("Delete: "+validatedFiles.get(validatedFiles.indexOf(currentfile)).getFile().delete());
							}
						}
						catch(Exception e) {	
							
						}
						
						boolean fileWasSaved=DownLoadedXMLWriter.writeDownloadedFileToDisk(filename, "",buf.toString());
						urlConnection.disconnect();
						
						if (RoommateConfig.VERBOSE) {
							if (fileWasSaved){
						    	System.out.print("Saved a file");			
							}
							else{
								System.out.print("ERROR: failed to saved a file");		
							}
						}
						
					} catch (Exception e) {
						e.printStackTrace();
						succes = false;
					}

				} else {

					HttpURLConnection urlConnection;

					// Create a foldername and add it to the filepath
					String userFolderPath = CryptoHelper
							.transformUsername(username);

					if (RoommateConfig.VERBOSE) {
						System.out.println("Password: " + new String(authData));
						System.out.println("Path : " + RoommateConfig.URL + ":"
								+ RoommateConfig.PORT
								+ RoommateConfig.USER_PATH_PREFIX
								+ userFolderPath + "/" + filename);
					}

					try {
						URL url2 = new URL(RoommateConfig.URL + ":" + RoommateConfig.PORT + RoommateConfig.USER_PATH_PREFIX+ userFolderPath + "/" + filename);
						urlConnection = (HttpURLConnection) url2.openConnection();
						urlConnection.setRequestProperty("Authorization", "Basic " + authData);
						urlConnection.setDoInput(true);
						InputStream in = new BufferedInputStream(urlConnection.getInputStream());
						byte[] contents = new byte[1024];
						int bytesRead = 0;
						StringBuffer buf = new StringBuffer("");
						while ((bytesRead = in.read(contents)) != -1) {
							buf.append(new String(contents, 0, bytesRead));
						}
						try{
							// Delete file
							ArrayList<BuildingFile> validatedFiles = updateFacade.getValidatetFilesfromSD();
							boolean fileWasDeleted = validatedFiles.get(validatedFiles.indexOf(currentfile)).getFile().delete();
							if (RoommateConfig.VERBOSE) {
								if (fileWasDeleted){
									System.out.println("File was automatically deleted ");
								}
								else{
									System.out.println("ERROR: Could not automatically delete a file");
								}
							}
						}
						catch(Exception e){		
						}
					
						boolean fileWasSaved=DownLoadedXMLWriter.writeDownloadedFileToDisk(filename, "",buf.toString());
						urlConnection.disconnect();
						
						if (RoommateConfig.VERBOSE) {
							if (fileWasSaved){
						    	System.out.print("Saved a file");			
							}
							else{
								System.out.print("ERROR: Failed to save a file");		
							}
						}			

					} catch (Exception e) {
						e.printStackTrace();
						System.out.println(e);
						succes = false;
					}
				}
			}
		}
		return null;
	}

	/**
	 * On post execute.
	 * 
	 * @param result
	 * 
	 */
	protected void onPostExecute(Void result) {
		
		if (this.filestobeupdated.size() > 0) {
			
			if (succes) {
				dialog.cancel();
				if (this.callingActivity.getClass().equals(ActivityMain.class)) {
					callingActivity.printToastMessages(((Activity)this.callingActivity).getString(R.string.msg_update),
							Toast.LENGTH_SHORT, false);
					callingActivity.createListView();
				} else {
					callingActivity.showDialog(((Activity)this.callingActivity).getString(R.string.msg_update), 
							((Activity)this.callingActivity).getString(R.string.title_updated), true);
				}

			} else {
				dialog.cancel();
				callingActivity.showDialog(((Activity)this.callingActivity).getString(R.string.msg_updateError), 
						((Activity)this.callingActivity).getString(R.string.title_updatedError), false);
			}
		} else {
			dialog.cancel();
		}
		this.cancel(true);
	}

	/**
	 * Doing necessary steps before executing the task.
	 */
	@Override
	protected void onPreExecute() {
		
		super.onPreExecute();
		dialog = null;
		dialog = new ProgressDialog(((Activity) callingActivity));
		dialog.setTitle(((Activity)this.callingActivity).getString(R.string.pleaseWait));
		dialog.setMessage(((Activity)this.callingActivity).getString(R.string.msg_signupCheck));
		dialog.setCancelable(false);
		dialog.show();
	}
}
