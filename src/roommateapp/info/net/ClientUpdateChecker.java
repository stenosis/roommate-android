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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import roommateapp.info.droid.RoommateConfig;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;
import roommateapp.info.R;

/**
 * ClientUpdateChecker checks for available
 * Roommate application updates on the internet.
 */
public class ClientUpdateChecker extends AsyncTask<String, String, Void> {
	
	// Instance variables
	private Activity ac;
	private String updateData[] = new String[2];
	private boolean showNotificationOnUp2Date;
	
	/**
	 * Constructor.
	 * 
	 * @param applicationContext
	 */
	public ClientUpdateChecker(Activity activity, boolean showNotificationOnUp2Date) {
		
		this.ac = activity;
		this.showNotificationOnUp2Date = showNotificationOnUp2Date;
	}
	
	/**
	 * On post execute.
	 */
	@SuppressWarnings("deprecation")
	@Override
	protected void onPostExecute(Void result) {
		
		super.onPostExecute(result);
		if(this.updateData[0] != null) {
			
			// Dialog
			AlertDialog alertDialog = new AlertDialog.Builder(this.ac).create();
			alertDialog.setTitle("Update gefunden");
			alertDialog.setMessage("Roommate " + updateData[0] + " " + this.ac.getString(R.string.update_available));
	       
			// Onlick listener class
			class OnCLickListenerButtonYes 
				implements android.content.DialogInterface.OnClickListener {
				
				
				Activity activity;
				String downloadURL;
				
				// Constructor for handing over data
				public OnCLickListenerButtonYes(Activity activity, String downloadURL) {
					
					this.activity = activity;
					this.downloadURL = downloadURL;
					
				}

				public void onClick(DialogInterface dialog, int which) {
					
					Intent browseIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(this.downloadURL));
					this.activity.startActivity(browseIntent);
				}
			}
			
			// Yes-Button
			alertDialog.setButton(this.ac.getString(android.R.string.yes), new OnCLickListenerButtonYes(this.ac, this.updateData[1]));
			
	        // No-Button
	        alertDialog.setButton2(this.ac.getString(android.R.string.no), new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	            
	            dialog.cancel();
	            }
	        });

		    // Show dialog
		    alertDialog.show();
		
		} else if (this.showNotificationOnUp2Date) {

			Toast.makeText(this.ac, this.ac.getString(R.string.msg_up2date), Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * Do in background.
	 */
	@Override
	protected Void doInBackground(String... params) {
		
		String versionOnline = null;
		String downloadPath = null;
		String faktor = "000000000";
		
		InputStream is = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		
		try {
			
			is = new URL(RoommateConfig.URL_APPVERSION).openStream();
			isr = new InputStreamReader(is);
			br = new BufferedReader(isr);
			
			// Get both lines
			if (br.ready()) {
				
				versionOnline = br.readLine();
				downloadPath = br.readLine();
			}

			// Contains data
			if(versionOnline != null && downloadPath != null) {
				
				String versionOnlineCheck = new String(versionOnline).replace(".", "");
				params[0] = params[0].replace(".","");
				int versionNumberO = Integer.parseInt((versionOnlineCheck + faktor).substring(0,7));
				int versionNumberL = Integer.parseInt((params[0] + faktor).substring(0,7));
				
				// Debug
				if (RoommateConfig.VERBOSE) {
					System.out.println(versionNumberO);
					System.out.println(versionNumberL);
				}
				
				// Is the online tag higher?
				if(versionNumberL < versionNumberO) {

					this.updateData[0] = "" + versionOnline;
					this.updateData[1] = downloadPath;
				}
			}
			
		// Catch
		} catch (Exception e) {
			
			System.out.println(e.getLocalizedMessage());
			
		// Close streams
		} finally {
			
			try {
				is.close();
			} catch (IOException e) {
				
				System.out.println(e.getLocalizedMessage());
			}
			try {
				isr.close();
			} catch (IOException e) {
				
				System.out.println(e.getLocalizedMessage());
			}
			try {
				br.close();
			} catch (IOException e) {
				
				System.out.println(e.getLocalizedMessage());
			}
		}
		return null;
	}	
}