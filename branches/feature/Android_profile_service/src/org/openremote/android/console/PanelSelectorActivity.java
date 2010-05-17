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
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.openremote.android.console.model.AppSettingsModel;
import org.openremote.android.console.model.ControllerException;
import org.openremote.android.console.model.ViewHelper;
import org.openremote.android.console.net.ORConnection;
import org.openremote.android.console.net.ORConnectionDelegate;
import org.openremote.android.console.net.ORHttpMethod;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


/**
 * Panel identity list selector activity. 
 * 
 * @author Tomsky Wang
 * @author Dan Cong
 *
 */
public class PanelSelectorActivity extends ListActivity implements ORConnectionDelegate{
   
   private boolean isResumed;
   List<String> panelsName = new ArrayList<String>();
   
   @Override
   protected void onPause() {
      isResumed = false;
      super.onPause();
   }

   @Override
   protected void onResume() {
      isResumed = true;
      super.onResume();
   }

   @Override
   public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
       new ORConnection(this ,ORHttpMethod.GET, true, getIntent().getDataString() + "/rest/panels", this);
   }

   @Override
   public void urlConnectionDidFailWithException(Exception e) {
      if (!isResumed) {
         return;
      }
      ViewHelper.showAlertViewWithTitle(this, "Error", "Can not get panel names.");
      Log.e("PANEL LIST", "Can not get panel names", e);
   }

   @Override
   public void urlConnectionDidReceiveData(InputStream data) {
      if (!isResumed) {
         return;
      }
      try{
         DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
         DocumentBuilder builder = factory.newDocumentBuilder();
         Document dom = builder.parse(data);
         Element root = dom.getDocumentElement();
         
         NodeList nodeList = root.getElementsByTagName("panel");
         int nodeNums = nodeList.getLength();
         for (int i = 0; i < nodeNums; i++) {
            panelsName.add(nodeList.item(i).getAttributes().getNamedItem("name").getNodeValue());
         }       
      } catch (IOException e) {
         Log.e("PANEL LIST", "The data is from ORConnection is bad", e);
         return;
      } catch (ParserConfigurationException e) {
         Log.e("PANEL LIST", "Cant build new Document builder", e);
         return;
      } catch (SAXException e) {
         Log.e("PANEL LIST", "Parse data error", e);
         return;
      }
      
      setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, panelsName));

      final ListView listView = getListView();
      listView.setItemsCanFocus(false);
      listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
      listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String selectedPanel = (String) parent.getItemAtPosition(position);
            Intent intent = getIntent();
            intent.setData(Uri.parse(selectedPanel));
            setResult(Constants.RESULT_PANEL_SELECTED, intent);
            AppSettingsModel.setCurrentPanelIdentity(PanelSelectorActivity.this, selectedPanel);
            finish();
         }
      });
      
      String panel = AppSettingsModel.getCurrentPanelIdentity(PanelSelectorActivity.this);
      if (!TextUtils.isEmpty(panel)) {
         int panelCount = panelsName.size();
         for (int i = 0; i < panelCount; i++) {
            if (panel.equals(panelsName.get(i))) {
               listView.setItemChecked(i, true);
               break;
            }
         }
      }
   }

   @Override
   public void urlConnectionDidReceiveResponse(HttpResponse httpResponse) {
      if (!isResumed) {
         return;
      }
      int statusCode = httpResponse.getStatusLine().getStatusCode();
      if (statusCode != Constants.HTTP_SUCCESS) {
         if (statusCode == ControllerException.UNAUTHORIZED) {
            Intent loginIntent = new Intent();
            loginIntent.setClass(this, LoginViewActivity.class);
            startActivity(loginIntent);
            finish();
         } else {
            // The following code customizes the dialog, becaurse the finish method should do after dialog show and click ok.
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Panel List Not Found");
            alertDialog.setMessage(ControllerException.exceptionMessageOfCode(statusCode));
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int which) {
                  finish();
                  return;
               }
            });
            alertDialog.show();
         }
      }
   }

}
