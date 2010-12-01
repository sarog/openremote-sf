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

#define degreesToRadian(x) (M_PI * (x) / 180.0)

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
- (void)saveLastGroupIdAndScreenId;
- (void)rerenderTabbarWithNewOrientation;
- (void)transformToOppositeOrientation;

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
			
			//register notifications
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
	[super loadView];
	[self.view setBackgroundColor:[UIColor blackColor]];
	

	//Must add xib file view into window to detect current device orientation.
	
	//Init the error view with xib
	errorViewController = [[ErrorViewController alloc] initWithErrorTitle:@"No Group Found" 
																													 message:@"Please check your setting or define a group with screens first."];
	[self.view addSubview:errorViewController.view];

	
	//Init the loading view with xib
	initViewController = [[InitViewController alloc] init];
	[self.view addSubview:initViewController.view];
}

- (void)refreshPolling {
	[currentGroupController startPolling];
}

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
- (GroupController *)recoverLastOrCreateGroup {
	NSArray *groups = [[Definition sharedDefinition] groups];
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
	return gc;
}

- (void)initGroups {
	
	[errorViewController.view removeFromSuperview];
	[initViewController.view removeFromSuperview];
	
	NSArray *groups = [[Definition sharedDefinition] groups];
	NSLog(@"groups count is %d",groups.count);
	
	if (groups.count > 0) {
		
		GroupController *gc = [self recoverLastOrCreateGroup];
		
		[groupControllers addObject:gc];
		[groupViewMap setObject:gc.view forKey:[NSString stringWithFormat:@"%d", gc.group.groupId]];	
		currentGroupController = [gc retain];
		
		TabBar *localTabBar = currentGroupController.group.tabBar;
		// local tabBar
		if (localTabBar) {
			localTabBarController = [[TabBarController alloc] initWithGroupController:currentGroupController tabBar:localTabBar];
			[self.view addSubview:localTabBarController.view];
			lastSubView = localTabBarController.view;
			
			[tabBarControllers addObject:localTabBarController];
			[tabBarControllerViewMap setObject:localTabBarController.view forKey:[NSString stringWithFormat:@"%d", gc.group.groupId]];
		}		
		// global tabBar
		else if ([[Definition sharedDefinition] tabBar]) {
			globalTabBarController = [[TabBarController alloc] initWithGroupController:currentGroupController tabBar:[[Definition sharedDefinition] tabBar]];
			[self.view addSubview:globalTabBarController.view];
			lastSubView = globalTabBarController.view;
		} else {
			[self.view addSubview:currentGroupController.view];
			lastSubView = currentGroupController.view;
		}
		
		//if last screen orientation is not current device orientation, transform to that orientation.
		NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
		if ([userDefaults objectForKey:@"lastScreenId"]) {
			int lastScreenId = [[userDefaults objectForKey:@"lastScreenId"] intValue];
			Screen *screen = [currentGroupController.group findScreenByScreenId:lastScreenId];
			if (screen && ([currentGroupController isOrientationLandscape] != screen.landscape)) {
				[self transformToOppositeOrientation];
				[self rerenderTabbarWithNewOrientation];
				[currentGroupController switchToScreen:screen.screenId];
			}
		}
		
		[self saveLastGroupIdAndScreenId];
	} else {		
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
	Navigate *historyNavigate = [[Navigate alloc] init];
	if (currentGroupController.group) {
		historyNavigate.fromGroup = currentGroupController.group.groupId;
		historyNavigate.fromScreen = [currentGroupController currentScreenId];
	} else {
		return;
	}
	
	if ([self navigateTo:navi]) {
		[self saveLastGroupIdAndScreenId];
		NSLog(@"navigte from group %d, screen %d", historyNavigate.fromGroup, historyNavigate.fromScreen);
		[navigationHistory addObject:historyNavigate];
		[historyNavigate release];
	}
	
	NSLog(@"navi history count = %d", navigationHistory.count);
}

- (void)saveLastGroupIdAndScreenId {
	NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
	[userDefaults setObject:[NSString stringWithFormat:@"%d",currentGroupController.group.groupId] forKey:@"lastGroupId"];
	[userDefaults setObject:[NSString stringWithFormat:@"%d",[currentGroupController currentScreenId]] forKey:@"lastScreenId"];
	NSLog(@"saveLastGroupIdAndScreenId : groupID %d, screenID %d", [[userDefaults objectForKey:@"lastGroupId"] intValue], [[userDefaults objectForKey:@"lastScreenId"] intValue]);
}

// Returned BOOL value is whether to save history
// if YES, should save history
// if NO, don't save history
- (BOOL)navigateTo:(Navigate *)navi {
	
	if (navi.toGroup > 0 ) {	                //toGroup & toScreen
		return [self navigateToGroup:navi.toGroup toScreen:navi.toScreen];
	} 
	
	else if (navi.isPreviousScreen) {					//toPreviousScreen
		return [self navigateToPreviousScreen];
	}
	
	else if (navi.isNextScreen) {							//toNextScreen
		return [self navigateToNextScreen];
	}
	
	//the following should not generate history record
	
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
	
	else if (navi.isSetting) {								//toSetting
		[self populateSettingsView:nil];
		return NO;
	}
	
	return NO;
}

//if the group view is not cached, tabbar can't show group view when rotation happens.
//rerendering the tabbar will show group view.
- (void)rerenderTabbarWithNewOrientation {
	if (currentGroupController.group.tabBar) {
		[localTabBarController updateTabItems];
	} else if ([[Definition sharedDefinition] tabBar]) {
		[globalTabBarController updateTabItems];
	}
}

//transform view +/- 90 degrees
- (void)transformToOppositeOrientation {
	
	[UIView beginAnimations:@"View transform" context:nil];
	[UIView setAnimationDuration:0.5f];
	[UIView setAnimationCurve:UIViewAnimationCurveEaseInOut];
	self.view.transform = CGAffineTransformIdentity;
	CGFloat h = self.view.frame.size.height;
	CGFloat w = self.view.frame.size.width;
	if (h > w) {
		NSLog(@"view did transform to landscape");
		[currentGroupController transformToOrientation:UIInterfaceOrientationLandscapeRight];
		
		//move transform centre, make portrait view and landscape view have the same centre.
		//by default portrait view and landscape view have the same origin.
		self.view.frame = CGRectMake((w-h)/2.0, (h-w)/2.0, h, w);
		
		self.view.transform = CGAffineTransformMakeRotation(degreesToRadian(90));
	} else {
		NSLog(@"view did transform to portrait");
		[currentGroupController transformToOrientation:UIInterfaceOrientationPortrait];
		//by default it's portart, needn't to specify -90 degrees.
		self.view.frame = CGRectMake(0, 0, h, w);
	}
	
	[UIView commitAnimations];
	
}

- (void)updateGlobalOrLocalTabbarViewToGroupController:(GroupController *)targetGroupController withGroupId:(int)groupId {
	
	if ([targetGroupController getCurrentOrientation] != [currentGroupController getCurrentOrientation]) {
		[targetGroupController setNewOrientation:[currentGroupController getCurrentOrientation]];
	}
	

//	[lastSubView removeFromSuperview];
	
	for (UIView *vi in self.view.subviews) {
		[vi retain];//cache it, or it will be release after removeFromSuperview.
		[vi removeFromSuperview];
	}
	
	UIView *v = [groupViewMap objectForKey:[NSString stringWithFormat:@"%d", groupId]];

	//if local tabbar exists
	if (targetGroupController.group.tabBar) {
		BOOL findCachedLocalTabBarController = NO;
		for (TabBarController *lTabBarController in tabBarControllers) {
			if (lTabBarController.groupController.group.groupId == groupId) {
				localTabBarController = lTabBarController;
				findCachedLocalTabBarController = YES;
				break;
			}
		}
		if (!findCachedLocalTabBarController) {
			localTabBarController = [[TabBarController alloc] initWithGroupController:targetGroupController tabBar:targetGroupController.group.tabBar];
			[tabBarControllers addObject:localTabBarController];
			[tabBarControllerViewMap setObject:localTabBarController.view forKey:[NSString stringWithFormat:@"%d", groupId]];
		} else {
			[localTabBarController updateGroupController:targetGroupController];
		}
		v = [tabBarControllerViewMap objectForKey:[NSString stringWithFormat:@"%d", groupId]];
	}
	//if global tabbar exists
	else if ([[Definition sharedDefinition] tabBar]) {
		if (globalTabBarController) {
			[globalTabBarController updateGroupController:targetGroupController];
		} else {
			globalTabBarController = [[TabBarController alloc] initWithGroupController:targetGroupController tabBar:[[Definition sharedDefinition] tabBar]];
		}
		v = globalTabBarController.view;
	}
	//lastSubView = v;

	[self.view addSubview:v];

	currentGroupController = targetGroupController;
}


- (BOOL)navigateToGroup:(int)groupId toScreen:(int)screenId {
	GroupController *targetGroupController = nil;
	
	BOOL isAnotherGroup = groupId != [currentGroupController groupId];
	BOOL isLastOrientationLandscape = [currentGroupController isOrientationLandscape];
	
	//if screenId is specified, and is not in current group, jump to that group
	if (groupId > 0 && isAnotherGroup) {
		//should find in cache first, but this cached GroupController may cause view bug, so don't use cache for now.
//		for (GroupController *gc in groupControllers) {
//			if ([gc groupId] == groupId) {
//				targetGroupController = gc;
//				break;
//			}
//		}
		//if not found in cache, create one
		if (targetGroupController == nil) {
			Group *group = [[Definition sharedDefinition] findGroupById:groupId];
			if (group) {
				targetGroupController = [[GroupController alloc] initWithGroup:group orientation:[currentGroupController getCurrentOrientation]];
				[groupControllers addObject:targetGroupController];
				[groupViewMap setObject:targetGroupController.view forKey:[NSString stringWithFormat:@"%d", group.groupId]];
			} else {
				return NO;
			}
		}
		
		[self updateGlobalOrLocalTabbarViewToGroupController:targetGroupController withGroupId:groupId];
		
	}
	
	//if screenId is specified, jump to that screen
	if (screenId > 0) {
		//if navigate to opposite orientation, need to transform view +/- 90 degrees.
		if (isLastOrientationLandscape != [currentGroupController isOrientationLandscapeWithScreenId:screenId]) {
			[self transformToOppositeOrientation];
			[self rerenderTabbarWithNewOrientation];
		}
		return [currentGroupController switchToScreen:screenId];
	}
	//If only group is specified, then by definition we show the first screen of that group.
	else if (screenId == 0) {
		Screen *screen = [currentGroupController.group.screens objectAtIndex:0];
		//if navigate to opposite orientation, need to transform view +/- 90 degrees.
		if (screen && (isLastOrientationLandscape != screen.landscape)) {
			[self transformToOppositeOrientation];
			[self rerenderTabbarWithNewOrientation];
		}
		return [currentGroupController switchToScreen:screen.screenId];
	}
		
	return YES;
}

//logout only when password is saved.
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
		if (backward.fromGroup > 0 && backward.fromScreen > 0 ) {
			NSLog(@"navigte back to group %d, screen %d", backward.fromGroup, backward.fromScreen);
			[self navigateToGroup:backward.fromGroup toScreen:backward.fromScreen];
		} else {
			[self navigateTo:backward];
		}
		//remove current navigation, navigate backward
		[navigationHistory removeLastObject];
	}
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
	[[NSNotificationCenter defaultCenter] postNotificationName:NotificationShowLoading object:nil];
	for (UIView *view in self.view.subviews) {
		[view removeFromSuperview];
	}
	[groupControllers removeAllObjects];
	[groupViewMap removeAllObjects];
	[tabBarControllers removeAllObjects];
	[tabBarControllerViewMap removeAllObjects];
	globalTabBarController = nil;
	localTabBarController = nil;
	
	if (currentGroupController) {
		[currentGroupController stopPolling];
	}
	currentGroupController = nil;
	
	[self initGroups];
	[[NSNotificationCenter defaultCenter] postNotificationName:NotificationHideLoading object:nil];
}

- (BOOL)isAppLaunching {
	return ![self isLoadingViewGone];
}

- (BOOL)isLoadingViewGone {
	return groupControllers.count > 0;
}

#pragma mark delegate method of LoginViewController

- (void)onSignin {
	[currentGroupController stopPolling];
	[[NSNotificationCenter defaultCenter] postNotificationName:NotificationShowLoading object:nil];
	[theDelegate performSelector:@selector(checkConfigAndUpdate)];
	[[NSNotificationCenter defaultCenter] postNotificationName:NotificationHideLoading object:nil];
}

- (void)onBackFromLogin {
	[theDelegate performSelector:@selector(updateDidFinished)];
}

#pragma mark delegate method of GestureWindow
- (void)performGesture:(Gesture *)gesture {
	NSLog(@"detected gesture : %@", [gesture toString]);
	[currentGroupController performGesture:gesture];
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation {
	if ([self isLoadingViewGone]) {
		if (currentGroupController.group.screens.count == 0) {
			return YES;
		} 
		return [currentGroupController shouldAutorotateToInterfaceOrientation:interfaceOrientation];
	} else {
		return YES;
	}
}

- (void)willRotateToInterfaceOrientation:(UIInterfaceOrientation)toInterfaceOrientation duration:(NSTimeInterval)duration {
	if ([self isLoadingViewGone]) {
		[currentGroupController willRotateToInterfaceOrientation:toInterfaceOrientation duration:duration];
	} else {
		[initViewController willRotateToInterfaceOrientation:toInterfaceOrientation duration:duration];
	}
}


#pragma mark Detect the shake motion.

-(BOOL)canBecomeFirstResponder {
	return YES;
}

-(void)viewDidAppear:(BOOL)animated {
	[super viewDidAppear:animated];
	[self becomeFirstResponder];
}

- (void)viewWillDisappear:(BOOL)animated {
	[self resignFirstResponder];
	[super viewWillDisappear:animated];
}


- (void)motionEnded:(UIEventSubtype)motion withEvent:(UIEvent *)event {
	if (event.type == UIEventSubtypeMotionShake && [self isLoadingViewGone]) {
		[self populateSettingsView:nil];
	}
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
