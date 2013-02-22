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

#define AUTO_DISCOVERY_SWITCH_INDEX 0 // Auto discovery boolean value is stored in the first item of appSettings.plist .
#define AUTO_DISCOVERY_URLS_INDEX   1 // Auto discovery urls are stored in the second item of appSettings.plist .
#define CUSOMIZED_URLS_INDEX        2 // Customized urls are stored in the 3rd item of appSettings.plist .
#define PANEL_IDENTITY_INDEX        3 // Selected panel indentity is stored in the 4th item of appSettings.plist .
#define SECURITY_INDEX              5 // Security settings are stored in the 6th item of appSettings.plist .

#define DEFAULT_SSL_PORT			-1   // Default ssl port like "https://org.openremote/conroller"
#define DEFAULT_TOMCAT_SSL_PORT		8443 // Default tomcat ssl port, the current default configuration use it.
#define DEFAULT_HTTPD_SSL_PORT		443  // Default ssl port for HTTPD.

/**
 * All setting infomations about current panel are accessed(read and write) by current class. 
 * Note: All setting infomations are stored into appSettings.plist .
 */
@interface AppSettingsDefinition : NSObject {
}

/**
 * Reload setting data from file appSettings.plist for refreshing setting data in memmory .
 */
+ (void)reloadData;

+ (void)reloadDataForTest;
/**
 * Get all the setting information about current panel .
 */
+ (NSMutableArray *)getAppSettings;

/**
 * Get specified setting informations with index from all the setting information about current panel .
 */
+(NSMutableDictionary *)getSectionWithIndex:(int)index;

/**
 * Get header of sepecified setting information with index from all the setting information about current panel .
 */
+ (NSString *)getSectionHeaderWithIndex:(int)index;

/**
 * Get footer of sepecified setting information with index from all the setting information about current panel .
 */
+ (NSString *)getSectionFooterWithIndex:(int)index;

/**
 * Get the auto discovery setting information from all the setting information about current panel .
 */
+ (NSMutableDictionary *)getAutoDiscoveryDic;

/**
 * Check if the function of auto discovery is enabled.
 * So if this is enabled, panel client can discovery controller server automatically.
 */
+ (BOOL)isAutoDiscoveryEnable;

/**
 * Enable the function of auto discovery.
 */
+ (void)setAutoDiscovery:(BOOL)on;

/**
 * Get servers by auto discovery stored in panel client.
 */
+ (NSMutableArray *)getAutoServers;

/**
 * Get servers by users input stored in panel client.
 */
+ (NSMutableArray *)getCustomServers;

/**
 * Get current server's url panel client use.
 */
+ (NSString *)getCurrentServerUrl;

/**
 * This is for refresh current server controller's url to latest one.
 */
+ (BOOL)readServerUrlFromFile;

/**
 * Change the current server's url to the specified url.
 */
+ (void)setCurrentServerUrl:(NSString *)url;

/**
 * Add a server info into auto servers array.
 */
+ (void)addAutoServer:(NSDictionary *)server;

/**
 * Synchronizes settings data into appSettings.plist .
 */
+ (void)writeToFile;

/**
 * Remove all auto servers from settings data .
 */
+ (void)removeAllAutoServer;

/**
 * Get panel identity section infomation from appSettings.plist .
 */
+ (NSMutableDictionary *)getPanelIdentityDic;

/**
 * Get panel identify current panel client use.
 */
+ (NSString *)getCurrentPanelIdentity;

/**
 * Get the chosen server url user select but unsaved into appSettings.plist .
 */
+ (NSString *)getUnsavedChosenServerUrl;

/**
 * Set the speccified url as choosen url of user.
 */
+ (void)setUnsavedChosenServerUrl:(NSString *)url;

/**
 * Get the security setting infomation from appSettings.plist .
 */
+ (NSMutableDictionary *)getSecurityDic;

/**
 * Check if panel client access controller server with SSL way.
 */
+ (BOOL)useSSL;

/**
 * Get security port controller server provide.
 */
+ (int)sslPort;

/**
 * Enable or disable security way to access controller server.
 * If parameter is YES, security way is enabled.
 * If parameter is NO, security way is disabled.
 */
+ (void)setUseSSL:(BOOL)on;

/**
 * Set security port to specified value.
 */
+ (void)setSslPort:(int)port;


@end
