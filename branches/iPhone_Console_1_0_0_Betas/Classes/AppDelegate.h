//
//  openremoteAppDelegate.h
//  openremote
//
//  Created by wei allen on 09-2-19.
//  Copyright finalist 2009. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "InitViewController.h"
#import "UpdateController.h"

@interface AppDelegate : NSObject <UIApplicationDelegate> {
	UIWindow *window;
	
	UIView *defaultView;
	
	InitViewController *initViewController;
	
	UIActivityIndicatorView *loadingView;
	
	UINavigationController *navigationController;
	
	UpdateController *updateController;
}

- (void)didUpadted;
- (void)didUseLocalCache:(NSString *)errorMessage;

@end

