/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2012, OpenRemote Inc.
 *
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
#import "LoginViewController.h"
#import "Definition.h"
#import "ViewHelper.h"
#import "NotificationConstant.h"
#import "ORConsoleSettingsManager.h"
#import "ORConsoleSettings.h"
#import "ORController.h"

@interface LoginViewController ()

- (void)goBack:(id)sender;

@property (nonatomic, retain) UITextField *usernameField;
@property (nonatomic, retain) UITextField *passwordField;
@property (nonatomic, assign) NSObject <LoginViewControllerDelegate> *delegate;
@property (nonatomic, retain, readwrite) id context;
 
@end

@implementation LoginViewController

- (id)initWithDelegate:(NSObject <LoginViewControllerDelegate> *)aDelegate context:(id)aContext
{
    self = [super initWithStyle:UITableViewStyleGrouped];
	if (self) {
		[self setTitle:@"Sign in"];
        self.delegate = aDelegate;
        self.context = aContext;
	}
	return self;
}

- (void)dealloc
{
    self.delegate = nil;
    self.context = nil;
    self.usernameField = nil;
    self.passwordField = nil;	
	[super dealloc];
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	[[NSNotificationCenter defaultCenter] postNotificationName:NotificationHideLoading object:nil];
	self.navigationItem.leftBarButtonItem = [[[UIBarButtonItem alloc]initWithTitle:@"Back" style:UIBarButtonItemStylePlain target:self action:@selector(goBack:)] autorelease];
}

- (void)viewDidUnload
{
	[[NSNotificationCenter defaultCenter] postNotificationName:NotificationShowLoading object:nil];
    [super viewDidUnload];
}

// Back to the view where loginView was triggered from.
- (void)goBack:(id)sender
{
    [self.delegate loginViewControllerDidCancelLogin:self];
    /*
     OLD CODE
	[self dismissModalViewControllerAnimated:YES];
	if ([theDelegate respondsToSelector:@selector(onBackFromLogin)]) {
		[theDelegate performSelector:@selector(onBackFromLogin)];
	}
     */
}

// Send sign in request to remote controller server by loginViewController's delegate.
- (void)signin:(id)sender
{
	if (self.usernameField.text == nil || self.passwordField.text == nil ||
			[@"" isEqualToString:self.usernameField.text] || [@"" isEqualToString:self.passwordField.text]) {
		[ViewHelper showAlertViewWithTitle:@"" Message:@"No username or password entered."];
		return;
	}
    
    [self.delegate loginViewController:self didProvideUserName:self.usernameField.text password:self.passwordField.text];

    /*
     OLD CODE
    // Why was it here ?
	// [[NSNotificationCenter defaultCenter] postNotificationName:NotificationShowLoading object:nil];
    
    
    // TODO: All this should not be in here, we are just presenting username/password request, we should not be concerned about how this gets used
    // TODO: + should not use selectedController but controller for which credentials were requested -> pass it somehow
    ORController *activeController = [ORConsoleSettingsManager sharedORConsoleSettingsManager].consoleSettings.selectedController;
    activeController.userName = usernameField.text;
	activeController.password = passwordField.text;
    
    // TODO: we might not want to save here, maybe have a method to set this and save in dedicated MOC
    [[ORConsoleSettingsManager sharedORConsoleSettingsManager] saveConsoleSettings];
    
	[self dismissModalViewControllerAnimated:NO];
	if ([theDelegate respondsToSelector:@selector(onSignin)]) {
		[theDelegate performSelector:@selector(onSignin)];
	}
     */
}

#pragma mark Table view methods

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 2;
}

// Customize the number of rows in the table view.
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
	if (section == 0) {
		return 2;
	} else if (section == 1) {
		return 1;
	}
	return 0;
}

// Customize the appearance of table view cells.
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{    
	static NSString *loginCellIdentifier = @"loginCell";
	
	UITableViewCell *loginCell = [tableView dequeueReusableCellWithIdentifier:loginCellIdentifier];

	if (loginCell == nil) {
		loginCell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:loginCellIdentifier] autorelease];
		loginCell.selectionStyle = UITableViewCellSelectionStyleNone;
	}

	if (indexPath.section == 0) {
		UITextField *textField = [[UITextField alloc] initWithFrame:CGRectZero];
		textField.contentVerticalAlignment = UIControlContentVerticalAlignmentCenter;
		textField.font = [UIFont systemFontOfSize:22];
		textField.keyboardType = UIKeyboardTypeURL;
		textField.adjustsFontSizeToFitWidth = YES;
		textField.autocapitalizationType = UITextAutocapitalizationTypeNone;
		textField.autocorrectionType = UITextAutocorrectionTypeNo;
		textField.textColor = [UIColor darkGrayColor];
		textField.returnKeyType = UIReturnKeyDone;
		textField.frame = CGRectInset(CGRectMake(100, (loginCell.bounds.size.height - 26)/2.0, loginCell.bounds.size.width,26),10,0);
		[textField setDelegate:self];
		[loginCell.contentView addSubview:textField];
    
		if (indexPath.row == 0) {
			loginCell.textLabel.text = @"Username";
			[textField becomeFirstResponder];
			self.usernameField = textField;
            
            ORController *activeController = [ORConsoleSettingsManager sharedORConsoleSettingsManager].consoleSettings.selectedController;
			self.usernameField.text = activeController.userName; 
		} else if (indexPath.row == 1) {
			loginCell.textLabel.text = @"Password";
			[textField setSecureTextEntry:YES];
			self.passwordField = textField;
		}
        [textField release];
	} else if (indexPath.section == 1) {
		UIButton *signinButton = [UIButton buttonWithType:UIButtonTypeCustom];
		
		UIImage *buttonImage = [[UIImage imageNamed:@"btn_green.png"] stretchableImageWithLeftCapWidth:20 topCapHeight:20];
		[signinButton setBackgroundImage:buttonImage forState:UIControlStateNormal];
		float height = [tableView rectForRowAtIndexPath:indexPath].size.height;
		[signinButton setFrame:CGRectMake(0, 0, loginCell.frame.size.width, height)];
		signinButton.autoresizingMask = UIViewAutoresizingFlexibleWidth;
		signinButton.titleLabel.font = [UIFont boldSystemFontOfSize:18];
		[signinButton setTitle:@"Sign In" forState:UIControlStateNormal];
		[loginCell.contentView addSubview:signinButton];		
		[signinButton addTarget:self action:@selector(signin:) forControlEvents:UIControlEventTouchDown];
	}
	return loginCell;
}

- (void)tableView:(UITableView *)tableView willDisplayCell:(UITableViewCell *)cell forRowAtIndexPath:(NSIndexPath *)indexPath
{	
	[[cell textLabel] setBackgroundColor:[UIColor clearColor]];
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField
{	
	if ([@"" isEqualToString:self.usernameField.text]) {
		[self.usernameField becomeFirstResponder];
		return YES;
	} else {
		[self.passwordField becomeFirstResponder];		
	}
	
	if ([@"" isEqualToString:self.passwordField.text]) {
		[self.passwordField becomeFirstResponder];
		return YES;
	} else {
		[self.usernameField becomeFirstResponder];
	}
	
	[self signin:nil];

	return YES;
}

- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section
{
	if (section == 0) {
		return @"Sign in using your Controller username and password";
	}
	return nil;
}

- (NSString *)tableView:(UITableView *)tableView titleForFooterInSection:(NSInteger)section
{
	if (section == 1) {
		return @"Commands and updates from Controller are secured. This requires user authentication.";
	}
	return nil;
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)toInterfaceOrientation
{
	return YES;
}

@synthesize usernameField;
@synthesize passwordField;
@synthesize delegate;
@synthesize context;

@end