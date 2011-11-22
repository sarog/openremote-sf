/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2011, OpenRemote Inc.
 *
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
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

// To appSetting View with clicking setting button in errorView.
- (void)gotoSettings:(id)sender {
	[[NSNotificationCenter defaultCenter] postNotificationName:NotificationPopulateSettingsView object:nil];
}

- (void)goBack:(id)sender {
	[[NSNotificationCenter defaultCenter] postNotificationName:NotificationNavigateBack object:nil];
}

// Enable rotating of errorView.
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
