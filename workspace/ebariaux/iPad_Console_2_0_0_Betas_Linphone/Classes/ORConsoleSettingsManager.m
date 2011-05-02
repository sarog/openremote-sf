//
//  ORConsoleSettingsManager.m
//  openremote
//
//  Created by Eric Bariaux on 02/05/11.
//  Copyright 2011 OpenRemote, Inc. All rights reserved.
//

#import "ORConsoleSettingsManager.h"
#import "DirectoryDefinition.h"
#import "ORConsoleSettings.h"


static ORConsoleSettingsManager *sharedORConsoleSettingsManager = nil;

// TODO: error handling

@implementation ORConsoleSettingsManager

+ (ORConsoleSettingsManager *)sharedORConsoleSettingsManager
{
    if (sharedORConsoleSettingsManager == nil) {
        sharedORConsoleSettingsManager = [[super allocWithZone:NULL] init];
    }
    return sharedORConsoleSettingsManager;
}

+ (id)allocWithZone:(NSZone *)zone
{
    return [[self sharedORConsoleSettingsManager] retain];
}

- (id)copyWithZone:(NSZone *)zone
{
    return self;
}

- (id)retain
{
    return self;
}

- (NSUInteger)retainCount
{
    return NSUIntegerMax;  //denotes an object that cannot be released
}

- (void)release
{
    //do nothing
}

- (id)autorelease
{
    return self;
}

- (void)dealloc
{
    [consoleSettings release];
	[managedObjectContext release];
    [managedObjectModel release];
    [persistentStoreCoordinator release];
    [super dealloc];
}

#pragma mark ORConsoleSettings management

- (ORConsoleSettings *)consoleSettings
{
    if (!consoleSettings) {
        NSLog(@"console settings not loaded");
        NSFetchRequest *fetchRequest = [[NSFetchRequest alloc] init];
        fetchRequest.entity = [NSEntityDescription entityForName:@"ORConsoleSettings" inManagedObjectContext:self.managedObjectContext];
        NSError *error = nil;
        consoleSettings = [[[self.managedObjectContext executeFetchRequest:fetchRequest error:&error] lastObject] retain];
        if (error) {
            NSLog(@"Error reading %@", error);
            // TODO: handle error
        }
        NSLog(@"Console settings read %@", consoleSettings);
        NSLog(@"auto discover %d ", consoleSettings.autoDiscovery);
        if (!consoleSettings) {
            NSLog(@"Console settings non existant in DB, creating one");
            consoleSettings = [[NSEntityDescription insertNewObjectForEntityForName:@"ORConsoleSettings" inManagedObjectContext:self.managedObjectContext] retain];
            [self saveConsoleSettings];
        }
    }
    return consoleSettings;
}

- (void)saveConsoleSettings
{
    NSLog(@"Asked to save console settings");
    NSLog(@"auto discover %d ", consoleSettings.autoDiscovery);
    NSError *error = nil;
    if (![self.managedObjectContext save:&error]) {
        NSLog(@"Could not save console settings %@", error);
        // TODO
    }
    NSLog(@"out of save console settings");
}

#pragma mark Core Data stack

/**
 Returns the managed object context for the application.
 If the context doesn't already exist, it is created and bound to the persistent store coordinator for the application.
 */
- (NSManagedObjectContext *)managedObjectContext
{	
    if (managedObjectContext != nil) {
        return managedObjectContext;
    }
	
    NSPersistentStoreCoordinator *coordinator = [self persistentStoreCoordinator];
    if (coordinator != nil) {
        managedObjectContext = [[NSManagedObjectContext alloc] init];
        [managedObjectContext setPersistentStoreCoordinator: coordinator];
    }
    return managedObjectContext;
}


/**
 Returns the managed object model for the application.
 If the model doesn't already exist, it is created by merging all of the models found in the application bundle.
 */
- (NSManagedObjectModel *)managedObjectModel
{
	
    if (managedObjectModel != nil) {
        return managedObjectModel;
    }
	
	// Initializing with URL instead of mergedModelFromBundles. If later used, exception thrown during save if validation errors instead of returning an error
	managedObjectModel = [[NSManagedObjectModel alloc] initWithContentsOfURL:[NSURL fileURLWithPath:[[NSBundle mainBundle] pathForResource:@"ORConsoleSettings" ofType:@"momd"]]];
    return managedObjectModel;
}


/**
 Returns the persistent store coordinator for the application.
 If the coordinator doesn't already exist, it is created and the application's store added to it.
 */
- (NSPersistentStoreCoordinator *)persistentStoreCoordinator
{
	
    if (persistentStoreCoordinator != nil) {
        return persistentStoreCoordinator;
    }
	
    NSURL *storeUrl = [NSURL fileURLWithPath: [[DirectoryDefinition applicationDocumentsDirectory] stringByAppendingPathComponent: @"ORConsoleSettings.sqlite"]];
	
	NSError *error = nil;
    persistentStoreCoordinator = [[NSPersistentStoreCoordinator alloc] initWithManagedObjectModel:[self managedObjectModel]];
    if (![persistentStoreCoordinator addPersistentStoreWithType:NSSQLiteStoreType
                                                  configuration:nil URL:storeUrl
														options:[NSDictionary dictionaryWithObjectsAndKeys:[NSNumber numberWithBool:YES], NSMigratePersistentStoresAutomaticallyOption,
																 [NSNumber numberWithBool:YES], NSInferMappingModelAutomaticallyOption, nil] error:&error]) {

                                                            NSLog(@"Error %@", error);
                                                        // TODO
                                                        

    }
	
    return persistentStoreCoordinator;
}


@end
