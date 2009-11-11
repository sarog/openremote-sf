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

//Private method declare
@interface AppDelegate (Private)
- (void)updateDidFinished;
- (void)didUpadted;
- (void)didUseLocalCache:(NSString *)errorMessage;
- (void)didUpdateFail:(NSString *)errorMessage;
- (void)navigateToGroup:(NSNotification *)notification;
- (void)populateLoginView:(id)sender;
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
}


// this method will be called after UpdateController give a callback.
- (void)updateDidFinished {
	NSLog(@"----------updateDidFinished------");
	[initViewController.view removeFromSuperview];
	NSArray *groups = [[Definition sharedDefinition] groups];
	
	if (groups.count > 0) {
		GroupController *gc = [[GroupController alloc] initWithGroup:((Group *)[groups objectAtIndex:0])];
		[groupControllers addObject:gc];
		[groupViewMap setObject:gc.view forKey:[NSString stringWithFormat:@"%d", gc.group.groupId]];
	}
	
	GroupController *defaultGroupController = nil;
	if (groupControllers.count > 0 ) {
		defaultGroupController = [groupControllers objectAtIndex:0];		
	}
	//navigationController = [[UINavigationController alloc] initWithRootViewController:defaultGroupController];
	//[window addSubview:navigationController.view];
	currentGroupController = defaultGroupController;
	[window addSubview:defaultGroupController.view];
	//[defaultGroupController release];
	[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(navigateToGroup:) name:NotificationNavigateToGroup object:nil];
	[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(populateLoginView:) name:NotificationPopulateCredentialView object:nil];
}

- (void)navigateToGroup:(NSNotification *)notification {
	Navigate *navi = (Navigate *)[notification object];
	GroupController *targetGroupController = nil;	
	BOOL notItSelf = navi.toGroup != currentGroupController.group.groupId;
	if (navi.toGroup > 0 && notItSelf) {
		for (GroupController *gc in groupControllers) {
			if (gc.group.groupId == navi.toGroup) {
				targetGroupController = gc;
			}
		}
		
		if (targetGroupController == nil) {
			Group *group = [[Definition sharedDefinition] findGroupById:navi.toGroup];			 
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
		UIView *view = [groupViewMap objectForKey:[NSString stringWithFormat:@"%d", navi.toGroup]];
		

		[currentGroupController.view removeFromSuperview];
		[window addSubview:view];

		[UIView commitAnimations];
		
		currentGroupController = targetGroupController;
		
		//[targetGroupController release];
	}
}

//prompts the user to enter a valid user name and password
- (void)populateLoginView:(id)sender {
	LoginViewController *loginController = [[LoginViewController alloc]init];
	UINavigationController *loginNavController = [[UINavigationController alloc] initWithRootViewController:loginController];
	[currentGroupController presentModalViewController:loginNavController animated:YES];
	[loginController release];
	[loginNavController release];
}	

#pragma mark delegate method of updateController
- (void)didUpadted {
	[self updateDidFinished];
}

- (void)didUseLocalCache:(NSString *)errorMessage {
	[ViewHelper showAlertViewWithTitle:@"Warning" Message:[errorMessage stringByAppendingString:@" Using cached content."]];
	[self updateDidFinished];
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
