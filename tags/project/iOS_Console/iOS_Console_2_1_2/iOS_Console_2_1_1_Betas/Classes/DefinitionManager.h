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

@interface DefinitionManager : NSObject {
    BOOL isUpdating;
	NSDate *lastUpdateTime;
	NSInvocationOperation *updateOperation;
	NSOperationQueue *updateOperationQueue; 
	UILabel *loading;

}

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

@property (nonatomic,readonly) BOOL isUpdating;
@property (nonatomic,readonly) NSDate *lastUpdateTime;
@property (nonatomic,retain) UILabel *loading;

@end
