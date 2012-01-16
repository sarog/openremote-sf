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
#import <UIKit/UIKit.h>
#import "ServerAutoDiscoveryController.h"
#import "UpdateController.h"
#import "FileUtils.h"
#import "ORControllerPanelsFetcher.h"
#import "ControllerDetailViewController.h"
#import "ChoosePanelViewController.h"
#import "LoginViewController.h"

@class ORConsoleSettingsManager;

/**
 * Render the UI of AppSetting view and provide functions of setting about panel client.
 */
@interface AppSettingController : UITableViewController <UITextFieldDelegate, ORControllerPanelsFetcherDelegate, ServerAutoDiscoveryControllerDelagate, ControllerDetailViewControllerDelegate, ChoosePanelViewControllerDelegate, UpdateControllerDelegate, LoginViewControllerDelegate> {
    
    ORConsoleSettingsManager *settingsManager;

	ServerAutoDiscoveryController *autoDiscoverController;
	UIBarButtonItem *done;
	UIBarButtonItem *cancel;
	BOOL isEditing;
	UIActivityIndicatorView *loadingView;
	UpdateController *updateController;
	UIActivityIndicatorView *spinner;
	UITextField *portField;	
}

@end