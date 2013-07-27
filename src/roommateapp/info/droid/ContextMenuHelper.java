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
import java.util.Iterator;
import java.util.Set;
import roommateapp.info.entities.BuildingFile;
import roommateapp.info.entities.Room;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import roommateapp.info.R;

/**
 * The ContextMenuHelper class is a basic helper class
 * which is used to provide access to the context menu 
 * through multiple activitys.
 */
public class ContextMenuHelper {

	// Instance variables
	private Context c;
	private String social_body;
	private String social_subject;
	private String roominfo;
	private String prop_beamer;
	private String prop_projector;
	private String prop_network;
	private String prop_power;
	private String prop_whiteboard;
	private String prop_pcool;
	private String exception;
	
	/**
	 * Constructor
	 * 
	 * @param c
	 */
	public ContextMenuHelper(Context c) {
		
		this.c = c;
		
		// Localisation
		this.social_body = c.getString(R.string.social_body);
		this.social_subject = c.getString(R.string.social_subject);
		this.roominfo = c.getString(R.string.info_roominfo);
		this.prop_beamer = c.getString(R.string.legend_beamer) + ".\n";
		this.prop_projector = c.getString(R.string.legend_projector) + ".\n";
		this.prop_network = c.getString(R.string.legend_network) + ".\n";
		this.prop_power = c.getString(R.string.legend_power) + ".\n";
		this.prop_whiteboard = c.getString(R.string.legend_whiteboard) + ".\n";
		this.prop_pcool = c.getString(R.string.legend_pc) + ".\n";
		this.exception = c.getString(R.string.info_exception) + ":\n";
	}
	
	/**
	 * Share the selected file.
	 * 
	 * @param selectedBuilding
	 * @return
	 */
	public Intent shareFile(BuildingFile selectedBuilding) {
		
		Uri uri = Uri.parse("file://" + selectedBuilding.getFile().getAbsolutePath());
		Intent share = new Intent(Intent.ACTION_SEND);
		share.setType("text/Message");
		share.putExtra(Intent.EXTRA_SUBJECT, social_subject);
		share.putExtra(Intent.EXTRA_TEXT, social_body);
		share.putExtra(Intent.EXTRA_STREAM, uri);
		return share;
	}
	
	/**
	 * Set the selected file as the default file.
	 * 
	 * @param selectedBuilding
	 * @param context
	 */
	public void setStandard(BuildingFile selectedBuilding) {
		
		Preferences.setDefaultFile(this.c, selectedBuilding.getFile().toString());
	}
	
	/**
	 * Remove the default file entrie.
	 * 
	 * @param context
	 * @return
	 */
	public void removeStandard(Context context) {
		
		Preferences.setDefaultFile(context, "");
	}
	
	/**
	 * Start navigation to the selected building file.
	 * 
	 * @param selectedBuilding
	 * @return
	 */
	public Intent startNavigation(BuildingFile selectedBuilding) {
		
		String lat = selectedBuilding.getLat();
		String lng = selectedBuilding.getLng();
		String uri = "geo:" + lat + "," + lng + "?z=" + RoommateConfig.MAPZOOMLVL;
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
		return intent;
	}
	
	/**
	 * Displays collected information about the
	 * selected building file.
	 * 
	 * @param selectedBuilding
	 * @param context
	 */
	public void viewBuildingInformation(BuildingFile selectedBuilding) {
		
        AlertDialog.Builder builder = new AlertDialog.Builder(this.c);
        builder.setTitle(selectedBuilding.getBuildingname());
        
        String infoText = "";
        
        if(selectedBuilding.getInfo().length() > 0) {
        	infoText = selectedBuilding.getInfo() + "\n\n";
        }
        
        infoText = infoText + this.c.getString(R.string.info_version) + ": " + selectedBuilding.getRelease() + "\n";
        
        // Format
        infoText = ((selectedBuilding.getLng().length() > 0) && (selectedBuilding.getLat().length() > 0))
        			? infoText + "\n" + this.c.getString(R.string.info_latCoordinate) + ": " + selectedBuilding.getLat() + 
        					"\n" + this.c.getString(R.string.info_lngCoordinate) + ": " + selectedBuilding.getLng() + "\n"
        			: infoText;
        infoText = (selectedBuilding.getSemester() != null)
        		   ? infoText + "\n" + this.c.getString(R.string.info_semester) + ": " + selectedBuilding.getSemester() + "\n"
        		   : infoText;
        
   		// State
        if(selectedBuilding.getState() != null && selectedBuilding.getState().length() > 0) {
        	
        	infoText = infoText + this.c.getString(R.string.info_state) + ": " + selectedBuilding.getState() + "\n";
        }
        
        // Public status
        if(selectedBuilding.getPublicStatus()) {
        	infoText = infoText + this.c.getString(R.string.info_publicbuilding) + "\n";
        }
        
        // Numbers of rooms
        if(selectedBuilding.getRoomListKeySet().size() > 0) {
        	infoText = infoText + this.c.getString(R.string.info_roomcount) + ": " + selectedBuilding.getRoomListKeySet().size();
        }
        
        // Filename
        infoText = infoText + "\n" + this.c.getString(R.string.info_filename) + ": " + selectedBuilding.getFile().getName();
        
        builder.setMessage(infoText);
        builder.create();
        builder.show();
	}
	
	/**
	 * Displays collected information about
	 * the selected room.
	 * 
	 * @param selectedRoom
	 * @param context
	 */
	public void viewRoomInformation(Room selectedRoom) {
		
        AlertDialog.Builder builder = new AlertDialog.Builder(this.c);
        builder.setTitle(selectedRoom.getName() + " " + roominfo);
        
        String infoText = "";
        
        // Infotext
        if(selectedRoom.getRoomInformation().length() != 0) {
        	infoText = selectedRoom.getRoomInformation() + "\n\n";
        }
        
        // Beamer
        if(selectedRoom.getBeamerStatus()) {
        	infoText = infoText + prop_beamer;
        }
        
        // Projector
        if(selectedRoom.getProjectorStatus()) {
        	infoText = infoText + prop_projector;
        }
        
        // Whiteboard
        if(selectedRoom.getWhiteboardStatus()) {
        	infoText = infoText + prop_whiteboard;
        }
        
        // Network
        if(selectedRoom.getNetworkStatus()) {
        	infoText = infoText + prop_network;
        }
        
        // Power supply
        if(selectedRoom.getPowerSupplyStatus()) {
        	infoText = infoText + prop_power;
        }
        
        // PC's
        if(selectedRoom.getPCPoolStatus()) {
        	infoText = infoText + prop_pcool;
        }
        
        // Exception-list
        if(!selectedRoom.getExceptionListKeys().isEmpty()) {
        	
        	Set<String> keys = selectedRoom.getExceptionListKeys();
        	Iterator<String> ite = keys.iterator();
        	
        	infoText = infoText + "\n" +exception;
        	
        	while(ite.hasNext()) {
        		
        		String date = ite.next();
        		String start = selectedRoom.getExecptionTime(date)[0];
        		String end = selectedRoom.getExecptionTime(date)[1];
        		infoText = infoText + date + ":\t" + start + " - " + end;
        	}
        }
        
        builder.setMessage(infoText);
        builder.create();
        builder.show();
	}
	 
	/**
	 * Deletes given building files from the SD-Card
	 * 
	 * @param selectedBuilding
	 * @param context
	 * @return Status der Operation
	 */
	public boolean deleteFile(BuildingFile selectedBuilding) {
		
		boolean status = false;
		String defaultFile = Preferences.getDefaultFile(this.c);
		
		// Check if the file is set as the default file
		if (defaultFile.equals(selectedBuilding.getFile().toString())) {
			status = selectedBuilding.getFile().delete();
			if (status) {
				Preferences.setDefaultFile(this.c, "");
			}
		// If not so, just delete the file
		} else {
			status = selectedBuilding.getFile().delete();
		}
		return status;
	}
}
