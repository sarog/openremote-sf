//
//  NSURLHelperTest.m
//  openremote
//
//  Created by Eric Bariaux on 02/05/12.
//  Copyright (c) 2012 OpenRemote, Inc. All rights reserved.
//

#import "NSURLHelperTest.h"
#import "NSURLHelper.h"

@implementation NSURLHelperTest

- (void)testValidURLs
{
    STAssertNotNil([NSURLHelper parseControllerURL:@"http://localhost:8080/controller"], @"");
    STAssertNotNil([NSURLHelper parseControllerURL:@"https://localhost:8080/controller"], @"");
    STAssertNotNil([NSURLHelper parseControllerURL:@"localhost:8080/controller"], @"");    
    STAssertNil([NSURLHelper parseControllerURL:@"http:8080/controller"], @"");
    // TODO: I would have liked that to fail, but it is considered a valid URL and still valid if prepending http://
//    STAssertNil([NSURLHelper parseControllerURL:@"error://localhost:8080/controller"], @"");
    STAssertNil([NSURLHelper parseControllerURL:@":8080/controller"], @"");
}

@end
