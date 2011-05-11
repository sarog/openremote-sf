/* OpenRemote, the Home of the Digital Home.
 *  * Copyright 2008-2011, OpenRemote Inc-2009, OpenRemote Inc.
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

#define AUTO_DISCOVERY_SWITCH_INDEX 0 // Auto discovery boolean value is stored in the first item of appSettings.plist .
#define AUTO_DISCOVERY_URLS_INDEX   1 // Auto discovery urls are stored in the second item of appSettings.plist .
#define CUSOMIZED_URLS_INDEX        2 // Customized urls are stored in the 3rd item of appSettings.plist .
#define PANEL_IDENTITY_INDEX        3 // Selected panel indentity is stored in the 4th item of appSettings.plist .

/**
 * All setting infomations about current panel are accessed(read and write) by current class. 
 * Note: All setting infomations are stored into appSettings.plist .
 */
@interface AppSettingsDefinition : NSObject {
    
    NSArray *settingsDefinition;
    
}

@property (nonatomic, readonly) NSArray *settingsDefinition;

+ (AppSettingsDefinition *)sharedAppSettingsDefinition;

/**
 * Get specified setting informations with index from all the setting information about current panel .
 */
- (NSDictionary *)getSectionWithIndex:(int)index;

/**
 * Get header of sepecified setting information with index from all the setting information about current panel .
 */
- (NSString *)getSectionHeaderWithIndex:(int)index;

/**
 * Get footer of sepecified setting information with index from all the setting information about current panel .
 */
- (NSString *)getSectionFooterWithIndex:(int)index;

/**
 * Get the auto discovery setting information from all the setting information about current panel .
 */
- (NSDictionary *)getAutoDiscoveryDic;

@end
