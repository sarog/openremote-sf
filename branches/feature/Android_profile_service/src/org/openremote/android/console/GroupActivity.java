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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.openremote.android.console.bindings.Gesture;
import org.openremote.android.console.bindings.Group;
import org.openremote.android.console.bindings.Navigate;
import org.openremote.android.console.bindings.Screen;
import org.openremote.android.console.model.AppSettingsModel;
import org.openremote.android.console.model.ControllerException;
import org.openremote.android.console.model.ListenerConstant;
import org.openremote.android.console.model.OREvent;
import org.openremote.android.console.model.OREventListener;
import org.openremote.android.console.model.ORListenerManager;
import org.openremote.android.console.model.UserCache;
import org.openremote.android.console.model.ViewHelper;
import org.openremote.android.console.model.XMLEntityDataBase;
import org.openremote.android.console.net.ORConnection;
import org.openremote.android.console.net.ORConnectionDelegate;
import org.openremote.android.console.net.ORHttpMethod;
import org.openremote.android.console.view.GroupView;
import org.openremote.android.console.view.ScreenView;
import org.openremote.android.console.view.ScreenViewFlipper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.GestureDetector.OnGestureListener;
import android.widget.LinearLayout;

public class GroupActivity extends Activity implements OnGestureListener, ORConnectionDelegate{

//   private static final int FLIPPER = 0xF00D;
   private GestureDetector gestureScanner;
   private GroupView currentGroupView;
   private LinearLayout linearLayout;
   private ScreenViewFlipper currentScreenViewFlipper;
   private Screen currentScreen;
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
       
       if (groupViews == null) {
          groupViews = new HashMap<Integer, GroupView>();
       }
       if (navigationHistory == null) {
          navigationHistory = new ArrayList<Navigate>();
       }
       recoverLastGroupScreen();
       
   }

   private void recoverLastGroupScreen() {
      Log.i("Before recovery------------Group_ID, Screen_ID--------------", UserCache.getLastGroupId(GroupActivity.this) + "," + UserCache.getLastScreenId(GroupActivity.this));
	   
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
      ScreenView currentScreenView = (ScreenView) currentScreenViewFlipper.getCurrentView();
      UserCache.saveLastGroupIdAndScreenId(GroupActivity.this, currentGroupView.getGroup().getGroupId(), currentScreenView.getScreen().getScreenId());
      currentScreen = currentScreenView.getScreen();
      currentScreenView.startPolling();
      addNaviagateListener();
      Log.i("After recovery------------Group_ID, Screen_ID--------------", UserCache.getLastGroupId(GroupActivity.this) + "," + UserCache.getLastScreenId(GroupActivity.this));
   }

   private void addNaviagateListener() {
      ORListenerManager.getInstance().addOREventListener(ListenerConstant.ListenerNavigateTo, new OREventListener() {
         public void handleEvent(OREvent event) {
            Navigate navigate = (Navigate) event.getData();
            if (navigate != null) {
               handleNavigate(navigate);
            }
         }
      });
   }

   private boolean moveRight() {
       Log.d(this.toString(), "MoveRight");
       if (currentScreenViewFlipper.getDisplayedChild() < screenSize - 1) {
          ((ScreenView) currentScreenViewFlipper.getCurrentView()).cancelPolling();
          currentScreenViewFlipper.setToNextAnimation();
          currentScreenViewFlipper.showNext();
          ((ScreenView) currentScreenViewFlipper.getCurrentView()).startPolling();
          UserCache.saveLastGroupIdAndScreenId(GroupActivity.this, currentGroupView.getGroup().getGroupId(), ((ScreenView) currentGroupView.getScreenViewFlipper().getCurrentView()).getScreen().getScreenId());
          Log.i("------------Group_ID, Screen_ID--------------", UserCache.getLastGroupId(GroupActivity.this) + "," + UserCache.getLastScreenId(GroupActivity.this));
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
          UserCache.saveLastGroupIdAndScreenId(GroupActivity.this, currentGroupView.getGroup().getGroupId(), ((ScreenView) currentGroupView.getScreenViewFlipper().getCurrentView()).getScreen().getScreenId());
          Log.i("------------Group_ID, Screen_ID--------------", UserCache.getLastGroupId(GroupActivity.this) + "," + UserCache.getLastScreenId(GroupActivity.this));
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
       currentScreen = ((ScreenView) currentScreenViewFlipper.getCurrentView()).getScreen();
       if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE
               && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
          Log.e(this.toString(), "right to left");
          onScreenGestureEvent(currentScreen.getGestureByType(Gesture.GestureSwipeType.GestureSwipeTypeRightToLeft));
          moveRight();
       } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
               && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
          Log.e(this.toString(), "left to right");
          onScreenGestureEvent(currentScreen.getGestureByType(Gesture.GestureSwipeType.GestureSwipeTypeLeftToRight));
          moveLeft();
       } else if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE
             && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
          Log.e(this.toString(), "bottom to top");
          onScreenGestureEvent(currentScreen.getGestureByType(Gesture.GestureSwipeType.GestureSwipeTypeBottomToTop));
       } else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE
             && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
          Log.e(this.toString(), "top to bottom");
          onScreenGestureEvent(currentScreen.getGestureByType(Gesture.GestureSwipeType.GestureSwipeTypeTopToBottom));
       }
       return true;
   }

   private void onScreenGestureEvent(Gesture gesture) {
      if (gesture != null) {
         if (gesture.isHasControlCommand()) {
            new ORConnection(this, ORHttpMethod.POST, true, AppSettingsModel.getCurrentServer(this)
                  + "/rest/control/" + gesture.getComponentId() + "/swipe", this);
         }
         if (gesture.getNavigate() != null) {
            handleNavigate(gesture.getNavigate());
         }
      }
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
       GroupActivity.populateMenu(menu);
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
         intent.setClass(GroupActivity.this, AppSettingsActivity.class);
         startActivity(intent);
      } else if (navigate.isLogin()) {
         Intent intent = new Intent();
         intent.setClass(GroupActivity.this, LoginViewActivity.class);
         startActivity(intent);
      } else if (navigate.isLogout()) {
         String username = UserCache.getUsername(GroupActivity.this);
         if (!TextUtils.isEmpty(username)) {
            UserCache.saveUser(GroupActivity.this, "", "");
            ViewHelper.showAlertViewWithTitle(GroupActivity.this, "Logout", username + " logout success.");
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
         currentScreen = ((ScreenView) currentScreenViewFlipper.getCurrentView()).getScreen();
         return true;
      }
      return false;
   }

   /**
    * @param navigate
    */
   private void handleNavigate(Navigate navigate) {
      Navigate historyNavigate = new Navigate();
      if (currentGroupView.getGroup() != null) {
         historyNavigate.setFromGroup(currentGroupView.getGroup().getGroupId());
         historyNavigate.setFromScreen(((ScreenView) currentGroupView.getScreenViewFlipper().getCurrentView()).getScreen().getScreenId());
         if (navigateTo(navigate)) {
            UserCache.saveLastGroupIdAndScreenId(GroupActivity.this, currentGroupView.getGroup().getGroupId(), ((ScreenView) currentGroupView.getScreenViewFlipper().getCurrentView()).getScreen().getScreenId());
            Log.i("------------Group_ID, Screen_ID--------------", UserCache.getLastGroupId(GroupActivity.this) + "," + UserCache.getLastScreenId(GroupActivity.this));
            navigationHistory.add(historyNavigate);
         }
      }
   }

   @Override
   public boolean onKeyDown(int keyCode, KeyEvent event) {
      if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
         ViewHelper.showAlertViewWithTitle(GroupActivity.this, "Exit", " Exit the application.");
         System.exit(0);
         return true;
     } else if (keyCode == KeyEvent.KEYCODE_HOME && event.getRepeatCount() == 0) {
         System.exit(0);
         return true;
      }
      return super.onKeyDown(keyCode, event);
   }

   @Override
   public void urlConnectionDidFailWithException(Exception e) {
      // do nothing.
   }

   @Override
   public void urlConnectionDidReceiveData(InputStream data) {
      // do nothing.
   }

   @Override
   public void urlConnectionDidReceiveResponse(HttpResponse httpResponse) {
      int responseCode = httpResponse.getStatusLine().getStatusCode();
      if (responseCode != 200) {
         if (responseCode == 401) {
            new LoginDialog(this);
         } else {
            ViewHelper.showAlertViewWithTitle(this, "Send Request Error", ControllerException.exceptionMessageOfCode(responseCode));
         }
      }
      
   }

   
}
