/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2012, OpenRemote Inc.
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

/*
 * This is the entrypoint of the application.
 *  After application have been started applicationDidFinishLaunching method will be called.
 */

#import "AppDelegate.h"
#import "NotificationConstant.h"
#import "URLConnectionHelper.h"

#import "ORConsoleSettingsManager.h"
#import "ORConsoleSettings.h"

#ifdef INCLUDE_SIP_SUPPORT
    #import "SipController.h"
#endif

//Private method declare
@interface AppDelegate (Private)

- (void)updateDidFinished;
- (void)didUpdate;
- (void)didUseLocalCache:(NSString *)errorMessage;
- (void)didUpdateFail:(NSString *)errorMessage;
- (void)checkConfigAndUpdate;

@end

@implementation AppDelegate

- (void)applicationDidFinishLaunching:(UIApplication *)application
{
	defaultViewController = [[DefaultViewController alloc] initWithDelegate:self];

	// Default window for the app
	window = [[GestureWindow alloc] initWithDelegate:defaultViewController];
	[window makeKeyAndVisible];
	
	[window addSubview:defaultViewController.view];
	
	//Init UpdateController and set delegate to this class, it have three delegate methods
    // - (void)didUpdate;
    // - (void)didUseLocalCache:(NSString *)errorMessage;
    // - (void)didUpdateFail:(NSString *)errorMessage;
	updateController = [[UpdateController alloc] initWithDelegate:self];
	
    // TODO: this should be done only if controller does report SIP capabilities
    #ifdef INCLUDE_SIP_SUPPORT
        sipController = [[SipController alloc] init];
    #endif
	
    [updateController startup];
}

// when it's launched by other apps.
- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
	[self applicationDidFinishLaunching:application];
	return YES;
}


//when it wake up, WIFI is active.
- (void)applicationDidBecomeActive:(UIApplication *)application {
	[defaultViewController refreshPolling];
}

// To save battery, it will disconnect from WIFI when in sleep mode. 
// if its plugged into USB or a charger it remains connect.
// locking a phone will not let it sleep at once until a couple of minutes. 
- (void)applicationWillResignActive:(UIApplication *)application {
	[URLConnectionHelper setWifiActive:NO];
}

- (void)checkConfigAndUpdate {
	[updateController checkConfigAndUpdate];
}

// this method will be called after UpdateController give a callback.
- (void)updateDidFinished {
	log4Info(@"----------updateDidFinished------");
    NSLog(@"Is App Launching %d", ([defaultViewController isAppLaunching]));

	if ([defaultViewController isAppLaunching]) {//blocked from app launching, should refresh all groups.
		[[NSNotificationCenter defaultCenter] postNotificationName:NotificationShowLoading object:nil];
        
        // EBR : this is what makes the UI display in the first place
        
		[defaultViewController initGroups];
	} else {//blocked from sending command, should refresh command.
		[defaultViewController refreshPolling];
	}
    [[NSNotificationCenter defaultCenter] postNotificationName:NotificationHideLoading object:nil];
}

#pragma mark delegate method of updateController

- (void)didUpdate {
    log4Info(@">>AppDelegate.didUpdate");
	[self updateDidFinished];
}

- (void)didUseLocalCache:(NSString *)errorMessage {
	if ([errorMessage isEqualToString:@"401"]) {
		[defaultViewController populateLoginView:nil];
	} else {
        ViewHelper *viewHelper = [[ViewHelper alloc] init];
		[viewHelper showAlertViewWithTitleAndSettingNavigation:@"Warning" Message:[errorMessage stringByAppendingString:@" Using cached content."]];
		[viewHelper release];
		[self updateDidFinished];
	}
	
}

- (void)didUpdateFail:(NSString *)errorMessage {
	log4Error(@"%@", errorMessage);
	if ([errorMessage isEqualToString:@"401"]) {
		[defaultViewController populateLoginView:nil];
	} else {
        ViewHelper *viewHelper = [[ViewHelper alloc] init];
		[viewHelper showAlertViewWithTitleAndSettingNavigation:@"Update Failed" Message:errorMessage];		
		[viewHelper release];
		[self updateDidFinished];
	}
	
}

- (void)dealloc {
	[updateController release];
	[defaultViewController release];	
	[window release];

    #ifdef INCLUDE_SIP_SUPPORT
        [sipController release];
    #endif
	[super dealloc];
}

@end