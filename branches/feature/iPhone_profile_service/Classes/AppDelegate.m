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

/*
  * This is the entrypoint of the application.
  *  After application have been started applicationDidFinishLaunching method will be called.
  */

#import "AppDelegate.h"

#import "ScreenViewController.h"
#import "GroupController.h"
#import "Group.h"
#import "Definition.h"
#import "InitViewController.h"
#import "UpdateController.h"
#import "ViewHelper.h"
#import "Navigate.h"
#import "NotificationConstant.h"
#import	"LoginViewController.h"
#import "AppSettingController.h"
#import "DataBaseService.h"
#import "User.h"
#import "LogoutHelper.h"

//Private method declare
@interface AppDelegate (Private)
- (void)updateDidFinished;
- (void)didUpadted;
- (void)didUseLocalCache:(NSString *)errorMessage;
- (void)didUpdateFail:(NSString *)errorMessage;
- (void)navigateFromNotification:(NSNotification *)notification;
- (void)populateLoginView:(id)sender;
- (void)populateSettingsView:(id)sender;
- (void)refreshView:(id)sender;
- (BOOL)navigateToGroup:(int)groupId toScreen:(int)screenId;
- (BOOL)navigateToScreen:(int)to;
- (BOOL)navigateToPreviousScreen;
- (BOOL)navigateToNextScreen;
- (void)logout;
- (void)navigateBackwardInHistory;
- (BOOL)navigateTo:(Navigate *)navi;
- (void)navigateToWithHistory:(Navigate *)navi;
@end

@implementation AppDelegate

//Entry point method
- (void)applicationDidFinishLaunching:(UIApplication *)application {    
	// Default window for the app
	window = [[UIWindow alloc] initWithFrame:[UIScreen mainScreen].bounds];
	[window makeKeyAndVisible];
	
	// Create a default view that won't be overlapped by status bar.
	// status bar is 20px high and on the top of window.
	// all the visible view contents will be shown inside this container.
	defaultView = [[UIView alloc] initWithFrame:CGRectMake(0, 20, 320, 460) ];
	[window addSubview:defaultView];
	
	//Init the loading view
	initViewController = [[InitViewController alloc] init];
	[defaultView addSubview:initViewController.view];
	[Definition sharedDefinition].loading = [initViewController label];
	//Init UpdateController and set delegate to this class, it have three delegate methods
    // - (void)didUpadted;
    // - (void)didUseLocalCache:(NSString *)errorMessage;
    // - (void)didUpdateFail:(NSString *)errorMessage;
	updateController = [[UpdateController alloc] initWithDelegate:self];
	[updateController checkConfigAndUpdate];
	groupControllers = [[NSMutableArray alloc] init]; 
	groupViewMap = [[NSMutableDictionary alloc] init];
	navigationHistory = [[NSMutableArray alloc] init];
	
	// Load logined iphone user last time.
	DataBaseService *dbService = [DataBaseService sharedDataBaseService];
	User *user = [dbService findLastLoginUser];
	[Definition sharedDefinition].username = user.username;
	[Definition sharedDefinition].password = user.password;
}


// this method will be called after UpdateController give a callback.
- (void)updateDidFinished {
	NSLog(@"----------updateDidFinished------");
	[initViewController.view removeFromSuperview];
	NSArray *groups = [[Definition sharedDefinition] groups];
	
	GroupController *defaultGroupController = nil;
	if (groups.count > 0) {
		GroupController *gc = [[GroupController alloc] initWithGroup:((Group *)[groups objectAtIndex:0])];
		[groupControllers addObject:gc];
		[groupViewMap setObject:gc.view forKey:[NSString stringWithFormat:@"%d", gc.group.groupId]];
		defaultGroupController = gc;	
	}

	//navigationController = [[UINavigationController alloc] initWithRootViewController:defaultGroupController];
	//[window addSubview:navigationController.view];
	currentGroupController = defaultGroupController;
	[defaultView addSubview:defaultGroupController.view];
	//[defaultGroupController release];
	[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(navigateFromNotification:) name:NotificationNavigateTo object:nil];
	[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(populateLoginView:) name:NotificationPopulateCredentialView object:nil];
	[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(populateSettingsView:) name:NotificationPopulateSettingsView object:nil];
	[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(refreshView:) name:NotificationRefreshGroupsView object:nil];	
	[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(navigateToScreen:) name:NotificationRefreshGroupsView object:nil];	
	
}

- (void)navigateFromNotification:(NSNotification *)notification {
	
	Navigate *navi = (Navigate *)[notification object];
	[self navigateToWithHistory:navi];
	
}

- (void)navigateToWithHistory:(Navigate *)navi {	
	navi.fromGroup = currentGroupController.group.groupId;
	navi.fromScreen = [currentGroupController currentScreenId];
	
	if ([self navigateTo:navi]) {
		[navigationHistory addObject:navi];
	}
	
	NSLog(@"navi history count = %d", navigationHistory.count);
}

// Returned BOOL value is whether to save history
// if YES, save history
// if NO, don't save history
- (BOOL)navigateTo:(Navigate *)navi {
	
	if (navi.toGroup > 0 ) {	                //toGroup & toScreen
		return [self navigateToGroup:navi.toGroup toScreen:navi.toScreen];
	} 
	
	else if (navi.toScreen > 0) {             //toScreen in current group
		return [self navigateToScreen:navi.toScreen];
	} 
	
	else if (navi.isSetting) {								//toSetting
		[self populateSettingsView:nil];
		return NO;
	} 
	
	else if (navi.isPreviousScreen) {					//toPreviousScreen
		return [self navigateToPreviousScreen];
	}
	
	else if (navi.isNextScreen) {							//toNextScreen
		return [self navigateToNextScreen];
	}
	
	else if (navi.isBack) {										//toBack
		[self navigateBackwardInHistory]; 
		return NO;
	} 
	
	else if (navi.isLogin) {									//toLogin
		[self populateLoginView:nil];
		return NO;
	} 
	
	else if (navi.isLogout) {									//toLogout
		[self logout];
		return NO;
	}
	return NO;
}

- (BOOL)navigateToGroup:(int)groupId toScreen:(int)screenId {
	GroupController *targetGroupController = nil;	
	BOOL notItSelf = groupId != currentGroupController.group.groupId;
	
	//if screenId is specified, and is not current group, jump to that group
	if (groupId > 0 && notItSelf) {
		//find in cache first
		for (GroupController *gc in groupControllers) {
			if (gc.group.groupId == groupId) {
				targetGroupController = gc;
			}
		}
		//if not found in cache, create one
		if (targetGroupController == nil) {
			Group *group = [[Definition sharedDefinition] findGroupById:groupId];
			if (group) {
				targetGroupController = [[GroupController alloc] initWithGroup:group];
				[groupControllers addObject:targetGroupController];
				[groupViewMap setObject:targetGroupController.view forKey:[NSString stringWithFormat:@"%d", group.groupId]];
			} else {
				return NO;
			}
		}
		
		[currentGroupController stopPolling];
		[targetGroupController startPolling];
		
		[UIView beginAnimations:nil context:nil];
		[UIView setAnimationDuration:1];
		
		// calculate animation curl up or down
		int currentIndex = 0;
		int targetIndex = 0;
		for (int i = 0; i<groupControllers.count; i++) {
			GroupController *gc = (GroupController *)[groupControllers objectAtIndex:i];
			if (gc.group.groupId == currentGroupController.group.groupId) {
				currentIndex = i;
			}
			if (gc.group.groupId == targetGroupController.group.groupId) {
				targetIndex = i;
			}			
		}
		BOOL forward = targetIndex > currentIndex;
		
		if (forward) {
			[UIView setAnimationTransition:UIViewAnimationTransitionCurlUp forView:defaultView cache:YES];
		} else {
			[UIView setAnimationTransition:UIViewAnimationTransitionCurlDown forView:defaultView cache:YES];
		}

		//[navigationController.view removeFromSuperview];
		//navigationController = [[UINavigationController alloc] initWithRootViewController:targetGroupController];
		UIView *view = [groupViewMap objectForKey:[NSString stringWithFormat:@"%d", groupId]];
		

		[currentGroupController.view removeFromSuperview];
		[defaultView addSubview:view];

		[UIView commitAnimations];
		
		currentGroupController = targetGroupController;
	}
	
	//if screenId is specified, jump to that screen
	if (screenId > 0) {
		return [currentGroupController switchToScreen:screenId];
	}
	
	return YES;
}

- (void)logout {
	if ([Definition sharedDefinition].password) {
		LogoutHelper *logout = [[LogoutHelper alloc] init];
		[logout requestLogout];
		[logout release];
	}	
}

- (void)navigateBackwardInHistory {
	if (navigationHistory.count > 0) {		
		Navigate *backward = (Navigate *)[navigationHistory lastObject];
		if (backward.toGroup > 0 || backward.toScreen > 0 || backward.isPreviousScreen || backward.isNextScreen) {
			[self navigateToGroup:backward.fromGroup toScreen:backward.fromScreen];
		} else {
			[self navigateTo:backward];
		}
		//remove current navigation, navigate backward
		[navigationHistory removeLastObject];
	}
}

- (BOOL)navigateToScreen:(int)to {
		return [currentGroupController switchToScreen:to];
}

- (BOOL)navigateToPreviousScreen {
		return [currentGroupController previousScreen];
}

- (BOOL)navigateToNextScreen {
		return [currentGroupController nextScreen];
}


//prompts the user to enter a valid user name and password
- (void)populateLoginView:(id)sender {
	LoginViewController *loginController = [[LoginViewController alloc]init];
	UINavigationController *loginNavController = [[UINavigationController alloc] initWithRootViewController:loginController];
	[currentGroupController presentModalViewController:loginNavController animated:YES];
	[loginController release];
	[loginNavController release];
}


- (void)populateSettingsView:(id)sender {
	AppSettingController *settingController = [[AppSettingController alloc]init];
	UINavigationController *settingNavController = [[UINavigationController alloc] initWithRootViewController:settingController];
	[currentGroupController presentModalViewController:settingNavController animated:YES];
	[settingController release];
	[settingNavController release];

}

- (void)refreshView:(id)sender {
	for (UIView *view in defaultView.subviews) {
		[view removeFromSuperview];
	}
	[groupControllers removeAllObjects];
	[groupViewMap removeAllObjects];
	[currentGroupController stopPolling];
	currentGroupController = nil;
	
	GroupController *defaultGroupController = nil;
	NSArray *groups = [[Definition sharedDefinition] groups];
	if (groups.count > 0) {
		GroupController *gc = [[GroupController alloc] initWithGroup:((Group *)[groups objectAtIndex:0])];
		[groupControllers addObject:gc];
		[groupViewMap setObject:gc.view forKey:[NSString stringWithFormat:@"%d", gc.group.groupId]];
		defaultGroupController = gc;	
	}
	
	currentGroupController = defaultGroupController;
	[defaultView addSubview:defaultGroupController.view];
	
}


#pragma mark delegate method of updateController
- (void)didUpadted {
	[self updateDidFinished];
}

- (void)didUseLocalCache:(NSString *)errorMessage {	
	[self updateDidFinished];
	[[ViewHelper alloc] showAlertViewWithTitleAndSettingNavigation:@"Warning" Message:[errorMessage stringByAppendingString:@" Using cached content."]];
}

- (void)didUpdateFail:(NSString *)errorMessage {
	[ViewHelper showAlertViewWithTitle:@"Warning" Message:errorMessage];
	[self updateDidFinished];
}

- (void)dealloc {
	[updateController release];
	[defaultView release];
	[navigationController release];
	[groupControllers release];
	[window release];
	[groupViewMap release];
	[navigationHistory release];
	
	[super dealloc];
}


@end
