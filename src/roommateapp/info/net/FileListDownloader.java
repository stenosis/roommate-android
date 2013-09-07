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
import roommateapp.info.droid.ActivityWithResponse;
import roommateapp.info.droid.RoommateConfig;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Base64;
import roommateapp.info.R;

/**
 * FileListDownloader is able to download
 * the filelists from the Roommate webservice.
 */
public class FileListDownloader extends AsyncTask<String, String, Void> {
	
	// Instance variables
	private ProgressDialog dialog = null;
	private ActivityWithResponse callingActivity;
	private boolean succes = false;
	private boolean downloadUserFiles;
	
	/**
	 * Constructor.
	 * 
	 * @param callingActivity
	 */
	public FileListDownloader(ActivityWithResponse callingActivity,boolean downloadUserFiles) {
		
		this.callingActivity = callingActivity;
		this.downloadUserFiles = downloadUserFiles;
	}

	/**
	 * Do in background.
	 * 
	 * @param arg0  Array von Argumten die gesetzt werden können.
	 */
	@Override
	protected Void doInBackground(String... arg0) {
		
		// For task
		String username = arg0[0];
		String pw = arg0[1];
		
		// Convert password + username into HTACCESS data
		String authData = new String(Base64.encode(
				(username + ":" + pw).getBytes(), Base64.NO_WRAP));
		HttpURLConnection urlConnection;
		String userFolderPath = CryptoHelper.transformUsername(username);
		
		if (RoommateConfig.VERBOSE) {
			
			System.out.println("User: " + username);
			System.out.println("Password: " + new String(authData));
			System.out.println("Dir: " + userFolderPath);
			System.out.println("Password encoded: " + new String(authData));
			System.out.println("Path : " + RoommateConfig.URL + ":"
					+ RoommateConfig.PORT + RoommateConfig.OFFICIAL_PATH_PREFIX
					+ RoommateConfig.FILELIST_FILENAME);
		}
		
		// Only try with a valid user account
		try {
			
			URL url2 = new URL(RoommateConfig.URL + ":" + RoommateConfig.PORT+ RoommateConfig.OFFICIAL_PATH_PREFIX+ RoommateConfig.FILELIST_FILENAME);
			urlConnection = (HttpURLConnection) url2.openConnection();
			urlConnection.setRequestProperty("Authorization", "Basic "	+ authData);
			urlConnection.setDoInput(true);
			InputStream in = new BufferedInputStream(urlConnection.getInputStream());
			byte[] contents = new byte[50];
			int bytesRead = 0;
			String strFileContents = "";
			
			while ((bytesRead = in.read(contents)) != -1) {
				strFileContents = strFileContents
						+ new String(contents, 0, bytesRead);
			}
			
			boolean fileWasSaved=DownLoadedXMLWriter.writeDownloadedFileToDisk(RoommateConfig.PUBLIC_FILELIST_FILENAME_ON_SD,RoommateConfig.MISC_FOLDER, strFileContents);
			urlConnection.disconnect();
			
			if (RoommateConfig.VERBOSE) {
				if (fileWasSaved){
			    	System.out.print("Saved the public filelist.");			
				}
				else{
					System.out.print("ERROR: failed to save the public fiellist.");		
				}
			}
			
			if (RoommateConfig.VERBOSE) {
				System.out.println(strFileContents.substring(1, 20));
			}
			
		} catch (Exception e) {
			
			e.printStackTrace();
			succes = false;
		}
		
		if (downloadUserFiles) {
			try {
				URL url3 = new URL(RoommateConfig.URL + ":"
						+ RoommateConfig.PORT + RoommateConfig.USER_PATH_PREFIX
						+ userFolderPath + RoommateConfig.FILELIST_FILENAME);
				urlConnection = (HttpURLConnection) url3.openConnection();
				urlConnection.setRequestProperty("Authorization", "Basic "
						+ authData);
				urlConnection.setDoInput(true);
				InputStream in2 = new BufferedInputStream(
						urlConnection.getInputStream());
				byte[] contents2 = new byte[1024];
				int bytesRead2 = 0;
				String strFileContents2 = "";

				while ((bytesRead2 = in2.read(contents2)) != -1) {
					strFileContents2 = strFileContents2
							+ new String(contents2, 0, bytesRead2);
				}

				boolean fileWasSaved = DownLoadedXMLWriter
						.writeDownloadedFileToDisk(
								RoommateConfig.USER_FILELIST_FILENAME_ON_SD,
								RoommateConfig.MISC_FOLDER, strFileContents2);
				urlConnection.disconnect();

				if (RoommateConfig.VERBOSE) {
					if (fileWasSaved) {
						System.out.print("Saved the user filelist");
					} else {
						System.out
								.print("ERROR: failed to save the user filelist");
					}
				}

				if (RoommateConfig.VERBOSE) {
					System.out.println(strFileContents2.substring(1, 20));
				}

				succes = true;
			} catch (Exception e) {
				e.printStackTrace();
				succes = false;
			}
		}

		return null;
	}
	
	/**
	 * On post execute.
	 * 
	 * @param result
	 */
	@Override
	protected void onPostExecute(Void result) {
		if (succes) {
			this.callingActivity.createListView();
			this.dialog.cancel();
		} else {
			this.callingActivity.createListView();
			this.dialog.cancel();
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
