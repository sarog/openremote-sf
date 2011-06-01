//
//  ButtonTest.m
//  openremote
//
//  Created by Eric Bariaux on 24/05/11.
//  Copyright 2011 OpenRemote, Inc. All rights reserved.
//

#import "ButtonTest.h"
#import "Definition.h"
#import "Button.h"
#import "LayoutContainer.h"
#import "AbsoluteLayoutContainer.h"
#import "Button.h"

@implementation ButtonTest

- (NSString *)pathForXMLFile:(NSString *)filename {
 	NSBundle *thisBundle = [NSBundle bundleForClass:[self class]];
	return [thisBundle pathForResource:filename ofType:@"xml"];
}

- (void)testButtonsAttribute {
    Definition *definition = [Definition sharedDefinition];
    [definition parsePanelConfigurationFileAtPath:[self pathForXMLFile:@"panel_button"]];
    for (Screen *screen in definition.screens) {
        for (LayoutContainer *layout in screen.layouts) {
            Button *button = (Button *)(((AbsoluteLayoutContainer *)layout).component);
            switch (button.componentId) {
                case 10:
                    STAssertTrue(button.hasPressCommand, @"Button is defined with press command");
                    STAssertTrue(button.hasShortReleaseCommand, @"Button is defined with short release command");
                    STAssertTrue(button.hasLongPressCommand, @"Button is defined with long press command");
                    STAssertTrue(button.hasLongReleaseCommand, @"Button is defined with long release command");
                    STAssertFalse(button.repeat, @"Default value for repeat is false if attribute not defined");
                    STAssertEquals(button.longPressDelay, (NSUInteger)250, @"Default value for long press delay is 250ms if attribute not defined");
                    break;
                case 11:
                    STAssertTrue(button.hasPressCommand, @"Button is defined with press command");
                    STAssertTrue(button.hasShortReleaseCommand, @"Button is defined with short release command");
                    STAssertTrue(button.hasLongPressCommand, @"Button is defined with long press command");
                    STAssertTrue(button.hasLongReleaseCommand, @"Button is defined with long release command");
                    STAssertFalse(button.repeat, @"Repeat should be false when long press or long release command is defined");
                    STAssertEquals(button.longPressDelay, (NSUInteger)250, @"Default value for long press delay is 250ms if attribute not defined");
                    break;
                case 12:
                    STAssertTrue(button.hasPressCommand, @"Button is defined with press command");
                    STAssertTrue(button.hasShortReleaseCommand, @"Button is defined with short release command");
                    STAssertFalse(button.hasLongPressCommand, @"Button is defined with no long press command");
                    STAssertFalse(button.hasLongReleaseCommand, @"Button is defined with no long release command");
                    STAssertTrue(button.repeat, @"Button is defined with repeat");
                    STAssertEquals(button.repeatDelay, (NSUInteger)100, @"Default value for repeat delay is 100ms if attribute not defined");
                    break;
                case 13:
                    STAssertTrue(button.hasPressCommand, @"Button is defined with press command");
                    STAssertTrue(button.hasShortReleaseCommand, @"Button is defined with short release command");
                    STAssertFalse(button.hasLongPressCommand, @"Button is defined with no long press command");
                    STAssertFalse(button.hasLongReleaseCommand, @"Button is defined with no long release command");
                    STAssertTrue(button.repeat, @"Button is defined with repeat");
                    STAssertEquals(button.repeatDelay, (NSUInteger)500, @"Value for repeat delay is specified at 500ms");
                    break;
                case 14:
                    STAssertTrue(button.hasPressCommand, @"Button is defined with press command");
                    STAssertTrue(button.hasShortReleaseCommand, @"Button is defined with short release command");
                    STAssertFalse(button.hasLongPressCommand, @"Button is defined with no long press command");
                    STAssertFalse(button.hasLongReleaseCommand, @"Button is defined with no long release command");
                    STAssertTrue(button.repeat, @"Button is defined with repeat");
                    STAssertEquals(button.repeatDelay, (NSUInteger)100, @"Value for repeat delay is minimum 100ms if smaller value is defined");
                    break;
                case 15:
                    STAssertTrue(button.hasPressCommand, @"Button is defined with press command");
                    STAssertFalse(button.hasShortReleaseCommand, @"Button is defined with no short release command");
                    STAssertFalse(button.hasLongPressCommand, @"Button is defined with no long press command");
                    STAssertTrue(button.hasLongReleaseCommand, @"Button is defined with long release command");
                    STAssertFalse(button.repeat, @"Default value for repeat is false if attribute not defined");
                    STAssertEquals(button.longPressDelay, (NSUInteger)1234, @"Value for long press delay is specified at 1234ms");
                    break;
                case 16:
                    STAssertTrue(button.hasPressCommand, @"Button is defined with press command");
                    STAssertFalse(button.hasShortReleaseCommand, @"Button is defined with no short release command");
                    STAssertFalse(button.hasLongPressCommand, @"Button is defined with no long press command");
                    STAssertTrue(button.hasLongReleaseCommand, @"Button is defined with long release command");
                    STAssertFalse(button.repeat, @"Default value for repeat is false if attribute not defined");
                    STAssertEquals(button.longPressDelay, (NSUInteger)250, @"Value for long press delay is minimum 250 ms if smaller value is defined");
                    break;
                default:
                    break;
            }
        }
    }
}

@end
