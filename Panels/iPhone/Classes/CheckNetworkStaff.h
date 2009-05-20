//
//  CheckNetworkStaff.h
//  openremote
//
//  Created by finalist on 5/19/09.
//  Copyright 2009 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>
enum CheckNetworkStatusFlag {
	kCheckNetworkStepOK = 1 << 0,
	kNoNetwork = 1 << 1,
	kIPAddressIsWrong = 1 << 2,
	kControllerNotStarted = 1 << 3,
	kControllerNotFindApp = 1 << 4,
	kControllerNotFindXml = 1 << 5,
	kNoConfgedServerUrl = 1 << 6,
};

@interface CheckNetworkStaff : NSObject {

}

+ (int)checkWhetherNetworkAvailable;
+ (int)checkIPAddress;
+ (int)checkControllerAvailable;
+ (int)checkXmlExist;
@end
