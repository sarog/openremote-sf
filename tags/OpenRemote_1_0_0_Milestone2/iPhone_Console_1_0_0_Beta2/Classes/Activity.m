//
//  Activity.m
//  openremote
//
//  Created by Dennis Stevense on 26-02-09.
//  Copyright 2009 __MyCompanyName__. All rights reserved.
//

#import "Activity.h"
#import "Screen.h"

@implementation Activity

@synthesize activityId,name, icon, screens;

#pragma mark Initializers

/**
 * Initialize according to the XML parser.
 */
- (id)initWithXMLParser:(NSXMLParser *)parser elementName:(NSString *)elementName attributes:(NSDictionary *)attributeDict parentDelegate:(NSObject *)parent {
	if (self == [super init]) {
		activityId = [[attributeDict objectForKey:@"id"] intValue];
		name = [[attributeDict objectForKey:@"name"] copy];
		icon = [[attributeDict objectForKey:@"icon"] copy]; 
		
		screens = [[NSMutableArray alloc] init];
		
		xmlParserParentDelegate = [parent retain];
		[parser setDelegate:self];
	}
	return self;
}

- (void)dealloc {
	[name release];
	[icon release];
	[screens release];
	
	[super dealloc];
}

#pragma mark Delegate methods of NSXMLParser

/**
 * Parse the screen elements by creating Screen instances for them.
 */
- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qualifiedName attributes:(NSDictionary *)attributeDict{
	if ([elementName isEqualToString:@"screen"]) {
		Screen *screen = [[Screen alloc] initWithXMLParser:parser elementName:elementName attributes:attributeDict parentDelegate:self];
		[screens addObject:screen];
		[screen release];
	}
}

/**
 * When we find an activity end element, restore the original XML parser delegate.
 */
- (void)parser:(NSXMLParser *)parser didEndElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qName {
	if ([elementName isEqualToString:@"activity"]) {
 		[parser setDelegate:xmlParserParentDelegate];
		[xmlParserParentDelegate release];
		xmlParserParentDelegate = nil;
	}
}

@end
