//
//  URLConnectionHelper.h
//  openremote
//
//  Created by wei allen on 09-2-19.
//  Copyright 2009 finalist. All rights reserved.
//

#import <UIKit/UIKit.h>

//Define a protocol 
@protocol URLConnectionHelperDelegate <NSObject>

- (void)definitionURLConnectionDidFinishLoading:(NSData *)data;

@end

@interface URLConnectionHelper : NSObject {
	//this object must implement this protocal like a interface in java.So we can ensure to call the mehod in deleget instance.
	id <URLConnectionHelperDelegate> delegate;
	NSMutableData *receivedData;
}

- (id)initWithURL:(NSURL *)url delegate:(id <URLConnectionHelperDelegate>)delegate;
- (id)initWithRequest:(NSURLRequest *)request delegate:(id <URLConnectionHelperDelegate>)d ;

@property(nonatomic,retain) id <URLConnectionHelperDelegate> delegate;

@end
