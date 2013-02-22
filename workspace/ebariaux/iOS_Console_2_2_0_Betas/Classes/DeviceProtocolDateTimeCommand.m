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
#import "DeviceProtocolDateTimeCommand.h"

@interface DeviceProtocolDateTimeCommand()

- (void)refresh:(NSTimer *)timer;

@property (nonatomic, retain) NSTimer *refreshTimer;

@end

@implementation DeviceProtocolDateTimeCommand

- (void)dealloc
{
    [self.refreshTimer invalidate];
    self.refreshTimer = nil;
    [super dealloc];
}

- (void)startUpdating
{
    self.refreshTimer = [NSTimer scheduledTimerWithTimeInterval:1.0 target:self selector:@selector(refresh:) userInfo:nil repeats:YES];
}

- (void)stopUpdating
{
    [self.refreshTimer invalidate];
    self.refreshTimer = nil;
}

- (void)refresh:(NSTimer *)timer
{
    [self publishValue];
}

- (NSString *)sensorValue
{
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
	[dateFormatter setTimeStyle:NSDateFormatterMediumStyle];
	[dateFormatter setDateStyle:NSDateFormatterShortStyle];
	NSString *dateTimeString = [dateFormatter stringFromDate:[NSDate date]];
	[dateFormatter release];
    return dateTimeString;
}

@synthesize refreshTimer;

@end