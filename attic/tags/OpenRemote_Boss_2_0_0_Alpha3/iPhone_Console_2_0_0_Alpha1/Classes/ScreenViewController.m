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


- (void)setScreen:(Screen *)s {
	[s retain];
	[screen release];
	screen = s;
	if ([[screen pollingComponentsIds] count] > 0 ) {
		polling = [[PollingHelper alloc] initWithComponentIds:[[[screen pollingComponentsIds] componentsJoinedByString:@","] retain]];
	}
	
}

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
	ScreenView *view = [[ScreenView alloc] init];

	//set Screen in ScreenView
	[view setScreen:screen];
	[self setView:view];
	[view release];
}

- (void)startPolling {
	[polling requestCurrentStatusAndStartPolling];
}
- (void)stopPolling {
	[polling cancelPolling];
}

- (void)sendCommandRequest:(int)componentId {
	
	if ([[Definition sharedDefinition] password] == nil) {
		[[NSNotificationCenter defaultCenter] postNotificationName:NotificationPopulateCredentialView object:nil];
		return;
	}
	
	
	NSString *location = [[NSString alloc] initWithFormat:[ServerDefinition securedControlRESTUrl]];
	NSURL *url = [[NSURL alloc]initWithString:[location stringByAppendingFormat:@"/%d/%@",componentId,@"swipe"]];
	NSLog([location stringByAppendingFormat:@"/%d/%@",componentId,@"swipe"]);
	
	
	//assemble put request 
	NSMutableURLRequest *request = [[NSMutableURLRequest alloc] init];
	[request setURL:url];
	[request setHTTPMethod:@"POST"];
	
	[CredentialUtil addCredentialToNSMutableURLRequest:request];
	
	URLConnectionHelper *connection = [[URLConnectionHelper alloc]initWithRequest:request  delegate:self];
	
	[location release];
	[url	 release];
	[request release];
	[connection autorelease];	
}

- (void)handleServerErrorWithStatusCode:(int) statusCode {
	if (statusCode != 200) {
		if (statusCode == 401) {
			[Definition sharedDefinition].password = nil;
		}
		
		[ViewHelper showAlertViewWithTitle:@"Send Request Error" Message:[ControllerException exceptionMessageOfCode:statusCode]];	
	}
}

#pragma mark delegate method of NSURLConnection
- (void) definitionURLConnectionDidFailWithError:(NSError *)error {
}


- (void)definitionURLConnectionDidFinishLoading:(NSData *)data {
}

- (void)definitionURLConnectionDidReceiveResponse:(NSURLResponse *)response {
	NSHTTPURLResponse *httpResp = (NSHTTPURLResponse *)response;
	
	[self handleServerErrorWithStatusCode:[httpResp statusCode]];
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
