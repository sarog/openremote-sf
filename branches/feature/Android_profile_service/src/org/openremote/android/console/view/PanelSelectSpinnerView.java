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
package org.openremote.android.console.view;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.openremote.android.console.Constants;
import org.openremote.android.console.LoginDialog;
import org.openremote.android.console.R;
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
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

/**
 * The Class PanelSelectSpinnerView.
 */
public class PanelSelectSpinnerView extends Spinner implements ORConnectionDelegate {

   private ArrayAdapter<String> arrayAdapter;
   private static final String  CHOOSE_PANEL = "choose panel";
   private boolean isSending;
   
   public PanelSelectSpinnerView(Context context) {
      super(context);
      setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT));
      arrayAdapter = new ArrayAdapter<String>(context, R.layout.simple_spinner_item_center);
      arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
      setAdapter(arrayAdapter);
      String currentPanel = AppSettingsModel.getCurrentPanelIdentity(context);
      if (!TextUtils.isEmpty(currentPanel)) {
         arrayAdapter.add(currentPanel);
      } else {
         arrayAdapter.add(CHOOSE_PANEL);
      }
      
      setOntouchListener(context, this);
      setOnItemSelectListener(context);
   }
   
   private void setOntouchListener(final Context context, final PanelSelectSpinnerView ORConnectionDelegate) {
      setOnTouchListener(new OnTouchListener() {
         public boolean onTouch(View v, MotionEvent event) {
            String currentServer = AppSettingsModel.getCurrentServer(context);
            if (!isSending && !TextUtils.isEmpty(currentServer)) {
               isSending = true;
               new ORConnection(context ,ORHttpMethod.GET, true, currentServer + "/rest/panels", ORConnectionDelegate);
            }
            return false;
         }
         
      });
   }
   
   private void setOnItemSelectListener(Context context) {
      setOnItemSelectedListener(new OnItemSelectedListener() {
         public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (arrayAdapter != null) {
               String selectedItem = arrayAdapter.getItem(position);
               if (!TextUtils.isEmpty(selectedItem)
                     && !selectedItem.equals(AppSettingsModel.getCurrentPanelIdentity(getContext()))
                     && !selectedItem.equals(CHOOSE_PANEL)) {
                  AppSettingsModel.setCurrentPanelIdentity(getContext(), selectedItem);
               }
            }
         }

         public void onNothingSelected(AdapterView<?> parent) {
         }
         
      });
   }
   @Override
   public void urlConnectionDidFailWithException(Exception e) {
      isSending = false;
      ViewHelper.showAlertViewWithTitle(getContext(), "Error", "Can not get panel identity list.");
      setDefaultAdapterContent();
      Log.e("PANEL LIST", "Can not get panel identity list", e);
   }

   @Override
   public void urlConnectionDidReceiveData(InputStream data) {
      isSending = false;
      arrayAdapter.clear();
      try {
         DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
         DocumentBuilder builder = factory.newDocumentBuilder();
         Document dom = builder.parse(data);
         Element root = dom.getDocumentElement();

         NodeList nodeList = root.getElementsByTagName("panel");
         int nodeNums = nodeList.getLength();
         for (int i = 0; i < nodeNums; i++) {
            arrayAdapter.add(nodeList.item(i).getAttributes().getNamedItem("name").getNodeValue());
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
      Log.e("arrayAdapter", "arrayAdapter count:" + arrayAdapter.getCount());
      String panel = AppSettingsModel.getCurrentPanelIdentity(getContext());
      int panelCount = arrayAdapter.getCount();
      if (!TextUtils.isEmpty(panel)) {
         if (panelCount == 0) {
            arrayAdapter.add(panel);
         } else {
            for (int i = 0; i < panelCount; i++) {
               if (panel.equals(arrayAdapter.getItem(i))) {
                  this.setSelection(i, true);
                  break;
               }
            }
         }
      } else if (panelCount == 0) {
         arrayAdapter.add(CHOOSE_PANEL);
      }
   }

   @Override
   public void urlConnectionDidReceiveResponse(HttpResponse httpResponse) {
      isSending = false;
      int statusCode = httpResponse.getStatusLine().getStatusCode();
      if (statusCode != Constants.HTTP_SUCCESS) {
         if (statusCode == ControllerException.UNAUTHORIZED) {
            new LoginDialog(getContext());
         } else {
            // The following code customizes the dialog, becaurse the finish method should do after dialog show and click ok.
            AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
            alertDialog.setTitle("Panel List Not Found");
            alertDialog.setMessage(ControllerException.exceptionMessageOfCode(statusCode));
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int which) {
                  return;
               }
            });
            alertDialog.show();
         }
         setDefaultAdapterContent();
      }
   }

   private void setDefaultAdapterContent() {
      if (arrayAdapter != null) {
         arrayAdapter.clear();
         String panel = AppSettingsModel.getCurrentPanelIdentity(getContext());
         if (!TextUtils.isEmpty(panel)) {
            arrayAdapter.add(panel);
         } else {
            arrayAdapter.add(CHOOSE_PANEL);
         }
      }
   }

   
}
