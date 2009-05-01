//
//  Control.h
//  openremote
//
//  Created by wei allen on 09-2-19.
//  Copyright 2009 finalist. All rights reserved.
//

#import <UIKit/UIKit.h>


@interface Control : NSObject {
	NSString *label;
	NSString *icon;
	int x;
	int y;
	int width;
	int height;
	int eventID;
	
	NSObject *xmlParserParentDelegate;
}

- (id)initWithXMLParser:(NSXMLParser *)parser elementName:(NSString *)elementName attributes:(NSDictionary *)attributeDict parentDelegate:(NSObject *)parent;


@property (nonatomic,readonly) NSString *label;
@property (nonatomic,readonly) NSString *icon;
@property (nonatomic,readonly) int x;
@property (nonatomic,readonly) int y;
@property (nonatomic,readonly) int width;
@property (nonatomic,readonly) int height;
@property (nonatomic,readonly) int eventID;

@end
