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
#import <UIKit/UIKit.h>
#import	"LoginViewController.h"
#import "GroupController.h"
#import "ErrorViewController.h"
#import "Navigate.h"
#import "NotificationConstant.h"
#import "AppSettingController.h"
#import "LogoutHelper.h"
#import "Gesture.h"
#import "TabBarItem.h"
#import "Definition.h"
#import "UpdateController.h"
#import "InitViewController.h"

/**
 * It's responsible for controlling rendering of all views related to client.
 * Its view is the root view container of all views related to client.
 */
@interface DefaultViewController : UIViewController <LoginViewControllerDelegate> {	
	id theDelegate;
	
	InitViewController *initViewController;
	GroupController *currentGroupController;
	NSMutableArray *navigationHistory;
	ErrorViewController* errorViewController;
	UpdateController *updateController;
}

- (id)initWithDelegate:(id)delegate;

- (void)initGroups;

/**
 * Starting polling for currentGroupController.
 */
- (void)refreshPolling;

/**
 * Prompts the user to enter a valid user name and password
 */
- (void)populateLoginView:(id)sender;

/**
 * Prompts the user to setting.
 */
- (void)populateSettingsView:(id)sender;

/**
 * Perform gesture action. Currently, the gesture should be one action of sliding from left to right, 
 * sliding from right to left, sliding from top to bottom and sliding from bottom to top.
 */
- (void)performGesture:(Gesture *)gesture;

/** 
 * Save id of current group and current screen while initializing groupController 
 * and navigating for recovery of lastScreenView in RootViewController.
 */
- (void)saveLastGroupIdAndScreenId;

/**
 * Check if the InitView and errorView are gone.
 */
- (BOOL)isLoadingViewGone;

/**
 * Check if the downloading panel.xml and parsing process are finished.
 */
- (BOOL)isAppLaunching;

@end
