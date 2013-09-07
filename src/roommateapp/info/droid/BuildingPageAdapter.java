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
import java.util.List;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import roommateapp.info.R;

/**
 * Page adapter for the ActivityBuilding.
 * Displaying two pages: 
 * 1.) Free rooms 
 * 2.) Roomlist
 */
public class BuildingPageAdapter extends PagerAdapter {
	
	// Instance variables
    List<View> pages = null;
    private String[] titles = new String[2];
    
    /**
     * 
     * @param pages
     * @param c
     */
    public BuildingPageAdapter(List<View> pages, Context c) {
    	
        this.pages = pages;
        this.titles[0] = c.getString(R.string.building_titleP1); // Free rooms
        this.titles[1] = c.getString(R.string.building_titleP2); // Roomlist
    }

    /**
     * 
     */
    @Override
    public int getCount() {
    	
        return pages.size();
    }

    /**
     * 
     */
    @Override
    public boolean isViewFromObject(View view, Object object) {
    	
        return view.equals(object);
    }

    /**
     * 
     */
    @Override
    public Object instantiateItem(View collection, int position){
    	
        View v = pages.get(position);
        ((ViewPager) collection).addView(v, 0);
        return v;
    }

    /**
     * 
     */
    @Override
    public void destroyItem(View collection, int position, Object view){
    	
        ((ViewPager) collection).removeView((View) view);
    }
    
    /**
	 * 
	 */
	@Override
	public CharSequence getPageTitle(int position) {
		
		return titles[position];
	}
}


/**
 * DO NOT DELETE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 */
//public class BuildingPageAdapter extends FragmentPagerAdapter {
//
//	/* Instanzvariablen */
//	private final int NUM_PAGES = 2;
//	private final String[] titles = { "Freie Räume", "Raumliste",};
//
//	/**
//	 * 
//	 * @param fm
//	 */
//	public BuildingPageAdapter(FragmentManager fm) {
//		super(fm);
//	}
//
//	/**
//	 * 
//	 */
//	@Override
//	public Fragment getItem(int pos) {
//		
//		BuildingPageFragment f = new BuildingPageFragment();
//		f.setView(pos);
//		return f;
//	}
//
//	/**
//	 * 
//	 */
//	@Override
//	public int getCount() {
//		return NUM_PAGES;
//	}
//
//	/**
//	 * 
//	 */
//	@Override
//	public CharSequence getPageTitle(int position) {
//		return titles[position];
//	}
//}


	
	
	

