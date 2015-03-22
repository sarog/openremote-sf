/* OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2009, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3.0 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

#import "ChoosePanelViewController.h"
#import "URLConnectionHelper.h"
#import "ServerDefinition.h"
#import "AppSettingsDefinition.h"
#import "ControllerException.h"
#import "CredentialUtil.h"
#import "Definition.h"
#import "DataBaseService.h"
#import "NotificationConstant.h"

@interface ChoosePanelViewController (Private)

- (void)requestPanelList;

@end


@implementation ChoosePanelViewController


- (id)init {
	if (self = [super initWithStyle:UITableViewStyleGrouped]) {
		[self setTitle:@"Panel List"];
		panels = [[NSMutableArray alloc] init];
		chosenPanel = [[AppSettingsDefinition getPanelIdentityDic] objectForKey:@"identity"];
		[self requestPanelList];
	}
	return self;
}

// Load panel list from remote controller server.
- (void)requestPanelList {
	[[NSNotificationCenter defaultCenter] postNotificationName:NotificationShowLoading object:nil];
	NSString *location = [ServerDefinition panelsRESTUrl];
	NSURL *url = [[NSURL alloc] initWithString:location];
	NSLog(@"panels:%@", location);
	
	//assemble put request 
	NSMutableURLRequest *request = [[NSMutableURLRequest alloc] init];
	[request setURL:url];
	[request setHTTPMethod:@"GET"];
	[CredentialUtil addCredentialToNSMutableURLRequest:request];
	
	URLConnectionHelper *connection = [[URLConnectionHelper alloc] initWithRequest:request delegate:self];
	
	[url	 release];
	[request release];
	[connection autorelease];	
}

#pragma mark Table view methods

- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section {
		return @"Choose Your Panel Identity:";
}

- (NSString *)tableView:(UITableView *)tableView titleForFooterInSection:(NSInteger)section{
	return @"UI differs according to different panel identity.";
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
	return 1;
}


// Customize the number of rows in the table view.
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
	return panels.count;
}

// Customize the appearance of table view cells.
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
	static NSString *panelCellIdentifier = @"panelCell";
	UITableViewCell *panelCell = [tableView dequeueReusableCellWithIdentifier:panelCellIdentifier];
	
	if (panelCell == nil) {
		panelCell = [[[UITableViewCell alloc] initWithFrame:CGRectZero reuseIdentifier:panelCellIdentifier] autorelease];
	}
	
	panelCell.textLabel.text = [panels objectAtIndex:indexPath.row];
	panelCell.selectionStyle = UITableViewCellSelectionStyleNone;
	
	if ([panelCell.textLabel.text isEqualToString:chosenPanel]) {
		panelCell.accessoryType = UITableViewCellAccessoryCheckmark;
	}
	return panelCell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
	
	UITableViewCell *cell = [tableView cellForRowAtIndexPath:indexPath];
	if (currentSelectedPanelIndex) {
		UITableViewCell *oldCell = [tableView cellForRowAtIndexPath:currentSelectedPanelIndex];
		if (cell.accessoryType  == oldCell.accessoryType) {
			return;
		}
		if (oldCell.accessoryType == UITableViewCellAccessoryCheckmark) {
			oldCell.accessoryType = UITableViewCellAccessoryNone;
		} 
	} 
	if (cell.accessoryType == UITableViewCellAccessoryNone) {
		cell.accessoryType = UITableViewCellAccessoryCheckmark;
		[[AppSettingsDefinition getPanelIdentityDic] setObject:cell.textLabel.text forKey:@"identity"];
	} 
	
	currentSelectedPanelIndex = indexPath;
	
	[AppSettingsDefinition writeToFile];
	[self.navigationController popViewControllerAnimated:YES];
}

// Show login dialog for users, if users didn't login remote controller server.
- (void)showLoginAlert {
	
	UIAlertView *prompt = [[UIAlertView alloc] initWithTitle:@"Controller Login" 
													 message:@"\n\n\n" // IMPORTANT
													delegate:self 
										   cancelButtonTitle:@"Cancel" 
										   otherButtonTitles:@"OK", nil];
	
	textField = [[UITextField alloc] initWithFrame:CGRectMake(22.0, 50.0, 240.0, 25.0)]; 
	textField.autocapitalizationType = UITextAutocapitalizationTypeNone;
	textField.autocorrectionType = UITextAutocorrectionTypeNo;
	textField.clearButtonMode = UITextFieldViewModeWhileEditing;
	textField.returnKeyType = UIReturnKeyDone;
	[textField setBackgroundColor:[UIColor whiteColor]];
	[textField setPlaceholder:@"username"];
	if ([Definition sharedDefinition].username != nil) {
		[textField setText:[Definition sharedDefinition].username];
	}
	
	[prompt addSubview:textField];
	
	textField2 = [[UITextField alloc] initWithFrame:CGRectMake(22.0, 85.0, 240.0, 25.0)]; 
	[textField2 setBackgroundColor:[UIColor whiteColor]];
	textField2.autocapitalizationType = UITextAutocapitalizationTypeNone;
	textField2.autocorrectionType = UITextAutocorrectionTypeNo;
	textField2.clearButtonMode = UITextFieldViewModeWhileEditing;
	[textField2 setPlaceholder:@"password"];
	[textField2 setSecureTextEntry:YES];
	textField2.returnKeyType = UIReturnKeyDone;
	[prompt addSubview:textField2];
	
	// set place
	//[prompt setTransform:CGAffineTransformMakeTranslation(0.0, 110.0)];
	[prompt show];
	[prompt release];
	
	// set cursor and show keyboard
	[textField becomeFirstResponder];
	
}

#pragma mark alert delegate
- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex {
	if (buttonIndex == 1) {
		[Definition sharedDefinition].username = textField.text;
		[Definition sharedDefinition].password = textField2.text;
		[[DataBaseService sharedDataBaseService] saveCurrentUser];
		[self requestPanelList];
	} 
}

// Handle the server errors which are from controller server with status code.
- (void)handleServerResponseWithStatusCode:(int) statusCode {
	if (statusCode != 200) {
		if (statusCode == UNAUTHORIZED) {
			[self showLoginAlert];
		} else {
			[ViewHelper showAlertViewWithTitle:@"Panel List Error" Message:[ControllerException exceptionMessageOfCode:statusCode]];	
		}
	} 
}


- (void)updateTableView {
	UITableView *tv = (UITableView *)self.view;
	[tv beginUpdates];
	
	NSArray *newArray = nil;
	newArray = panels;
	
	NSMutableArray *insertIndexPaths = [[NSMutableArray alloc] init];
	for (int j = 0; j < newArray.count; j++){
		[insertIndexPaths addObject:[NSIndexPath indexPathForRow:j inSection:0]];
	}
	[tv insertRowsAtIndexPaths:insertIndexPaths withRowAnimation:UITableViewRowAnimationBottom];
	
	[tv endUpdates];
	
	[insertIndexPaths release];
}


#pragma mark delegate method of NSURLConnection
- (void) definitionURLConnectionDidFailWithError:(NSError *)error {
	
}


- (void)definitionURLConnectionDidFinishLoading:(NSData *)data {
	NSString *result = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
	
	NSXMLParser *xmlParser = [[NSXMLParser alloc] initWithData:data];
	[xmlParser setDelegate:self];
	[xmlParser parse];
	[[NSNotificationCenter defaultCenter] postNotificationName:NotificationHideLoading object:nil];
	[self updateTableView];
	[xmlParser release];
	[result release];
}

- (void)definitionURLConnectionDidReceiveResponse:(NSURLResponse *)response {
	NSHTTPURLResponse *httpResp = (NSHTTPURLResponse *)response;
	[self handleServerResponseWithStatusCode:[httpResp statusCode]];
}

- (void)viewWillDisappear:(BOOL)animated {
	[[NSNotificationCenter defaultCenter] postNotificationName:NotificationHideLoading object:nil];
}

#pragma mark delegate method of NSXMLParser
//when find a panel start we get its *name* attribute as logical identity
- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qualifiedName attributes:(NSDictionary *)attributeDict{
	if ([elementName isEqualToString:@"panel"]) {
		NSLog(@"panel logical id : %@",[attributeDict valueForKey:@"name"]);
		[panels addObject:[attributeDict valueForKey:@"name"]]; 
	}
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)toInterfaceOrientation {
	return YES;
}


- (void)dealloc {
	[panels release];
	[currentSelectedPanelIndex release];
	[chosenPanel release];
	
	[super dealloc];
}


@end

