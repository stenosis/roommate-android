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
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import roommateapp.info.entities.BuildingFile;
import roommateapp.info.io.DirFileChecker;
import roommateapp.info.net.ClientUpdateChecker;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import roommateapp.info.R;

/**
 * ActivityIntroduction displays introduction
 * information by the first start of the Roommate app.
 *
 */
public class ActivityIntroduction extends Activity {
	
	// Instance variables
	private List<View> pages;
	private ViewPager viewPager;
	private ViewPager mViewPager;
	private File roommateDirectory;
	private String roommateURL;
	private DirFileChecker dirFileChecker;
	private boolean checkedOnceForClientUpdate;
	private boolean isSDPresent;
	
	/**
	 * On create of the activity
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_introduction);
		
		// Remove animation during switching activitys
	    overridePendingTransition(0, 0);
		
	    // Get some data for the activity
		this.roommateDirectory = new File(getIntent().getStringExtra("sdPath"));
		this.roommateURL = getIntent().getStringExtra("roommateURL");
		
		// Checking if Roommate-files are available
    	checkForFiles();
		
    	// Removing default entry from preferences
		resetToDefault();
		
		// Setting up the inflator for the info pages
		LayoutInflater inflater = LayoutInflater.from(this);
	    pages = new ArrayList<View>();

	    // Register pages
	    View page = inflater.inflate(R.layout.introduction_page_1, null);
	    pages.add(page);
	    
	    page = inflater.inflate(R.layout.introduction_page_2, null);
	    pages.add(page);
	    
	    page = inflater.inflate(R.layout.introduction_page_3, null);
	    pages.add(page);
	    
		BuildingPageAdapterIntroduction pagerAdapter = new BuildingPageAdapterIntroduction(pages);
	    viewPager = new ViewPager(this);
	    viewPager.setAdapter(pagerAdapter);
	    viewPager.setCurrentItem(0);     	    
	    mViewPager= (ViewPager) findViewById(R.id.viewpagerintroduction);
	    mViewPager.setAdapter(pagerAdapter);

	    // Change indicator theme colors
	    highlightIndicator(0);
	    mViewPager.setOnPageChangeListener(new OnPageChangeListener(){
	    	 
            @Override
            public void onPageScrollStateChanged(int position) {}
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {}
            @Override
            
            public void onPageSelected(int position) {
            	
                switch(position){
                case 0:
                	highlightIndicator(0);
                    break;

                case 1:
                	highlightIndicator(1);
                    break;
                
                case 2:
                	highlightIndicator(2);
                	break;
                }
            }
	    });
	    
		// Open ActivityAbout by clicking on the headline
		TextView title = (TextView) this.findViewById(R.id.headline);
		title.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onClickGoToAbout(v);
			}
		});
	    
	    // Check for available Client updates.
	    checkForClientUpdates();
	}
	
	/**
	 * Checks for available Roommate App updates.
	 */
	private void checkForClientUpdates() {
		
        if(!Preferences.isClientUpdateUserInteraction(getApplicationContext())
        		|| Preferences.isClientUpdateEnabled(getApplicationContext())) {
        	
        	if(!this.checkedOnceForClientUpdate) {
        		
            	Preferences.setClientUpdateEnabled(getApplicationContext(), true);
            	
    			ClientUpdateChecker updateChecker = new ClientUpdateChecker(this, false);
    			updateChecker.execute(getString(R.string.aboutVersion));
        	}
        } 
	}
	
	/**
	 * 
	 */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
	    
    	menu.add(R.string.menu_addfile);
	    menu.add(R.string.title_settings);
	    menu.add(R.string.title_about);
	    return true;
    }
    
    /**
     * 
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	
    	String selItem = (String) item.getTitle();
    	View v = getWindow().getDecorView().findViewById(android.R.id.content);
    	
    	if(selItem.equals(getString(R.string.menu_addfile))) {
    		
    		onClickGoToAddFile(v);
    		
    	} else if (selItem.equals(getString(R.string.title_settings))) {
    		
    		onClickGoToSettings(v);
    		
    	} else if (selItem.equals(getString(R.string.title_about))) {
    		
    		onClickGoToAbout(v);
    	}
    	return true;
    }	
	
    /**
     * Resume.
     */
	@Override
	protected void onResume() {
		super.onResume();
		
    	checkForFiles();
	}
	
	/**
	 * Checking for available Roommate-files.
	 */
	private void checkForFiles() {
		
		this.isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
		this.dirFileChecker = new DirFileChecker(this.roommateDirectory);
		ArrayList<BuildingFile> validatedBuildingfiles = this.dirFileChecker.getValidatedFiles();
		
		// If we found one, we'll return to the main activity
		if(validatedBuildingfiles.size() > 0) {
			Intent intent = new Intent(this, ActivityMain.class);
			startActivity(intent);
		}
		
		if(!this.isSDPresent) {
			
			Toast.makeText(getApplicationContext(), getString(R.string.msg_needSD), Toast.LENGTH_LONG).show();
		}
	}	
	
	/**
	 * Removing the default entry from the preferences.
	 */
	private void resetToDefault() {
		
		Preferences.setDefaultFile(this, "");
	}
	
	/**
	 * onBackPressed moves the app to the background.
	 */
	@Override
	public void onBackPressed() {
		
		moveTaskToBack(true);
	}
	
	/**
	 * Switching to the ActivitySetting.
	 * 
	 * @param view
	 */
	public void onClickGoToSettings(View view) {
		
		Intent intent = new Intent(this, ActivitySettings.class);
		startActivity(intent);
	}

	/**
	 * Switching to the ActiivtyAbout.
	 * 
	 * @param view
	 */
	public void onClickGoToAbout(View view) {
		
		Intent intent = new Intent(this, ActivityAbout.class);
		startActivity(intent);
	}
	
	/**
	 * Switching to the ActivityAddFile.
	 * 
	 * @param view
	 */
	public void onClickGoToAddFile(View view) {
		
		Intent intent = new Intent(this, ActivityAddFile.class);
		
		intent.putExtra("sdPath", this.roommateDirectory.toString());
		intent.putExtra("roommateURL", this.roommateURL);
		
		startActivity(intent);
	}
	
	/**
	 * Change the indicator which displays 
	 * the current active page.
	 * 
	 * @param number 0-2
	 */
	private void highlightIndicator(int number) {
		
		if(number <= 2) {
		
			ImageView indicator;
			
			switch (number) {
				case 0:
					indicator = (ImageView) findViewById(R.id.imgIntroDot1);
					indicator.setImageResource(R.drawable.dot1);
					
					indicator = (ImageView) findViewById(R.id.imgIntroDot2);
					indicator.setImageResource(R.drawable.dot0);
					indicator = (ImageView) findViewById(R.id.imgIntroDot3);
					indicator.setImageResource(R.drawable.dot0);
					break;
				case 1:
					indicator = (ImageView) findViewById(R.id.imgIntroDot2);
					indicator.setImageResource(R.drawable.dot1);
					
					indicator = (ImageView) findViewById(R.id.imgIntroDot1);
					indicator.setImageResource(R.drawable.dot0);
					indicator = (ImageView) findViewById(R.id.imgIntroDot3);
					indicator.setImageResource(R.drawable.dot0);
					break;
				case 2:
					indicator = (ImageView) findViewById(R.id.imgIntroDot3);
					indicator.setImageResource(R.drawable.dot1);
					
					indicator = (ImageView) findViewById(R.id.imgIntroDot1);
					indicator.setImageResource(R.drawable.dot0);
					indicator = (ImageView) findViewById(R.id.imgIntroDot2);
					indicator.setImageResource(R.drawable.dot0);
					break;
			}
		}
	}
}
