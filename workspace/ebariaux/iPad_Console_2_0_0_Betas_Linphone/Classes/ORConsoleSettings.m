//
//  ORConsoleSettings.m
//  openremote
//
//  Created by Eric Bariaux on 29/04/11.
//  Copyright (c) 2011 OpenRemote, Inc. All rights reserved.
//

#import "ORConsoleSettings.h"
#import "ORController.h"


@implementation ORConsoleSettings

@dynamic unorderedAutoDiscoveredControllers;
@dynamic unorderedConfiguredControllers;
@dynamic selectedDiscoveredController;
@dynamic selectedConfiguredController;

- (NSArray *)autoDiscoveredControllers
{
    return nil;
}

- (NSArray *)configuredControllers
{
    return nil;
}
   
   
- (BOOL)isAutoDiscovery
{
    [self willAccessValueForKey:@"autoDiscovery"];
    BOOL b = [[self primitiveValueForKey:@"autoDiscovery"] boolValue];
    [self didAccessValueForKey:@"autoDiscovery"];
    return b;
}

- (void)setAutoDiscovery:(BOOL)autoDiscovery
{
    [self willChangeValueForKey:@"autoDiscovery"];
    [self setPrimitiveValue:[NSNumber numberWithBool:autoDiscovery] forKey:@"autoDiscovery"];
    [self didChangeValueForKey:@"autoDiscovery"];
}

- (void)addUnorderedAutoDiscoveredControllersObject:(ORController *)value
{    
    NSSet *changedObjects = [[NSSet alloc] initWithObjects:&value count:1];
    [self willChangeValueForKey:@"unorderedAutoDiscoveredControllers" withSetMutation:NSKeyValueUnionSetMutation usingObjects:changedObjects];
    [[self primitiveValueForKey:@"unorderedAutoDiscoveredControllers"] addObject:value];
    [self didChangeValueForKey:@"unorderedAutoDiscoveredControllers" withSetMutation:NSKeyValueUnionSetMutation usingObjects:changedObjects];
    [changedObjects release];
}

- (void)removeUnorderedAutoDiscoveredControllersObject:(ORController *)value
{
    NSSet *changedObjects = [[NSSet alloc] initWithObjects:&value count:1];
    [self willChangeValueForKey:@"unorderedAutoDiscoveredControllers" withSetMutation:NSKeyValueMinusSetMutation usingObjects:changedObjects];
    [[self primitiveValueForKey:@"unorderedAutoDiscoveredControllers"] removeObject:value];
    [self didChangeValueForKey:@"unorderedAutoDiscoveredControllers" withSetMutation:NSKeyValueMinusSetMutation usingObjects:changedObjects];
    [changedObjects release];
}

- (void)addUnorderedAutoDiscoveredControllers:(NSSet *)value
{    
    [self willChangeValueForKey:@"unorderedAutoDiscoveredControllers" withSetMutation:NSKeyValueUnionSetMutation usingObjects:value];
    [[self primitiveValueForKey:@"unorderedAutoDiscoveredControllers"] unionSet:value];
    [self didChangeValueForKey:@"unorderedAutoDiscoveredControllers" withSetMutation:NSKeyValueUnionSetMutation usingObjects:value];
}

- (void)removeUnorderedAutoDiscoveredControllers:(NSSet *)value
{
    [self willChangeValueForKey:@"unorderedAutoDiscoveredControllers" withSetMutation:NSKeyValueMinusSetMutation usingObjects:value];
    [[self primitiveValueForKey:@"unorderedAutoDiscoveredControllers"] minusSet:value];
    [self didChangeValueForKey:@"unorderedAutoDiscoveredControllers" withSetMutation:NSKeyValueMinusSetMutation usingObjects:value];
}

- (void)addUnorderedConfiguredControllersObject:(ORController *)value
{
    NSSet *changedObjects = [[NSSet alloc] initWithObjects:&value count:1];
    [self willChangeValueForKey:@"unorderedConfiguredControllers" withSetMutation:NSKeyValueUnionSetMutation usingObjects:changedObjects];
    [[self primitiveValueForKey:@"unorderedConfiguredControllers"] addObject:value];
    [self didChangeValueForKey:@"unorderedConfiguredControllers" withSetMutation:NSKeyValueUnionSetMutation usingObjects:changedObjects];
    [changedObjects release];
}

- (void)removeUnorderedConfiguredControllersObject:(ORController *)value
{
    NSSet *changedObjects = [[NSSet alloc] initWithObjects:&value count:1];
    [self willChangeValueForKey:@"unorderedConfiguredControllers" withSetMutation:NSKeyValueMinusSetMutation usingObjects:changedObjects];
    [[self primitiveValueForKey:@"unorderedConfiguredControllers"] removeObject:value];
    [self didChangeValueForKey:@"unorderedConfiguredControllers" withSetMutation:NSKeyValueMinusSetMutation usingObjects:changedObjects];
    [changedObjects release];
}

- (void)addUnorderedConfiguredControllers:(NSSet *)value
{
    [self willChangeValueForKey:@"unorderedConfiguredControllers" withSetMutation:NSKeyValueUnionSetMutation usingObjects:value];
    [[self primitiveValueForKey:@"unorderedConfiguredControllers"] unionSet:value];
    [self didChangeValueForKey:@"unorderedConfiguredControllers" withSetMutation:NSKeyValueUnionSetMutation usingObjects:value];
}

- (void)removeUnorderedConfiguredControllers:(NSSet *)value
{
    [self willChangeValueForKey:@"unorderedConfiguredControllers" withSetMutation:NSKeyValueMinusSetMutation usingObjects:value];
    [[self primitiveValueForKey:@"unorderedConfiguredControllers"] minusSet:value];
    [self didChangeValueForKey:@"unorderedConfiguredControllers" withSetMutation:NSKeyValueMinusSetMutation usingObjects:value];
}

@end
