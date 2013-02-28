//
//  StringUtils.m
//  openremote
//
//  Created by finalist on 2/24/09.
//  Copyright 2009 __MyCompanyName__. All rights reserved.
//

#import "StringUtils.h"


@implementation StringUtils


+ (NSString *)parsefileNameFromString:(NSString *)str {
	if ([[str pathComponents] count] > 0) {
		NSString *url =  [[str pathComponents] objectAtIndex:[[str pathComponents] count] - 1];
		return [[url retain] autorelease];
	}
	return nil;
}

@end
