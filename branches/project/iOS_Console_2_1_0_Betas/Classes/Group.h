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
#import "XMLEntity.h"
#import "TabBar.h"
#import "Screen.h"

/**
 * Stores screens model data and parsed from element group in panel.xml.
 * XML fragment example:
 * <group id="27" name="Bedroom">
 *    <include type="screen" ref="30" />
 *    <include type="screen" ref="45" />
 * </group>
 */
@interface Group : XMLEntity <NSXMLParserDelegate> {
	
	int groupId;
	NSString *name;
	NSMutableArray *screens;
	TabBar *tabBar;
	
}

@property (nonatomic,readonly) int groupId;
@property (nonatomic,readonly) NSString *name;
@property (nonatomic,readonly) NSMutableArray *screens;
@property (nonatomic,readonly) TabBar *tabBar;

/**
 * Get all screens whose orientation is portrait.
 */
- (NSArray *) getPortraitScreens;

/**
 * Get all screens whose orientation is landscape.
 */
- (NSArray *) getLandscapeScreens;

/**
 * Find screen model in specified orientation screens of group containing by screen id.
 */
- (BOOL)canFindScreenById:(int)screenId inOrientation:(BOOL)isLandscape;

/**
 * Find screen model by screen id. returns nil if not found.
 */
- (Screen *) findScreenByScreenId:(int)screenId;

@end
