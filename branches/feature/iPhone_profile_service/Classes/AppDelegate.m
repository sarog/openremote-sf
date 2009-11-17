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
- (void)navigateTo:(NSNotification *)notification;
- (void)populateLoginView:(id)sender;
- (void)populateSettingsView:(id)sender;
- (void)refreshView:(id)sender;
- (void)navigateToGroup:(int)to;
- (void)navigateToScreen:(int)to;
- (void)navigateToPreviousScreen;
- (void)navigateToNextScreen;
- (void)logout;
@end

@implementation AppDelegate

//Entry point method
- (void)applicationDidFinishLaunching:(UIApplication *)application {    
	// Default window for the app
	window = [[UIWindow alloc] initWithFrame:[UIScreen mainScreen].bounds];
	[window makeKeyAndVisible];
	
	// Create a default view with window size.
	defaultView = [[UIView alloc] initWithFrame:CGRectMake(window.bounds.origin.x, window.bounds.origin.y+20, window.bounds.size.height, window.bounds.size.width) ];
	//add the default view to window
	[window addSubview:defaultView];
	
	//Init the loading view
	initViewController = [[InitViewController alloc] init];
	[defaultView addSubview:initViewController.view];
	[Definition sharedDefinition].loading =[initViewController label];
	//Init UpdateController and set delegate to this class, it have three delegate methods
    // - (void)didUpadted;
    // - (void)didUseLocalCache:(NSString *)errorMessage;
    // - (void)didUpdateFail:(NSString *)errorMessage;
	updateController = [[UpdateController alloc] initWithDelegate:self];
	[updateController checkConfigAndUpdate];
	groupControllers = [[NSMutableArray alloc] init]; 
	groupViewMap = [[NSMutableDictionary alloc] init];
	
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
	[window addSubview:defaultGroupController.view];
	//[defaultGroupController release];
	[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(navigateTo:) name:NotificationNavigateTo object:nil];
	[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(populateLoginView:) name:NotificationPopulateCredentialView object:nil];
	[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(populateSettingsView:) name:NotificationPopulateSettingsView object:nil];
	[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(refreshView:) name:NotificationRefreshGroupsView object:nil];	
	[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(navigateToScreen:) name:NotificationRefreshGroupsView object:nil];	
	
}

- (void)navigateTo:(NSNotification *)notification {
	Navigate *navi = (Navigate *)[notification object];

	if (navi.toGroup > 0 ) {									//toGroup
		[self navigateToGroup:navi.toGroup];
	} 
	
	else if (navi.toScreen > 0) {							//toScreen
		[self navigateToScreen:navi.toScreen];
	} 
	
	else if (navi.isSetting) {								//toSetting
		[self populateSettingsView:nil];
	} 
	
	else if (navi.isPreviousScreen) {					//toPreviousScreen
		[self navigateToPreviousScreen];
	}
	
	else if (navi.isNextScreen) {							//toNextScreen
		[self navigateToNextScreen];
	}
	
	else if (navi.isBack) {										//toBack TODO
		//[self navigateToBack]; 
	} 
	
	else if (navi.isLogin) {									//toLogin
		[self populateLoginView:nil];
	} 
	
	else if (navi.isLogout) {									//toLogout TODO
		[self logout];
	}
	
}

- (void)navigateToGroup:(int)to {
	
	GroupController *targetGroupController = nil;	
	BOOL notItSelf = to != currentGroupController.group.groupId;
	if (to > 0 && notItSelf) {
		for (GroupController *gc in groupControllers) {
			if (gc.group.groupId == to) {
				targetGroupController = gc;
			}
		}
		
		if (targetGroupController == nil) {
			Group *group = [[Definition sharedDefinition] findGroupById:to];			 
			targetGroupController = [[GroupController alloc] initWithGroup:group];
			[groupControllers addObject:targetGroupController];
			[groupViewMap setObject:targetGroupController.view forKey:[NSString stringWithFormat:@"%d", group.groupId]];
		}
		
		[currentGroupController stopPolling];
		[targetGroupController startPolling];
		
		[UIView beginAnimations:nil context:nil];
		[UIView setAnimationDuration:1];
		[UIView setAnimationTransition:UIViewAnimationTransitionCurlUp forView:window cache:YES];
		
		//[navigationController.view removeFromSuperview];
		//navigationController = [[UINavigationController alloc] initWithRootViewController:targetGroupController];
		UIView *view = [groupViewMap objectForKey:[NSString stringWithFormat:@"%d", to]];
		

		[currentGroupController.view removeFromSuperview];
		[window addSubview:view];

		[UIView commitAnimations];
		
		currentGroupController = targetGroupController;
		
		//[targetGroupController release];
	}
}

- (void)logout {
	if ([Definition sharedDefinition].password) {
		LogoutHelper *logout = [[LogoutHelper alloc] init];
		[logout requestLogout];
		[logout release];
	}	
}

- (void)navigateToScreen:(int)to {
	if (to > 0) {
		[currentGroupController switchToScreen:to];
	}
}

- (void)navigateToPreviousScreen {
		[currentGroupController previousScreen];
}

- (void)navigateToNextScreen {
		[currentGroupController nextScreen];
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
	for (UIView *view in window.subviews) {
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
	[window addSubview:defaultGroupController.view];
	
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
	
	[super dealloc];
}


@end
