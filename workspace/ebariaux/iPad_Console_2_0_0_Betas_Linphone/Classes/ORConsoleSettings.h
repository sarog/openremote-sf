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

- (void)addConfiguredController:(ORController *)controller;
- (void)addConfiguredControllerForURL:(NSString *)url;
- (void)removeConfiguredControllerAtIndex:(NSUInteger)index;

- (void)removeAllAutoDiscoveredControllers;
- (void)addAutoDiscoveredController:(ORController *)controller;
- (void)addAutoDiscoveredControllerForURL:(NSString *)url;

@property (nonatomic, assign, getter=isAutoDiscovery) BOOL autoDiscovery;
@property (nonatomic, retain) NSSet *unorderedAutoDiscoveredControllers;
@property (nonatomic, retain) NSSet *unorderedConfiguredControllers;
@property (nonatomic, retain) ORController *selectedDiscoveredController;
@property (nonatomic, retain) ORController *selectedConfiguredController;

@property (readonly) NSArray *autoDiscoveredControllers;
@property (readonly) NSArray *configuredControllers;

@property (readonly) NSArray *controllers;
@property (nonatomic, assign) ORController *selectedController;

@end
