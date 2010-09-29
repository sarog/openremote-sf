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
#import "Label.h"

/**
 * Stores image src and label model and parsed from element image in panel.xml.
 * XML fragment example:
 * <image id="60"  src = "b.png" style="">
 *    <link type="sensor" ref="1001">
 *       <state name="on" value="on.png" />
 *       <state name="off" value="off.png" />
 *    </link>
 *    <include type="label" ref="64" />
 * </image>
 */
@interface Image : SensorComponent {
	NSString *src;
	NSString *style;
	Label *label;
}

@property (nonatomic, readwrite, retain) NSString *src;
@property (nonatomic, readwrite, retain) NSString *style;
@property (nonatomic, readwrite, retain) Label *label;

@end
