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
package roommateapp.info.droid;

/* imports */
import java.io.File;
import java.util.ArrayList;
import roommateapp.info.entities.BuildingFile;
import roommateapp.info.net.ClientUpdateChecker;
import roommateapp.info.net.FileDownloader;
import roommateapp.info.net.LoginChecker;
import roommateapp.info.net.LoginCheckerActivitySwitch;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import roommateapp.info.R;

/**
 * ActivitySettings manage the available
 * preferences of the application.
 */
public class ActivitySettings extends Activity implements ActivityWithResponse {

	// Instance variables
	private CheckBox box;
	private int buildingCount;
	
	/**
	 * On create of the activity
	 */
	@Override
    public void onCreate(Bundle savedInstanceState) {
		
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        
        // Hand over data
        this.buildingCount = getIntent().getIntExtra("buildingCount", 0);
        
        setTextShadow();
        setSettingsByConfig();
        checkForClientUpdates();
    }
	
	/**
	 * Checks for available Roommate App updates.
	 */
	private void checkForClientUpdates() {
		
        if(!Preferences.isClientUpdateUserInteraction(getApplicationContext())
        		|| Preferences.isClientUpdateEnabled(getApplicationContext())) {
        	
        	if(this.buildingCount == 0) {
        		
            	Preferences.setClientUpdateEnabled(getApplicationContext(), true);
            	
    			ClientUpdateChecker updateChecker = new ClientUpdateChecker(this, false);
    			updateChecker.execute(getString(R.string.aboutVersion));
        	}
        } 
	}
	
	/**
	 * On Resume.
	 */
	@Override
	protected void onResume() {
		
		super.onResume();
		setSettingsByConfig();
	}

	/**
	 * Displays a dialog.
	 */
	public void showDialog(String text, String title, final boolean returnToMain) {
		
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			
		    public void onClick(DialogInterface dialog, int which) {
		    	
		        switch (which) {
		        case DialogInterface.BUTTON_POSITIVE:
		            if (returnToMain) {
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
	 * Shows a dialog which will switch to the
	 * ActivitySync if it will be accepted.
	 */
	public void showDialogForLogin(String text,String title, final boolean returnToMain) {
		
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int which) {
		        switch (which) {
		        case DialogInterface.BUTTON_POSITIVE:
		            if (returnToMain) {
		            	returntoMain();
		            }
		            startUpdaterifUserisLoggedIn();
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
	 * 
	 */
	public void createListView() {
		// dummy for the interface
	}
	
	/**
	 * 
	 */
	public void returntoMain() {
		// dummy for the interface
	}

	/**
	 * Setting up the default Settings.
	 */
    private void setSettingsByConfig() {

		LinearLayout myLayout = (LinearLayout) findViewById(R.id.layout);
		myLayout.requestFocus();

		// Placing the user name and checks if the saving option is turned on
		if (Preferences.isRememberOn(getApplicationContext())) {
			
			String eMail = Preferences.getEmail(getApplicationContext());
			if (!eMail.equals("-1")) {
				
				((EditText) findViewById(R.id.etxt_eMail)).setText(eMail);
			}
			
			String pw = Preferences.getPw(getApplicationContext());
			if (!pw.equals("-1")) {
				
				((EditText) findViewById(R.id.etxt_pw)).setText(pw);
			}
		}
		
		box = ((CheckBox) findViewById(R.id.box_remember));
		box.setEnabled(true);
		((CheckBox) findViewById(R.id.box_remember)).setChecked(Preferences
				.isRememberOn(getApplicationContext()));
		((EditText) findViewById(R.id.etxt_eMail)).setOnFocusChangeListener(new OnFocusChangeListener() {
			
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus) {
					if(!((EditText) findViewById(R.id.etxt_eMail)).getText().toString().equals(Preferences.getEmail(getApplicationContext()))) {
						
						box.setChecked(false);
						Preferences.setEmail(getApplicationContext(), "-1");
			        	Preferences.setPw(getApplicationContext(), "-1");
					}
				}
			}
		});
		
    	((EditText) findViewById(R.id.etxt_pw)).setOnFocusChangeListener(new OnFocusChangeListener() {
			
			public void onFocusChange(View v, boolean hasFocus) {
				if(!hasFocus) {
					
					if(!((EditText) findViewById(R.id.etxt_pw)).getText().toString().equals(Preferences.getPw(getApplicationContext()))) {
						
						box.setChecked(false);
						Preferences.setEmail(getApplicationContext(), "-1");
			        	Preferences.setPw(getApplicationContext(), "-1");
					}
				}
			}
		});
    	
    	/** Setting saved preferences for **/
    	
    	// Auto sync
        boolean checked = Preferences.isAutosyncEnabled(getApplicationContext());
        ((CheckBox) findViewById(R.id.box_autosync)).setChecked(checked);

        
        // Holidayfilter
        File holidayFile = new File(Preferences.getRoommateDir(getApplicationContext()) 
        		+ "/" + RoommateConfig.MISC_FOLDER + "/" + RoommateConfig.HOLIDAYFILE);
        checked = Preferences.isFilterHolidaysOn(getApplicationContext());

        if(checked && holidayFile.exists()) {
 
        	((CheckBox) findViewById(R.id.box_filterHolidays)).setChecked(checked);

        } else {
        	
        	checked = false;
        	((CheckBox) findViewById(R.id.box_filterHolidays)).setChecked(checked);
        	Preferences.setFilterHolidays(getApplicationContext(), checked);
        	
        }
        
        // Searching for updates
    	((CheckBox) findViewById(R.id.box_checkClientUpdate)).setChecked(
    			Preferences.isClientUpdateEnabled(getApplicationContext()));
    }

    
    /**
     * onClickLogin tries to establish and valid 
     * the the user login data.
     * 
     * @param view
     */
	public void onClickLogin(View view) {
		
		String tempEmail = ((EditText) findViewById(R.id.etxt_eMail)).getText().toString();
		String tempPw = ((EditText) findViewById(R.id.etxt_pw)).getText().toString();
		
		// If a Email adress was entered
		if(!tempEmail.equals(getString(R.string.eingabeEmail)) && tempEmail.length() > 0) {
		
			// Check login data with async task
			new LoginChecker(this).execute(tempEmail, tempPw);
			
			if (((CheckBox) findViewById(R.id.box_remember)).isChecked()) {
				Preferences.setEmail(getApplicationContext(), tempEmail);
				Preferences.setPw(getApplicationContext(), tempPw);
			}
			
		} else {
			
			// Login failed
			Toast.makeText(getApplicationContext(), getString(R.string.no_valid_email), Toast.LENGTH_LONG).show();
			EditText mail = ((EditText) findViewById(R.id.etxt_eMail));
			mail.requestFocus();
		}    
	}
	
	/**
	 * 
	 */
	public void enableLoginElements() {
		
		//((CheckBox) findViewById(R.id.box_autosync)).setEnabled(true);
		//((Button) findViewById(R.id.btn_manuelExchange)).setEnabled(true);
		Preferences.setValidUser(getApplicationContext(), true);
	}

	/**
	 * 
	 */
	public void disableLoginElements() {
		
		//((CheckBox) findViewById(R.id.box_autosync)).setChecked(false);
		//((CheckBox) findViewById(R.id.box_autosync)).setEnabled(false);
		//((Button) findViewById(R.id.btn_manuelExchange)).setEnabled(false);
		Preferences.setValidUser(getApplicationContext(), false);
	}

	/**
	 * 
	 * 
	 * 
	 * @param view
	 */
    public void onClickManuelExchange(View view) {

			String tempEmail =Preferences.getEmail(getApplicationContext());		
			String tempPw = Preferences.getPw(getApplicationContext());	
			new LoginCheckerActivitySwitch(this).execute(tempEmail, tempPw);
    }
    
    /**
     * 
     */
	public void startUpdaterifUserisLoggedIn(){
		
		Intent intent = new Intent(this, ActivitySync.class);
		startActivity(intent);
	}
    
	/**
	 * Shows a dialog warning if the user
	 * hastn set any login data.
	 */
	public void showMessageIfUserisNotLoggedInAndHasNoData(){
		
		AlertDialog.Builder msg = new AlertDialog.Builder(this);
		msg.setTitle(getString(R.string.exchange_nofiles_hl));
		msg.setMessage(getString(R.string.exchange_nofiles_msg));
		msg.create();
		msg.show();
	}
    
    /**
     * 
     * @param view
     */
    public void onClickAutosync(View view) {
    	
    	Preferences.setEmail(getApplicationContext(), ((EditText) findViewById(R.id.etxt_eMail)).getText().toString());
    	Preferences.setPw(getApplicationContext(), ((EditText) findViewById(R.id.etxt_pw)).getText().toString());
    	
    	if(!Preferences.hasValidUser(getApplicationContext())) {
    		
        	showHint();
    	} else {
    		
    		Preferences.setAutosync(getApplicationContext(), ((CheckBox) findViewById(R.id.box_autosync)).isChecked());
    	}
    	
    }
    
    /**
     * 
     */
    private void showHint() {
    	
    	// Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.dialog_fire_missiles);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   ((EditText) findViewById(R.id.etxt_eMail)).requestFocus();
                   }
               });
        
        // Create the AlertDialog object and return it
        builder.create();
        builder.show();
    }
    
    /**
     * Saving or reset necessary data
     * for a user login.
     * @param view
     */
    public void onClickRemember(View view) {
    	
    	if(((CheckBox) findViewById(R.id.box_remember)).isChecked()) {
    		
    		Preferences.setEmail(getApplicationContext(), ((EditText) findViewById(R.id.etxt_eMail)).getText().toString());
        	Preferences.setPw(getApplicationContext(), ((EditText) findViewById(R.id.etxt_pw)).getText().toString());
        	Preferences.setRemember(getApplicationContext(), true);
        	
    	} else {
    		
    		// Reset the data
    		Preferences.setEmail(getApplicationContext(), "-1");
        	Preferences.setPw(getApplicationContext(), "-1");
        	((EditText) findViewById(R.id.etxt_eMail)).setText(R.string.eingabeEmail);
        	((EditText) findViewById(R.id.etxt_pw)).setText(R.string.passwort);
        	Preferences.setRemember(getApplicationContext(), false);
    	}
    }
    
    /**
     * Checks for the holidayfile list and
     * downloads the file if it dosnt already exists.
     * 
     * @param view
     */
    @SuppressWarnings("deprecation")
	public void onClickFilterHolidays(View view) {
    	
    	Preferences.setFilterHolidays(getApplicationContext(), ((CheckBox) findViewById(R.id.box_filterHolidays)).isChecked());
    	
    	if(Preferences.isFilterHolidaysOn(this)) {
    		
    		File holidayFile = new File(Preferences.getRoommateDir(this).toString() 
    				+ "/" + RoommateConfig.MISC_FOLDER + "/", RoommateConfig.HOLIDAYFILE);
    		
    		// If the file doenst exits, we could download it
    		if(!holidayFile.exists()) {
    			
    			// Setting up a dialog
    			AlertDialog downloadDialog = new AlertDialog.Builder(this).create();
    			downloadDialog.setTitle(getString(R.string.dl_holidayfile_hl));
    			downloadDialog.setMessage(getString(R.string.dl_holidayfile_question));
    			
    			// onClick-listener for the Yes-Button
    			class OnCLickListenerDownloadButtonYes 
    				implements android.content.DialogInterface.OnClickListener {
    				
    				public void onClick(DialogInterface dialog, int which) {

    					downloadHolidayFile();
    				}
    			}
    			
    			// Yes-Button
    			downloadDialog.setButton(getString(android.R.string.yes),new OnCLickListenerDownloadButtonYes());
    			
    			// No-Button
    	        downloadDialog.setButton2(getString(android.R.string.no), new DialogInterface.OnClickListener() {
    	        	
    	            public void onClick(DialogInterface dialog, int which) {
    	            dialog.cancel();
    	            
    	            downloadHolidayFileCancel();

    	            }
    	        });
    	        downloadDialog.setCancelable(false);
    			downloadDialog.show();   
    		}
    	}
    }
    
	/**
	 * Checking for Roommate Client Updates.
	 * 
	 * @param v
	 */
	public void onClickCheckClientUpdate(View v) {
		
//		addNotification();
		
		// Setting up the preferences
		Preferences.setClientUpdateEnabled(getApplicationContext(), ((CheckBox) findViewById(R.id.box_checkClientUpdate)).isChecked());
		Preferences.setClientUpdateUserInteraction(getApplicationContext(), true);
		
		// Check immediately for an Roommate client update when the button was checked
		if(Preferences.isClientUpdateEnabled(getApplicationContext())) {
			
			ClientUpdateChecker updateChecker = new ClientUpdateChecker(this, true);
			updateChecker.execute(getString(R.string.aboutVersion));
		}
	}
	    
    /**
     * Downloads the holidayfile.
     */
    protected void downloadHolidayFile() {
    	
		FileDownloader fd = new FileDownloader(
				this, new File(Preferences.getRoommateDir(this) + "/" + RoommateConfig.MISC_FOLDER + "/"),"","");
		fd.isHolidayfile(true);
		fd.execute(RoommateConfig.HOLIDAYFILE_URL);
    }
    
    /**
     * Cancels the holidayfile download.
     */
    protected void downloadHolidayFileCancel() {
    	
        CheckBox cboxHoliday = (CheckBox) findViewById(R.id.box_filterHolidays);
        cboxHoliday.setChecked(false);
        Preferences.setFilterHolidays(this, false);
    }
    
    /**
     * Setting up some text shadow effects.
     */
    private void setTextShadow() {
    	
		TextView settings = (TextView) this.findViewById(R.id.txt_settings_hl_settings);
		settings.setShadowLayer(1, 0, 1, Color.WHITE);
		
		TextView account = (TextView) this.findViewById(R.id.txt_settings_hl_account);
		account.setShadowLayer(1, 0, 1, Color.WHITE);
		
		TextView sync = (TextView) this.findViewById(R.id.txt_settings_hl_sync);
		sync.setShadowLayer(1, 0, 1, Color.WHITE);
    }

	/**
	 * Displaying a Toast message.
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
	 * 
	 */
	public void setSaveBox() {	
		
		((CheckBox) findViewById(R.id.box_remember)).setEnabled(true);
	}

	/**
	 * 
	 */
	public void createListView(ArrayList<BuildingFile> files) {
		
	}
	
    /**
     * Return to the ActivityMain.
	 * 
	 * @param view
	 */
	public void onClickGoToMain(View view) {
		
		onBackPressed();
	}
	
	/**
	 * Return to the ActivityMain.
	 */
	@Override
	public void onBackPressed() {

		Intent intent = new Intent(this.getApplicationContext(), ActivityMain.class);
		intent.putExtra("update", false);
		intent.putExtra("openStd", false);
		
		if(this.buildingCount > 0) {
			intent.putExtra("checkedOnceForClientUpdate", true);
		}
		
		startActivity(intent);
	}
}
