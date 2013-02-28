//
//  Activity.h
//  openremote
//
//  Created by Dennis Stevense on 26-02-09.
//  Copyright 2009 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface Activity : NSObject {
	int activityId;
	NSString *name;
	NSString *icon;
	NSMutableArray *screens;
	
	NSObject *xmlParserParentDelegate;
}

- (id)initWithXMLParser:(NSXMLParser *)parser elementName:(NSString *)elementName attributes:(NSDictionary *)attributeDict parentDelegate:(NSObject *)parent;

@property (nonatomic,readonly) int activityId;
@property (nonatomic,readonly) NSString *name;
@property (nonatomic,readonly) NSString *icon;
@property (nonatomic,readonly) NSArray *screens;

@end
