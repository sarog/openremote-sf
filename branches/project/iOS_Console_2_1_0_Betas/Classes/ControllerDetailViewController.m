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
#import "ORControllerGroupMembersFetchStatusIconProvider.h"

// TODO: customize keyboard with keys such as http://, https://, /controller, 8080,  and other std ports to help text entry
// EBR : not sure we really want the above ?

#define kControllerUrlCellIdentifier @"kControllerUrlCellIdentifier"
#define kGroupMemberCellIdentifier @"kGroupMemberCellIdentifier"

@interface ControllerDetailViewController()

@property (nonatomic, retain) ORController *controller;
@property (nonatomic, retain) NSArray *groupMembers;
@property (nonatomic, retain) UITextField *urlField;

- (void)updateTableViewHeaderForGroupMemberFetchStatus;

@end

@implementation ControllerDetailViewController

@synthesize delegate;

@synthesize controller;
@synthesize groupMembers;
@synthesize urlField;

- (id)initWithController:(ORController *)aController
{
	self = [super initWithStyle:UITableViewStyleGrouped];
    if (self) {
        self.controller = aController;
        self.groupMembers = [self.controller.groupMembers allObjects];
        [self.controller addObserver:self forKeyPath:@"groupMembers" options:0 context:NULL];
    }
	return self;
}

- (void)dealloc
{
    [self.controller removeObserver:self forKeyPath:@"groupMembers"];
    self.controller = nil;
    self.urlField.delegate = nil;
    self.urlField = nil;
    [super dealloc];
}

#pragma mark - View lifecycle

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(orControllerGroupMembersFetchStatusChanged:) name:kORControllerGroupMembersFetchingNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(orControllerGroupMembersFetchStatusChanged:) name:kORControllerGroupMembersFetchFailedNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(orControllerGroupMembersFetchStatusChanged:) name:kORControllerGroupMembersFetchSucceededNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(orControllerGroupMembersFetchRequiresAuthentication:) name:kORControllerGroupMembersFetchRequiresAuthenticationNotification object:nil];
    
    [self updateTableViewHeaderForGroupMemberFetchStatus];

}

- (void)viewDidUnload
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    [super viewDidUnload];
}

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
    return 2;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    switch (section) {
        case 0:
            return 1;
        case 1:
            return [self.groupMembers count];
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
        }
    }
    return cell;
}

- (CGFloat)tableView:(UITableView *)aTableView heightForHeaderInSection:(NSInteger)section
{
	return 40.0;
}

- (NSString *)tableView:(UITableView *)tableView titleForFooterInSection:(NSInteger)section
{
	return (section == 0)?@"Sample:192.168.1.2:8080/controller":@"";
}

- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section
{
    switch (section) {
        case 0:
            return @"Controller URL:";
        case 1:
            return @"Roundrobin group members:";
        default:
            return nil;
    }
}

#pragma mark - Table view delegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [tableView deselectRowAtIndexPath:indexPath animated:NO];
}

#pragma mark - KVO

- (void)observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary *)change context:(void *)context
{
    // Only observing one value, no need to check
    self.groupMembers = [self.controller.groupMembers allObjects];
    [self.tableView reloadSections:[NSIndexSet indexSetWithIndex:1] withRowAnimation:UITableViewRowAnimationFade];
}

#pragma mark - ORController group members fetch notifications

- (void)orControllerGroupMembersFetchStatusChanged:(NSNotification *)notification
{
    [self updateTableViewHeaderForGroupMemberFetchStatus];
}

- (void)orControllerGroupMembersFetchRequiresAuthentication:(NSNotification *)notification
{
    [self orControllerGroupMembersFetchStatusChanged:notification];
    
    // TODO
    /*
    if (settingsManager.consoleSettings.selectedController == [notification object]) {
        [self populateLoginView:self];
    }
     */
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

@end