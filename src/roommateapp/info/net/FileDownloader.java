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

/** imports **/
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.StringTokenizer;
import org.apache.http.util.ByteArrayBuffer;
import org.xml.sax.SAXException;
import roommateapp.info.droid.ActivityAddFile;
import roommateapp.info.droid.ActivityMain;
import roommateapp.info.droid.RoommateConfig;
import roommateapp.info.io.Parser;
import roommateapp.info.io.XercesParser;
import roommateapp.info.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Base64;
import android.widget.EditText;
import android.widget.Toast;

/**
 * FileDownloader can download roommate files or
 * the holiday filelist.
 * 
 */
public class FileDownloader extends AsyncTask<Object, Object, Object> {
	
	// Instance variables
	private Activity ac;
	private File roommateDirectory;
	private File downloadedFile;
	private boolean isHolidayFile;
	private String dls;
	private String dlf;
	private String dls_holiday;
	private String valiFail;
	private String dialog_pleaseWait;
	private String dialog_downloading;
	private ProgressDialog dialog = null;
	private String username = "";
	private String pw = "";
	
	/**
	 * Constructor
	 * 
	 * @param ac
	 * @param roommateDirectory
	 * @param username
	 * @param pw
	 */
	public FileDownloader(Activity ac, File roommateDirectory, String username, String pw) {
		
		this.ac = ac;
		this.roommateDirectory = roommateDirectory;
		this.username = username;
		
		// Localisation
		this.dls = ac.getString(R.string.msg_downloadSuccess);
		this.dlf = ac.getString(R.string.msg_downloadFailed);
		this.valiFail = ac.getString(R.string.msg_novalidfile);
		this.dls_holiday = ac.getString(R.string.msg_holidayfiledownloaded);
		this.dialog_pleaseWait = ac.getString(R.string.pleaseWait);
		this.dialog_downloading = ac.getString(R.string.downloading);
	}
	
	/**
	 * On pre exectue.
	 */
	@Override
	protected void onPreExecute() {
		
		super.onPreExecute();
		dialog = null;
		dialog = new ProgressDialog(((Activity) ac));
		dialog.setTitle(this.dialog_pleaseWait);
		dialog.setMessage(this.dialog_downloading);
		dialog.setCancelable(false);
		dialog.show();
	}
	
	/**
	 * Parallel execution
	 */
	@SuppressLint("UseValueOf")
	@Override
	protected Object doInBackground(Object... params) {

		if(!this.isHolidayFile) {

			/* Progressbar einblenden */
			this.ac.runOnUiThread(new Runnable() {
				public void run() {
					toggleProgressbar();
				}
			});	
		}
		
		boolean success = true;
		String eingabe = (String) params[0];

		if (eingabe != null) {
		
			try {
				URL url = new URL(eingabe);

				// Get filename
				String fileName = eingabe;
				StringTokenizer tokenizer = new StringTokenizer(fileName, "/", false);
				int tokens = tokenizer.countTokens();
				for (int i = 0; i < tokens; i++) {
					fileName = tokenizer.nextToken();
				}

				// Create file
				this.downloadedFile = new File(this.roommateDirectory, fileName);

				// Download and write file
				try {
					// Password and username if it's HTACCESS
					String authData = new String(Base64.encode((username + ":" + pw).getBytes(), Base64.NO_WRAP));
					
					URLConnection urlcon = url.openConnection();
					
					// Authorisation if it's a userfile
					if(eingabe.startsWith(RoommateConfig.URL)){
						urlcon.setRequestProperty("Authorization", "Basic "+ authData);
						urlcon.setDoInput(true);
					}
					InputStream inputstream = urlcon.getInputStream();
					ByteArrayBuffer baf = new ByteArrayBuffer(50);

					int current = 0;

					while ((current = inputstream.read()) != -1) {
						baf.append((byte) current);
					}

					FileOutputStream fos = new FileOutputStream(this.downloadedFile);
					fos.write(baf.toByteArray());
					fos.close();

				} catch (IOException e) {
					success = false;
					e.printStackTrace();
				}
			} catch (MalformedURLException e) {
				success = false;
				e.printStackTrace();
			}
		} else {
			success = false;
		}	
		return new Boolean(success);
	}

	@Override
	/**
	 * After execution
	 */
	protected void onPostExecute(Object result) {
		
		if(!this.isHolidayFile) {
			
			// Hide progressbar
			this.ac.runOnUiThread(new Runnable() {
				public void run() {
					toggleProgressbar();
				}
			});			
		}
		
		this.dialog.cancel();
		
		boolean ergebnis = (Boolean)result;
		
		if (!ergebnis) {
			
			// Download failed
			Toast.makeText(this.ac.getApplicationContext(), dlf, Toast.LENGTH_LONG).show();
		
			if(!this.isHolidayFile) {
				
				toggleProgressbar();
			} else {
				
//				this.dialog.cancel();
//		        CheckBox cboxHoliday = (CheckBox) this.ac.findViewById(R.id.box_filterHolidays);
//		        cboxHoliday.setChecked(false);
//		        Preferences.setFilterHolidays(this.ac, false);
			}
			
		} else {
			
			// Download success
			if(!this.isHolidayFile) {
				
				boolean valide = checkValidate();
				if (valide) {
					Toast.makeText(this.ac.getApplicationContext(), dls, Toast.LENGTH_LONG).show();
					
					// Clear the textfield
					EditText urlBox = (EditText) this.ac.findViewById(R.id.enterURL);
					urlBox.setText("");
					
					// Return to the ActivityMain
					Intent mainIntent = new Intent(this.ac.getApplicationContext(), ActivityMain.class);
					mainIntent.putExtra("openStd", false);
					this.ac.startActivity(mainIntent);
					
				} else {
					
					// Change color of the textfield
					this.ac.runOnUiThread(new Runnable() {
						public void run() {
							toogleURLBGColor();
						}
					});
					
					Toast.makeText(this.ac.getApplicationContext(), valiFail, Toast.LENGTH_LONG).show();
					toggleProgressbar();
					File file = new File(this.downloadedFile.toString());
					file.delete();
				}				
			} else {
				Toast.makeText(this.ac.getApplicationContext(), dls_holiday, Toast.LENGTH_LONG).show();
			}
		}	
	}
	
	/**
	 * Check if the downloaded Roommate file is valid.
	 * 
	 * @param path
	 * @return ob Datei valide ist
	 */
	public boolean checkValidate() {
		
		boolean valide = false;
		
		try {
			Parser checkParser = new XercesParser();
			checkParser.setRoommateFileCheckPath(this.downloadedFile.toString());
			
			try {
				checkParser.parseRoommateFileCheck();
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
	 */
	public void toggleProgressbar() {
		((ActivityAddFile) this.ac).toogleElementsForDownload();
	}
	
	/**
	 * 
	 */
	public void toogleURLBGColor() {
		((ActivityAddFile) this.ac).toogleTextbackgroundColor();
	}
	
	/**
	 * Tell the downloader to download the holidayfile list
	 * from the server instead of an Roommate file.
	 * 
	 * @param status
	 */
	public void isHolidayfile(boolean status) {
		
		this.isHolidayFile = status;
	}
}
