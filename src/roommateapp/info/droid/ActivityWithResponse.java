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
import roommateapp.info.entities.BuildingFile;
import android.content.Context;

/**
 * Interface for the Activitys.
 */
public interface ActivityWithResponse {
	
	public void printToastMessages(String text, int duration, final boolean returnToMain);
	
	public void createListView(ArrayList<BuildingFile> files);
	
	public void createListView();

	public Context getApplicationContext();

	public void returntoMain();
	
	public void showDialog(String text, String title, final boolean returnToMain);
	
	public void startUpdaterifUserisLoggedIn();

	public void setSaveBox();	
}