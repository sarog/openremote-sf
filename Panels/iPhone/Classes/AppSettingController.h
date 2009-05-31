//
//  AppSettingController.h
//  openremote
//
//  Created by finalist on 5/14/09.
//  Copyright 2009 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ServerAutoDiscoveryController.h"

@interface AppSettingController : UITableViewController {
	NSString *pathToUserCopyOfPlist;
	BOOL autoDiscovery;
	NSMutableArray *serverArray;
	NSIndexPath *currentSelectedServerIndex;
	ServerAutoDiscoveryController *autoDiscoverController;
	UIBarButtonItem *done;
	UIBarButtonItem *edit;
	BOOL isEditing;
	UIActivityIndicatorView *loadingView;
}
@end
