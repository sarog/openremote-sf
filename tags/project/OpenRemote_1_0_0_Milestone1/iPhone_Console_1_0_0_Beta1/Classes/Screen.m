//
//  Screen.m
//  openremote
//
//  Created by wei allen on 09-2-19.
//  Copyright 2009 finalist. All rights reserved.
//

#import "Screen.h"
#import "Control.h"


@implementation Screen
 
@synthesize name,icon,controls,rows,cols;

#pragma mark constructor
//Initialize itself accoding to xml parser
- (id)initWithXMLParser:(NSXMLParser *)parser elementName:(NSString *)elementName attributes:(NSDictionary *)attributeDict parentDelegate:(NSObject *)parent {
	if (self = [super init]) {
		
		name = [[attributeDict objectForKey:@"name"] copy];
		icon = [[attributeDict objectForKey:@"icon"] copy]; 
		rows = [[attributeDict objectForKey:@"row"] intValue];
		cols = [[attributeDict objectForKey:@"col"] intValue];
		
		controls = [[NSMutableArray alloc] init];
		
		xmlParserParentDelegate = [parent retain];
		[parser setDelegate:self];
	}
	return self;
}

#pragma mark deleget method of NSXMLParser
//end the screen parse set deleget back to parent
- (void)parser:(NSXMLParser *)parser didEndElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qName {
	if ([elementName isEqualToString:@"screen"]) {
		// set back the delegate to original one. In order to  parse "screens" element
 		[parser setDelegate:xmlParserParentDelegate];
		[xmlParserParentDelegate release];
		xmlParserParentDelegate = nil;
	}
}

//Parse control element and add it in to controls 
- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qualifiedName attributes:(NSDictionary *)attributeDict{
	if ([elementName isEqualToString:@"button"]) {
		// Call Control's initialize method to parse xml using NSXMLParser
		Control *control = [[Control alloc] initWithXMLParser:parser elementName:elementName attributes:attributeDict parentDelegate:self];
		[controls addObject:control];
		[control release];
	}
}


- (void)dealloc {
	[name release];
	[icon release];
	[controls release];
	
	[super dealloc];
}
@end
