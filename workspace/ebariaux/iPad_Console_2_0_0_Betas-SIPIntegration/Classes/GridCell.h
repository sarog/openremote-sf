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
#import "Component.h"
#import "XMLEntity.h"

/**
 * Store model data of components and parsed from element cell in panel.xml.
 * XML fragment example:
 * <grid left="20" top="20" width="300" height="400" rows="2" cols="2">
 *    <cell x="0" y="0" rowspan="1" colspan="1">
 *    </cell>
 * </grid>
 */
@interface GridCell : XMLEntity <NSXMLParserDelegate> {
	int x;
	int y;
	int rowspan;
	int colspan;
	Component *component;
	
}

@property (nonatomic, readonly) int x;
@property (nonatomic, readonly) int y;
@property (nonatomic, readonly) int rowspan;
@property (nonatomic, readonly) int colspan;
@property (nonatomic, readonly) Component *component;

@end
