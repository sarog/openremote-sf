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

import java.util.List;

import org.openremote.android.console.model.AppSettingsModel;

import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class PanelSelectorActivity extends ListActivity {

   @Override
   public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
       List<String> panelList = HTTPUtil.getPanels(this, getIntent().getDataString());
       setListAdapter(new ArrayAdapter<String>(this,
               android.R.layout.simple_list_item_single_choice, panelList));
       
       final ListView listView = getListView();

       listView.setItemsCanFocus(false);
       listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
       listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String selectedPanel = (String)parent.getItemAtPosition(position);
            Intent intent = getIntent();
            intent.setData(Uri.parse(selectedPanel));
            setResult(Constants.RESULT_PANEL_SELECTED, intent);
            AppSettingsModel.setCurrentPanelIdentity(PanelSelectorActivity.this, selectedPanel);
            finish();
         }
       });
       String panel = AppSettingsModel.getCurrentPanelIdentity(PanelSelectorActivity.this);
       if (!TextUtils.isEmpty(panel)) {
          int panelCount = panelList.size();
          for (int i = 0; i < panelCount; i++) {
            if (panel.equals(panelList.get(i))) {
               listView.setItemChecked(i, true);
               break;
            }
         }
       }
   }

}
