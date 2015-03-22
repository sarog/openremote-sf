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


@implementation ServerDefinition

+ (NSString *)serverUrl {
	static NSString *serverUrl;
	serverUrl = [AppSettingsDefinition getCurrentServerUrl];
	return  serverUrl;
}

//use HTTPS, SSL port
+ (NSString *)applyHttpsAndSslPort:(NSString *)serverUrl {
	//NSString * url = [serverUrl copy];
	if (![AppSettingsDefinition useSSL]) {
		NSString *urlStr = [serverUrl copy];
		return urlStr;
	}
	
	NSURL *url = [NSURL URLWithString:serverUrl];

	if ([url scheme] == nil) {
		// TODO: should propagate malformed URL up to user, requires API modification.
		return nil;
	}
	
	NSString *host = [url host];
	NSString *path = [url path];

	NSString *securePort = [NSString stringWithFormat:@":%d",[AppSettingsDefinition sslPort]];
	
	[url initWithScheme:@"https" host:[host stringByAppendingString:securePort] path:path];

	return [url absoluteString];
}

+ (NSString *)securedServerUrl {
	return  [self applyHttpsAndSslPort:[AppSettingsDefinition getCurrentServerUrl]];
}

+ (NSString *)panelXmlRESTUrl {
	NSString *panelUrl = [NSString stringWithFormat:@"rest/panel/%@",[AppSettingsDefinition getCurrentPanelIdentity]];
	NSString *panelXmlUrl = [[self securedOrRawServerUrl] stringByAppendingPathComponent:panelUrl];
	return panelXmlUrl;
}

//Round-Robin (failover) servers
+ (NSString *)serversXmlRESTUrl {
	NSString *serversXmlUrl = [[self securedOrRawServerUrl] stringByAppendingPathComponent:@"rest/servers"];
	return serversXmlUrl;
}

+ (NSString *)imageUrl {
	return [[self securedOrRawServerUrl] stringByAppendingPathComponent:@"resources"];
}

+ (NSString *)controlRESTUrl {
	return [[self securedOrRawServerUrl] stringByAppendingPathComponent:@"rest/control"];
}

//returns serverUrl, if SSL is enabled, use secured server url.
+ (NSString *)securedOrRawServerUrl {
	return [AppSettingsDefinition useSSL] ? [self securedServerUrl] : [self serverUrl];
}

+ (NSString *)statusRESTUrl {
	return [[self securedOrRawServerUrl] stringByAppendingPathComponent:@"rest/status"];	
}

+ (NSString *)pollingRESTUrl {
	return [[self securedOrRawServerUrl] stringByAppendingPathComponent:@"rest/polling"];
}

+ (NSString *)logoutUrl {
	return [[self securedOrRawServerUrl] stringByAppendingPathComponent:@"logout"];
}

+ (NSString *)panelsRESTUrl {
	NSString *url = [AppSettingsDefinition getUnsavedChosenServerUrl];
	url = [AppSettingsDefinition useSSL] ? [self applyHttpsAndSslPort:url] : url;
	return [url stringByAppendingPathComponent:@"rest/panels"];
}

+ (NSString *)hostName {
	return [StringUtils parseHostNameFromServerUrl:[self serverUrl]];
}

@end
