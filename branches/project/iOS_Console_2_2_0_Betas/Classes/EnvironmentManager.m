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
#import "EnvironmentManager.h"

#define kMode @"EnvironmentManager_Mode"
#define kModeYear @"EnvironmentManager_Year"
#define kModeMonth @"EnvironmentManager_Month"
#define kModeDay @"EnvironmentManager_Day"
#define kPeriod @"EnvironmentManager_Period"

@implementation EnvironmentManager

+ (NSDate *)getCurrentPeriod:(NSMutableDictionary *)context
{
    NSDate *value = [context objectForKey:kPeriod];
    if (!value) {
        value = [NSDate date];
        [context setObject:value forKey:kPeriod];
    }
    return value;
}

+ (NSString *)getGraphURL:(NSMutableDictionary *)context
{
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    [formatter setTimeStyle:NSDateFormatterNoStyle];
    NSString *mode = [context objectForKey:kMode];
    if ([kModeDay isEqualToString:mode]) {
        [formatter setDateFormat:@"yyyy-MM-dd"];
    } else if ([kModeMonth isEqualToString:mode]) {
        [formatter setDateFormat:@"yyyy-MM"];        
    } else {
        [formatter setDateFormat:@"yyyy"];        
    }
    NSString *periodValue = [formatter stringFromDate:[self getCurrentPeriod:context]];
    [formatter release];
    return [NSString stringWithFormat:@"http://192.168.0.118:8080/beehive/rest/user/openremote/graph/temperature?period=%@&width=580&height=360", periodValue];
}

+ (NSString *)getPeriodString:(NSMutableDictionary *)context
{
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    [formatter setTimeStyle:NSDateFormatterNoStyle];
    NSString *mode = [context objectForKey:kMode];
    if ([kModeDay isEqualToString:mode]) {
        [formatter setDateFormat:@"dd-MM-yyyy"];
    } else if ([kModeMonth isEqualToString:mode]) {
        [formatter setDateFormat:@"MMM yyyy"];        
    } else {
        [formatter setDateFormat:@"yyyy"];        
    }
    NSString *retValue = [formatter stringFromDate:[self getCurrentPeriod:context]];
    [formatter release];
    return retValue;
}

+ (void)periodPlus:(NSMutableDictionary *)context
{
    NSString *mode = [context objectForKey:kMode];
    NSDateComponents *comps = [[NSDateComponents alloc] init];
    if ([kModeDay isEqualToString:mode]) {
        [comps setDay:1];
    } else if ([kModeMonth isEqualToString:mode]) {
        [comps setMonth:1];
    } else {
        [comps setYear:1];
    }
    [context setObject:[[NSCalendar currentCalendar] dateByAddingComponents:comps toDate:[self getCurrentPeriod:context] options:0] forKey:kPeriod];
    [comps release];
}

+ (void)periodMinus:(NSMutableDictionary *)context
{
    NSString *mode = [context objectForKey:kMode];
    NSDateComponents *comps = [[NSDateComponents alloc] init];
    if ([kModeDay isEqualToString:mode]) {
        [comps setDay:-1];
    } else if ([kModeMonth isEqualToString:mode]) {
        [comps setMonth:-1];
    } else {
        [comps setYear:-1];
    }
    [context setObject:[[NSCalendar currentCalendar] dateByAddingComponents:comps toDate:[self getCurrentPeriod:context] options:0] forKey:kPeriod];
    [comps release];
}

+ (void)setModeYear:(NSMutableDictionary *)context
{
    [context setObject:kModeYear forKey:kMode];
}

+ (void)setModeMonth:(NSMutableDictionary *)context
{
    [context setObject:kModeMonth forKey:kMode];
}

+ (void)setModeDay:(NSMutableDictionary *)context
{
    [context setObject:kModeDay forKey:kMode];
}

@end
