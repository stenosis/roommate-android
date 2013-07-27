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
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import roommateapp.info.R;

/**
 * Page fragment for the ActivityBuilding
 * and the BuildingPageAdapater.
 * 
 */
public class BuildingPageFragment extends Fragment {

	// Instance variables
	public static final int FREEROOMS=1;
	public static final int ALLROOMS=0;
	private int activeFragment=1;

	/**
	 * 
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View v = new View(getActivity());
				
		// Saves the content and position when rotation the screen
		setRetainInstance(true);
		
		if (activeFragment == FREEROOMS){
			v = inflater.inflate(R.layout.all_rooms, container, false);
		}
		else if (activeFragment == ALLROOMS){
			v = inflater.inflate(R.layout.free_rooms, container, false);
		} 
		
		return v;
	}

	/**
	 * 
	 * @param Id
	 */
	public void setView(int Id){
		
		this.activeFragment=Id;
	}
}
