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
package org.openremote.android.console;

import java.util.ArrayList;
import java.util.HashMap;

import org.openremote.android.console.bindings.Group;
import org.openremote.android.console.bindings.Navigate;
import org.openremote.android.console.model.ListenerConstant;
import org.openremote.android.console.model.OREvent;
import org.openremote.android.console.model.OREventListener;
import org.openremote.android.console.model.ORListenerManager;
import org.openremote.android.console.model.UserCache;
import org.openremote.android.console.model.ViewHelper;
import org.openremote.android.console.model.XMLEntityDataBase;
import org.openremote.android.console.net.ORControllerServerSwitcher;
import org.openremote.android.console.view.GroupView;
import org.openremote.android.console.view.ScreenView;
import org.openremote.android.console.view.ScreenViewFlipper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.GestureDetector.OnGestureListener;
import android.widget.LinearLayout;

public class GroupHandler extends Activity implements OnGestureListener {

//   private static final int FLIPPER = 0xF00D;
   private GestureDetector gestureScanner;
   private GroupView currentGroupView;
   private LinearLayout linearLayout;
   private ScreenViewFlipper currentScreenViewFlipper;
   private int screenSize;
   private HashMap<Integer, GroupView> groupViews;
   private ArrayList<Navigate> navigationHistory;

   private static final int SWIPE_MIN_DISTANCE = 120;
   private static final int SWIPE_THRESHOLD_VELOCITY = 200;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       getWindow().requestFeature(Window.FEATURE_NO_TITLE);
       getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
       
       this.gestureScanner = new GestureDetector(this);
       Log.d(this.toString(), "in oncreate for GroupHandler");
       
       if (groupViews == null) {
          groupViews = new HashMap<Integer, GroupView>();
       }
       if (navigationHistory == null) {
          navigationHistory = new ArrayList<Navigate>();
       }
       recoverLastGroupScreen();
       
//       Group group = XMLEntityDataBase.getFirstGroup();
//       screenSize = group.getScreens().size();
//       currentGroupView = new GroupView(this, group);
//       groupViews.put(group.getGroupId(), currentGroupView);
//       
//       currentScreenViewFlipper = currentGroupView.getScreenViewFlipper();
//       linearLayout = new LinearLayout(this);
//       linearLayout.addView(currentScreenViewFlipper);
//       this.setContentView(linearLayout);
//       UserCache.saveLastGroupIdAndScreenId(GroupHandler.this, currentGroupView.getGroup().getGroupId(), ((ScreenView) currentGroupView.getScreenViewFlipper().getCurrentView()).getScreen().getScreenId());
//       Log.i("------------Group_ID, Screen_ID--------------", UserCache.getLastGroupId(GroupHandler.this) + "," + UserCache.getLastScreenId(GroupHandler.this));
//       ((ScreenView) currentScreenViewFlipper.getCurrentView()).startPolling();
//       addNaviagateListener();
   }

   private void recoverLastGroupScreen() {
      Log.i("Before recovery------------Group_ID, Screen_ID--------------", UserCache.getLastGroupId(GroupHandler.this) + "," + UserCache.getLastScreenId(GroupHandler.this));
	   
      int lastGroupID = UserCache.getLastGroupId(this);
      Group lastGroup = XMLEntityDataBase.getGroup(lastGroupID);
      if (lastGroup == null) {
    	  lastGroup = XMLEntityDataBase.getFirstGroup();
      }
      screenSize = lastGroup.getScreens().size();
      currentGroupView = new GroupView(this, lastGroup);
      groupViews.put(lastGroup.getGroupId(), currentGroupView);
      currentScreenViewFlipper = currentGroupView.getScreenViewFlipper();

      int lastScreenID = UserCache.getLastScreenId(this);
      if (lastScreenID > 0) {
    	  currentScreenViewFlipper.setDisplayedChild(lastGroup.getScreens().indexOf(XMLEntityDataBase.getScreen(lastScreenID)));
      }
      
      linearLayout = new LinearLayout(this);
      linearLayout.addView(currentScreenViewFlipper);
      this.setContentView(linearLayout);
      
      UserCache.saveLastGroupIdAndScreenId(GroupHandler.this, currentGroupView.getGroup().getGroupId(), ((ScreenView) currentGroupView.getScreenViewFlipper().getCurrentView()).getScreen().getScreenId());
      ((ScreenView) currentScreenViewFlipper.getCurrentView()).startPolling();
      addNaviagateListener();
      Log.i("After recovery------------Group_ID, Screen_ID--------------", UserCache.getLastGroupId(GroupHandler.this) + "," + UserCache.getLastScreenId(GroupHandler.this));
   }

   private void addNaviagateListener() {
      ORListenerManager.getInstance().addOREventListener(ListenerConstant.ListenerNavigateTo, new OREventListener() {
         public void handleEvent(OREvent event) {
            Navigate navigate = (Navigate) event.getData();
            if (navigate != null) {
               Navigate historyNavigate = new Navigate();
               if (currentGroupView.getGroup() != null) {
                  historyNavigate.setFromGroup(currentGroupView.getGroup().getGroupId());
                  historyNavigate.setFromScreen(((ScreenView) currentGroupView.getScreenViewFlipper().getCurrentView()).getScreen().getScreenId());
               } else {
                  return;
               }
               if (navigateTo(navigate)) {
                  UserCache.saveLastGroupIdAndScreenId(GroupHandler.this, currentGroupView.getGroup().getGroupId(), ((ScreenView) currentGroupView.getScreenViewFlipper().getCurrentView()).getScreen().getScreenId());
                  Log.i("------------Group_ID, Screen_ID--------------", UserCache.getLastGroupId(GroupHandler.this) + "," + UserCache.getLastScreenId(GroupHandler.this));
                  navigationHistory.add(historyNavigate);
               }
            }
         }
      });
   }
//   private void startLoadingImages(ORActivity activity) {
//        this.imageLoader = this.imageLoader == null ? new ImageLoader() :
//        this.imageLoader.reset();
//       for (Screen s : activity.getScreens()) {
//
//           for (Button b : s.getButtons()) {
//               if (b.getIcon() != null) {
//                   this.imageLoader.load(this.url + "/" + b.getIcon(),
//                           makeTyper(s, this));
//               }
//           }
//       }
//       this.imageLoader.start();
//
//   }

//   private Typer makeTyper(final Screen s, final Context c) {
//       return new ImageLoader.Typer() {
//
//           @Override
//           public ImageView type(Bitmap bitmap) {
//               if (bitmap == null) {
//                   ImageButton ib2 = new ImageButton(c);
//                   ib2.setImageResource(R.drawable.ic_notfound);
//               }
//               int rowsize = 420 / s.getRow();
//               int colsize = 310 / s.getCol();
//               int yinset = rowsize / 6;
//               int xinset = colsize / 7;
//               int height = bitmap.getHeight();
//               int width = bitmap.getWidth();
//               if (height < (rowsize - (yinset * 2))
//                       && width < (colsize - (xinset * 2))) {
//                   ImageButton ib = new ImageButton(c);
//                   ib.setImageBitmap(bitmap);
//                   return ib;
//               } else {
//                   ImageView iv = new ImageView(c);
//                   iv.setImageBitmap(bitmap);
//                   iv.setBackgroundColor(0);
//                   return iv;
//               }
//           }
//       };
//   }

//   private AbsoluteLayout constructScreen(int col, int row,
//           List<Button> buttons, Map<String, Integer> loaded) {
//       int rowsize = 420 / row;
//       int colsize = 310 / col;
//       int yinset = rowsize / 6;
//       int xinset = colsize / 7;
//       Log.d(this.toString(), "rowsize=" + rowsize + ",colsize=" + colsize);
//
//       AbsoluteLayout screen = new AbsoluteLayout(this);
//       screen.setBackgroundColor(0);
//       for (Button button : buttons) {
//           int posX = (colsize * button.getX());
//           int posY = (rowsize * button.getY());
//           if (!nvl(button.getIcon())) {
//               Bitmap bitmap = getImage(button);
//               View view = null;
//               int height = bitmap != null ? bitmap.getHeight() : -1;
//               int width = bitmap != null ? bitmap.getWidth() : -1;
//               // Log.d(this.toString(),"height="+height+",width="+width);
//               int i = loaded.get(button.getIcon()) == null ? 0 : loaded
//                       .get(button.getIcon());
//               view = bitmap != null ? getViewForButton(button.getIcon()).get(
//                       i) : constructNotFoundButton();
//               i++;
//               loaded.put(button.getIcon(), i);
//               if (height < (rowsize - (yinset * 2))
//                       && width < (colsize - (xinset * 2))) {
//
//                   height = (rowsize - (yinset * 2));
//                   width = (colsize - (xinset * 2));
//                   posX += xinset;
//                   posY += yinset;
//               }
//               if ((posX + width) > 320) {
//                   posX += (320 - (posX + width));
//                   posX = posX >= 0 ? posX : 0;
//               }
//               if ((posY + height) > 480) {
//                   posY += (480 - (posY + height));
//                   posY = posY >= 0 ? posY : 0;
//               }
//               width = width > 320 ? 320 : width;
//               height = height > 455 ? 455 : height;
//               Log.d(this.toString(), "positioning " + button.getIcon()
//                       + " num " + i + " at " + width + "," + height + ","
//                       + posX + "," + posY);
//               if (height < 400 && width < 300) {
//                   // dirty dirty hack, make big buttons that do nothing
//                   // inoperative for clicks so that gestures work
//                   // properly
//                   view.setOnClickListener(createClickListener(url, button
//                           .getId()));
//                   view.setOnTouchListener(createTouchListener(url, button
//                           .getId()));
//
//               }
//
//               screen.addView(view, new AbsoluteLayout.LayoutParams(width,
//                       height, posX, posY));
//           } else if (!nvl(button.getLabel())) {
//               android.widget.Button b = new android.widget.Button(this);
//               b.setTypeface(Typeface.DEFAULT_BOLD);
//               b.setTextSize(18);
//               b.setOnTouchListener(createTouchListener(url, button.getId()));
//               b.setOnClickListener(createClickListener(url, button.getId()));
//               b.setText(button.getLabel());
//               screen.addView(b, new AbsoluteLayout.LayoutParams(
//                       (colsize - (xinset * 2)), (rowsize - (yinset * 2)),
//                       posX + xinset, posY + yinset));
//           }
//
//       }
//       return screen;
//   }
//
//   private ImageView constructNotFoundButton() {
//       ImageButton b = new ImageButton(this);
//       b.setImageResource(R.drawable.ic_notfound);
//       return b;
//   }
//
//   private List<ImageView> getViewForButton(String icon) {
//       Log.d(this.toString(), "get view for " + icon);
//       return this.imageLoader.getView(this.url + "/" + icon);
//   }
//
//   private OnTouchListener createTouchListener(final String url,
//           final String id) {
//       OnTouchListener listener = new View.OnTouchListener() {
//
//           @Override
//           public boolean onTouch(View v, MotionEvent event) {
//               String param = null;
//               int action = event.getAction();
//               switch (action) {
//               case MotionEvent.ACTION_DOWN:
//                   param = HTTPUtil.PRESS;
//                   break;
//               case MotionEvent.ACTION_UP:
//                   param = HTTPUtil.RELEASE;
//                   break;
//               }
//               int status;
//               try {
//                   status = HTTPUtil.sendButton(url, id, HTTPUtil.CLICK);
//                   if (status != Constants.HTTP_SUCCESS) {
//                       showDialog(Constants.DIALOG_ERROR_ID);
//                   }
//                   Log.d(this.toString(), id + " " + param + " touch status "
//                           + status);
//
//               } catch (Exception e) {
//                   Log.e(this.toString(), id + " " + param + " failed", e);
//               }
//
//               return false;
//           }
//       };
//
//       return listener;
//   }
//
//   private OnClickListener createClickListener(final String url,
//           final String id) {
//
//       OnClickListener listener = new View.OnClickListener() {
//
//           @Override
//           public void onClick(View v) {
//               Log.d(this.toString(), id + " click");
//               try {
//                   int status = HTTPUtil.sendButton(url, id, HTTPUtil.CLICK);
//                   Log.d(this.toString(), id + " click status " + status);
//               } catch (Exception e) {
//                   Log.e(this.toString(), id + " click failed", e);
//               }
//
//           }
//       };
//       return listener;
//   }
//
//   private Bitmap getImage(Button button) {
//       return this.imageLoader.getBitmap(this.url + "/" + button.getIcon());
//   }

//   private boolean nvl(String label) {
//       return label == null || label.equals("");
//   }

   private boolean moveRight() {
       Log.d(this.toString(), "MoveRight");
       if (currentScreenViewFlipper.getDisplayedChild() < screenSize - 1) {
          ((ScreenView) currentScreenViewFlipper.getCurrentView()).cancelPolling();
          currentScreenViewFlipper.setToNextAnimation();
          currentScreenViewFlipper.showNext();
          ((ScreenView) currentScreenViewFlipper.getCurrentView()).startPolling();
          UserCache.saveLastGroupIdAndScreenId(GroupHandler.this, currentGroupView.getGroup().getGroupId(), ((ScreenView) currentGroupView.getScreenViewFlipper().getCurrentView()).getScreen().getScreenId());
          Log.i("------------Group_ID, Screen_ID--------------", UserCache.getLastGroupId(GroupHandler.this) + "," + UserCache.getLastScreenId(GroupHandler.this));
          return true;
       }
       return false;
   }

   private boolean moveLeft() {
       Log.d(this.toString(), "MoveLeft");
       if (currentScreenViewFlipper.getDisplayedChild() > 0) {
          ((ScreenView) currentScreenViewFlipper.getCurrentView()).cancelPolling();
          currentScreenViewFlipper.setToPreviousAnimation();
          currentScreenViewFlipper.showPrevious();
          ((ScreenView) currentScreenViewFlipper.getCurrentView()).startPolling();
          UserCache.saveLastGroupIdAndScreenId(GroupHandler.this, currentGroupView.getGroup().getGroupId(), ((ScreenView) currentGroupView.getScreenViewFlipper().getCurrentView()).getScreen().getScreenId());
          Log.i("------------Group_ID, Screen_ID--------------", UserCache.getLastGroupId(GroupHandler.this) + "," + UserCache.getLastScreenId(GroupHandler.this));
          return true;
       }
       return false;
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

   

   @Override
   protected void onStart() {
      super.onStart();
      ((ScreenView) currentScreenViewFlipper.getCurrentView()).startPolling();
   }

   @Override
   protected void onStop() {
      super.onStop();
      ((ScreenView) currentScreenViewFlipper.getCurrentView()).cancelPolling();
   }

   @Override
   protected void onDestroy() {
      super.onDestroy();
      ((ScreenView) currentScreenViewFlipper.getCurrentView()).cancelPolling();
   }

   /**
    * @param navigate
    */
   private boolean navigateTo(Navigate navigate) {
      if (navigate.isNextScreen()) {
         return moveRight();
      } else if (navigate.isPreviousScreen()) {
         return moveLeft();
      } else if (navigate.getToGroup() > 0) {
         return navigateToGroup(navigate.getToGroup(), navigate.getToScreen());
      } else if (navigate.isBack()) {
         if (navigationHistory.size() > 0) {
            Navigate backward = navigationHistory.get(navigationHistory.size() - 1);
            if (backward.getFromGroup() > 0 && backward.getFromScreen() > 0) {
               navigateToGroup(backward.getFromGroup(), backward.getFromScreen());
            }
            navigationHistory.remove(backward);
         }
      } else if (navigate.isSetting()) {
         Intent intent = new Intent();
         intent.setClass(GroupHandler.this, AppSettingsActivity.class);
         startActivity(intent);
      } else if (navigate.isLogin()) {
         Intent intent = new Intent();
         intent.setClass(GroupHandler.this, LoginViewActivity.class);
         startActivity(intent);
      } else if (navigate.isLogout()) {
         String username = UserCache.getUsername(GroupHandler.this);
         if (!TextUtils.isEmpty(username)) {
            UserCache.saveUser(GroupHandler.this, "", "");
            ViewHelper.showAlertViewWithTitle(GroupHandler.this, "Logout", username + " logout success.");
            ((ScreenView) currentScreenViewFlipper.getCurrentView()).cancelPolling();
         }
      }
      return false;
   }

   private boolean navigateToGroup(int toGroupId, int toScreenId) {
      Group targetGroup = XMLEntityDataBase.getGroup(toGroupId);
      if (targetGroup != null) {
         ((ScreenView) currentScreenViewFlipper.getCurrentView()).cancelPolling();
         if (currentGroupView.getGroup().getGroupId() != toGroupId) {
            GroupView targetGroupView = groupViews.get(toGroupId);
            if (targetGroupView == null) {
               targetGroupView = new GroupView(this, targetGroup);
               groupViews.put(toGroupId, targetGroupView);
            }
            linearLayout.removeView(currentScreenViewFlipper);
            currentScreenViewFlipper = targetGroupView.getScreenViewFlipper();
            linearLayout.addView(currentScreenViewFlipper);
            if (toScreenId > 0) {
               currentScreenViewFlipper.setDisplayedChild(targetGroup.getScreens().indexOf(XMLEntityDataBase.getScreen(toScreenId)));
            }
            screenSize = targetGroup.getScreens().size();
            currentGroupView = targetGroupView;
         } else {
            // in same group.
            if (toScreenId > 0) {
               currentScreenViewFlipper.setDisplayedChild(targetGroup.getScreens().indexOf(XMLEntityDataBase.getScreen(toScreenId)));
            }
         }
         ((ScreenView) currentScreenViewFlipper.getCurrentView()).startPolling();
         return true;
      }
      return false;
   }

}
