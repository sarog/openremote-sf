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
#import "LocalController.h"
#import "ControllerComponent.h"
#import "LocalCommand.h"
#import "LocalSensor.h"

@interface LocalController()

@property (nonatomic, retain) NSMutableDictionary *components;
@property (nonatomic, retain) NSMutableDictionary *commands;
@property (nonatomic, retain) NSMutableDictionary *sensors;
@property (nonatomic, retain) NSMutableDictionary *commandsPerComponents;

@end

@implementation LocalController

- (id)init
{
    self = [super init];
    if (self) {
        self.components = [NSMutableDictionary dictionary];
		self.commands = [NSMutableDictionary dictionary];
		self.sensors = [NSMutableDictionary dictionary];
    }
    return self;
}

- (void)dealloc
{
    self.components = nil;
    self.commands = nil;
    self.sensors = nil;
    self.commandsPerComponents = nil;
	[super dealloc];
}

- (void)addComponent:(ControllerComponent *)component
{
    [components setObject:component forKey:[NSNumber numberWithInt:component.componentId]];
}

- (void)addCommand:(LocalCommand *)command
{
    [commands setObject:command forKey:[NSNumber numberWithInt:command.componentId]];
}

- (void)addSensor:(LocalSensor *)sensor
{
    [sensors setObject:sensor forKey:[NSNumber numberWithInt:sensor.componentId]];
}

- (ControllerComponent *)componentForId:(NSUInteger)anId
{
    return [components objectForKey:[NSNumber numberWithInt:anId]];
}

- (LocalCommand *)commandForId:(NSUInteger)anId
{
	return [commands objectForKey:[NSNumber numberWithInt:anId]];
}

- (LocalSensor *)sensorForId:(NSUInteger)anId
{
	return [sensors objectForKey:[NSNumber numberWithInt:anId]];
}

/**
 * Returns the list of client side commands for a given component id and action.
 * action is dependant on the component type (e.g. for switch it can be on or off).
 * If the cache of component id -> commands is not yet build, do it know.
 */
- (NSArray *)commandsForComponentId:(NSUInteger)anId action:(NSString *)action
{
    if (!commandsPerComponents) {
        [self buildCommandsPerComponentsCache];
    }
    return [[commandsPerComponents objectForKey:[NSNumber numberWithInt:anId]] objectForKey:action];
}

- (void)buildCommandsPerComponentsCache
{
    self.commandsPerComponents = [NSMutableDictionary dictionary];
    for (ControllerComponent *component in [self.components allValues]) {
        [self.commandsPerComponents setObject:[component commandsPerAction] forKey:[NSNumber numberWithInt:component.componentId]];
    }
}

@synthesize components;
@synthesize commands;
@synthesize sensors;
@synthesize commandsPerComponents;

@end