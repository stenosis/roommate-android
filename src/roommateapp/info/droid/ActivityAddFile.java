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
import roommateapp.info.net.FileDownloader;
import roommateapp.info.qr.IntentIntegrator;
import roommateapp.info.qr.IntentResult;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import roommateapp.info.R;

/**
 * The "Add-File" Activity let you open a QR-scanner
 * and starts the downloading logic.
 */
public class ActivityAddFile extends Activity {

	// Instance variables
	private File roommateDirectory;
	private String tempURL;
	private boolean toogleProgressbar;
	private int buildingCount;
	private boolean isSDPresent;
	
	/**
	 * On create of the activity
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addfile);
		
		this.isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
		
		// Get data from ActitivyMain
		this.roommateDirectory = new File(getIntent().getStringExtra("sdPath"));
		this.buildingCount = getIntent().getIntExtra("buildingCount", 0);

		// Hide loading-progressbar
		LinearLayout llpb = (LinearLayout) findViewById(R.id.progressBarDownloadLL);
		ViewGroup.LayoutParams params = llpb.getLayoutParams();
		params.height = 0;
		llpb.setLayoutParams(new LinearLayout.LayoutParams(params));
		
		ProgressBar pb = (ProgressBar) findViewById(R.id.progressBarDownload);
		TextView pbt = (TextView) findViewById(R.id.txt_progressBarDownload);
		pb.setVisibility(View.INVISIBLE);
		pbt.setVisibility(View.INVISIBLE);
		
		// Headline returns to the prev. activity.
		TextView title = (TextView) this.findViewById(R.id.headline);
		title.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		
		// Set some text-shadow effects
		setTextShadow();
	}
    
    /**
     * Set text-shadow effects in this activity.
     */
    private void setTextShadow() {
    	
		TextView url = (TextView) this.findViewById(R.id.txt_addfile_url);
		url.setShadowLayer(1, 0, 1, Color.WHITE);

		TextView publicfiles = (TextView) this.findViewById(R.id.txt_publicfiles);
		publicfiles.setShadowLayer(1, 0, 1, Color.WHITE);
    }
	
    /**
     * Toggles the progessbar while downloading.
     */
	public void toogleElementsForDownload() {
		
		ProgressBar pb = (ProgressBar) findViewById(R.id.progressBarDownload);
		TextView pbt = (TextView) findViewById(R.id.txt_progressBarDownload);
		EditText urlBox = (EditText) findViewById(R.id.enterURL);
		
		if (this.toogleProgressbar) {	
			
			// Hide loading-progressbar
			pb.setVisibility(View.INVISIBLE);
			pbt.setVisibility(View.INVISIBLE);
			urlBox.setEnabled(true);
			this.toogleProgressbar = false;
			
			LinearLayout llpb = (LinearLayout) findViewById(R.id.progressBarDownloadLL);
			ViewGroup.LayoutParams params = llpb.getLayoutParams();
			params.height = 0;
			llpb.setLayoutParams(new LinearLayout.LayoutParams(params)); 
			
		} else {
			
			// Unhide loading-progressbar
			pb.setVisibility(View.VISIBLE);
			pbt.setVisibility(View.VISIBLE);
			urlBox.setEnabled(false);
			this.toogleProgressbar = true;
			
			LinearLayout llpb = (LinearLayout) findViewById(R.id.progressBarDownloadLL);
			ViewGroup.LayoutParams params = llpb.getLayoutParams();
			params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
			llpb.setLayoutParams(new LinearLayout.LayoutParams(params)); 
		}
	}
	
	/**
	 * Set the Roommate file directory.
	 * 
	 * @param roommateFileDirectory
	 */
	public void setRoomateFileDirectory(File roommateFileDirectory) {
		
		this.roommateDirectory = roommateFileDirectory;
	}
	
	/**
	 * Toogle the background and text color of the edit text field
	 * for displaying the download process failed.
	 */
	public void toogleTextbackgroundColor() {
		
		EditText et = (EditText) findViewById(R.id.enterURL);
		et.setBackgroundColor(Color.rgb(255, 51, 102));
		et.setTextColor(Color.rgb(255, 255, 255));	
	}

	/**
	 * Return to the main activity
	 * 
	 * @param v
	 */
	public void onClickGoToMain(View v) {
		
		super.onBackPressed();
	}

	/**
	 * Switch to the QR-Code scanner.
	 * 
	 * @param v
	 */
	public void onClickGoToQR(View v) {
		
		IntentIntegrator integrator = new IntentIntegrator(this);
		integrator.initiateScan();
	}
	
	/**
	 * When returning from the QR-Code scanner, check the commited data.
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		
		IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		if (scanResult != null) {

			tempURL = scanResult.getContents();

			if (tempURL != null) {

				if (!tempURL.startsWith(RoommateConfig.ROOMMATE_URL)) {
					DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {

							EditText et = (EditText) findViewById(R.id.enterURL);

							switch (which) {
							case DialogInterface.BUTTON_POSITIVE:
								et.setText(tempURL);
								break;

							case DialogInterface.BUTTON_NEGATIVE:
								et.setText("");

								// No-button was clicked
								break;
							}
						}
					};

					// Calls a warning, unknown website
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setTitle(getString(R.string.warning_hl));
					builder.setMessage(getString(R.string.warning_unknownurl))
							.setPositiveButton(getString(android.R.string.yes),
									dialogClickListener)
							.setNegativeButton(getString(android.R.string.no),
									dialogClickListener).create().show();

				} else {
					
					// The url seems to be our website
					EditText et = (EditText) findViewById(R.id.enterURL);
					et.setText(tempURL);
				}
			}
		}
	}

	/**
	 * Download the URL to the SD-card.
	 * 
	 * @param v
	 */
	public void onClickAddFile(View v) {
		
		EditText urlBox = (EditText) findViewById(R.id.enterURL);
		
		if(urlBox.length() > 0) {
			
			String fileAdress = urlBox.getText().toString();
			
			if (!fileAdress.startsWith("http://")){
				fileAdress="http://"+fileAdress;
			}
			
			urlBox.setText(fileAdress);
			
			toogleElementsForDownload();
			
			String username = Preferences.getEmail(getApplicationContext());
			String pw =  Preferences.getPw(getApplicationContext());
			
			// Start file downloading
			FileDownloader fd = new FileDownloader(this, this.roommateDirectory,username,pw);
			fd.execute(fileAdress);
			
		} else {
			
			Toast toast= Toast.makeText(getApplicationContext(), 
					getString(R.string.wrong_url), Toast.LENGTH_LONG);  
					toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 100);
					toast.show();
			
			urlBox.requestFocus();
		}
	}
	
	/**
	 * Open ActivityPublicFiles.
	 * 
	 * @param v
	 */
	public void onClickViewPublicFiles(View v) {

		if(this.isSDPresent) {
			
			Intent intent = new Intent(this, ActivityPublicFiles.class);
			startActivity(intent);
			
		} else {
			Toast.makeText(getApplicationContext(), getString(R.string.msg_needSD), Toast.LENGTH_LONG).show();
		}
	}
	
	/**
	 * Open the ActivitySettings.
	 * 
	 * @param v
	 */
	public void onClickGoToSettings(View v) {
		
		Intent intent = new Intent(this, ActivitySettings.class);
		intent.putExtra("buildingCount", this.buildingCount);
		startActivity(intent);
	}
}