/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2011, OpenRemote Inc.
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

#import "ControllerDetailViewController.h"
#import "ORController.h"
#import "ORGroupMember.h"
#import "TextFieldCell.h"
#import "StyleValue1TextEntryCell.h"
#import "ORControllerGroupMembersFetchStatusIconProvider.h"

#define kControllerUrlCellIdentifier @"kControllerUrlCellIdentifier"
#define kGroupMemberCellIdentifier @"kGroupMemberCellIdentifier"
#define kUsernameCellIdentifier @"kUsernameCellIdentifier"
#define kPasswordCellIdentifier @"kPasswordCellIdentifier"

@interface ControllerDetailViewController()

@property (nonatomic, retain) NSManagedObjectContext *managedObjectContext;
@property (nonatomic, retain) ORController *controller;
@property (nonatomic, retain) UITextField *usernameField;
@property (nonatomic, retain) UITextField *passwordField;
@property (nonatomic, retain) UITextField *urlField;
// We're using this group member property instead of accessing controller.groupMembers directly
// because we want an array to have an order to display in table view
// We observe controller.groupMembers to keep this on in sync
@property (nonatomic, retain) NSArray *groupMembers;
// Used to indicate that the done button has been clicked -> cancel management because no target/action on back button
@property (nonatomic, assign) BOOL doneAction;
@property (nonatomic, assign) BOOL creating;
@property (nonatomic, retain) NSUndoManager *previousUndoManager;
@property (nonatomic, retain) UIColor *originalTextColor;

- (void)updateTableViewHeaderForGroupMemberFetchStatus;
- (void)refreshGroupMemberTableViewSection;

@end

@implementation ControllerDetailViewController

- (id)initWithController:(ORController *)aController
{
	self = [super initWithStyle:UITableViewStyleGrouped];
    if (self) {
        self.controller = aController;
    }
	return self;
}

- (id)initWithManagedObjectContext:(NSManagedObjectContext *)moc
{
	self = [super initWithStyle:UITableViewStyleGrouped];
    if (self) {
        self.managedObjectContext = moc;
    }
	return self;
}

- (void)dealloc
{
    self.controller = nil;
    self.managedObjectContext = nil;
    self.urlField = nil;
    self.usernameField = nil;
    self.passwordField = nil;
    self.previousUndoManager = nil;
    self.originalTextColor = nil;
    [super dealloc];
}

#pragma mark - View lifecycle

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(orControllerGroupMembersFetchStatusChanged:) name:kORControllerGroupMembersFetchingNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(orControllerGroupMembersFetchStatusChanged:) name:kORControllerGroupMembersFetchFailedNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(orControllerGroupMembersFetchStatusChanged:) name:kORControllerGroupMembersFetchSucceededNotification object:nil];
    // We don't present a login panel when on this page, user can use "regular" fields to enter credentials
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(orControllerGroupMembersFetchStatusChanged:) name:kORControllerGroupMembersFetchRequiresAuthenticationNotification object:nil];
    
    self.navigationItem.rightBarButtonItem = [[[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemDone target:self action:@selector(done:)] autorelease];
    
    [self updateTableViewHeaderForGroupMemberFetchStatus];
}

- (void)viewDidUnload
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    self.urlField = nil;
    self.usernameField = nil;
    self.passwordField = nil;

    [super viewDidUnload];
}

- (void)viewWillDisappear:(BOOL)animated
{
    [self.managedObjectContext.undoManager endUndoGrouping];

    // Prevents update of values when text field looses 1st responder
    self.urlField.delegate = nil;
    self.usernameField.delegate = nil;
    self.passwordField.delegate = nil;

    if (!self.doneAction) {
        [self.controller.managedObjectContext undo];        
    }
    self.controller.managedObjectContext.undoManager = self.previousUndoManager;
    self.previousUndoManager = nil;

    [self.controller removeObserver:self forKeyPath:@"groupMembers"];

    [super viewWillDisappear:animated];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
    self.doneAction = NO;
    
    // Make sure we're delegate of text fields if they exist so we get values update
    if (self.urlField) {
        self.urlField.delegate = self;
    }
    if (self.usernameField) {
        self.usernameField.delegate = self;
    }
    if (self.passwordField) {
        self.passwordField.delegate = self;
    }

    if (self.controller) {
        self.managedObjectContext = self.controller.managedObjectContext;
        self.creating = NO;
		self.title = [NSString stringWithFormat:@"Editing %@", self.controller.primaryURL];
	} else {
        NSAssert(self.managedObjectContext, @"If no controller was specified, a managed object context must be");
        self.controller = [NSEntityDescription insertNewObjectForEntityForName:@"ORController" inManagedObjectContext:self.managedObjectContext];
        self.creating = YES;
		self.title = @"Add a Controller";
	}
    self.previousUndoManager = self.managedObjectContext.undoManager;
    self.managedObjectContext.undoManager = [[[NSUndoManager alloc] init] autorelease];
    [self.managedObjectContext.undoManager beginUndoGrouping];
    
    self.groupMembers = [self.controller.groupMembers allObjects];
    [self.controller addObserver:self forKeyPath:@"groupMembers" options:0 context:NULL];
}

- (void)viewDidAppear:(BOOL)animated
{
    if (self.creating) {
        [self.urlField becomeFirstResponder];
    }
    [super viewDidAppear:animated];
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return YES;
}

#pragma mark - Actions

- (void)done:(id)sender
{
    [self.urlField resignFirstResponder];
    [self.usernameField resignFirstResponder];
    [self.passwordField resignFirstResponder];
    if (!self.controller || !self.controller.primaryURL) {
        [self.delegate didFailToAddController];
        return;
    }
    self.doneAction = YES;
    if (self.creating) {
        [self.delegate didAddController:self.controller];      
    } else {
        [self.delegate didEditController:self.controller];
    }
}

#pragma mark - UITextField delegate

- (BOOL)textFieldShouldEndEditing:(UITextField *)textField
{
    
    NSString *url;
    if (textField == self.urlField) {
        url = [textField.text stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]];
        if (![url hasPrefix:@"http://"] && ![url hasPrefix:@"https://"]) {
            url = [NSString stringWithFormat:@"http://%@", url];
        }
        
        // TODO: have better validation and non intrusive error messages
        // Following test does fail if what follows :// is totally wrong
        
        NSURL *nsUrl = [NSURL URLWithString:url];
        if ([nsUrl scheme] == nil) {
            self.urlField.textColor = [UIColor redColor];
            return NO;
        }
        self.urlField.textColor = self.originalTextColor;
        self.urlField.text = url;
        self.controller.primaryURL = url;
    } else if (textField == self.usernameField) {
        self.controller.userName = textField.text;
    } else if (textField == self.passwordField) {
        self.controller.password = textField.text;
    }
    [self.controller cancelGroupMembersFetch];
    self.groupMembers = nil;
    [self.controller fetchGroupMembers];
    return YES;
}

- (BOOL)textFieldShouldReturn:(UITextField *)textField
{
    [textField resignFirstResponder];
    return YES;
}

#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 3;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    switch (section) {
        case 0:
            return 1;
        case 1:
            return [self.groupMembers count];
        case 2:
            return 2;
        default:
            return 0;
    }
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    UITableViewCell *cell = nil;
    switch (indexPath.section) {
        case 0:
        {
            cell = [tableView dequeueReusableCellWithIdentifier:kControllerUrlCellIdentifier];
            if (cell == nil) {
                cell = [[[TextFieldCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:kControllerUrlCellIdentifier] autorelease];
                self.urlField = ((TextFieldCell *)cell).textField;
                self.urlField.delegate = self;
                self.originalTextColor = self.urlField.textColor;
            }
            self.urlField.text = self.controller.primaryURL;
            break;
        }
        case 1:
        {
            cell = [tableView dequeueReusableCellWithIdentifier:kGroupMemberCellIdentifier];
            if (cell == nil) {
                cell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:kGroupMemberCellIdentifier] autorelease];
            }            
            cell.textLabel.text = ((ORGroupMember *)[self.groupMembers objectAtIndex:indexPath.row]).url;
            break;
        }
        case 2:
        {
            switch (indexPath.row) {
                case 0:
                    cell = [tableView dequeueReusableCellWithIdentifier:kUsernameCellIdentifier];
                    if (cell == nil) {
                        cell = [[[StyleValue1TextEntryCell alloc] initWithReuseIdentifier:kUsernameCellIdentifier] autorelease];
                        self.usernameField = ((TextFieldCell *)cell).textField;
                        self.usernameField.delegate = self;
                    }
                    cell.textLabel.text = @"User name";
                    ((TextFieldCell *)cell).textField.text = self.controller.userName;
                    break;
                case 1:
                    cell = [tableView dequeueReusableCellWithIdentifier:kPasswordCellIdentifier];
                    if (cell == nil) {
                        cell = [[[StyleValue1TextEntryCell alloc] initWithReuseIdentifier:kPasswordCellIdentifier] autorelease];
                        self.passwordField = ((TextFieldCell *)cell).textField;
                        self.passwordField.secureTextEntry = YES;

                        self.passwordField.delegate = self;
                    }
                    cell.textLabel.text = @"Password";
                    ((TextFieldCell *)cell).textField.text = self.controller.password;
                    ((TextFieldCell *)cell).textField.secureTextEntry = YES;
                    break;
            }
        }
    }
    return cell;
}

- (NSString *)tableView:(UITableView *)tableView titleForFooterInSection:(NSInteger)section
{
	return (section == 0)?@"Sample:192.168.1.2:8080/controller":@"";
}

- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section
{
    switch (section) {
        case 0:
            // Handled by custom view so error message can be displayed
            return nil;
        case 1:
            return @"Roundrobin group members:";
        case 2:
            return @"Login:";
        default:
            return nil;
    }
}

#pragma mark - Table view delegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [tableView deselectRowAtIndexPath:indexPath animated:NO];
}

- (CGFloat)tableView:(UITableView *)aTableView heightForHeaderInSection:(NSInteger)section
{
	return 40.0;
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section
{
    if (section == 0) {
        // Note: all this can be accomplish with just the tableView:titleForHeaderInSection: method
        // This has been added to add display of error message in section title.
        // See IPHONE-123 for more information
        UIView *v = [[[UIView alloc] initWithFrame:CGRectMake(0.0, 0.0, 20.0, tableView.frame.size.width)] autorelease];
        UILabel *l = [[UILabel alloc] initWithFrame:CGRectMake(54.0, 11.0, 330.0, 21.0)]; // TODO: compute width
        l.text = @"Controller URL:";
        l.font = [UIFont boldSystemFontOfSize:17];
        l.textColor = [UIColor colorWithRed:0.298039 green:0.337255 blue:0.423529 alpha:1.0];
        l.backgroundColor = [UIColor groupTableViewBackgroundColor];
        [v addSubview:l];
        [l release];
        return v;
    }
    return nil;
}

#pragma mark - KVO

- (void)observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary *)change context:(void *)context
{
    // Only observing one value, no need to check
    self.groupMembers = [self.controller.groupMembers allObjects];
}

#pragma mark - ORController group members fetch notifications

- (void)orControllerGroupMembersFetchStatusChanged:(NSNotification *)notification
{
    [self updateTableViewHeaderForGroupMemberFetchStatus];
}

#pragma mark - Utility methods

- (void)updateTableViewHeaderForGroupMemberFetchStatus
{
    UIView *statusView = [ORControllerGroupMembersFetchStatusIconProvider viewForGroupMembersFetchStatus:self.controller.groupMembersFetchStatus];
    CGRect sectionBounds = [self.tableView rectForSection:0];
    UIView *aView = [[UIView alloc] initWithFrame:CGRectMake(sectionBounds.origin.x, 0.0, sectionBounds.size.width, 40.0)];
    // Status view is centered but with a 12 points offset from top. Also offset 44 from right border to align on rows' border
    statusView.frame = CGRectMake(sectionBounds.size.width - statusView.frame.size.width - 44.0, (int)(12.0 + (aView.frame.size.height - statusView.frame.size.height)/ 2.0), statusView.frame.size.width, statusView.frame.size.height);
    [aView addSubview:statusView];
    self.tableView.tableHeaderView = aView;
    [aView release];    
}

- (void)refreshGroupMemberTableViewSection
{
    [self.tableView reloadSections:[NSIndexSet indexSetWithIndex:1] withRowAnimation:UITableViewRowAnimationFade];
}

@synthesize delegate;
@synthesize managedObjectContext;
@synthesize controller;
@synthesize groupMembers;
@synthesize usernameField;
@synthesize passwordField;
@synthesize urlField;
@synthesize doneAction;
@synthesize creating;
@synthesize previousUndoManager;
@synthesize originalTextColor;

- (void)setGroupMembers:(NSArray *)theGroupMembers
{
    if (groupMembers != theGroupMembers) {
        [groupMembers release];
        groupMembers = [theGroupMembers retain];
        // This must be performed after the setter finishes, otherwise the data source code using the getter does not see the new value
        [self performSelector:@selector(refreshGroupMemberTableViewSection) withObject:nil afterDelay:0.0];
    }
}

@end