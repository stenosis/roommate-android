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
import roommateapp.info.droid.ActivitySettings;
import roommateapp.info.droid.RoommateConfig;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Base64;
import roommateapp.info.R;

/**
 * LoginChecker provides methods to check the
 * validation of user account data for the connection
 * to the Roommate webservice. If the login is not possible
 * this class will provide a switch of the activity.
 */
public class LoginCheckerActivitySwitch extends AsyncTask<String, String, Void> {
	
	// Instance variables
	private ActivitySettings callingActivity;
	private boolean succes = false;
	private ProgressDialog dialog = null;
	
	/**
	 * Constructor.
	 * 
	 * @param callingActivity Activity welche diesen Task erzeugt hat.
	 */
	public LoginCheckerActivitySwitch(ActivitySettings callingActivity) {
		
		this.callingActivity = callingActivity;
	}

	/**
	 * Do in background: check if the login works.
	 * 
	 * @param arg0
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
		
		// Debug
		if (RoommateConfig.VERBOSE) {
			
			System.out.println("User: " + username);
			System.out.println("Password: " + new String(authData));
			System.out.println("Dir: " + userFolderPath);
			System.out.println("Password encoded: " + new String(authData));
			System.out.println("Path : " + RoommateConfig.URL + ":" + RoommateConfig.PORT + RoommateConfig.USER_PATH_PREFIX	+ userFolderPath + RoommateConfig.FILELIST_FILENAME);
		}
		
		try {
			
			URL url2 = new URL(RoommateConfig.URL + ":" + RoommateConfig.PORT + RoommateConfig.USER_PATH_PREFIX+ userFolderPath + RoommateConfig.FILELIST_FILENAME);
			urlConnection = (HttpURLConnection) url2.openConnection();
			urlConnection.setRequestProperty("Authorization", "Basic "+ authData);
			urlConnection.setDoInput(true);
			InputStream in = new BufferedInputStream(urlConnection.getInputStream());
			byte[] contents = new byte[RoommateConfig.BUFFER_SIZE];
			int bytesRead = 0;
			String strFileContents = "";
			
			while ((bytesRead = in.read(contents)) != -1) {
				strFileContents = new String(contents, 0, bytesRead);
			}

			urlConnection.disconnect();
			succes = true;
			
			// Validation
			if (!strFileContents.substring(39, 54).equals(
					RoommateConfig.XML_HEADER)) {
				
				if (!strFileContents.substring(39, 55).equals(RoommateConfig.XML_HEADER2)) {
					if (RoommateConfig.VERBOSE) {
						System.out.println(strFileContents.substring(39, 55));
						System.out.println(RoommateConfig.XML_HEADER2);
					}
					succes = false;
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			succes=false;
		} finally {

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
			dialog.cancel();
			
			// Activate the saving option in the settings
			callingActivity.enableLoginElements();
			((ActivitySettings)callingActivity).startUpdaterifUserisLoggedIn();
			 
		} else {
			dialog.cancel();
			
			// Activity starts the updater automatically
			callingActivity.disableLoginElements();
			callingActivity.showDialogForLogin(this.callingActivity.getString(R.string.msg_justpublicfiles), this.callingActivity.getString(R.string.title_update), false);
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
		dialog.setTitle(this.callingActivity.getString(R.string.pleaseWait));
		dialog.setMessage(this.callingActivity.getString(R.string.msg_signupCheck));
		dialog.setCancelable(false);
		dialog.show();
	}
}
