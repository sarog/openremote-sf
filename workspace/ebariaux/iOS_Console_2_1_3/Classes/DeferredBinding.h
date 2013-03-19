//
//  DeferredBinding.h
//  openremote
//
//  Created by Eric Bariaux on 26/08/11.
//  Copyright 2011 OpenRemote, Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

@class Definition;

@interface DeferredBinding : NSObject

@property (nonatomic, assign, readonly) int boundComponentId;
@property (nonatomic, retain, readonly) id enclosingObject;
@property (nonatomic, assign) Definition *definition;

- (id)initWithBoundComponentId:(int)anId enclosingObject:(id)anEnclosingObject;

- (void)bind;

@end
