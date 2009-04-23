//
//  ControlView.m
//  openremote
//
//  Created by finalist on 2/23/09.
//  Copyright 2009 __MyCompanyName__. All rights reserved.
//

#import "ControlView.h"
#import "Control.h"
#import "DirectoryDefinition.h"
#import "ServerDefinition.h"

@interface ControlView (Private) 
- (void)createButton;
- (void)controlButtonDidTaped:(id)sender;
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
	
	UIImage *buttonImage = [[UIImage imageNamed:@"button.png"] stretchableImageWithLeftCapWidth:20 topCapHeight:20];
	[button setBackgroundImage:buttonImage forState:UIControlStateNormal];
	
	buttonImage = [[UIImage imageNamed:@"buttonHighlighted.png"] stretchableImageWithLeftCapWidth:20 topCapHeight:20];
	[button setBackgroundImage:buttonImage forState:UIControlStateHighlighted];
	
	[button setFont:[UIFont boldSystemFontOfSize:18]];
	[button setTitleShadowColor:[UIColor grayColor] forState:UIControlStateNormal];
	[button setTitleShadowOffset:CGSizeMake(0, -2)];
	
	[button addTarget:self action:@selector(controlButtonDidTaped:) forControlEvents:UIControlEventTouchUpInside];
	if (control.icon) {
		UIImage *icon = [[UIImage alloc] initWithContentsOfFile:[[DirectoryDefinition imageCacheFolder] stringByAppendingPathComponent:control.icon]];
		[button setImage:icon forState:UIControlStateNormal];
		[icon	 release];
	} else {
		[button setTitle:control.label forState:UIControlStateNormal];
	}
	[self addSubview:button];
	
}

//Invoked when the button touch up inside
- (void) controlButtonDidTaped:(id)sender {
	// DENNIS: You probably want to move this code to a 'Server' model class or something like that! Does not belong in a view class.

	NSString *location = [[NSString alloc] initWithFormat:[ServerDefinition eventHandleRESTUrl]];
	NSURL *url = [[NSURL alloc]initWithString:[location stringByAppendingFormat:@"?id=%d",control.eventID]];
	//assemble put request 
	NSMutableURLRequest *request = [[NSMutableURLRequest alloc] init];
	[request setURL:url];
	[request setHTTPMethod:@"POST"];

	URLConnectionHelper *connection = [[URLConnectionHelper alloc]initWithRequest:request  delegate:self];
										   
	[location release];
	[url	 release];
	[request release];
	[connection release];
	
}

#pragma mark delegate method of NSURLConnection
//Shows alertView when url connection failtrue
- (void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error {
	UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"ERROR OCCUR" message:error.localizedDescription  delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
	[alert show];
	[alert release];
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
}

//override layoutSubviews method of UIView 
- (void)layoutSubviews {	
	[button setFrame:[self bounds]];
}


- (void)drawRect:(CGRect)rect {
    // Drawing code
}


- (void)dealloc {
    [control release];
	[button release];
    [super dealloc];
}


@end
