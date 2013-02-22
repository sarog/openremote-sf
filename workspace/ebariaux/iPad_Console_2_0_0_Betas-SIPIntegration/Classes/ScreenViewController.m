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


#import "ScreenViewController.h"
#import "ScreenView.h"
#import "ViewHelper.h"
#import "Definition.h"
#import "NotificationConstant.h"
#import "ServerDefinition.h"
#import "CredentialUtil.h"
#import "ControllerException.h"
#import "DataBaseService.h"

@interface ScreenViewController (Private)

- (void)sendCommandRequest:(int)componentId;
- (void)doNavigate:(Navigate *)navi;

@end



@implementation ScreenViewController

@synthesize screen, polling;

/**
 * Assign parameter screen model data to screenViewController.
 */
- (void)setScreen:(Screen *)s {
	[s retain];
	[screen release];
	screen = s;
	if ([[screen pollingComponentsIds] count] > 0 ) {
		polling = [[PollingHelper alloc] initWithComponentIds:[[screen pollingComponentsIds] componentsJoinedByString:@","]];
	}
}

/**
 * Perform gesture action. Currently, the gesture should be one action of sliding from left to right, 
 * sliding from right to left, sliding from top to bottom and sliding from bottom to top.
 */
- (void)performGesture:(Gesture *)gesture {
	Gesture * g = [screen getGestureIdByGestureSwipeType:gesture.swipeType];
	if (g) {
		if (g.hasControlCommand) {
			[self sendCommandRequest:g.componentId];
		} else if (g.navigate) {
			[self doNavigate:g.navigate];
		}
	}

}

// Implement loadView to create a view hierarchy programmatically.
- (void)loadView {
	ScreenView *v = [[ScreenView alloc] init];

	//set Screen in ScreenView
	[v setScreen:screen];
	
	[self setView:v];
	[v setBackgroundColor:[UIColor blackColor]];
	[v release];
}

- (void)startPolling {
	[polling requestCurrentStatusAndStartPolling];
}
- (void)stopPolling {
	[polling cancelPolling];
}

// Send control command for gesture actions.
- (void)sendCommandRequest:(int)componentId {
	
//	if ([[Definition sharedDefinition] password] == nil) {
//		[[NSNotificationCenter defaultCenter] postNotificationName:NotificationPopulateCredentialView object:nil];
//		return;
//	}
	
	NSString *location = [[ServerDefinition controlRESTUrl] stringByAppendingFormat:@"/%d/swipe", componentId];
	NSURL *url = [[NSURL alloc] initWithString:location];
	NSLog(@"%@", location);
	
	
	//assemble put request 
	NSMutableURLRequest *request = [[NSMutableURLRequest alloc] init];
	[request setURL:url];
	[request setHTTPMethod:@"POST"];
	
	[CredentialUtil addCredentialToNSMutableURLRequest:request];
	
	URLConnectionHelper *connection = [[URLConnectionHelper alloc] initWithRequest:request delegate:self];
	
	[url release];
	[request release];
	[connection autorelease];	
}

// Handle the server errors which are from controller server with status code.
- (void)handleServerResponseWithStatusCode:(int) statusCode {
	if (statusCode != 200) {
		if (statusCode == UNAUTHORIZED) {
			[Definition sharedDefinition].password = nil;
			[[NSNotificationCenter defaultCenter] postNotificationName:NotificationPopulateCredentialView object:nil];
		} else {
			[ViewHelper showAlertViewWithTitle:@"Command failed" Message:[ControllerException exceptionMessageOfCode:statusCode]];
		}
	}
}

#pragma mark delegate method of NSURLConnection
- (void) definitionURLConnectionDidFailWithError:(NSError *)error {
}


- (void)definitionURLConnectionDidFinishLoading:(NSData *)data {
}

- (void)definitionURLConnectionDidReceiveResponse:(NSURLResponse *)response {
	NSHTTPURLResponse *httpResp = (NSHTTPURLResponse *)response;
	
	[self handleServerResponseWithStatusCode:[httpResp statusCode]];
}


- (void)doNavigate:(Navigate *)navi {
	[[NSNotificationCenter defaultCenter] postNotificationName:NotificationNavigateTo object:navi];
}

- (void)dealoc {
	[polling release];
	//[screen release];
	
	[super dealloc];
}

@end
