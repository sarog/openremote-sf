//
//  ControllerDetailViewController.h
//  openremote
//
//  Created by Eric Bariaux on 11/07/11.
//  Copyright 2011 OpenRemote, Inc. All rights reserved.
//

#import <UIKit/UIKit.h>

@class ORController;

@protocol ControllerDetailViewControllerDelegate <NSObject>

- (void)didAddServerURL:(NSString *)serverURL;
- (void)didEditController:(ORController *)controller;

@end

@interface ControllerDetailViewController : UITableViewController <UITextFieldDelegate> {
    
}

@property (nonatomic, retain) NSObject<ControllerDetailViewControllerDelegate> *delegate;

- (id)initWithController:(ORController *)aController;

@end
