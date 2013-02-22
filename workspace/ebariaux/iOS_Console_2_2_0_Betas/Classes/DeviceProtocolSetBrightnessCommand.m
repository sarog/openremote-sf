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
#import "DeviceProtocolSetBrightnessCommand.h"
#import "LocalCommand.h"

@implementation DeviceProtocolSetBrightnessCommand

- (id)initWithRuntime:(ClientSideRuntime *)runtime
{
    self = [super init];
    if (self) {
//        self.clientSideRuntime = runtime;
    }
    return self;
}

- (void)execute:(LocalCommand *)command commandType:(NSString *)commandType
{
    if ([[UIScreen mainScreen] respondsToSelector:@selector(setBrightness:)]) {
        NSString *brightness = [command propertyValueForKey:@"brightness"];
        
        if (!brightness) {
            // If command originated from a slider, commandType will contain slider value. So if commandType validates as a number, use it as parameter
            NSNumberFormatter *formatter = [[NSNumberFormatter alloc] init];
            [formatter setFormatterBehavior:NSNumberFormatterBehavior10_4];
            [formatter setLocale:[[[NSLocale alloc] initWithLocaleIdentifier:@"en_US"] autorelease]];
            NSNumber *result = nil;
            NSError *error = nil;
            NSRange range = NSMakeRange(0, commandType.length);
            if ([formatter getObjectValue:&result forString:commandType range:&range error:&error]) {
                brightness = commandType;
            }
        }        
        if (brightness) {
            [UIScreen mainScreen].brightness = ([brightness intValue] / 100.0);
            // Setting the brightness does not post a brightness change notification, so do post it ourself to make sure our sensor gets updated
            [[NSNotificationCenter defaultCenter] postNotificationName:UIScreenBrightnessDidChangeNotification object:nil];
        }
    }
}

@end