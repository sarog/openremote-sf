//
//  Screen.h
//  openremote
//
//  Created by wei allen on 09-2-19.
//  Copyright 2009 finalist. All rights reserved.
//

#import <UIKit/UIKit.h>


@interface Screen : NSObject {
	NSString *name;
	NSString *icon;
	NSMutableArray *controls;
	
	int rows;
	int cols;
	
	NSObject *xmlParserParentDelegate;
}

- (id)initWithXMLParser:(NSXMLParser *)parser elementName:(NSString *)elementName attributes:(NSDictionary *)attributeDict parentDelegate:(NSObject *)parent;

@property (nonatomic,readonly) NSString *name;
@property (nonatomic,readonly) NSString *icon;
@property (nonatomic,readonly) NSArray *controls;

@property (nonatomic,readonly) int rows;
@property (nonatomic,readonly) int cols;

@end
