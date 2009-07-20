//
//  CheckNetworkStaffException.h
//  openremote
//
//  Created by finalist on 5/31/09.
//  Copyright 2009 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>


@interface CheckNetworkStaffException : NSException {
	NSString *title;
	NSString *message;
}

+ (CheckNetworkStaffException *)exceptionWithTitle:(NSString *)t message:(NSString *)msg; 

@property (nonatomic,copy) NSString *title;
@property (nonatomic,copy) NSString *message;

@end
