//
//  TimeManager.m
//  openremote
//
//  Created by Eric Bariaux on 04/03/11.
//  Copyright 2011 OpenRemote, Inc. All rights reserved.
//

#import "TimeManager.h"

@implementation TimeManager

+ (NSString *)getDateTime:(NSMutableDictionary *)context
{
	NSDateFormatter *df = [[NSDateFormatter alloc] init];
	[df setTimeStyle:NSDateFormatterMediumStyle];
	[df setDateStyle:NSDateFormatterShortStyle];
	NSString *retValue = [df stringFromDate:[NSDate date]];
	[df release];
	return retValue;
}

@end