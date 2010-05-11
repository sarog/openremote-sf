package org.openremote.android.console;

import org.openremote.android.console.model.UserCache;
import org.openremote.android.console.model.ViewHelper;
import org.openremote.android.console.util.AsyncResourceLoader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class LoginViewActivity extends Activity {

   private EditText usernameText;
   private EditText passwordText;
   private boolean isFromMain;
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
      setTitle(R.string.login);
      if (AsyncResourceLoader.LOAD_RESOURCE.equals(getIntent().getDataString())) {
         isFromMain = true;
      }
      this.setContentView(R.layout.login_view);
      usernameText = (EditText)findViewById(R.id.username);
      usernameText.setText(UserCache.getUsername(this));
      passwordText = (EditText)findViewById(R.id.password);
      Button loginButton = (Button)findViewById(R.id.login);
      loginButton.setOnClickListener(new OnloginClickListener());
      Button cancelButton = (Button)findViewById(R.id.login_cancel);
      cancelButton.setOnClickListener(new OnCancelClickListener());
   }
   
   private final class OnloginClickListener implements OnClickListener {
      public void onClick(View v) {
         String username = usernameText.getText().toString();
         String password = passwordText.getText().toString();
         if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            ViewHelper.showAlertViewWithTitle(LoginViewActivity.this, "Warn", "No username or password entered.");
            return;
         } else {
            UserCache.saveUser(LoginViewActivity.this, username, password);
         }
         
         if (isFromMain) {
            Intent intent = new Intent();
            intent.setClass(LoginViewActivity.this, Main.class);
            startActivity(intent);
         }
         finish();
      }
   }
   
   private final class OnCancelClickListener implements OnClickListener {
      public void onClick(View v) {
         finish();
      }
   }
   
}
