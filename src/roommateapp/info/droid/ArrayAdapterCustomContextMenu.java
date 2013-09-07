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
import roommateapp.info.R;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Array adapter for the context
 * custon menu within the ActictyBuilding.
 * @param <E>
 */
public class ArrayAdapterCustomContextMenu<E> extends ArrayAdapter<String> {

	// Instance variables
	private ArrayList<String> items;
	private Context context;
	private boolean isFilterActive;
	private boolean isDefault;
	private AlertDialog dialog;
	private ActivityBuilding<E> building;

	/**
	 * Constructor.
	 * 
	 * @param context
	 * @param resource
	 * @param isFilterActive
	 * @param isDefault
	 */
	public ArrayAdapterCustomContextMenu(Context context, int resource, 
			boolean isFilterActive, boolean isDefault, AlertDialog dialog, ActivityBuilding<E> building) {
		
		super(context, resource);
		
		this.isFilterActive = isFilterActive;
		this.isDefault = isDefault;
		this.context = context;
		this.dialog = dialog;
		this.building = building;
		this.items = new ArrayList<String>();
		
		// Fill arraylist with the context menu items
		if(this.isDefault) {
			this.items.add(context.getResources().getString(R.string.cmenu_remstd));
		}
		this.items.add(context.getResources().getString(R.string.cmenu_nav));
		this.items.add(context.getResources().getString(R.string.cmenu_info));
		this.items.add(context.getResources().getString(R.string.cmenu_share));
		this.items.add(context.getResources().getString(R.string.cmenu_legende));
		this.items.add(context.getResources().getString(R.string.cmenu_filter));
	}
	
	/**
	 * Get size of the items.
	 */
	public int getCount() {
		
		return this.items.size();
	}

	/**
	 * Switch Item to be displayed.
	 * 
	 * @param index
	 * @return
	 */
	public String switchItem(int index) {
		
		return this.items.get(index);
	}
	
	/**
	 * Create the view of the custom context menu item.
	 */
	public View getView(int position, View convertView, ViewGroup parent) {

		View row = convertView;
		TextView text;
		ImageView resetFilter;
		
		if (row == null) {

			// ROW INFLATION
			LayoutInflater inflater = (LayoutInflater) this.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.filerow_contextmenu, parent, false);
		}
		
		// Set text
		final String entry = switchItem(position);
		text = (TextView) row.findViewById(R.id.txt_customContextMenu);
		text.setText(entry);
		text.setTextColor(row.getResources().getColor(android.R.color.primary_text_light));
		
		// Change background color on android 2.x
		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			RelativeLayout rl = (RelativeLayout) row.findViewById(R.id.rl_rowContextMenu);
			rl.setBackgroundColor(row.getResources().getColor(android.R.color.background_light));
		}
		
		// Inner class for touch event on the reset filter button
		class OnTouchListenerResetButton implements OnTouchListener {

			private View row;
			
			public OnTouchListenerResetButton(View row) {
				this.row = row;
			}
			
			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				
				// Change the background color on touch
				if (MotionEvent.ACTION_DOWN == event.getAction()) {
					
					ImageView resetButton = (ImageView) row.findViewById(R.id.img_resetfilter);
					resetButton.setImageResource(R.drawable.cross_hover);
					
				// Change the background color back to normal when leaving the touch area 
				} else if (MotionEvent.ACTION_CANCEL == event.getAction()) {
					
					ImageView resetButton = (ImageView) row.findViewById(R.id.img_resetfilter);
					resetButton.setImageResource(R.drawable.cross_normal);
					
				// Act on pressing the reset button
				} else if (MotionEvent.ACTION_UP == event.getAction()) {
					
					building.resetFilter();
					dialog.dismiss();
				}
				
				return true;
			}
		}
		
		// Inner class for touch event on the row
		class OnTouchListenerEntry implements OnTouchListener {

			private TextView text;
			private View row;
			
			public OnTouchListenerEntry(TextView text, View row) {
				this.text = text;
				this.row = row;
			}
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				// Change the background color on touch
		        if (MotionEvent.ACTION_DOWN == event.getAction()) {
		        	
					text.setBackgroundColor(row.getResources().getColor(android.R.color.holo_blue_light));

				// Act on events
		        } else if (MotionEvent.ACTION_UP == event.getAction()) {
		        	
					text.setBackgroundColor(row.getResources().getColor(android.R.color.white));
					
					// Remove default entry
					if (entry.equals(row.getResources().getString(R.string.cmenu_remstd))) {
						
						Preferences.setDefaultFile(row.getContext(), "");
						dialog.dismiss();
												
					// Start navigation
					} else if (entry.equals(row.getResources().getString(R.string.cmenu_nav))) {
						
						building.showNavigation();
						dialog.dismiss();
					
					// Display share options
					} else if (entry.equals(row.getResources().getString(R.string.cmenu_share))) {
						
						building.showShareOptions();
						dialog.dismiss();
						
					// Show Building information
					} else if (entry.equals(row.getResources().getString(R.string.cmenu_info))) {
						
						building.showBuildingInformation();
						dialog.dismiss();
					
					// Display the legende
					} else if (entry.equals(row.getResources().getString(R.string.cmenu_legende))) {
						
						building.showSymbolLegend();
						dialog.dismiss();
						
					// Set filter
					} else if (entry.equals(row.getResources().getString(R.string.cmenu_filter))) {
						
						building.showFilterOptions();
						dialog.dismiss();
					}
		            v.performClick();
		         
		        // Change the background color back to normal when leaving the touch area
		        } else if (MotionEvent.ACTION_CANCEL == event.getAction()) {
		        	
		        	text.setBackgroundColor(row.getResources().getColor(android.R.color.transparent));
		        }
		        return true;
			}
		}
		text.setOnTouchListener(new OnTouchListenerEntry(text, row));
		
		// Reset-filter button
		resetFilter = (ImageView) row.findViewById(R.id.img_resetfilter);
		if(isFilterActive && entry.equals(context.getResources().getString(R.string.cmenu_filter))) {
			
			resetFilter.setOnTouchListener(new OnTouchListenerResetButton(row));

		} else {
			resetFilter.setVisibility(ImageView.INVISIBLE);
			resetFilter = (ImageView) row.findViewById(R.id.img_resetfilterline);
			resetFilter.setVisibility(ImageView.INVISIBLE);
		}
		return row;
	}
}