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
#import "ScreenTest.h"
#import "Definition.h"
#import "Screen.h"
#import "PanelDefinitionParser.h"

@implementation ScreenTest

- (NSString *)pathForXMLFile:(NSString *)filename {
 	NSBundle *thisBundle = [NSBundle bundleForClass:[self class]];
	return [thisBundle pathForResource:filename ofType:@"xml"];
}

- (void)testScreenIdForOrientation {
    PanelDefinitionParser *parser = [[PanelDefinitionParser alloc] init];
    NSData *data = [[NSData alloc] initWithContentsOfFile:[self pathForXMLFile:@"panel_screenIdForOrientation"]];
    Definition *definition = [parser parseDefinitionFromXML:data];
    Screen *portraitScreen = [definition findScreenById:3];
    STAssertNotNil(portraitScreen, @"Portrait screen should exist");
    STAssertEquals(portraitScreen.screenId, 3, @"id of portrait screen should be 3");
    STAssertEquals([portraitScreen screenIdForOrientation:UIInterfaceOrientationLandscapeLeft], 3, @"Same screen (3) should be used for all orientations");
    STAssertEquals([portraitScreen screenIdForOrientation:UIInterfaceOrientationLandscapeRight], 3, @"Same screen (3) should be used for all orientations");
    STAssertEquals([portraitScreen screenIdForOrientation:UIInterfaceOrientationPortrait], 3, @"Same screen (3) should be used for all orientations");
    STAssertEquals([portraitScreen screenIdForOrientation:UIInterfaceOrientationPortraitUpsideDown], 3, @"Same screen (3) should be used for all orientations");
    
    Screen *landscapeScreen = [definition findScreenById:4];
    STAssertNotNil(landscapeScreen, @"Landscape screen should exist");
    STAssertEquals(landscapeScreen.screenId, 4, @"id of landscape screen should be 4");
    STAssertEquals([landscapeScreen screenIdForOrientation:UIInterfaceOrientationLandscapeLeft], 4, @"Same screen (4) should be used for all orientations");
    STAssertEquals([landscapeScreen screenIdForOrientation:UIInterfaceOrientationLandscapeRight], 4, @"Same screen (4) should be used for all orientations");
    STAssertEquals([landscapeScreen screenIdForOrientation:UIInterfaceOrientationPortrait], 4, @"Same screen (4) should be used for all orientations");
    STAssertEquals([landscapeScreen screenIdForOrientation:UIInterfaceOrientationPortraitUpsideDown], 4, @"Same screen (4) should be used for all orientations");

    Screen *dualScreenPortraitVersion = [definition findScreenById:5];
    STAssertNotNil(dualScreenPortraitVersion, @"Dual (P) screen should exist");
    STAssertEquals(dualScreenPortraitVersion.screenId, 5, @"id of dual (P) screen should be 5");
    STAssertEquals([dualScreenPortraitVersion screenIdForOrientation:UIInterfaceOrientationLandscapeLeft], 6, @"Landscape version (6) should be used for landscape orientations");
    STAssertEquals([dualScreenPortraitVersion screenIdForOrientation:UIInterfaceOrientationLandscapeRight], 6, @"Landscape version (6) should be used for landscape orientations");
    STAssertEquals([dualScreenPortraitVersion screenIdForOrientation:UIInterfaceOrientationPortrait], 5, @"Portrait version (5) should be used for portrait orientations");
    STAssertEquals([dualScreenPortraitVersion screenIdForOrientation:UIInterfaceOrientationPortraitUpsideDown], 5, @"Portrait version (5) should be used for portrait orientations");

    Screen *dualScreenLandscapeVersion = [definition findScreenById:6];
    STAssertNotNil(dualScreenLandscapeVersion, @"Dual (L) screen should exist");
    STAssertEquals(dualScreenLandscapeVersion.screenId, 6, @"id of dual (L) screen should be 5");
    STAssertEquals([dualScreenLandscapeVersion screenIdForOrientation:UIInterfaceOrientationLandscapeLeft], 6, @"Landscape version (6) should be used for landscape orientations");
    STAssertEquals([dualScreenLandscapeVersion screenIdForOrientation:UIInterfaceOrientationLandscapeRight], 6, @"Landscape version (6) should be used for landscape orientations");
    STAssertEquals([dualScreenLandscapeVersion screenIdForOrientation:UIInterfaceOrientationPortrait], 5, @"Portrait version (5) should be used for portrait orientations");
    STAssertEquals([dualScreenLandscapeVersion screenIdForOrientation:UIInterfaceOrientationPortraitUpsideDown], 5, @"Portrait version (5) should be used for portrait orientations");
    [data release];
    [parser release];
}

@end
