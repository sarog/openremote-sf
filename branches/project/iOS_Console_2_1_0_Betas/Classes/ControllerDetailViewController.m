//
//  ControllerDetailViewController.m
//  openremote
//
//  Created by Eric Bariaux on 11/07/11.
//  Copyright 2011 OpenRemote, Inc. All rights reserved.
//

#import "ControllerDetailViewController.h"
#import "ORController.h"
#import "TextFieldCell.h"

// TODO: customize keyboard with keys such as http://, https://, /controller, 8080,  and other std ports to help text entry
// EBR : not sure we really want the above ?

#define kControllerUrlCellIdentifier @"kControllerUrlCellIdentifier"

@interface ControllerDetailViewController()

@property (nonatomic, retain) ORController *controller;
@property (nonatomic, retain) UITextField *urlField;

@end

@implementation ControllerDetailViewController

@synthesize delegate;

@synthesize controller;
@synthesize urlField;

- (id)initWithController:(ORController *)aController
{
	self = [super initWithStyle:UITableViewStyleGrouped];
    if (self) {
        self.controller = aController;
    }
	return self;
}

- (void)dealloc
{
    self.controller = nil;
    self.urlField.delegate = nil;
    self.urlField = nil;
    [super dealloc];
}

#pragma mark - View lifecycle

- (void)viewWillDisappear:(BOOL)animated
{
    [self.urlField resignFirstResponder];
    [super viewWillDisappear:animated];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    if (self.controller) {
		self.title = [NSString stringWithFormat:@"Editing %@", self.controller.primaryURL];
	} else {
        
        // TODO: should create a temporary object, but CoreData -> need managed object context, ...
        
		self.title = @"Add a Controller";
	}
}

- (void)viewDidAppear:(BOOL)animated
{
    [self.urlField becomeFirstResponder];
    [super viewDidAppear:animated];
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return YES;
}

#pragma mark - UITextField delegate

- (BOOL)textFieldShouldReturn:(UITextField *)textField
{   
    NSString *url = nil;
    
    if ([textField.text hasPrefix:@"http://"] || [textField.text hasPrefix:@"https://"]) {
        url = textField.text;
    } else {
        url = [NSString stringWithFormat:@"http://%@", textField.text];
    }
    
    // TODO: have better validation and non intrusive error messages
    // following test will never fail as we set the scheme above
    
	NSURL *nsUrl = [NSURL URLWithString:url];
	if ([nsUrl scheme] == nil) {
//		[ViewHelper showAlertViewWithTitle:@"" Message:@"URL is invalid."];
		return NO;
	}
    
    if (self.controller) {
        self.controller.primaryURL = url;
        [delegate didEditController:self.controller];
    } else {
        [delegate didAddServerURL:url];        
    }
	return YES;
}

#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return 1;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:kControllerUrlCellIdentifier];
    if (cell == nil) {
		cell = [[TextFieldCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:kControllerUrlCellIdentifier];
        urlField = ((TextFieldCell *)cell).textField;
        urlField.delegate = self;
    }
    urlField.text = self.controller.primaryURL;
    
    return cell;
}

- (CGFloat)tableView:(UITableView *)aTableView heightForHeaderInSection:(NSInteger)section
{
	return 40.0;
}

- (NSString *)tableView:(UITableView *)tableView titleForFooterInSection:(NSInteger)section
{
	return @"Sample:192.168.1.2:8080/controller";
}

- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section
{
	return @"Controller URL:";
}

#pragma mark - Table view delegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [tableView deselectRowAtIndexPath:indexPath animated:NO];
}

@end