/* OpenRemote, the Home of the Digital Home.
 * Copyright 2009, OpenRemote Inc.
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
package org.openremote.android.console;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openremote.android.console.bindings.Button;
import org.openremote.android.console.bindings.ORActivity;
import org.openremote.android.console.bindings.Screen;
import org.openremote.android.console.image.ImageLoader;
import org.openremote.android.console.image.ImageLoader.Typer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AbsoluteLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ViewFlipper;

/**
 * This is the actual screen for handling activities (aka Watch TV, Lights,
 * etc), It is reconstructed for each activity.
 * 
 * @author Andrew C. Oliver <acoliver at osintegrators.com>
 */
public class ActivityHandler extends Activity implements OnGestureListener {

    private static final int FLIPPER = 0xF00D;
    private ORActivity activity;
    private GestureDetector gestureScanner;
    private String url;
    private ImageLoader imageLoader;
    private ViewFlipper vf;

    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.imageLoader = Main.imageLoader;
        this.imageLoader.reset();
        this.url = getSharedPreferences(ConfigureActivity.OPEN_REMOTE_PREFS, 0)
                .getString(Constants.URL, "http://192.168.1.1");
        this.gestureScanner = new GestureDetector(this);
        Log.d(this.toString(), "in oncreate for ActivityHandler");
        this.activity = (ORActivity) getIntent().getExtras().get("activity");
        this.setTitle(activity.getName());
        startLoadingImages(activity);
        LinearLayout ll = new LinearLayout(this);
        vf = new ViewFlipper(this);
        vf.setId(FLIPPER);
        ll.addView(vf);
        this.setContentView(ll);
        constructScreens(vf, activity);

    }

    private void startLoadingImages(ORActivity activity) {
        // this.imageLoader = this.imageLoader == null ? new ImageLoader() :
        // this.imageLoader.reset();
        for (Screen s : activity.getScreens()) {

            for (Button b : s.getButtons()) {
                if (b.getIcon() != null) {
                    this.imageLoader.load(this.url + "/" + b.getIcon(),
                            makeTyper(s, this));
                }
            }
        }
        this.imageLoader.start();

    }

    private Typer makeTyper(final Screen s, final Context c) {
        return new ImageLoader.Typer() {

            @Override
            public ImageView type(Bitmap bitmap) {
                if (bitmap == null) {
                    ImageButton ib2 = new ImageButton(c);
                    ib2.setImageResource(R.drawable.ic_notfound);
                }
                int rowsize = 420 / s.getRow();
                int colsize = 310 / s.getCol();
                int yinset = rowsize / 6;
                int xinset = colsize / 7;
                int height = bitmap.getHeight();
                int width = bitmap.getWidth();
                if (height < (rowsize - (yinset * 2))
                        && width < (colsize - (xinset * 2))) {
                    ImageButton ib = new ImageButton(c);
                    ib.setImageBitmap(bitmap);
                    return ib;
                } else {
                    ImageView iv = new ImageView(c);
                    iv.setImageBitmap(bitmap);
                    iv.setBackgroundColor(0);
                    return iv;
                }
            }
        };
    }

    private void constructScreens(ViewFlipper vf, ORActivity activity) {
        List<Screen> screens = activity.getScreens();
        Map<String, Integer> loaded = new HashMap<String, Integer>();
        for (Screen screen : screens) {
            AbsoluteLayout tl = constructScreen(screen.getCol(), screen
                    .getRow(), screen.getButtons(), loaded);
            tl.setBackgroundColor(0);
            tl.setTag(screen.getName());
            vf.addView(tl);
        }

    }

    private AbsoluteLayout constructScreen(int col, int row,
            List<Button> buttons, Map<String, Integer> loaded) {
        int rowsize = 420 / row;
        int colsize = 310 / col;
        int yinset = rowsize / 6;
        int xinset = colsize / 7;
        Log.d(this.toString(), "rowsize=" + rowsize + ",colsize=" + colsize);

        AbsoluteLayout screen = new AbsoluteLayout(this);
        screen.setBackgroundColor(0);
        for (Button button : buttons) {
            int posX = (colsize * button.getX());
            int posY = (rowsize * button.getY());
            if (!nvl(button.getIcon())) {
                Bitmap bitmap = getImage(button);
                View view = null;
                int height = bitmap != null ? bitmap.getHeight() : -1;
                int width = bitmap != null ? bitmap.getWidth() : -1;
                // Log.d(this.toString(),"height="+height+",width="+width);
                int i = loaded.get(button.getIcon()) == null ? 0 : loaded
                        .get(button.getIcon());
                view = bitmap != null ? getViewForButton(button.getIcon()).get(
                        i) : constructNotFoundButton();
                i++;
                loaded.put(button.getIcon(), i);
                if (height < (rowsize - (yinset * 2))
                        && width < (colsize - (xinset * 2))) {

                    height = (rowsize - (yinset * 2));
                    width = (colsize - (xinset * 2));
                    posX += xinset;
                    posY += yinset;
                }
                if ((posX + width) > 320) {
                    posX += (320 - (posX + width));
                    posX = posX >= 0 ? posX : 0;
                }
                if ((posY + height) > 480) {
                    posY += (480 - (posY + height));
                    posY = posY >= 0 ? posY : 0;
                }
                width = width > 320 ? 320 : width;
                height = height > 455 ? 455 : height;
                Log.d(this.toString(), "positioning " + button.getIcon()
                        + " num " + i + " at " + width + "," + height + ","
                        + posX + "," + posY);
                if (height < 400 && width < 300) {
                    // dirty dirty hack, make big buttons that do nothing
                    // inoperative for clicks so that gestures work
                    // properly
                    view.setOnClickListener(createClickListener(url, button
                            .getId()));
                    view.setOnTouchListener(createTouchListener(url, button
                            .getId()));

                }

                screen.addView(view, new AbsoluteLayout.LayoutParams(width,
                        height, posX, posY));
            } else if (!nvl(button.getLabel())) {
                android.widget.Button b = new android.widget.Button(this);
                b.setTypeface(Typeface.DEFAULT_BOLD);
                b.setTextSize(18);
                b.setOnTouchListener(createTouchListener(url, button.getId()));
                b.setOnClickListener(createClickListener(url, button.getId()));
                b.setText(button.getLabel());
                screen.addView(b, new AbsoluteLayout.LayoutParams(
                        (colsize - (xinset * 2)), (rowsize - (yinset * 2)),
                        posX + xinset, posY + yinset));
            }

        }
        return screen;
    }

    private ImageView constructNotFoundButton() {
        ImageButton b = new ImageButton(this);
        b.setImageResource(R.drawable.ic_notfound);
        return b;
    }

    private List<ImageView> getViewForButton(String icon) {
        Log.d(this.toString(), "get view for " + icon);
        return this.imageLoader.getView(this.url + "/" + icon);
    }

    private OnTouchListener createTouchListener(final String url,
            final String id) {
        OnTouchListener listener = new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                String param = null;
                int action = event.getAction();
                switch (action) {
                case MotionEvent.ACTION_DOWN:
                    param = HTTPUtil.PRESS;
                    break;
                case MotionEvent.ACTION_UP:
                    param = HTTPUtil.RELEASE;
                    break;
                }
                int status;
                try {
                    status = HTTPUtil.sendButton(url, id, HTTPUtil.CLICK);
                    if (status != Constants.HTTP_SUCCESS) {
                        showDialog(Constants.DIALOG_ERROR_ID);
                    }
                    Log.d(this.toString(), id + " " + param + " touch status "
                            + status);

                } catch (Exception e) {
                    Log.e(this.toString(), id + " " + param + " failed", e);
                }

                return false;
            }
        };

        return listener;
    }

    private OnClickListener createClickListener(final String url,
            final String id) {

        OnClickListener listener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(this.toString(), id + " click");
                try {
                    int status = HTTPUtil.sendButton(url, id, HTTPUtil.CLICK);
                    Log.d(this.toString(), id + " click status " + status);
                } catch (Exception e) {
                    Log.e(this.toString(), id + " click failed", e);
                }

            }
        };
        return listener;
    }

    private Bitmap getImage(Button button) {
        return this.imageLoader.getBitmap(this.url + "/" + button.getIcon());
    }

    private boolean nvl(String label) {
        return label == null || label.equals("");
    }

    private void moveRight() {
        Log.d(this.toString(), "MoveRight");
        ViewFlipper vf = (ViewFlipper) findViewById(FLIPPER);
        vf.showNext();
    }

    private void moveLeft() {

        Log.d(this.toString(), "MoveLeft");
        ViewFlipper vf = (ViewFlipper) findViewById(FLIPPER);
        vf.showPrevious();
    }

    @Override
    public boolean onTouchEvent(MotionEvent me) {
        return gestureScanner.onTouchEvent(me);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
            float velocityY) {
        Log.v(this.toString(), "fling");
        if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
                && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
            moveRight();
        } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
            moveLeft();
        }

        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
            float distanceY) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        handleMenu(item);
        return true;
    }

    public void handleMenu(MenuItem item) {
        switch (item.getItemId()) {
        case Constants.MENU_ITEM_QUIT:
            System.exit(0);
            break;
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        ActivityHandler.populateMenu(menu);
        return true;
    }

    public static void populateMenu(Menu menu) {
        menu.setQwertyMode(true);
        MenuItem quit = menu.add(0, Constants.MENU_ITEM_QUIT, 0, R.string.back);
        quit.setIcon(R.drawable.ic_menu_revert);
    }

    protected Dialog onCreateDialog(int id) {
        Dialog dialog = null;
        switch (id) {
        case Constants.DIALOG_ERROR_ID:
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Error, go back?").setCancelable(false)
                    .setPositiveButton(R.string.yes,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                        int id) {
                                    System.exit(0);
                                }
                            }).setNegativeButton(R.string.no,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                        int id) {
                                    dialog.cancel();
                                }
                            });
            dialog = builder.create();

            break;
        default:
            dialog = null;
        }
        return dialog;
    }

}
