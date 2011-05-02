//
//  ORGroupMember.h
//  openremote
//
//  Created by Eric Bariaux on 29/04/11.
//  Copyright (c) 2011 OpenRemote, Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

@class ORController;

@interface ORGroupMember : NSManagedObject {
@private
}

@property (nonatomic, retain) NSString * url;
@property (nonatomic, retain) NSDate * lastFailureDate;
@property (nonatomic, retain) NSNumber * failuresCount;
@property (nonatomic, retain) ORController * controller;

@end
