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

#import "ServerDefinition.h"
#import "AppSettingsDefinition.h"
#import "StringUtils.h"

#define SECURITY_PORT 8443

@implementation ServerDefinition

+ (NSString *)serverUrl {
	static NSString *serverUrl;
	serverUrl = [AppSettingsDefinition getCurrentServerUrl];
	return  serverUrl;
}

//HTTPS
+ (NSString *)securedServerUrl {
	static NSString *serverUrl;
	serverUrl = [AppSettingsDefinition getCurrentServerUrl];
	serverUrl = [serverUrl stringByReplacingOccurrencesOfString:@"http" withString:@"https"];
	
	NSString *port = [StringUtils parsePortFromServerUrl:serverUrl];
	serverUrl = [serverUrl stringByReplacingOccurrencesOfString:port withString:[NSString stringWithFormat:@"%d", SECURITY_PORT]];
	
	return  serverUrl;
}

+ (NSString *)panelXmlRESTUrl {
	NSString *panelUrl = [NSString stringWithFormat:@"rest/panel/%@",[AppSettingsDefinition getCurrentPanelIdentity]];
	NSString *panelXmlUrl = [[self serverUrl] stringByAppendingPathComponent:panelUrl];
	return panelXmlUrl;
}

+ (NSString *)panelXmlUrl {
	NSString *panelUrl = @"resources/panel.xml";
	NSString *panelXmlUrl = [[self serverUrl] stringByAppendingPathComponent:panelUrl];
	return panelXmlUrl;
}

+ (NSString *)serversXmlRESTUrl {
	NSString *serversXmlUrl = [[self serverUrl] stringByAppendingPathComponent:@"rest/servers"];
	return serversXmlUrl;
}

+ (NSString *)imageUrl {
	return [[self serverUrl] stringByAppendingPathComponent:@"resources"];
}

+ (NSString *)controlRESTUrl {
	return [[self serverUrl] stringByAppendingPathComponent:@"rest/control"];
}

+ (NSString *)securedControlRESTUrl {
	return [[self securedServerUrl] stringByAppendingPathComponent:@"rest/control"];
}

+ (NSString *)statusRESTUrl {
	return [[self serverUrl] stringByAppendingPathComponent:@"rest/status"];	
}

+ (NSString *)pollingRESTUrl {
	return [[self serverUrl] stringByAppendingPathComponent:@"rest/polling"];
}

+ (NSString *)logoutUrl {
	return [[self serverUrl] stringByAppendingPathComponent:@"logout"];
}

+ (NSString *)panelsRESTUrl {
	NSLog([AppSettingsDefinition getUnsavedChosenServerUrl]);
	return [[AppSettingsDefinition getUnsavedChosenServerUrl] stringByAppendingPathComponent:@"rest/panels"];
}

+ (NSString *)hostName {
	return [StringUtils parseHostNameFromServerUrl:[self serverUrl]];
}

@end
