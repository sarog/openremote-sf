//
//  Style1TextEntryCell.m
//  openremote
//
//  Created by Eric Bariaux on 19/08/11.
//  Copyright 2011 OpenRemote, Inc. All rights reserved.
//

#import "StyleValue1TextEntryCell.h"

@implementation StyleValue1TextEntryCell

@synthesize textField;

- (id)initWithReuseIdentifier:(NSString *)reuseIdentifier
{
    self = [super initWithStyle:UITableViewCellStyleValue1 reuseIdentifier:reuseIdentifier];
    if (self) {
        textField = [[UITextField alloc] initWithFrame:CGRectZero];
        textField.autocapitalizationType = UITextAutocapitalizationTypeNone;// no auto capitalization support
        textField.autocorrectionType = UITextAutocorrectionTypeNo;// no auto correction support
        self.selectionStyle = UITableViewCellSelectionStyleNone;
        [self.contentView addSubview:textField];
        [textField release];
    }
    return self;
}

- (void)layoutSubviews
{
    [super layoutSubviews];
    
    textField.font = self.detailTextLabel.font;
    textField.textColor = self.detailTextLabel.textColor;
    int offset = 300;
    textField.frame = CGRectMake(self.textLabel.frame.origin.x + offset, self.textLabel.frame.origin.y, self.contentView.bounds.size.width - offset - self.textLabel.frame.origin.x, self.textLabel.frame.size.height);
}

@end
