/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2012, OpenRemote Inc.
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

- (void)addUnorderedControllersObject:(ORController *)value;
- (void)removeUnorderedControllersObject:(ORController *)value;
- (void)addUnorderedControllers:(NSSet *)value;
- (void)removeUnorderedControllers:(NSSet *)value;

@end

@implementation ORConsoleSettings

@dynamic unorderedControllers;
@dynamic selectedController;

- (void)awakeFromFetch
{
	[super awakeFromFetch];
	[self addObserver:self forKeyPath:@"unorderedControllers" options:NSKeyValueObservingOptionNew context:nil];
}

- (void)awakeFromInsert
{
	[super awakeFromInsert];
	[self addObserver:self forKeyPath:@"unorderedControllers" options:NSKeyValueObservingOptionNew context:nil];
}

- (void)observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary *)change context:(void *)context
{
	if ([keyPath isEqualToString:@"unorderedControllers"]) {
		[controllers release];
		controllers = nil;
	}
}

- (void)didTurnInfoFault
{
	[controllers dealloc]; // TODO: check that ???? should be release ???
	controllers = nil;
    [self removeObserver:nil forKeyPath:@"unorderedControllers"];
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

- (NSArray *)controllers
{
    if (controllers == nil) {
        NSMutableArray *temp = [NSMutableArray arrayWithArray:[self.unorderedControllers allObjects]];
        NSSortDescriptor *indexSort = [[NSSortDescriptor alloc] initWithKey:@"index" ascending:YES];
		[temp sortUsingDescriptors:[NSArray arrayWithObject:indexSort]];
		[indexSort release];
        controllers = [[NSArray alloc] initWithArray:temp];
    }
    return controllers;
}

- (void)addController:(ORController *)controller
{
    controller.index = [NSNumber numberWithInt:[((ORController *)[self.controllers lastObject]).index intValue] + 1];
    [self addUnorderedControllersObject:controller];
}

- (ORController *)addControllerForURL:(NSString *)url
{
    ORController *controller = [NSEntityDescription insertNewObjectForEntityForName:@"ORController" inManagedObjectContext:self.managedObjectContext];
    controller.primaryURL = url;
    [self addController:controller];
    if (!self.selectedController) {
        self.selectedController = controller;
    }
    return controller;
}

- (void)removeControllerAtIndex:(NSUInteger)index
{
    ORController *controller = [self.controllers objectAtIndex:index];
    if (self.selectedController == controller) {
        self.selectedController = nil;
    }
    [self removeUnorderedControllersObject:controller];
}


- (void)addUnorderedControllersObject:(ORController *)value
{
    NSSet *changedObjects = [[NSSet alloc] initWithObjects:&value count:1];
    [self willChangeValueForKey:@"unorderedControllers" withSetMutation:NSKeyValueUnionSetMutation usingObjects:changedObjects];
    [[self primitiveValueForKey:@"unorderedControllers"] addObject:value];
    [self didChangeValueForKey:@"unorderedControllers" withSetMutation:NSKeyValueUnionSetMutation usingObjects:changedObjects];
    [changedObjects release];
}

- (void)removeUnorderedControllersObject:(ORController *)value
{
    NSSet *changedObjects = [[NSSet alloc] initWithObjects:&value count:1];
    [self willChangeValueForKey:@"unorderedControllers" withSetMutation:NSKeyValueMinusSetMutation usingObjects:changedObjects];
    [[self primitiveValueForKey:@"unorderedControllers"] removeObject:value];
    [self didChangeValueForKey:@"unorderedControllers" withSetMutation:NSKeyValueMinusSetMutation usingObjects:changedObjects];
    [changedObjects release];
}

- (void)addUnorderedControllers:(NSSet *)value
{
    [self willChangeValueForKey:@"unorderedControllers" withSetMutation:NSKeyValueUnionSetMutation usingObjects:value];
    [[self primitiveValueForKey:@"unorderedControllers"] unionSet:value];
    [self didChangeValueForKey:@"unorderedControllers" withSetMutation:NSKeyValueUnionSetMutation usingObjects:value];
}

- (void)removeUnorderedControllers:(NSSet *)value
{
    [self willChangeValueForKey:@"unorderedControllers" withSetMutation:NSKeyValueMinusSetMutation usingObjects:value];
    [[self primitiveValueForKey:@"unorderedControllers"] minusSet:value];
    [self didChangeValueForKey:@"unorderedControllers" withSetMutation:NSKeyValueMinusSetMutation usingObjects:value];
}

@end
