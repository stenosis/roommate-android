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
import roommateapp.info.droid.ActivitySync;
import roommateapp.info.droid.RoommateConfig;
import android.app.Activity;
import android.os.AsyncTask;
import android.util.Base64;
import android.widget.Toast;
import roommateapp.info.R;

/**
 * SingleUserFileUpdater is able to download
 * builing files from the Roommate webservice
 * which belongs to an user account.
 */
public class SingleUserFileUpdater extends AsyncTask<String, String, Void> {
	
	// Instance variables
	private ActivitySync callingActivity;
	private boolean succes = false;

	/**
	 * Constructor.
	 * 
	 * @param callingActivity
	 */
	public SingleUserFileUpdater(ActivitySync callingActivity) {
		
		this.callingActivity = callingActivity;
	}
	
	/**
	 * Do in background
	 */
	@Override
	protected Void doInBackground(String... arg0) {
		
		// For task
		String username = arg0[0];
		String pw = arg0[1];
		String filename = arg0[2];
		
		// Convert password + username into HTACCESS data
		String authData = new String(Base64.encode((username + ":" + pw).getBytes(), Base64.NO_WRAP));
		HttpURLConnection urlConnection;
		
		// Change foldername and add it to the path
		String userFolderPath = CryptoHelper.transformUsername(username);
		
		// Debug
		if (RoommateConfig.VERBOSE) {
			System.out.println("Password: " + new String(authData));
			System.out.println("Path : " + RoommateConfig.URL + ":" + RoommateConfig.PORT + RoommateConfig.USER_PATH_PREFIX+ userFolderPath+ "/"+ filename);
		}
		
		try {
			URL url2 = new URL(RoommateConfig.URL + ":" + RoommateConfig.PORT + RoommateConfig.USER_PATH_PREFIX+ userFolderPath + "/"+ filename);
			urlConnection = (HttpURLConnection) url2.openConnection();
			urlConnection.setRequestProperty("Authorization", "Basic "+ authData);
			urlConnection.setDoInput(true);
			InputStream in = new BufferedInputStream(urlConnection.getInputStream());
			byte[] contents = new byte[1024];
			int bytesRead = 0;
			StringBuffer buf = new StringBuffer("");
			
			while ((bytesRead = in.read(contents)) != -1) {
				buf.append(new String(contents, 0, bytesRead));
			}
			
			boolean fileWasSaved=DownLoadedXMLWriter.writeDownloadedFileToDisk(filename,"", buf.toString());
			urlConnection.disconnect();
			
			// Debug
			if (RoommateConfig.VERBOSE) {
				if (fileWasSaved){
			    	System.out.print("Userfile: "+filename+" was downloaded and saved");			
				}
				else{
					System.out.print("Userfile: "+filename+" could not save");			
				}
			}
			
			succes = true;
		} catch (Exception e) {
			
			e.printStackTrace();
			System.out.println(e);
		} finally {
			
		}
		return null;
	}

	/**
	 * Doing necessary steps before executing the task.
	 * 
	 * @param result
	 */
	@Override
	protected void onPostExecute(Void result) {
		
		if (succes) {
			callingActivity.printToastMessages(((Activity)this.callingActivity).getString(R.string.msg_downloadSuccess),
					Toast.LENGTH_SHORT,false);
		} else {
			callingActivity.printToastMessages(((Activity)this.callingActivity).getString(R.string.msg_downloadFailed)
					,Toast.LENGTH_SHORT,false);
		}
		this.cancel(true);
	}
}
