//
//  Definition.h
//  openremote
//
//  Created by wei allen on 09-2-20.
//  Copyright 2009 finalist. All rights reserved.
//

#import <UIKit/UIKit.h>

extern NSString *const DefinationUpdateDidFinishedNotification;
extern NSString *const DefinationNeedNotUpdate;

@interface Definition : NSObject {	
	
	
	BOOL isUpdating;
	NSDate *lastUpdateTime;
	NSMutableArray *activities;
	
	NSInvocationOperation *updateOperation;
	NSOperationQueue *updateOperationQueue; 
	
}

+ (Definition *)sharedDefinition;
- (void)update;
- (BOOL) isDataReady;

@property (nonatomic,readonly) BOOL isUpdating;
@property (nonatomic,readonly) NSDate *lastUpdateTime;
@property (nonatomic,readonly) NSMutableArray *activities;

@end
