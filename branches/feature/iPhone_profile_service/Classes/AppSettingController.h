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


#import <UIKit/UIKit.h>
#import "ServerAutoDiscoveryController.h"
#import "UpdateController.h"
#import "FileUtils.h"

/**
 * Render the UI of AppSetting view and provide functions of setting about panel client.
 */
@interface AppSettingController : UITableViewController <UITextFieldDelegate> {
	NSString *pathToUserCopyOfPlist;
	BOOL autoDiscovery;
	NSMutableArray *serverArray;
	NSIndexPath *currentSelectedServerIndex;
	ServerAutoDiscoveryController *autoDiscoverController;
	UIBarButtonItem *done;
	UIBarButtonItem *cancel;
	BOOL isEditing;
	UIActivityIndicatorView *loadingView;
	UpdateController *updateController;
	NSTimer *getAutoServersTimer;
	UIActivityIndicatorView *spinner;
	UITextField *portField;
	
}
@end
