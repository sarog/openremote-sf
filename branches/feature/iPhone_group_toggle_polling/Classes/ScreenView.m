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


#import "ScreenView.h"
#import "Control.h"
#import "ControlView.h"
#import "LayoutContainer.h"
#import "LayoutContainerView.h"
#import "ServerDefinition.h"
#import "ViewHelper.h"

@interface ScreenView (Private) 
- (void)createLayout;
- (void)requestComponentsStatus;
- (void)doPolling;
- (void)cancelPolling;
@end

@implementation ScreenView

@synthesize screen, layoutContainerViews, pollingComponentsMap, isPolling, pollingComponentsIds;

//override the constractor
- (id)initWithFrame:(CGRect)frame {
    if (self = [super initWithFrame:frame]) {
			layoutContainerViews = [[NSMutableArray alloc] init];
			pollingComponentsMap = [[NSMutableDictionary alloc] init];
			isPolling = NO;
    }
    return self;
}

//Set screen *and* render its layout 
- (void)setScreen:(Screen *)s {
	[s retain];
	[screen release];
	screen = s;
	[self createLayout];
}
	
//create each layout container in screen
- (void)createLayout {
//	if (layoutContainerViews.count != 0) {
//		[layoutContainerViews release];
//		layoutContainerViews = [[NSMutableArray alloc] init];
//	}


	for (LayoutContainer *layout in screen.layouts) { 
		LayoutContainerView *layoutView = [LayoutContainerView buildWithLayoutContainer:layout];

		[self addSubview:layoutView];
		[layoutContainerViews addObject:layoutView];

		for (ControlView *controlView in layoutView.pollingComponents) {
			[pollingComponentsMap setObject:controlView forKey:[NSString stringWithFormat:@"%d",[controlView getControlId]]];
		}
		
		[layoutView release];
	}

	pollingComponentsIds = [[pollingComponentsMap allKeys] componentsJoinedByString:@","];
	
	[self requestComponentsStatus];
}


//override layoutSubviews method of UIView, In order to resize the ControlView when add subview
// Only in this time we can know this view's size
- (void)layoutSubviews {
	//[screenNameLabel setFrame:CGRectMake(self.bounds.origin.x, self.bounds.origin.y, self.bounds.size.width, 20)];

	//int h = self.bounds.size.height/screen.rows;	
//	//int h = (self.bounds.size.height-20)/screen.rows;
//	int w = self.bounds.size.width/screen.cols;
//	
//	
//	for (ControlView *controlView in controlViews) {
//		Control *control = [controlView control];
//		[controlView setFrame:CGRectInset(CGRectMake(control.x*w, control.y*h, w*control.width, h*control.height),roundf(w*0.1),  roundf(h*0.1))];
//		//[controlView setFrame:CGRectInset(CGRectMake(control.x*w, (control.y*h +20), w*control.width, h*control.height),roundf(w*0.1), roundf(h*0.1))];
//		[controlView layoutSubviews];
	//}
}

- (void)requestComponentsStatus {
	[self cancelPolling];
	
	NSString *location = [[NSString alloc] initWithFormat:[ServerDefinition statusRESTUrl]];
	NSURL *url = [[NSURL alloc]initWithString:[location stringByAppendingFormat:@"/%@",pollingComponentsIds]];
	NSLog([location stringByAppendingFormat:@"/%@",pollingComponentsIds]);
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

- (void)doPolling {
	isPolling = YES;
	NSString *location = [[NSString alloc] initWithFormat:[ServerDefinition pollingRESTUrl]];
	NSURL *url = [[NSURL alloc]initWithString:[location stringByAppendingFormat:@"/%@",pollingComponentsIds]];
	NSLog([location stringByAppendingFormat:@"/%@",pollingComponentsIds]);
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

- (void)cancelPolling {
	isPolling = NO;
	
}


- (void)handleServerErrorWithStatusCode:(int) statusCode {
	if (statusCode != 200) {
		NSString *errorMessage = nil;
		switch (statusCode) {
			case 404:
				errorMessage = [NSString stringWithString:@"The command was sent to an invalid URL."];
				break;
			case 500:
				errorMessage = [NSString stringWithString:@"Error in controller. Please check controller log."];
				break;
		}
		if (!errorMessage) {
			errorMessage = [NSString stringWithFormat:@"Occured unknown error, satus code is @d",statusCode];
		}
		[ViewHelper showAlertViewWithTitle:@"Send Request Error" Message:errorMessage];
		isPolling = NO;
	} 

}

#pragma mark delegate method of NSURLConnection
- (void) definitionURLConnectionDidFailWithError:(NSError *)error {
	//if (!isError) {
	UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Error Occured" message:[error localizedDescription] delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
	[alert show];
	[alert release];
	//isError = YES;
	//} 
}

//Do polling when the request successful
- (void)definitionURLConnectionDidFinishLoading:(NSData *)data {
	NSString *result = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];

	if (isPolling) {
		
	}
	
	[self doPolling];
	[result release];
	NSLog(@"definitionURLConnectionDidFinishLoading");
}

- (void)definitionURLConnectionDidReceiveResponse:(NSURLResponse *)response {
	NSHTTPURLResponse *httpResp = (NSHTTPURLResponse *)response;
	NSLog(@"statusCode is %d", [httpResp statusCode]);
	
	[self handleServerErrorWithStatusCode:[httpResp statusCode]];
}

- (void)dealloc {
	[screen release];
	[layoutContainerViews release];
	[pollingComponentsMap release];
	[pollingComponentsIds release];
	[super dealloc];
}


@end
