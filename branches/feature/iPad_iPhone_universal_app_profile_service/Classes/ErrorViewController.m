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

@interface ErrorViewController (Private)
- (void)gotoSettings:(id)sender;
- (void)goBack:(id)sender;
@end

@implementation ErrorViewController

- (id)initWithErrorTitle:(NSString *)title message:(NSString *)message{
	BOOL isIPad = [UIScreen mainScreen].bounds.size.width == 768;
	if (self = [super initWithNibName: isIPad ? @"ErrorViewController~iPad" : @"ErrorViewController~iPhone" bundle:nil]) {
		UIToolbar* toolbar = [[UIToolbar alloc] initWithFrame:CGRectMake(0, 0, self.view.bounds.size.width, 44)];
		[toolbar setAutoresizingMask:UIViewAutoresizingFlexibleWidth];
		items = [[NSMutableArray alloc] init];
		
		UIBarButtonItem *item = [[UIBarButtonItem alloc] initWithTitle:@"Settings" 
																														 style:UIBarButtonItemStyleBordered 
																														target:self 
																														action:@selector(gotoSettings:)];
		[items addObject: item];
		[item release];
		
		[toolbar setItems:items];
		
		[titleLabel setText:title];
		[msgLabel setText:message];

		[self.view addSubview:toolbar];
		
		[toolbar release];
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


- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)toInterfaceOrientation {
	return YES;
}


- (void)dealloc {
	[items release];
	[titleLabel release];
	[msgLabel release];
	
	[super dealloc];
}


@end
