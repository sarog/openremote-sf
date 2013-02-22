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

#import "AppSettingsDefinitionTest.h"
#import "AppSettingsDefinition.h"

@implementation AppSettingsDefinitionTest

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

/* Test get default server "http://controller.openremote.org/iphone/controller".
 */
- (void) testGetDefaultServer {
	NSString *currentController = [AppSettingsDefinition getCurrentServerUrl];
	STAssertTrue([currentController isEqualToString:@"http://controller.openremote.org/iphone/controller"],@"but %@", currentController);
}

/* Test basic set/get Controller URL.
 */
- (void) testSetCurrentServer {
	[AppSettingsDefinition setCurrentServerUrl:@"localhost"];
	NSString *currentController = [AppSettingsDefinition getCurrentServerUrl];
	STAssertTrue([currentController isEqualToString:@"localhost"],@"but %@", currentController);
}

/* Test setting controller URL to nil value, should return an empty string.
 */
- (void) testSetCurrentServerNil {
	[AppSettingsDefinition setCurrentServerUrl:nil];
	NSString *controller = [AppSettingsDefinition getCurrentServerUrl];
	STAssertTrue([controller isEqualToString:@""],@"but %@", controller);
}

/* Test setting controller URL to empty value, should return an empty string.
 */
-(void) testSetCurrentServerEmpty {
	[AppSettingsDefinition setCurrentServerUrl:@""];
	NSString *controller = [AppSettingsDefinition getCurrentServerUrl];
	STAssertTrue([controller isEqualToString:@""],@"but %@", controller);
}

/* As we don't enforece URL in the API, any string value will do.
 * This should eventually go away with API evolution/fix.
 */
-(void) testSetCurrentServerBadDesign {
	[AppSettingsDefinition setCurrentServerUrl:@"any value will do"];
	NSString *controller = [AppSettingsDefinition getCurrentServerUrl];
	STAssertTrue([controller isEqualToString:@"any value will do"],@"but %@", controller);
	//STFail(@"App settings don't validate URLs");
}

/* Basic SSL enable on/off test.
 */
-(void) testUseSSL {
	[AppSettingsDefinition setUseSSL:YES];
	STAssertTrue([AppSettingsDefinition useSSL], nil);
	[AppSettingsDefinition setUseSSL:NO];
	STAssertFalse([AppSettingsDefinition useSSL], nil);
}

/* Test setSslPort edge cases.
 */
-(void) testSetSSLPortEdgeCases {
	[AppSettingsDefinition setSslPort:0];
	STAssertEquals([AppSettingsDefinition sslPort], 0, nil);
	
	[AppSettingsDefinition setSslPort:65535];
	STAssertEquals([AppSettingsDefinition sslPort], 65535, nil);
	
	[AppSettingsDefinition setSslPort:8443];
	
	[AppSettingsDefinition setSslPort:-100];
	STAssertEquals([AppSettingsDefinition sslPort], 8443, nil);
	
	[AppSettingsDefinition setSslPort:65536];
	STAssertEquals([AppSettingsDefinition sslPort], 8443, nil);
}

/* Test get default panel, the default panel is "None".
 */
- (void) testGetDefaultPanel {
    NSString *panelIdentity = [AppSettingsDefinition getCurrentPanelIdentity];
	STAssertTrue([panelIdentity isEqualToString:@"None"],@"but %@", panelIdentity);
}


#endif


@end
