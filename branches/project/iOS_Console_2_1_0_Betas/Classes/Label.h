/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2011, OpenRemote Inc.
 *
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
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
@interface Label : SensorComponent <NSXMLParserDelegate> {
	int fontSize;
	NSString *color;
	NSString *text;
}

@property(nonatomic, assign) int fontSize;
@property(nonatomic, retain) NSString *color;
@property(nonatomic, retain) NSString *text;

@end
