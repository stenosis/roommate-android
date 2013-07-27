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
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import roommateapp.info.R;

/**
 * Array adapter for displaying the updatable Roommate 
 * files which belongs to the ActivitySync.
 */
public class ArrayAdapterUpdateFileItem extends ArrayAdapter<BuildingFile> {

	// Instance variables
	private ActivitySync syncActivity;
	private ArrayList<BuildingFile> buildingFiles;
	private Context c;

	/**
	 * Constructor
	 * 
	 * @param context
	 * @param textViewResourceId
	 * @param objects Liste der Items
	 */
	public ArrayAdapterUpdateFileItem(ActivitySync mainActivity, Context context,
			int textViewResourceId, ArrayList<BuildingFile> buildingFiles) {

		super(context, textViewResourceId);
		this.buildingFiles = buildingFiles;
		this.syncActivity = mainActivity;
		this.c = context;

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
	 * Switch the selected Roommate file.
	 * 
	 * @param index
	 * @return
	 */
	public BuildingFile switchItem(int index) {
		
		return this.buildingFiles.get(index);
	}

	/**
	 * Setting up the row data.
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View row = convertView;
		TextView frHeadline;
		TextView frSubline;

		if (row == null) {

			// ROW INFLATION
			LayoutInflater inflater = (LayoutInflater) this.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.filerow_sync, parent, false);
		}

		final BuildingFile entry = switchItem(position);		

		// Setting up the entries
		frHeadline = (TextView) row.findViewById(R.id.frHeadlinesync);
		frHeadline.setText(entry.getBuildingname());
		frHeadline.setHorizontallyScrolling(true);

		frSubline = (TextView) row.findViewById(R.id.frSublinesync);

		String date = entry.getDate();
		
		// Setting date and release
		frSubline.setText(date + ", " + this.c.getString(R.string.title_version) + ": " + entry.getRelease());
		
		// Inner class for an OnClick listener
		class OnLongCLickListenerFile implements OnLongClickListener {
			
			// Instance variables
			@SuppressWarnings("unused")
			public BuildingFile building1;
			private View row;

			/**
			 * Constructor
			 * 
			 * @param building
			 */
			public OnLongCLickListenerFile(BuildingFile building1, View row) {

				this.building1 = building1;
				this.row = row;
			}
			
			/**
			 * A long click will select the file as marked.
			 */
			public boolean onLongClick(View v) {
				
				CheckBox checkboxUpdate = (CheckBox) row.findViewById(R.id.checkBoxupdate);
				
				if (checkboxUpdate.isChecked()){
					checkboxUpdate.setChecked(!checkboxUpdate.isChecked());
				}
				return false;
			}
		}
		
		// Reset to default values
		 RelativeLayout ll = (RelativeLayout) row.findViewById(R.id.LinearLayoutFileRowsync);
		ll.setBackgroundResource(R.drawable.bgfrstd);
		ll.setOnLongClickListener(new OnLongCLickListenerFile(entry,row));
		
		// Change font color and images
		TextView headline = (TextView) row.findViewById(R.id.frHeadlinesync);
		headline.setTextColor(Color.rgb(88, 88, 88));
		headline.setShadowLayer(1, 0, 1, Color.WHITE);
		TextView subline = (TextView) row.findViewById(R.id.frSublinesync);
		subline.setTextColor(Color.rgb(88, 88, 88));
		ImageView clock = (ImageView) row.findViewById(R.id.frImageSublinesync);
		clock.setImageResource(R.drawable.frclockstd);

		// Inner class for an OnClick listener
		class OnCLickListenerFile implements View.OnClickListener {
			
			// Instance variables
			public BuildingFile building1;
			
			/**
			 * Constructor
			 * @param building 
			 */
			public OnCLickListenerFile(BuildingFile building1) {

				this.building1 = building1;
			}

			// not used at the moment
			public void onClick(View v) {

			}
		}

		CheckBox checkboxUpdate = (CheckBox) row.findViewById(R.id.checkBoxupdate);
	
		// Set the onclick listener
		checkboxUpdate.setOnClickListener(new OnCLickListenerFile(entry) {

			public void onClick(View v) {

				if (((CheckBox) v).isChecked()) {
					
					if (RoommateConfig.VERBOSE) {
						
						System.out.println("Upgrading: " + (building1).toString());
					}
					syncActivity.addFileFromfilesToBeUpdated(entry);
				} else {
					
					if (RoommateConfig.VERBOSE) {
						
						System.out.println((building1).toString() + "wont be updated anymore");
					}
					syncActivity.removeFileFromfilesToBeUpdated(entry);
				}
			}	
		});
		
		// When all files are marked
		if (this.syncActivity.allFilesChecked){
		
			checkboxUpdate.setChecked(true);
		}

		return row;
	}
}
