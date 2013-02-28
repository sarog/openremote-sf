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

#import "ErrorViewController.h"
#import "NotificationConstant.h"
#import "Navigate.h"
#import "UIViewUtil.h"
#import "ClippedUIImage.h"
#import "XMLEntity.h"

@interface ErrorViewController (Private)
- (void)gotoSettings:(id)sender;
- (void)goBack:(id)sender;
@end

static const int ERROR_IMAGE_FIXED_WIDTH = 160;
static const int ERROR_IMAGE_FIXED_HEIGHT = 160;

@implementation ErrorViewController

- (id)initWithErrorTitle:(NSString *)title message:(NSString *)message{
	if (self = [super init]) {
		UIToolbar* toolbar = [[UIToolbar alloc] initWithFrame:CGRectMake(0, 0, 320, 44)];
		items = [[NSMutableArray alloc] init];
		
		UIBarButtonItem *item = [[UIBarButtonItem alloc] initWithTitle:@"Back" style:UIBarButtonItemStyleBordered target:self action:@selector(goBack:)];
		[items addObject: item];
		[item release];
		
		item = [[UIBarButtonItem alloc] initWithTitle:@"Settings" style:UIBarButtonItemStyleBordered target:self action:@selector(gotoSettings:)];
		[items addObject: item];
		[item release];
		
		[toolbar setItems:items];
		
		UIView *bgview = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 320, 460)];
		[bgview setBackgroundColor:[UIColor whiteColor]];
		[self setView:bgview];
		UIImage *errorImage = [UIImage imageNamed:@"repair.png"];
		//UIButton *errorImageView = [[UIButton alloc] initWithFrame:CGRectMake((320-errorImage.size.width)/2.0, 100, errorImage.size.width , errorImage.size.height)];
		//[errorImageView setBackgroundImage:errorImage forState:UIControlStateNormal];
		UIView *errorImageDependingOnView = [[UIView alloc] initWithFrame:CGRectMake((320-ERROR_IMAGE_FIXED_WIDTH)/2.0, 100, ERROR_IMAGE_FIXED_WIDTH , ERROR_IMAGE_FIXED_HEIGHT)];
		UIImageView *errorImageView = [UIViewUtil clippedUIImageViewWith:errorImage dependingOnUIView:errorImageDependingOnView uiImageAlignToUIViewPattern:IMAGE_ABSOLUTE_ALIGN_TO_VIEW isUIImageFillUIView:NO];
		
		titleLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, 280, 320, 30)];
		[titleLabel setText:title];
		[titleLabel setBackgroundColor:[UIColor clearColor]];
		[titleLabel setTextColor:[UIColor grayColor]];
		[titleLabel setFont:[UIFont boldSystemFontOfSize:20]];
		[titleLabel setTextAlignment:UITextAlignmentCenter];
		
		msgLabel = [[UILabel alloc] initWithFrame:CGRectMake(20, 310, 280, 60)];
		[msgLabel setText:message];
		[msgLabel setBackgroundColor:[UIColor clearColor]];
		[msgLabel setTextColor:[UIColor grayColor]];
		[msgLabel setFont:[UIFont boldSystemFontOfSize:13]];
		[msgLabel setTextAlignment:UITextAlignmentCenter];
		[msgLabel setNumberOfLines:3];
		
		[self.view addSubview:titleLabel];
		[self.view addSubview:msgLabel];
		[self.view addSubview:errorImageView];
		[self.view addSubview:toolbar];
		
		[toolbar release];
		[titleLabel release];
		[msgLabel release];
		[errorImageView release];
	}
	return self;
}

- (void)setTitle:(NSString *)title message:(NSString *)message {
	[titleLabel setText:title];
	[msgLabel setText:message];
}

- (void)gotoSettings:(id)sender {
	[[NSNotificationCenter defaultCenter] postNotificationName:NotificationPopulateSettingsView object:nil];
}

- (void)goBack:(id)sender {
	[[NSNotificationCenter defaultCenter] postNotificationName:NotificationNavigateBack object:nil];
}


- (void)dealloc {
	[items release];
	
	[super dealloc];
}


@end
