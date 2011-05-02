//
//  ORConsoleSettings.h
//  openremote
//
//  Created by Eric Bariaux on 29/04/11.
//  Copyright (c) 2011 OpenRemote, Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

@class ORController;

@interface ORConsoleSettings : NSManagedObject {
@private
    NSArray *autoDiscoveredControllers;
    NSArray *configuredController;
}

@property (nonatomic, assign, getter=isAutoDiscovery) BOOL autoDiscovery;
@property (nonatomic, retain) NSSet *unorderedAutoDiscoveredControllers;
@property (nonatomic, retain) NSSet *unorderedConfiguredControllers;
@property (nonatomic, retain) ORController *selectedDiscoveredController;
@property (nonatomic, retain) ORController *selectedConfiguredController;

@property (readonly) NSArray *autoDiscoveredControllers;
@property (readonly) NSArray *configuredControllers;

@end
