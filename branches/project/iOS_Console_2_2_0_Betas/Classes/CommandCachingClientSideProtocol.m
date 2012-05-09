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
#import "CommandCachingClientSideProtocol.h"
#import "ClientSideBeanManager.h"
#import "ClientSideProtocolCommand.h"
#import "LocalCommand.h"
#import "LocalSensor.h"

@interface CommandCachingClientSideProtocol()

@property (nonatomic, retain) ClientSideRuntime *clientSideRuntime;
@property (nonatomic, retain) ClientSideBeanManager *beanManager;

@end

@implementation CommandCachingClientSideProtocol

- (id)initWithRuntime:(ClientSideRuntime *)runtime
{
    self = [super init];
    if (self) {
        self.clientSideRuntime = runtime;
        self.beanManager = [[[ClientSideBeanManager alloc] initWithRuntime:self.clientSideRuntime] autorelease];
    }
    return self;
}

- (void)dealloc
{
    self.clientSideRuntime = nil;
    self.beanManager = nil;
    [super dealloc];
}

- (void)executeCommand:(LocalCommand *)command
{
    id <ClientSideProtocolCommand> cmd = [self.beanManager beanForKey:[command propertyValueForKey:@"command"]];
    [cmd execute];
}

- (void)startUpdatingSensor:(LocalSensor *)sensor
{
    id <ClientSideProtocolCommand> cmd = [self.beanManager beanForKey:[sensor.command propertyValueForKey:@"command"]];
    [cmd startUpdatingSensor:sensor];
}

- (void)stopUpdatingSensor:(LocalSensor *)sensor
{
    id <ClientSideProtocolCommand> cmd = [self.beanManager beanForKey:[sensor.command propertyValueForKey:@"command"]];
    [cmd stopUpdatingSensor:sensor];
}

@synthesize clientSideRuntime;
@synthesize beanManager;

@end