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
import java.util.ArrayList;
import roommateapp.info.entities.Lecture;
import roommateapp.info.entities.Room;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import roommateapp.info.R;

/**
 * Array adapter for the room information
 * needed to display within the ActivityRoom.
 */
public class ArrayAdapterRoomInfoItem extends ArrayAdapter<Room> {
	
	// Instance variables
	private String available;
	private String damagedInformation;
	private ArrayList<Lecture> lectures;
	private String[] lectureTimesExtra;
	private String selectedTime;
	private Context c;
	
	/**
	 * Constructor
	 * 
	 * @param context
	 * @param textViewResourceId
	 * @param lectures
	 * @param lectureTimesExtra
	 */
	public ArrayAdapterRoomInfoItem(Context context, int textViewResourceId,
										ArrayList<Lecture> lectures, String[] lectureTimesExtra,
										String selectedTime) {
		
		super(context, textViewResourceId);
		this.lectures = lectures;
		this.lectureTimesExtra = lectureTimesExtra;
		this.selectedTime = selectedTime;
		this.c = context;
		
		this.available = this.c.getString(R.string.available);
		this.damagedInformation = this.c.getString(R.string.error_damageddata);
		
		// If the room got more lectures than registred hours
		if(getCount() != lectureTimesExtra.length) {
			
			System.out.println("ERROR: Room got damaged information.");
			Toast.makeText(getContext(), damagedInformation, Toast.LENGTH_LONG).show();
			ActivityRoom activityRoom = (ActivityRoom) context;
			activityRoom.onBackPressed();
		}
	}

	/**
	 * Returns the row count.
	 */
	@Override
	public int getCount() {
		return lectures.size();
	}
	
	/**
	 * Checks if the times of a lecture match.
	 * 
	 * @return
	 */
	private boolean checkMatchTime(String time) {
		
		boolean match = false;
		
		Integer rowLectureStart = Integer.parseInt(time.substring(0,5).replace(":", ""));
		Integer rowLectureEnd = Integer.parseInt(time.substring(6,11).replace(":", ""));
		Integer selectedTime = Integer.parseInt(this.selectedTime.replace(":", ""));

		if(selectedTime >= rowLectureStart && selectedTime < rowLectureEnd) {
			match = true;
		}
		
		return match;
	}

	/**
	 * setzt
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View row = convertView;
		String info = "";
		
		if (row == null) {
			
			// ROW INFLATION
			LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.filerow_room_info, parent, false);
		}
		
		// If the room is available at the current hour
		if(lectures.get(position).toString().equals("available")) {
			
			// Change color theme for free lecture
			LinearLayout ll = (LinearLayout) row.findViewById(R.id.ll_roominfo);
			ll.setBackgroundResource(R.drawable.bgfrstd2);
			
			TextView roomInfo = (TextView) row.findViewById(R.id.txt_room_info);
			roomInfo.setTextColor(Color.rgb(255, 255, 255));
			roomInfo.setShadowLayer(0, 0, 0, Color.WHITE);
			
			TextView lecturePeriode = (TextView) row.findViewById(R.id.txt_lecturePeriod);
			lecturePeriode.setTextColor(Color.rgb(255, 255, 255));
			
			TextView lectureIndex = (TextView) row.findViewById(R.id.txt_indexLecture);
			lectureIndex.setTextColor(Color.rgb(255, 255, 255));
			
			ImageView clockIcon = (ImageView) row.findViewById(R.id.img_room_clock);
			clockIcon.setImageResource(R.drawable.frclockstd2);
			
			ImageView rowMark = (ImageView) row.findViewById(R.id.img_rowmark);
			rowMark.setImageResource(R.drawable.rowmark2);
			
			// Is this the current lecture which needed to mark?
			if(checkMatchTime(lectureTimesExtra[position])) {
				rowMark.setVisibility(ImageView.VISIBLE);
			} else {
				rowMark.setVisibility(ImageView.INVISIBLE);
			}
			
			// Setting the number of the lecture
			TextView textview = (TextView) row.findViewById(R.id.txt_indexLecture);
			textview.setText((position + 1) + ".");
			
			// Setting the time periode
			textview = (TextView) row.findViewById(R.id.txt_lecturePeriod);
			textview.setText(lectureTimesExtra[position]);
			
			// Setting the lecture name
			info = info + available;
			textview = (TextView) row.findViewById(R.id.txt_room_info);
			textview.setText(info);
		
	    // if the room is occupied at the current lecture
		} else {
						
			// Change color theme for an occupied lecture
			LinearLayout ll = (LinearLayout) row.findViewById(R.id.ll_roominfo);
			ll.setBackgroundResource(R.drawable.bgfrstd);
			
			TextView roomInfo = (TextView) row.findViewById(R.id.txt_room_info);
			roomInfo.setTextColor(Color.rgb(88, 88, 88));
			roomInfo.setShadowLayer(1, 0, 1, Color.WHITE);
			
			TextView lecturePeriode = (TextView) row.findViewById(R.id.txt_lecturePeriod);
			lecturePeriode.setTextColor(Color.rgb(88, 88, 88));
			
			TextView lectureIndex = (TextView) row.findViewById(R.id.txt_indexLecture);
			lectureIndex.setTextColor(Color.rgb(88, 88, 88));
			
			ImageView clockIcon = (ImageView) row.findViewById(R.id.img_room_clock);
			clockIcon.setImageResource(R.drawable.frclockstd);
			
			ImageView rowMark = (ImageView) row.findViewById(R.id.img_rowmark);
			rowMark.setImageResource(R.drawable.rowmark);
			
			// Is this the current lecture which needed to mark?
			if(checkMatchTime(lectureTimesExtra[position])) {
				rowMark.setVisibility(ImageView.VISIBLE);
			} else {
				rowMark.setVisibility(ImageView.INVISIBLE);
			}
			
			// Setting the number of the lecture
			TextView textview = (TextView) row.findViewById(R.id.txt_indexLecture);
			textview.setText((position + 1) + ".");
			
			// Setting the time periode
			textview = (TextView) row.findViewById(R.id.txt_lecturePeriod);
			textview.setText(lectureTimesExtra[position]);
			
			// Setting the lecture name
			info = info + lectures.get(position).toString();
			textview = (TextView) row.findViewById(R.id.txt_room_info);
			textview.setText(info);
		}
		return row;
	}
}
