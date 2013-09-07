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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.xml.sax.SAXException;
import roommateapp.info.entities.BuildingFile;
import roommateapp.info.entities.Room;
import roommateapp.info.io.Parser;
import roommateapp.info.io.XercesParser;
import roommateapp.info.net.ClientUpdateChecker;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import roommateapp.info.R;

/**
 * The ActivityBuilding creates the
 * entire view of the building.
 * 
 * @param <E>
 */
public class ActivityBuilding<E> extends FragmentActivity {

	// Instance variables
	private BuildingFile selectedBuilding;
	private Parser parser;
	private String[] weekdays;
	private String[] hours;
	private ContextMenuHelper contextMenuHelper;
	private TimePickerFragment timePickerFragment;
	private String[] filterlist = new String[7];
	private boolean[] filterActive = new boolean[7];
	private int lectureIndex = 1;
	private boolean checkedOnceForClientUpdate;
	private List<View> pages;
	private ArrayList<Room> allRooms;
	private TextView dialogView;
	private MenuDialog customMenuDialog;
//	private HolidayList holidayList;
//	private File holidayListFile;
	
	/**
	 * Inner class for overwriting  the context menu.
	 */
    private class MenuDialog extends AlertDialog {
    	
    	private ActivityBuilding<E> building;
    	
		@SuppressWarnings("unchecked")
		public MenuDialog(Context context) {
            super(context);
            this.building = (ActivityBuilding<E>) context;
            View cus_menu = getLayoutInflater().inflate(R.layout.custom_contextmenu, null);
            setView(cus_menu, 0,0,0,0);
        }

        @Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			
			ListView lv = (ListView) findViewById(R.id.lv_customItemView);
			boolean isFilterActive = FilterHelper.hasActiveFilter(filterActive);
			boolean isDefault = selectedBuilding.getFile().toString().equals(Preferences.getDefaultFile(getApplicationContext()));
			ArrayAdapterCustomContextMenu<?> adapter = 
					new ArrayAdapterCustomContextMenu<E>(getApplicationContext(), 0, isFilterActive, isDefault, this, building);
			lv.setAdapter(adapter);
		}

		@Override
        public boolean onKeyUp(int keyCode, KeyEvent event) {
              if (keyCode == KeyEvent.KEYCODE_MENU) {
                    dismiss();
                    return true;
              }
              return super.onKeyUp(keyCode, event);
        }
    }
	
	/**
	 * On create of the activity
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_building);
		
		initActivityBuilding((savedInstanceState == null));
		this.contextMenuHelper = new ContextMenuHelper(this);
		
		// Get localisation for the filter
		this.filterlist[0] = getString(R.string.filter_info);
		this.filterlist[1] = getString(R.string.filter_whiteboard);
		this.filterlist[2] = getString(R.string.filter_power);
		this.filterlist[3] = getString(R.string.filter_network);
		this.filterlist[4] = getString(R.string.filter_pc);
		this.filterlist[5] = getString(R.string.filter_projector);
		this.filterlist[6] = getString(R.string.filter_beamer);
	}
	
	/**
	 * first init of the data
	 */
	private void initActivityBuilding(boolean firstInit) {
		
		// Init time picker
		this.timePickerFragment = new TimePickerFragment(this);
		
		// Get submitted data for this activity
		this.selectedBuilding = getIntent().getParcelableExtra("selectedBuilding");
		this.checkedOnceForClientUpdate = getIntent().getBooleanExtra("checkedOnceForClientUpdate", true);
		
		// If this is the first instance go for parsing the file data
		if (firstInit == true || (this.selectedBuilding.getLectureTimes() == null)) {
			
			if (RoommateConfig.VERBOSE) {
				System.out.println("Debug: first init");
			}
			
			try {
				this.parser = new XercesParser();
				this.parser.setRoommateFilePath(this.selectedBuilding.getFile().toString());
				this.parser.setBuildingFile(this.selectedBuilding);
				this.parser.parseRoommateFile();
				
			} catch (SAXException e) {
				e.printStackTrace();	
				if (RoommateConfig.VERBOSE) {
					System.out.println("PARSER FAIL, SAXException");
				}
				Intent intent = new Intent(this, ActivityMain.class);
				intent.putExtra("openStd", false);
				startActivity(intent);
				
			} catch (IOException e) {
				e.printStackTrace();
				if (RoommateConfig.VERBOSE) {
					System.out.println("PARSER FAIL, IOException");
				}
				Intent intent = new Intent(this, ActivityMain.class);
				intent.putExtra("openStd", false);
				startActivity(intent);
			}			
		}
		
		// Does the file contains rooms?
		if (this.selectedBuilding.getRoomListKeySet().isEmpty()) {
			
			Toast.makeText(getApplicationContext(), getString(R.string.error_norooms), Toast.LENGTH_LONG).show();
			if (RoommateConfig.VERBOSE) {
				System.out.println("WARNING: No rooms where found in this building.");
			}
			finish();
			
		} else {
			
			// Get the holidaylist
//			if (Preferences.isFilterHolidaysOn(getApplicationContext())) {
//				
//				parseHolidayfile();
//			}
			
			// Set the headline
			TextView headlineActivity = (TextView)findViewById(R.id.TextViewBuildingHeadline);
			headlineActivity.setText(this.selectedBuilding.getBuildingname());
			headlineActivity.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					onBackPressed();
				}
			});
			
			// Set the swipe pages
			// pages.get(0 for free_rooms; 1 for allrooms).findViewById(id) 
			LayoutInflater inflater = LayoutInflater.from(this);
		    pages = new ArrayList<View>();

		    // Page free_rooms
		    View page = inflater.inflate(R.layout.view_free_rooms, null);
		    pages.add(page);
		    
		    // Page all_rooms
		    page = inflater.inflate(R.layout.view_all_rooms, null);
		    pages.add(page);
			
		    // Set pages
			BuildingPageAdapter pagerAdapter = new BuildingPageAdapter(pages, this);
		    ViewPager viewPager = new ViewPager(this);
		    viewPager.setAdapter(pagerAdapter);
		    viewPager.setCurrentItem(0);
		    ViewPager mViewPager= (ViewPager) findViewById(R.id.viewpager);
		    mViewPager.setAdapter(pagerAdapter);
			
		    // Displaying filter status
			if (Preferences.isFilterActive(getApplicationContext())) {
				
				filterActive = Preferences.getFilters(getApplicationContext());
			}
			
		    createListViewAll();
		    
		    // Set the current day and time
		    setTimebar(DateHelper.getCurrentHour(), DateHelper.getCurrentMinute(), DateHelper.translateWeekday(DateHelper.getCurrentDay(this), this));
			this.hours = getHours();
			this.weekdays = this.selectedBuilding.getWeekdaysStrings();
			this.weekdays = DateHelper.translateWeekdays(this.weekdays, this);
						
			// Check for client updates if the file was open via "open by default" operation 
			if(Preferences.getDefaultFile(getApplicationContext()) != null
					&& Preferences.getDefaultFile(getApplicationContext()).equals(this.selectedBuilding.getFile().toString())
					&& Preferences.isClientUpdateEnabled(getApplicationContext())
					&& !this.checkedOnceForClientUpdate) {
				
				ClientUpdateChecker updateChecker = new ClientUpdateChecker(this, false);
				updateChecker.execute(getString(R.string.aboutVersion));
			}
		}
	}
	
	/**
	 * createListViewAll creates a custom list view for
	 * all the entire room list view.
	 * 
	 * @param listViewItems
	 */
	private void createListViewAll() {
		
		
		// Filter wanted rooms which are going to be displayed
	  	this.allRooms = FilterHelper.getRoomsStateFilter(selectedBuilding, filterActive, Preferences.getSize(getApplicationContext()));
	    
	    // Tab: all_rooms
	    ArrayAdapterAllRoomsItem adapter = 
	    		new ArrayAdapterAllRoomsItem(this, R.layout.filerow_all_rooms, allRooms, this);
		ListView listView = (ListView) pages.get(1).findViewById(R.id.listAllRooms);
		listView.setAdapter(adapter);
		listView.setItemsCanFocus(true);
		
		// Display filter statusbar.
		if(Preferences.isFilterActive(getApplicationContext())) {
 		   showFilterStatusbar(getString(R.string.filter_active) + " (" + allRooms.size() 
 				   + "/" + selectedBuilding.getRoomListKeySet().size() + ")");
 	    } else {
 	    	
 		   hideFilterStatusbar();
 	    }
	}
	
	/**
	 * createListViewFreeRooms creates a custom list view
	 * for the current available rooms.
	 */
	private void createListViewFreeRooms() {
		
		String day = (String) ((TextView) pages.get(0).findViewById(R.id.txt_date)).getText();
		day = DateHelper.translateWeekday(day, this);
		
		ArrayAdapterFreeRoomsItem adapter = new ArrayAdapterFreeRoomsItem(this,
                R.layout.filerow_free_rooms, FilterHelper.getRoomsTimeFilter(allRooms, this.lectureIndex, day, this.selectedBuilding, this), this);
		ListView listView = (ListView) pages.get(0).findViewById(R.id.listfreerooms);
		listView.setAdapter(adapter);
		listView.setItemsCanFocus(true);
	}

	/**
	 * Hide the filter statusbar.
	 */
	public void hideFilterStatusbar() {
		
		LinearLayout filterBar = (LinearLayout)findViewById(R.id.filterStatus);
		ViewGroup.LayoutParams params = filterBar.getLayoutParams();
		params.height = 0;
		filterBar.setLayoutParams(new LinearLayout.LayoutParams(params));
	}
	
	/**
	 * Display the filter statusbar.
	 * 
	 * @param message to be displayed
	 */
	public void showFilterStatusbar(String message) {
		
		// Set message
		TextView filterText = (TextView)findViewById(R.id.filterMsgText);
		filterText.setText(message);
		
		// Make statusbar visible 
		LinearLayout filterBar = (LinearLayout)findViewById(R.id.filterStatus);
		ViewGroup.LayoutParams params = filterBar.getLayoutParams();
		
		params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
		filterBar.setLayoutParams(new LinearLayout.LayoutParams(params));
	}
	
	/**
	 * onClickDayPicker lets you pick a available day
	 * from the building.
	 * 
	 * @param view
	 */
	public void onClickDayPicker(View view) {
		
		// Set the selected day
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setItems(this.weekdays, new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	        	   setTimebar(weekdays[id]);
	           }
	       }); 
		
		builder.setTitle(getString(R.string.hl_weekdays));
		builder.create();
		builder.show();
	}
	
	/**
	 * onClickTimePicker lets you pick the time of day.
	 * 
	 * @param view
	 */
	public void onClickTimePicker(View view) {	
		
		String time = ((TextView) pages.get(0).findViewById(R.id.txt_time)).getText().toString();
		int hour = DateHelper.getHour(time);
		int minute = DateHelper.getMinute(time);
		this.timePickerFragment.setHour(hour);
		this.timePickerFragment.setMinute(minute);
		this.timePickerFragment.show(getSupportFragmentManager(), "timePicker");
	}
	
	/**
	 * onClickLecturePicker lets you pick a available
	 * lecture number of the day.
	 * 
	 * @param view
	 */
	public void onClickLecturePicker(View view) {
		
		// Set the selected lecture number of the current day.
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setItems(this.hours, new DialogInterface.OnClickListener() {
	           public void onClick(DialogInterface dialog, int id) {
	        	   setTimebar(id + 1);
	           }
	       }); 
		
		builder.setTitle(getString(R.string.hl_lecture));
		builder.create();
		builder.show();
	}
	
	/**
	 * 
	 */
	private void openCustomContextMenu() {

      customMenuDialog = new MenuDialog(this);
      customMenuDialog.setTitle(getString(R.string.cmenu_hl));
      customMenuDialog.show();
	}
	
	/**
	 * Opens up the context menu by pressing the hardware menu key.
	 */
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		
	    if (keyCode == KeyEvent.KEYCODE_MENU) {
	    	
//	    	registerContextButton(getWindow().getDecorView().findViewById(android.R.id.content));
	    	openCustomContextMenu();
	    }
	    return super.onKeyUp(keyCode, event);
	}	
	
	/**
	 * Register the context menu.
	 * 
	 * @param v
	 */
	public void registerContextButton(View v) {
				
//		registerForContextMenu(getWindow().getDecorView().findViewById(android.R.id.content));
//		openContextMenu(getWindow().getDecorView().findViewById(android.R.id.content));
		openCustomContextMenu();
	}
	
	/**
	 * Create the context menu.
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {

		// Set Headline
		menu.setHeaderTitle(getString(R.string.cmenu_hl));
		
		// Remove default setting, if file was set as default
		String defaultFile = Preferences.getDefaultFile(getApplicationContext());
		if(this.selectedBuilding.getFile().toString().equals(defaultFile)) {
			menu.add(getString(R.string.cmenu_remstd));
		}
		
		// Navigation
		if (!this.selectedBuilding.getLat().equals("") && !this.selectedBuilding.getLng().equals("")) {
			menu.add(getString(R.string.cmenu_nav));
		}
		
		// Display building information
		if (!this.selectedBuilding.getInfo().equals("")) {
			menu.add(getString(R.string.cmenu_info));
		}
		
		// Display legend
		menu.add(getString(R.string.cmenu_legende));
		
		// Display share option
		menu.add(getString(R.string.cmenu_share));
		
		// Display filter settings
		menu.add(getString(R.string.cmenu_filter));
	}
	
	/**
	 * Actions of the selected context menu entry.
	 */
	public boolean onContextItemSelected(MenuItem item) {
		
		// Remove default entry
		if (item.getTitle().equals(getString(R.string.cmenu_remstd))) {
			
			Preferences.setDefaultFile(getApplicationContext(), "");
		
		// Start navigation
		} else if (item.getTitle().equals(getString(R.string.cmenu_nav))) {
			
			showNavigation();
			
		// Display share options
		} else if (item.getTitle().equals(getString(R.string.cmenu_share))) {
			
			showShareOptions();
		
		// Show Building information
		} else if (item.getTitle().equals(getString(R.string.cmenu_info))) {
			
			showBuildingInformation();
		
		// Set filter
		} else if (item.getTitle().equals(getString(R.string.cmenu_filter))) {
			
			showFilterOptions();
			
		// Display the legende	
		} else if (item.getTitle().equals(getString(R.string.cmenu_legende))) {
			
			showSymbolLegend();
		}
		
		return super.onContextItemSelected(item);
	}
	
	/**
	 * Clears the filter settings.
	 */
	public void resetFilter() {
		
		filterActive = new boolean[] {false, false, false, false, false, false, false};
		Preferences.setFilters(getApplicationContext(), filterActive);
		Preferences.setSize(getApplicationContext(), 0);
    	createListViewAll();
    	createListViewFreeRooms();
	}
	
	/**
	 * Show symbol legend.
	 */
	public void showSymbolLegend() {
	
		AlertDialog.Builder legende = new AlertDialog.Builder(this);
		legende.setTitle(getString(R.string.legend_hl));
		
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
	    View layout = inflater.inflate(R.layout.dialog_legende, null);

	    AlertDialog alert = legende.create();
	    alert.setView(layout, 0,0,0,0);
	    alert.show();
	}
	
	/**
	 * Open building informations.
	 */
	public void showBuildingInformation() {
		
		this.contextMenuHelper.viewBuildingInformation(this.selectedBuilding);
	}
	
	/**
	 * Share building file.
	 */
	public void showShareOptions() {
		
		startActivity(Intent.createChooser(
				this.contextMenuHelper.shareFile(this.selectedBuilding), getString(R.string.social_headline)));
	}
	
	/**
	 * Start navigation.
	 */
	public void showNavigation() {
		
		try {
			startActivity(this.contextMenuHelper.startNavigation(this.selectedBuilding));
			
		} catch (ActivityNotFoundException e) {
			Toast.makeText(getApplicationContext(), getString(R.string.nav_noappfound), Toast.LENGTH_LONG).show();
		}
	}
	
	/**
	 * Show the room properties filter options.
	 */
	public void showFilterOptions() {
		
		AlertDialog.Builder filter = new AlertDialog.Builder(this);
		filter.setTitle(getString(R.string.filtertitle));
		
	    // Get the layout inflater
	    LayoutInflater inflater = this.getLayoutInflater();

	    // Inflate and set the layout for the dialog
	    // Pass null as the parent view because its going in the dialog layout
	    View dialogview = inflater.inflate(R.layout.dialog_filter, null);
	    filter.setView(dialogview);
	    
	    // Seekbar
	    dialogView = (TextView) dialogview.findViewById(R.id.txt_filterroomsize);
	    SeekBar seekbar = (SeekBar) dialogview.findViewById(R.id.seekBar_filter);
	    int size = Preferences.getSize(getApplicationContext());
		size = (size == -1)? 0: size;
		
		seekbar.setProgress(size);
		dialogView.setText(getString(R.string.filter_msgP1) 
				+ " " + size + " " + getString(R.string.filter_msgP2));
	            
	    seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				int stepSize = 5;
				progress = ((int)Math.round(progress/stepSize))*stepSize;
			    seekBar.setProgress(progress);
                dialogView.setText(getString(R.string.filter_msgP1) 
                		+ " " + progress + " " + getString(R.string.filter_msgP2));
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
				// Nothing here (:
			}

			public void onStopTrackingTouch(SeekBar seekBar) {
				
				Preferences.setSize(getApplicationContext(), seekBar.getProgress());
			}
	    });
	    
	    // Checkbox list
		ListView choiceList;
		 
        choiceList = (ListView)dialogview.findViewById(R.id.listFilter);
        
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
							          android.R.layout.simple_list_item_multiple_choice, 
							          this.filterlist);
      
        choiceList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        choiceList.setAdapter(adapter);
        boolean[] filterChecked = this.filterActive;
        
        // Set the saved filter preferences
		for(int i = 0; i <filterChecked.length; i++) {
			choiceList.setItemChecked(i, filterChecked[i]);
		}
 
		choiceList.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				filterActive[arg2] = !filterActive[arg2];
			}
		});
		
		// Filter
		filter.setPositiveButton(getString(R.string.filter_set), new DialogInterface.OnClickListener() {
               
               public void onClick(DialogInterface dialog, int id) {
            	   Preferences.setFilters(getApplicationContext(), filterActive);
            	   createListViewAll();
            	   createListViewFreeRooms();
               }
           });
		
		boolean isFilterActive = false;
		for(int i = 0; i < filterChecked.length && !isFilterActive; i++) {
			
			isFilterActive = filterChecked[i];
		}
		
		// Clear the filter settings
		if(isFilterActive || (Preferences.getSize(getApplicationContext()) > 0)) {
			
			filter.setNegativeButton(getString(R.string.filter_reset), new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					
					resetFilter();
				}
			});
		}
		
		filter.create();
		filter.setCancelable(false);
		filter.show();
	}
	
	/**
	 * Sets the current time from the selected lecture number.
	 * 
	 * @param lectureId
	 */
	public void setTimebar(int lectureId) {
		
		String time = selectedBuilding.getLectureStart(lectureId);
		int hour = DateHelper.getHour(time);
		int minute = DateHelper.getMinute(time);
		String day = ((TextView) pages.get(0).findViewById(R.id.txt_date)).getText().toString();		
		setTimebar(hour, minute, day);
	}
	
	/**
	 * Sets the current selected day.
	 * 
	 * @param day
	 */
	public void setTimebar(String day) {
		
		String time = ((TextView) pages.get(0).findViewById(R.id.txt_time)).getText().toString();
		int hour = DateHelper.getHour(time);
		int minute = DateHelper.getMinute(time);
		setTimebar(hour, minute, day);
	}
	
	/**
	 * Sets the current selected time of day.
	 * 
	 * @param hour
	 * @param minute
	 */
	public void setTimebar(int hour, int minute) {
		
		setTimebar(hour, minute, ((TextView) pages.get(0).findViewById(R.id.txt_date)).getText().toString());
	}
	
	/**
	 * Sets the current selected time of day and 
	 * also the weekday.
	 * 
	 * @param hours
	 * @param minutes
	 */
	public void setTimebar(int hour, int minute, String day) {
		
		TextView timeField = (TextView) pages.get(0).findViewById(R.id.txt_time);
		TextView lectureField = (TextView) pages.get(0).findViewById(R.id.txt_lesson);
		TextView dateField = (TextView) pages.get(0).findViewById(R.id.txt_date);
		String weekdayTxt = day;
		day = DateHelper.translateWeekday(day, this);
		String lectureFieldTxt = "";
		String timeTxt = DateHelper.getTimeString(hour, minute);
		
		if (DateHelper.isDayAvailable(day, selectedBuilding.getWeekdaysArrayList())) {
			
			// If the return is -1 means that we're not longer in the current day.
			int closestlecture = DateHelper.getClosestLecture(hour, minute, selectedBuilding);
			if (closestlecture == -1) {
				
				weekdayTxt = DateHelper.getNextAvailableDay(day, selectedBuilding.getWeekdaysArrayList(), this);
				weekdayTxt = DateHelper.translateWeekday(weekdayTxt, this);
				// Set the first lecture number as the selected time.
				timeTxt = selectedBuilding.getLectureStart(1);
				lectureFieldTxt = "1";
				Toast.makeText(getApplicationContext(), getString(R.string.msg_toolate), Toast.LENGTH_SHORT).show();
				
			} else {
				
				lectureFieldTxt = closestlecture + "";
			}
		} else {
		
			weekdayTxt = DateHelper.getNextAvailableDay(day, selectedBuilding.getWeekdaysArrayList(), this);
			weekdayTxt = DateHelper.translateWeekday(weekdayTxt, this);
			timeTxt = selectedBuilding.getLectureStart(1);
			lectureFieldTxt = "1";
			Toast.makeText(getApplicationContext(), getString(R.string.msg_wrongday), Toast.LENGTH_SHORT).show();
			
		}
		timeField.setText(timeTxt);
		lectureField.setText(lectureFieldTxt + ". " + getString(R.string.hour));
		lectureIndex = Integer.parseInt(lectureFieldTxt) - 1;
		dateField.setText(weekdayTxt);
		createListViewFreeRooms();
		
//		if (Preferences.isFilterHolidaysOn(getApplicationContext())) {
//			if(this.holidayList.getHolidays(this.selectedBuilding.getState()).containsKey(DateHelper.getDateOfDay(day, this))){
//				String holidayText = getString(R.string.msg_at) + " " + DateHelper.getDateOfDayandYear(day, this) 
//						+ " " + getString(R.string.msg_is) + " " 
//			            + this.holidayList.getHolidays(this.selectedBuilding.getState()).get(DateHelper.getDateOfDay(day, this)) 
//			            + ".";
//				Toast.makeText(getApplicationContext(), holidayText, Toast.LENGTH_LONG).show();
//			}
//		}
		
	}
	
	/**
	 * Switch to ActivityRoom for the selected room.
	 * 
	 * @param room 
	 */
	public void openRoom(String room) {
		
		Room selectedRoom = this.selectedBuilding.getRoom(room);
		Intent intent = new Intent(this, ActivityRoom.class);
		
		// Hand over some data before starting the activity
		intent.putExtra("roomName", room);
		
		String day = ((TextView) pages.get(0).findViewById(R.id.txt_date)).getText().toString();
		intent.putExtra("selectedWeekday", day);
		intent.putExtra("selectedRoom", selectedRoom);
		intent.putExtra("lecturetimes", getLectureTimesForRoomInfo());
		intent.putExtra("weekdays", selectedBuilding.getWeekdaysStrings());
		intent.putExtra("selectedTime", ((TextView) pages.get(0).findViewById(R.id.txt_time)).getText().toString());
		
		startActivity(intent);
	}
	
	/**
	 * Creates the hours-data which are going to 
	 * be displayed at the lecture picker.
	 * 
	 * @return
	 */
	private String[] getHours() {
		
		String[] hours = new String[this.selectedBuilding.getLectureCount()];
		String hour = "";
		for(int i = 1; i <= hours.length; i++) {
			hour = i + ". " + getString(R.string.hour) + " " + selectedBuilding.getLectureStart(i) + " - " + selectedBuilding.getLectureEnd(i);

			hours[i-1] = hour;
		}		
		
		return hours;		
	}
	
	/**
	 * Creates the lecture-data which are going to
	 * be displayed at the lecture picker.
	 * 
	 * @return
	 */
	private String[] getLectureTimesForRoomInfo() {
		
		String[] hours = new String[this.selectedBuilding.getLectureCount()];
		String hour = "";
		for(int i = 1; i <= hours.length; i++) {
			hour = selectedBuilding.getLectureStart(i) + "\n" + selectedBuilding.getLectureEnd(i);

			hours[i-1] = hour;
		}		
		
		return hours;		
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
	 * 
	 */
	@Override
	public void onBackPressed() {
		
		// Hand over some data before starting the activity
		Intent intent = new Intent(this.getApplicationContext(), ActivityMain.class);
		intent.putExtra("update", false);
		intent.putExtra("openStd", false);
		intent.putExtra("checkedOnceForClientUpdate", true);
		
		startActivity(intent);
	}
	
	/**
	 * Return the selected building of this activity.
	 * 
	 * @return
	 */
	public BuildingFile getBuilding() {
		
		return this.selectedBuilding;
	}
	
	/**
	 * Parsing the holiday filelist.
	 */
//	@SuppressWarnings("deprecation")
//	private void parseHolidayfile() {
//		
//		this.holidayListFile = new File(Preferences.getRoommateDir(
//				getApplicationContext()) + RoommateConfig.HOLIDAYFILE_URI);
//		
//		if (RoommateConfig.VERBOSE) {
//			System.out.println(this.holidayListFile.toString());
//		}
//		
//		if(this.holidayListFile.exists()) {
//			
//			try {
//				this.parser = new XercesParser();
//				this.parser.setHolidayFilePath(this.holidayListFile.toString());
//				this.parser.parseHolidayFile();
//				this.holidayList = parser.getHolidayList();
//
//				// check if the holiday filelist is up to date
//				if(!this.holidayList.checkYearStatus()) {
//					
//	    			// if not, try to download the new version
//	    			AlertDialog downloadDialog = new AlertDialog.Builder(this).create();
//	    			downloadDialog.setTitle(getString(R.string.dl_holidayfile_hl));
//	    			downloadDialog.setMessage(getString(R.string.dl_holidayfile_txt));
//	    			
//	    			class OnCLickListenerDownloadButtonYes 
//	    				implements android.content.DialogInterface.OnClickListener {
//	    				
//	    				public void onClick(DialogInterface dialog, int which) {
//
//	    					updateHolidayFile();
//	    				}
//	    			}
//	    			
//	    			// Yes-Button
//	    			downloadDialog.setButton(getString(android.R.string.yes),new OnCLickListenerDownloadButtonYes());
//	    			
//	    			// No-Button
//	    	        downloadDialog.setButton2(getString(android.R.string.no), new DialogInterface.OnClickListener() {
//	    	        	
//	    	            public void onClick(DialogInterface dialog, int which) {
//	    	            dialog.cancel();
//	    	            
//	    	            }
//	    	        });
//	    	        downloadDialog.setCancelable(false);
//	    			downloadDialog.show();
//				}
//				
//			} catch (SAXException e) {
//				e.printStackTrace();
//				if (RoommateConfig.VERBOSE) {
//					System.out.println("PARSER FAIL HOLIDAYFILE, SAXException");
//				}
//			} catch (IOException e) {
//				e.printStackTrace();
//				if (RoommateConfig.VERBOSE) {
//					System.out.println("PARSER FAIL HOLIDAYFILE, IOException");
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//				if (RoommateConfig.VERBOSE) {
//					System.out.println("PARSER FAIL HOLIDAYFILE, Exception");
//				}
//			}
//		} else {
//			if (RoommateConfig.VERBOSE) {
//				System.out.println("ERROR: No holidayfile present");
//			}
//		}
//	}
	
    /**
     * Downloading a new holiday file.
     */
//    protected void updateHolidayFile() {
//    	
//    	if(this.holidayListFile.delete()) {
//    		FileDownloader fd = new FileDownloader(this, new File(Preferences.getRoommateDir(this) + "/" + RoommateConfig.MISC_FOLDER + "/"),"","");
//    		fd.isHolidayfile(true);
//    		fd.execute(RoommateConfig.HOLIDAYFILE_URL);
//    	}
//    }
}