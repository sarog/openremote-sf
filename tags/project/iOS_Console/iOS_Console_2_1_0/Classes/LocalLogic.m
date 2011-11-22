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
#import "LocalLogic.h"
#import "LocalSensor.h"
#import "LocalCommand.h"
#import "LocalTask.h"

@implementation LocalLogic

- (id)init
{
    self = [super init];
    if (self) {
		sensors = [[NSMutableDictionary alloc] init];
		commands = [[NSMutableDictionary alloc] init];
		tasks = [[NSMutableDictionary alloc] init];
    }
    return self;
}

- (void)addSensor:(LocalSensor *)sensor
{
    [sensors setObject:sensor forKey:[NSNumber numberWithInt:sensor.componentId]];
}

- (void)addCommand:(LocalCommand *)command
{
    [commands setObject:command forKey:[NSNumber numberWithInt:command.componentId]];
}

- (void)addTask:(LocalTask *)task
{
    [tasks setObject:task forKey:[NSNumber numberWithInt:task.componentId]];
}

- (LocalSensor *)sensorForId:(NSUInteger)anId {
	return [sensors objectForKey:[NSNumber numberWithInt:anId]];
}

- (LocalCommand *)commandForId:(NSUInteger)anId {
	return [commands objectForKey:[NSNumber numberWithInt:anId]];
}

- (LocalTask *)taskForId:(NSUInteger)anId {
	return [tasks objectForKey:[NSNumber numberWithInt:anId]];
}

- (void)dealloc {
	[sensors release];
	[commands release];
	[tasks release];
	[super dealloc];
}

@end
