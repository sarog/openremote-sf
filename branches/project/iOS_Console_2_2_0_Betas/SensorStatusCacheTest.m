//
//  SensorStatusCacheTest.m
//  openremote
//
//  Created by Eric Bariaux on 05/05/12.
//  Copyright (c) 2012 OpenRemote, Inc. All rights reserved.
//

#import "SensorStatusCacheTest.h"
#import "SensorStatusCache.h"
#import "NotificationConstant.h"
#import "OCMockObject.h"

@interface SensorStatusCacheTest ()

@property (nonatomic, retain) SensorStatusCache *statusCache;

@end

@implementation SensorStatusCacheTest

- (void)dealloc
{
    self.statusCache = nil;
    [super dealloc];
}

- (void)setUp
{
    self.statusCache = [[[SensorStatusCache alloc] initWithNotificationCenter:[NSNotificationCenter defaultCenter]] autorelease];
}

- (void)tearDown
{
    self.statusCache = nil;
}

- (void)testPublishedValueNotificationSent
{    
    id mockCenter = [OCMockObject mockForClass:[NSNotificationCenter class]];
    SensorStatusCache *cache = [[SensorStatusCache alloc] initWithNotificationCenter:mockCenter];
    [[mockCenter expect] postNotificationName:[NSString stringWithFormat:NotificationPollingStatusIdFormat, 1] object:cache];
    [cache publishNewValue:@"Value1" forSensorId:@"1"];
    [mockCenter verify];
    [cache release];
}

- (void)testReadingValueFromCache
{
    [self.statusCache publishNewValue:@"Value1" forSensorId:@"1"];
    STAssertEqualObjects([self.statusCache valueForSensorId:@"1"], @"Value1", @"Value should have been updated to Value1");
    [self.statusCache publishNewValue:@"Value2" forSensorId:@"1"];
    STAssertEqualObjects([self.statusCache valueForSensorId:@"1"], @"Value2", @"Value should have been updated to Value2");
}

- (void)testReadingValueFromCacheAfterCacheCleaned
{
    [self.statusCache publishNewValue:@"Value1" forSensorId:@"1"];
    [self.statusCache clearStatusCacheRemovingObservers:NO];
    STAssertNil([self.statusCache valueForSensorId:@"1"], @"Value should not be in cache after it was cleared");
}

@synthesize statusCache;

@end