//
//  Style1TextEntryCell.h
//  openremote
//
//  Created by Eric Bariaux on 19/08/11.
//  Copyright 2011 OpenRemote, Inc. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface StyleValue1TextEntryCell : UITableViewCell {

    UITextField *textField;
}

@property (nonatomic, readonly) UITextField *textField;

- (id)initWithReuseIdentifier:(NSString *)reuseIdentifier;

@end
