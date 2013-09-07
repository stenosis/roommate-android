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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import roommateapp.info.R;

/**
 * The "About-Screen" of the Roommate-App
 */
public class ActivityAbout extends Activity {

	// Instance variables
	private static final String SPACEDOT = ((char) 183) + " ";
	
	// Slogans
	private static final String[] SLOGANS = {
		"Let Your Roommate Do The Walking",
		"Everyone's Favourite Roommate",
		"The World's Favourite Roommate",
		"My Doctor Says Roommate",
		"The Roommate Breakfast",
		"Just Do Roommate",
		"The Science Of Roommate",
		"There Ain't No Party Like A Roommate Party",
		"Next To The Breast, Roommate's the Best",
		"Tell Them About The Roommate, Mummy",
		"Recommended By Dr. Roommate",
		"The Right Roommate at the Right Time",
		"The Roommate Effect",
		"Please Don't Squeeze The Roommate",
		"Aaahh, Roommate!",
		"Plop, Plop, Fizz, Fizz, Oh, What a Roommate it is!",
		"You Can Be Sure of Roommate",
		"Ding-Dong! Roommate Calling!",
		"Mama's got the Magic of Roommate",
		"Strong and Beautiful, Just Like Roommate"
	};
	private static final String SLOGANFRI = "Thank Roommate It's Friday";

	/**
	 * On create of the activity
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		
		// Headline returns to the prev. activity.
		TextView title = (TextView) this.findViewById(R.id.headline);
		title.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		
		// Set text
		setText();
		setTextShadow();
		sloganized();
	}
    
    /**
     * Set text-shadow effects in this activity.
     */
    private void setTextShadow() {
    	
		TextView headline = (TextView) this.findViewById(R.id.txt_about_headline);
		headline.setShadowLayer(1, 0, 1, Color.WHITE);
    	
		TextView version = (TextView) this.findViewById(R.id.txt_about_hl_version);
		version.setShadowLayer(1, 0, 1, Color.WHITE);
		
		TextView details = (TextView) this.findViewById(R.id.txt_about_hl_details);
		details.setShadowLayer(1, 0, 1, Color.WHITE);
		
		TextView changelog = (TextView) this.findViewById(R.id.txt_about_hl_changelog);
		changelog.setShadowLayer(1, 0, 1, Color.WHITE);
		
		TextView team = (TextView) this.findViewById(R.id.txt_about_hl_team);
		team.setShadowLayer(1, 0, 1, Color.WHITE);
		
		TextView licence = (TextView) this.findViewById(R.id.txt_about_hl_licence);
		licence.setShadowLayer(1, 0, 1, Color.WHITE);
    }

	/**
	 * Open the Roommate-website.
	 * 
	 * @param v View
	 */
	public void onClickURL(View v) {
		
		Intent browseIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(RoommateConfig.ROOMMATE_URL));
		startActivity(browseIntent);
	}
	
	/**
	 * Returns the compile date for a timestamp.
	 * 
	 * @return Compile date
	 */
	private String getCompileDate() {
		
		String date = "";

		try {
			ApplicationInfo ai = getPackageManager().getApplicationInfo(getPackageName(), 0);
			ZipFile zf = new ZipFile(ai.sourceDir);
			ZipEntry ze = zf.getEntry("classes.dex");
			long time = ze.getTime();
			date = SimpleDateFormat.getInstance().format(new java.util.Date(time));
			date.replace(" ", " ,");

		} catch (Exception e) {

		}
		return date;
	}
	
	/**
	 * Set a random slogan :)
	 */
	private void sloganized() {
		
		int friday = (Calendar.FRIDAY - 1);
		int weekday = new GregorianCalendar().get(Calendar.DAY_OF_WEEK) - 1;
		
		// when it's friday, set the special friday-slogan
		if (weekday == friday) {
			
			TextView slogan = (TextView) this.findViewById(R.id.txt_about_headline);
			slogan.setText(SLOGANFRI);
			
		// else get a random slogan
		} else {
			
			int random = (int) (Math.random() * SLOGANS.length);
			TextView slogan = (TextView) this.findViewById(R.id.txt_about_headline);
			slogan.setText(SLOGANS[random]);			
		}
	}
	
	/**
	 * Set a bunch of information textfields.
	 */
	private void setText() {
		
		// app version
		TextView tv_version = (TextView) this.findViewById(R.id.txt_about_version);
		tv_version.setText(getString(R.string.aboutVersion) + ", Timestamp: " + getCompileDate());

		// contact information
		TextView tv_details = (TextView) this.findViewById(R.id.txt_about_details);
		String about_details = "Support: support@roommateapp.info\n" +
				"Website: www.roommateapp.info\n";

		tv_details.setText(about_details);
		
		// changelog
		TextView tv_changelog = (TextView) this.findViewById(R.id.txt_about_changelog);
		String about_changelog = "Version 1.2.5\n" +
				SPACEDOT + getString(R.string.changelog1_2_5_p1) + "\n" +
				SPACEDOT + getString(R.string.changelog1_2_5_p2) + "\n" +
				SPACEDOT + getString(R.string.changelog1_2_5_p3) + "\n" +
				SPACEDOT + getString(R.string.changelog1_2_5_p4) + "\n" +
				SPACEDOT + getString(R.string.changelog1_2_5_p5) + "\n" +
				"\nVersion 1.2\n" +
				SPACEDOT + getString(R.string.changelog1_2_p1) + "\n" +
				SPACEDOT + getString(R.string.changelog1_2_p2) + "\n" +
				SPACEDOT + getString(R.string.changelog1_2_p3) + "\n" +
				SPACEDOT + getString(R.string.changelog1_2_p4) + "\n" +
				SPACEDOT + getString(R.string.changelog1_2_p5) + "\n" +
				"\nVersion 1.1\n" +
				SPACEDOT + getString(R.string.changelog1_1_p1) + "\n" +
				SPACEDOT + getString(R.string.changelog1_1_p2) + "\n" +
				SPACEDOT + getString(R.string.changelog1_1_p3) + "\n" +
				SPACEDOT + getString(R.string.changelog1_1_p4) + "\n" +
				"\nVersion 1.0\n" +
				SPACEDOT + getString(R.string.changelog1_0_p1);
		tv_changelog.setText(about_changelog);
		
		// Team-Roommate
		TextView tv_team = (TextView) this.findViewById(R.id.txt_about_team);
		String about_team = "Team Roommate SPIN/SPMI 2012-2013:\n\n" +
				SPACEDOT + "Sarah Meilwes\n" +
				SPACEDOT + "Tim Rieck\n" +
				SPACEDOT + "Jan Ruland\n" +
				SPACEDOT + "Peter Honerbom\n" +
				SPACEDOT + "Gerrit van Gelder\n" +
				SPACEDOT + "Bastian Behr\n\n" +
				getString(R.string.aboutThanksTo) + ":\n\n" +
				SPACEDOT + "Prof. Dr. Andreas M. Heinecke\n" +
				SPACEDOT + "Dipl.-Inform. Karen Bensmann\n" +
				SPACEDOT + "Mike Neumann\n" +
				SPACEDOT + "Christopher Huber\n" +
				SPACEDOT + "Matthias Rottländer\n" +
				SPACEDOT + "Armin Al-Failee\n" +
				SPACEDOT + "Max Schulte";
		
		tv_team.setText(about_team);
		
		// licence
		TextView tv_licence = (TextView) this.findViewById(R.id.txt_about_licence);
		String about_licence = SPACEDOT + "Roommate\nTeam Roommate\nCopyright © 2012-2013 Tim Rieck\nReleased under the GPL3\n\n" +
				SPACEDOT + "Xerces\nApache Software Foundation\nApache License 2.0\n\n" +
				SPACEDOT + "ZXing\nApache License 2.0";
		tv_licence.setText(about_licence);
	}

	/**
	 * Open the GPLv3 website.
	 * 
	 * @param v View
	 */
	public void onClickGPL(View v) {
		
		Intent browseIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(RoommateConfig.GPL_URL));
		startActivity(browseIntent);
	}
	
	/**
	 * Open the Google+ website.
	 * 
	 * @param v
	 */
	public void onClickGooglePlus(View v) {
		
		Intent browseIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(RoommateConfig.GOOGLEPLUS_URL));
		startActivity(browseIntent);
	}
	
	/**
	 * Open the Facebook website.
	 * 
	 * @param v
	 */
	public void onClickFacebook(View v) {

		Intent browseIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(RoommateConfig.FACEBOOK_URL));
		startActivity(browseIntent);
	}
	
	/**
	 * Open the GitHub website.
	 * 
	 * @param v
	 */
	public void onClickGitHub(View v) {

		Intent browseIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(RoommateConfig.GITHUB_URL));
		startActivity(browseIntent);
	}
	
    /**
     * Return to the main activity.
	 * 
	 * @param view
	 */
	public void onClickGoToMain(View view) {
		
		super.onBackPressed();
	}
}