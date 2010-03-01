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
#import "ServerAutoDiscoveryController.h"

#define AUTO_DISCOVERY_SWITCH_INDEX 0
#define AUTO_DISCOVERY_URLS_INDEX 1
#define CUSOMIZED_URLS_INDEX 2
#define PANEL_IDENTITY_INDEX 3

@interface AppSettingsDefinition : NSObject {

}
+ (void)reloadData;
+ (NSMutableArray *)getAppSettings;
+(NSMutableDictionary *)getSectionWithIndex:(int)index;
+ (NSString *)getSectionHeaderWithIndex:(int)index;
+ (NSString *)getSectionFooterWithIndex:(int)index;
+ (NSMutableDictionary *)getAutoDiscoveryDic;
+ (BOOL)isAutoDiscoveryEnable;
+ (void)setAutoDiscovery:(BOOL)on;
+ (NSMutableArray *)getAutoServers;
+ (NSMutableArray *)getCustomServers;
+ (NSString *)getCurrentServerUrl;
+ (BOOL)readServerUrlFromFile;
+ (void)setCurrentServerUrl:(NSString *)url;
+ (void)addAutoServer:(NSDictionary *)server;
+ (void)writeToFile;
+ (void)removeAllAutoServer;
+ (void)writeToFile;

+ (NSMutableDictionary *)getPanelIdentityDic;
+ (NSString *)getCurrentPanelIdentity;
+ (NSString *)getUnsavedChosenServerUrl;
+ (void)setUnsavedChosenServerUrl:(NSString *)url;


@end
