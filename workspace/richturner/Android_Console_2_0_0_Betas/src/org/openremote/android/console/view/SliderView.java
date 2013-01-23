/*
 * OpenRemote, the Home of the Digital Home. Copyright 2008-2011, OpenRemote
 * Inc.
 * 
 * See the contributors.txt file in the distribution for a full listing of
 * individual contributors.
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.openremote.android.console.view;

import org.openremote.android.console.Constants;
import org.openremote.android.console.R;
import org.openremote.android.console.bindings.Slider;
import org.openremote.android.console.model.ListenerConstant;
import org.openremote.android.console.model.OREvent;
import org.openremote.android.console.model.OREventListener;
import org.openremote.android.console.model.ORListenerManager;
import org.openremote.android.console.model.PollingStatusParser;
import org.openremote.android.console.util.ImageUtil;
import org.openremote.android.console.util.NumberFormat;
import org.openremote.android.console.view.seekbar.ORSeekBar;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Handler;
import android.os.Message;
import android.text.Layout;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableRow;

/**
 * This class is responsible for rendering the slider in screen with the slider
 * data.
 * 
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 * @author handy 2010-05-12
 * 
 */
public class SliderView extends SensoryControlView implements ORSeekBar.OnSeekBarChangeListener {
	public static final int TRACK_HEIGHT = 9;
	public static final int TRACK_BORDER = 0;
	public static final int THUMB_SIZE = 23;
	public static final double MAX_MIN_IMAGE_SIZE_RATIO_LIMIT = 0.2;
	public static final int MAX_MIN_IMAGE_TRACK_SPACING = 2;
	private Context context;
	private Slider slider;
	private ImageView minImage;
	private ImageView maxImage;
	private ImageView thumb;
	private ImageView minTrack;
	private ImageView maxTrack;
	private boolean isVertical = false;
	private int u = 0;
	private int v = 0;
	private int minValue = 0;
	private int maxValue = 100;
	private int valueRange = 100;
	private int minThumbPos = 0;
	private int thumbRange = 0;
	private int lastValue = 0;
	private int value = 0;
	private int stepSize = 1;
	private boolean trackHidden = false;

	private static final int SEEK_BAR_PROGRESS_INIT_VALUE = 0;
	private static final int SEEK_BAR_PROGRESS_MAX = 100;

	private static final int SEEK_BAR_MIN_WIDTH = 29;
	private static final int SEEK_BAR_MIN_HEIGHT = 29;

	private static final int DEFAULT_HORIZONTAL_SEEK_BAR_HEIGHT = 26;
	private static final int DEFAULT_VERTICAL_SEEK_BAR_WIDTH = 26;

	private static final int SEEK_BAR_MAX_SIZE = 9;

	private static final int SEEK_BAR_MIN_IMAGE_WIDTH = 20;
	private static final int SEEK_BAR_MIN_IMAGE_HEIGHT = 20;
	private static final int SEEK_BAR_MAX_IMAGE_WIDTH = 20;
	private static final int SEEK_BAR_MAX_IMAGE_HEIGHT = 20;

	private int slideToBusinessValue = 0;

	protected SliderView(Context context, Slider slider) {
		super(context);
		this.context = context;

		if (slider != null) {
			setComponent(slider);
			this.slider = slider;
			initSlider();

			// If the sensor is null, the sliderview is one-way.
			if (slider.getSensor() != null) {
				addPollingSensoryListener();
			}
		}
	}

	private void initSlider() {
		int width = slider.getFrameWidth();
		int height = slider.getFrameHeight();
		isVertical = slider.isVertical();

		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		RelativeLayout sliderLayout = (RelativeLayout) inflater.inflate(R.layout.vertical_seekbar,
				(ViewGroup) findViewById(R.id.slider_layout));
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
		sliderLayout.setLayoutParams(params);

		u = isVertical ? height : width;
		v = isVertical ? width : height;
		int trackLength = u;

		minImage = (ImageView) sliderLayout.findViewById(R.id.slider_minvalue_image);
		maxImage = (ImageView) sliderLayout.findViewById(R.id.slider_maxvalue_image);
		minTrack = (ImageView) sliderLayout.findViewById(R.id.slider_mintrack);
		maxTrack = (ImageView) sliderLayout.findViewById(R.id.slider_maxtrack);
		thumb = (ImageView) sliderLayout.findViewById(R.id.slider_thumb);
		minImage.setId(1);
		maxImage.setId(2);
		minTrack.setId(3);
		maxTrack.setId(4);
		thumb.setId(5);

		// Configure the layout of the various components and their sizes

		// Configure min and max images
		RelativeLayout.LayoutParams mnLayoutParams = (RelativeLayout.LayoutParams) minImage.getLayoutParams();
		int minHeight = 0;
		int minWidth = 0;
		
		if (slider.getMinImage() != null) {
			Drawable drawable = ImageUtil.createFromPathQuietly(context, Constants.FILE_FOLDER_PATH
					+ slider.getMinImage().getSrc());
			minImage.setImageDrawable(drawable);
			minWidth = drawable.getIntrinsicWidth();
			minHeight = drawable.getIntrinsicHeight();
		}
//		int drawableU = MAX_MIN_IMAGE_TRACK_SPACING;

		mnLayoutParams.height = minHeight;
		mnLayoutParams.width = minWidth;
		
		if (isVertical) {
			mnLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
			mnLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		} else {
			mnLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
			mnLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		}

		//trackLength -= drawableU;

		// int minDrawableHeight = isVertical ? minDrawable.getIntrinsicWidth() :
		// minDrawable.getIntrinsicHeight();
		minImage.setBackgroundColor(Color.DKGRAY);
		minImage.setLayoutParams(mnLayoutParams);


		// Configure the thumb
		Drawable thumbDrawable;
		
		RelativeLayout.LayoutParams thumbLayoutParams = (RelativeLayout.LayoutParams) thumb
				.getLayoutParams();

		if (slider.getThumbImage() != null) {
			thumbDrawable = ImageUtil.createFromPathQuietly(context, Constants.FILE_FOLDER_PATH
					+ slider.getThumbImage().getSrc());
		} else {
			thumbDrawable = getResources().getDrawable(R.drawable.slider_thumb2);
		}

		int thumbU = isVertical ? thumbDrawable.getIntrinsicHeight() : thumbDrawable.getIntrinsicWidth();
		int thumbV = isVertical ? thumbDrawable.getIntrinsicWidth() : thumbDrawable.getIntrinsicHeight();
		int halfThumbU = (int)Math.round((double)thumbU / 2);
		int thumbMargin = (int) Math.round(((double) v - thumbV) / 2);

		if (isVertical) {
			thumbLayoutParams.setMargins(thumbMargin, 0, thumbMargin, 0);
			thumbLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
			thumbLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		} else {
			thumbLayoutParams.setMargins(0, thumbMargin, 0, thumbMargin);
			thumbLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
			thumbLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		}

		thumb.setImageDrawable(thumbDrawable);
		thumb.setLayoutParams(thumbLayoutParams);

		// Configure track
		int minTrackV = TRACK_HEIGHT;
		int maxTrackV = TRACK_HEIGHT;
		Drawable minTrackDrawable, maxTrackDrawable;
		RelativeLayout.LayoutParams minLayoutParams = (RelativeLayout.LayoutParams) minTrack
				.getLayoutParams();
		RelativeLayout.LayoutParams maxLayoutParams = (RelativeLayout.LayoutParams) maxTrack
				.getLayoutParams();

		if (slider.getMinTrackImage() != null) {
			minTrackDrawable = ImageUtil.createFromPathQuietly(context, Constants.FILE_FOLDER_PATH
					+ slider.getMinTrackImage().getSrc());
		} else {
			int minId = isVertical ? R.drawable.slider_min_track_v : R.drawable.slider_min_track;
			minTrackDrawable = getResources().getDrawable(minId);
		}
	
		if (slider.getMaxTrackImage() != null) {
			maxTrackDrawable = ImageUtil.createFromPathQuietly(context, Constants.FILE_FOLDER_PATH
					+ slider.getMaxTrackImage().getSrc());
		} else {
			int maxId = isVertical ? R.drawable.slider_max_track_v : R.drawable.slider_max_track;
			maxTrackDrawable = getResources().getDrawable(maxId);
		}

		int minDrawableV = isVertical ? minTrackDrawable.getIntrinsicWidth() : minTrackDrawable
				.getIntrinsicHeight();
		minTrackV = minDrawableV > v ? v : minDrawableV;
		
		int maxDrawableV = isVertical ? maxTrackDrawable.getIntrinsicWidth() : maxTrackDrawable
				.getIntrinsicHeight();
		maxTrackV = maxDrawableV > v ? v : maxDrawableV;

//		int minTrackMargin = (int) Math.round(((double) v - minTrackV) / 2);
//		int maxTrackMargin = (int) Math.round(((double) v - maxTrackV) / 2);

		if (isVertical) {
			minLayoutParams.width = minTrackV;
			minLayoutParams.height = halfThumbU;
			minLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
			minLayoutParams.addRule(RelativeLayout.ABOVE, minImage.getId());
			maxLayoutParams.width = maxTrackV;
			maxLayoutParams.height = trackLength - halfThumbU;
			maxLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
			maxLayoutParams.addRule(RelativeLayout.ABOVE, minTrack.getId());
		} else {
			minLayoutParams.width = halfThumbU;
			minLayoutParams.height = minTrackV;
			minLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
			minLayoutParams.addRule(RelativeLayout.RIGHT_OF, minImage.getId());
			maxLayoutParams.width = trackLength - halfThumbU;
			maxLayoutParams.height = maxTrackV;
			maxLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
			maxLayoutParams.addRule(RelativeLayout.RIGHT_OF, minTrack.getId());
		}

		minTrack.setImageDrawable(minTrackDrawable);
		minTrack.setLayoutParams(minLayoutParams);
		maxTrack.setImageDrawable(maxTrackDrawable);
		maxTrack.setLayoutParams(maxLayoutParams);
		this.addView(sliderLayout);

		// // isVertical
		// if (slider.isVertical()) {
		// initVerticalSeekBar(inflater);
		// } else {
		// initHorizontalSeekBar(inflater);
		// }
	}

	// private void initVerticalSeekBar(LayoutInflater inflater) {
	// // Get the rootView(TableLayout) of seekbar.
	// // Because of minValueImage and maxValueImage, the seekbar is layouted in
	// TableLayout.
	// ViewGroup seekBarRootView = (ViewGroup)
	// inflater.inflate(R.layout.vertical_seekbar,
	// (ViewGroup) findViewById(R.id.vertical_seekbar_root_layout));
	//
	// // Get the seekbar instance from rootView(TableLayout).
	// verticalSeekBar = (ORSeekBar)
	// seekBarRootView.findViewById(R.id.vertical_seekbar);
	//
	// //Set custom track image, include minTrack and maxTrack.
	// Drawable maxTrackDrawable = null;
	// Drawable minTrackDrawable = null;
	// boolean clipImage = false;
	// if (slider.getMaxTrackImage() != null) {
	// maxTrackDrawable = ImageUtil.createFromPathQuietly(context,
	// Constants.FILE_FOLDER_PATH
	// + slider.getMaxTrackImage().getSrc());
	// }
	// if (slider.getMinTrackImage() != null) {
	// minTrackDrawable = ImageUtil.createFromPathQuietly(context,
	// Constants.FILE_FOLDER_PATH
	// + slider.getMinTrackImage().getSrc());
	// }
	// if (maxTrackDrawable != null || minTrackDrawable != null) {
	// if (maxTrackDrawable == null) {
	// maxTrackDrawable =
	// context.getResources().getDrawable(R.drawable.vertical_seekbar_background);
	// }
	// if (minTrackDrawable == null) {
	// minTrackDrawable =
	// context.getResources().getDrawable(R.drawable.vertical_seekbar_progress);
	// }
	// int maxTrackWidth = maxTrackDrawable.getIntrinsicWidth();
	// int maxTrackHeight = maxTrackDrawable.getIntrinsicHeight();
	// if (maxTrackWidth > DEFAULT_VERTICAL_SEEK_BAR_WIDTH && maxTrackHeight >
	// slider.getFrameHeight()) {
	// BitmapDrawable bd = (BitmapDrawable)maxTrackDrawable;
	// bd.setBounds(0, 0, DEFAULT_VERTICAL_SEEK_BAR_WIDTH,
	// slider.getFrameHeight());
	// bd.setGravity(Gravity.TOP);
	// clipImage = true;
	// }
	//
	// int minTrackWidth = minTrackDrawable.getIntrinsicWidth();
	// int minTrackHeight = minTrackDrawable.getIntrinsicHeight();
	// if (minTrackWidth > DEFAULT_VERTICAL_SEEK_BAR_WIDTH && minTrackHeight >
	// slider.getFrameHeight()) {
	// BitmapDrawable bd = (BitmapDrawable)minTrackDrawable;
	// bd.setBounds(0, 0, DEFAULT_VERTICAL_SEEK_BAR_WIDTH,
	// slider.getFrameHeight());
	// bd.setGravity(Gravity.BOTTOM);
	// clipImage = true;
	// }
	//
	// Drawable[] lda = {
	// maxTrackDrawable,
	// new ClipDrawable(minTrackDrawable,
	// Gravity.BOTTOM,
	// ClipDrawable.VERTICAL)
	// };
	// LayerDrawable ld = new LayerDrawable(lda);
	// ld.setId(0, android.R.id.background);
	// ld.setId(1, android.R.id.progress);
	// if (ld.getIntrinsicWidth() < DEFAULT_VERTICAL_SEEK_BAR_WIDTH) {
	// verticalSeekBar.setMaxWidth(ld.getIntrinsicWidth());
	// }
	// verticalSeekBar.setProgressDrawable(ld);
	// }
	// if (!clipImage) {
	// verticalSeekBar.setMaxWidth(SEEK_BAR_MAX_SIZE);
	// }
	//
	// verticalSeekBar.setMax(SEEK_BAR_PROGRESS_MAX);
	// verticalSeekBar.setProgress(getProgressOfBusinessValue(SEEK_BAR_PROGRESS_INIT_VALUE));
	// if (slider.isPassive()) {
	// verticalSeekBar.setEnabled(false);
	// }
	//
	// // Set the default layoutparams for seekbar. Set the width and height for
	// seekbar.
	// // slider.getFrameHeight() means the height of vertical seekbar no
	// minValueImage and maxValueImage
	// //verticalSeekBar.setLayoutParams(new
	// TableRow.LayoutParams(SEEK_BAR_MIN_WIDTH, slider.getFrameHeight()));
	// if (slider.getThumbImage() != null) {
	// Drawable thumbDrawable = ImageUtil.createFromPathQuietly(context,
	// Constants.FILE_FOLDER_PATH + slider.getThumbImage().getSrc());
	// verticalSeekBar.setThumb(thumbDrawable);
	// }
	//
	// // if (slider.getMinImage() != null) {
	// // Drawable minValueDrawable = ImageUtil.createFromPathQuietly(context,
	// Constants.FILE_FOLDER_PATH
	// // + slider.getMinImage().getSrc());
	// // ImageView minValueImageView = (ImageView)
	// seekBarRootView.findViewById(R.id.vertical_seekbar_minvalue_image);
	// // minValueImageView.setImageDrawable(minValueDrawable);
	// //
	// // // Limit the width and height of minValueImage
	// // minValueImageView.setLayoutParams(new
	// TableRow.LayoutParams(SEEK_BAR_MIN_IMAGE_WIDTH,
	// // SEEK_BAR_MIN_IMAGE_HEIGHT));
	// //
	// // // Set the height of vertical seekbar no maxImageHeight
	// // verticalSeekBar.setLayoutParams(new
	// TableRow.LayoutParams(SEEK_BAR_MIN_WIDTH, slider.getFrameHeight()
	// // - SEEK_BAR_MIN_IMAGE_HEIGHT));
	// //
	// // }
	// //
	// // if (slider.getMaxImage() != null) {
	// // Drawable maxValueDrawable = ImageUtil.createFromPathQuietly(context,
	// Constants.FILE_FOLDER_PATH
	// // + slider.getMaxImage().getSrc());
	// //
	// // ImageView maxValueImageView = (ImageView)
	// seekBarRootView.findViewById(R.id.vertical_seekbar_maxvalue_image);
	// // maxValueImageView.setImageDrawable(maxValueDrawable);
	// //
	// // // Limit the width and height of maxValueImage
	// // maxValueImageView.setLayoutParams(new
	// TableRow.LayoutParams(SEEK_BAR_MAX_IMAGE_WIDTH,
	// // SEEK_BAR_MAX_IMAGE_HEIGHT));
	// //
	// // if (slider.getMinImage() != null) {
	// // // Set the height of vertical seekbar exists minValueImage and
	// maxValueImage
	// // verticalSeekBar.setLayoutParams(new
	// TableRow.LayoutParams(SEEK_BAR_MIN_WIDTH, slider.getFrameHeight()
	// // - SEEK_BAR_MIN_IMAGE_HEIGHT - SEEK_BAR_MAX_IMAGE_HEIGHT));
	// // } else {
	// // // Set the height of vertical seekbar no minImageHeight
	// // verticalSeekBar.setLayoutParams(new
	// TableRow.LayoutParams(SEEK_BAR_MIN_WIDTH, slider.getFrameHeight()
	// // - SEEK_BAR_MAX_IMAGE_HEIGHT));
	// // }
	// // }
	//
	// verticalSeekBar.setOnSeekBarChangeListener(this);
	// this.addView(seekBarRootView);
	// }
	//
	// private void initHorizontalSeekBar(LayoutInflater inflater) {
	// ViewGroup seekBarRootView = (ViewGroup)
	// inflater.inflate(R.layout.horizontal_seekbar,
	// (ViewGroup) findViewById(R.id.horizontal_seekbar_root_layout));
	//
	// horizontalSeekBar = (ORSeekBar)
	// seekBarRootView.findViewById(R.id.horizontal_seekbar);
	// boolean clipImage = false;
	// //Set custom track image, include minTrack and maxTrack.
	// Drawable maxTrackDrawable = null;
	// Drawable minTrackDrawable = null;
	// if (slider.getMaxTrackImage() != null) {
	// maxTrackDrawable = ImageUtil.createFromPathQuietly(context,
	// Constants.FILE_FOLDER_PATH
	// + slider.getMaxTrackImage().getSrc());
	// }
	// if (slider.getMinTrackImage() != null) {
	// minTrackDrawable = ImageUtil.createFromPathQuietly(context,
	// Constants.FILE_FOLDER_PATH
	// + slider.getMinTrackImage().getSrc());
	// }
	// if (maxTrackDrawable != null || minTrackDrawable != null) {
	// if (maxTrackDrawable == null) {
	// maxTrackDrawable =
	// context.getResources().getDrawable(R.drawable.horizontal_seekbar_background);
	// }
	// if (minTrackDrawable == null) {
	// minTrackDrawable =
	// context.getResources().getDrawable(R.drawable.horizontal_seekbar_progress);
	// }
	// int maxTrackWidth = maxTrackDrawable.getIntrinsicWidth();
	// int maxTrackHeight = maxTrackDrawable.getIntrinsicHeight();
	// if (maxTrackHeight > DEFAULT_HORIZONTAL_SEEK_BAR_HEIGHT && maxTrackWidth >
	// slider.getFrameWidth()) {
	// BitmapDrawable bd = (BitmapDrawable)maxTrackDrawable;
	// bd.setBounds(0, 0, slider.getFrameWidth(),
	// DEFAULT_HORIZONTAL_SEEK_BAR_HEIGHT);
	// bd.setGravity(Gravity.RIGHT);
	// clipImage = true;
	// }
	//
	// int minTrackWidth = minTrackDrawable.getIntrinsicWidth();
	// int minTrackHeight = minTrackDrawable.getIntrinsicHeight();
	// if (minTrackHeight > DEFAULT_HORIZONTAL_SEEK_BAR_HEIGHT && minTrackWidth >
	// slider.getFrameWidth()) {
	// BitmapDrawable bd = (BitmapDrawable)minTrackDrawable;
	// bd.setBounds(0, 0, slider.getFrameWidth(),
	// DEFAULT_HORIZONTAL_SEEK_BAR_HEIGHT);
	// bd.setGravity(Gravity.LEFT);
	// clipImage = true;
	// }
	//
	// Drawable[] lda = {
	// maxTrackDrawable,
	// new ClipDrawable(minTrackDrawable,
	// Gravity.LEFT,
	// ClipDrawable.HORIZONTAL)
	// };
	// LayerDrawable ld = new LayerDrawable(lda);
	// ld.setId(0, android.R.id.background);
	// ld.setId(1, android.R.id.progress);
	// if (ld.getIntrinsicHeight() < DEFAULT_HORIZONTAL_SEEK_BAR_HEIGHT) {
	// horizontalSeekBar.setMaxHeight(ld.getIntrinsicHeight());
	// }
	// horizontalSeekBar.setProgressDrawable(ld);
	// }
	// if (!clipImage) {
	// horizontalSeekBar.setMaxHeight(SEEK_BAR_MAX_SIZE);
	// }
	// horizontalSeekBar.setMax(SEEK_BAR_PROGRESS_MAX);
	// horizontalSeekBar.setProgress(getProgressOfBusinessValue(SEEK_BAR_PROGRESS_INIT_VALUE));
	// if (slider.isPassive()) {
	// horizontalSeekBar.setEnabled(false);
	// }
	//
	// horizontalSeekBar.setLayoutParams(new
	// TableRow.LayoutParams(slider.getFrameWidth(), SEEK_BAR_MIN_HEIGHT));
	// if (slider.getThumbImage() != null) {
	// Drawable thumbDrawable = ImageUtil.createFromPathQuietly(context,
	// Constants.FILE_FOLDER_PATH
	// + slider.getThumbImage().getSrc());
	// horizontalSeekBar.setThumb(thumbDrawable);
	// }
	//
	// if (slider.getMinImage() != null) {
	// Drawable minValueDrawable = ImageUtil.createFromPathQuietly(context,
	// Constants.FILE_FOLDER_PATH
	// + slider.getMinImage().getSrc());
	//
	// ImageView minValueImageView = (ImageView)
	// seekBarRootView.findViewById(R.id.horizontal_seekbar_minvalue_image);
	// minValueImageView.setImageDrawable(minValueDrawable);
	// minValueImageView.setLayoutParams(new
	// TableRow.LayoutParams(SEEK_BAR_MIN_IMAGE_WIDTH,
	// SEEK_BAR_MIN_IMAGE_HEIGHT));
	//
	// horizontalSeekBar.setLayoutParams(new
	// TableRow.LayoutParams(slider.getFrameWidth() - SEEK_BAR_MIN_IMAGE_WIDTH,
	// SEEK_BAR_MIN_HEIGHT));
	// }
	//
	// if (slider.getMaxImage() != null) {
	// Drawable maxValueDrawable = ImageUtil.createFromPathQuietly(context,
	// Constants.FILE_FOLDER_PATH
	// + slider.getMaxImage().getSrc());
	//
	// ImageView maxValueImageView = (ImageView)
	// seekBarRootView.findViewById(R.id.horizontal_seekbar_maxvalue_image);
	// maxValueImageView.setImageDrawable(maxValueDrawable);
	// maxValueImageView.setLayoutParams(new
	// TableRow.LayoutParams(SEEK_BAR_MAX_IMAGE_WIDTH,
	// SEEK_BAR_MAX_IMAGE_HEIGHT));
	//
	// if (slider.getMinImage() != null) {
	// horizontalSeekBar.setLayoutParams(new
	// TableRow.LayoutParams(slider.getFrameWidth()
	// - SEEK_BAR_MIN_IMAGE_WIDTH - SEEK_BAR_MAX_IMAGE_WIDTH,
	// SEEK_BAR_MIN_HEIGHT));
	// } else {
	// horizontalSeekBar.setLayoutParams(new
	// TableRow.LayoutParams(slider.getFrameWidth()
	// - SEEK_BAR_MAX_IMAGE_WIDTH, SEEK_BAR_MIN_HEIGHT));
	// }
	// }
	//
	// horizontalSeekBar.setOnSeekBarChangeListener(this);
	// this.addView(seekBarRootView);
	// }

	private int getProgressOfBusinessValue(int businessValue) {
		if (slider.getMaxValue() == 0 || slider.getMaxValue() == slider.getMinValue()) {
			return 0;
		}
		double progress = SEEK_BAR_PROGRESS_MAX
				* ((float) (businessValue - slider.getMinValue()) / (slider.getMaxValue() - slider
						.getMinValue()));
		return (int) NumberFormat.format(0, progress);
	}

	@Override
	public void addPollingSensoryListener() {
		final Integer sensorId = ((Slider) getComponent()).getSensor().getSensorId();
		Log.i("OpenRemote-SLIDER", "sensor id is " + sensorId);
		if (sensorId > 0) {
			ORListenerManager.getInstance().addOREventListener(
					ListenerConstant.ListenerPollingStatusIdFormat + sensorId, new OREventListener() {
						public void handleEvent(OREvent event) {
							String value = PollingStatusParser.statusMap.get(sensorId.toString()).toLowerCase();
							int valueInt = 0;
							try {
								valueInt = Integer.parseInt(value);
							} catch (NumberFormatException e) {
								Log.e("OpenRemote-SLIDER", "The returned format of polling value " + value
										+ " for slider is wrong", e);
								return;
							}
							pollingValueUIHandler.sendEmptyMessage(valueInt);
						}
					});
		}
	}

	private Handler pollingValueUIHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			int businessValue = msg.what;
			int progress = getProgressOfBusinessValue(businessValue);
			// if(slider.isVertical()) {
			// verticalSeekBar.setProgress(progress);
			// } else {
			// horizontalSeekBar.setProgress(progress);
			// }
			// super.handleMessage(msg);
		}
	};

	@Override
	public void onProgressChanged(ORSeekBar seekBar, int progress, boolean fromUser) {
		slideToBusinessValue = (int) (((float) progress / SEEK_BAR_PROGRESS_MAX)
				* (slider.getMaxValue() - slider.getMinValue()) + slider.getMinValue());
	}

	@Override
	public void onStartTrackingTouch(ORSeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(ORSeekBar seekBar) {
		Log.i("OpenRemote-SLIDER", "Slide to business value " + slideToBusinessValue);
		sendCommandRequest(String.valueOf(slideToBusinessValue));
	}
}
