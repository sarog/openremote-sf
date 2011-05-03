//
//  ORConsoleSettingsManager.h
//  openremote
//
//  Created by Eric Bariaux on 02/05/11.
//  Copyright 2011 OpenRemote, Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

@class ORConsoleSettings;

@interface ORConsoleSettingsManager : NSObject {
    
    NSManagedObjectModel *managedObjectModel;
    NSManagedObjectContext *managedObjectContext;	    
    NSPersistentStoreCoordinator *persistentStoreCoordinator;

    ORConsoleSettings *consoleSettings;
}

@property (nonatomic, retain, readonly) NSManagedObjectModel *managedObjectModel;
@property (nonatomic, retain, readonly) NSManagedObjectContext *managedObjectContext;
@property (nonatomic, retain, readonly) NSPersistentStoreCoordinator *persistentStoreCoordinator;

+ (ORConsoleSettingsManager *)sharedORConsoleSettingsManager;

- (ORConsoleSettings *)consoleSettings;
- (void)saveConsoleSettings;
- (void)cancelConsoleSettingsChanges;

@end
