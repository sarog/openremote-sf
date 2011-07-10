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
#import "LayoutContainer.h"

/**
 * Store gridcell model and parsed from element grid in panel.xml.
 * XML fragment example:
 * <grid left="20" top="20" width="300" height="400" rows="2" cols="2">
 *    <cell x="0" y="0" rowspan="1" colspan="1">
 *    </cell>
 * </grid>
 */
@interface GridLayoutContainer : LayoutContainer <NSXMLParserDelegate> {
	NSMutableArray *cells;
	int rows;
	int cols;
}

@property (nonatomic, readonly) NSMutableArray *cells;
@property (nonatomic, readonly) int rows;
@property (nonatomic, readonly) int cols;

@end
