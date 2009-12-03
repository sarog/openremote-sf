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
#import "Toggle.h"
#import "ToggleState.h"
#import "Switch.h"
#import "GridLayoutContainer.h"
#import "GridCell.h"
#import "Button.h"
#import "Monitor.h"
#import "Slider.h"
#import "Label.h"
#import "Image.h"
#import "Gesture.h"
#import "BackgroundRelativePositionConstant.h"

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
						if ([cell.control isKindOfClass:[Button class]]) {
							Button * but = (Button *)cell.control;
							[buts addObject:but];
							NSString *expectedName = [[NSMutableString alloc] initWithFormat:@"%c",(char)65 + but_index];						
							STAssertTrue([but.name isEqualToString:expectedName],@"expected %@, but %@",expectedName,but.name);
							int expectedId = (59 + but_index++);
							STAssertTrue(expectedId == but.controlId,@"expected %d, but %d",expectedId,but.controlId);
							NSString *expectedNormalImageName = nil;
							if (but.image) {
								expectedNormalImageName = [[NSMutableString alloc] initWithFormat:@"%c.png",(char)97 + image_index++];						
								STAssertTrue([but.image.src isEqualToString:expectedNormalImageName],@"expected %@, but %@",expectedNormalImageName,but.image.src);
							}
							NSString *expectedPressedImageName = nil;
							if (but.imagePressed) {
								expectedPressedImageName = [[NSMutableString alloc] initWithFormat:@"%c.png",(char)97 + image_index++];
								STAssertTrue([but.imagePressed.src isEqualToString:expectedPressedImageName],@"expected %@, but %@",expectedPressedImageName,but.imagePressed.src);
							}
							
							[expectedNormalImageName release];
							[expectedPressedImageName release];
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
	STAssertTrue(((Button *)[buts objectAtIndex:2]).hasCommand == YES,@"expected YES");
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
						if ([cell.control isKindOfClass:[Switch class]]) {
							Switch *theSwitch = (Switch *)cell.control;
							int expectedId = (59 + switch_index++);
							STAssertTrue(expectedId == theSwitch.controlId,@"expected %d, but %d",expectedId,theSwitch.controlId);	
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
					
					if ([abso.control isKindOfClass:[Switch class]]) {
						Switch *theSwitch = (Switch *)abso.control;
						int expectedId = (59 + switch_index++);
						STAssertTrue(expectedId == theSwitch.controlId,@"expected %d, but %d",expectedId,theSwitch.controlId);	
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

// panel_grid_monitor.xml test
- (void) testParsePanelGridMonitorXML {
	NSLog(@"Begin testParsePanelGridMonitorXML");
	[[Definition sharedDefinition] clearPanelXMLData];
	NSData *xml = [self readFile:@"panel_grid_monitor.xml"];
	
	NSXMLParser *xmlParser = [[NSXMLParser alloc] initWithData:xml];
	[xmlParser setDelegate:self];
	[xmlParser parse];
	NSMutableArray *groups = [[Definition sharedDefinition] groups];
	NSMutableArray *screens = [[Definition sharedDefinition] screens];
	int monitor_index = 0;
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
						if ([cell.control isKindOfClass:[Monitor class]]) {
							Monitor *theMonitor = (Monitor *)cell.control;
							int expectedId = (59 + monitor_index++);
							STAssertTrue(expectedId == theMonitor.controlId,@"expected %d, but %d",expectedId,theMonitor.controlId);
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
	NSLog(@"End testParsePanelGridMonitorXML");
}

// panel_absolute_monitor.xml test
- (void) testParsePanelAbsoluteMonitorXML {
	NSLog(@"Begin testParsePanelAbsoluteMonitorXML ");
	[[Definition sharedDefinition] clearPanelXMLData];	
	NSData *xml = [self readFile:@"panel_absolute_monitor.xml"];
	
	NSXMLParser *xmlParser = [[NSXMLParser alloc] initWithData:xml];
	[xmlParser setDelegate:self];
	[xmlParser parse];
	NSMutableArray *groups = [[Definition sharedDefinition] groups];
	NSMutableArray *screens = [[Definition sharedDefinition] screens];
	int monitor_index = 0;
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
					
					if ([abso.control isKindOfClass:[Monitor class]]) {
						Monitor *theMonitor = (Monitor *)abso.control;
						int expectedId = (59 + monitor_index++);
						STAssertTrue(expectedId == theMonitor.controlId,@"expected %d, but %d",expectedId,theMonitor.controlId);
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
	NSLog(@"End testParsePanelAbsoluteMonitorXML ");
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
						if ([cell.control isKindOfClass:[Slider class]]) {
							Slider *theSlider = (Slider *)cell.control;
							int expectedId = (59 + slider_index++);
							STAssertTrue(expectedId == theSlider.controlId,@"expected %d, but %d",expectedId,theSlider.controlId);
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
					
					if ([abso.control isKindOfClass:[Switch class]]) {
						Slider *theSlider = (Slider *)abso.control;
						int expectedId = (59 + slider_index++);
						STAssertTrue(expectedId == theSlider.controlId,@"expected %d, but %d",expectedId,theSlider.controlId);
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
						if ([cell.control isKindOfClass:[Label class]]) {
							Label *theLabel = (Label *)cell.control;
							int expectedId = (59 + label_index++);
							STAssertTrue(expectedId == theLabel.controlId,@"expected %d, but %d",expectedId,theLabel.controlId);
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
					
					if ([abso.control isKindOfClass:[Label class]]) {
						Label *theLabel= (Label *)abso.control;
						int expectedId = (59 + label_index++);
						STAssertTrue(expectedId == theLabel.controlId,@"expected %d, but %d",expectedId,theLabel.controlId);
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
						if ([cell.control isKindOfClass:[Image class]]) {
							Image *theImage = (Image *)cell.control;
							int expectedId = (59 + image_index++);
							STAssertTrue(expectedId == theImage.controlId,@"expected %d, but %d",expectedId,theImage.controlId);
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
					
					if ([abso.control isKindOfClass:[Image class]]) {
						Image *theImage= (Image *)abso.control;
						int expectedId = (59 + image_index++);
						STAssertTrue(expectedId == theImage.controlId,@"expected %d, but %d",expectedId,theImage.controlId);
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
			
			// fullscreen
			BOOL fullScreen = [[screen background] fullScreen];
			BOOL expectedFullScreen;
			if (background_index%2 != 0) {
				expectedFullScreen = YES;
			} else {
				expectedFullScreen = NO;
			}
			STAssertTrue(fullScreen == expectedFullScreen, @"expected %d, but %d", expectedFullScreen, fullScreen);
			NSLog(@"fullScreen of background image is %d", fullScreen);
			
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
					
					if ([abso.control isKindOfClass:[Image class]]) {
						Image *theImage= (Image *)abso.control;
						int expectedId = (59 + image_index++);
						STAssertTrue(expectedId == theImage.controlId,@"expected %d, but %d",expectedId,theImage.controlId);
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
			
			// fullscreen
			BOOL fullScreen = [[screen background] fullScreen];
			BOOL expectedFullScreen;
			if (background_index%2 != 0) {
				expectedFullScreen = YES;
			} else {
				expectedFullScreen = NO;
			}
			STAssertTrue(fullScreen == expectedFullScreen, @"expected %d, but %d", expectedFullScreen, fullScreen);
			NSLog(@"fullScreen of background image is %d", fullScreen);
			
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
					
					if ([abso.control isKindOfClass:[Image class]]) {
						Image *theImage= (Image *)abso.control;
						int expectedId = (59 + image_index++);
						STAssertTrue(expectedId == theImage.controlId,@"expected %d, but %d",expectedId,theImage.controlId);
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

// panel_absolute_toggle.xml test
- (void) testParsePanelAbsoluteToggleXML {

	[[Definition sharedDefinition] clearPanelXMLData];
	NSData *xml = [self readFile:@"panel_absolute_toggle.xml"];
	
	NSXMLParser *xmlParser = [[NSXMLParser alloc] initWithData:xml];
	[xmlParser setDelegate:self];
	[xmlParser parse];
	NSMutableArray *groups = [[Definition sharedDefinition] groups];
	NSMutableArray *screens = [[Definition sharedDefinition] screens];
	int state_index = 0;
	int toggle_index = 0;
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
					if ([abso.control isKindOfClass:[Toggle class]]) {
						Toggle *toggle = (Toggle *)abso.control;
						int expectedId = (59 + toggle_index++);
						STAssertTrue(expectedId == toggle.controlId,@"expected %d, but %d",expectedId,toggle.controlId);
						NSLog(@"toggle has %d states", toggle.states.count);
						for (ToggleState *st in toggle.states) {							
							//NSLog(@"command ref = %d" ,st.commandId);
							NSString *expectedName = [[NSMutableString alloc] initWithFormat:@"%c.png",(char)97 + state_index++];
							NSLog(@"expected state name = %@", expectedName);
							STAssertTrue([st.image.src isEqualToString:expectedName],@"expected %@, but %@",expectedName,st.image.src);
							[expectedName release];
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
	
	Screen *screen1 = (Screen *)[screens objectAtIndex:0];
	NSString *ids = [[screen1 pollingComponentsIds] componentsJoinedByString:@","];
	STAssertTrue([@"59,60" isEqualToString:ids],@"expected '59,60', but %@",ids);
	
	[xmlParser release];
	[xml release];
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
				STAssertEquals(gesture.hasControlCommnad, YES, @"expected yes, but %d",gesture.hasControlCommnad);
				gesture_index++;
			}
			NSLog(@"screen %@ has %d layout", screen.name, screen.layouts.count);
			for (LayoutContainer *layout in screen.layouts) {
				if([layout isKindOfClass:[AbsoluteLayoutContainer class]]){					
					NSLog(@"layout is absolute ");
					AbsoluteLayoutContainer *abso =(AbsoluteLayoutContainer *)layout;
					NSString *layoutAttrs = [[NSMutableString alloc] initWithFormat:@"%d %d %d %d",abso.left,abso.top,abso.width,abso.height];
					NSString *expectedAttrs = @"20 320 100 100";
					STAssertTrue([expectedAttrs isEqualToString:layoutAttrs],@"expected %@, but %@",expectedAttrs,layoutAttrs);
					[layoutAttrs release];
					
					if ([abso.control isKindOfClass:[Switch class]]) {
						Slider *theSlider = (Slider *)abso.control;
						int expectedId = (59 + slider_index++);
						STAssertTrue(expectedId == theSlider.controlId,@"expected %d, but %d",expectedId,theSlider.controlId);
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
