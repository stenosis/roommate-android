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
package roommateapp.info.entities;

/**
 * The class Lecture represents the schedulded information about 
 * a class-lecture including the lecture subject, type and the host name.
 */
public class Lecture {

	// Instance variables
	private static final String AVAILABLE = "available";
	private String lectureName = new String();
	private String type = new String();
	private String host = new String();
	private boolean isAvailable;

	/**
	 * Constructor empty
	 */
	public Lecture() {
		
		this.lectureName = null;
		this.type = null;
		this.host = null;
		this.isAvailable = true;
	}

	/**
	 * The method isAvailable returns a logical value if the lecture is set.
	 * 
	 * @return true or false
	 */
	public boolean isAvailable() {
		
		return this.isAvailable;
	}

	/**
	 * The method setAvailable sets if a lecture is set or to be empty by a
	 * logical value.
	 * 
	 * @param available
	 */
	public void setAvailable(boolean available) {
		
		this.isAvailable = available;
	}

	/**
	 * The method getSubject returns the lectures subject.
	 * 
	 * @return subject
	 */
	public String getLectureName() {
		
		return this.lectureName;
	}

	/**
	 * The method setSubject sets the lectures subject.
	 * 
	 * @param subject
	 */
	public void setLectureName(String subject) {
		
		this.lectureName = subject;
	}

	/**
	 * The method getType returns the type of the lecture, neither it is a
	 * reading, a training or whatever is set.
	 * 
	 * @return type
	 */
	public String getType() {
		
		return this.type;
	}

	/**
	 * The method setType set the type of a lecture, neither it is a reading or
	 * a training.
	 * 
	 * @param type
	 */
	public void setType(String type) {
		
		this.type = type;
	}

	/**
	 * The method getHost returns the name of the lectures host.
	 * 
	 * @return host name
	 */
	protected String getHost() {
		
		return this.host;
	}

	/**
	 * The method setHost set the host name of the current lecture.
	 * 
	 * @param host
	 */
	public void setHost(String host) {
		
		this.host = host;
	}

	/**
	 * 
	 * @return subject, type, host as String
	 */
	@Override
	public String toString() {
		
		String result = AVAILABLE;
		
		if(this.lectureName != null) {
			result = this.lectureName;
			if(this.type != null) {
				result = result + ", " + this.type;
			} if (this.host != null) {
				result = result + ", " + this.host;
			}
		}
		return result;
	}
}