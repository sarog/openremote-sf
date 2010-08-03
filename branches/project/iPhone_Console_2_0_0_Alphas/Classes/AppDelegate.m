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
#import "NotificationConstant.h"


//Private method declare
@interface AppDelegate (Private)
- (void)updateDidFinished;
- (void)didUpadted;
- (void)didUseLocalCache:(NSString *)errorMessage;
- (void)didUpdateFail:(NSString *)errorMessage;
- (void)checkConfigAndUpdate;
@end

@implementation AppDelegate

//Entry point method
- (void)applicationDidFinishLaunching:(UIApplication *)application {
	
	// Load logined iphone user last time.
	[[DataBaseService sharedDataBaseService] initLastLoginUser];
	
	defaultViewController = [[DefaultViewController alloc] initWithDelegate:self];
	
	defaultView = defaultViewController.view;
	
	// Default window for the app
	window = [[GestureWindow alloc] initWithDelegate:defaultViewController];
	[window makeKeyAndVisible];
	
	[window addSubview:defaultView];
	
	//Init UpdateController and set delegate to this class, it have three delegate methods
    // - (void)didUpadted;
    // - (void)didUseLocalCache:(NSString *)errorMessage;
    // - (void)didUpdateFail:(NSString *)errorMessage;
	updateController = [[UpdateController alloc] initWithDelegate:self];
	
	[self checkConfigAndUpdate];
	
}

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
	[self applicationDidFinishLaunching:application];
	return YES;
}

- (void)checkConfigAndUpdate {
	[updateController checkConfigAndUpdate];
}

// this method will be called after UpdateController give a callback.
- (void)updateDidFinished {
	NSLog(@"----------updateDidFinished------");
	if (![defaultViewController isLoadingViewGone]) {
		[[NSNotificationCenter defaultCenter] postNotificationName:NotificationShowLoading object:nil];
		[defaultViewController initGroups];
		[[NSNotificationCenter defaultCenter] postNotificationName:NotificationHideLoading object:nil];
	}
}


#pragma mark delegate method of updateController
- (void)didUpadted {
	[self updateDidFinished];
}

- (void)didUseLocalCache:(NSString *)errorMessage {
	if ([errorMessage isEqualToString:@"401"]) {
		[defaultViewController populateLoginView:nil];
	} else {
		[[[ViewHelper alloc] init] showAlertViewWithTitleAndSettingNavigation:@"Warning" Message:[errorMessage stringByAppendingString:@" Using cached content."]];		
		[self updateDidFinished];
	}
	
}

- (void)didUpdateFail:(NSString *)errorMessage {
	NSLog(@"%@", errorMessage);
	if ([errorMessage isEqualToString:@"401"]) {
		[defaultViewController populateLoginView:nil];
	} else {
		[[[ViewHelper alloc] init] showAlertViewWithTitleAndSettingNavigation:@"Update Failed" Message:errorMessage];		
		[self updateDidFinished];
	}
	
}


- (void)dealloc {
	[updateController release];
	[defaultViewController release];	
	[window release];	
	
	[super dealloc];
}


@end
