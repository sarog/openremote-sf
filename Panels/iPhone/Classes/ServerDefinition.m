//
//  ServerDefinition.m
//  openremote
//
//  Created by finalist on 2/23/09.
//  Copyright 2009 __MyCompanyName__. All rights reserved.
//

#import "ServerDefinition.h"
#import "AppSettingsDefinition.h"

@implementation ServerDefinition

+ (NSString *)serverUrl {
	static NSString *serverUrl;
	serverUrl = [AppSettingsDefinition getCurrentServerUrl];
	return  serverUrl;
}

+ (NSString *)sampleXmlUrl {
	return [[self serverUrl] stringByAppendingPathComponent:@"resources/iphone.xml"];
}

+ (NSString *)imageUrl {
	return [[self serverUrl] stringByAppendingPathComponent:@"resources"];
}

+ (NSString *)eventHandleRESTUrl {
	return [[self serverUrl] stringByAppendingPathComponent:@"cmd.htm"];
}


@end
