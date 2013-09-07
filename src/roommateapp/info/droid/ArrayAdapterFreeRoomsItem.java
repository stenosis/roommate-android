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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import roommateapp.info.entities.Room;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import roommateapp.info.R;

/**
 * Array adapter for the free room list
 * of the ActivityBuilding.
 */
@SuppressLint("NewApi")
public class ArrayAdapterFreeRoomsItem extends ArrayAdapter<Room> {
	
	// Instance variables
	private ActivityBuilding<?> activityBuilding;
	private static final String SIZE = " ";
	private String noFreeRoomsFound;
	private HashMap<Room, Integer> roomMap;
	private ArrayList<Room> rooms;
	private Context c;
	
	// Inner class for an OnClick listener
	class OnCLickListenerRoom implements OnClickListener {

		// Instance variables
		private Room room;

		/**
		 * Constructor
		 * 
		 * @param room
		 */
		public OnCLickListenerRoom(Room room) {

			this.room = room;
		}

		/**
		 * Opens the selected room.
		 */
		public void onClick(View v) {

			activityBuilding.openRoom(room.getName());
		}
	}

	/**
	 * Setting up the row room data.
	 * 
	 * @param context
	 * @param textViewResourceId
	 * @param roomsMap
	 * @param activityBuilding
	 */
	public ArrayAdapterFreeRoomsItem(Context context, int textViewResourceId,
			                             HashMap<Room, Integer> roomsMap, 
			                             ActivityBuilding<?> activityBuilding) {
		
		super(context, textViewResourceId);
		this.roomMap = roomsMap;
		this.c = context;
		this.noFreeRoomsFound = this.c.getString(R.string.msg_nofreeroomsfound);
		Iterator<Room> iter = roomMap.keySet().iterator();
  		this.rooms = new ArrayList<Room>();
  		while(iter.hasNext()) {
  			
  			Room key = (Room) iter.next();
  			rooms.add(key);
  		}
		this.activityBuilding = activityBuilding;	
		
		// When there's no entrie to display
		if(getCount() == 0) {
			
			Toast.makeText(getContext(), this.noFreeRoomsFound, Toast.LENGTH_SHORT).show();
		
			
		} else {
			
			// Sort the entries
			Collections.sort(this.rooms, new RowComarator());
		}
	}
	
	// Inner Class for sorting the entries.
	class RowComarator implements Comparator<Room> {

		public int compare(Room r1, Room r2) {
			int comp = roomMap.get(r2).compareTo(roomMap.get(r1));
			comp = (comp == 0) ?  r1.getName().compareTo(r2.getName()) : comp;
			return comp;
		}
	}
	
	/**
	 * Returns the row count.
	 */
	@Override
	public int getCount() {
		
		return rooms.size();
	}

	/**
	 * Setting up the free rooms row view.
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View row = convertView;
		Room currentRoom = rooms.get(position);
		
		if (row == null) {
			// ROW INFLATION
			LayoutInflater inflater = (LayoutInflater) this.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.filerow_free_rooms, parent, false);
		}
		
		// Name
		TextView textview = (TextView) row.findViewById(R.id.txt_free_room_name);
		textview.setShadowLayer(1, 0, 1, Color.WHITE);
		textview.setText(currentRoom.getName());
		
		// Click listener
		RelativeLayout layout = (RelativeLayout) row.findViewById(R.id.layout_freeroom);
		layout.setOnClickListener(new OnCLickListenerRoom(currentRoom));
		
		// Raum size
		textview = (TextView) row.findViewById(R.id.txt_roomSize);
		
		if(currentRoom.getRoomSize() == 0) {	
			
			textview.setText(this.c.getString(R.string.room_sizeNA));
		} else {
			
			textview.setText(SIZE + currentRoom.getRoomSize());
		}
		
		// Display for how long the room is available
		textview = (TextView) row.findViewById(R.id.txt_freelectures);
		
		textview.setText("" + this.roomMap.get(currentRoom));
		
		// Room propertie array
		ImageView[] images = {(ImageView) row.findViewById(R.id.imgFilter00),
							(ImageView) row.findViewById(R.id.imgFilter01),
							(ImageView) row.findViewById(R.id.imgFilter02),
							(ImageView) row.findViewById(R.id.imgFilter03),
							(ImageView) row.findViewById(R.id.imgFilter04),
							(ImageView) row.findViewById(R.id.imgFilter05),
							(ImageView) row.findViewById(R.id.imgFilter06)};
		
		// Reset the parants propertie images
		for(int i = 0; i < images.length; i++) {
			images[i].setImageResource(R.drawable.filterempty);
		}
		
		// Properties to filter
		if(currentRoom.hasPropteries() || currentRoom.getRoomInformation() != null) {
			
			int index = 0;
			// Infostring
			if(currentRoom.getRoomInformation().length() > 0) {
				images[index].setImageResource(R.drawable.filterinfotext);
				index++;
			}
			// Whiteboard
			if(currentRoom.getWhiteboardStatus()) {
				images[index].setImageResource(R.drawable.filterwhiteboard);
				index++;
			}
			// Power supply
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
		}
		return row;
	}
}
