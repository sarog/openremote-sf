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
#import "Control.h"
#import "DirectoryDefinition.h"
#import "ServerDefinition.h"
#import "ViewHelper.h"

@interface ControlView (Private) 
- (void)createButton;
- (void)controlButtonDown:(id)sender;
- (void)controlButtonUp:(id)sender;
- (void)sendRequest;
- (void)sendBegin;
- (void)sendEnd;
- (void)handleServerErrorWithStatusCode:(int) statusCode;
@end

@implementation ControlView

@synthesize control;


- (id)initWithFrame:(CGRect)frame {
    if (self = [super initWithFrame:frame]) {
        // Initialization code
	[self setBackgroundColor:[UIColor blackColor]];
	
    }
    return self;
}

// Set control and add button in this view according to control
- (void)setControl:(Control *)c {
	[c retain];
	[control release];
	control = c;
	
	[self createButton];
}

//Create button according to control and add tap event
- (void)createButton {
	if (button != nil) {
		[button release];
		button = nil;
	}
	button = [[UIButton buttonWithType:UIButtonTypeCustom] retain];
	icon = nil;	
		
	[button addTarget:self action:@selector(controlButtonDown:) forControlEvents:UIControlEventTouchDown];
	[button addTarget:self action:@selector(controlButtonUp:) forControlEvents:UIControlEventTouchUpInside];
	[button addTarget:self action:@selector(controlButtonUp:) forControlEvents:UIControlEventTouchUpOutside];	
	[self addSubview:button];
	
}

//Invoked when the button touch up inside
- (void)controlButtonDown:(id)sender {
	isTouchUp =NO;
	shouldSendEnd = NO;
	isError = NO;

	buttonTimer = [NSTimer scheduledTimerWithTimeInterval:0.4 target:self selector:@selector(checkClick) userInfo:nil repeats:NO];
}

- (void)checkClick{
	[buttonTimer invalidate];
	if (!isTouchUp) {
		[self sendBegin];
		shouldSendEnd = YES;
	}
}

- (void) controlButtonUp:(id)sender {
	
	isTouchUp = YES;
	if (shouldSendEnd) {
		[self sendEnd];
	} else {
		[buttonTimer invalidate];
		[self	sendRequest];
	}
}

- (void)sendRequest {
	NSLog(@"Send request");
	NSString *location = [[NSString alloc] initWithFormat:[ServerDefinition eventHandleRESTUrl]];
	NSURL *url = [[NSURL alloc]initWithString:[location stringByAppendingFormat:@"/%d/click",control.eventID]];
	//assemble put request 
	NSMutableURLRequest *request = [[NSMutableURLRequest alloc] init];
	[request setURL:url];
	[request setHTTPMethod:@"POST"];

	URLConnectionHelper *connection = [[URLConnectionHelper alloc]initWithRequest:request  delegate:self];
										   
	[location release];
	[url	 release];
	[request release];
	[connection autorelease];	
}

- (void)sendBegin {
	NSLog(@"Send Begin.");
	NSString *location = [[NSString alloc] initWithFormat:[ServerDefinition eventHandleRESTUrl]];
	NSURL *url = [[NSURL alloc]initWithString:[location stringByAppendingFormat:@"/%d/press",control.eventID]];
	//assemble put request 
	NSMutableURLRequest *request = [[NSMutableURLRequest alloc] init];
	[request setURL:url];
	[request setHTTPMethod:@"POST"];
	
	URLConnectionHelper *connection = [[URLConnectionHelper alloc]initWithRequest:request  delegate:self];
	
	[location release];
	[url	 release];
	[request release];
	[connection autorelease];	
	
}
- (void)sendEnd {
	NSLog(@"Send End.");
	NSString *location = [[NSString alloc] initWithFormat:[ServerDefinition eventHandleRESTUrl]];
	NSURL *url = [[NSURL alloc]initWithString:[location stringByAppendingFormat:@"/%d/release",control.eventID]];
	//assemble put request 
	NSMutableURLRequest *request = [[NSMutableURLRequest alloc] init];
	[request setURL:url];
	[request setHTTPMethod:@"POST"];
	
	URLConnectionHelper *connection = [[URLConnectionHelper alloc]initWithRequest:request  delegate:self];
	
	[location release];
	[url	 release];
	[request release];
	[connection autorelease];	
	
}


#pragma mark delegate method of NSURLConnection
- (void) definitionURLConnectionDidFailWithError:(NSError *)error {
	if (!isError) {
		UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Error Occured" message:[error localizedDescription] delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
		[alert show];
		[alert release];
		isError = YES;
	} 
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
	NSLog(@"definitionURLConnectionDidFinishLoading");
}

- (void)definitionURLConnectionDidReceiveResponse:(NSURLResponse *)response {
	NSHTTPURLResponse *httpResp = (NSHTTPURLResponse *)response;
	NSLog(@"statusCode is %d", [httpResp statusCode]);
	
	[self handleServerErrorWithStatusCode:[httpResp statusCode]];
}

- (void)handleServerErrorWithStatusCode:(int) statusCode {
	if (statusCode != 200) {
		NSString *errorMessage = nil;
		switch (statusCode) {
			case 404:
				errorMessage = [NSString stringWithString:@"Request URL is invalid"];
				break;
			case 418:
				errorMessage = [NSString stringWithString:@"Event Build Error. Happens when an event can't be built from a DOM Element."];
				break;
			case 419:
				errorMessage = [NSString stringWithString:@"No Such Button Error."];
				break;
			case 420:
				errorMessage = [NSString stringWithString:@"No Such Event Builder Error."];
				break;
			case 422:
				errorMessage = [NSString stringWithString:@"controller.xml Not Found Error."];
				break;
			case 423:
				errorMessage = [NSString stringWithString:@"No Such Event Error."];
				break;
			case 424:
				errorMessage = [NSString stringWithString:@"Invalid controller.xml Error."];
				break;
			case 500:
				errorMessage = [NSString stringWithString:@"Server error"];
				break;
		}
		if (!errorMessage) {
			errorMessage = [NSString stringWithFormat:@"Occured unknown error, satus code is @d",statusCode];
		}
		[ViewHelper showAlertViewWithTitle:@"Send Request Error" Message:errorMessage];
	}
		
}

//override layoutSubviews method of UIView 
- (void)layoutSubviews {	
	[button setFrame:[self bounds]];
	if (control.icon && [[NSFileManager defaultManager] fileExistsAtPath:[[DirectoryDefinition imageCacheFolder] stringByAppendingPathComponent:control.icon]]) {
		icon = [[UIImage alloc] initWithContentsOfFile:[[DirectoryDefinition imageCacheFolder] stringByAppendingPathComponent:control.icon]];
//		if (icon.size.width > self.bounds.size.width || icon.size.height > self.bounds.size.height) {
//			CGSize size = CGSizeMake(0,0);
//			if ((icon.size.width -  self.bounds.size.width) > (icon.size.height - self.bounds.size.height)) {
//				size = CGSizeMake(self.bounds.size.width, icon.size.height * ((icon.size.width -  self.bounds.size.width) /icon.size.width ));
//			} else {
//				size = CGSizeMake(icon.size.width * ((icon.size.height -  self.bounds.size.height) /icon.size.height ), self.bounds.size.height);
//			}
//			NSLog(@"CGSize width = %d,height = %d",size.width,size.height);
//			UIGraphicsBeginImageContext(size);
//			
//			CGContextRef context = UIGraphicsGetCurrentContext();
//			CGContextTranslateCTM(context, 0.0, size.height);
//			CGContextScaleCTM(context, 1.0, -1.0);
//			
//			CGContextDrawImage(context, CGRectMake(0.0f, 0.0f, size.width, size.height), icon.CGImage);
//			
//			UIImage* scaledImage = UIGraphicsGetImageFromCurrentImageContext();
//			
//			UIGraphicsEndImageContext();
//			
//			
//			[button setImage:scaledImage forState:UIControlStateNormal];
//		} else {
			[button setImage:icon forState:UIControlStateNormal];
//		}

		
	} else {
		UIImage *buttonImage = [[UIImage imageNamed:@"button.png"] stretchableImageWithLeftCapWidth:20 topCapHeight:20];
		[button setBackgroundImage:buttonImage forState:UIControlStateNormal];
		
		buttonImage = [[UIImage imageNamed:@"buttonHighlighted.png"] stretchableImageWithLeftCapWidth:20 topCapHeight:20];
		[button setBackgroundImage:buttonImage forState:UIControlStateHighlighted];
		
		[button setFont:[UIFont boldSystemFontOfSize:18]];
		[button setTitleShadowColor:[UIColor grayColor] forState:UIControlStateNormal];
		[button setTitleShadowOffset:CGSizeMake(0, -2)];
		[button setTitle:control.label forState:UIControlStateNormal];
	}
	
}


- (void)drawRect:(CGRect)rect {
    // Drawing code
}


- (void)dealloc {
	[icon release];
	[buttonTimer release];
    [control release];
	[button release];
	
    [super dealloc];
}


@end
