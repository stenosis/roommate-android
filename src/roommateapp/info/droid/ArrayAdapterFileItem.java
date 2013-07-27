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
import java.util.Collections;
import java.util.Comparator;
import roommateapp.info.entities.BuildingFile;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import roommateapp.info.R;

/**
 * Array adapter for the building file view
 * within the ActivityMain.
 */
public class ArrayAdapterFileItem extends ArrayAdapter<BuildingFile> {

	// Instance variables
	private ActivityMain mainActivity;
	private String defaultItem;
	private ArrayList<BuildingFile> buildingFiles;

	/**
	 * Constructor
	 * 
	 * @param context
	 * @param textViewResourceId
	 * @param objects Liste der Items
	 */
	public ArrayAdapterFileItem(ActivityMain mainActivity, Context context,
			int textViewResourceId, String defaultItem, ArrayList<BuildingFile> buildingFiles) {

		super(context, textViewResourceId);
		this.buildingFiles = buildingFiles;
		this.mainActivity = mainActivity;
		this.defaultItem = defaultItem;

		// Sort the entries
		Collections.sort(this.buildingFiles, new RowComarator());
	}

	// Inner Class for sorting the entries.
	class RowComarator implements Comparator<BuildingFile> {

		public int compare(BuildingFile b1, BuildingFile b2) {
			return b1.getBuildingname().compareTo(b2.getBuildingname());
		}
	}

	/**
	 * Returns the row count.
	 */
	public int getCount() {
		
		return this.buildingFiles.size();
	}
	
	/**
	 * Switch the selected Roommate file
	 * within this array adapter.
	 * 
	 * @param index
	 * @return
	 */
	public BuildingFile switchItem(int index) {
		
		return this.buildingFiles.get(index);
	}

	/**
	 * Setting up the row Roommate file data.
	 */
	@SuppressLint("CutPasteId")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View row = convertView;
		TextView frHeadline;
		TextView frSubline;
		
		if (row == null) {

			// ROW INFLATION
			LayoutInflater inflater = (LayoutInflater) this.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.filerow, parent, false);
		}

		final BuildingFile entry = switchItem(position);		

		// Setting the head and subline
		frHeadline = (TextView) row.findViewById(R.id.frHeadline);
		frHeadline.setText(entry.getBuildingname());
		frHeadline.setHorizontallyScrolling(true);

		frSubline = (TextView) row.findViewById(R.id.frSubline);
		
		// Set date and semester if available
		String date = entry.getDate();
		if (entry.getSemester() != null) {
			date = date + ", " + entry.getSemester();
		}
		
		frSubline.setText(date);

		// Inner class for an OnClick listener
		class OnCLickListenerFile implements OnClickListener {
			
			// Instance variables
			private BuildingFile building;

			/**
			 * Constructor
			 * 
			 * @param building currenft file
			 */
			public OnCLickListenerFile(BuildingFile building) {

				this.building = building;
			}

			/**
			 * Opens the selected Roommate file.
			 */
			public void onClick(View v) {

				mainActivity.openRoommateFile(building, true);
			}
		}
		
		// Set the LinearLayoutFileRow OnClick Listener
		LinearLayout linearLayoutFileRow = (LinearLayout) row.findViewById(R.id.LinearLayoutFileRow);
		linearLayoutFileRow.setOnClickListener(new OnCLickListenerFile(entry));

		// OnClickListener for the settings-button
		ImageView frSettings = (ImageView) row.findViewById(R.id.frSettingsButton);

		frSettings.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {

				// Register and open the context menu
				mainActivity.registerContextButton(v, entry);
				mainActivity.openContextMenu(v);
			}
		});
		
		// Change the background layout if this is the default file (green)
		if (this.defaultItem != null && switchItem(position).getFile().toString().equals((this.defaultItem))) {
		
			RelativeLayout ll = (RelativeLayout) row.findViewById(R.id.linearLayout1);
			ll.setBackgroundResource(R.drawable.bgfrstd2);

			// Change font color to white
			TextView headline = (TextView) row.findViewById(R.id.frHeadline);
			headline.setTextColor(Color.rgb(255, 255, 255));
			headline.setShadowLayer(0, 0, 0, Color.GRAY);
			TextView subline = (TextView) row.findViewById(R.id.frSubline);
			subline.setTextColor(Color.rgb(255, 255, 255));

			// Change images
			ImageView settings = (ImageView) row.findViewById(R.id.frSettingsButton);
			settings.setImageResource(R.drawable.frsettingsstd2);
			ImageView clock = (ImageView) row.findViewById(R.id.frImageSubline);
			clock.setImageResource(R.drawable.frclockstd2);
			
		} else {
			
			// Force default
			RelativeLayout ll = (RelativeLayout) row.findViewById(R.id.linearLayout1);
			ll.setBackgroundResource(R.drawable.bgfrstd);

			// Change font color
			TextView headline = (TextView) row.findViewById(R.id.frHeadline);
			headline.setTextColor(Color.rgb(88, 88, 88));
			headline.setShadowLayer(1, 0, 1, Color.WHITE);
			TextView subline = (TextView) row.findViewById(R.id.frSubline);
			subline.setTextColor(Color.rgb(88, 88, 88));

			// Change images
			ImageView settings = (ImageView) row.findViewById(R.id.frSettingsButton);
			settings.setImageResource(R.drawable.frsettingsstd);
			ImageView clock = (ImageView) row.findViewById(R.id.frImageSubline);
			clock.setImageResource(R.drawable.frclockstd);
		}
		return row;
	}
}
