//
//  Control.m
//  openremote
//
//  Created by wei allen on 09-2-19.
//  Copyright 2009 finalist. All rights reserved.
//

#import "Control.h"


@implementation Control
@synthesize label,icon,x,y,width,height,eventID;

#pragma mark constractor of control
- (id)initWithXMLParser:(NSXMLParser *)parser elementName:(NSString *)elementName attributes:(NSDictionary *)attributeDict parentDelegate:(NSObject *)parent {
	if (self = [super init]) {
		
		label = [[attributeDict objectForKey:@"label"] copy];
		icon = [[attributeDict objectForKey:@"icon"] copy];
		x = [[attributeDict objectForKey:@"x"] intValue];
		y = [[attributeDict objectForKey:@"y"] intValue];
		width = [[attributeDict objectForKey:@"width"] intValue];
		height = [[attributeDict objectForKey:@"height"] intValue];
		eventID = [[attributeDict objectForKey:@"id"] intValue];
		
		xmlParserParentDelegate = [parent retain];
		[parser setDelegate:self];
	}
	return self;
}

#pragma mark deletegate method of NSXMLParser
- (void)parser:(NSXMLParser *)parser didEndElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qName {
	if ([elementName isEqualToString:@"button"]) {
		[parser setDelegate:xmlParserParentDelegate];
		[xmlParserParentDelegate release];
		xmlParserParentDelegate = nil;
	}
}


- (void)dealloc {
	[label release];
	[icon release];
	[super dealloc];
}
@end
