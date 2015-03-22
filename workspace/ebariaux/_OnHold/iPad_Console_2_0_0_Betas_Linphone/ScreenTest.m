//
//  ScreenTest.m
//  openremote
//
//  Created by Eric Bariaux on 20/04/11.
//  Copyright 2011 OpenRemote, Inc. All rights reserved.
//

#import "ScreenTest.h"
#import "Definition.h"

@implementation ScreenTest

- (NSString *)pathForXMLFile:(NSString *)filename {
 	NSBundle *thisBundle = [NSBundle bundleForClass:[self class]];
	return [thisBundle pathForResource:filename ofType:@"xml"];
}

- (void)testScreenIdForOrientation {
    Definition *definition = [Definition sharedDefinition];
    [definition parsePanelConfigurationFileAtPath:[self pathForXMLFile:@"panel_screenIdForOrientation"]];
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
}

@end
