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
import roommateapp.info.entities.Room;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import roommateapp.info.R;

/**
 * The ActivityRoom displays the room
 * information
 * 
 */
public class ActivityRoom extends Activity{

	// Instance variables
	private static final String SELECTEDROOM = "selectedRoom";
	private static final String SELECTEDWEEKDAY = "selectedWeekday";
	private static final String LECTURETIMES = "lecturetimes";
	private static final String WEEKDAYS = "weekdays";
	private static final String SELECTEDTIME = "selectedTime";
	private String selectedWeekday;
	private Room currentRoom;
	private String selectedTime;
	private String[] lectureTimesInfo;
	private String[] weekdays;
	
	/**
	 * On create of the activity
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_room);
		
		// Get necessary data
		this.selectedWeekday = this.getIntent().getStringExtra(SELECTEDWEEKDAY);
		this.currentRoom = getIntent().getParcelableExtra(SELECTEDROOM);
		this.lectureTimesInfo = getIntent().getStringArrayExtra(LECTURETIMES);
		this.weekdays = getIntent().getStringArrayExtra(WEEKDAYS);
		this.selectedTime = getIntent().getStringExtra(SELECTEDTIME);
		
		// For translation of the datafile
		this.weekdays = DateHelper.translateWeekdays(this.weekdays, this);
				
		// Init
		initRoomData();
	}
	
	/**
	 * Set the room data.
	 */
	private void initRoomData() {
		
		// Array of all propertie images
		ImageView[] images = {(ImageView) findViewById(R.id.imgFilter00),
							(ImageView) findViewById(R.id.imgFilter01),
							(ImageView) findViewById(R.id.imgFilter02),
							(ImageView) findViewById(R.id.imgFilter03),
							(ImageView) findViewById(R.id.imgFilter04),
							(ImageView) findViewById(R.id.imgFilter05),
							(ImageView) findViewById(R.id.imgFilter06),
							(ImageView) findViewById(R.id.imgFilter07)};

		// Reset the displayed images
		for(int i = 0; i < images.length; i++) {
			images[i].setImageResource(R.drawable.filterempty);
		}
		
		// Check which propertie can be displayed
		if(currentRoom.hasPropteries() || currentRoom.getRoomInformation() != null) {
			
			int index = 0;
			
			// Whiteboard
			if(currentRoom.getWhiteboardStatus()) {
				images[index].setImageResource(R.drawable.filterwhiteboard);
				index++;
			}
			// Powersupply
			if(currentRoom.getPowerSupplyStatus()) {
				images[index].setImageResource(R.drawable.filterpower);
				index++;
			}
			// Network
			if(currentRoom.getNetworkStatus()) {
				images[index].setImageResource(R.drawable.filternetwork);
				index++;
			}
			// PC
			if(currentRoom.getPCPoolStatus()) {
				images[index].setImageResource(R.drawable.filterpc);
				index++;
			}
			// Projektor
			if(currentRoom.getProjectorStatus()) {
				images[index].setImageResource(R.drawable.filterprojector);
				index++;
			}
			// Beamer
			if(currentRoom.getBeamerStatus()) {
				images[index].setImageResource(R.drawable.filterbeamer);
				index++;
			}
			// Exception
			if(!currentRoom.getExceptionListKeys().isEmpty()) {
				images[index].setImageResource(R.drawable.filterexception);
				index++;
			}
		}
		
		// Check if the room contains a infostring
		if(!this.currentRoom.hasPropteries() 
				&& this.currentRoom.getRoomInformation().length() == 0
				&& this.currentRoom.getExceptionListKeys().isEmpty()) {
			
			ImageView btnInfo = (ImageView) this.findViewById(R.id.img_info);
			btnInfo.setVisibility(ImageView.GONE);
		}
		
		// Hide the propertie bar if the room doesnt have any properties
		if(!this.currentRoom.hasPropteries()) {
			
			hidePropertiesBar();
		}
		
		// Set the title for the roomname
		TextView title = (TextView) this.findViewById(R.id.title_room);
		title.setText(this.currentRoom.getName());
		
		// Set filterlist
		changeLecturesOfday();
	}
	
	/**
	 * On resume.
	 */
	@Override
	protected void onResume() {
		
		super.onResume();
		
		if(this.currentRoom == null) {
			Intent intent = new Intent(this, ActivityMain.class);
			startActivity(intent);
		}
	}
	
	/**
	 * Hide the propertie bar 
	 * on top of the activity layout.
	 */
	private void hidePropertiesBar() {
		
		LinearLayout propertiesBar = (LinearLayout) findViewById(R.id.roomPropertiesLayout);
		ViewGroup.LayoutParams params = propertiesBar.getLayoutParams();
		params.height = 0;
	}
	
	/**
	 * Changes the lectures which are going to be 
	 * displayed in this activity by the selected weekday.
	 */
	private void changeLecturesOfday() {
		
		TextView day = (TextView)findViewById(R.id.txt_dateOfRoom);
		System.out.println(day.toString());
		day.setText(DateHelper.translateWeekday(DateHelper.translateWeekday(selectedWeekday, this), this));
		ArrayAdapterRoomInfoItem adapter = new ArrayAdapterRoomInfoItem(this,
                R.layout.filerow_room_info, currentRoom.getLecturesOfDay(DateHelper.translateWeekday(selectedWeekday, this)), 
                this.lectureTimesInfo, selectedTime);
		ListView listView = (ListView) findViewById(R.id.listRoomInfo);
		listView.setAdapter(adapter);
		listView.setItemsCanFocus(true);
	}

	/**
	 * Return to ActivityBuilding.
	 * 
	 * @param v
	 */
	public void onClickGoToBuilding(View v) {
		
		this.finish();
	}
	
	/**
	 * Displays information about this room.
	 * 
	 * @param v
	 */
	public void showRoomInfo(View v) {
		
		ContextMenuHelper contextMenuHelper = new ContextMenuHelper(this);
		contextMenuHelper.viewRoomInformation(this.currentRoom);
	}
	
	/**
	 * Let you change the selected weekday.
	 * 
	 * @param v
	 */
	public void onClickChangeWeekday(View v) {
				
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setItems(this.weekdays, new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	        	   selectedWeekday = weekdays[id];
	        	   changeLecturesOfday();
	           }
	       }); 
		
		builder.setTitle(getString(R.string.hl_weekdays));
		builder.create();
		builder.show();
	}
}
