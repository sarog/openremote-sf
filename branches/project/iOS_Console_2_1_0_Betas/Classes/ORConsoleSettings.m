/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2011, OpenRemote Inc.
 *
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
#import "ORConsoleSettings.h"
#import "ORController.h"

@interface ORConsoleSettings ()

- (void)addUnorderedAutoDiscoveredControllersObject:(ORController *)value;
- (void)removeUnorderedAutoDiscoveredControllersObject:(ORController *)value;
- (void)addUnorderedAutoDiscoveredControllers:(NSSet *)value;
- (void)removeUnorderedAutoDiscoveredControllers:(NSSet *)value;
- (void)addUnorderedConfiguredControllersObject:(ORController *)value;
- (void)removeUnorderedConfiguredControllersObject:(ORController *)value;
- (void)addUnorderedConfiguredControllers:(NSSet *)value;
- (void)removeUnorderedConfiguredControllers:(NSSet *)value;

@end

@implementation ORConsoleSettings

@dynamic unorderedAutoDiscoveredControllers;
@dynamic unorderedConfiguredControllers;
@dynamic selectedDiscoveredController;
@dynamic selectedConfiguredController;

- (void)awakeFromFetch
{
	[super awakeFromFetch];
	[self addObserver:self forKeyPath:@"unorderedConfiguredControllers" options:NSKeyValueObservingOptionNew context:nil];
	[self addObserver:self forKeyPath:@"unorderedAutoDiscoveredControllers" options:NSKeyValueObservingOptionNew context:nil];
}

- (void)awakeFromInsert
{
	[super awakeFromInsert];
	[self addObserver:self forKeyPath:@"unorderedConfiguredControllers" options:NSKeyValueObservingOptionNew context:nil];
	[self addObserver:self forKeyPath:@"unorderedAutoDiscoveredControllers" options:NSKeyValueObservingOptionNew context:nil];
}

- (void)observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary *)change context:(void *)context
{
	if ([keyPath isEqualToString:@"unorderedConfiguredControllers"]) {
		[configuredController release];
		configuredController = nil;
	} else if ([keyPath isEqualToString:@"unorderedAutoDiscoveredControllers"]) {
		[autoDiscoveredControllers release];
		autoDiscoveredControllers = nil;
	}

}

- (void)didTurnInfoFault
{
	[configuredController dealloc];
	configuredController = nil;
    [self removeObserver:nil forKeyPath:@"unorderedConfiguredControllers"];
	[autoDiscoveredControllers dealloc];
	autoDiscoveredControllers = nil;
    [self removeObserver:nil forKeyPath:@"unorderedAutoDiscoveredControllers"];
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

- (NSArray *)autoDiscoveredControllers
{
    if (autoDiscoveredControllers == nil) {
        NSMutableArray *temp = [NSMutableArray arrayWithArray:[self.unorderedAutoDiscoveredControllers allObjects]];
        NSSortDescriptor *indexSort = [[NSSortDescriptor alloc] initWithKey:@"index" ascending:YES];
		[temp sortUsingDescriptors:[NSArray arrayWithObject:indexSort]];
		[indexSort release];
        autoDiscoveredControllers = [[NSArray alloc] initWithArray:temp];
    }
    return autoDiscoveredControllers;
}

- (void)removeAllAutoDiscoveredControllers
{
    [self removeUnorderedAutoDiscoveredControllers:self.unorderedAutoDiscoveredControllers];
    self.selectedDiscoveredController = nil;
}

- (NSArray *)configuredControllers
{
    if (configuredController == nil) {
        NSMutableArray *temp = [NSMutableArray arrayWithArray:[self.unorderedConfiguredControllers allObjects]];
        NSSortDescriptor *indexSort = [[NSSortDescriptor alloc] initWithKey:@"index" ascending:YES];
		[temp sortUsingDescriptors:[NSArray arrayWithObject:indexSort]];
		[indexSort release];
        configuredController = [[NSArray alloc] initWithArray:temp];
    }
    return configuredController;
}

- (void)addConfiguredController:(ORController *)controller
{
    controller.index = [NSNumber numberWithInt:[((ORController *)[self.configuredControllers lastObject]).index intValue] + 1];
    [self addUnorderedConfiguredControllersObject:controller];
}

- (ORController *)addConfiguredControllerForURL:(NSString *)url
{
    ORController *controller = [NSEntityDescription insertNewObjectForEntityForName:@"ORController" inManagedObjectContext:self.managedObjectContext];
    controller.primaryURL = url;
    [self addConfiguredController:controller];
    if (!self.selectedConfiguredController) {
        self.selectedConfiguredController = controller;
    }
    return controller;
}

- (void)removeConfiguredControllerAtIndex:(NSUInteger)index
{
    ORController *controller = [self.configuredControllers objectAtIndex:index];
    if (self.selectedConfiguredController == controller) {
        self.selectedConfiguredController = nil;
    }
    [self removeUnorderedConfiguredControllersObject:controller];
}

- (void)addAutoDiscoveredController:(ORController *)controller
{
    controller.index = [NSNumber numberWithInt:[((ORController *)[self.autoDiscoveredControllers lastObject]).index intValue] + 1];
    [self addUnorderedAutoDiscoveredControllersObject:controller];
}

- (ORController *)addAutoDiscoveredControllerForURL:(NSString *)url
{
    ORController *controller = [NSEntityDescription insertNewObjectForEntityForName:@"ORController" inManagedObjectContext:self.managedObjectContext];
    controller.primaryURL = url;
    [self addAutoDiscoveredController:controller];
    if (!self.selectedDiscoveredController) {
        self.selectedDiscoveredController = controller;
    }
    return controller;
}

- (NSArray *)controllers
{
    return (self.isAutoDiscovery)?self.autoDiscoveredControllers:self.configuredControllers;
}

- (ORController *)selectedController
{
    return (self.isAutoDiscovery)?self.selectedDiscoveredController:self.selectedConfiguredController;
}

- (void)setSelectedController:(ORController *)controller
{
    if (self.isAutoDiscovery) {
        self.selectedDiscoveredController = controller;
    } else {
        self.selectedConfiguredController = controller;
    }
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
