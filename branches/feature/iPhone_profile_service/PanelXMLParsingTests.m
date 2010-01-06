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

#import "PanelXMLParsingTests.h"
#import "Group.h"
#import "Screen.h"
#import "Control.h"
#import "LayoutContainer.h"
#import "AbsoluteLayoutContainer.h"
#import "Switch.h"
#import "GridLayoutContainer.h"
#import "GridCell.h"
#import "Button.h"
#import "Monitor.h"
#import "Slider.h"
#import "Label.h"
#import "Image.h"
#import "Gesture.h"
#import "XMLEntity.h"
#import "Tabbar.h"
#import "TabbarItem.h"

@implementation PanelXMLParsingTests

#if USE_APPLICATION_UNIT_TEST     // all code under test is in the iPhone Application

- (void) testAppDelegate {
    
    id yourApplicationDelegate = [[UIApplication sharedApplication] delegate];
    STAssertNotNil(yourApplicationDelegate, @"UIApplication failed to find the AppDelegate");
    
}

#else                           // all code under test must be linked into the Unit Test bundle
@synthesize definition;

- (NSData *) readFile:(NSString *) fileName {
	NSData *data = [[NSData alloc] initWithContentsOfFile:fileName];
	NSString *content = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
	NSLog(content);
	[content release];
	return data;
}

// panel_grid_button.xml test
- (void) testParsePanelGridButtonXML {
	[[Definition sharedDefinition] clearPanelXMLData];
	NSLog(@"testParsePanelGridButtonXML ");
	NSData *xml = [self readFile:@"panel_grid_button.xml"];
	
	NSXMLParser *xmlParser = [[NSXMLParser alloc] initWithData:xml];
	[xmlParser setDelegate:self];
	[xmlParser parse];
	NSMutableArray *groups = [[Definition sharedDefinition] groups];
	NSMutableArray *screens = [[Definition sharedDefinition] screens];
	int image_index = 0;
	int but_index = 0;
	NSMutableArray *cells = [[NSMutableArray alloc] init];
	NSMutableArray *buts = [[NSMutableArray alloc] init];
	for (Group *group in groups) {
		NSLog(@"group %@ has %d screen", group.name,group.screens.count);
		for (Screen *screen in group.screens) {			
			NSLog(@"screen %@ has %d layout", screen.name, screen.layouts.count);
			for (LayoutContainer *layout in screen.layouts) {
				if([layout isKindOfClass:[GridLayoutContainer class]]){					
					NSLog(@"layout is grid ");
					GridLayoutContainer *grid =(GridLayoutContainer *)layout;
					NSString *layoutAttrs = [[NSMutableString alloc] initWithFormat:@"%d %d %d %d",grid.left,grid.top,grid.width,grid.height];
					NSString *expectedAttrs = @"20 20 300 400";
					STAssertTrue([expectedAttrs isEqualToString:layoutAttrs],@"expected %@, but %@",expectedAttrs,layoutAttrs);
					[layoutAttrs release];
					
					for (GridCell *cell in grid.cells) {			
						[cells addObject:cell];
						if ([cell.component isKindOfClass:[Button class]]) {
							Button * but = (Button *)cell.component;
							[buts addObject:but];
							NSString *expectedName = [[NSMutableString alloc] initWithFormat:@"%c",(char)65 + but_index];						
							STAssertTrue([but.name isEqualToString:expectedName],@"expected %@, but %@",expectedName,but.name);
							int expectedId = (59 + but_index++);
							STAssertTrue(expectedId == but.componentId,@"expected %d, but %d",expectedId,but.componentId);
							NSString *expectedDefaultIconName = nil;
							if (but.defaultIcon) {
								expectedDefaultIconName = [[NSMutableString alloc] initWithFormat:@"%c.png",(char)97 + image_index++];						
								STAssertTrue([but.defaultIcon.src isEqualToString:expectedDefaultIconName],@"expected %@, but %@",expectedDefaultIconName,but.defaultIcon.src);
							}
							NSString *expectedPressedIconName = nil;
							if (but.pressedIcon) {
								expectedPressedIconName = [[NSMutableString alloc] initWithFormat:@"%c.png",(char)97 + image_index++];
								STAssertTrue([but.pressedIcon.src isEqualToString:expectedPressedIconName],@"expected %@, but %@",expectedPressedIconName,but.pressedIcon.src);
							}
							
							[expectedDefaultIconName release];
							[expectedPressedIconName release];
						}	
					}
				}				
			}
		}
	}
	
	NSLog(@"groups count = %d",[groups count]);
	NSLog(@"screens count = %d",[screens count]);
	NSLog(@"xml parse done");
	
	NSMutableArray *screenNames = [NSMutableArray arrayWithObjects:@"basement",@"floor",nil];
	NSMutableArray *groupNames = [NSMutableArray arrayWithObjects:@"All rooms",@"living room",nil];
	
	//check screens
	for (int i=0;i<screenNames.count;i++) {
		STAssertTrue([[screenNames objectAtIndex:i] isEqualToString:[[screens objectAtIndex:i] name]],@"expected %@, but %@",[screenNames objectAtIndex:i],[[screens objectAtIndex:i] name]);
		STAssertTrue(i+5 == [[screens objectAtIndex:i] screenId],@"expected %d, but %d",i+5,[[screens objectAtIndex:i] screenId]);
	}
	
	//check groups
	for (int i=0;i<groupNames.count;i++) {
		STAssertTrue([[groupNames objectAtIndex:i] isEqualToString:[[groups objectAtIndex:i] name]],@"expected %@, but %@",[groupNames objectAtIndex:i],[[groups objectAtIndex:i] name]);
		STAssertTrue(i+1 == [[groups objectAtIndex:i] groupId],@"expected %d, but %d",i+1,[[groups objectAtIndex:i] groupId]);
	}
	
	STAssertTrue(cells.count== 11,@"expected %d, but %d",11,cells.count);
	STAssertTrue(((GridCell *)[cells objectAtIndex:0]).colspan == 1,@"expected %d",1);
	STAssertTrue(((GridCell *)[cells objectAtIndex:0]).rowspan == 1,@"expected %d",1);
	STAssertTrue(((GridCell *)[cells objectAtIndex:1]).rowspan == 1,@"expected %d",1);
	STAssertTrue(((GridCell *)[cells objectAtIndex:2]).colspan == 1,@"expected %d",1);
	STAssertTrue(((GridCell *)[cells objectAtIndex:3]).colspan == 1,@"expected %d",1);
	STAssertTrue(((GridCell *)[cells objectAtIndex:4]).colspan == 2,@"expected %d",2);
	Screen *screen1 = (Screen *)[screens objectAtIndex:0];
	NSString *ids = [[screen1 pollingComponentsIds] componentsJoinedByString:@","];
	STAssertTrue([@"" isEqualToString:ids],@"expected '', but %@",ids);
	
	
	STAssertTrue(buts.count== 11,@"expected %d, but %d",11,buts.count);
	STAssertTrue(((Button *)[buts objectAtIndex:0]).navigate.toScreen == 19,@"expected %d",19);
	STAssertTrue(((Button *)[buts objectAtIndex:0]).hasCommand == NO,@"expected NO");
	STAssertTrue(((Button *)[buts objectAtIndex:1]).hasCommand == YES,@"expected YES");
	STAssertTrue(((Button *)[buts objectAtIndex:1]).navigate == nil,@"expected nil");
	STAssertTrue(((Button *)[buts objectAtIndex:2]).hasCommand == NO,@"expected NO");
	STAssertTrue(((Button *)[buts objectAtIndex:2]).navigate.toScreen == 29,@"expected %d",29);
	STAssertTrue(((Button *)[buts objectAtIndex:3]).hasCommand == NO,@"expected %d",NO);
	STAssertTrue(((Button *)[buts objectAtIndex:3]).navigate.toGroup == 9,@"expected %d",9);
	STAssertTrue(((Button *)[buts objectAtIndex:4]).hasCommand == NO,@"expected %d",NO);
	STAssertTrue(((Button *)[buts objectAtIndex:4]).navigate.toGroup == 9,@"expected %d",9);
	STAssertTrue(((Button *)[buts objectAtIndex:5]).hasCommand == NO,@"expected %d",NO);
	STAssertTrue(((Button *)[buts objectAtIndex:5]).navigate.isPreviousScreen == YES,@"expected %d",YES);
	STAssertTrue(((Button *)[buts objectAtIndex:6]).hasCommand == NO,@"expected %d",NO);
	STAssertTrue(((Button *)[buts objectAtIndex:6]).navigate.isNextScreen == YES,@"expected %d",YES);
	STAssertTrue(((Button *)[buts objectAtIndex:7]).navigate.isSetting == YES,@"expected %d",YES);
	STAssertTrue(((Button *)[buts objectAtIndex:8]).navigate.isBack == YES,@"expected %d",YES);
	STAssertTrue(((Button *)[buts objectAtIndex:9]).navigate.isLogin == YES,@"expected %d",YES);
	STAssertTrue(((Button *)[buts objectAtIndex:10]).navigate.isLogout == YES,@"expected %d",YES);
	
	[xmlParser release];
	[xml release];
	[cells release];
}



// panel_grid_switch.xml test
- (void) testParsePanelGridSwitchXML {
	[[Definition sharedDefinition] clearPanelXMLData];
	NSLog(@"testParsePanelGridSwitchXML ");
	NSData *xml = [self readFile:@"panel_grid_switch.xml"];
	
	NSXMLParser *xmlParser = [[NSXMLParser alloc] initWithData:xml];
	[xmlParser setDelegate:self];
	[xmlParser parse];
	NSMutableArray *groups = [[Definition sharedDefinition] groups];
	NSMutableArray *screens = [[Definition sharedDefinition] screens];
	int state_index = 0;
	int switch_index = 0;
	NSMutableArray *cells = [[NSMutableArray alloc] init];
	for (Group *group in groups) {
		NSLog(@"group %@ has %d screen", group.name,group.screens.count);
		for (Screen *screen in group.screens) {			
			NSLog(@"screen %@ has %d layout", screen.name, screen.layouts.count);
			for (LayoutContainer *layout in screen.layouts) {
				if([layout isKindOfClass:[GridLayoutContainer class]]){					
					NSLog(@"layout is grid ");
					GridLayoutContainer *grid =(GridLayoutContainer *)layout;
					NSString *layoutAttrs = [[NSMutableString alloc] initWithFormat:@"%d %d %d %d",grid.left,grid.top,grid.width,grid.height];
					NSString *expectedAttrs = @"20 20 300 400";
					STAssertTrue([expectedAttrs isEqualToString:layoutAttrs],@"expected %@, but %@",expectedAttrs,layoutAttrs);
					[layoutAttrs release];
					
					for (GridCell *cell in grid.cells) {			
						[cells addObject:cell];
						if ([cell.component isKindOfClass:[Switch class]]) {
							Switch *theSwitch = (Switch *)cell.component;
							int expectedId = (59 + switch_index++);
							STAssertTrue(expectedId == theSwitch.componentId,@"expected %d, but %d",expectedId,theSwitch.componentId);	
							NSString *expectedOnName = [[NSMutableString alloc] initWithFormat:@"%c.png",(char)97 + state_index++];						
							STAssertTrue([theSwitch.onImage.src isEqualToString:expectedOnName],@"expected %@, but %@",expectedOnName,theSwitch.onImage.src);
							NSString *expectedOffName = [[NSMutableString alloc] initWithFormat:@"%c.png",(char)97 + state_index++];
							STAssertTrue([theSwitch.offImage.src isEqualToString:expectedOffName],@"expected %@, but %@",expectedOffName,theSwitch.offImage.src);
							[expectedOnName release];
							[expectedOffName release];
						}	
					}
				}				
			}
		}
	}
	
	NSLog(@"groups count = %d",[groups count]);
	NSLog(@"screens count = %d",[screens count]);
	NSLog(@"xml parse done");
	
	NSMutableArray *screenNames = [NSMutableArray arrayWithObjects:@"basement",@"floor",nil];
	NSMutableArray *groupNames = [NSMutableArray arrayWithObjects:@"All rooms",@"living room",nil];
	
	//check screens
	for (int i=0;i<screenNames.count;i++) {
		STAssertTrue([[screenNames objectAtIndex:i] isEqualToString:[[screens objectAtIndex:i] name]],@"expected %@, but %@",[screenNames objectAtIndex:i],[[screens objectAtIndex:i] name]);
		STAssertTrue(i+5 == [[screens objectAtIndex:i] screenId],@"expected %d, but %d",i+5,[[screens objectAtIndex:i] screenId]);
	}
	
	//check groups
	for (int i=0;i<groupNames.count;i++) {
		STAssertTrue([[groupNames objectAtIndex:i] isEqualToString:[[groups objectAtIndex:i] name]],@"expected %@, but %@",[groupNames objectAtIndex:i],[[groups objectAtIndex:i] name]);
		STAssertTrue(i+1 == [[groups objectAtIndex:i] groupId],@"expected %d, but %d",i+1,[[groups objectAtIndex:i] groupId]);
	}
	
	STAssertTrue(cells.count== 5,@"expected %d, but %d",5,cells.count);
	STAssertTrue(((GridCell *)[cells objectAtIndex:0]).colspan == 1,@"expected %d",1);
	STAssertTrue(((GridCell *)[cells objectAtIndex:0]).rowspan == 1,@"expected %d",1);
	STAssertTrue(((GridCell *)[cells objectAtIndex:1]).rowspan == 1,@"expected %d",1);
	STAssertTrue(((GridCell *)[cells objectAtIndex:2]).colspan == 1,@"expected %d",1);
	STAssertTrue(((GridCell *)[cells objectAtIndex:3]).colspan == 1,@"expected %d",1);
	STAssertTrue(((GridCell *)[cells objectAtIndex:4]).colspan == 2,@"expected %d",2);
	Screen *screen1 = (Screen *)[screens objectAtIndex:0];
	NSString *ids = [[screen1 pollingComponentsIds] componentsJoinedByString:@","];
	STAssertTrue([@"59,60,61,62" isEqualToString:ids],@"expected 59,60,61,62, but %@",ids);
	
	[xmlParser release];
	[xml release];
	[cells release];
}


// panel_absolute_switch.xml test
- (void) testParsePanelAbsoluteSwitchXML {
	[[Definition sharedDefinition] clearPanelXMLData];
	NSLog(@"testParsePanelAbsoluteSwitchXML ");
	NSData *xml = [self readFile:@"panel_absolute_switch.xml"];
	
	NSXMLParser *xmlParser = [[NSXMLParser alloc] initWithData:xml];
	[xmlParser setDelegate:self];
	[xmlParser parse];
	NSMutableArray *groups = [[Definition sharedDefinition] groups];
	NSMutableArray *screens = [[Definition sharedDefinition] screens];
	int state_index = 0;
	int switch_index = 0;
	for (Group *group in groups) {
		NSLog(@"group %@ has %d screen", group.name,group.screens.count);
		for (Screen *screen in group.screens) {			
			NSLog(@"screen %@ has %d layout", screen.name, screen.layouts.count);
			for (LayoutContainer *layout in screen.layouts) {
				if([layout isKindOfClass:[AbsoluteLayoutContainer class]]){					
					NSLog(@"layout is absolute ");
					AbsoluteLayoutContainer *abso =(AbsoluteLayoutContainer *)layout;
					NSString *layoutAttrs = [[NSMutableString alloc] initWithFormat:@"%d %d %d %d",abso.left,abso.top,abso.width,abso.height];
					NSString *expectedAttrs = @"20 320 100 100";
					STAssertTrue([expectedAttrs isEqualToString:layoutAttrs],@"expected %@, but %@",expectedAttrs,layoutAttrs);
					[layoutAttrs release];
					
					if ([abso.component isKindOfClass:[Switch class]]) {
						Switch *theSwitch = (Switch *)abso.component;
						int expectedId = (59 + switch_index++);
						STAssertTrue(expectedId == theSwitch.componentId,@"expected %d, but %d",expectedId,theSwitch.componentId);	
						NSString *expectedOnName = [[NSMutableString alloc] initWithFormat:@"%c.png",(char)97 + state_index++];						
						STAssertTrue([theSwitch.onImage.src isEqualToString:expectedOnName],@"expected %@, but %@",expectedOnName,theSwitch.onImage.src);
						NSString *expectedOffName = [[NSMutableString alloc] initWithFormat:@"%c.png",(char)97 + state_index++];
						STAssertTrue([theSwitch.offImage.src isEqualToString:expectedOffName],@"expected %@, but %@",expectedOffName,theSwitch.offImage.src);
						[expectedOnName release];
						[expectedOffName release];
					}					
				}				
			}
		}
	}
	
	NSLog(@"groups count = %d",[groups count]);
	NSLog(@"screens count = %d",[screens count]);
	NSLog(@"xml parse done");
	
	NSMutableArray *screenNames = [NSMutableArray arrayWithObjects:@"basement",@"floor",nil];
	NSMutableArray *groupNames = [NSMutableArray arrayWithObjects:@"All rooms",@"living room",nil];
	
	//check screens
	for (int i=0;i<screenNames.count;i++) {
		STAssertTrue([[screenNames objectAtIndex:i] isEqualToString:[[screens objectAtIndex:i] name]],@"expected %@, but %@",[screenNames objectAtIndex:i],[[screens objectAtIndex:i] name]);
		STAssertTrue(i+5 == [[screens objectAtIndex:i] screenId],@"expected %d, but %d",i+5,[[screens objectAtIndex:i] screenId]);
	}
	
	//check groups
	for (int i=0;i<groupNames.count;i++) {
		STAssertTrue([[groupNames objectAtIndex:i] isEqualToString:[[groups objectAtIndex:i] name]],@"expected %@, but %@",[groupNames objectAtIndex:i],[[groups objectAtIndex:i] name]);
		STAssertTrue(i+1 == [[groups objectAtIndex:i] groupId],@"expected %d, but %d",i+1,[[groups objectAtIndex:i] groupId]);
	}
	Screen *screen1 = (Screen *)[screens objectAtIndex:0];
	NSString *ids = [[screen1 pollingComponentsIds] componentsJoinedByString:@","];
	STAssertTrue([@"59,60" isEqualToString:ids],@"expected 59,60 but %@",ids);
	
	[xmlParser release];
	[xml release];

}


// panel_grid_slider.xml test
- (void) testParsePanelGridSliderXML {
	NSLog(@"Begin testParsePanelGridSliderXML");
	[[Definition sharedDefinition] clearPanelXMLData];
	NSData *xml = [self readFile:@"panel_grid_slider.xml"];
	
	NSXMLParser *xmlParser = [[NSXMLParser alloc] initWithData:xml];
	[xmlParser setDelegate:self];
	[xmlParser parse];
	NSMutableArray *groups = [[Definition sharedDefinition] groups];
	NSMutableArray *screens = [[Definition sharedDefinition] screens];
	int slider_index = 0;
	NSMutableArray *cells = [[NSMutableArray alloc] init];
	for (Group *group in groups) {
		NSLog(@"group %@ has %d screen", group.name,group.screens.count);
		for (Screen *screen in group.screens) {			
			NSLog(@"screen %@ has %d layout", screen.name, screen.layouts.count);
			for (LayoutContainer *layout in screen.layouts) {
				if([layout isKindOfClass:[GridLayoutContainer class]]){					
					NSLog(@"layout is grid ");
					GridLayoutContainer *grid =(GridLayoutContainer *)layout;
					NSString *layoutAttrs = [[NSMutableString alloc] initWithFormat:@"%d %d %d %d",grid.left,grid.top,grid.width,grid.height];
					NSString *expectedAttrs = @"20 20 300 400";
					STAssertTrue([expectedAttrs isEqualToString:layoutAttrs],@"expected %@, but %@",expectedAttrs,layoutAttrs);
					[layoutAttrs release];
					
					for (GridCell *cell in grid.cells) {			
						[cells addObject:cell];
						if ([cell.component isKindOfClass:[Slider class]]) {
							Slider *theSlider = (Slider *)cell.component;
							int expectedId = (59 + slider_index++);
							STAssertTrue(expectedId == theSlider.componentId,@"expected %d, but %d",expectedId,theSlider.componentId);
							float maxValue = 100.0f;						
							STAssertTrue(theSlider.maxValue == maxValue,@"expected %f, but %f", maxValue, theSlider.maxValue);
							float minValue = 0.0f;
							STAssertTrue(theSlider.minValue == minValue,@"expected %f, but %f", minValue, theSlider.minValue);
						}	
					}
				}				
			}
		}
	}
	
	NSLog(@"groups count = %d",[groups count]);
	NSLog(@"screens count = %d",[screens count]);
	NSLog(@"xml parse done");
	
	NSMutableArray *screenNames = [NSMutableArray arrayWithObjects:@"basement",@"floor",nil];
	NSMutableArray *groupNames = [NSMutableArray arrayWithObjects:@"All rooms",@"living room",nil];
	
	//check screens
	for (int i=0;i<screenNames.count;i++) {
		STAssertTrue([[screenNames objectAtIndex:i] isEqualToString:[[screens objectAtIndex:i] name]],@"expected %@, but %@",[screenNames objectAtIndex:i],[[screens objectAtIndex:i] name]);
		STAssertTrue(i+5 == [[screens objectAtIndex:i] screenId],@"expected %d, but %d",i+5,[[screens objectAtIndex:i] screenId]);
	}
	
	//check groups
	for (int i=0;i<groupNames.count;i++) {
		STAssertTrue([[groupNames objectAtIndex:i] isEqualToString:[[groups objectAtIndex:i] name]],@"expected %@, but %@",[groupNames objectAtIndex:i],[[groups objectAtIndex:i] name]);
		STAssertTrue(i+1 == [[groups objectAtIndex:i] groupId],@"expected %d, but %d",i+1,[[groups objectAtIndex:i] groupId]);
	}
	
	STAssertTrue(cells.count== 5,@"expected %d, but %d",5,cells.count);
	STAssertTrue(((GridCell *)[cells objectAtIndex:0]).colspan == 1,@"expected %d",1);
	STAssertTrue(((GridCell *)[cells objectAtIndex:0]).rowspan == 1,@"expected %d",1);
	STAssertTrue(((GridCell *)[cells objectAtIndex:1]).rowspan == 1,@"expected %d",1);
	STAssertTrue(((GridCell *)[cells objectAtIndex:2]).colspan == 1,@"expected %d",1);
	STAssertTrue(((GridCell *)[cells objectAtIndex:3]).colspan == 1,@"expected %d",1);
	STAssertTrue(((GridCell *)[cells objectAtIndex:4]).colspan == 2,@"expected %d",2);
	Screen *screen1 = (Screen *)[screens objectAtIndex:0];
	NSString *ids = [[screen1 pollingComponentsIds] componentsJoinedByString:@","];
	STAssertTrue([@"59,60,61,62" isEqualToString:ids],@"expected 59,60,61,62, but %@",ids);
	
	[xmlParser release];
	[xml release];
	[cells release];
	NSLog(@"End testParsePanelGridSliderXML");
}

// panel_absolute_slider.xml test
- (void) testParsePanelAbsoluteSliderXML {
	NSLog(@"Begin testParsePanelAbsoluteSliderXML");
	[[Definition sharedDefinition] clearPanelXMLData];
	NSData *xml = [self readFile:@"panel_absolute_slider.xml"];
	
	NSXMLParser *xmlParser = [[NSXMLParser alloc] initWithData:xml];
	[xmlParser setDelegate:self];
	[xmlParser parse];
	NSMutableArray *groups = [[Definition sharedDefinition] groups];
	NSMutableArray *screens = [[Definition sharedDefinition] screens];
	int slider_index = 0;
	for (Group *group in groups) {
		NSLog(@"group %@ has %d screen", group.name,group.screens.count);
		for (Screen *screen in group.screens) {			
			NSLog(@"screen %@ has %d layout", screen.name, screen.layouts.count);
			for (LayoutContainer *layout in screen.layouts) {
				if([layout isKindOfClass:[AbsoluteLayoutContainer class]]){					
					NSLog(@"layout is absolute ");
					AbsoluteLayoutContainer *abso =(AbsoluteLayoutContainer *)layout;
					NSString *layoutAttrs = [[NSMutableString alloc] initWithFormat:@"%d %d %d %d",abso.left,abso.top,abso.width,abso.height];
					NSString *expectedAttrs = @"20 320 100 100";
					STAssertTrue([expectedAttrs isEqualToString:layoutAttrs],@"expected %@, but %@",expectedAttrs,layoutAttrs);
					[layoutAttrs release];
					
					if ([abso.component isKindOfClass:[Slider class]]) {
						Slider *theSlider = (Slider *)abso.component;
						int expectedId = (59 + slider_index++);
						STAssertTrue(expectedId == theSlider.componentId,@"expected %d, but %d",expectedId,theSlider.componentId);
						float maxValue = 100.0f;						
						STAssertTrue(theSlider.maxValue == maxValue,@"expected %f, but %f", maxValue, theSlider.maxValue);
						float minValue = 0.0f;
						STAssertTrue(theSlider.minValue == minValue,@"expected %f, but %f", minValue, theSlider.minValue);
					}					
				}				
			}
		}
	}
	
	NSLog(@"groups count = %d",[groups count]);
	NSLog(@"screens count = %d",[screens count]);
	NSLog(@"xml parse done");
	
	NSMutableArray *screenNames = [NSMutableArray arrayWithObjects:@"basement",@"floor",nil];
	NSMutableArray *groupNames = [NSMutableArray arrayWithObjects:@"All rooms",@"living room",nil];
	
	//check screens
	for (int i=0;i<screenNames.count;i++) {
		STAssertTrue([[screenNames objectAtIndex:i] isEqualToString:[[screens objectAtIndex:i] name]],@"expected %@, but %@",[screenNames objectAtIndex:i],[[screens objectAtIndex:i] name]);
		STAssertTrue(i+5 == [[screens objectAtIndex:i] screenId],@"expected %d, but %d",i+5,[[screens objectAtIndex:i] screenId]);
	}
	
	//check groups
	for (int i=0;i<groupNames.count;i++) {
		STAssertTrue([[groupNames objectAtIndex:i] isEqualToString:[[groups objectAtIndex:i] name]],@"expected %@, but %@",[groupNames objectAtIndex:i],[[groups objectAtIndex:i] name]);
		STAssertTrue(i+1 == [[groups objectAtIndex:i] groupId],@"expected %d, but %d",i+1,[[groups objectAtIndex:i] groupId]);
	}
	Screen *screen1 = (Screen *)[screens objectAtIndex:0];
	NSString *ids = [[screen1 pollingComponentsIds] componentsJoinedByString:@","];
	STAssertTrue([@"59,60" isEqualToString:ids],@"expected 59,60 but %@",ids);
	
	[xmlParser release];
	[xml release];
	NSLog(@"End testParsePanelAbsoluteSliderXML");
}

// panel_grid_label.xml test
- (void) testParsePanelGridLabelXML {
	NSLog(@"Begin testParsePanelGridLabelXML");
	[[Definition sharedDefinition] clearPanelXMLData];
	NSData *xml = [self readFile:@"panel_grid_label.xml"];
	
	NSXMLParser *xmlParser = [[NSXMLParser alloc] initWithData:xml];
	[xmlParser setDelegate:self];
	[xmlParser parse];
	NSMutableArray *groups = [[Definition sharedDefinition] groups];
	NSMutableArray *screens = [[Definition sharedDefinition] screens];
	int label_index = 0;
	int state_index = 0;
	NSMutableArray *cells = [[NSMutableArray alloc] init];
	for (Group *group in groups) {
		NSLog(@"group %@ has %d screen", group.name,group.screens.count);
		for (Screen *screen in group.screens) {			
			NSLog(@"screen %@ has %d layout", screen.name, screen.layouts.count);
			for (LayoutContainer *layout in screen.layouts) {
				if([layout isKindOfClass:[GridLayoutContainer class]]){					
					NSLog(@"layout is grid ");
					GridLayoutContainer *grid =(GridLayoutContainer *)layout;
					NSString *layoutAttrs = [[NSMutableString alloc] initWithFormat:@"%d %d %d %d",grid.left,grid.top,grid.width,grid.height];
					NSString *expectedAttrs = @"20 20 300 400";
					STAssertTrue([expectedAttrs isEqualToString:layoutAttrs],@"expected %@, but %@",expectedAttrs,layoutAttrs);
					[layoutAttrs release];
					
					for (GridCell *cell in grid.cells) {			
						[cells addObject:cell];
						if ([cell.component isKindOfClass:[Label class]]) {
							Label *theLabel = (Label *)cell.component;
							int expectedId = (59 + label_index++);
							STAssertTrue(expectedId == theLabel.componentId,@"expected %d, but %d",expectedId,theLabel.componentId);
							NSString *labelValue = [[NSString alloc] initWithFormat:@"%c", (char)65 + state_index++];					
							STAssertTrue([theLabel.value isEqualToString:labelValue],@"expected %@, but %@", labelValue, theLabel.value);
						}	
					}
				}				
			}
		}
	}
	
	NSLog(@"groups count = %d",[groups count]);
	NSLog(@"screens count = %d",[screens count]);
	NSLog(@"xml parse done");
	
	NSMutableArray *screenNames = [NSMutableArray arrayWithObjects:@"basement",@"floor",nil];
	NSMutableArray *groupNames = [NSMutableArray arrayWithObjects:@"All rooms",@"living room",nil];
	
	//check screens
	for (int i=0;i<screenNames.count;i++) {
		STAssertTrue([[screenNames objectAtIndex:i] isEqualToString:[[screens objectAtIndex:i] name]],@"expected %@, but %@",[screenNames objectAtIndex:i],[[screens objectAtIndex:i] name]);
		STAssertTrue(i+5 == [[screens objectAtIndex:i] screenId],@"expected %d, but %d",i+5,[[screens objectAtIndex:i] screenId]);
	}
	
	//check groups
	for (int i=0;i<groupNames.count;i++) {
		STAssertTrue([[groupNames objectAtIndex:i] isEqualToString:[[groups objectAtIndex:i] name]],@"expected %@, but %@",[groupNames objectAtIndex:i],[[groups objectAtIndex:i] name]);
		STAssertTrue(i+1 == [[groups objectAtIndex:i] groupId],@"expected %d, but %d",i+1,[[groups objectAtIndex:i] groupId]);
	}
	
	STAssertTrue(cells.count== 5,@"expected %d, but %d",5,cells.count);
	STAssertTrue(((GridCell *)[cells objectAtIndex:0]).colspan == 1,@"expected %d",1);
	STAssertTrue(((GridCell *)[cells objectAtIndex:0]).rowspan == 1,@"expected %d",1);
	STAssertTrue(((GridCell *)[cells objectAtIndex:1]).rowspan == 1,@"expected %d",1);
	STAssertTrue(((GridCell *)[cells objectAtIndex:2]).colspan == 1,@"expected %d",1);
	STAssertTrue(((GridCell *)[cells objectAtIndex:3]).colspan == 1,@"expected %d",1);
	STAssertTrue(((GridCell *)[cells objectAtIndex:4]).colspan == 2,@"expected %d",2);
	
	[xmlParser release];
	[xml release];
	[cells release];
	NSLog(@"End testParsePanelGridLabelXML");
}

// panel_absolute_label.xml test
- (void) testParsePanelAbsoluteLabelXML {
	NSLog(@"Begin testParsePanelAbsoluteLabelXML");
	[[Definition sharedDefinition] clearPanelXMLData];
	NSData *xml = [self readFile:@"panel_absolute_label.xml"];
	
	NSXMLParser *xmlParser = [[NSXMLParser alloc] initWithData:xml];
	[xmlParser setDelegate:self];
	[xmlParser parse];
	NSMutableArray *groups = [[Definition sharedDefinition] groups];
	NSMutableArray *screens = [[Definition sharedDefinition] screens];
	int label_index = 0;
	int state_index = 0;
	for (Group *group in groups) {
		NSLog(@"group %@ has %d screen", group.name,group.screens.count);
		for (Screen *screen in group.screens) {			
			NSLog(@"screen %@ has %d layout", screen.name, screen.layouts.count);
			for (LayoutContainer *layout in screen.layouts) {
				if([layout isKindOfClass:[AbsoluteLayoutContainer class]]){					
					NSLog(@"layout is absolute ");
					AbsoluteLayoutContainer *abso =(AbsoluteLayoutContainer *)layout;
					NSString *layoutAttrs = [[NSMutableString alloc] initWithFormat:@"%d %d %d %d",abso.left,abso.top,abso.width,abso.height];
					NSString *expectedAttrs = @"20 320 100 100";
					STAssertTrue([expectedAttrs isEqualToString:layoutAttrs],@"expected %@, but %@",expectedAttrs,layoutAttrs);
					[layoutAttrs release];
					
					if ([abso.component isKindOfClass:[Label class]]) {
						Label *theLabel= (Label *)abso.component;
						int expectedId = (59 + label_index++);
						STAssertTrue(expectedId == theLabel.componentId,@"expected %d, but %d",expectedId,theLabel.componentId);
						NSString *labelValue = [[NSString alloc] initWithFormat:@"%c", (char)65 + state_index++];
						STAssertTrue([theLabel.value isEqualToString:labelValue],@"expected %@, but %@", labelValue, theLabel.value);
					}					
				}				
			}
		}
	}
	
	NSLog(@"groups count = %d",[groups count]);
	NSLog(@"screens count = %d",[screens count]);
	NSLog(@"xml parse done");
	
	NSMutableArray *screenNames = [NSMutableArray arrayWithObjects:@"basement",@"floor",nil];
	NSMutableArray *groupNames = [NSMutableArray arrayWithObjects:@"All rooms",@"living room",nil];
	
	//check screens
	for (int i=0;i<screenNames.count;i++) {
		STAssertTrue([[screenNames objectAtIndex:i] isEqualToString:[[screens objectAtIndex:i] name]],@"expected %@, but %@",[screenNames objectAtIndex:i],[[screens objectAtIndex:i] name]);
		STAssertTrue(i+5 == [[screens objectAtIndex:i] screenId],@"expected %d, but %d",i+5,[[screens objectAtIndex:i] screenId]);
	}
	
	//check groups
	for (int i=0;i<groupNames.count;i++) {
		STAssertTrue([[groupNames objectAtIndex:i] isEqualToString:[[groups objectAtIndex:i] name]],@"expected %@, but %@",[groupNames objectAtIndex:i],[[groups objectAtIndex:i] name]);
		STAssertTrue(i+1 == [[groups objectAtIndex:i] groupId],@"expected %d, but %d",i+1,[[groups objectAtIndex:i] groupId]);
	}
	
	[xmlParser release];
	[xml release];
	NSLog(@"End testParsePanelAbsoluteLabelXML");
}

// panel_grid_image.xml test
- (void) testParsePanelGridImageXML {
	NSLog(@"Begin testParsePanelGridImageXML");
	[[Definition sharedDefinition] clearPanelXMLData];
	NSData *xml = [self readFile:@"panel_grid_image.xml"];
	
	NSXMLParser *xmlParser = [[NSXMLParser alloc] initWithData:xml];
	[xmlParser setDelegate:self];
	[xmlParser parse];
	NSMutableArray *groups = [[Definition sharedDefinition] groups];
	NSMutableArray *screens = [[Definition sharedDefinition] screens];
	int image_index = 0;
	int state_index = 0;
	NSMutableArray *cells = [[NSMutableArray alloc] init];
	for (Group *group in groups) {
		NSLog(@"group %@ has %d screen", group.name,group.screens.count);
		for (Screen *screen in group.screens) {			
			NSLog(@"screen %@ has %d layout", screen.name, screen.layouts.count);
			for (LayoutContainer *layout in screen.layouts) {
				if([layout isKindOfClass:[GridLayoutContainer class]]){					
					NSLog(@"layout is grid ");
					GridLayoutContainer *grid =(GridLayoutContainer *)layout;
					NSString *layoutAttrs = [[NSMutableString alloc] initWithFormat:@"%d %d %d %d",grid.left,grid.top,grid.width,grid.height];
					NSString *expectedAttrs = @"20 20 300 400";
					STAssertTrue([expectedAttrs isEqualToString:layoutAttrs],@"expected %@, but %@",expectedAttrs,layoutAttrs);
					[layoutAttrs release];
					
					for (GridCell *cell in grid.cells) {			
						[cells addObject:cell];
						if ([cell.component isKindOfClass:[Image class]]) {
							Image *theImage = (Image *)cell.component;
							int expectedId = (59 + image_index++);
							STAssertTrue(expectedId == theImage.componentId,@"expected %d, but %d",expectedId,theImage.componentId);
							NSString *imageSrc = [[NSString alloc] initWithFormat:@"%c.png", (char)97 + state_index++];					
							STAssertTrue([theImage.src isEqualToString:imageSrc],@"expected %@, but %@", imageSrc, theImage.src);
						}	
					}
				}				
			}
		}
	}
	
	NSLog(@"groups count = %d",[groups count]);
	NSLog(@"screens count = %d",[screens count]);
	NSLog(@"xml parse done");
	
	NSMutableArray *screenNames = [NSMutableArray arrayWithObjects:@"basement",@"floor",nil];
	NSMutableArray *groupNames = [NSMutableArray arrayWithObjects:@"All rooms",@"living room",nil];
	
	//check screens
	for (int i=0;i<screenNames.count;i++) {
		STAssertTrue([[screenNames objectAtIndex:i] isEqualToString:[[screens objectAtIndex:i] name]],@"expected %@, but %@",[screenNames objectAtIndex:i],[[screens objectAtIndex:i] name]);
		STAssertTrue(i+5 == [[screens objectAtIndex:i] screenId],@"expected %d, but %d",i+5,[[screens objectAtIndex:i] screenId]);
	}
	
	//check groups
	for (int i=0;i<groupNames.count;i++) {
		STAssertTrue([[groupNames objectAtIndex:i] isEqualToString:[[groups objectAtIndex:i] name]],@"expected %@, but %@",[groupNames objectAtIndex:i],[[groups objectAtIndex:i] name]);
		STAssertTrue(i+1 == [[groups objectAtIndex:i] groupId],@"expected %d, but %d",i+1,[[groups objectAtIndex:i] groupId]);
	}
	
	STAssertTrue(cells.count== 5,@"expected %d, but %d",5,cells.count);
	STAssertTrue(((GridCell *)[cells objectAtIndex:0]).colspan == 1,@"expected %d",1);
	STAssertTrue(((GridCell *)[cells objectAtIndex:0]).rowspan == 1,@"expected %d",1);
	STAssertTrue(((GridCell *)[cells objectAtIndex:1]).rowspan == 1,@"expected %d",1);
	STAssertTrue(((GridCell *)[cells objectAtIndex:2]).colspan == 1,@"expected %d",1);
	STAssertTrue(((GridCell *)[cells objectAtIndex:3]).colspan == 1,@"expected %d",1);
	STAssertTrue(((GridCell *)[cells objectAtIndex:4]).colspan == 2,@"expected %d",2);
	
	[xmlParser release];
	[xml release];
	[cells release];
	NSLog(@"End testParsePanelGridImageXML");
}
// panel_absolute_image.xml test
- (void) testParsePanelAbsoluteImageXML {
	NSLog(@"Begin testParsePanelAbsoluteImageXML");
	[[Definition sharedDefinition] clearPanelXMLData];
	NSData *xml = [self readFile:@"panel_absolute_image.xml"];
	
	NSXMLParser *xmlParser = [[NSXMLParser alloc] initWithData:xml];
	[xmlParser setDelegate:self];
	[xmlParser parse];
	NSMutableArray *groups = [[Definition sharedDefinition] groups];
	NSMutableArray *screens = [[Definition sharedDefinition] screens];
	int image_index = 0;
	int state_index = 0;
	for (Group *group in groups) {
		NSLog(@"group %@ has %d screen", group.name,group.screens.count);
		for (Screen *screen in group.screens) {			
			NSLog(@"screen %@ has %d layout", screen.name, screen.layouts.count);
			for (LayoutContainer *layout in screen.layouts) {
				if([layout isKindOfClass:[AbsoluteLayoutContainer class]]){					
					NSLog(@"layout is absolute ");
					AbsoluteLayoutContainer *abso =(AbsoluteLayoutContainer *)layout;
					NSString *layoutAttrs = [[NSMutableString alloc] initWithFormat:@"%d %d %d %d",abso.left,abso.top,abso.width,abso.height];
					NSString *expectedAttrs = @"20 320 100 100";
					STAssertTrue([expectedAttrs isEqualToString:layoutAttrs],@"expected %@, but %@",expectedAttrs,layoutAttrs);
					[layoutAttrs release];
					
					if ([abso.component isKindOfClass:[Image class]]) {
						Image *theImage= (Image *)abso.component;
						int expectedId = (59 + image_index++);
						STAssertTrue(expectedId == theImage.componentId,@"expected %d, but %d",expectedId,theImage.componentId);
						NSString *imageSrc = [[NSString alloc] initWithFormat:@"%c.png", (char)97 + state_index++];
						STAssertTrue([theImage.src isEqualToString:imageSrc],@"expected %@, but %@", theImage.src, imageSrc);
					}					
				}				
			}
		}
	}
	
	NSLog(@"groups count = %d",[groups count]);
	NSLog(@"screens count = %d",[screens count]);
	NSLog(@"xml parse done");
	
	NSMutableArray *screenNames = [NSMutableArray arrayWithObjects:@"basement",@"floor",nil];
	NSMutableArray *groupNames = [NSMutableArray arrayWithObjects:@"All rooms",@"living room",nil];
	
	//check screens
	for (int i=0;i<screenNames.count;i++) {
		STAssertTrue([[screenNames objectAtIndex:i] isEqualToString:[[screens objectAtIndex:i] name]],@"expected %@, but %@",[screenNames objectAtIndex:i],[[screens objectAtIndex:i] name]);
		STAssertTrue(i+5 == [[screens objectAtIndex:i] screenId],@"expected %d, but %d",i+5,[[screens objectAtIndex:i] screenId]);
	}
	
	//check groups
	for (int i=0;i<groupNames.count;i++) {
		STAssertTrue([[groupNames objectAtIndex:i] isEqualToString:[[groups objectAtIndex:i] name]],@"expected %@, but %@",[groupNames objectAtIndex:i],[[groups objectAtIndex:i] name]);
		STAssertTrue(i+1 == [[groups objectAtIndex:i] groupId],@"expected %d, but %d",i+1,[[groups objectAtIndex:i] groupId]);
	}
	
	[xmlParser release];
	[xml release];
	NSLog(@"End testParsePanelAbsoluteImageXML");
}

// panel_absolute_screen_backgroundimage.xml test
- (void) testParsePanelAbsoluteScreenBackgroundimageXML {
	NSLog(@"Begin testParsePanelAbsoluteScreenBackgroundimageXML");
	[[Definition sharedDefinition] clearPanelXMLData];
	NSData *xml = [self readFile:@"panel_absolute_screen_backgroundimage.xml"];
	
	NSXMLParser *xmlParser = [[NSXMLParser alloc] initWithData:xml];
	[xmlParser setDelegate:self];
	[xmlParser parse];
	NSMutableArray *groups = [[Definition sharedDefinition] groups];
	NSMutableArray *screens = [[Definition sharedDefinition] screens];
	int background_index = 1;
	int image_index = 0;
	int state_index = 0;
	for (Group *group in groups) {
		NSLog(@"group %@ has %d screen", group.name,group.screens.count);
		for (Screen *screen in group.screens) {
			
			NSLog(@"Begin test background of screen %@", [screen name]);
			// absolute position
			STAssertTrue([[screen background] isBackgroundImageAbsolutePosition], @"expected %d, but %d", YES, [[screen background] isBackgroundImageAbsolutePosition]);
			NSLog(@"isBackgroundImageAbsolutePosition of screen background is %d", [[screen background] isBackgroundImageAbsolutePosition]);
			
			int backgroundImageLeft = [[screen background] backgroundImageAbsolutePositionLeft];
			int backgroundImageTop = [[screen background] backgroundImageAbsolutePositionTop];
			int expectedBackgroundImageLeft = 100*background_index;
			int expectedBackgroundImageTop = 100*background_index;
			STAssertTrue(backgroundImageLeft == expectedBackgroundImageLeft, @"expected %d, but %d", expectedBackgroundImageLeft, backgroundImageLeft);
			STAssertTrue(backgroundImageTop == expectedBackgroundImageTop, @"expected %d, but %d", expectedBackgroundImageTop, backgroundImageTop);
			NSLog(@"absolute position of background image is: %d,%d", backgroundImageLeft, backgroundImageTop);
			
			// fillscreen
			BOOL fillScreen = [[screen background] fillScreen];
			BOOL expectedFillScreen = NO;
			STAssertTrue(fillScreen == expectedFillScreen, @"expected %d, but %d", expectedFillScreen, fillScreen);
			NSLog(@"fillScreen of background image is %d", fillScreen);
			
			// background image src
			NSString *backgroundImageSrc = [[[screen background] backgroundImage] src];
			NSString *expectedBackgroundImageSrc = [[NSString alloc] initWithFormat:@"basement%d.png", background_index];
			STAssertTrue([expectedBackgroundImageSrc isEqualToString:backgroundImageSrc], @"expected %@, but %@", expectedBackgroundImageSrc, backgroundImageSrc);
			NSLog(@"background image src of background is %@", backgroundImageSrc);
			
			NSLog(@"End test background of screen %@", [screen name]);			
			background_index++;
			
			NSLog(@"screen %@ has %d layout", screen.name, screen.layouts.count);
			for (LayoutContainer *layout in screen.layouts) {
				if([layout isKindOfClass:[AbsoluteLayoutContainer class]]){					
					NSLog(@"layout is absolute ");
					AbsoluteLayoutContainer *abso =(AbsoluteLayoutContainer *)layout;
					NSString *layoutAttrs = [[NSMutableString alloc] initWithFormat:@"%d %d %d %d",abso.left,abso.top,abso.width,abso.height];
					NSString *expectedAttrs = @"20 320 100 100";
					STAssertTrue([expectedAttrs isEqualToString:layoutAttrs],@"expected %@, but %@",expectedAttrs,layoutAttrs);
					[layoutAttrs release];
					
					if ([abso.component isKindOfClass:[Image class]]) {
						Image *theImage= (Image *)abso.component;
						int expectedId = (59 + image_index++);
						STAssertTrue(expectedId == theImage.componentId,@"expected %d, but %d",expectedId,theImage.componentId);
						NSString *imageSrc = [[NSString alloc] initWithFormat:@"%c.png", (char)97 + state_index++];
						STAssertTrue([theImage.src isEqualToString:imageSrc],@"expected %@, but %@", theImage.src, imageSrc);
					}					
				}				
			}
		}
	}
	
	NSLog(@"groups count = %d",[groups count]);
	NSLog(@"screens count = %d",[screens count]);
	NSLog(@"xml parse done");
	
	NSMutableArray *screenNames = [NSMutableArray arrayWithObjects:@"basement",@"floor",nil];
	NSMutableArray *groupNames = [NSMutableArray arrayWithObjects:@"All rooms",@"living room",nil];
	
	//check screens
	for (int i=0;i<screenNames.count;i++) {
		STAssertTrue([[screenNames objectAtIndex:i] isEqualToString:[[screens objectAtIndex:i] name]],@"expected %@, but %@",[screenNames objectAtIndex:i],[[screens objectAtIndex:i] name]);
		STAssertTrue(i+5 == [[screens objectAtIndex:i] screenId],@"expected %d, but %d",i+5,[[screens objectAtIndex:i] screenId]);
	}
	
	//check groups
	for (int i=0;i<groupNames.count;i++) {
		STAssertTrue([[groupNames objectAtIndex:i] isEqualToString:[[groups objectAtIndex:i] name]],@"expected %@, but %@",[groupNames objectAtIndex:i],[[groups objectAtIndex:i] name]);
		STAssertTrue(i+1 == [[groups objectAtIndex:i] groupId],@"expected %d, but %d",i+1,[[groups objectAtIndex:i] groupId]);
	}
	
	[xmlParser release];
	[xml release];
	NSLog(@"End testParsePanelAbsoluteScreenBackgroundimageXML");
}

// panel_relative_screen_backgroundimage.xml test
- (void) testParsePanelRelativeScreenBackgroundimageXML {
	NSLog(@"Begin testParsePanelRelativeScreenBackgroundimageXML");
	[[Definition sharedDefinition] clearPanelXMLData];
	NSData *xml = [self readFile:@"panel_relative_screen_backgroundimage.xml"];
	
	NSXMLParser *xmlParser = [[NSXMLParser alloc] initWithData:xml];
	[xmlParser setDelegate:self];
	[xmlParser parse];
	NSMutableArray *groups = [[Definition sharedDefinition] groups];
	NSMutableArray *screens = [[Definition sharedDefinition] screens];
	int background_index = 1;
	int image_index = 0;
	int state_index = 0;
	for (Group *group in groups) {
		NSLog(@"group %@ has %d screen", group.name,group.screens.count);
		for (Screen *screen in group.screens) {
			
			NSLog(@"Begin test background of screen %@", [screen name]);
			// relative position
			STAssertTrue(![[screen background] isBackgroundImageAbsolutePosition], @"expected %d, but %d", NO, [[screen background] isBackgroundImageAbsolutePosition]);
			NSLog(@"isBackgroundImageAbsolutePosition of screen background is %d", [[screen background] isBackgroundImageAbsolutePosition]);
			
			NSString *backgroundImageRelativePosition = [[screen background] backgroundImageRelativePosition];
			NSString *expectedBackgroundImageRelativePosition;
			if (background_index%2 != 0) {
				expectedBackgroundImageRelativePosition = BG_IMAGE_RELATIVE_POSITION_LEFT;
			} else {
				expectedBackgroundImageRelativePosition = BG_IMAGE_RELATIVE_POSITION_RIGHT;
			}
			STAssertTrue([backgroundImageRelativePosition isEqualToString:expectedBackgroundImageRelativePosition], @"expected %@, but %@", expectedBackgroundImageRelativePosition, backgroundImageRelativePosition);
			NSLog(@"relative position of background image is %@", backgroundImageRelativePosition);
			
			// fillscreen
			BOOL fillScreen = [[screen background] fillScreen];
			BOOL expectedFillScreen = NO;
			STAssertTrue(fillScreen == expectedFillScreen, @"expected %d, but %d", expectedFillScreen, fillScreen);
			NSLog(@"fillScreen of background image is %d", fillScreen);
			
			// background image src
			NSString *backgroundImageSrc = [[[screen background] backgroundImage] src];
			NSString *expectedBackgroundImageSrc = [[NSString alloc] initWithFormat:@"basement%d.png", background_index];
			STAssertTrue([expectedBackgroundImageSrc isEqualToString:backgroundImageSrc], @"expected %@, but %@", expectedBackgroundImageSrc, backgroundImageSrc);
			NSLog(@"background image src is ", backgroundImageSrc);
			
			background_index++;
			
			NSLog(@"screen %@ has %d layout", screen.name, screen.layouts.count);
			for (LayoutContainer *layout in screen.layouts) {
				if([layout isKindOfClass:[AbsoluteLayoutContainer class]]){					
					NSLog(@"layout is absolute ");
					AbsoluteLayoutContainer *abso =(AbsoluteLayoutContainer *)layout;
					NSString *layoutAttrs = [[NSMutableString alloc] initWithFormat:@"%d %d %d %d",abso.left,abso.top,abso.width,abso.height];
					NSString *expectedAttrs = @"20 320 100 100";
					STAssertTrue([expectedAttrs isEqualToString:layoutAttrs],@"expected %@, but %@",expectedAttrs,layoutAttrs);
					[layoutAttrs release];
					
					if ([abso.component isKindOfClass:[Image class]]) {
						Image *theImage= (Image *)abso.component;
						int expectedId = (59 + image_index++);
						STAssertTrue(expectedId == theImage.componentId,@"expected %d, but %d",expectedId,theImage.componentId);
						NSString *imageSrc = [[NSString alloc] initWithFormat:@"%c.png", (char)97 + state_index++];
						STAssertTrue([theImage.src isEqualToString:imageSrc],@"expected %@, but %@", theImage.src, imageSrc);
					}					
				}				
			}
		}
	}
	
	NSLog(@"groups count = %d",[groups count]);
	NSLog(@"screens count = %d",[screens count]);
	NSLog(@"xml parse done");
	
	NSMutableArray *screenNames = [NSMutableArray arrayWithObjects:@"basement",@"floor",nil];
	NSMutableArray *groupNames = [NSMutableArray arrayWithObjects:@"All rooms",@"living room",nil];
	
	//check screens
	for (int i=0;i<screenNames.count;i++) {
		STAssertTrue([[screenNames objectAtIndex:i] isEqualToString:[[screens objectAtIndex:i] name]],@"expected %@, but %@",[screenNames objectAtIndex:i],[[screens objectAtIndex:i] name]);
		STAssertTrue(i+5 == [[screens objectAtIndex:i] screenId],@"expected %d, but %d",i+5,[[screens objectAtIndex:i] screenId]);
	}
	
	//check groups
	for (int i=0;i<groupNames.count;i++) {
		STAssertTrue([[groupNames objectAtIndex:i] isEqualToString:[[groups objectAtIndex:i] name]],@"expected %@, but %@",[groupNames objectAtIndex:i],[[groups objectAtIndex:i] name]);
		STAssertTrue(i+1 == [[groups objectAtIndex:i] groupId],@"expected %d, but %d",i+1,[[groups objectAtIndex:i] groupId]);
	}
	
	[xmlParser release];
	[xml release];
	NSLog(@"End testParsePanelRelativeScreenBackgroundimageXML");
}

// panel_absolute_slider_gesture.xml test
- (void) testParsePanelAbsoluteSliderGestureXML {
	NSLog(@"Begin testParsePanelAbsoluteSliderGestureXML ");
	[[Definition sharedDefinition] clearPanelXMLData];	
	NSData *xml = [self readFile:@"panel_absolute_slider_gesture.xml"];
	
	NSXMLParser *xmlParser = [[NSXMLParser alloc] initWithData:xml];
	[xmlParser setDelegate:self];
	[xmlParser parse];
	NSMutableArray *groups = [[Definition sharedDefinition] groups];
	NSMutableArray *screens = [[Definition sharedDefinition] screens];
	int slider_index = 0;
	int gesture_index = 0;
	for (Group *group in groups) {
		NSLog(@"group %@ has %d screen", group.name,group.screens.count);
		for (Screen *screen in group.screens) {
			int count = screen.gestures.count;
			STAssertTrue(4 == count, @"expected 4, but %d",count);
			for (Gesture *gesture in screen.gestures) {
				STAssertEquals(gesture.swipeType, gesture_index % 4, @"expected %d, but %d",gesture_index % 4,gesture.swipeType);
				STAssertEquals(gesture.hasControlCommand, YES, @"expected yes, but %d",gesture.hasControlCommand);
				
				switch (gesture_index % 4) {
					case 0:
						STAssertEquals(gesture.navigate.toScreen, 19, @"expected 19, but %d",gesture.navigate.toScreen);
						break;
					case 1:
						STAssertEquals(gesture.navigate.toGroup, 19, @"expected 19, but %d",gesture.navigate.toGroup);
						break;
					case 2:
						STAssertEquals(gesture.navigate.isSetting, YES, @"expected YES, but %d",gesture.navigate.isSetting);
						break;
					case 3:
						STAssertTrue(gesture.navigate == nil, @"expected nil, but %d",gesture.navigate);
						break;
					default:
						break;
				}
				
				gesture_index++;
				
			}
			NSLog(@"screen %@ has %d layout", screen.name, screen.layouts.count);
			for (LayoutContainer *layout in screen.layouts) {
				if([layout isKindOfClass:[AbsoluteLayoutContainer class]]){					
					NSLog(@"laylongt is absolute ");
					AbsoluteLayoutContainer *abso =(AbsoluteLayoutContainer *)layout;
					NSString *layoutAttrs = [[NSMutableString alloc] initWithFormat:@"%d %d %d %d",abso.left,abso.top,abso.width,abso.height];
					NSString *expectedAttrs = @"20 320 100 100";
					STAssertTrue([expectedAttrs isEqualToString:layoutAttrs],@"expected %@, but %@",expectedAttrs,layoutAttrs);
					[layoutAttrs release];
					
					if ([abso.component isKindOfClass:[Slider class]]) {
						Slider *theSlider = (Slider *)abso.component;
						int expectedId = (59 + slider_index++);
						STAssertTrue(expectedId == theSlider.componentId,@"expected %d, but %d",expectedId,theSlider.componentId);
						float maxValue = 100.0f;						
						STAssertTrue(theSlider.maxValue == maxValue,@"expected %f, but %f", maxValue, theSlider.maxValue);
						float minValue = 0.0f;
						STAssertTrue(theSlider.minValue == minValue,@"expected %f, but %f", minValue, theSlider.minValue);
					}					
				}				
			}
		}
	}
	
	NSLog(@"groups count = %d",[groups count]);
	NSLog(@"screens count = %d",[screens count]);
	NSLog(@"xml parse done");
	
	NSMutableArray *screenNames = [NSMutableArray arrayWithObjects:@"basement",@"floor",nil];
	NSMutableArray *groupNames = [NSMutableArray arrayWithObjects:@"All rooms",@"living room",nil];
	
	//check screens
	for (int i=0;i<screenNames.count;i++) {
		STAssertTrue([[screenNames objectAtIndex:i] isEqualToString:[[screens objectAtIndex:i] name]],@"expected %@, but %@",[screenNames objectAtIndex:i],[[screens objectAtIndex:i] name]);
		STAssertTrue(i+5 == [[screens objectAtIndex:i] screenId],@"expected %d, but %d",i+5,[[screens objectAtIndex:i] screenId]);
	}
	
	//check groups
	for (int i=0;i<groupNames.count;i++) {
		STAssertTrue([[groupNames objectAtIndex:i] isEqualToString:[[groups objectAtIndex:i] name]],@"expected %@, but %@",[groupNames objectAtIndex:i],[[groups objectAtIndex:i] name]);
		STAssertTrue(i+1 == [[groups objectAtIndex:i] groupId],@"expected %d, but %d",i+1,[[groups objectAtIndex:i] groupId]);
	}
	Screen *screen1 = (Screen *)[screens objectAtIndex:0];
	NSString *ids = [[screen1 pollingComponentsIds] componentsJoinedByString:@","];
	STAssertTrue([@"59,60" isEqualToString:ids],@"expected 59,60 but %@",ids);
	
	[xmlParser release];
	[xml release];
}

// panel_global_tabbar.xml test
- (void) testParsePanelGlobalTabbarXML {
	NSLog(@"Begin testParsePanelTabbarXML");
	[[Definition sharedDefinition] clearPanelXMLData];
	NSData *xml = [self readFile:@"panel_global_tabbar.xml"];
	
	NSXMLParser *xmlParser = [[NSXMLParser alloc] initWithData:xml];
	[xmlParser setDelegate:self];
	[xmlParser parse];
	
	// Begin assert tabbar.
	TabBar *tabBar = [[Definition sharedDefinition] tabBar];
	NSLog(@"Tabbar is : %@", tabBar);
	NSMutableArray *expectedTabBarItemsName = [NSMutableArray arrayWithObjects:@"previous", @"next", @"setting", nil];
	NSMutableArray *expectedTabBarItemsImageSrc = [NSMutableArray arrayWithObjects:@"previous.png", @"next.png", @"setting.png", nil];
	NSMutableArray *tabBarItems = tabBar.tabBarItems;
	NSLog(@"TabBar items count is : %d", tabBarItems.count);
	for (int i=0; i<tabBarItems.count; i++) {
		TabBarItem *tabBarItem = [tabBarItems objectAtIndex:i];
		
		// assert tabbar item name.
		NSString *expectedTabBarItemName = [expectedTabBarItemsName objectAtIndex:i];
		STAssertTrue([tabBarItem.tabBarItemName isEqualToString:expectedTabBarItemName], @"expected %@, but %@", expectedTabBarItemName, tabBarItem.tabBarItemName);
		NSLog(@"tabbarItemName is %@", [tabBarItem tabBarItemName]);
		NSLog(@"expectedTabbarItemName is %@", expectedTabBarItemName);
		
		// assert tabbar item navigate
		Navigate *navigate = tabBarItem.navigate;
		BOOL expectedIsPreviousScreen = YES;
		BOOL expectedIsNextScreen = YES;
		BOOL expectedIsSetting = YES;
		
		BOOL isPreviousScreen = navigate.isPreviousScreen;
		BOOL isNextScreen = navigate.isNextScreen;
		BOOL isSetting = navigate.isSetting;
		if (i % 3 == 0) {
			STAssertTrue(isPreviousScreen == expectedIsPreviousScreen, @"expected %d, but %d", expectedIsPreviousScreen, isPreviousScreen);
		} else if (i % 3 == 1) {
			STAssertTrue(isNextScreen == expectedIsNextScreen, @"expected %d, but %d", expectedIsNextScreen, isNextScreen);
		} else if (i % 3 == 2) {
			STAssertTrue(isSetting == expectedIsSetting, @"expected %d, but %d", expectedIsSetting, isSetting);
		}
		NSLog(@"IsPreviousScreen is %d", isPreviousScreen);
		NSLog(@"isNextScreen is %d", isNextScreen);
		NSLog(@"isSetting is %d", isSetting);
		
		// assert tabbar item image
		NSString *expectedTabBarItemImageSrc = [expectedTabBarItemsImageSrc objectAtIndex:i];
		STAssertTrue([tabBarItem.tabBarItemImage.src isEqualToString:expectedTabBarItemImageSrc], @"expected %@, but %@", expectedTabBarItemImageSrc, tabBarItem.tabBarItemImage.src);
		NSLog(@"tabBarItemImage src is %@", [[tabBarItem tabBarItemImage] src]);
		NSLog(@"expectedTabBarItemsImage src is %@", expectedTabBarItemImageSrc);
	}
	// End assert tabbar.
	
	NSMutableArray *groups = [[Definition sharedDefinition] groups];
	NSMutableArray *screens = [[Definition sharedDefinition] screens];
	NSLog(@"Has %d grounp(s).", [groups count]);
	int background_index = 1;
	int image_index = 0;
	int state_index = 0;
	NSMutableArray *cells = [[NSMutableArray alloc] init];
	for (Group *group in groups) {
		NSLog(@"group %@ has %d screen", group.name,group.screens.count);
		for (Screen *screen in group.screens) {
			
			NSLog(@"Begin test background of screen %@", [screen name]);
			// relative position
			STAssertTrue(![[screen background] isBackgroundImageAbsolutePosition], @"expected %d, but %d", NO, [[screen background] isBackgroundImageAbsolutePosition]);
			NSLog(@"isBackgroundImageAbsolutePosition of screen background is %d", [[screen background] isBackgroundImageAbsolutePosition]);
			
			NSString *backgroundImageRelativePosition = [[screen background] backgroundImageRelativePosition];
			NSString *expectedBackgroundImageRelativePosition;
			if (background_index%2 != 0) {
				expectedBackgroundImageRelativePosition = BG_IMAGE_RELATIVE_POSITION_LEFT;
			} else {
				expectedBackgroundImageRelativePosition = BG_IMAGE_RELATIVE_POSITION_RIGHT;
			}
			STAssertTrue([backgroundImageRelativePosition isEqualToString:expectedBackgroundImageRelativePosition], @"expected %@, but %@", expectedBackgroundImageRelativePosition, backgroundImageRelativePosition);
			NSLog(@"relative position of background image is %@", backgroundImageRelativePosition);
			
			// fillscreen
			BOOL fillScreen = [[screen background] fillScreen];
			BOOL expectedFillScreen = NO;
			STAssertTrue(fillScreen == expectedFillScreen, @"expected %d, but %d", expectedFillScreen, fillScreen);
			NSLog(@"fillScreen of background image is %d", fillScreen);
			
			// background image src
			NSString *backgroundImageSrc = [[[screen background] backgroundImage] src];
			NSString *expectedBackgroundImageSrc = [[NSString alloc] initWithFormat:@"basement%d.png", background_index];
			STAssertTrue([expectedBackgroundImageSrc isEqualToString:backgroundImageSrc], @"expected %@, but %@", expectedBackgroundImageSrc, backgroundImageSrc);
			NSLog(@"background image src of background is %@", backgroundImageSrc);
			
			NSLog(@"End test background of screen %@", [screen name]);			
			background_index++;
			
			NSLog(@"screen %@ has %d layout", screen.name, screen.layouts.count);
			for (LayoutContainer *layout in screen.layouts) {
				if([layout isKindOfClass:[GridLayoutContainer class]]){					
					NSLog(@"layout is grid ");
					GridLayoutContainer *grid =(GridLayoutContainer *)layout;
					NSString *layoutAttrs = [[NSMutableString alloc] initWithFormat:@"%d %d %d %d",grid.left,grid.top,grid.width,grid.height];
					NSString *expectedAttrs = @"20 20 300 400";
					STAssertTrue([expectedAttrs isEqualToString:layoutAttrs],@"expected %@, but %@",expectedAttrs,layoutAttrs);
					[layoutAttrs release];
					
					for (GridCell *cell in grid.cells) {			
						[cells addObject:cell];
						if ([cell.component isKindOfClass:[Image class]]) {
							Image *theImage = (Image *)cell.component;
							int expectedId = (59 + image_index++);
							STAssertTrue(expectedId == theImage.componentId,@"expected %d, but %d",expectedId,theImage.componentId);
							NSString *imageSrc = [[NSString alloc] initWithFormat:@"%c.png", (char)97 + state_index++];					
							STAssertTrue([theImage.src isEqualToString:imageSrc],@"expected %@, but %@", imageSrc, theImage.src);
						}	
					}
				}				
			}
		}
	}
	
	NSLog(@"groups count = %d",[groups count]);
	NSLog(@"screens count = %d",[screens count]);
	NSLog(@"xml parse done");
	
	NSMutableArray *screenNames = [NSMutableArray arrayWithObjects:@"basement",@"floor",nil];
	NSMutableArray *groupNames = [NSMutableArray arrayWithObjects:@"All rooms",@"living room",nil];
	
	//check screens
	for (int i=0;i<screenNames.count;i++) {
		STAssertTrue([[screenNames objectAtIndex:i] isEqualToString:[[screens objectAtIndex:i] name]],@"expected %@, but %@",[screenNames objectAtIndex:i],[[screens objectAtIndex:i] name]);
		STAssertTrue(i+5 == [[screens objectAtIndex:i] screenId],@"expected %d, but %d",i+5,[[screens objectAtIndex:i] screenId]);
	}
	
	//check groups
	for (int i=0;i<groupNames.count;i++) {
		STAssertTrue([[groupNames objectAtIndex:i] isEqualToString:[[groups objectAtIndex:i] name]],@"expected %@, but %@",[groupNames objectAtIndex:i],[[groups objectAtIndex:i] name]);
		STAssertTrue(i+1 == [[groups objectAtIndex:i] groupId],@"expected %d, but %d",i+1,[[groups objectAtIndex:i] groupId]);
	}
	
	[xmlParser release];
	[xml release];
	NSLog(@"End testParsePanelTabbarXML");
}

// panel_local_tabbar.xml test
- (void) testParsePanelLocalTabbarXML {
	NSLog(@"Begin testParsePanelTabbarXML");
	[[Definition sharedDefinition] clearPanelXMLData];
	NSData *xml = [self readFile:@"panel_local_tabbar.xml"];
	
	NSXMLParser *xmlParser = [[NSXMLParser alloc] initWithData:xml];
	[xmlParser setDelegate:self];
	[xmlParser parse];
	
	NSMutableArray *groups = [[Definition sharedDefinition] groups];
	NSMutableArray *screens = [[Definition sharedDefinition] screens];
	NSLog(@"Has %d grounp(s).", [groups count]);
	int background_index = 1;
	int image_index = 0;
	int state_index = 0;
	NSMutableArray *cells = [[NSMutableArray alloc] init];
	for (Group *group in groups) {
		// Begin assert tabbar
		TabBar *localTabBar = group.tabBar;
		NSLog(@"LocalTabbar of grounp '%@' is : %@", group.name, localTabBar);
		NSMutableArray *expectedLocalTabBarItemsName = [NSMutableArray arrayWithObjects:@"previous", @"next", @"setting", nil];
		NSMutableArray *expectedLocalTabBarItemsImageSrc = [NSMutableArray arrayWithObjects:@"previous.png", @"next.png", @"setting.png", nil];
		NSMutableArray *localTabBarItems = localTabBar.tabBarItems;
		NSLog(@"LocalTabBar items count of group '%@' is : %d", group.name, localTabBarItems.count);
		for (int i=0; i<localTabBarItems.count; i++) {
			TabBarItem *localTabBarItem = [localTabBarItems objectAtIndex:i];
			
			// assert tabbar item name.
			NSString *expectedLocalTabBarItemName = [expectedLocalTabBarItemsName objectAtIndex:i];
			STAssertTrue([localTabBarItem.tabBarItemName isEqualToString:expectedLocalTabBarItemName], @"expected %@, but %@", expectedLocalTabBarItemName, localTabBarItem.tabBarItemName);
			NSLog(@"localTabbarItemName is %@", [localTabBarItem tabBarItemName]);
			NSLog(@"expectedLocalTabbarItemName is %@", expectedLocalTabBarItemName);
		
			// assert tabbar item navigate
			Navigate *navigate = localTabBarItem.navigate;
			BOOL expectedIsPreviousScreen = YES;
			BOOL expectedIsNextScreen = YES;
			BOOL expectedIsSetting = YES;

			BOOL isPreviousScreen = navigate.isPreviousScreen;
			BOOL isNextScreen = navigate.isNextScreen;
			BOOL isSetting = navigate.isSetting;
			if (i % 3 == 0) {
				STAssertTrue(isPreviousScreen == expectedIsPreviousScreen, @"expected %d, but %d", expectedIsPreviousScreen, isPreviousScreen);
			} else if (i % 3 == 1) {
				STAssertTrue(isNextScreen == expectedIsNextScreen, @"expected %d, but %d", expectedIsNextScreen, isNextScreen);
			} else if (i % 3 == 2) {
				STAssertTrue(isSetting == expectedIsSetting, @"expected %d, but %d", expectedIsSetting, isSetting);
			}
			NSLog(@"IsPreviousScreen of local TabBarItem '%@' navigate is %d", localTabBarItem.tabBarItemName, isPreviousScreen);
			NSLog(@"isNextScreen of local TabBarItem '%@' navigate is %d", localTabBarItem.tabBarItemName, isNextScreen);
			NSLog(@"isSetting of local TabBarItem '%@' navigate is %d", localTabBarItem.tabBarItemName, isSetting);
	
			// assert tabbar item image
			NSString *expectedLocalTabBarItemImageSrc = [expectedLocalTabBarItemsImageSrc objectAtIndex:i];
			STAssertTrue([localTabBarItem.tabBarItemImage.src isEqualToString:expectedLocalTabBarItemImageSrc], @"expected %@, but %@", expectedLocalTabBarItemImageSrc, localTabBarItem.tabBarItemImage.src);
			NSLog(@"localTabBarItemImage src is %@", [[localTabBarItem tabBarItemImage] src]);
			NSLog(@"expectedLocalTabBarItemsImage src is %@", expectedLocalTabBarItemImageSrc);
		}
		// End assert tabbar
		
		NSLog(@"group %@ has %d screen", group.name,group.screens.count);
		for (Screen *screen in group.screens) {
			
			NSLog(@"Begin test background of screen %@", [screen name]);
			// relative position
			STAssertTrue(![[screen background] isBackgroundImageAbsolutePosition], @"expected %d, but %d", NO, [[screen background] isBackgroundImageAbsolutePosition]);
			NSLog(@"isBackgroundImageAbsolutePosition of screen background is %d", [[screen background] isBackgroundImageAbsolutePosition]);
			
			NSString *backgroundImageRelativePosition = [[screen background] backgroundImageRelativePosition];
			NSString *expectedBackgroundImageRelativePosition;
			if (background_index%2 != 0) {
				expectedBackgroundImageRelativePosition = BG_IMAGE_RELATIVE_POSITION_LEFT;
			} else {
				expectedBackgroundImageRelativePosition = BG_IMAGE_RELATIVE_POSITION_RIGHT;
			}
			STAssertTrue([backgroundImageRelativePosition isEqualToString:expectedBackgroundImageRelativePosition], @"expected %@, but %@", expectedBackgroundImageRelativePosition, backgroundImageRelativePosition);
			NSLog(@"relative position of background image is %@", backgroundImageRelativePosition);
			
			// fillscreen
			BOOL fillScreen = [[screen background] fillScreen];
			BOOL expectedFillScreen = NO;
			STAssertTrue(fillScreen == expectedFillScreen, @"expected %d, but %d", expectedFillScreen, fillScreen);
			NSLog(@"fillScreen of background image is %d", fillScreen);
			
			// background image src
			NSString *backgroundImageSrc = [[[screen background] backgroundImage] src];
			NSString *expectedBackgroundImageSrc = [[NSString alloc] initWithFormat:@"basement%d.png", background_index];
			STAssertTrue([expectedBackgroundImageSrc isEqualToString:backgroundImageSrc], @"expected %@, but %@", expectedBackgroundImageSrc, backgroundImageSrc);
			NSLog(@"background image src of background is %@", backgroundImageSrc);
			
			NSLog(@"End test background of screen %@", [screen name]);			
			background_index++;
			
			NSLog(@"screen %@ has %d layout", screen.name, screen.layouts.count);
			for (LayoutContainer *layout in screen.layouts) {
				if([layout isKindOfClass:[GridLayoutContainer class]]){					
					NSLog(@"layout is grid ");
					GridLayoutContainer *grid =(GridLayoutContainer *)layout;
					NSString *layoutAttrs = [[NSMutableString alloc] initWithFormat:@"%d %d %d %d",grid.left,grid.top,grid.width,grid.height];
					NSString *expectedAttrs = @"20 20 300 400";
					STAssertTrue([expectedAttrs isEqualToString:layoutAttrs],@"expected %@, but %@",expectedAttrs,layoutAttrs);
					[layoutAttrs release];
					
					for (GridCell *cell in grid.cells) {			
						[cells addObject:cell];
						if ([cell.component isKindOfClass:[Image class]]) {
							Image *theImage = (Image *)cell.component;
							int expectedId = (59 + image_index++);
							STAssertTrue(expectedId == theImage.componentId,@"expected %d, but %d",expectedId,theImage.componentId);
							NSString *imageSrc = [[NSString alloc] initWithFormat:@"%c.png", (char)97 + state_index++];					
							STAssertTrue([theImage.src isEqualToString:imageSrc],@"expected %@, but %@", imageSrc, theImage.src);
						}	
					}
				}				
			}
		}
	}
	
	NSLog(@"groups count = %d",[groups count]);
	NSLog(@"screens count = %d",[screens count]);
	NSLog(@"xml parse done");
	
	NSMutableArray *screenNames = [NSMutableArray arrayWithObjects:@"basement",@"floor",nil];
	NSMutableArray *groupNames = [NSMutableArray arrayWithObjects:@"All rooms",@"living room",nil];
	
	//check screens
	for (int i=0;i<screenNames.count;i++) {
		STAssertTrue([[screenNames objectAtIndex:i] isEqualToString:[[screens objectAtIndex:i] name]],@"expected %@, but %@",[screenNames objectAtIndex:i],[[screens objectAtIndex:i] name]);
		STAssertTrue(i+5 == [[screens objectAtIndex:i] screenId],@"expected %d, but %d",i+5,[[screens objectAtIndex:i] screenId]);
	}
	
	//check groups
	for (int i=0;i<groupNames.count;i++) {
		STAssertTrue([[groupNames objectAtIndex:i] isEqualToString:[[groups objectAtIndex:i] name]],@"expected %@, but %@",[groupNames objectAtIndex:i],[[groups objectAtIndex:i] name]);
		STAssertTrue(i+1 == [[groups objectAtIndex:i] groupId],@"expected %d, but %d",i+1,[[groups objectAtIndex:i] groupId]);
	}
	
	[xmlParser release];
	[xml release];
	NSLog(@"End testParsePanelTabbarXML");
}


#pragma mark delegate method of NSXMLParser
//Delegate method when find a element start
- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qualifiedName attributes:(NSDictionary *)attributeDict{
	
	if ([elementName isEqualToString:@"screen"]) {
		NSLog(@"start at screen");
		Screen *screen = [[Screen alloc] initWithXMLParser:parser elementName:elementName attributes:attributeDict parentDelegate:self];
		[[[Definition sharedDefinition] screens] addObject:screen];
		[screen release];
	} else if ([elementName isEqualToString:@"group"]) {
		NSLog(@"start at group");
		Group *group = [[Group alloc] initWithXMLParser:parser elementName:elementName attributes:attributeDict parentDelegate:self];
		[[[Definition sharedDefinition] groups] addObject:group];
		[group release];
	} else if ([elementName isEqualToString:@"tab"]) {
		NSLog(@"start at tab");
		TabBar *tabBar = [[TabBar alloc] initWithXMLParser:parser elementName:elementName attributes:attributeDict parentDelegate:self];
		[Definition sharedDefinition].tabBar = tabBar;
		[tabBar release];
	}
}

/**
 * When we find an openremote end element, restore the original XML parser delegate.
 */
- (void)parser:(NSXMLParser *)parser didEndElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qName {
	if ([elementName isEqualToString:@"openremote"]) {
		NSLog(@"End parse openremote");
		//		[parser setDelegate:nil];
		// 		[parser setDelegate:xmlParserParentDelegate];
		
	}
}
#endif


@end
