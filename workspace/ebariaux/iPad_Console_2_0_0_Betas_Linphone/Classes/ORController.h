//
//  ORController.h
//  openremote
//
//  Created by Eric Bariaux on 29/04/11.
//  Copyright (c) 2011 OpenRemote, Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

@class ORConsoleSettings;

@interface ORController : NSManagedObject {
@private
}

@property (nonatomic, retain) NSString * primaryURL;
@property (nonatomic, retain) NSNumber * index;
@property (nonatomic, retain) NSSet* groupMembers;
@property (nonatomic, retain) ORConsoleSettings * settingsForAutoDiscoveredControllers;
@property (nonatomic, retain) ORConsoleSettings * settingsForConfiguredControllers;
@property (nonatomic, retain) ORConsoleSettings * settingsForSelectedDiscoveredController;
@property (nonatomic, retain) ORConsoleSettings * settingsForSelectedConfiguredController;

@end
