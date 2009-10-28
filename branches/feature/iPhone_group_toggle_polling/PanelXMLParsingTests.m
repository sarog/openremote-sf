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
	STAssertTrue(((GridCell *)[cells objectAtIndex:3]).colspan == 0,@"expected %d",0);
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
