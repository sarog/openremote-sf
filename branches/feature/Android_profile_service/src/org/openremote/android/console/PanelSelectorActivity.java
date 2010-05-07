/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2009, OpenRemote Inc.
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

import android.app.ListActivity;
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

public class PanelSelectorActivity extends ListActivity implements ORConnectionDelegate{

   List<String> panelsName = new ArrayList<String>();
   @Override
   public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
       new ORConnection(this ,ORHttpMethod.GET, true, getIntent().getDataString() + "/rest/panels", this);
   }

   @Override
   public void urlConnectionDidFailWithException(Exception e) {
      ViewHelper.showAlertViewWithTitle(this, "Error", "Can not get panel names.");
      Log.e("ERROR", "Can not get panel names", e);
      finish();
   }

   @Override
   public void urlConnectionDidReceiveData(InputStream data) {
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
         Log.e("ERROR", "The data is from ORConnection is bad", e);
      } catch (ParserConfigurationException e) {
         Log.e("ERROR", "Cant build new Document builder", e);
      } catch (SAXException e) {
         Log.e("ERROR", "Parse data error", e);
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
      int statusCode = httpResponse.getStatusLine().getStatusCode();
      if (statusCode != 200) {
         ViewHelper.showAlertViewWithTitle(this, "Send Request Error", ControllerException
               .exceptionMessageOfCode(statusCode));
         finish();
      }
   }

}
