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
#import <UIKit/UIKit.h>
#import "Screen.h"
#import "Group.h"
#import "Tabbar.h"

#define TWICE 2 // Constant of parsing panel.xml .

@class LocalLogic;

/**
 * This class is responsible for downloading, parsing panel data and storing some models data(groups, screens, labels and tabBar)
 */
@interface Definition : NSObject <NSXMLParserDelegate> {		
	BOOL isUpdating;
	NSDate *lastUpdateTime;
	NSMutableArray *groups;
	NSMutableArray *screens;
	NSMutableArray *labels;
	TabBar *tabBar;
	LocalLogic *localLogic;
	NSMutableArray *imageNames;
	NSInvocationOperation *updateOperation;
	NSOperationQueue *updateOperationQueue; 
	UILabel *loading;
}

/**
 * Get Definition singleton instance.
 */
+ (Definition *)sharedDefinition;

/**
 * Parses the XML panel configuration file at the provided path and populates the receiver with the parsed configuration.
 *
 * @param NSString * full path of the XML file containing the panel configuration to parse
 */
- (void)parsePanelConfigurationFileAtPath:(NSString *)configurationFilePath;

/**
 * Download and parse panel data.
 */
- (void)update;

/**
 * Check the downloaded data is ready.
 */
- (BOOL)isDataReady;

/**
 * Use local cache in handset side.
 */
- (void)useLocalCacheDirectly;

/**
 * Clear stored models data(groups, screens, labels and tabBar).
 */
- (void)clearPanelXMLData;

/**
 * Add image name to a array for downloading images into image cache.
 */
- (void)addImageName:(NSString *)imageName;

/**
 * Get a group instance with group id.
 */
- (Group *)findGroupById:(int)groupId;

/**
 * Get a screen instance with screen id.
 */
- (Screen *)findScreenById:(int)screenId;

/**
 * Add a group instance for caching.
 */
- (void)addGroup:(Group *)group;

/**
 * Add a screen instance for caching.
 */
- (void)addScreen:(Screen *)screen;

/**
 * Add a label instance for caching.
 */
- (void) addLabel:(Label *)label;

/**
 * Get a label instance with lable id.
 */
- (Label *)findLabelById:(int)labelId;

@property (nonatomic,readonly) BOOL isUpdating;
@property (nonatomic,readonly) NSDate *lastUpdateTime;
@property (nonatomic,readonly) NSMutableArray *groups;
@property (nonatomic,readonly) NSMutableArray *screens;
@property (nonatomic,retain) NSMutableArray *labels;
@property (nonatomic,retain) TabBar *tabBar;
@property (nonatomic, readonly) LocalLogic *localLogic;
@property (nonatomic,readonly) NSMutableArray *imageNames;
@property (nonatomic,retain) UILabel *loading;

@end
