//
//  CheckNetworkStaff.h
//  openremote
//
//  Created by finalist on 5/19/09.
//  Copyright 2009 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface CheckNetworkStaff : NSObject {

}

+ (void)checkWhetherNetworkAvailable;
+ (void)checkIPAddress;
+ (void)checkControllerAvailable;
+ (void)checkXmlExist;
+ (void)checkAll;
@end
