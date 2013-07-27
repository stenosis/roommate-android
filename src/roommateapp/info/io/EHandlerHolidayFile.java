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
package roommateapp.info.io;

/* imports */
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import roommateapp.info.droid.RoommateConfig;

/**
 * Error Handler of the Holiday file parser.
 */
public class EHandlerHolidayFile implements ErrorHandler {

	public void error(SAXParseException exception) throws SAXException {
		if (RoommateConfig.VERBOSE) {
			System.out.println("ERROR: Holiday file parsing error");
		}
	}

	public void fatalError(SAXParseException exception) throws SAXException {
		if (RoommateConfig.VERBOSE) {
			System.out.println("ERROR: Holiday file parsing FATAL error");
		}
	}

	public void warning(SAXParseException exception) throws SAXException {
		if (RoommateConfig.VERBOSE) {
			System.out.println("ERROR: Holiday file parsing warning");
		}
	}
}