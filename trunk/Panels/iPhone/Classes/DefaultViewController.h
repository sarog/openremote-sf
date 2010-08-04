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
#import "GroupController.h"
#import "ErrorViewController.h"
#import "Navigate.h"
#import "NotificationConstant.h"
#import	"LoginViewController.h"
#import "AppSettingController.h"
#import "DataBaseService.h"
#import "LogoutHelper.h"
#import "Gesture.h"
#import "TabBarItem.h"
#import "TabBarController.h"
#import "Definition.h"
#import "UpdateController.h"
#import "InitViewController.h"

@interface DefaultViewController : UIViewController {
	
	id theDelegate;
	
	InitViewController *initViewController;
	NSMutableArray *groupControllers;
	GroupController *currentGroupController;
	NSMutableDictionary *groupViewMap;
	NSMutableArray *navigationHistory;
	ErrorViewController* errorViewController;
	TabBarController *globalTabBarController;
	TabBarController *localTabBarController;
	UIView *lastSubView;
	NSMutableArray *tabBarControllers;
	NSMutableDictionary *tabBarControllerViewMap;
	UpdateController *updateController;

}

- (void)initGroups;
- (void)populateLoginView:(id)sender;
- (void)populateSettingsView:(id)sender;
- (void)performGesture:(Gesture *)gesture;
- (void)saveLastGroupIdAndScreenId;
- (BOOL)isLoadingViewGone;

@end
