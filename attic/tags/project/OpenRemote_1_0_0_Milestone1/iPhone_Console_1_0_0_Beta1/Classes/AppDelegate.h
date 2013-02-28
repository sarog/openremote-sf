//
//  openremoteAppDelegate.h
//  openremote
//
//  Created by wei allen on 09-2-19.
//  Copyright finalist 2009. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface AppDelegate : NSObject <UIApplicationDelegate> {
	UIWindow *window;
	
	UIActivityIndicatorView *loadingView;
	
	UINavigationController *navigationController;
}

@end

