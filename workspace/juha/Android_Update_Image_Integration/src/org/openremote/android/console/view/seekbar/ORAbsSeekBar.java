/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2010, OpenRemote Inc.
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
package org.openremote.android.console.view.seekbar;

import org.openremote.android.console.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;

/**
 * This class is rewrite the AbsSeekBar of Android, and for supporting vertical AbsSeek Bar.
 * <p> 
 * See {@link android.widget.AbsSeekBar}
 * </p>
 * 
 * @author handy.wang, tomsky.wang
 */
public class ORAbsSeekBar extends ORProgressBar {

   private Drawable mThumb;
   private int mThumbOffset;

   /**
    * On touch, this offset plus the scaled value from the position of the touch will form the progress value. Usually
    * 0.
    */
   float mTouchProgressOffset;

   /**
    * Whether this is user seekable.
    */
   boolean mIsUserSeekable = true;

   /**
    * On key presses (right or left), the amount to increment/decrement the progress.
    */
   private int mKeyProgressIncrement = 1;

   private static final int NO_ALPHA = 0xFF;
   private float mDisabledAlpha;

   public ORAbsSeekBar(Context context) {
      super(context);
   }

   public ORAbsSeekBar(Context context, AttributeSet attrs) {
      super(context, attrs);
   }

   public ORAbsSeekBar(Context context, AttributeSet attrs, int defStyle) {
      super(context, attrs, defStyle);

      TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SeekBar, defStyle, 0);
      Drawable thumb = a.getDrawable(R.styleable.SeekBar_android_thumb);
      setThumb(thumb); // will guess mThumbOffset if thumb != null...
      // ...but allow layout to override this
      int thumbOffset = a.getDimensionPixelOffset(R.styleable.SeekBar_android_thumbOffset, getThumbOffset());
      setThumbOffset(thumbOffset);

      a.recycle();

      a = context.obtainStyledAttributes(attrs, R.styleable.Theme, 0, 0);
      mDisabledAlpha = a.getFloat(R.styleable.Theme_android_disabledAlpha, 0.5f);
      a.recycle();
   }

   /**
    * Sets the thumb that will be drawn at the end of the progress meter within the SeekBar.
    * <p>
    * If the thumb is a valid drawable (i.e. not null), half its width will be used as the new thumb offset (@see
    * #setThumbOffset(int)).
    * 
    * @param thumb
    *           Drawable representing the thumb
    */
   public void setThumb(Drawable thumb) {
      if (thumb != null) {
         thumb.setCallback(this);

         // Assuming the thumb drawable is symmetric, set the thumb offset
         // such that the thumb will hang halfway off either edge of the
         // progress bar.
         if (vertical) {
            mThumbOffset = (int) thumb.getIntrinsicHeight() / 2;
         }
      }
      mThumb = thumb;
      invalidate();
   }

   /**
    * @see #setThumbOffset(int)
    */
   public int getThumbOffset() {
      return mThumbOffset;
   }

   /**
    * Sets the thumb offset that allows the thumb to extend out of the range of the track.
    * 
    * @param thumbOffset
    *           The offset amount in pixels.
    */
   public void setThumbOffset(int thumbOffset) {
      mThumbOffset = thumbOffset;
      invalidate();
   }

   /**
    * Sets the amount of progress changed via the arrow keys.
    * 
    * @param increment
    *           The amount to increment or decrement when the user presses the arrow keys.
    */
   public void setKeyProgressIncrement(int increment) {
      mKeyProgressIncrement = increment < 0 ? -increment : increment;
   }

   /**
    * Returns the amount of progress changed via the arrow keys.
    * <p>
    * By default, this will be a value that is derived from the max progress.
    * 
    * @return The amount to increment or decrement when the user presses the arrow keys. This will be positive.
    */
   public int getKeyProgressIncrement() {
      return mKeyProgressIncrement;
   }

   @Override
   public synchronized void setMax(int max) {
      super.setMax(max);

      if ((mKeyProgressIncrement == 0) || (getMax() / mKeyProgressIncrement > 20)) {
         // It will take the user too long to change this via keys, change it
         // to something more reasonable
         setKeyProgressIncrement(Math.max(1, Math.round((float) getMax() / 20)));
      }
   }

   @Override
   protected boolean verifyDrawable(Drawable who) {
      return who == mThumb || super.verifyDrawable(who);
   }

   @Override
   protected void drawableStateChanged() {
      super.drawableStateChanged();

      Drawable progressDrawable = getProgressDrawable();
      if (progressDrawable != null) {
         progressDrawable.setAlpha(isEnabled() ? NO_ALPHA : (int) (NO_ALPHA * mDisabledAlpha));
      }

      if (mThumb != null && mThumb.isStateful()) {
         int[] state = getDrawableState();
         mThumb.setState(state);
      }
   }

   @Override
   void onProgressRefresh(float scale, boolean fromUser) {
      Drawable thumb = mThumb;
      if (thumb != null) {
         if (vertical) {
            setThumbPos(getHeight(), thumb, scale, Integer.MIN_VALUE);
         } else {
            setThumbPos(getWidth(), thumb, scale, Integer.MIN_VALUE);
         }
         /*
          * Since we draw translated, the drawable's bounds that it signals for invalidation won't be the actual bounds
          * we want invalidated, so just invalidate this whole view.
          */
         invalidate();
      }
   }

   @Override
   protected void onSizeChanged(int w, int h, int oldw, int oldh) {
      Drawable d = getCurrentDrawable();
      Drawable thumb = mThumb;
      if (vertical) {
         int thumbWidth = thumb == null ? 0 : thumb.getIntrinsicWidth();
         // The max height does not incorporate padding, whereas the height
         // parameter does
         int trackWidth = Math.min(mMaxWidth, w - mPaddingRight - mPaddingLeft);
         int max = getMax();
         float scale = max > 0 ? (float) getProgress() / (float) max : 0;
         if (thumbWidth > trackWidth) {
            int gapForCenteringTrack = (thumbWidth - trackWidth) / 2;
            if (thumb != null) {
               if (thumbWidth < w) {
                  setThumbPos(h, thumb, scale, (w - thumbWidth) / 2);
               } else {
                  setThumbPos(h, thumb, scale, gapForCenteringTrack * -1);
               }
            }
            if (d != null) {
               // Canvas will be translated by the padding, so 0,0 is where we start drawing
               d.setBounds(gapForCenteringTrack, 0, w - mPaddingRight - mPaddingLeft - gapForCenteringTrack, h
                     - mPaddingBottom - mPaddingTop);
            }
         } else {
            if (d != null) {
               // Canvas will be translated by the padding, so 0,0 is where we start drawing
               d.setBounds(0, 0, w - mPaddingRight - mPaddingLeft, h - mPaddingBottom - mPaddingTop);
            }
            int gap = (trackWidth - thumbWidth) / 2;
            if (thumb != null) {
               setThumbPos(h, thumb, scale, gap);
            }
         }
      } else {
         int thumbHeight = thumb == null ? 0 : thumb.getIntrinsicHeight();
         // The max height does not incorporate padding, whereas the height
         // parameter does
         int trackHeight = Math.min(mMaxHeight, h - mPaddingTop - mPaddingBottom);
         int max = getMax();
         float scale = max > 0 ? (float) getProgress() / (float) max : 0;

         if (thumbHeight > trackHeight) {
            if (thumb != null) {
               setThumbPos(w, thumb, scale, 0);
            }
            int gapForCenteringTrack = (thumbHeight - trackHeight) / 2;
            if (d != null) {
               // Canvas will be translated by the padding, so 0,0 is where we start drawing
               d.setBounds(0, gapForCenteringTrack, w - mPaddingRight - mPaddingLeft, h - mPaddingBottom
                     - gapForCenteringTrack - mPaddingTop);
            }
         } else {
            if (d != null) {
               // Canvas will be translated by the padding, so 0,0 is where we start drawing
               d.setBounds(0, 0, w - mPaddingRight - mPaddingLeft, h - mPaddingBottom - mPaddingTop);
            }
            int gap = (trackHeight - thumbHeight) / 2;
            if (thumb != null) {
               setThumbPos(w, thumb, scale, gap);
            }
         }
      }
   }

   /**
    * @param gap
    *           If set to {@link Integer#MIN_VALUE}, this will be ignored and
    */
   private void setThumbPos(int h, Drawable thumb, float scale, int gap) {
      if (vertical) {
         int available = h - mPaddingTop - mPaddingBottom;
         int thumbWidth = thumb.getIntrinsicWidth();
         int thumbHeight = thumb.getIntrinsicHeight();
         available -= thumbHeight;

         // The extra space for the thumb to move on the track
         available += mThumbOffset * 2;
         int thumbPos = (int) ((1 - scale) * available);
         int leftBound, rightBound;
         if (gap == Integer.MIN_VALUE) {
            Rect oldBounds = thumb.getBounds();
            leftBound = oldBounds.left;
            rightBound = oldBounds.right;
         } else {
            leftBound = gap;
            rightBound = gap + thumbWidth;
         }

         // Canvas will be translated, so 0,0 is where we start drawing
         thumb.setBounds(leftBound, thumbPos, rightBound, thumbPos + thumbHeight);
      } else {
         int available = h - mPaddingLeft - mPaddingRight;
         int thumbWidth = thumb.getIntrinsicWidth();
         int thumbHeight = thumb.getIntrinsicHeight();
         available -= thumbWidth;

         // The extra space for the thumb to move on the track
         available += mThumbOffset * 2;

         int thumbPos = (int) (scale * available);

         int topBound, bottomBound;
         if (gap == Integer.MIN_VALUE) {
            Rect oldBounds = thumb.getBounds();
            topBound = oldBounds.top;
            bottomBound = oldBounds.bottom;
         } else {
            topBound = gap;
            bottomBound = gap + thumbHeight;
         }

         // Canvas will be translated, so 0,0 is where we start drawing
         thumb.setBounds(thumbPos, topBound, thumbPos + thumbWidth, bottomBound);
      }
   }

   @Override
   protected synchronized void onDraw(Canvas canvas) {
      super.onDraw(canvas);
      if (mThumb != null) {
         canvas.save();
         // Translate the padding. For the x, we need to allow the thumb to
         // draw in its extra space
         if (vertical) {
            canvas.translate(mPaddingLeft, mPaddingTop - mThumbOffset);
         } else {
            canvas.translate(mPaddingLeft - mThumbOffset, mPaddingTop);
         }
         mThumb.draw(canvas);
         canvas.restore();
      }
   }

   @Override
   protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
      Drawable d = getCurrentDrawable();

      int dw = 0;
      int dh = 0;
      if (vertical) {
         int thumbWidth = mThumb == null ? 0 : mThumb.getIntrinsicWidth();
         if (d != null) {
            dw = Math.max(mMinWidth, Math.min(mMaxWidth, d.getIntrinsicWidth()));
            dw = Math.max(thumbWidth, dh);
            dh = Math.max(mMinHeight, Math.min(mMaxHeight, d.getIntrinsicHeight()));
         }
      } else {
         int thumbHeight = mThumb == null ? 0 : mThumb.getIntrinsicHeight();
         if (d != null) {
            dw = Math.max(mMinWidth, Math.min(mMaxWidth, d.getIntrinsicWidth()));
            dh = Math.max(mMinHeight, Math.min(mMaxHeight, d.getIntrinsicHeight()));
            dh = Math.max(thumbHeight, dh);
         }
      }
      dw += mPaddingLeft + mPaddingRight;
      dh += mPaddingTop + mPaddingBottom;
      setMeasuredDimension(resolveSize(dw, widthMeasureSpec), resolveSize(dh, heightMeasureSpec));
   }

   @Override
   public boolean onTouchEvent(MotionEvent event) {
      if (!mIsUserSeekable || !isEnabled()) {
         return false;
      }

      switch (event.getAction()) {
      case MotionEvent.ACTION_DOWN:
         setPressed(true);
         onStartTrackingTouch();
         trackTouchEvent(event);
         break;

      case MotionEvent.ACTION_MOVE:
         trackTouchEvent(event);
         attemptClaimDrag();
         break;

      case MotionEvent.ACTION_UP:
         trackTouchEvent(event);
         onStopTrackingTouch();
         setPressed(false);
         // ProgressBar doesn't know to repaint the thumb drawable
         // in its inactive state when the touch stops (because the
         // value has not apparently changed)
         invalidate();
         break;

      case MotionEvent.ACTION_CANCEL:
         onStopTrackingTouch();
         setPressed(false);
         invalidate(); // see above explanation
         break;
      }
      return true;
   }

   private void trackTouchEvent(MotionEvent event) {
      float scale;
      float progress = 0;
      if (vertical) {
         final int height = getHeight();
         final int available = height - mPaddingTop - mPaddingBottom;
         int y = height - (int) event.getY();
         if (y < mPaddingBottom) {
            scale = 0.0f;
         } else if (y > height - mPaddingTop) {
            scale = 1.0f;
         } else {
            scale = (float) (y - mPaddingBottom) / (float) available;
            progress = mTouchProgressOffset;
         }
      } else {
         final int width = getWidth();
         final int available = width - mPaddingLeft - mPaddingRight;
         int x = (int) event.getX();
         if (x < mPaddingLeft) {
            scale = 0.0f;
         } else if (x > width - mPaddingRight) {
            scale = 1.0f;
         } else {
            scale = (float) (x - mPaddingLeft) / (float) available;
            progress = mTouchProgressOffset;
         }
      }

      final int max = getMax();
      progress += scale * max;
      if (progress < 0) {
         progress = 0;
      } else if (progress > max) {
         progress = max;
      }
      setProgress((int) progress, true);
   }

   /**
    * Tries to claim the user's drag motion, and requests disallowing any ancestors from stealing events in the drag.
    */
   private void attemptClaimDrag() {
      if (mParent != null) {
         mParent.requestDisallowInterceptTouchEvent(true);
      }
   }

   /**
    * This is called when the user has started touching this widget.
    */
   void onStartTrackingTouch() {
   }

   /**
    * This is called when the user either releases his touch or the touch is canceled.
    */
   void onStopTrackingTouch() {
   }

   /**
    * Called when the user changes the seekbar's progress by using a key event.
    */
   void onKeyChange() {
   }

   @Override
   public boolean onKeyDown(int keyCode, KeyEvent event) {
      int progress = getProgress();

      switch (keyCode) {
      case KeyEvent.KEYCODE_DPAD_DOWN:
         if (progress <= 0) break;
         setProgress(progress - mKeyProgressIncrement, true);
         onKeyChange();
         return true;

      case KeyEvent.KEYCODE_DPAD_UP:
         if (progress >= getMax()) break;
         setProgress(progress + mKeyProgressIncrement, true);
         onKeyChange();
         return true;
      }

      return super.onKeyDown(keyCode, event);
   }

}
