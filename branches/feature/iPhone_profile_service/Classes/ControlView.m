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
#import "ControlView.h"
#import "Toggle.h"
#import "ToggleView.h"
#import "SwitchView.h"
#import "Switch.h"
#import "ViewHelper.h"
#import "ServerDefinition.h"
#import "ButtonView.h"
#import "Button.h"
#import "CFNetwork/CFHTTPMessage.h"
#import "Definition.h"
#import "NotificationConstant.h"
#import "DataBaseService.h"
#import "User.h"

@interface ControlView (Private)

@end


@implementation ControlView

@synthesize control, controlTimer;

//NOTE:You should init all these views with initWithFrame and you should pass in valid frame rects.
//Otherwise, UI widget will not work in nested UIViews
+ (ControlView *)buildWithControl:(Control *)control frame:(CGRect)frame{
	ControlView *controlView = nil;
	if ([control isKindOfClass:[Toggle class]]) {
		controlView = [ToggleView alloc];
	} else if  ([control isKindOfClass:[Switch class]]) {
		controlView = [SwitchView alloc];
	} else if  ([control isKindOfClass:[Button class]]) {
		controlView = [ButtonView alloc];
	}

	
	return [controlView initWithControl:control frame:frame];
}

- (id)initWithControl:(Control *)c frame:(CGRect)frame{
	if (self = [super initWithFrame:frame]) {
		control = c;
		isError = NO;
		//transparent background 
		[self setBackgroundColor:[UIColor colorWithRed:0.0 green:0.0 blue:0.0 alpha:0.0]];
		//[self setContentMode:UIViewContentModeTopLeft];
	}

	return self;
}

/* Sets polling status.
 * Returns YES if success, returns NO if the status is invalid.
 * NOTE: This is an abstract method, must be implemented in subclass
 */
- (void)setPollingStatus:(NSNotification *)notification {
	[self doesNotRecognizeSelector:_cmd];
}



- (void)sendCommandRequest:(NSString *)commandType{
	
	if ([[Definition sharedDefinition] password] == nil) {
		[[NSNotificationCenter defaultCenter] postNotificationName:NotificationPopulateCredentialView object:nil];
		return;
	}

	
	NSString *location = [[NSString alloc] initWithFormat:[ServerDefinition securedControlRESTUrl]];
	NSURL *url = [[NSURL alloc]initWithString:[location stringByAppendingFormat:@"/%d/%@",control.controlId,commandType]];
	NSLog([location stringByAppendingFormat:@"/%d/%@",control.controlId,commandType]);


	//assemble put request 
	NSMutableURLRequest *request = [[NSMutableURLRequest alloc] init];
	[request setURL:url];
	[request setHTTPMethod:@"POST"];
	
	CFHTTPMessageRef dummyRequest = CFHTTPMessageCreateRequest(kCFAllocatorDefault, CFSTR("GET"), (CFURLRef)[request URL], kCFHTTPVersion1_1);
	CFHTTPMessageAddAuthentication(dummyRequest, nil, (CFStringRef)[[Definition sharedDefinition] username], (CFStringRef)[[Definition sharedDefinition] password], kCFHTTPAuthenticationSchemeBasic, FALSE);
	NSString *authString = (NSString *)CFHTTPMessageCopyHeaderFieldValue(dummyRequest, CFSTR("Authorization"));
	CFRelease(dummyRequest);
	[request setValue:authString forHTTPHeaderField:@"Authorization"];
	
	URLConnectionHelper *connection = [[URLConnectionHelper alloc]initWithRequest:request  delegate:self];
	
	[location release];
	[url	 release];
	[request release];
	[connection autorelease];	
}

- (void)handleServerErrorWithStatusCode:(int) statusCode {
	if (statusCode != 200) {
		NSString *errorMessage = nil;
		switch (statusCode) {
			case 404:
				errorMessage = [NSString stringWithString:@"The command was sent to an invalid URL."];
				break;
			case 418:
				errorMessage = [NSString stringWithString:@"Controller failed to construct an event for this command. Please check the controller log."];
				break;
			case 419:
				errorMessage = [NSString stringWithString:@"Controller did not recognize the sent command id."];
				break;
			case 420:
				errorMessage = [NSString stringWithString:@"Controller failed to create an event for the command. Please check controller configuration and log."];
				break;
			case 422:
				errorMessage = [NSString stringWithString:@"Error in controller - controller.xml is not correctly deployed."];
				break;
			case 423:
				errorMessage = [NSString stringWithString:@"Controller did not locate a mapped event correctly. Please check the controller.xml configuration."];
				break;
			case 424:
				errorMessage = [NSString stringWithString:@"Error in controller - invalid controller.xml. Please check controller log for errors."];
				break;
			case 500:
				errorMessage = [NSString stringWithString:@"Error in controller. Please check controller log."];
				break;
			case 401://HTTP Basic Auth
				errorMessage = [NSString stringWithString:@"You can't execute a protected command without authentication."];
				[Definition sharedDefinition].password = nil;
				break;
		}
		if (!errorMessage) {
			errorMessage = [NSString stringWithFormat:@"Occured unknown error, satus code is %d",statusCode];
		}
		[self cancelTimer];
		isError = YES;
		[ViewHelper showAlertViewWithTitle:@"Send Request Error" Message:errorMessage];
	} else {  // 200 
		DataBaseService *dbService = [DataBaseService sharedDataBaseService];
		User *user = [[User alloc] initWithUsernameAndPassword:[Definition sharedDefinition].username password:[Definition sharedDefinition].password];
		[dbService deleteAllUsers];
		[dbService insertUser:user];
	}
	
}

- (void)cancelTimer {
	if (controlTimer) {
		[controlTimer invalidate];
	}
	controlTimer = nil;
}

#pragma mark delegate method of NSURLConnection
- (void) definitionURLConnectionDidFailWithError:(NSError *)error {

	[self cancelTimer];
	[[ViewHelper alloc] showAlertViewWithTitleAndSettingNavigation:@"Command failed" Message:[error localizedDescription]];

}

//Shows alertView when the request successful
- (void)definitionURLConnectionDidFinishLoading:(NSData *)data {
	//	NSString *result = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
	//	if (YES) {
	//	//if ([result isEqualToString:@"true"]) {
	//		//UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Result" message:[[[NSString alloc] initWithFormat: @"Send Put request with event id: %d success!",control.eventID] autorelease] delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
	////		[alert show];
	////		[alert release];
	//	}
	//	[result release];
	//NSLog(@"definitionURLConnectionDidFinishLoading");
}

- (void)definitionURLConnectionDidReceiveResponse:(NSURLResponse *)response {
	NSHTTPURLResponse *httpResp = (NSHTTPURLResponse *)response;
	NSLog(@"control[%d]statusCode is %d",control.controlId, [httpResp statusCode]);
	
	[self handleServerErrorWithStatusCode:[httpResp statusCode]];
}


- (void)dealloc {
	[controlTimer release];
	[control release];
	[super dealloc];
}


@end
