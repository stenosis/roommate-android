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
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;
import roommateapp.info.R;

/**
 * Time picker fragment.
 */
@SuppressLint("ValidFragment")
public class TimePickerFragment extends DialogFragment
	implements TimePickerDialog.OnTimeSetListener {
	
	// Instance variables
	private int hour = DateHelper.getCurrentHour();
	private int minute = DateHelper.getCurrentMinute();
	private ActivityBuilding<?> buildingActivity;
	private Context c;
	private TimePickerDialog picker;
	private boolean startedOnce;
	
	/**
	 * Constructor.
	 * 
	 * @param building
	 */
	public TimePickerFragment(ActivityBuilding<?> building) {
		
		this.buildingActivity = building;
		this.c = building.getApplicationContext();
	}
	
	/**
	 * 
	 */
	public TimePickerFragment() {
		
	}

	/**
	 * Set hour.
	 * 
	 * @param hour
	 */
	public void setHour(int hour) {
		
		this.hour = hour;
	}

	/**
	 * Set minute.
	 * 
	 * @param minute
	 */
	public void setMinute(int minute) {
		
		this.minute = minute;
	}

	/**
	 * On create of the time picker dialog
	 */
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		// Create a new instance of TimePickerDialog and return it
		picker = new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
		
    	int cMin = DateHelper.getCurrentMinute();
    	int cHour = DateHelper.getCurrentHour();
    	String curTime = DateHelper.getTimeString(cHour, cMin);
    	String selTime = DateHelper.getTimeString(hour, minute);
    	String closingTime = DateHelper.getClosingTime(this.buildingActivity.getBuilding());
    	int compareTimes = closingTime.compareTo(curTime);
    	
    	// Show a reset-button if the current time isnt the selected
    	if(!curTime.equals(selTime) && compareTimes > 1) {
    		
    		picker.setButton(DialogInterface.BUTTON_NEUTRAL, this.c.getText(R.string.filter_reset), new DialogInterface.OnClickListener() {

    			// Set the selected time to the current time
                public void onClick(DialogInterface dialog, int which) {
                	
                	int cMin = DateHelper.getCurrentMinute();
                	int cHour = DateHelper.getCurrentHour();
                	buildingActivity.setTimebar(cHour, cMin);
                	picker.updateTime(cHour, cMin);
                }
            });
    	}
		return picker;
	}

	/**
	 * On time set.
	 */
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		
		this.hour = hourOfDay;
		this.minute = minute;
		
		// Update displayed time
		if(!this.startedOnce) {
			
			this.startedOnce = true;
			
			if(this.buildingActivity != null) {
				
				this.buildingActivity.setTimebar(hour, minute);
			} else {
				
				this.picker.getWindow().closeAllPanels();
				this.picker.dismiss();
			}
		}
	}
	
	@Override
	public void dismiss() {
		
		this.startedOnce = false;
		this.picker.getWindow().closeAllPanels();
		super.dismiss();
	}

	@Override
	public void onDestroy() {
		
		this.startedOnce = false;
		this.picker.getWindow().closeAllPanels();
		super.onDestroy();
	}

	@Override
	public void onPause() {
		
		this.startedOnce = false;
		this.picker.getWindow().closeAllPanels();
		super.onPause();
	}
}