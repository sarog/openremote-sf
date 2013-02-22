//
//  ButtonCommandDeferredBinding.h
//  openremote
//
//  Created by Eric Bariaux on 09/05/12.
//  Copyright (c) 2012 OpenRemote, Inc. All rights reserved.
//

#import "DeferredBinding.h"

@interface ControllerComponentCommandDeferredBinding : DeferredBinding

- (id)initWithBoundComponentId:(int)anId enclosingObject:(id)anEnclosingObject action:(NSString *)anAction;

@end