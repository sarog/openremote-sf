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


#import "StringUtils.h"


@implementation StringUtils


+ (NSString *)parsefileNameFromString:(NSString *)str {
	if ([[str pathComponents] count] > 0) {
		NSString *url =  [[str pathComponents] objectAtIndex:[[str pathComponents] count] - 1];
		return [[url retain] autorelease];
	}
	return nil;
}

+ (NSString *)parsePortFromServerUrl:(NSString *)serverUrl {
	NSRange colonStrRange = [serverUrl rangeOfString:@":" options:NSBackwardsSearch];
	NSString *appnameAndPortStr = [serverUrl substringFromIndex:colonStrRange.location + 1];
	NSRange slashRange = [appnameAndPortStr rangeOfString:@"/" options:NSBackwardsSearch];
	return [appnameAndPortStr substringToIndex:slashRange.location];
}

+ (NSString *)parseHostNameFromServerUrl:(NSString *)serverUrl {
	NSRange protocolStrRange = [serverUrl rangeOfString:@"/"];
	NSString *hostAndPortAndAppNameStr = [serverUrl substringFromIndex:protocolStrRange.location + 2];
	NSRange colonStrRange = [hostAndPortAndAppNameStr rangeOfString:@":" options:NSBackwardsSearch];
	return [hostAndPortAndAppNameStr substringToIndex:colonStrRange.location];
}

@end
