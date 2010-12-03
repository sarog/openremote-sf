/* OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2010, OpenRemote Inc.
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

#import "SeverDefinitionTest.h"
#import "AppSettingsDefinition.h"
#import "ServerDefinition.h"

@implementation SeverDefinitionTest

#if USE_APPLICATION_UNIT_TEST     // all code under test is in the iPhone Application

- (void) testAppDelegate {
    
    id yourApplicationDelegate = [[UIApplication sharedApplication] delegate];
    STAssertNotNil(yourApplicationDelegate, @"UIApplication failed to find the AppDelegate");
    
}

#else                           // all code under test must be linked into the Unit Test bundle

/* The setUp method is called automatically before each test-case method (load default appSettings from appSettings.plist).
 */
- (void) setUp {
	[AppSettingsDefinition reloadDataForTest];
}

/* Reset application settings after each test.
 */
- (void) tearDown {
	[AppSettingsDefinition setCurrentServerUrl:@"http://controller.openremote.org/iphone/controller"];
	[AppSettingsDefinition setUseSSL:NO];
	[AppSettingsDefinition setSslPort:8443];
}

/* Tests get secured controller URL when SSL is turned off.
 */
- (void) testSecuredServerUrlNotUseSSL {
	NSString *securedServer = [ServerDefinition securedServerUrl];
	STAssertTrue([securedServer isEqualToString:[AppSettingsDefinition getCurrentServerUrl]],nil);
}

/* Tests get secured controller URL when SSL is turned on.
 */
- (void) testSecuredServerUrlUseSSL {
	[AppSettingsDefinition setUseSSL:YES];
	[AppSettingsDefinition setSslPort:443];
	NSString *securedServer = [ServerDefinition securedServerUrl];
	STAssertTrue([securedServer isEqualToString:@"https://controller.openremote.org:443/iphone/controller"],nil);
}

/* Tests get secured controller URL when url is ip format.
 */
- (void) testSecuredServerUrlUseSSL2 {
	[AppSettingsDefinition setUseSSL:YES];
	[AppSettingsDefinition setSslPort:9999];
	[AppSettingsDefinition setCurrentServerUrl:@"http://192.168.100.113:8080/controller/"];
	NSString *securedServer = [ServerDefinition securedServerUrl];
	STAssertTrue([securedServer isEqualToString:@"https://192.168.100.113:9999/controller"],nil);
}

#endif


@end
