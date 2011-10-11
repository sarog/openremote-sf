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
#import "DefaultViewController.h"
#import "ORConsoleSettingsManager.h"
#import "ORConsoleSettings.h"
#import "ORController.h"
#import "Definition.h"

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

- (id)initWithDelegate:(id)delegate
{
    self = [super init];
    if (self) {	
			theDelegate = delegate;
			navigationHistory = [[NSMutableArray alloc] init];
			
			//register notifications
			[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(navigateFromNotification:) name:NotificationNavigateTo object:nil];
			[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(populateSettingsView:) name:NotificationPopulateSettingsView object:nil];
			[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(refreshView:) name:NotificationRefreshGroupsView object:nil];	
			[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(navigateBackwardInHistory:) name:NotificationNavigateBack object:nil];	
    }
    return self;
}

- (void)dealloc {
	[navigationHistory release];
    
    // TODO: recheck release of those, what about on view unload in case of low memory condition
    [currentGroupController release];
    
	[errorViewController release];
	[updateController release];
	
	[super dealloc];
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    [[UIDevice currentDevice] beginGeneratingDeviceOrientationNotifications];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(populateLoginView:) name:NotificationPopulateCredentialView object:nil];
    
    // EBR: is this required, already set in Info.plist
    [self.navigationController setNavigationBarHidden:YES];
}

- (void)viewDidUnload
{
    [[NSNotificationCenter defaultCenter] removeObserver:self name:NotificationPopulateCredentialView object:nil];
    [[UIDevice currentDevice] endGeneratingDeviceOrientationNotifications];
    [super viewDidUnload];
}

- (void)loadView
{
	[super loadView];
	[self.view setBackgroundColor:[UIColor blackColor]];

	//Init the error view with xib
	errorViewController = [[ErrorViewController alloc] initWithErrorTitle:@"No Group Found" message:@"Please check your setting or define a group with screens first."];
	[self.view addSubview:errorViewController.view];
	
	//Init the loading view with xib
	initViewController = [[InitViewController alloc] init];
	[self.view addSubview:initViewController.view];
}

- (void)refreshPolling
{
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
	NSArray *groups = [[[ORConsoleSettingsManager sharedORConsoleSettingsManager] consoleSettings].selectedController.definition groups];
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
			gc = [[GroupController alloc] initWithGroup:lastGroup parentViewController:self];
		} else {
			gc = [[GroupController alloc] initWithGroup:((Group *)[groups objectAtIndex:0]) parentViewController:self];
		}
	} else {
		gc = [[GroupController alloc] initWithGroup:((Group *)[groups objectAtIndex:0]) parentViewController:self];
	}
	return [gc autorelease];
}

- (void)initGroups {
	[errorViewController.view removeFromSuperview];
	[initViewController.view removeFromSuperview];
	
    Definition *definition = [[ORConsoleSettingsManager sharedORConsoleSettingsManager] consoleSettings].selectedController.definition;
	NSArray *groups = [definition groups];
	NSLog(@"groups count is %d",groups.count);
	
	if (groups.count > 0) {
		GroupController *gc = [self recoverLastOrCreateGroup];
		currentGroupController = [gc retain];
		[self.view addSubview:currentGroupController.view];
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
	if (!currentGroupController.group) {
        return;
    }
	
	if ([self navigateTo:navi]) {
        Navigate *historyNavigate = [[Navigate alloc] init];
		historyNavigate.fromGroup = currentGroupController.group.groupId;
		historyNavigate.fromScreen = [currentGroupController currentScreenId];
		[self saveLastGroupIdAndScreenId];
		NSLog(@"navigate from group %d, screen %d", historyNavigate.fromGroup, historyNavigate.fromScreen);
		[navigationHistory addObject:historyNavigate];
		[historyNavigate release];
	}
	
	NSLog(@"navi history count = %d", navigationHistory.count);
}

- (void)saveLastGroupIdAndScreenId {
	if (currentGroupController.group.groupId == 0 || [currentGroupController currentScreenId] == 0) {
		return;
	}
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

- (void)updateGlobalOrLocalTabbarViewToGroupController:(GroupController *)targetGroupController withGroupId:(int)groupId
{	
    [self.view.subviews makeObjectsPerformSelector:@selector(removeFromSuperview)];
	
	UIView *v = targetGroupController.view;

	[self.view addSubview:v];

    if (currentGroupController) {
        [currentGroupController release];
    }
	currentGroupController = [targetGroupController retain];
}

- (BOOL)navigateToGroup:(int)groupId toScreen:(int)screenId {
	GroupController *targetGroupController = nil;
	
	BOOL isAnotherGroup = groupId != [currentGroupController groupId];
	
    
    Definition *definition = [[ORConsoleSettingsManager sharedORConsoleSettingsManager] consoleSettings].selectedController.definition;

	//if screenId is specified, and is not in current group, jump to that group
	if (groupId > 0 && isAnotherGroup) {
		if (targetGroupController == nil) {
			Group *group = [definition findGroupById:groupId];
			if (group) {
				targetGroupController = [[[GroupController alloc] initWithGroup:group parentViewController:self] autorelease];
			} else {
				return NO;
			}
		}
		
        [currentGroupController stopPolling];
		[self updateGlobalOrLocalTabbarViewToGroupController:targetGroupController withGroupId:groupId];
	}
	
    Screen *screen = nil;
	if (screenId > 0) {
        // If screenId is specified, jump to that screen
         screen = [currentGroupController.group findScreenByScreenId:screenId];
    } else {
        //If only group is specified, then by definition we show the first screen of that group.
        screen = [currentGroupController.group.screens objectAtIndex:0];
    }
    // First check if we have a screen more appropriate for the current device orientation orientation
    if (screen) {
        screenId = [screen screenIdForOrientation:[[UIDevice currentDevice] orientation]];
    }
	return [currentGroupController switchToScreen:screenId];
}

//logout only when password is saved.
- (void)logout {
	if ([ORConsoleSettingsManager sharedORConsoleSettingsManager].consoleSettings.selectedController.password) {
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
	AppSettingController *settingController = [[AppSettingController alloc] init];
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
	
	if (currentGroupController) {
		[currentGroupController stopPolling];
        [currentGroupController release];
        currentGroupController = nil;
	}
	
	[self initGroups];
	[[NSNotificationCenter defaultCenter] postNotificationName:NotificationHideLoading object:nil];
}

- (BOOL)isAppLaunching {
	return ![self isLoadingViewGone];
}

- (BOOL)isLoadingViewGone {
	return currentGroupController != nil;
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

#pragma mark Rotation handling

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return YES;
}

// Because this VC is installed at root, it needs to forward those messages to the VC it contains
- (void)willRotateToInterfaceOrientation:(UIInterfaceOrientation)toInterfaceOrientation duration:(NSTimeInterval)duration
{
	if ([self isLoadingViewGone]) {
		[currentGroupController willRotateToInterfaceOrientation:toInterfaceOrientation duration:duration];
	} else {
		[initViewController willRotateToInterfaceOrientation:toInterfaceOrientation duration:duration];
	}
}

- (void)didRotateFromInterfaceOrientation:(UIInterfaceOrientation)fromInterfaceOrientation
{
    if ([self isLoadingViewGone]) {
		[currentGroupController didRotateFromInterfaceOrientation:fromInterfaceOrientation];
    } else {
        [initViewController didRotateFromInterfaceOrientation:fromInterfaceOrientation];
    }
}

#pragma mark Detect the shake motion.

-(BOOL)canBecomeFirstResponder {
	return YES;
}

-(void)viewDidAppear:(BOOL)animated {
	[super viewDidAppear:animated];
    if ([self isLoadingViewGone]) {
		[currentGroupController viewDidAppear:animated];
    }
    
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

@end