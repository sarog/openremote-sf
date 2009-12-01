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
#import "MonitorView.h"
#import "Monitor.h"
#import "CFNetwork/CFHTTPMessage.h"
#import "Definition.h"
#import "NotificationConstant.h"
#import "DataBaseService.h"
#import "CredentialUtil.h"
#import "ControllerException.h"
#import "Slider.h"
#import "SliderView.h"

@interface ControlView (Private)

@end


@implementation ControlView

@synthesize control;

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
	} else if ([control isKindOfClass:[Monitor class]]) {
		controlView = [MonitorView alloc];
	} else if ([control isKindOfClass:[Slider class]]) {
		controlView = [SliderView alloc];
	} else {
		return nil;
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
	
	[CredentialUtil addCredentialToNSMutableURLRequest:request];
	
	URLConnectionHelper *connection = [[URLConnectionHelper alloc]initWithRequest:request  delegate:self];
	
	[location release];
	[url	 release];
	[request release];
	[connection autorelease];	
}

- (void)handleServerErrorWithStatusCode:(int) statusCode {
	if (statusCode != 200) {
		if (statusCode != 401) {
			[[DataBaseService sharedDataBaseService] saveCurrentUser];
		} else {
			[Definition sharedDefinition].password = nil;
		}

		[self cancelTimer];
		isError = YES;
		[ViewHelper showAlertViewWithTitle:@"Send Request Error" Message:[ControllerException exceptionMessageOfCode:statusCode]];	
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

}


- (void)definitionURLConnectionDidFinishLoading:(NSData *)data {
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
