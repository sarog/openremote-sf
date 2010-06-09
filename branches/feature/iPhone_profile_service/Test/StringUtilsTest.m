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

#import <SenTestingKit/SenTestingKit.h>
#import <UIKit/UIKit.h>
#import "StringUtils.h"

@interface StringUtilsTest : SenTestCase {

}

@end


@implementation StringUtilsTest


//parse file name
- (void) testParsefileNameFromString {
	NSString *name = [StringUtils parsefileNameFromString:@"doc/panel.xml"];
	STAssertTrue([@"panel.xml" isEqualToString:name], @"expected panel.xml, but %@",name);
}

//parse port
- (void) testParsePort {
	NSString *port = [StringUtils parsePortFromServerUrl:@"http://10.10.10.103:8080/HA_controller"];
	STAssertTrue([@"8080" isEqualToString:port], @"expected 8080, but %@",port);
}

- (void) testParsePortWithSlash {
	NSString *port = [StringUtils parsePortFromServerUrl:@"http://10.10.10.103:8080/HA_controller/"];
	STAssertTrue([@"8080" isEqualToString:port], @"expected 8080, but %@",port);
}

//parse host name
- (void) testParseHostNameFromServerUrl {
	NSString *name = [StringUtils parseHostNameFromServerUrl:@"http://10.10.10.103:8080/HA_controller"];
	STAssertTrue([@"10.10.10.103" isEqualToString:name], @"expected 10.10.10.103, but %@",name);
}


@end
