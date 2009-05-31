//
//  openremoteAppDelegate.m
//  openremote
//
//  Created by wei allen on 09-2-19.
//  Copyright finalist 2009. All rights reserved.
//

#import "AppDelegate.h"
#import "Activity.h"
#import "Screen.h"
#import "Control.h"
#import "ScreenViewController.h"
#import "ServerDefinition.h"
#import "Definition.h"
#import "ActivitiesController.h"
#import "AppSettingsDefinition.h"
#import "InitViewController.h"
#import "NotificationConstant.h"

@interface AppDelegate (Private)

- (void)updateDidFinished;
- (void)NeedNotUpdate;
- (void)checkConfigAndUpdate;
- (void)addNotificationObserver;
- (void)showSettingsView;

@end

@implementation AppDelegate




- (void)applicationDidFinishLaunching:(UIApplication *)application {    
    // Override point for customization after application launch
	window = [[UIWindow alloc] initWithFrame:[UIScreen mainScreen].bounds];
	[window makeKeyAndVisible];
	
	defaultView = [[UIView alloc] initWithFrame:CGRectMake(window.bounds.origin.x, window.bounds.origin.y+20, window.bounds.size.height, window.bounds.size.width) ];
	[window addSubview:defaultView];
	
	initViewController = [[InitViewController alloc] init];
	
	[defaultView addSubview:initViewController.view];
	
	[self addNotificationObserver];
	
	[AppSettingsDefinition checkConfigAndUpdate];
	
	
	
}

- (void)addNotificationObserver {
	//add a observer to defination upload task 
	[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(updateDidFinished) name:DefinationUpdateDidFinishedNotification object:nil];	
	[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(updateDidFinished) name:DefinationNeedNotUpdate object:nil];	
	

}


- (void)updateDidFinished {
	NSLog(@"----------updateDidFinished------");
	[initViewController.view removeFromSuperview];
	
	ActivitiesController *activityController = [[ActivitiesController alloc] init];
	[activityController setTitle:@"Activities"];
	navigationController = [[UINavigationController alloc] initWithRootViewController:activityController];
	[window addSubview:navigationController.view];
	[activityController release];
}


- (void)dealloc {
	[defaultView release];
	[navigationController release];
	[window release];
    [super dealloc];
	
}


@end
