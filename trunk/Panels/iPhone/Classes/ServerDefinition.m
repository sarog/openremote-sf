//
//  ServerDefinition.m
//  openremote
//
//  Created by finalist on 2/23/09.
//  Copyright 2009 __MyCompanyName__. All rights reserved.
//

#import "ServerDefinition.h"

@implementation ServerDefinition

+ (NSString *)serverUrl {
	static NSString *serverUrl;
	
	if (serverUrl == nil) {
		NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
		serverUrl = [[defaults objectForKey:@"serverUrl"] stringByAppendingPathComponent:@""];
		if (!serverUrl) {
			[self registerDefaultsFromSettingsBundle];
		}
		serverUrl = [[defaults objectForKey:@"serverUrl"] stringByAppendingPathComponent:@""];
		[serverUrl retain];
	}
	return  serverUrl;
}

+ (NSString *)sampleXmlUrl {
	return [[self serverUrl] stringByAppendingPathComponent:@"iphone.xml"];
}

+ (NSString *)imageUrl {
	return [self serverUrl];
}

+ (NSString *)eventHandleRESTUrl {
	return [[self serverUrl] stringByAppendingPathComponent:@"cmd.htm"];
}

+ (void)registerDefaultsFromSettingsBundle {
	NSString *settingsBundle = [[NSBundle mainBundle] pathForResource:@"Settings" ofType:@"bundle"];
	if(!settingsBundle) {
		NSLog(@"Could not find Settings.bundle");
		return;
	}
	
	NSDictionary *settings = [NSDictionary dictionaryWithContentsOfFile:[settingsBundle stringByAppendingPathComponent:@"Root.plist"]];
	NSArray *preferences = [settings objectForKey:@"PreferenceSpecifiers"];
	
	NSMutableDictionary *defaultsToRegister = [[NSMutableDictionary alloc] initWithCapacity:[preferences count]];
	for(NSDictionary *prefSpecification in preferences) {
		NSString *key = [prefSpecification objectForKey:@"Key"];
		if(key) {
			[defaultsToRegister setObject:[prefSpecification objectForKey:@"DefaultValue"] forKey:key];
		}
	}
	
	[[NSUserDefaults standardUserDefaults] registerDefaults:defaultsToRegister];
	[defaultsToRegister release];
}


@end
