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

#import "DefaultViewController.h"
static NSString *TABBAR_SCALE_GLOBAL = @"global";
static NSString *TABBAR_SCALE_LOCAL = @"local";
static NSString *TABBAR_SCALE_NONE = @"none";

@interface DefaultViewController (Private)


- (void)navigateFromNotification:(NSNotification *)notification;
- (void)refreshView:(id)sender;
- (BOOL)navigateToGroup:(int)groupId toScreen:(int)screenId;
- (BOOL)navigateToScreen:(int)to;
- (BOOL)navigateToPreviousScreen;
- (BOOL)navigateToNextScreen;
- (void)logout;
- (void)navigateBackwardInHistory:(id)sender;
- (BOOL)navigateTo:(Navigate *)navi;
- (void)navigateToWithHistory:(Navigate *)navi;


@end


@implementation DefaultViewController


- (id)initWithDelegate:(id)delegate {
    if (self = [super init]) {	
			theDelegate = delegate;
			groupControllers = [[NSMutableArray alloc] init]; 
			groupViewMap = [[NSMutableDictionary alloc] init];
			tabBarControllers = [[NSMutableArray alloc] init];
			tabBarControllerViewMap = [[NSMutableDictionary alloc] init];
			navigationHistory = [[NSMutableArray alloc] init];
			
			[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(navigateFromNotification:) name:NotificationNavigateTo object:nil];
			[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(populateLoginView:) name:NotificationPopulateCredentialView object:nil];
			[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(populateSettingsView:) name:NotificationPopulateSettingsView object:nil];
			[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(refreshView:) name:NotificationRefreshGroupsView object:nil];	
			[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(navigateBackwardInHistory:) name:NotificationNavigateBack object:nil];	
    }
    return self;
}



- (void)loadView {
	// Create a default view that won't be overlapped by status bar.
	// status bar is 20px high and on the top of window.
	// all the visible view contents will be shown inside this container.
	[self setView:[[UIView alloc] initWithFrame:CGRectMake(0, 20, 320, 460) ]];
}


- (void)initGroups {
	NSArray *groups = [[Definition sharedDefinition] groups];
	
	if (groups.count > 0) {
		
		/**
		 * About recovering to last group and screen.
		 * I)Currently, there are two use cases which relate with recovery mechanism.
		 * 1) While setting.
		 *    DESC: User presses setting item in tabbar or in certain screen when user had switch
		 *    to certain screen of certain group. After Uesr done setting, the app must switch to 
		 *    the screen which before user pressing setting.
		 *    
		 * 2) While switching to groupmember controller.
		 *    DESC: If current controller down, app will switch to groupmember controller of crashed controller.
		 *    However, the process is tranparent. That means user won't feel controller-switch. So, the app must
		 *    keep the same screen before and after switching controller.
		 *
		 * II)Technically speaking, app will save the groupId and screenId when user switch to certain group and screen 
		 * or navigage to certain screen. The follows are in detail:
		 *    1)Navigate action: Append code in self method *navigateToWithHistory*
		 *    2)Scroll screen action: Apend code in method *setViewControllers* and *updateViewForCurrentPageAndBothSides*
		 *    of class PaginationController.
		 *    3)Finished the initGroups: Append code in tail of self method *initGroups*:[self saveLastGroupIdAndScreenId];
		 *
		 * III)The saved groupId and screenId will be used in following situation:
		 *    While app initializing groups(see method initGroups) in current classs, app gets groupId and screenId stored, and then switch
		 *    to the destination described by groupId and screenId.
		 */
		//Begin: Recover the last group
		NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
		GroupController *gc = nil;
		if ([userDefaults objectForKey:@"lastGroupId"]) {
			int lastGroupId = [[userDefaults objectForKey:@"lastGroupId"] intValue];
			Group *lastGroup = nil;
			for (Group *tempGroup in groups) {
				if (lastGroupId == tempGroup.groupId) {
					lastGroup = tempGroup;
					break;
				}
			}
			if (lastGroup) {
				gc = [[GroupController alloc] initWithGroup:lastGroup];
			} else {
				gc = [[GroupController alloc] initWithGroup:((Group *)[groups objectAtIndex:0])];
			}
		} else {
			gc = [[GroupController alloc] initWithGroup:((Group *)[groups objectAtIndex:0])];
		}
		//End: Recover the last group
		
		[groupControllers addObject:gc];
		[groupViewMap setObject:gc.view forKey:[NSString stringWithFormat:@"%d", gc.group.groupId]];	
		currentGroupController = [gc retain];
		
		TabBar *localTabBar = currentGroupController.group.tabBar;
		// local tabBar
		if (localTabBar) {
			localTabBarController = [[TabBarController alloc] initWithGroupController:currentGroupController tabBar:localTabBar];
			tabBarScale = TABBAR_SCALE_LOCAL;
			[self.view addSubview:localTabBarController.view];
		} else 
			// global tabBar
			if ([[Definition sharedDefinition] tabBar]) {
				globalTabBarController = [[TabBarController alloc] initWithGroupController:currentGroupController tabBar:[[Definition sharedDefinition] tabBar]];
				tabBarScale = TABBAR_SCALE_GLOBAL;
				[self.view addSubview:globalTabBarController.view];
			} else {
				tabBarScale = TABBAR_SCALE_NONE;
				[self.view addSubview:currentGroupController.view];
			}
		
		//Begin: Recover the last screen	
		int lastScreenId = [[userDefaults objectForKey:@"lastScreenId"] intValue];
		if (lastScreenId > 0) {
			[currentGroupController switchToScreen:lastScreenId];
		}
		//End: Recover the last screen
		// ReSave last groupId and screenId
		[self saveLastGroupIdAndScreenId];
	} else {		
		errorViewController = [[ErrorViewController alloc] initWithErrorTitle:@"No Group Found" message:@"Please associate screens with group or reset setting."];
		[self.view addSubview:errorViewController.view];		
	}
	
}

- (void)navigateFromNotification:(NSNotification *)notification {
	if (notification) {
		Navigate *navi = (Navigate *)[notification object];
		[self navigateToWithHistory:navi];
	}
}

- (void)navigateToWithHistory:(Navigate *)navi {
	if (currentGroupController.group) {
		navi.fromGroup = currentGroupController.group.groupId;
		navi.fromScreen = [currentGroupController currentScreenId];
	} else {
		return;
	}
	
	if ([self navigateTo:navi]) {
		NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
		[userDefaults setObject:[NSString stringWithFormat:@"%d",currentGroupController.group.groupId] forKey:@"lastGroupId"];
		[userDefaults setObject:[NSString stringWithFormat:@"%d",[currentGroupController currentScreenId]] forKey:@"lastScreenId"];		
		[navigationHistory addObject:navi];
	}
	
	NSLog(@"navi history count = %d", navigationHistory.count);
}

- (void) saveLastGroupIdAndScreenId {
	NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
	[userDefaults setObject:[NSString stringWithFormat:@"%d",currentGroupController.group.groupId] forKey:@"lastGroupId"];
	[userDefaults setObject:[NSString stringWithFormat:@"%d",[currentGroupController currentScreenId]] forKey:@"lastScreenId"];
	NSLog(@"saveLastGroupIdAndScreenId : groupID %d, screenID %d", [[userDefaults objectForKey:@"lastGroupId"] intValue], [[userDefaults objectForKey:@"lastScreenId"] intValue]);
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
		[self navigateBackwardInHistory:nil]; 
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
	BOOL notItSelf = groupId != [currentGroupController groupId];
	
	//if screenId is specified, and is not current group, jump to that group
	if (groupId > 0 && notItSelf) {
		//find in cache first
		NSLog(@"groupControllers size = %d",groupControllers.count);
		for (GroupController *gc in groupControllers) {
			if ([gc groupId] == groupId) {
				targetGroupController = gc;
				break;
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
		
		if ([TABBAR_SCALE_GLOBAL isEqualToString:tabBarScale]) {
			[globalTabBarController.view removeFromSuperview];
		} else if([TABBAR_SCALE_LOCAL isEqualToString:tabBarScale]) {
			[localTabBarController.view removeFromSuperview];
		} else if([TABBAR_SCALE_NONE isEqualToString:tabBarScale]) {
			[currentGroupController.view removeFromSuperview];
		}
		UIView *view = [groupViewMap objectForKey:[NSString stringWithFormat:@"%d", groupId]];
		tabBarScale = TABBAR_SCALE_NONE;
		
		// begin tabBar and view(local or global)
		//if global tabbar exists
		if (globalTabBarController) {
			view = globalTabBarController.view;
			tabBarScale = TABBAR_SCALE_GLOBAL;
		}		
		//if local tabbar exists
		if (targetGroupController.group.tabBar) {
			BOOL findCachedTargetTabBarController = NO;
			for (TabBarController *tempTargetTabBarController in tabBarControllers) {
				if (tempTargetTabBarController.groupController.group.groupId == targetGroupController.group.groupId) {
					localTabBarController = tempTargetTabBarController;
					findCachedTargetTabBarController = YES;
					break;
				}
			}
			if (!findCachedTargetTabBarController) {
				localTabBarController = [[TabBarController alloc] initWithGroupController:targetGroupController tabBar:targetGroupController.group.tabBar];
				[tabBarControllers addObject:localTabBarController];
				[tabBarControllerViewMap setObject:localTabBarController.view forKey:[NSString stringWithFormat:@"%d", localTabBarController.groupController.group.groupId]];
			}
			tabBarScale = TABBAR_SCALE_LOCAL;
			view = [tabBarControllerViewMap objectForKey:[NSString stringWithFormat:@"%d", localTabBarController.groupController.group.groupId]];
		}
		// end tabBar and view(local or global)
		
		[currentGroupController stopPolling];
		[targetGroupController startPolling];
		
		[UIView beginAnimations:nil context:nil];
		[UIView setAnimationDuration:1];
		
		// calculate animation curl up or down
		int currentIndex = 0;
		int targetIndex = 0;
		for (int i = 0; i<groupControllers.count; i++) {
			GroupController *gc = (GroupController *)[groupControllers objectAtIndex:i];
			if ([gc groupId] == [currentGroupController groupId]) {
				currentIndex = i;
			}
			if ([gc groupId] == [targetGroupController groupId]) {
				targetIndex = i;
			}			
		}
		BOOL forward = targetIndex > currentIndex;
		
		if (forward) {
			[UIView setAnimationTransition:UIViewAnimationTransitionCurlUp forView:self.view cache:YES];
		} else {
			[UIView setAnimationTransition:UIViewAnimationTransitionCurlDown forView:self.view cache:YES];
		}
		
		[self.view addSubview:view];
		
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

- (void)navigateBackwardInHistory:(id)sender {
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
	LoginViewController *loginController = [[LoginViewController alloc] initWithDelegate:self];
	UINavigationController *loginNavController = [[UINavigationController alloc] initWithRootViewController:loginController];
	[self presentModalViewController:loginNavController animated:NO];
	[loginController release];
	[loginNavController release];
}


- (void)populateSettingsView:(id)sender {
	AppSettingController *settingController = [[AppSettingController alloc]init];
	UINavigationController *settingNavController = [[UINavigationController alloc] initWithRootViewController:settingController];
	[self presentModalViewController:settingNavController animated:YES];
	[settingController release];
	[settingNavController release];
}

- (void)refreshView:(id)sender {
	for (UIView *view in self.view.subviews) {
		[view removeFromSuperview];
	}
	[groupControllers removeAllObjects];
	[groupViewMap removeAllObjects];
	[tabBarControllers removeAllObjects];
	[tabBarControllerViewMap removeAllObjects];
	if (currentGroupController) {
		[currentGroupController stopPolling];
	}
	currentGroupController = nil;
	
	[self initGroups];
	
}

#pragma mark delegate method of LoginViewController

- (void)onSignin {
	[currentGroupController stopPolling];
	[theDelegate performSelector:@selector(checkConfigAndUpdate)];
}

- (void)onBackFromLogin {
	
	[theDelegate performSelector:@selector(updateDidFinished)];
}

#pragma mark delegate method of GestureWindow
- (void)performGesture:(Gesture *)gesture {
	NSLog(@"detected gesture : %d", gesture.swipeType);
	[currentGroupController performGesture:gesture];
}


- (void)dealloc {
	[groupViewMap release];
	[navigationHistory release];
	[errorViewController release];
	[globalTabBarController release];
	[tabBarControllers release];
	[groupControllers release];
	[updateController release];
	
	[super dealloc];
}


@end
