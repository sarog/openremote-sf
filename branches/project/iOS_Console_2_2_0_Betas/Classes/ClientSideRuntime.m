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
#import "ClientSideRuntime.h"
#import "ORConsoleSettingsManager.h"
#import "ORConsoleSettings.h"
#import "ORController.h"
#import "Definition.h"
#import "LocalController.h"
#import "LocalCommand.h"
#import "LocalSensor.h"
#import "SIPProtocol.h"

@interface ClientSideRuntime()

- (id <ClientSideProtocol>)implementationForProtocol:(NSString *)protocolName;

@property (nonatomic, assign) ORController *controller;
@property (nonatomic, retain) NSDictionary *protocolsRegistry;
@property (nonatomic, retain) NSMutableDictionary *protocolsImplementation;

@end

@implementation ClientSideRuntime

- (id)initWithController:(ORController *)aController
{
    self = [super init];
    if (self) {
        self.controller = aController;
        self.protocolsRegistry = [NSDictionary dictionaryWithContentsOfFile:[[NSBundle mainBundle] pathForResource:@"ClientSideProtocols" ofType:@"plist"]];
        self.protocolsImplementation = [NSMutableDictionary dictionaryWithCapacity:[self.protocolsRegistry count]];
    }
    return self;
}

- (void)dealloc
{
    self.controller = nil;
    self.protocolsRegistry = nil;
    self.protocolsImplementation = nil;
    [super dealloc];
}

- (void)executeCommands:(NSArray *)commands
{
    for (NSNumber *commandId in commands) {
        LocalCommand *command = [controller.definition.localController commandForId:[commandId intValue]];
        if (command) {
            [self executeCommand:command];
        }
    }
}

- (void)executeCommand:(LocalCommand *)command
{
    id <ClientSideProtocol> protocol = [self implementationForProtocol:command.protocol];
    [protocol executeCommand:command];
}

- (void)startUpdatingSensor:(LocalSensor *)sensor
{
    id <ClientSideProtocol> protocol = [self implementationForProtocol:sensor.command.protocol];
    [protocol startUpdatingSensor:sensor];
}

- (void)stopUpdatingSensor:(LocalSensor *)sensor
{
    id <ClientSideProtocol> protocol = [self implementationForProtocol:sensor.command.protocol];
    [protocol stopUpdatingSensor:sensor];
}

- (id <ClientSideProtocol>)implementationForProtocol:(NSString *)protocolName
{
    id <ClientSideProtocol> protocol = [self.protocolsImplementation objectForKey:protocolName];
    if (protocol) {
        return protocol;
    }
    NSString *protocolClassName = [self.protocolsRegistry objectForKey:protocolName];
    if (protocolClassName) {
        Class protocolClass = NSClassFromString(protocolClassName);
        if (protocolClass) {
            protocol = [[protocolClass alloc] initWithRuntime:self.controller.clientSideRuntime];
            if (protocol) {
                [self.protocolsImplementation setObject:protocol forKey:protocolName];
                [protocol release];
            }
        }
    }
    return protocol;
}

- (SensorStatusCache *)sensorStatusCache
{
    return self.controller.sensorStatusCache;
}

@synthesize controller;
@synthesize protocolsRegistry;
@synthesize protocolsImplementation;

@end