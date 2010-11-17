/* OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2009, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3.0 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

#import <Foundation/Foundation.h>
#import "SensorComponent.h"

/**
 * Stores text, textcolor, font size and parsed from element label in panel.xml.
 * XML fragment example:
 * <label id="59" fontSize="14" color="#AAAAAA" text="AWaiting">
 *    <link type="sensor" ref="1001">
 *       <state name="on" value="LAMP_ON" />
 *       <state name="off" value="LAMP_OFF" />
 *    </link>
 * </label>
 */
@interface Label : SensorComponent {
	int fontSize;
	NSString *color;
	NSString *text;
}

@property(nonatomic, assign) int fontSize;
@property(nonatomic, retain) NSString *color;
@property(nonatomic, retain) NSString *text;

@end
