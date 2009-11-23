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

#import "SwitchServerAlertHelper.h"
#import "NotificationConstant.h"
#import "AppSettingsDefinition.h"
#import "ViewHelper.h"

@interface SwitchServerAlertHelper(Private)
-(void) switchToDiscoveredServer;
-(void) switchToCustomizedServer;
@end




@implementation SwitchServerAlertHelper

@synthesize updateController;

#pragma mark Instance method

-(void) showAlertViewWithTitleDiscorveredServerAndSettingNavigation:(NSString *)title Message:(NSString *)message  {
	UIAlertView *alert = [[UIAlertView alloc] initWithTitle:title message:message delegate:self cancelButtonTitle:nil otherButtonTitles:nil];
	[alert setTitle:@"switch to discovered server"];
	[alert addButtonWithTitle:@"Refresh"];
	[alert addButtonWithTitle:@"Settings"];
	[alert show];
	[alert setDelegate:self];
	[alert autorelease];
}

-(void) showAlertViewWithTitleCustomizedServerAndSettingNavigation:(NSString *)title Message:(NSString *)message  {
	UIAlertView *alert = [[UIAlertView alloc] initWithTitle:title message:message delegate:self cancelButtonTitle:nil otherButtonTitles:nil];
	[alert setTitle:@"switch to customized server"];
	[alert addButtonWithTitle:@"OK"];
	[alert addButtonWithTitle:@"Settings"];
	[alert show];
	[alert setDelegate:self];
	[alert autorelease];
}

-(void) showAlertViewWithTitleOnlyNoAndSettingNavigation:(NSString *)title Message:(NSString *)message  {
	UIAlertView *alert = [[UIAlertView alloc] initWithTitle:title message:message delegate:self cancelButtonTitle:nil otherButtonTitles:nil];
	[alert setTitle:@"no available server"];
	[alert addButtonWithTitle:@"NO"];
	[alert addButtonWithTitle:@"Settings"];
	[alert show];
	[alert setDelegate:self];
	[alert autorelease];
}

#pragma mark Private method

-(void) switchToDiscoveredServer {
	NSMutableArray *availableAutoServers = [AppSettingsDefinition getAutoServers];
	[AppSettingsDefinition setAutoDiscovery:YES];
	[[availableAutoServers objectAtIndex:0] setValue:[NSNumber numberWithBool:YES] forKey:@"choose"];
	[AppSettingsDefinition writeToFile];
	NSString *availableAutoServerURL = [[availableAutoServers objectAtIndex:0] objectForKey:@"url"];
	NSLog(@"Switching to %@ best autoServer, please wait.", availableAutoServerURL);
	if (updateController) {
		[updateController release];
		updateController = nil;
	}
	updateController = [[UpdateController alloc] initWithDelegate:self];
	[updateController checkConfigAndUpdate];
}

-(void) switchToCustomizedServer {
	NSMutableArray *availableCustomizedServers = [AppSettingsDefinition getCustomServers];
	[AppSettingsDefinition setAutoDiscovery:NO];
	[[availableCustomizedServers objectAtIndex:0] setValue:[NSNumber numberWithBool:YES] forKey:@"choose"];
	[AppSettingsDefinition writeToFile];
	NSString *availableCustomizedServerURL = [[availableCustomizedServers objectAtIndex:0] objectForKey:@"url"];
	NSLog(@"Switching to %@ best customizedServer, please wait.", availableCustomizedServerURL);
	if (updateController) {
		[updateController release];
		updateController = nil;
	}
	updateController = [[UpdateController alloc] initWithDelegate:self];
	[updateController checkConfigAndUpdate];
}

#pragma mark Delegate method of UIAlertView

-(void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex {	
	if (buttonIndex == 0 && [[alertView title] isEqualToString:@"switch to discovered server"]) {
		[self switchToDiscoveredServer];
	} else if (buttonIndex == 0 && [[alertView title] isEqualToString:@"switch to customized server"]) {
		[self switchToCustomizedServer];
	} else if (buttonIndex == 0 && [[alertView title] isEqualToString:@"no available server"]) {
		//do nothing
	}
	if (buttonIndex == 1) {//setting button
		[[NSNotificationCenter defaultCenter] postNotificationName:NotificationPopulateSettingsView object:nil];
	}
}

#pragma mark Delegate method of UpdateController

- (void)didUpadted {
	NSLog(@"----------DidUpdated in switchServerAlertHelper------------");
	[[NSNotificationCenter defaultCenter] postNotificationName:NotificationRefreshGroupsView object:nil];
}

- (void)didUseLocalCache:(NSString *)errorMessage {
	NSLog(@"------------didUseLocalCache in switchServerAlertHelper------------");
	[ViewHelper showAlertViewWithTitle:@"Warning" Message:errorMessage];
	[[NSNotificationCenter defaultCenter] postNotificationName:NotificationRefreshGroupsView object:nil];
}

- (void)dealloc {
	[updateController release];
	[super dealloc];
}

@end
