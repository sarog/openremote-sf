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
#import "UIViewUtil.h"
#import "ClippedUIImage.h"


@implementation UIViewUtil

/**
 * Clip image depending on image's container view, align pattern and fill pattern.
 */
+ (UIImageView *)clippedUIImageViewWith:(UIImage *)uiImage dependingOnUIView:(UIView *)uiView uiImageAlignToUIViewPattern:(NSString *)align isUIImageFillUIView:(BOOL)imageFillView
{
	if (uiImage && uiView) {
		ClippedUIImage *clippedUIImage = [[ClippedUIImage alloc] initWithUIImage:uiImage dependingOnUIView:uiView imageAlignToView:align];
		UIImageView *uiImageView = [[UIImageView alloc] initWithFrame:uiView.frame];
		[uiImageView setImage:clippedUIImage];
        [clippedUIImage release];
		if (imageFillView) {
			[uiImageView sizeToFit];
		}
		[uiImageView setContentMode:uiView.contentMode];
		return [uiImageView autorelease];
	} else {
		return nil;
	}
}

@end
