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
#import "ScreenSubController.h"
#import "Screen.h"
#import "Background.h"
#import "Image.h"
#import "DirectoryDefinition.h"
#import "FileUtils.h"
#import "UIViewUtil.h"
#import "ClippedUIImage.h"
#import "LayoutContainer.h"
#import "LayoutContainerSubController.h"

@interface ScreenSubController() 

@property (nonatomic, readwrite, retain) UIView *view;
@property (nonatomic, retain) Screen *screen;
@property (nonatomic, retain) NSMutableArray *layoutContainers;

- (void)createView;
- (void)createSubControllersForLayoutContainers;

@end

@implementation ScreenSubController

- (id)initWithScreen:(Screen *)aScreen
{
    self = [super init];
    if (self) {
        self.screen = aScreen;
        [self createView];
        [self createSubControllersForLayoutContainers];
    }    
    return self;
}

- (void)dealloc
{
    self.screen = nil;
    self.layoutContainers = nil;
    [super dealloc];
}

- (void)createSubControllersForLayoutContainers
{
    self.layoutContainers = [NSMutableArray arrayWithCapacity:[self.screen.layouts count]];
    for (LayoutContainer *layout in self.screen.layouts) {
        LayoutContainerSubController *ctrl = [[[LayoutContainerSubController subControllerClassForModelObject:layout] alloc] initWithLayoutContainer:layout];
        [self.view addSubview:ctrl.view];
        [self.layoutContainers addObject:ctrl];
        [ctrl release];
    }
}

- (void)createView
{
    int screenBackgroundImageViewWidth = 0;
    int screenBackgroundImageViewHeight = 0;
    
    if (self.screen.landscape) {
        screenBackgroundImageViewWidth = [UIScreen mainScreen].bounds.size.height;
        screenBackgroundImageViewHeight = [UIScreen mainScreen].bounds.size.width;
    } else {
        screenBackgroundImageViewWidth = [UIScreen mainScreen].bounds.size.width;
        screenBackgroundImageViewHeight = [UIScreen mainScreen].bounds.size.height;
    }

	if ([[[self.screen background] backgroundImage] src] && [[NSFileManager defaultManager] fileExistsAtPath:[[DirectoryDefinition imageCacheFolder] stringByAppendingPathComponent:[[[self.screen background] backgroundImage] src]]]) {
		UIImage *backgroundImage = [[UIImage alloc] initWithContentsOfFile:[[DirectoryDefinition imageCacheFolder] stringByAppendingPathComponent:[[[self.screen background] backgroundImage] src]]];
        if (backgroundImage) { // File might exist but not be an image, only proceed if we have an image
            UIImageView *backgroundImageView = [[UIImageView alloc] init];
            // fillscreen is false
            if (![[self.screen background] fillScreen]) {
                NSLog(@"BackgroundImage isn't fillScreen");
                NSLog(@"BackgroundImage's original width:%f, height:%f", backgroundImage.size.width, backgroundImage.size.height);
                
                // absolute position of screen background.
                if ([[self.screen background] isBackgroundImageAbsolutePosition]) {
                    int left = [[self.screen background] backgroundImageAbsolutePositionLeft];
                    int top = [[self.screen background] backgroundImageAbsolutePositionTop];
                    if (left > 0) {
                        screenBackgroundImageViewWidth = screenBackgroundImageViewWidth-left;
                    }
                    if (top > 0) {
                        screenBackgroundImageViewHeight = screenBackgroundImageViewHeight-top;
                    }
                    [backgroundImageView setFrame:CGRectMake(left, top, screenBackgroundImageViewWidth, screenBackgroundImageViewHeight)];
                    backgroundImageView = [UIViewUtil clippedUIImageViewWith:backgroundImage dependingOnUIView:backgroundImageView uiImageAlignToUIViewPattern:IMAGE_ABSOLUTE_ALIGN_TO_VIEW isUIImageFillUIView:YES];
                    if (left < 0) {
                        left = 0;
                    }
                    if (top < 0) {
                        top = 0;
                    }
                    [backgroundImageView setFrame:CGRectMake(left, top, backgroundImageView.frame.size.width, backgroundImageView.frame.size.height)];
                    NSLog(@"Clipped BackgroundImage's width:%f, height:%f", backgroundImageView.image.size.width, backgroundImageView.image.size.height);
                    NSLog(@"BackgroundImageView's left is %d, top is %d", left, top);
                    NSLog(@"BackgroundImageView's width:%f, height:%f", backgroundImageView.frame.size.width, backgroundImageView.frame.size.height);
                }
                // relative position of screen background.
                else {
                    // relative position
                    [backgroundImageView setFrame:CGRectMake(0, 0, screenBackgroundImageViewWidth, screenBackgroundImageViewHeight)];
                    NSString *backgroundImageRelativePosition = [[self.screen background] backgroundImageRelativePosition];
                    backgroundImageView = [UIViewUtil clippedUIImageViewWith:backgroundImage dependingOnUIView:backgroundImageView uiImageAlignToUIViewPattern:backgroundImageRelativePosition isUIImageFillUIView:NO];
                }
            }
            // fillscreen is true
            else {
                [backgroundImageView setFrame:CGRectMake(0, 0, screenBackgroundImageViewWidth, screenBackgroundImageViewHeight)];
                backgroundImageView = [UIViewUtil clippedUIImageViewWith:backgroundImage dependingOnUIView:backgroundImageView uiImageAlignToUIViewPattern:IMAGE_ABSOLUTE_ALIGN_TO_VIEW isUIImageFillUIView:YES];
            }
            NSLog(@"Added width: %d, height: %d backgroundImageView", screenBackgroundImageViewWidth, screenBackgroundImageViewHeight);
            [backgroundImageView setUserInteractionEnabled:YES];
            self.view = backgroundImageView;
            [backgroundImageView release];
            [backgroundImage release];
        }
    }
    // If for some reason something went wrong in creating the background with the image, just add a view with requested dimensions
    if (!self.view) {
        UIView *aView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, screenBackgroundImageViewWidth, screenBackgroundImageViewHeight)];
        aView.backgroundColor = [UIColor blackColor];
        self.view = aView;
        [aView release];
    }
}

@synthesize view;
@synthesize screen;
@synthesize layoutContainers;

@end