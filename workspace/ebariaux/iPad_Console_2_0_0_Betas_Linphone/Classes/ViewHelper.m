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


#import "ViewHelper.h"
#import "NotificationConstant.h"

@implementation ViewHelper

+ (void)showAlertViewWithTitle:(NSString *)title Message:(NSString *)message  {
	[[NSNotificationCenter defaultCenter] postNotificationName:NotificationHideLoading object:nil];
	UIAlertView *alert = [[UIAlertView alloc] initWithTitle:title message:message delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
	[alert show];
	[alert release];
}

- (void)showAlertViewWithTitleAndSettingNavigation:(NSString *)title Message:(NSString *)message  {
	[[NSNotificationCenter defaultCenter] postNotificationName:NotificationHideLoading object:nil];
    [self retain]; // We want to stick around as we set ourself as delegate
	UIAlertView *alert = [[UIAlertView alloc] initWithTitle:title message:message delegate:self cancelButtonTitle:@"OK" otherButtonTitles:nil];
	[alert addButtonWithTitle:@"Settings"];
	[alert show];
	[alert release];
}

// Delegate method of UIAlertViewDelegate.
// Called when a button is clicked. The view will be automatically dismissed after this call returns
- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex {
	if (buttonIndex == 1) {//setting button
		[[NSNotificationCenter defaultCenter] postNotificationName:NotificationPopulateSettingsView object:nil];
	}
}

- (void)alertView:(UIAlertView *)alertView didDismissWithButtonIndex:(NSInteger)buttonIndex {
    alertView.delegate = nil; // Make sure we won't receive any further message after we release ourself
    [self release];
}

@end
